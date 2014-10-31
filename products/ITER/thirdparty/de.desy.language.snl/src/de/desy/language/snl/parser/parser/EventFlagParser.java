package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.EventFlagNode;

public class EventFlagParser extends
		AbstractOptimizedStatementParser<EventFlagNode> {
	
	public EventFlagParser(Interval[] exclusions) {
		super(exclusions);
	}

	@Override
	protected String getPatternString() {
		return getPrePatternString() + "([a-zA-Z][0-9a-zA-Z_]*)"
				+ getPostPatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(evflag\\s+)";
	}

	@Override
	protected String getPostPatternString() {
		return "(\\s*;)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end() - 1;
		this._statement = mainMatcher.group();
		this._found = true;
		this._node = new EventFlagNode(mainMatcher.group(2), this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
