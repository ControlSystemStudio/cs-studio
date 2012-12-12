package de.desy.language.snl.ui.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.desy.language.editor.ui.editor.highlighting.CharacterSequence;
import de.desy.language.snl.codeElements.PredefinedMethods;

public class SNLMethodRule implements IRule {

	private final PredefinedMethods _method;
	private final IToken _token;

	public SNLMethodRule(final PredefinedMethods method, final IToken token) {
		this._method = method;
		this._token = token;
	}

	public IToken evaluate(final ICharacterScanner scanner) {
		IToken result = null;

		final CharacterSequence cs = new CharacterSequence(scanner);

		final short readCharsInMethodName = SNLMethodRule.checkNamePrefix(cs,
				this._method.getMethodName());
		
		if (readCharsInMethodName > 0) {
			
			// Just prove that no more identifier chars following.
			char charBehindName = cs.readSingleCharacter();
			cs.performUnreadOneSingleChar();
			if( ! Character.isJavaIdentifierPart(charBehindName) ) {
				result = this._token;
			}
			
			// no more name-characters should follow...
			// Character lastReadAsObject =
			// RuleUtils.readUpToFirstNonWhitespace(cs);
			// Character lastReadAsObject =
			// RuleUtils.readWhiteSpaceUntilCharIgnoringComment('(', cs);
			// if (lastReadAsObject != null) {
			// char lastRead = lastReadAsObject;
			// if (lastRead == '(') {
			// result = _token;
			// /*- now check count of params (check the count of comma)
			// <pre>
			// if (cs.hasMoreCharactes()) {
			// boolean hasParam = false;
			// int commaCount = 0;
			// boolean eof = false;
			// lastRead = cs.readSingleCharacter();
			// while (lastRead != ')') {
			// if (!hasParam && !Character.isWhitespace(lastRead)) {
			// hasParam = true;
			// }
			// if (lastRead == ',') {
			// commaCount++;
			// }
			//
			// if (cs.hasMoreCharactes()) {
			// lastRead = cs.readSingleCharacter();
			// } else {
			// eof = true;
			// break;
			// }
			// }
			//
			// if (!eof) {
			// int paramCount = 0;
			// if (hasParam) {
			// paramCount = commaCount + 1;
			// }
			// if (_method.getParamCount() == paramCount) {
			// result = _token;
			// }
			// }
			// }
			// </pre>*/
			// }
			// }
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
