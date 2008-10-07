package de.desy.language.snl.ui.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import de.desy.language.editor.core.ILanguageElements;

/**
 * A rule to match specified {@link ILanguageElements}.
 */
public class SNLCodeElementRule implements IRule {

	/**
	 * The internal used code rule.
	 */
	private final WordRule _wordRule;

	/**
	 * Creates a rule for just one code element.
	 * 
	 * @param codeElementToMatch
	 *            The code element to match.
	 * @param tokenToDeliverOnMatch
	 *            The token to be delivered on match.
	 */
	public SNLCodeElementRule(final ILanguageElements codeElementToMatch,
			final IToken tokenToDeliverOnMatch) {
		this(new ILanguageElements[] { codeElementToMatch },
				tokenToDeliverOnMatch);
	}

	/**
	 * Creates a rule for a whole enumeration of code elements.
	 * 
	 * @param <T>
	 *            Just the type of the enumeration elements
	 * @param codeElementsEnum
	 *            The enumeration class (type).
	 * @param tokenToDeliverOnMatch
	 *            The token to be delivered on match.
	 */
	public <T extends Enum<?> & ILanguageElements> SNLCodeElementRule(
			final Class<T> codeElementsEnum, final IToken tokenToDeliverOnMatch) {
		this(codeElementsEnum.getEnumConstants(), tokenToDeliverOnMatch);
	}

	/**
	 * Creates a rule for an array of code elements.
	 * 
	 * @param codeElementsToMatch
	 *            The array of code elements to match.
	 * @param tokenToDeliverOnMatch
	 *            The token to be delivered on match.
	 */
	public SNLCodeElementRule(final ILanguageElements[] codeElementsToMatch,
			final IToken tokenToDeliverOnMatch) {
		final WordDetector wordDetector = new WordDetector();

		this._wordRule = new WordRule(wordDetector, Token.UNDEFINED);
		for (final ILanguageElements codeElement : codeElementsToMatch) {
			this._wordRule.addWord(codeElement.getElementName(),
					tokenToDeliverOnMatch);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IToken evaluate(final ICharacterScanner scanner) {
		return this._wordRule.evaluate(scanner);
	}
}
