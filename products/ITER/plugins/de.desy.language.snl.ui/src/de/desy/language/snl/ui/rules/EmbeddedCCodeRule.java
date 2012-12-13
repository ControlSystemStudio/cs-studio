package de.desy.language.snl.ui.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.desy.language.editor.ui.editor.highlighting.CharacterSequence;
import de.desy.language.editor.ui.editor.highlighting.RuleUtils;

/**
 * This rule searches for an embedded c statement. Each separator has to stand
 * alone (except blanks) in its line. The separators are '%{' and '}%'.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
public class EmbeddedCCodeRule implements IRule {

	private final IToken _tokenToDeliverOnMatch;

	public EmbeddedCCodeRule(final IToken tokenToDeliverOnMatch) {
		this._tokenToDeliverOnMatch = tokenToDeliverOnMatch;
	}

	private IToken returnFail(final CharacterSequence cs) {
		cs.performUnread();
		return Token.UNDEFINED;
	}

	public IToken evaluate(final ICharacterScanner scanner) {
		final CharacterSequence cs = new CharacterSequence(scanner);

		// First char have to be a Line-break:
//		if (!cs.hasMoreCharacters() || (cs.readSingleCharacter() != '\n')) {
//			return this.returnFail(cs);
//		}

		Character charAsObject = RuleUtils.readUpToFirstNonWhitespace(cs);

		// Next char have to exist and have to be '%'
		if ((charAsObject == null) || (charAsObject.charValue() != '%')) {
			return this.returnFail(cs);
		}

		// Next char have to be a '{':
		if (!cs.hasMoreCharacters() || (cs.readSingleCharacter() != '{')) {
			return this.returnFail(cs);
		}

		if (RuleUtils.readWhitespacesUpToNextLineBreak(cs)) {
			// Prefix complete
			while (RuleUtils.readCharsUpToNextLineBreak(cs)) {
				charAsObject = RuleUtils.readUpToFirstNonWhitespace(cs);

				// Next char have to exist and have to be '%'
				if ((charAsObject == null) || (charAsObject.charValue() != '}')) {
					continue;
				}
				// Next char have to be a '%':
				if (!cs.hasMoreCharacters()
						|| (cs.readSingleCharacter() != '%')) {
					continue;
				}

				if (RuleUtils.readWhitespacesUpToNextLineBreak(cs)) {
					// PostFix complete
					final long takenCharCount = cs.getReadCount() - 1;
					// unread last '\n'
					cs.performUnreadWithKeepingGivenCharsRead(takenCharCount);
					return this._tokenToDeliverOnMatch;
				}
				if (cs.hasEndOfStreamBeenReached()) {
					// PostFix complete caused by eof.
					return this._tokenToDeliverOnMatch;
				}

			}
			return this.returnFail(cs);
		}
		return this.returnFail(cs);

	}
}
