package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.desy.language.snl.parser.nodes.StateNode;

public class StateParser extends AbstractDefaultStatementParser<StateNode> {

	@Override
	protected void doFindNext(final CharSequence input, final int startIndex) {
		this._found = false;
		final String prePatternString = this.getPrePatternString();
		final Pattern prePattern = Pattern.compile(prePatternString);
		final Matcher preMatcher = prePattern.matcher(input);
		int localStart = startIndex;
		while (preMatcher.find(localStart)) {
			final Pattern pattern = Pattern.compile(this.getPatternString());
			final Matcher matcher = pattern.matcher(input);
			final int end = preMatcher.end();
			matcher.region(startIndex, end);
			if (matcher.find()) {
				final String body = this.determineStatementBody(input
						.subSequence(end - 1, input.length()));
				if (body == null) {
					this._found = false;
					localStart = end;
					continue;
				}
				this._statement = matcher.group() + body.substring(1);
				this._startOffSet = matcher.start();
				this._endOffSet = end - 2 + body.length();
				final String stateName = matcher.group(2);
				this._found = true;
				this._node = new StateNode(stateName, body.substring(1, body
						.length() - 1), this.getStartOffsetLastFound(), this
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

	private String determineStatementBody(final CharSequence input) {
		assert input.charAt(0) == '{';
		final BlockStatementParser bodyParser = new BlockStatementParser();
		bodyParser.findNext(input);
		if (bodyParser.hasFoundElement()) {
			return bodyParser.getLastFoundStatement();
		}
		return null;
	}

	@Override
	protected String getPatternString() {
		return "(state\\s+)([a-zA-Z][0-9a-zA-Z_]*)"
				+ this.getPrePatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(\\s*\\{)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		throw new UnsupportedOperationException(
				"In this parser a specail behavior of doFindNext(CharSequence,int) is required; by this this method should not be called!");
	}

}
