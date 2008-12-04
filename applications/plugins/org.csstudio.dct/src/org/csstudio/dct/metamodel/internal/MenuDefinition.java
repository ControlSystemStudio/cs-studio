package org.csstudio.dct.metamodel.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.metamodel.IChoice;
import org.csstudio.dct.metamodel.IMenuDefinition;


/**
 * Standard implementation of {@link IMenuDefinition}.
 * 
 * @author Sven Wende
 * 
 */
public class MenuDefinition implements IMenuDefinition {

	private String name;
	private List<IChoice> choices;
	
	/**
	 * Constructor.
	 * 
	 * @param name a non-empty name
	 */
	public MenuDefinition(String name) {
		assert name != null;
		this.name = name;
		choices = new ArrayList<IChoice>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<IChoice> getChoices() {
		return choices;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addChoice(IChoice choice) {
		choices.add(choice);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeChoice(IChoice choice) {
		choices.remove(choice);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

}
