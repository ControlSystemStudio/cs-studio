package de.desy.language.snl.parser.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.desy.language.snl.parser.Interval;
import de.desy.language.snl.parser.nodes.AbstractSNLNode;

public abstract class AbstractOptimizedStatementParser<N extends AbstractSNLNode>
		extends AbstractStatementParser<N> {

	protected boolean _found;
	protected String _statement;
	protected int _startOffSet;
	protected int _endOffSet;
	protected N _node;
	private Matcher _matcher;
	
	private final Interval[] _exclusions;
	
	public AbstractOptimizedStatementParser() {
		_exclusions = new Interval[0];
	}
	
	public AbstractOptimizedStatementParser(Interval[] exclusions) {
		assert exclusions != null : "exclusions != null";
		
		_exclusions = exclusions;
	}

	@Override
	protected void doFindNext(final CharSequence input, final int startIndex) {
		this._found = false;
		
		final Pattern prePattern = Pattern.compile(getPrePatternString());
		final Matcher preMatcher = prePattern.matcher(input);
		final Pattern postPattern = Pattern.compile(getPostPatternString());
		final Matcher postMatcher = postPattern.matcher(input);
		final Pattern pattern = Pattern.compile(this.getPatternString());
		_matcher = pattern.matcher(input);
		
		int localStart = startIndex;
		while (preMatcher.find(localStart)) {
			localStart = preMatcher.start();
			
			while (postMatcher.find(localStart)) {
				final int end = postMatcher.end();			
				this._matcher.region(localStart, end);
				if (this._matcher.find()) {
					this.matchFound(postMatcher, this._matcher);
					return;
				}
				localStart = determineStartPosition(end);
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
	
	protected int determineStartPosition(int pos) {
		for (Interval current : _exclusions) {
			if (current.contains(pos)) {
				return current.getEnd();
			}
		}
		return pos;
	}
	
	protected abstract String getPrePatternString();

	protected abstract String getPostPatternString();

	protected abstract String getPatternString();

	/**
	 * Will be called if the pre-matcher and the matcher has found a possible
	 * matching element.
	 * 
	 * @param preMatcher
	 * @param mainMatcher
	 */
	protected abstract void matchFound(Matcher preMatcher, Matcher mainMatcher);

	protected final Matcher getMatcher() {
		return this._matcher;
	}

	@Override
	protected N doGetLastFoundAsNode() {
		return this._node;
	}

	@Override
	protected String doGetLastFoundStatement() {
		return this._statement;
	}

	@Override
	protected int doStartOffsetLastFound() {
		return this._startOffSet;
	}

	@Override
	protected int doEndOffsetLastFound() {
		return this._endOffSet;
	}

	@Override
	public boolean hasFoundElement() {
		return this._found;
	}

}
