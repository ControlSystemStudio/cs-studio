package org.csstudio.sds.language.script.ui.rules;

import org.csstudio.sds.language.script.codeElements.PredefinedFunctions;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.desy.language.editor.ui.editor.highlighting.CharacterSequence;

public class ScriptMethodRule implements IRule {

    private final PredefinedFunctions _method;
    private final IToken _token;

    public ScriptMethodRule(final PredefinedFunctions method, final IToken token) {
        this._method = method;
        this._token = token;
    }

    public IToken evaluate(final ICharacterScanner scanner) {
        IToken result = null;

        final CharacterSequence cs = new CharacterSequence(scanner);

        final short readCharsInMethodName = ScriptMethodRule.checkNamePrefix(cs,
                this._method.getElementName());
        if (readCharsInMethodName > 0) {
            result = this._token;
        }

        if (result == null) {
            result = Token.UNDEFINED;
            cs.performUnread();
        } else {
            cs.performUnreadWithKeepingGivenCharsRead(readCharsInMethodName);
        }

        return result;
    }

    /**
     * Checks if the given method name is the name in the character sequence.
     *
     * @returns the number of chars read for the name.
     */
    private static short checkNamePrefix(final CharacterSequence cs,
            final String methodName) {

        short readedChars = 0;
        char lastReadChar;
        final char[] pattern = methodName.toCharArray();

        for (final char c : pattern) {
            if (!cs.hasMoreCharacters()) {
                // no more chars, but char expected -> false
                return -1;
            }

            lastReadChar = cs.readSingleCharacter();
            readedChars++;

            if (lastReadChar != c) {
                return -1;
            }
        }

        if (pattern.length != cs.getReadCount()) {
            return -1;
        }

        return readedChars;
    }
}
