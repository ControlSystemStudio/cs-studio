package de.desy.language.editor.ui.editor.highlighting.predefinedRules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Checks for single char.
 *
 * @author P.Tomaszewski
 *
 * TODO Move this class and its Test to
 * de.desy.snl.editor.draft.ui.extension.predefinedRules
 */
public abstract class SingleCharRule implements IRule {

    /** Style token. */
    private final IToken token;

    /**
     * Creates new rule.
     *
     * @param token
     *            Style token.
     */
    public SingleCharRule(final IToken token) {
        super();
        this.token = token;
    }

    /**
     * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
     */
    @Override
    public IToken evaluate(final ICharacterScanner scanner) {
        final int ch = scanner.read();

        if (this.isRuleChar(ch)) {
            return this.token;
        }
        scanner.unread();
        return Token.UNDEFINED;
    }

    /**
     * Checks if char is rule char.
     *
     * @param ch
     *            Char to check.
     * @return <b>true</b> if rule char.
     */
    protected abstract boolean isRuleChar(int ch);
}
