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
    public String validate(T input);
}
