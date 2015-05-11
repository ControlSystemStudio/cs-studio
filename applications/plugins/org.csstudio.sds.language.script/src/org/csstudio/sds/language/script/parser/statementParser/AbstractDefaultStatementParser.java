package org.csstudio.sds.language.script.parser.statementParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.sds.language.script.parser.nodes.AbstractScriptNode;

public abstract class AbstractDefaultStatementParser<N extends AbstractScriptNode>
        extends AbstractStatementParser<N> {

    protected boolean _found;
    protected String _statement;
    protected int _startOffSet;
    protected int _endOffSet;
    protected N _node;
    private Matcher _matcher;

    @Override
    protected void doFindNext(final CharSequence input, final int startIndex) {
        this._found = false;
        final String prePatternString = this.getPrePatternString();
        final Pattern prePattern = Pattern.compile(prePatternString);
        final Matcher preMatcher = prePattern.matcher(input);
        int localStart = startIndex;
        while (preMatcher.find(localStart)) {
            final Pattern pattern = Pattern.compile(this.getPatternString());
            this._matcher = pattern.matcher(input);
            final int end = preMatcher.end();
            this._matcher.region(startIndex, end);
            if (this._matcher.find()) {
                this.matchFound(preMatcher, this._matcher);
                break;
            }
            localStart = end;
            if (localStart > input.length()) {
                this._found = false;
                break;
            }
        }
    }

    protected abstract String getPrePatternString();

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
