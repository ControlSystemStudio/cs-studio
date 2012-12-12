package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.nodes.CharNode;

public class CharParser extends AbstractDefaultStatementParser<CharNode> {

	@Override
	protected String getPatternString() {
		return "(\')([^\\n]*)" + this.getPrePatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(\')";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end();
		this._statement = mainMatcher.group();
		this._found = true;
		this._node = new CharNode(this._statement, this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
