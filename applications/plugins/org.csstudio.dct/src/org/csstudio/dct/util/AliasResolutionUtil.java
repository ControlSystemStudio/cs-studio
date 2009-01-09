package org.csstudio.dct.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;

public class AliasResolutionUtil {
	public static String getParameterValueFromHierarchy(IInstance instance, String parameter) {
		String result = null;

		// 1. step: derive parameters from parents
		Stack<IContainer> parentStack = getParentStack(instance);

		while (!parentStack.isEmpty()) {
			IContainer top = parentStack.pop();
			if (top.hasParameterValue(parameter)) {
				result = top.getParameterValue(parameter);
			}
		}

		return result;
	}

	public static String resolve(String name, IElement element) throws AliasResolutionException {
		Map<String, String> aliases = new HashMap<String, String>();

		if (element instanceof IRecord) {
			IRecord record = (IRecord) element;
			aliases = getFinalAliases(record.getContainer());
		} else if (element instanceof IContainer) {
			aliases = getFinalAliases((IContainer) element);
		}

		String result = ReplaceAliasesUtil.createCanonicalName(name, aliases);

		return result;
	}

	public static Map<String, String> getFinalAliases(IContainer container) {
		Map<String, String> result = new HashMap<String, String>();

		// 1. step: derive parameters from parents
		Stack<IContainer> parentStack = getParentStack(container);

		while (!parentStack.isEmpty()) {
			IContainer top = parentStack.pop();
			result.putAll(top.getParameterValues());
		}

		// 2. step: derive properties from parent
		parentStack = getParentStack(container);

		while (!parentStack.isEmpty()) {
			IContainer top = parentStack.pop();
			result.putAll(top.getProperties());
		}

		// 3. step: derive from containers
		Stack<IContainer> containerStack = getContainerStack(container);

		while (!containerStack.isEmpty()) {
			IContainer top = containerStack.pop();
			result.putAll(getFinalAliases(top));
		}

		return result;
	}

	public static List<String> getRequiredAliasNames(final String input) {
		return ReplaceAliasesUtil.getRequiredAliasNames(input);
	}
	/**
	 * Collect all parent containers in a stack. On top of the returned stack is
	 * the parent that resides at the top of the hierarchy.
	 * 
	 * @return all parent containers, including this
	 */
	protected static Stack<IContainer> getContainerStack(IContainer container) {
		Stack<IContainer> stack = new Stack<IContainer>();

		IContainer c = container.getContainer();

		while (c != null) {
			stack.add(c);
			c = c.getContainer();
		}
		return stack;
	}

	/**
	 * Collect all parent containers in a stack. On top of the returned stack is
	 * the parent that resides at the top of the hierarchy.
	 * 
	 * @return all parent containers, including this
	 */
	protected static Stack<IContainer> getParentStack(IContainer instance) {
		Stack<IContainer> stack = new Stack<IContainer>();

		IContainer c = instance;

		while (c != null) {
			stack.add(c);
			c = c.getParent();
		}

		return stack;
	}
}
