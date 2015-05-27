package de.desy.language.editor.ui.editor.highlighting.predefinedRules;

import org.eclipse.jface.text.rules.IToken;

/**
 * Braces rule.
 *
 * @author P.Tomaszewski
 *
 * TODO Move this class and its Test to
 * de.desy.snl.editor.draft.ui.extension.predefinedRules
 */
public class BraceRule extends SingleCharRule {

    /**
     * Creates new rule.
     *
     * @param token
     *            Style token.
     */
    public BraceRule(final IToken token) {
        super(token);
    }

    /**
     * @see SingleCharRule#isRuleChar(int)
     */
    @Override
    protected boolean isRuleChar(final int ch) {
        return (ch == '{') || (ch == '}') || (ch == '[') || (ch == ']')
                || (ch == '(') || (ch == ')');
    }

}
