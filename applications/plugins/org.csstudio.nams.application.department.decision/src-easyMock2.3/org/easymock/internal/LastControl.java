/*
 * Copyright (c) 2001-2007 OFFIS, Tammo Freese.
 * This program is made available under the terms of the MIT License.
 */
package org.easymock.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.easymock.IArgumentMatcher;
import org.easymock.internal.matchers.And;
import org.easymock.internal.matchers.Not;
import org.easymock.internal.matchers.Or;

public class LastControl {
	private static final ThreadLocal<MocksControl> threadToControl = new ThreadLocal<MocksControl>();

	private static final ThreadLocal<Stack<Object[]>> threadToCurrentArguments = new ThreadLocal<Stack<Object[]>>();

	private static final ThreadLocal<Stack<IArgumentMatcher>> threadToArgumentMatcherStack = new ThreadLocal<Stack<IArgumentMatcher>>();

	public static synchronized void reportLastControl(final MocksControl control) {
		if (control != null) {
			LastControl.threadToControl.set(control);
		} else {
			LastControl.threadToControl.remove();
		}
	}

	public static synchronized MocksControl lastControl() {
		return LastControl.threadToControl.get();
	}

	public static synchronized void reportMatcher(final IArgumentMatcher matcher) {
		Stack<IArgumentMatcher> stack = LastControl.threadToArgumentMatcherStack
				.get();
		if (stack == null) {
			stack = new Stack<IArgumentMatcher>();
			LastControl.threadToArgumentMatcherStack.set(stack);
		}
		stack.push(matcher);
	}

	public static synchronized List<IArgumentMatcher> pullMatchers() {
		final Stack<IArgumentMatcher> stack = LastControl.threadToArgumentMatcherStack
				.get();
		if (stack == null) {
			return null;
		}
		LastControl.threadToArgumentMatcherStack.remove();
		return new ArrayList<IArgumentMatcher>(stack);
	}

	public static synchronized void reportAnd(final int count) {
		final Stack<IArgumentMatcher> stack = LastControl.threadToArgumentMatcherStack
				.get();
		LastControl.assertState(stack != null, "no matchers found.");
		stack.push(new And(LastControl.popLastArgumentMatchers(count)));
	}

	public static synchronized void reportNot() {
		final Stack<IArgumentMatcher> stack = LastControl.threadToArgumentMatcherStack
				.get();
		LastControl.assertState(stack != null, "no matchers found.");
		stack.push(new Not(LastControl.popLastArgumentMatchers(1).get(0)));
	}

	private static List<IArgumentMatcher> popLastArgumentMatchers(
			final int count) {
		final Stack<IArgumentMatcher> stack = LastControl.threadToArgumentMatcherStack
				.get();
		LastControl.assertState(stack != null, "no matchers found.");
		LastControl.assertState(stack.size() >= count, "" + count
				+ " matchers expected, " + stack.size() + " recorded.");
		final List<IArgumentMatcher> result = new LinkedList<IArgumentMatcher>();
		result.addAll(stack.subList(stack.size() - count, stack.size()));
		for (int i = 0; i < count; i++) {
			stack.pop();
		}
		return result;
	}

	private static void assertState(boolean toAssert, final String message) {
		if (!toAssert) {
			LastControl.threadToArgumentMatcherStack.remove();
			throw new IllegalStateException(message);
		}
	}

	public static void reportOr(final int count) {
		final Stack<IArgumentMatcher> stack = LastControl.threadToArgumentMatcherStack
				.get();
		LastControl.assertState(stack != null, "no matchers found.");
		stack.push(new Or(LastControl.popLastArgumentMatchers(count)));
	}

	public static Object[] getCurrentArguments() {
		final Stack<Object[]> stack = LastControl.threadToCurrentArguments
				.get();
		if ((stack == null) || stack.empty()) {
			return null;
		}
		return stack.lastElement();
	}

	public static void pushCurrentArguments(final Object[] args) {
		Stack<Object[]> stack = LastControl.threadToCurrentArguments.get();
		if (stack == null) {
			stack = new Stack<Object[]>();
			LastControl.threadToCurrentArguments.set(stack);
		}
		stack.push(args);
	}

	public static void popCurrentArguments() {
		final Stack<Object[]> stack = LastControl.threadToCurrentArguments
				.get();
		stack.pop();
	}
}
