package org.csstudio.alarm.beast.notifier.actions;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.EActionStatus;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVAlarmHandler;
import org.csstudio.alarm.beast.notifier.PVSummary;
import org.csstudio.alarm.beast.notifier.model.AbstractNotificationAction;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.util.EMailCommandHandler;
import org.csstudio.alarm.beast.notifier.util.EMailCommandValidator;

/**
 * Action for sending EMails.
 * If the parsed command is complete, the email does not need to be filled.
 * Otherwise, subject and body are built from {@link AlarmTreeItem} description.
 * @author Fred Arnaud (Sopra Group)
 *
 */
@SuppressWarnings("nls")
public class EmailNotificationAction extends AbstractNotificationAction {

    public static final String MAIL_SCHEME = "mailto";

	private String host, from;
	private List<String> to, cc, cci;
	private String subject = "";
	private String body = "";

	/** <code>true</code> if all field of the action can be filled with command */
	private boolean isCommandComplete = false;


	/** {@inheritDoc} */
	@Override
	public void init(AlarmNotifier notifier, ActionID id, ItemInfo item, int delay,
			String details, IActionValidator validator) {
		super.init(notifier, id, item, delay, details);
		this.host = org.csstudio.alarm.beast.notifier.Preferences.getSMTP_Host();
		this.from =  org.csstudio.alarm.beast.notifier.Preferences.getSMTP_Sender();

		try {
			EMailCommandValidator ecv = (EMailCommandValidator) validator;
			validator.validate();
			EMailCommandHandler ech = ecv.getHandler();
			this.to = ech.getTo();
			this.cc = ech.getCc();
			this.cci = ech.getCci();
			this.subject = ech.getSubject();
			this.body = ech.getBody();
			this.isCommandComplete = ecv.isComplete();
		} catch (Exception e) {
			updateStatus(EActionStatus.STOPPED);
			Activator.getLogger().log(Level.WARNING,
					"Unrecognized EMail command pattern for \"{0}\": {1}",
					new Object[] { details, e.getMessage() });
		}
	}

	protected void fill() {
		if (!isCommandComplete) {
			if (subject == null || "".equals(subject.trim()))
				subject = buildSubject();
			if (body == null || "".equals(body.trim()))
				body = buildBody();
		}
	}

	private String buildSubject() {
		StringBuilder builder = new StringBuilder();
		if(isPV) {
			PVSummary summary = PVSummary.buildFromSnapshot(pvs.get(
					item.getName()).getCurrent());
			builder.append(summary.getSummary());
			return builder.toString();
		}
		// count alarms by severity
		int minorCount = 0;
		int majorCount = 0;
		int invalidCount = 0;
		for (PVAlarmHandler pv : pvs.values()) {
			switch (pv.getCurrent().getCurrentSeverity()) {
			case MINOR: minorCount++; break;
			case MAJOR: majorCount++; break;
			case INVALID: invalidCount++; break;
			}
		}
		// nb MINOR alarms – nb MINOR alarms - <area/system name>: <area/system description>
		if (invalidCount > 0) {
			builder.append(invalidCount + " INVALID alarms");
			builder.append(" - ");
		}
		if (majorCount > 0) {
			builder.append(majorCount + " MAJOR alarms");
			builder.append(" - ");
		}
		if (minorCount > 0) {
			builder.append(minorCount + " MINOR alarms");
			builder.append(" - ");
		}
		builder.append(item.getName());
		return builder.toString();
	}

	private String buildBody() {
		StringBuilder builder = new StringBuilder();
		if(isPV) {
			PVSummary summary = PVSummary.buildFromSnapshot(pvs.get(
					item.getName()).getCurrent());
			builder.append(summary.getDetails());
			return builder.toString();
		}
		for (PVAlarmHandler pv : pvs.values()) {
			PVSummary summary = PVSummary.buildFromSnapshot(pv.getCurrent());
			builder.append(summary.getSummary());
			builder.append("\n");
		}
		return builder.toString();
	}

	/** {@inheritDoc} */
	@Override
	public void execute() {
		fill();
		if (checkContent())
			send();
	}

	private void send() {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "25");
		Session session = Session.getDefaultInstance(props);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			Address[] toAddresses = new Address[to.size()];
			int count = 0;
			for (String address : to) {
				toAddresses[count] = new InternetAddress(address);
				count++;
			}
			message.setRecipients(Message.RecipientType.TO, toAddresses);
			if (cc != null) {
				count = 0;
				Address[] ccAddresses = new Address[cc.size()];
				for (String address : cc) {
					ccAddresses[count] = new InternetAddress(address);
					count++;
				}
				message.setRecipients(Message.RecipientType.CC, ccAddresses);
			}
			if (cci != null) {
				count = 0;
				Address[] bccAddresses = new Address[cci.size()];
				for (String address : cci) {
					bccAddresses[count] = new InternetAddress(address);
					count++;
				}
				message.setRecipients(Message.RecipientType.BCC, bccAddresses);
			}
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
            Activator.getLogger().log(Level.FINE, "Sent EMail to {0}", to);
		} catch (MessagingException e) {
			Activator.getLogger().log(Level.SEVERE,
					"Exception durring EMail sending: {0}", e.getMessage());
		}
	}

	private boolean checkContent() {
		return ((to != null && !to.isEmpty())
				&& (subject != null && !"".equals(subject.trim()))
				&& (body != null));
	}

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public List<String> getCc() {
		return cc;
	}
	public void setCc(List<String> cc) {
		this.cc = cc;
	}
	public List<String> getCci() {
		return cci;
	}
	public void setCci(List<String> cci) {
		this.cci = cci;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		if(subject == null) return;
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		if(body == null) return;
		this.body = body;
	}

	@Override
	public void dump() {
		System.out.println("EMailNotificationAction [\n\tto= " + to
				+ "\n\tcc= " + cc
				+ "\n\tcci= " + cci
				+ "\n\tsubject= " + subject
				+ "\n\tbody= " + body
				+ "\n]");
	}

}
