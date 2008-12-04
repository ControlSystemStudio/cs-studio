package org.csstudio.dct.model.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IInstanceContainer;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.util.CompareUtil;

/**
 * Standard implementation of {@link IInstance}.
 * 
 * @author Sven Wende
 */
public class Instance extends AbstractContainer implements IInstance {

	private Map<String, String> parameterValues;
	private IInstanceContainer container;

	public Instance(IContainer parent) {
		super(null, parent);
		this.parameterValues = new HashMap<String, String>();
	}
	
	public Instance(IContainer parent, UUID id) {
		super(null, parent, id);
		this.parameterValues = new HashMap<String, String>();
	}

	public Instance(String name, IPrototype prototype) {
		this(prototype);
		setName(name);
	}

	public Instance(String name, IPrototype prototype, UUID id) {
		this(prototype, id);
		setName(name);
	}
	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getParameterValues() {
		return parameterValues;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParameterValue(String key, String value) {
		if (value != null && value.length() > 0) {
			parameterValues.put(key, value);
		} else {
			parameterValues.remove(key);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getParameterValue(String key) {
		return parameterValues.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasParameterValue(String key) {
		return parameterValues.containsKey(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public IPrototype getPrototype() {
		IContainer parent = getParent();

		if (parent instanceof IPrototype) {
			return (IPrototype) parent;
		} else {
			if (parent instanceof IInstance) {
				return ((IInstance) parent).getPrototype();
			} else {
				return null;
			}
		}
	}

	public IInstanceContainer getContainer() {
		return container;
	}

	public void setContainer(IInstanceContainer container) {
		this.container = container;
	}

	public Map<String, Object> getFinalProperties() {
		Map<String, Object> result = new HashMap<String, Object>();

		Stack<IContainer> stack = getParentStack();

		while (!stack.isEmpty()) {
			IContainer top = stack.pop();
			result.putAll(top.getProperties());
		}

		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof Instance) {
			Instance instance = (Instance) obj;

			if (super.equals(obj)) {
				// .. parameter values
				if (getParameterValues().equals(instance.getParameterValues())) {
					// .. container
					if (CompareUtil.idsEqual(getContainer(), instance.getContainer())) {
						// .. folder
						if (CompareUtil.idsEqual(getParentFolder(), instance.getParentFolder())) {
							result = true;
						}
					}
				}
			}
		}

		return result;
	}

//	/**
//	 *{@inheritDoc}
//	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((parameterValues == null) ? 0 : parameterValues.hashCode());
//		return result;
//
//	}
}
