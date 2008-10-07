package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.nodes.MonitorStatementNode;

public class MonitorStatementParser extends
		AbstractDefaultStatementParser<MonitorStatementNode> {

	@Override
	protected String getPatternString() {
		return "(monitor\\s+)([a-zA-Z][0-9a-zA-Z_]*)"
				+ this.getPrePatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(\\s*;)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end() - 1;
		this._statement = mainMatcher.group();
		this._found = true;
		this._node = new MonitorStatementNode(mainMatcher.group(2), this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
