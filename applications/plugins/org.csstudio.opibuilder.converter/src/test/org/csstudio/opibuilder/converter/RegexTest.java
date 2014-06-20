package org.csstudio.opibuilder.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

	public static void main(String[] args) {
		Pattern p = Pattern.compile("static .*");
		Matcher m = p.matcher("static 0 static Disconn/Invalid"
				+ "  { 65535 65535 65535 }    #0 white\nstatic 107 \"Top Shadow\""
				+ "       { 60652 60652 60652 }    #");
		while (m.find()) {
			// System.out.println(m.group());
		}
		// rule pattern
		p = Pattern.compile("rule [^\\x7B]*\\x7B{1}[^\\x7D]*\\x7D{1}", Pattern.DOTALL);

		String ruleExpression = "rule 100 exampleRule { " + "\n=100 || =200 : strange"
				+ "\n >=20         : invisible" + "\n >0 && <10    : red"
				+ "\n >=10 && <20  : \"blinking red\"" + "\n default      : green" + "\n}";
		m = p.matcher(ruleExpression + "\n" + "\n" + "\n" + "\nrule 147 \"fil-sts\" {"
				+ "\n >=-0.5 && <0.5        : \"black\"            # fil off        = 0"
				+ "\n >=0.5  && <1.5        : \"brt-orange\"       # fil. on        = 1"
				+ "\n >=1.5  && <2.5        : \"blinking orange\"  # on too long    = 2"
				+ "\n >=2.5  && <3.5        : \"orange-42\"	    # black heat     = 3" + "\n}sdfs");
		while (m.find()) {
			System.out.println("-----------");
			System.out.println(m.group());
			System.out.println("-----------");
		}

		p = Pattern.compile(">={1}|<={1}|>{1}|<{1}|={1}");
		m = p.matcher(ruleExpression);
		StringBuilder sb = new StringBuilder(ruleExpression);
		int i = 0;
		while (m.find()) {
			System.out.println(m.group() + ":" + m.start());
			sb.insert(m.start() + 3 * i, "pv0");
			i++;
		}
		System.out.println(sb);
		
		
		System.out.println("LOC\\$(!M)abc".replaceAll("\\x24\\x28\\x21[A-Z]{1}\\x29", "\\$(DID)"));
	}

}
