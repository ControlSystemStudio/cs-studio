package org.csstudio.alarm.beast.notifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.SeverityLevel;
import org.eclipse.osgi.util.NLS;

/**
 * Build a summary of current alarm state for the specified PV.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class PVSummary {

	private String name, description;
	private String current_severity, severity;
	private String current_message, message;
	private String value;
	private String timestamp;

	boolean isNLSMessage = false;
	private static Pattern NLSPattern = Pattern.compile("\\{\\ *\\d+\\ *\\}");

	/** Pattern for description prefixes */
    final protected static Pattern PrefixPattern = Pattern.compile("^[\\*\\!]*(.*)");

	public static PVSummary buildFromSnapshot(PVSnapshot pv)
	{
		String description = pv.getDescription().trim();
		boolean isNLSMessage = false;
		Matcher NLSMatcher = NLSPattern.matcher(description);
		if (NLSMatcher.find()) {
			isNLSMessage = true;
		}
		// Clean description
		Matcher prefixMatcher = PrefixPattern.matcher(description);
		if (prefixMatcher.matches()) {
			description = prefixMatcher.group(1).trim();
		}
		String name = pv.getName();

		String current_severity = pv.getCurrentSeverity().name();
		String severity = pv.getSeverity().name();
		String current_message = pv.getCurrentMessage();
		String message = pv.getMessage();
		String value = pv.getValue();

		String timestamp = pv.getTimestamp() == null ? "(no time)" : pv.getTimestamp().toString();

		return new PVSummary(description, name, current_severity, severity,
				current_message, message, value, timestamp, isNLSMessage);
	}

	public PVSummary(String description,
			String name,
			String current_severity,
			String severity,
			String current_message,
			String message,
			String value,
			String timestamp,
			boolean isNLSMessage)
	{
		this.description = description;
		this.name = name;
		this.current_severity = current_severity;
		this.severity = severity;
		this.current_message = current_message;
		this.message = message;
		this.value = value;
		this.timestamp = timestamp;
		this.isNLSMessage = isNLSMessage;
	}

	public String getSummary()
	{
		if (isNLSMessage) {
			String[] bindings = { current_severity, value };
			return NLS.bind(description, bindings);
		}
		StringBuilder builder = new StringBuilder();
		builder.append(current_severity);
		if (current_severity.equals(SeverityLevel.MINOR_ACK)
				|| current_severity.equals(SeverityLevel.MAJOR_ACK))
			builder.append(" alarm ACK - ");
		else builder.append(" alarm - ");
		builder.append(name);
		if (description != null && !"".equals(description)) {
			builder.append(": ");
			builder.append(description);
		}
		return builder.toString();
	}

	public String getDetails()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Timestamp: ");
		builder.append(timestamp);
		builder.append(" - ");
		builder.append("PV value: ");
		builder.append(value);
		builder.append(" - ");
		builder.append("PV status: ");
		builder.append(current_message);
		builder.append("\n");
		builder.append("SERVER severity: ");
		builder.append(severity);
		builder.append(" - ");
		builder.append("SERVER status: ");
		builder.append(message);
		return builder.toString();
	}

}
