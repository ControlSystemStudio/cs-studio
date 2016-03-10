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
import org.csstudio.sds.util.ColorAndFontUtil;

/**
 * Advanced fast color rule.
 *
 * @author Kai Meyer
 * @version $Revision: 1.2 $
 *
 */
public final class AdvancedColorRule implements IRule {

    /**
     * BLACK color.
     */
    private static final String COLOR_BLACK = "#000000";

    /**
     * Standard constructor.
     */
    public AdvancedColorRule() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Object[] arguments) {
        if ((arguments != null) && (arguments.length > 0) && (arguments.length < 5)) {
            if (arguments[0] instanceof Number) {

                String highColor =arguments.length>1 ? resolveColor(arguments[1]) : COLOR_BLACK;
                String mediumColor = arguments.length>2 ? resolveColor(arguments[2]) : COLOR_BLACK;
                String lowColor = arguments.length>3 ? resolveColor(arguments[3]) : COLOR_BLACK;

                double d = ((Number) arguments[0]).doubleValue();

                if (d > 66) {
                    return highColor;
                }

                if (d > 33) {
                    return mediumColor;
                }

                if (d > 0) {
                    return lowColor;
                }
            }
        }

        return COLOR_BLACK;
    }

    private String resolveColor(final Object arg) {
        if ((arg!=null) && ColorAndFontUtil.isHex(arg.toString())) {
            return arg.toString();
        } else {
            return COLOR_BLACK;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "";
    }

}
