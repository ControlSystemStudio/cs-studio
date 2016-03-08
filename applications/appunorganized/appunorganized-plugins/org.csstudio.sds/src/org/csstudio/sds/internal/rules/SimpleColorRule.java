/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.internal.rules;

import org.csstudio.sds.model.IRule;
import org.eclipse.core.runtime.Platform;

/**
 * Simple fast color rule.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class SimpleColorRule implements IRule {

    /**
     * RED color.
     */
    static final String COLOR_RED = "#ff0000";

    /**
     * YELLOW color.
     */
    static final String COLOR_YELLOW = "#FFFF00";

    /**
     * GREEN color.
     */
    static final String COLOR_GREEN = "#00ff00";

    /**
     * BLACK color.
     */
    static final String COLOR_BLACK = "#000000";

    /**
     * Standard constructor.
     */
    public SimpleColorRule() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Object[] arguments) {
        if ((arguments != null) && (arguments.length > 0)) {
            if (arguments[0] instanceof Number) {
                double d = ((Number) arguments[0]).doubleValue();

                if (d > 66) {
                    return COLOR_RED;
                }

                if (d > 33) {
                    return COLOR_YELLOW;
                }

                if (d > 0) {
                    return COLOR_GREEN;
                }
            }
        }

        return COLOR_BLACK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        String ls = System.getProperty(Platform.PREF_LINE_SEPARATOR);
        return "When the value is a Number and "+ls
             + "value > 66 return red: "+COLOR_RED+ls
             + "value > 33 return yellow: "+COLOR_YELLOW+ls
             + "value >  0 return green: "+COLOR_GREEN+ls
             + "else return black: "+COLOR_BLACK;
    }
}
