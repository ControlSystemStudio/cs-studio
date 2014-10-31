package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.desy.language.snl.parser.nodes.WhenNode;

public class WhenParser extends AbstractDefaultStatementParser<WhenNode> {

	@Override
	protected String getPatternString() {
		return "(\\s*state\\s*)([a-zA-Z][0-9a-zA-Z_]*)([;]?)";
	}

	@Override
	protected String getPrePatternString() {
		return "(when\\s*\\()";
	}

	@Override
	protected void doFindNext(final CharSequence input, final int startIndex) {
		this._found = false;
		final Pattern prePattern = Pattern.compile(getPrePatternString());
		final Matcher preMatcher = prePattern.matcher(input);
		final Pattern pattern = Pattern.compile(getPatternString());
		final Matcher matcher = pattern.matcher(input);
		
		int localStart = startIndex;
		while (preMatcher.find(localStart)) {
			final String conditionWithBraces = this.determineCondition(input,
					preMatcher.end() - 1);
			if (conditionWithBraces == null) {
				this._found = false;
				break;
			}
			final int end = preMatcher.end();
			final int startOffsetOfBlock = end - 1
					+ conditionWithBraces.length();
			final String bodyContentWithBraces = this.determineContent(input,
					startOffsetOfBlock);
			if (bodyContentWithBraces == null) {
				this._found = false;
				break;
			}
			matcher.region(end - 1 + conditionWithBraces.length()
					+ bodyContentWithBraces.length(), input.length());
			if (matcher.find()) {
				this._startOffSet = preMatcher.start();
				this._endOffSet = matcher.end();
				this._found = true;
				this._statement = input.subSequence(
						this.getStartOffsetLastFound(),
						this.getEndOffsetLastFound()).toString();
				this._node = new WhenNode(conditionWithBraces.subSequence(1,
						conditionWithBraces.length() - 1).toString().trim(),
						bodyContentWithBraces.substring(1,
								bodyContentWithBraces.length() - 1), matcher
								.group(2), this.getStartOffsetLastFound(), this
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

	private String determineContent(final CharSequence input,
			final int startOffsetOfBlock) {
		final BlockStatementParser parser = new BlockStatementParser();
		parser.findNext(input, startOffsetOfBlock);

		if (!parser.hasFoundElement()) {
			return null;
		}

		final String lastFoundStatement = parser.getLastFoundStatement();

		return lastFoundStatement;
	}

	private String determineCondition(final CharSequence input,
			final int startOffsetOfBlock) {
		assert input.charAt(startOffsetOfBlock) == '(';
		final ConditionStatementParser bodyParser = new ConditionStatementParser();
		bodyParser.findNext(input, startOffsetOfBlock);
		if (bodyParser.hasFoundElement()) {
			return bodyParser.getLastFoundStatement();
		}
		return null;
	}

	@Override
	protected void matchFound(final Matcher preMatcher,
			final Matcher mainMatcher) {
		throw new UnsupportedOperationException(
				"In this parser a specail behavior of doFindNext(CharSequence,int) is required; by this this method should not be called!");
	}

}
