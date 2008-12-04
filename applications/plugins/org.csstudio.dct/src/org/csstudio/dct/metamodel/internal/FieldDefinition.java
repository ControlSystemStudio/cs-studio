package org.csstudio.dct.metamodel.internal;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IMenuDefinition;


/**
 * Standard implementation of {@link IFieldDefinition}.
 * 
 * @author Sven Wende
 * 
 */
public class FieldDefinition implements IFieldDefinition {
	private String extra;
	private String initial;
	private String interest;
	private String name;
	private String prompt;
	private String promptGroup;
	private String size;
	private String special;
	private String type;
	private IMenuDefinition menuDefinition;

	public FieldDefinition(String name, String type) {
		assert type != null;
		assert name != null;
		this.type = type;
		this.name = name;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getInitial() {
		return initial;
	}

	public void setInitial(String initial) {
		this.initial = initial;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public IMenuDefinition getMenu() {
		return menuDefinition;
	}

	public void setMenuDefinition(IMenuDefinition menuDefinition) {
		this.menuDefinition = menuDefinition;
	}

	public String getName() {
		return name;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getPromptGroup() {
		return promptGroup;
	}

	public void setPromptGroup(String promptGroup) {
		this.promptGroup = promptGroup;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSpecial() {
		return special;
	}

	public void setSpecial(String special) {
		this.special = special;
	}

	public String getType() {
		return type;
	}

}
