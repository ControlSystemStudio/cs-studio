package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.DefineStatementNode;

public class DefineConstantStatementParser extends AbstractOptimizedStatementParser<DefineStatementNode>  {

	public DefineConstantStatementParser(Interval[] exclusions) {
		super(exclusions);
	}
	
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end() - 1;
		this._statement = mainMatcher.group().substring(0, mainMatcher.group().length() - 1);
		this._found = true;
		this._node = new DefineStatementNode(mainMatcher.group(2), mainMatcher.group(4), this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

	protected String getPatternString() {
		return getPrePatternString() + "([\\S&&[^()]]*)(\\s+)(\\S*)([\\s&&[^\\n\\f\\r]]*)([^\\n\\f\\r]*)" + getPostPatternString();
	}
	
	protected String getPrePatternString() {
		return "(#define\\s+)";
	}

	protected String getPostPatternString() {
		return "([\\n\\f\\r])";
	}

}
