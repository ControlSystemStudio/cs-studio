package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.DefineStatementNode;

public class DefineFunctionStatementParser extends
		AbstractOptimizedStatementParser<DefineStatementNode> {

	public DefineFunctionStatementParser(Interval[] exclusions) {
		super(exclusions);
	}
	
	@Override
	protected String getPatternString() {
		return getPrePatternString() + "(\\S*\\([[\\s&&[^\\n\\f\\r]][\\S&&[^\\)]]]*\\))(\\s+)([\\S[\\s&&[^\\n\\f\\r]]]*;)([^\\n\\f\\r]*)" + getPostPatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(#define\\s+)";
	}

	@Override
	protected String getPostPatternString() {
		return "([\\n\\f\\r])";
	}
	
	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end();
		this._statement = mainMatcher.group();
		this._found = true;
		this._node = new DefineStatementNode(mainMatcher.group(2), mainMatcher.group(4), this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
