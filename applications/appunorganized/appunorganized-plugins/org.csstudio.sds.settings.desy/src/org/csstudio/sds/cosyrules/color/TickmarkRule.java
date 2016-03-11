/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: TickmarkRule.java,v 1.6 2010/06/17 10:39:50 hrickens Exp $
 */
package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.IRule;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.6 $
 * @since 27.09.2007
 */
public class TickmarkRule implements IRule {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "org.css.sds.Tickmark";

    /**
     * Standard constructor.
     */
    public TickmarkRule() {
    }

    /**
     * Normally the first argument was return unless the second argument is
     * "No_ALARM" then return Double.NaN. This switch off the Tick mark on the
     * Bargraph.
     *
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Object[] arguments) {
        if (arguments != null) {
            if (arguments.length > 1) {
                if (arguments[1] instanceof Double) {
                    int temp = (int) (((Double) arguments[1]) + 0.5);
                    if ((temp == 0)) {
                        return Double.NaN;
                    }
                } else if (arguments[1] instanceof Long) {
                    if (((Long) arguments[1]) == 0) {
                        return Double.NaN;
                    }
                } else if (arguments[1] instanceof String) {
                    if (((String) arguments[1]).equals("NO_ALARM")) {
                        return Double.NaN;
                    }
                }
            }
            if (arguments.length > 0) {
                return arguments[0];
            }
        }
        return 0.0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

}
