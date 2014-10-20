package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;

import de.desy.language.snl.parser.nodes.MultiLineCommentNode;

public class MultiLineCommentParser extends
		AbstractDefaultStatementParser<MultiLineCommentNode> {

	@Override
	protected String getPatternString() {
		return "(/\\*)([\\S\\s]*)" + this.getPrePatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(\\*/)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		this._startOffSet = mainMatcher.start();
		this._endOffSet = mainMatcher.end();
		this._statement = mainMatcher.group();
		this._found = true;
		this._node = new MultiLineCommentNode(this._statement, this
				.getStartOffsetLastFound(), this.getEndOffsetLastFound());
	}

}
