package org.csstudio.config.authorizeid;

/**
 * The ICustomInputValidator is customization of {@code IInputValidator}.
 * <p>It validates two inputs instead of one</p> 
 * @see org.eclipse.jface.dialogs.InputDialog
 */
public interface ICustomInputValidator {
    /**
     * Validates the given string.  Returns an error message to display
     * if the new text is invalid.  Returns <code>null</code> if there
     * is no error.  Note that the empty string is not treated the same
     * as <code>null</code>; it indicates an error state but with no message
     * to display.
     * 
     * @param newText the text to check for validity
     * 
     * @return an error message or <code>null</code> if no error
     */
    public String isValid(String newText1, String newText2);
}
