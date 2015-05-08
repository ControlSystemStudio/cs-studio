package org.csstudio.sds.language.script.ui.rules;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.sds.language.script.codeElements.Keywords;
import org.csstudio.sds.language.script.codeElements.PredefinedFunctions;
import org.csstudio.sds.language.script.codeElements.PredefinedVariables;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import de.desy.language.editor.ui.editor.highlighting.AbstractRuleProvider;
import de.desy.language.editor.ui.editor.highlighting.predefinedRules.BraceRule;
import de.desy.language.editor.ui.editor.highlighting.predefinedRules.NumberRule;

public class RuleProvider extends AbstractRuleProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IRule> doCreateCustomRules() {
        final List<IRule> rules = new LinkedList<IRule>();

        // Rule of MultiLineComments.
        final IToken multiLineCommentToken = this
                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_MULTI_LINE_COMMENT);
        final MultiLineRule multiLineCommentRule = new MultiLineRule("/*",
                "*/", multiLineCommentToken);
        rules.add(multiLineCommentRule);

        // Rule of Braces.
        final IToken braceToken = this
                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_BRACES);
        final BraceRule braceRule = new BraceRule(braceToken);
        rules.add(braceRule);

        // Attention: Have to be placed before any word-rule.
        final IToken predefinedMethodToken = this
                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_PREDEFINED_METHOD);

        for (final PredefinedFunctions p : PredefinedFunctions.values()) {
            final ScriptMethodRule methodRule = new ScriptMethodRule(p,
                    predefinedMethodToken);
            rules.add(methodRule);
        }

        // Add rule for strings
        final IToken stringToken = this
                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_STRING);
        rules.add(new SingleLineRule("\"", "\"", stringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add(new SingleLineRule("'", "'", stringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

        // Rule of predefined variables:
        final IToken variableToken = this
                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_PREDEFINED_VARIABLES);
        final ScriptCodeElementRule variableRule = new ScriptCodeElementRule(
                PredefinedVariables.class, variableToken);
        rules.add(variableRule);

        // Rule of predefined keywords:
        final IToken keywordToken = this
                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_KEYWORD);
        final ScriptCodeElementRule keywordRule = new ScriptCodeElementRule(
                Keywords.class, keywordToken);
        rules.add(keywordRule);

        // Rule of other words (must be placed before numbers to avoid
        // highlighting of "abc1"):
        rules
                .add(new WordRule(
                        new WordDetector(),
                        this
                                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_DEFAULT)));

        // Rule of numbers:
        final IToken numberToken = this
                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_NUMBER);
        final NumberRule numberRule = new NumberRule(numberToken);
        rules.add(numberRule);

        // Rule of operators:
        final IToken operatorToken = this
                .createTokenForCodeElementType(ScriptCodeElementTextAttributeConstants.SCRIPT_OPERATOR);
        final SNLOperatorRule opRule = new SNLOperatorRule(operatorToken);
        rules.add(opRule);

        return rules;
    }

    /**
     * Creates a Token from a given element of
     * {@link ScriptCodeElementTextAttributeConstants}.
     *
     * @param element
     *            The element, not null.
     * @return A token, not null.
     */
    private IToken createTokenForCodeElementType(
            final ScriptCodeElementTextAttributeConstants element) {
        return new Token(new TextAttribute(this.getColor(element.getRGB()),
                null, element.getSwtFontStyleCode()));
    }

    /**
     * Creates a color for given rgb.
     *
     * @param rgb
     *            the rgb value.
     */
    private Color getColor(final RGB rgb) {
        Color color = JFaceResources.getColorRegistry().get(rgb.toString());
        if (color == null) {
            JFaceResources.getColorRegistry().put(rgb.toString(), rgb);
        }
        color = JFaceResources.getColorRegistry().get(rgb.toString());
        return color;
    }

}
