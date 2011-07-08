
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
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class JMSProducer implements Producer {

	private static ILogger injectedLogger;

	public static void staticInjectLogger(final ILogger logger) {
		JMSProducer.injectedLogger = logger;
	}

	private final MessageProducer[] producers;
	private boolean isClosed;
	private final ILogger _logger;
	private final Session[] _sessions;

	public JMSProducer(final String messageDestinationName,
			final PostfachArt artDesPostfaches, final Session[] sessions)
			throws JMSException {

		this._sessions = sessions;
		this._logger = JMSProducer.injectedLogger;

		this.producers = new MessageProducer[sessions.length];
		try {
			for (int i = 0; i < sessions.length; i++) {
				Destination destination = null;
				switch (artDesPostfaches) {
				case QUEUE:
					destination = sessions[i]
							.createQueue(messageDestinationName);
					break;
				case TOPIC:
				case TOPIC_DURABLE:
					destination = sessions[i]
							.createTopic(messageDestinationName);
					break;
				}
				this.producers[i] = sessions[i].createProducer(destination);
			}
		} catch (final JMSException e) {
			this.tryToClose();
			this._logger.logErrorMessage(this, e.getLocalizedMessage(), e);
			throw e;
		}
		this.isClosed = false;
	}

	@Override
    public boolean isClosed() {
		return this.isClosed;
	}

	@Override
    public void sendeSystemnachricht(final SystemNachricht systemNachricht)
			throws MessagingException {
		try {
			if (systemNachricht.istSyncronisationsAufforderung()) {
				for (int i = 0; i < this._sessions.length; i++) {
					final MapMessage mapMessage = this._sessions[i]
							.createMapMessage();
					mapMessage.setString(MessageKeyEnum.MSGPROP_COMMAND
							.getStringValue(),
							MessageKeyUtil.MSGVALUE_TCMD_RELOAD_CFG_START);
					mapMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
					this.producers[i].send(mapMessage);
				}
			} else if (systemNachricht.istSyncronisationsBestaetigung()) {
				for (int i = 0; i < this._sessions.length; i++) {
					final MapMessage mapMessage = this._sessions[i]
							.createMapMessage();
					mapMessage.setString(MessageKeyEnum.MSGPROP_COMMAND
							.getStringValue(),
							MessageKeyUtil.MSGVALUE_TCMD_RELOAD_CFG_END);
					mapMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
					this.producers[i].send(mapMessage);
				}
			} else {
				this._logger
						.logErrorMessage(this, "unbekannte Systemnachricht.");
			}
		} catch (final Throwable e) {
			this._logger.logWarningMessage(this,
					"Throwable recieved during send of system message", e);
			throw new MessagingException(
					"Throwable recieved during send of system message", e);
		}
	}

	@Override
    public void sendeVorgangsmappe(final Vorgangsmappe vorgangsmappe)
			throws MessagingException {
		final Regelwerkskennung regelwerkskennung = vorgangsmappe
				.gibPruefliste().gibRegelwerkskennung();
		final AlarmNachricht alarmNachricht = vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges();
		final Map<MessageKeyEnum, String> contentMap = alarmNachricht
				.getContentMap();
		final Map<String, String> unknownContentMap = alarmNachricht
				.getUnknownContentMap();

		try {
			for (int i = 0; i < this._sessions.length; i++) {
				MapMessage mapMessage;
				mapMessage = this._sessions[i].createMapMessage();

				final Set<Entry<MessageKeyEnum, String>> entrySet = contentMap
						.entrySet();
				for (final Entry<MessageKeyEnum, String> entry : entrySet) {
					mapMessage.setString(entry.getKey().getStringValue(), entry
							.getValue());
				}

				final Set<Entry<String, String>> unknownEntrySet = unknownContentMap.entrySet();
				for (final Entry<String, String> entry : unknownEntrySet) {
					mapMessage.setString(entry.getKey(), entry
							.getValue());
				}

				mapMessage.setString(MessageKeyEnum.AMS_FILTERID
						.getStringValue(), Integer.toString(regelwerkskennung
						.getRegelwerksId()));

				mapMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
				this.producers[i].send(mapMessage);
			}
		} catch (final JMSException e) {
			this._logger.logWarningMessage(this,
					"JMSException during send of Vorgangsmappe", e);
			throw new MessagingException(
					"JMSException during send of Vorgangsmappe", e);
		}
	}

	@Override
    public void tryToClose() {
		for (final MessageProducer producer : this.producers) {
			if (producer != null) {
				try {
					producer.close();
				} catch (final JMSException e) {
				    // Can be ignored
				}
			}
		}
		this.isClosed = true;
		this._logger.logDebugMessage(this, "Producer closed");
	}
}
