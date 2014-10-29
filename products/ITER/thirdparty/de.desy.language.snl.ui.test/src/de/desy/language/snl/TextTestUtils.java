package de.desy.language.snl;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.eclipse.jface.text.rules.ICharacterScanner;

public final class TextTestUtils {

	/**
	 * Creates the expectation of any char in the string for given mock and
	 * returns the number of expected chars.
	 */
	public static int expectManyChars(final ICharacterScanner icsMock,
			final String text) {
		IExpectationSetters<Integer> expects = EasyMock.expect(icsMock.read());
		int charCount = 0;
		for (final char c : text.toCharArray()) {
			expects = expects.andReturn((int) c);
			charCount++;
		}
		return charCount;
	}

	/**
	 * Creates the expectation of any char in the string for given mock, appends
	 * EOF and returns the number of expected chars (exclusive EOF).
	 */
	public static int expectManyCharsAndAddEOF(final ICharacterScanner icsMock,
			final String text) {
		IExpectationSetters<Integer> expects = EasyMock.expect(icsMock.read());
		int charCount = 0;
		for (final char c : text.toCharArray()) {
			expects = expects.andReturn((int) c);
			charCount++;
		}
		expects.andReturn(ICharacterScanner.EOF);
		return charCount;
	}

}
