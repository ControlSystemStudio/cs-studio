package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.AssignStatementNode;

public class AssignStatementParser extends
		AbstractOptimizedStatementParser<AssignStatementNode> {
	
	public AssignStatementParser(Interval[] exclusions) {
		super(exclusions);
	}

	@Override
	protected String getPatternString() {
		return getPrePatternString() + "([a-zA-Z_][0-9a-zA-Z_]*)(\\s*\\[\\s*\\d+\\s*\\])*(\\s+to\\s+)(\"[\\s\\S]*\")"
				+ getPostPatternString(); 
	}

	@Override
	protected String getPrePatternString() {
		return "(assign\\s+)";
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
		final String channelNameWithQuotes = mainMatcher.group(5);
		final boolean isArray = mainMatcher.group(3) != null; 
		this._found = true;
		this._node = new AssignStatementNode(mainMatcher.group(2),
				channelNameWithQuotes.substring(1, channelNameWithQuotes
						.length() - 1), this.getStartOffsetLastFound(), this
						.getEndOffsetLastFound(), isArray);
	}

}
