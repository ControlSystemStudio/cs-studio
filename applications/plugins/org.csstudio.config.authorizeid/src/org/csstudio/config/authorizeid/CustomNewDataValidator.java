package org.csstudio.config.authorizeid;

/**
 * {@code NewDataValidator} is a class that validates input data
 * for LDAP entries for second table
 * @author Rok Povsic
 */
public class CustomNewDataValidator implements ICustomInputValidator {

	/**
	 * If input is empty or has LDAP non-supported characters, "Ok"
	 * button is disabled.
	 */
	public String isValid(String newText1, String newText2) {
		int len1 = newText1.length();
		int len2 = newText2.length();
		
		if(len1 < 1) {
			return "Please insert at least one character.";
		}
		
		// LDAP non-supported characters: +, \ and /
		if((newText1.indexOf("+") > -1) || (newText1.indexOf("\\") > -1) || (newText1.indexOf("/")) > -1) {
			return "You have entered a LDAP non-supported character (+, \\, /).";
		}
		
		if(len2 < 1) {
			return "Please insert at least one character.";
		}
		
		// LDAP non-supported characters: +, \ and /
		if((newText2.indexOf("+") > -1) || (newText2.indexOf("\\") > -1) || (newText2.indexOf("/")) > -1) {
			return "You have entered a LDAP non-supported character (+, \\, /).";
		}
		
		return null;
	}
}
