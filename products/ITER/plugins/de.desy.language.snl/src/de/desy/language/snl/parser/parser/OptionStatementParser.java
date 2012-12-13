package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.nodes.OptionStatementNode;

public class OptionStatementParser extends
		AbstractOptimizedStatementParser<OptionStatementNode> {

	@Override
	protected String getPatternString() {
		return getPrePatternString() + "([+,\\S\\s]*)" + getPostPatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(option\\s+)";
	}

	@Override
	protected String getPostPatternString() {
		return "(\\s*;)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end();
		this._statement = mainMatcher.group();
		this._found = true;
		this._node = new OptionStatementNode(mainMatcher.group(2), this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
