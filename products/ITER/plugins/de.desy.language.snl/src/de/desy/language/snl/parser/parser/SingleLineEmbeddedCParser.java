package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.desy.language.snl.parser.nodes.SingleLineEmbeddedCNode;

public class SingleLineEmbeddedCParser extends
		AbstractDefaultStatementParser<SingleLineEmbeddedCNode> {

	@Override
	protected String getPatternString() {
		return "(%%)([^\\n]*[\\n])";
	}

	@Override
	protected String getPrePatternString() {
		throw new UnsupportedOperationException(
				"In this parser a specail behavior of doFindNext(CharSequence,int) is required; by this this method should not be called!");
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		throw new UnsupportedOperationException(
				"In this parser a specail behavior of doFindNext(CharSequence,int) is required; by this this method should not be called!");
	}

	@Override
	protected void doFindNext(final CharSequence input, final int startIndex) {
		this._found = false;
		final Pattern pattern = Pattern.compile(this.getPatternString());
		final Matcher matcher = pattern.matcher(input);
		int localStart = startIndex;
		while (matcher.find(localStart)) {
			final int end = matcher.end();
			String body = matcher.group(2);
			body = body.substring(0, body.length() - 1);
			if (body != null) {
				this._statement = matcher.group();
				assert this._statement.charAt(this._statement.length() - 1) == '\n';
				this._startOffSet = matcher.start();
				this._endOffSet = matcher.end() - 1;
				this._found = true;
				this._node = new SingleLineEmbeddedCNode(body, this
						.getStartOffsetLastFound(), this
						.getEndOffsetLastFound());
				break;
			}
			localStart = end;
			if (localStart > input.length()) {
				this._found = false;
				break;
			}
		}
	}
}
