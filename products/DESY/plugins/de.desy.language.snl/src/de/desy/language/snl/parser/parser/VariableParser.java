package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.codeElements.PredefinedTypes;
import de.desy.language.snl.parser.nodes.VariableNode;

public class VariableParser extends
		AbstractDefaultStatementParser<VariableNode> {

	// @Override
	// protected void doFindNext(CharSequence input, int startIndex) {
	// _found = false;
	//		
	// String prePatternString = "(\\s*;)";
	// Pattern prePattern = Pattern.compile(prePatternString);
	// Matcher preMatcher = prePattern.matcher(input);
	// int localStart = startIndex;
	// while (preMatcher.find(localStart)) {
	// Pattern pattern =
	// Pattern.compile("(long|double|char|string|int|short)(\\s+)([a-zA-Z][0-9a-zA-Z]*)"+prePatternString);
	// Matcher matcher = pattern.matcher(input);
	// int end = preMatcher.end();
	// matcher.region(localStart, end);
	// if (matcher.find()) {
	// _statement = matcher.group();
	// _startOffSet = matcher.start();
	// _endOffSet = end-1;
	// String type = matcher.group(1);
	// String varName = matcher.group(3);
	// _found = true;
	// _node = new VariableNode(varName, type, getStartOffsetLastFound(),
	// getEndOffsetLastFound());
	// break;
	// }
	// localStart = end;
	// if(localStart > input.length())
	// {
	// _found = false;
	// break;
	// }
	// }
	// }

	@Override
	protected String getPatternString() {
		final PredefinedTypes[] predefinedTypes = PredefinedTypes.values();
		final StringBuffer typeBuffer = new StringBuffer(predefinedTypes[0]
				.getElementName());
		for (int i = 1; i < predefinedTypes.length; i++) {
			final PredefinedTypes predefinedType = predefinedTypes[i];
			// TODO this case can be deleted, when the EventFlag is no longer a
			// type
			if (!predefinedType.equals(PredefinedTypes.EVFLAG)) {
				typeBuffer.append("|");
				typeBuffer.append(predefinedType.getElementName());
			}
		}
		return "(" + typeBuffer.toString() + ")(\\s+)([a-zA-Z][0-9a-zA-Z]*)"
				+ this.getPrePatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(\\s*;)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._statement = mainMatcher.group();
		this._startOffSet = mainMatcher.start();
		this._endOffSet = preMatcher.end() - 1;
		final String type = mainMatcher.group(1);
		final String varName = mainMatcher.group(3);
		this._found = true;
		this._node = new VariableNode(varName, type, this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
