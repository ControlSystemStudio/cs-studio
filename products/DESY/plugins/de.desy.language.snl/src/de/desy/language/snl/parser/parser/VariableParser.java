package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.codeElements.PredefinedTypes;
import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.VariableNode;

public class VariableParser extends
		AbstractOptimizedStatementParser<VariableNode> {

	
	public VariableParser(Interval[] exclusions) {
		super(exclusions);
	}
	
	@Override
	protected String getPostPatternString() {
		return "(\\s*;)";
	}
	
	@Override
	protected String getPatternString() {
		return getPrePatternString() + "([a-zA-Z_][0-9a-zA-Z_]*)(\\s*\\[\\s*\\d+\\s*\\])*"
				+ getPostPatternString();
	}

	@Override
	protected String getPrePatternString() {
		final PredefinedTypes[] predefinedTypes = PredefinedTypes.values();
		final StringBuffer typeBuffer = new StringBuffer(predefinedTypes[0]
				.getElementName());
		for (int i = 1; i < predefinedTypes.length; i++) {
			final PredefinedTypes predefinedType = predefinedTypes[i];
			if (!predefinedType.equals(PredefinedTypes.EVFLAG)) {
				typeBuffer.append("|");
				typeBuffer.append(predefinedType.getElementName());
			}
		}
		return "(" + typeBuffer.toString() + ")(\\s+)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._statement = mainMatcher.group();
		this._startOffSet = mainMatcher.start();
		this._endOffSet = preMatcher.end() - 1;
		final String type = mainMatcher.group(1);
		final String varName = mainMatcher.group(3);
		final boolean isArray = mainMatcher.group(4) != null;
		this._found = true;
		this._node = new VariableNode(varName, type, this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound(), isArray);
	}

}
