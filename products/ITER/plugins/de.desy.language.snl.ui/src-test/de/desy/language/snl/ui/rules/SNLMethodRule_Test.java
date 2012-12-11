package de.desy.language.snl.ui.rules;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.Token;
import org.junit.Test;

import de.desy.language.snl.codeElements.PredefinedMethods;

public class SNLMethodRule_Test extends TestCase {

	private static final Token OK_TOKEN = new Token("OK!");

	@Test
	public void testEvaluateOnSuccessWithWithespacesBetweenNameAndBraket() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		EasyMock.expect(icsMock.read()).andReturn((int) 'p').andReturn(
				(int) 'v').andReturn((int) 'P').andReturn((int) 'u').andReturn(
				(int) 't').andReturn((int) ' ');
		// .andReturn((int) ' ').andReturn((int) ' ').andReturn((int) '(');
		// .andReturn((int) 'p') // Param: p1 (but params be ignored)
		// .andReturn((int) '1').andReturn((int) ')');
		// no EOF is specified cause no more char should be read afterwards ')'

		// clean-ups
		icsMock.unread();
		EasyMock.expectLastCall().times(1); // goes back to end of the name...

		EasyMock.replay(icsMock);

		final SNLMethodRule rule = new SNLMethodRule(
				PredefinedMethods.pvPut_Channel, SNLMethodRule_Test.OK_TOKEN);

		Assert
				.assertEquals(SNLMethodRule_Test.OK_TOKEN, rule
						.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}

	@Test
	public void testEvaluateOnSuccess() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		EasyMock.expect(icsMock.read()).andReturn((int) 'p').andReturn(
				(int) 'v').andReturn((int) 'P').andReturn((int) 'u').andReturn(
				(int) 't').andReturn((int) ' ');
		// .andReturn((int) '(');
		// .andReturn((int) 'p') // Param: p1 (will be ignored)
		// .andReturn((int) '1').andReturn((int) ')');

		// clean-ups
		icsMock.unread();
		EasyMock.expectLastCall().times(1); // unread brace only

		EasyMock.replay(icsMock);

		final SNLMethodRule rule = new SNLMethodRule(
				PredefinedMethods.pvPut_Channel, SNLMethodRule_Test.OK_TOKEN);

		Assert
				.assertEquals(SNLMethodRule_Test.OK_TOKEN, rule
						.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}

	/**
	 * Currently this test has no special behavior since parameter recognition
	 * is removed!
	 */
	@Test
	public void testEvaluateOnSuccessWithTextParam() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		EasyMock.expect(icsMock.read()).andReturn((int) 'p').andReturn(
				(int) 'v').andReturn((int) 'P').andReturn((int) 'u').andReturn(
				(int) 't').andReturn((int) '(');
		// .andReturn((int) '(');
		// .andReturn((int) '"') // Param: p1 (will be ignored)
		// .andReturn((int) 'H').andReturn((int) 'a').andReturn((int)
		// '"').andReturn((int) ')');

		// clean-ups
		icsMock.unread();
		EasyMock.expectLastCall().times(1); // unread brace only

		EasyMock.replay(icsMock);

		final SNLMethodRule rule = new SNLMethodRule(
				PredefinedMethods.pvPut_Channel, SNLMethodRule_Test.OK_TOKEN);

		Assert
				.assertEquals(SNLMethodRule_Test.OK_TOKEN, rule
						.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}

	/**
	 * This test will not fail anymore, cause parameters are ignored!
	 */
	@Test
	public void testEvaluateOnFailureCauseOfInvalidParams() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		EasyMock.expect(icsMock.read()).andReturn((int) 'p').andReturn(
				(int) 'v').andReturn((int) 'P').andReturn((int) 'u').andReturn(
				(int) 't').andReturn((int) '(');
		// .andReturn((int) '(');
		// .andReturn((int) ')');

		icsMock.unread();
		EasyMock.expectLastCall().times(1); // unread brace only

		EasyMock.replay(icsMock);

		final SNLMethodRule rule = new SNLMethodRule(
				PredefinedMethods.pvPut_Channel, SNLMethodRule_Test.OK_TOKEN);

		// Will not fail cause parameter is ignored.
		// assertEquals(Token.UNDEFINED, rule.evaluate(icsMock));
		Assert
				.assertEquals(SNLMethodRule_Test.OK_TOKEN, rule
						.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}

	@Test
	public void testEvaluateOnFailureWithEOF() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		EasyMock.expect(icsMock.read()).andReturn((int) 'p').andReturn(
				(int) 'v').andReturn((int) 'P')
				.andReturn(ICharacterScanner.EOF);

		icsMock.unread();
		EasyMock.expectLastCall().times(4);

		EasyMock.replay(icsMock);

		final SNLMethodRule rule = new SNLMethodRule(
				PredefinedMethods.pvPut_Channel, SNLMethodRule_Test.OK_TOKEN);

		Assert.assertEquals(Token.UNDEFINED, rule.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}

	/**
	 * Will not fail anymore cause parameter list is ignored.
	 */
	@Test
	public void testEvaluateOnFailureWithEOFBehinBraket() {
		final ICharacterScanner icsMock = EasyMock
				.createMock(ICharacterScanner.class);

		EasyMock.expect(icsMock.read()).andReturn((int) 'p').andReturn(
				(int) 'v').andReturn((int) 'P').andReturn((int) 'u').andReturn(
				(int) 't').andReturn((int) '(');
		// .andReturn((int) '(');
		// .andReturn(ICharacterScanner.EOF);

		icsMock.unread();
		EasyMock.expectLastCall().times(1); // unread brace only

		EasyMock.replay(icsMock);

		final SNLMethodRule rule = new SNLMethodRule(
				PredefinedMethods.pvPut_Channel, SNLMethodRule_Test.OK_TOKEN);

		// Will not fail cause parameter is ignored.
		// assertEquals(Token.UNDEFINED, rule.evaluate(icsMock));
		Assert
				.assertEquals(SNLMethodRule_Test.OK_TOKEN, rule
						.evaluate(icsMock));

		EasyMock.verify(icsMock);
	}

}
