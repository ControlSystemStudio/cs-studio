/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal.matchers;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayEquals extends Equals {

	public static Object[] createObjectArray(final Object array) {
		if (array instanceof Object[]) {
			return (Object[]) array;
		}
		final Object[] result = new Object[Array.getLength(array)];
		for (int i = 0; i < Array.getLength(array); i++) {
			result[i] = Array.get(array, i);
		}
		return result;
	}

	public ArrayEquals(final Object expected) {
		super(expected);
	}

	@Override
	public void appendTo(final StringBuffer buffer) {
		if ((this.getExpected() != null)
				&& this.getExpected().getClass().isArray()) {
			this.appendArray(ArrayEquals.createObjectArray(this.getExpected()),
					buffer);
		} else {
			super.appendTo(buffer);
		}
	}

	@Override
	public boolean matches(final Object actual) {
		final Object expected = this.getExpected();
		if ((expected instanceof boolean[])
				&& ((actual == null) || (actual instanceof boolean[]))) {
			return Arrays.equals((boolean[]) expected, (boolean[]) actual);
		} else if ((expected instanceof byte[])
				&& ((actual == null) || (actual instanceof byte[]))) {
			return Arrays.equals((byte[]) expected, (byte[]) actual);
		} else if ((expected instanceof char[])
				&& ((actual == null) || (actual instanceof char[]))) {
			return Arrays.equals((char[]) expected, (char[]) actual);
		} else if ((expected instanceof double[])
				&& ((actual == null) || (actual instanceof double[]))) {
			return Arrays.equals((double[]) expected, (double[]) actual);
		} else if ((expected instanceof float[])
				&& ((actual == null) || (actual instanceof float[]))) {
			return Arrays.equals((float[]) expected, (float[]) actual);
		} else if ((expected instanceof int[])
				&& ((actual == null) || (actual instanceof int[]))) {
			return Arrays.equals((int[]) expected, (int[]) actual);
		} else if ((expected instanceof long[])
				&& ((actual == null) || (actual instanceof long[]))) {
			return Arrays.equals((long[]) expected, (long[]) actual);
		} else if ((expected instanceof short[])
				&& ((actual == null) || (actual instanceof short[]))) {
			return Arrays.equals((short[]) expected, (short[]) actual);
		} else if ((expected instanceof Object[])
				&& ((actual == null) || (actual instanceof Object[]))) {
			return Arrays.equals((Object[]) expected, (Object[]) actual);
		} else {
			return super.matches(actual);
		}
	}

	private void appendArray(final Object[] array, final StringBuffer buffer) {
		buffer.append("[");
		for (int i = 0; i < array.length; i++) {
			new Equals(array[i]).appendTo(buffer);
			if (i != array.length - 1) {
				buffer.append(", ");
			}
		}
		buffer.append("]");
	}
}
