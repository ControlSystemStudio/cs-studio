package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.nodes.AssignStatementNode;

public class AssignStatementParser extends
		AbstractDefaultStatementParser<AssignStatementNode> {

	@Override
	protected String getPatternString() {
		return "(assign\\s+)([a-zA-Z][0-9a-zA-Z]*)(\\s+to\\s+)(\"[\\s\\S]*\")"
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
		this._endOffSet = mainMatcher.end();
		this._statement = mainMatcher.group();
		final String channelNameWithQuotes = mainMatcher.group(4);
		this._found = true;
		this._node = new AssignStatementNode(mainMatcher.group(2),
				channelNameWithQuotes.substring(1, channelNameWithQuotes
						.length() - 1), this.getStartOffsetLastFound(), this
						.getEndOffsetLastFound());
	}

}
