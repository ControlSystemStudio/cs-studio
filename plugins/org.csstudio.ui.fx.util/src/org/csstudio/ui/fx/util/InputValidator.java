package org.csstudio.ui.fx.util;

/**
 *
 * <code>InputValidator</code> is a validator which validates an object and returns the message what is wrong with the
 * object (if anything is indeed wrong).
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T> the type of the value that can be checked by this validator
 */
@FunctionalInterface
public interface InputValidator<T> {

    /**
     * Validate the input value and return a string describing what is wrong with the input or null if the value is
     * acceptable.
     *
     * @param input the value to validate
     * @return error description if no acceptable or null if acceptable
     */
    String validate(T input);

    /**
     * Validation may block or allow the procedure to continue. In case if the process is allowed to continue even if
     * the validation of the input fails, this method should return true. If the process should be blocked this method
     * should return false. The method can be used when the validation result is just a warning but still acceptable
     * (e.g. allow user to confirm the dialog input even if the input is not valid).
     *
     * @param input the input that is being validated
     * @return true if the process is allowed to continue or false otherwise
     */
    default boolean isAllowedToProceed(T input){
        return false;
    }
}
