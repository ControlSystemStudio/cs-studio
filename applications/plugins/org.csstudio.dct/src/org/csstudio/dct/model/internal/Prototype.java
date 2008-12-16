package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;

/**
 * Standard implementation of {@link IPrototype}.
 * 
 * @author Sven Wende
 */
public class Prototype extends AbstractContainer implements IPrototype {
	private List<Parameter> parameters;

	public Prototype(String name, UUID id) {
		super(name, null, id);
		this.parameters = new ArrayList<Parameter>();
	}	
	

	/**
	 * {@inheritDoc}
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addParameter(int index, Parameter parameter) {
		parameters.add(index, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeParameter(Parameter parameter) {
		parameters.remove(parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeParameter(int index) {
		parameters.remove(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasParameter(String key) {
		boolean result = false;
		for (Parameter p : parameters) {
			if (p.getName().equals(key)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getParameterValues() {
		Map<String, String> result = new HashMap<String, String>();

		for (Parameter p : parameters) {
			result.put(p.getName(), p.getDefaultValue());
		}

		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void accept(IVisitor visitor) {
		visitor.visit(this);
		
		for(IInstance instance : getInstances()) {
			instance.accept(visitor);
		}
		
		for(IRecord record : getRecords()) {
			record.accept(visitor);
		}
	}
	
	/**
	 *{@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof Prototype) {
			Prototype prototype = (Prototype) obj;

			if (super.equals(obj)) {
				// .. parameters
				if (getParameters().equals(prototype.getParameters())) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
		
	}
}
