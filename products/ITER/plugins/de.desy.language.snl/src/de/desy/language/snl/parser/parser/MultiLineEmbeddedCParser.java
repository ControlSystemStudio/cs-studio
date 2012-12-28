package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.nodes.MultiLineEmbeddedCNode;

public class MultiLineEmbeddedCParser extends
		AbstractDefaultStatementParser<MultiLineEmbeddedCNode> {

	// public static String findNextMultiLineEmbeddedC(final String input) {
	//		
	// String prePatternString = "([\n\r]+\\s*\\}%\\s*[\n\r]+)";
	// Pattern prePattern = Pattern.compile(prePatternString);
	// Matcher preMatcher = prePattern.matcher(input);
	// if (preMatcher.find()) {
	// Pattern pattern =
	// Pattern.compile("(%\\{\\s*[\n\r]+)([\\S\\s]*)"+prePatternString);
	// Matcher matcher = pattern.matcher(input);
	// matcher.region(0, preMatcher.end());
	// if (matcher.find()) {
	// StringBuffer result = new StringBuffer();
	// result.append(matcher.group(1));
	// result.append(matcher.group(2));
	// result.append(matcher.group(3));
	// return result.toString();
	// }
	// }
	// return null;
	// }

	@Override
	protected String getPatternString() {
		return "(%\\{\\s*[\n\r]+)([\\S\\s]*)" + this.getPrePatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "([\n\r]+\\s*\\}%\\s*[\n\r]+)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end();
		this._statement = mainMatcher.group();
		this._found = true;
		this._node = new MultiLineEmbeddedCNode(this._statement, this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
