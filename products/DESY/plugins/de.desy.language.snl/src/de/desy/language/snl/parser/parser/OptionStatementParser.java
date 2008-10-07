package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.nodes.OptionStatementNode;

public class OptionStatementParser extends
		AbstractDefaultStatementParser<OptionStatementNode> {

	@Override
	protected String getPatternString() {
		return "(option\\s+)([+,\\S\\s]*)" + this.getPrePatternString();
	}

	@Override
	protected String getPrePatternString() {
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
