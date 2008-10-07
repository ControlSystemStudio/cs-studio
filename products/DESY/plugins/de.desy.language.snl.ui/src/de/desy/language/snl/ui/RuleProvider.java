package de.desy.language.snl.ui;

import java.util.LinkedList;
import java.util.List;

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
import de.desy.language.snl.codeElements.Keywords;
import de.desy.language.snl.codeElements.PredefinedConstants;
import de.desy.language.snl.codeElements.PredefinedMethods;
import de.desy.language.snl.codeElements.PredefinedTypes;
import de.desy.language.snl.ui.rules.EmbeddedCCodeRule;
import de.desy.language.snl.ui.rules.SNLCodeElementRule;
import de.desy.language.snl.ui.rules.SNLCodeElementTextAttributeConstants;
import de.desy.language.snl.ui.rules.SNLMethodRule;
import de.desy.language.snl.ui.rules.SNLOperatorRule;
import de.desy.language.snl.ui.rules.WordDetector;

public class RuleProvider extends AbstractRuleProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IRule> doCreateCustomRules() {
		final List<IRule> rules = new LinkedList<IRule>();
		// Rule of embedded c.
		final IToken embeddedCToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_EMBEDDED_C);
		final EmbeddedCCodeRule embeddedCRule = new EmbeddedCCodeRule(
				embeddedCToken);
		rules.add(embeddedCRule);

		// Rule of MultiLineComments.
		final IToken multiLineCommentToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_MULTI_LINE_COMMENT);
		final MultiLineRule multiLineCommentRule = new MultiLineRule("/*",
				"*/", multiLineCommentToken);
		rules.add(multiLineCommentRule);

		// Rule of SingleLineComments.
		final IToken singleLineCommentToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_SINGLE_LINE_COMMENT);
		final SingleLineRule singleLineCommentRule = new SingleLineRule("//",
				null, singleLineCommentToken);
		rules.add(singleLineCommentRule);

		// Rule of SingleLine embedded C.
		final IToken singleLineEmbeddedCToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_SINGLE_LINE_EMBEDDED_C);
		final SingleLineRule singleLineEmbeddedCRule = new SingleLineRule("%%",
				null, singleLineEmbeddedCToken);
		rules.add(singleLineEmbeddedCRule);

		// Rule of Braces.
		final IToken braceToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_BRACES);
		final BraceRule braceRule = new BraceRule(braceToken);
		rules.add(braceRule);

		// Attention: Have to be placed before any word-rule.
		final IToken predefinedMethodToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_PREDEFINED_METHOD);

		for (final PredefinedMethods p : PredefinedMethods.values()) {
			final SNLMethodRule methodRule = new SNLMethodRule(p,
					predefinedMethodToken);
			rules.add(methodRule);
		}

		// Add rule for strings
		final IToken stringToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_STRING);
		rules.add(new SingleLineRule("\"", "\"", stringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new SingleLineRule("'", "'", stringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Rule of predefined keywords:
		final IToken keywordToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_KEYWORD);
		final SNLCodeElementRule keywordRule = new SNLCodeElementRule(
				Keywords.class, keywordToken);
		rules.add(keywordRule);

		// Rule of predefined types:
		final IToken predefinedTypesToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_TYPE);
		final SNLCodeElementRule predefinedTypesRule = new SNLCodeElementRule(
				PredefinedTypes.class, predefinedTypesToken);
		rules.add(predefinedTypesRule);

		// Rule of predefined constants:
		final IToken predefinedConstantsToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_CONSTANTS);
		final SNLCodeElementRule predefinedConstantsRule = new SNLCodeElementRule(
				PredefinedConstants.class, predefinedConstantsToken);
		rules.add(predefinedConstantsRule);

		// Rule of other words (must be placed before numbers to avoid
		// highlighting of "abc1"):
		rules
				.add(new WordRule(
						new WordDetector(),
						this
								.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_DEFAULT)));

		// Rule of numbers:
		final IToken numberToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_NUMBER);
		final NumberRule numberRule = new NumberRule(numberToken);
		rules.add(numberRule);

		// Rule of operators:
		final IToken operatorToken = this
				.createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_OPERATOR);
		final SNLOperatorRule opRule = new SNLOperatorRule(operatorToken);
		rules.add(opRule);

		// Rule of preprocessor statements: TODO decide if required?
		// IToken preprocessorToken =
		// createTokenForCodeElementType(SNLCodeElementTextAttributeConstants.SNL_TYPE);
		// PreprocessorRule preprocessorRule = new PreprocessorRule(
		// new WordDetector(), preprocessorToken);
		// for (PrecompilerDirectives directive :
		// PrecompilerDirectives.values()) {
		// preprocessorRule.addWord(directive.getElementName(),
		// preprocessorToken);
		// }
		// rules.add(preprocessorRule);

		return rules;
	}

	/**
	 * Creates a Token from a given element of
	 * {@link SNLCodeElementTextAttributeConstants}.
	 * 
	 * @param element
	 *            The element, not null.
	 * @return A token, not null.
	 */
	private IToken createTokenForCodeElementType(
			final SNLCodeElementTextAttributeConstants element) {
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
