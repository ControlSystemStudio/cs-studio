package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.nodes.ProgramNode;

public class ProgramParser extends AbstractDefaultStatementParser<ProgramNode> {

	@Override
	protected String getPatternString() {
		return "(program\\s+)([a-zA-Z_][0-9a-zA-Z_]*)([^\\n\\S]*)([\\(][\\S\\s]*[\\)])?"
				+ this.getPrePatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "([^\\n\\S]*[;\\n])";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._found = true;
		final StringBuffer result = new StringBuffer();
		result.append(mainMatcher.group());
		this._statement = result.toString();
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end() - 1;

		this._node = new ProgramNode(mainMatcher.group(2), this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
