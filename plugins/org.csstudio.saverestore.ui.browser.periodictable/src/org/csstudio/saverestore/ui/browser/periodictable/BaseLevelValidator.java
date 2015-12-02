package org.csstudio.saverestore.ui.browser.periodictable;

import org.csstudio.ui.fx.util.InputValidator;

/**
 *
 * <code>BaseLevelValidator</code> validates the input if it matches the string that can be parsed to an isotope.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BaseLevelValidator implements InputValidator<String> {

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.InputValidator#validate(java.lang.Object)
     */
    @Override
    public String validate(String newText) {
        try {
            Isotope.of(newText);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage() + " (<element>_<mass>_<abs_charge><p|n>_<energy>)";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
