package org.csstudio.sds.language.script.parser.statementParser;

import java.util.regex.Matcher;

import org.csstudio.sds.language.script.parser.nodes.CommentNode;

public class CommentParser extends AbstractDefaultStatementParser<CommentNode> {

    @Override
    protected String getPatternString() {
        return "(/\\*)([\\S\\s]*)" + this.getPrePatternString();
    }

    @Override
    protected String getPrePatternString() {
        return "(\\*/)";
    }

    @Override
    protected void matchFound(Matcher preMatcher, Matcher mainMatcher) {
        this._startOffSet = mainMatcher.start();
        this._endOffSet = mainMatcher.end();
        this._statement = mainMatcher.group();
        this._found = true;
        this._node = new CommentNode(this._statement, this
                .getStartOffsetLastFound(), this.getEndOffsetLastFound());
    }

}
