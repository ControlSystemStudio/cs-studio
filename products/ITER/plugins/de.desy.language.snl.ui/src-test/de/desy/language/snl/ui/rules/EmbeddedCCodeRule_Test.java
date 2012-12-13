package de.desy.language.snl.ui.rules;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.Token;
import org.junit.Test;

import de.desy.language.snl.TextTestUtils;

public class EmbeddedCCodeRule_Test extends TestCase {
	private static final Token OK_TOKEN = new Token("OK!");

	@Test
	public void testEvaluate() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		final int charCount = TextTestUtils
				.expectManyChars(
						icsMock,
						"\n %{   \nHallo Welt, dass ist C mit einem String \"}%\"\n und noch }%\n Juchhu \n}%  \n");

		Assert.assertEquals(80, charCount);

		// clean-ups
		icsMock.unread();
		EasyMock.expectLastCall().times(1); // goes back to end of the line (may
											// expect line break in next rule).

		EasyMock.replay(icsMock);

		final EmbeddedCCodeRule rule = new EmbeddedCCodeRule(
				EmbeddedCCodeRule_Test.OK_TOKEN);

		Assert.assertEquals(EmbeddedCCodeRule_Test.OK_TOKEN, rule
				.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}

	@Test
	public void testEvaluate_OnFailure() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		final int charCount = TextTestUtils
				.expectManyCharsAndAddEOF(
						icsMock,
						"\n %{   \nHallo Welt, dass ist C mit einem String \"}%\"\n und noch }%\n Juchhu \n ggg }% \n");

		Assert.assertEquals(84, charCount); // + EOF

		// clean-ups
		icsMock.unread();
		EasyMock.expectLastCall().times(84 + 1); // goes back to the start
													// (84 chars + eof).

		EasyMock.replay(icsMock);

		final EmbeddedCCodeRule rule = new EmbeddedCCodeRule(
				EmbeddedCCodeRule_Test.OK_TOKEN);

		Assert.assertEquals(Token.UNDEFINED, rule.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}

	@Test
	public void testEvaluate_WithoutLastLineBreak() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		final int charCount = TextTestUtils
				.expectManyCharsAndAddEOF(
						icsMock,
						"\n %{   \nHallo Welt, dass ist C mit einem String \"}%\"\n und noch }%\n Juchhu \n}%");

		Assert.assertEquals(77, charCount);

		// clean-ups
		icsMock.unread();
		EasyMock.expectLastCall().times(1); // unreads the eof.

		EasyMock.replay(icsMock);

		final EmbeddedCCodeRule rule = new EmbeddedCCodeRule(
				EmbeddedCCodeRule_Test.OK_TOKEN);

		Assert.assertEquals(EmbeddedCCodeRule_Test.OK_TOKEN, rule
				.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}
}
