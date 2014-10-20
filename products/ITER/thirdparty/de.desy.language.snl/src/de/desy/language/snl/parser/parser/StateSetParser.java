package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.desy.language.snl.parser.nodes.StateSetNode;

public class StateSetParser extends
		AbstractDefaultStatementParser<StateSetNode> {

	@Override
	protected String getPatternString() {
		return "(ss\\s+)([a-zA-Z][0-9a-zA-Z_]*)(\\s*\\{)";
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
			final String body = this.determineStatementBody(input, end - 1);
			if (body != null) {
				this._statement = matcher.group() + body.substring(1);
				this._startOffSet = matcher.start();
				this._endOffSet = end - 1 + body.length() - 1;
				final String stateSetName = matcher.group(2);
				this._found = true;
				this._node = new StateSetNode(stateSetName, body.substring(1,
						body.length() - 1), this.getStartOffsetLastFound(),
						this.getEndOffsetLastFound());
				break;
			}
			localStart = end;
			if (localStart > input.length()) {
				this._found = false;
				break;
			}
		}
	}

	private String determineStatementBody(final CharSequence input,
			final int startOffsetOfBlock) {
		assert input.charAt(startOffsetOfBlock) == '{';
		final BlockStatementParser bodyParser = new BlockStatementParser();
		bodyParser.findNext(input, startOffsetOfBlock);
		if (bodyParser.hasFoundElement()) {
			return bodyParser.getLastFoundStatement();
		}
		return null;
	}

}
