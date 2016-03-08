/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
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
            return e.getMessage() + " (<element>_<mass>_<abs_charge><p|n>)";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
