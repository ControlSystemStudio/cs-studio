package org.csstudio.config.authorizeid;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * {@code NewDataValidator} is a class that validates input data
 * for LDAP entries.
 * @author Rok Povsic
 */
public class NewDataValidator implements IInputValidator {

	/**
	 * If input is empty or has LDAP non-supported characters, "Ok"
	 * button is disabled.
	 */
	public String isValid(String newText) {
		int len = newText.length();
		
		if(len < 1) {
			return Messages.NewDataValidator_ValidatorDesc;
		}
		
		// LDAP non-supported characters: +, \ and /
		if((newText.indexOf("+") > -1) || (newText.indexOf("\\") > -1) || (newText.indexOf("/")) > -1) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return Messages.NewDataValidator_ValidatorWarn;
		}
		
		return null;
	}
}
