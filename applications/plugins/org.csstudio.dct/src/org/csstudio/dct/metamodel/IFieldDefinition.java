package org.csstudio.dct.metamodel;

public interface IFieldDefinition {
	String getName();
	String getType();
	String getPrompt();
	String getPromptGroup();
	String getInterest();
	String getSpecial();
	String getSize();
	String getExtra();
	String getInitial();
	IMenuDefinition getMenu();
	
}
