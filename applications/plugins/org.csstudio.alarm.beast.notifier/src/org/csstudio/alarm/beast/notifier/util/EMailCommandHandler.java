package org.csstudio.alarm.beast.notifier.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;

/**
 * Handler for EMail command:
 * Parse {@link AADataStructure} details to extract information.
 * Exemple: 
 *     mailto:rf_support@iter.org;rf_operator@iter.org?cc=rf.ro@iter.org&subject=RF Source 1 in error&body=Major Alarm raised
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class EMailCommandHandler implements IActionHandler {

	private String details;
	private List<String> to, cc, cci;
	private String subject, body;
	
	private final String emailPattern = "[_A-Za-z0-9-]+(?:\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(?:\\.[A-Za-z0-9]+)*(?:\\.[A-Za-z]{2,})";
	private final String emailListPattern = "((?:" + emailPattern + "(?:\\ *;\\ *)?)+)";
	private final String attributeSeparator = "(?:\\ *\\?\\ *)?";
	private final String textContent = "([^&]+)";
	
	private final String toPattern = emailListPattern + attributeSeparator;
	private final String ccPattern = "\\ *cc\\ *=\\ *" + emailListPattern + attributeSeparator;
	private final String cciPattern = "\\ *cci\\ *=\\ *" + emailListPattern + attributeSeparator;
	private final String subjectPattern = "\\ *subject\\ *=\\ *" + textContent + attributeSeparator;
	private final String bodyPattern = "\\ *body\\ *=\\ *" + textContent + attributeSeparator;
	
	
	public EMailCommandHandler(String details) {
		this.details = details;
	}
	
	private void parseCC(String entry) {
		Pattern p = Pattern.compile(ccPattern);
		Matcher m = p.matcher(entry);
		if (m.find()) {
			String ccList = m.group(1);
			StringTokenizer st = new StringTokenizer(ccList, ";");
			cc = new ArrayList<String>();
			while (st.hasMoreElements()) {
				cc.add(st.nextToken());
			}
		}
	}

	private void parseCCi(String entry) {
		Pattern p = Pattern.compile(cciPattern);
		Matcher m = p.matcher(entry);
		if (m.find()) {
			String cciList = m.group(1);
			StringTokenizer st = new StringTokenizer(cciList, ";");
			cci = new ArrayList<String>();
			while (st.hasMoreElements()) {
				cci.add(st.nextToken());
			}
		}
	}

	private void parseSubject(String entry) {
		Pattern p = Pattern.compile(subjectPattern);
		Matcher m = p.matcher(entry);
		if (m.find()) {
			subject = m.group(1);
		}
	}

	private void parseBody(String entry) {
		Pattern p = Pattern.compile(bodyPattern);
		Matcher m = p.matcher(entry);
		if (m.find()) {
			body = m.group(1);
		}
	}
	
	public void parse() {
		Pattern p = Pattern.compile("^mailto:\\ *" + toPattern + "(.*)$");
		Matcher m = p.matcher(details);
		String params = null;
		if (m.matches()) {
			String toList = m.group(1);
			StringTokenizer st = new StringTokenizer(toList, ";");
			to = new ArrayList<String>();
			while (st.hasMoreElements()) {
				to.add(st.nextToken().trim());
			}
			params = m.group(2).trim();
		}
		if (params == null || "".equals(params))
			return;
		parseCC(params);
		parseCCi(params);
		parseSubject(params);
		parseBody(params);
	}
	
	public List<String> getTo() {
		return to;
	}
	public List<String> getCc() {
		return cc;
	}
	public List<String> getCci() {
		return cci;
	}
	public String getSubject() {
		if (subject == null) return "";
		return subject;
	}
	public String getBody() {
		if (body == null) return "";
		return body;
	}

	@Override
	public String toString() {
		return "EMailCommandHandler [to=" + to + ", cc=" + cc + ", cci=" + cci
				+ ", subject=" + subject + ", body=" + body + "]";
	}

}
