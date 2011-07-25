package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.desy.language.snl.parser.nodes.StateNode;

public class StateParser extends AbstractOptimizedStatementParser<StateNode> {

	@Override
	protected void doFindNext(final CharSequence input, final int startIndex) {
		this._found = false;
		final Pattern prePattern = Pattern.compile(this.getPrePatternString());
		final Matcher preMatcher = prePattern.matcher(input);
		final Pattern postPattern = Pattern.compile(this.getPostPatternString());
		final Matcher postMatcher = postPattern.matcher(input);
		final Pattern pattern = Pattern.compile(this.getPatternString());
		final Matcher matcher = pattern.matcher(input);
		
		int localStart = startIndex;
		while (preMatcher.find(localStart)) {
			localStart = preMatcher.end();
			
			while (postMatcher.find(localStart + 1)) {
				final int end = postMatcher.end();
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
					return;
				}
				localStart = end;
				if (localStart > input.length()) {
					this._found = false;
					return;
				}
			}
			
			if (localStart > input.length()) {
				this._found = false;
				return;
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
		return getPrePatternString() + "([a-zA-Z][0-9a-zA-Z_]*)"
				+ getPostPatternString();
	}

	@Override
	protected String getPrePatternString() {
		return "(state\\s+)";
	}

	@Override
	protected String getPostPatternString() {
		return "(\\s*\\{)";
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		throw new UnsupportedOperationException(
				"In this parser a specail behavior of doFindNext(CharSequence,int) is required; by this this method should not be called!");
	}

}
