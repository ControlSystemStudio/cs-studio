package org.csstudio.nams.service.messaging.impl.jms;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;

public class JMSProducer implements Producer {

	private static Logger injectedLogger;

	public static void staticInjectLogger(Logger logger) {
		injectedLogger = logger;
	}

	private MessageProducer[] producers;
	private boolean isClosed;
	private Logger logger;
	private final Session[] sessions;

	public JMSProducer(String messageDestinationName,
			PostfachArt artDesPostfaches, Session[] sessions)
			throws JMSException {

		this.sessions = sessions;
		logger = injectedLogger;

		producers = new MessageProducer[sessions.length];
		try {
			for (int i = 0; i < sessions.length; i++) {
				Destination destination = null;
				switch (artDesPostfaches) {
				case QUEUE:
					destination = sessions[i]
							.createQueue(messageDestinationName);
					break;
				case TOPIC:
					destination = sessions[i]
							.createTopic(messageDestinationName);
					break;
				}
				producers[i] = sessions[i].createProducer(destination);
			}
		} catch (JMSException e) {
			close();
			logger.logErrorMessage(this, e.getLocalizedMessage(), e);
			throw e;
		}
		isClosed = false;
	}

	public void close() {
		for (MessageProducer producer : producers) {
			if (producer != null) {
				try {
					producer.close();
				} catch (JMSException e) {
				}
			}
		}
		isClosed = true;
		logger.logDebugMessage(this, "Producer closed");
	}

	public boolean isClosed() {
		return isClosed;
	}

//	final static String MSGPROP_COMMAND = "COMMAND";
//	final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";
//	final static String MSGVALUE_TCMD_RELOAD_CFG_START = MSGVALUE_TCMD_RELOAD
//			+ "_START";
//	final static String MSGVALUE_TCMD_RELOAD_CFG_END = MSGVALUE_TCMD_RELOAD
//			+ "_END";

	public void sendeSystemnachricht(SystemNachricht systemNachricht) {
		// TODO implementieren
		try {
			if (systemNachricht.istSyncronisationsAufforderung()) {
				for (int i = 0; i < sessions.length; i++) {
					MapMessage mapMessage = sessions[i].createMapMessage();
					mapMessage.setString(MessageKeyEnum.MSGPROP_COMMAND.getStringValue(),
							MessageKeyUtil.MSGVALUE_TCMD_RELOAD_CFG_START);
					mapMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
					producers[i].send(mapMessage);
				}
			} else if (systemNachricht.istSyncronisationsBestaetigung()) {
				for (int i = 0; i < sessions.length; i++) {
					MapMessage mapMessage = sessions[i].createMapMessage();
					mapMessage.setString(MessageKeyEnum.MSGPROP_COMMAND.getStringValue(),
							MessageKeyUtil.MSGVALUE_TCMD_RELOAD_CFG_END);
					mapMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
					producers[i].send(mapMessage);
				}
			} else {
				logger.logErrorMessage(this, "unbekannte Systemnachricht.");
			}
		} catch (JMSException e) {
			// FIXME exception handling
			e.printStackTrace();
		}
	}

	public void sendeVorgangsmappe(Vorgangsmappe vorgangsmappe) {
		Regelwerkskennung regelwerkskennung = vorgangsmappe.gibPruefliste()
				.gibRegelwerkskennung();
		AlarmNachricht alarmNachricht = vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges();
		Map<MessageKeyEnum, String> contentMap = alarmNachricht.getContentMap();
		
		try {
			for (int i = 0; i < sessions.length; i++) {
				MapMessage mapMessage;
				mapMessage = sessions[i].createMapMessage();
				
				Set<Entry<MessageKeyEnum,String>> entrySet = contentMap.entrySet();
				for (Entry<MessageKeyEnum, String> entry : entrySet) {
					mapMessage.setString(entry.getKey().getStringValue(), entry.getValue());
				}
				
				mapMessage.setString(MessageKeyEnum.AMS_FILTERID.getStringValue(), Integer.toString(regelwerkskennung.getRegelwerksId()));
				
				mapMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
				producers[i].send(mapMessage);
			}
		} catch (JMSException e) {
			// FIXME exception handling
			e.printStackTrace();
		}
	}
}
