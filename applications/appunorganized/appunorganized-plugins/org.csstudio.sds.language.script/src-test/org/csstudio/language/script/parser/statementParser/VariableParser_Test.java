package org.csstudio.language.script.parser.statementParser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.sds.language.script.codeElements.PredefinedVariables;
import org.csstudio.sds.language.script.parser.nodes.VariableNode;
import org.csstudio.sds.language.script.parser.statementParser.VariableParser;
import org.junit.Test;


public class VariableParser_Test extends TestCase {

    private final String _source =     "var returns = \"org.eclipse.swt.graphics.RGB\";\n" +
                                    "var description = \"Simple Color Rule\";\n" +
                                    "var parameters = new Array(\"An arbitrary PV\");\n" +
                                    "\n" +
                                    "var RED = new Packages.org.eclipse.swt.graphics.RGB(0,1,1);\n" +
                                    "var YELLOW = new Packages.org.eclipse.swt.graphics.RGB(60,1,1);\n" +
                                    "var GREEN = new Packages.org.eclipse.swt.graphics.RGB(120,1,1);\n" +
                                    "\n" +
                                    "function execute(args) {\n" +
                                    "    var dataElement = args[0];\n" +
                                    "    \n" +
                                    "    if (dataElement > 66) {\n" +
                                    "        return RED;\n" +
                                    "    }\n" +
                                    "    \n" +
                                    "    if (dataElement > 33) {\n" +
                                    "        return YELLOW;\n" +
                                    "    }\n" +
                                    "    \n" +
                                    "    return GREEN;\n" +
                                    "};\n";

    @Test
    public void testFindNextCharSequence() {
        final VariableParser parser = new VariableParser();

        // var returns
        parser.findNext(this._source);
        Assert.assertTrue(parser.hasFoundElement());
        Assert.assertEquals("var returns = \"org.eclipse.swt.graphics.RGB\";", parser.getLastFoundStatement());
        VariableNode lastFoundAsNode = parser.getLastFoundAsNode();
        Assert.assertEquals(VariableNode.class, lastFoundAsNode.getClass());
        Assert.assertEquals("returns", lastFoundAsNode.getSourceIdentifier());
        Assert.assertTrue(lastFoundAsNode.isPredefined());
        Assert.assertEquals(PredefinedVariables.RETURNS, lastFoundAsNode.getPredefinedVariable());
        Assert.assertTrue(lastFoundAsNode.hasOffsets());
        Assert.assertEquals(0, lastFoundAsNode.getStatementStartOffset());
        Assert.assertEquals(44, lastFoundAsNode.getStatementEndOffset());

        Assert.assertEquals(parser.getEndOffsetLastFound(), lastFoundAsNode
                .getStatementEndOffset());

        // var description
        parser.findNext(this._source, parser.getEndOffsetLastFound());
        Assert.assertTrue(parser.hasFoundElement());
        Assert.assertEquals("var description = \"Simple Color Rule\";", parser.getLastFoundStatement());
        lastFoundAsNode = parser.getLastFoundAsNode();
        Assert.assertEquals(VariableNode.class, lastFoundAsNode.getClass());
        Assert.assertEquals("description", lastFoundAsNode.getSourceIdentifier());
        Assert.assertTrue(lastFoundAsNode.isPredefined());
        Assert.assertEquals(PredefinedVariables.DESCRIPTION, lastFoundAsNode.getPredefinedVariable());
        Assert.assertTrue(lastFoundAsNode.hasOffsets());
        Assert.assertEquals(46, lastFoundAsNode.getStatementStartOffset());
        Assert.assertEquals(83, lastFoundAsNode.getStatementEndOffset());

        // var RED
        parser.findNext(this._source, parser.getEndOffsetLastFound()+45);
        Assert.assertTrue(parser.hasFoundElement());
        Assert.assertEquals("var RED = new Packages.org.eclipse.swt.graphics.RGB(0,1,1);", parser.getLastFoundStatement());
        lastFoundAsNode = parser.getLastFoundAsNode();
        Assert.assertEquals(VariableNode.class, lastFoundAsNode.getClass());
        Assert.assertEquals("RED", lastFoundAsNode.getSourceIdentifier());
        Assert.assertFalse(lastFoundAsNode.isPredefined());
        Assert.assertTrue(lastFoundAsNode.hasOffsets());
        Assert.assertEquals(133, lastFoundAsNode.getStatementStartOffset());
        Assert.assertEquals(191, lastFoundAsNode.getStatementEndOffset());
    }

}
