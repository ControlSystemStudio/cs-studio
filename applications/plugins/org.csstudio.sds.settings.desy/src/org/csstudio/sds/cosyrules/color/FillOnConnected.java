/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
package org.csstudio.sds.cosyrules.color;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.model.logic.IRule;

/**
 * Color rule Alarm, translated from an ADL file.
 * 
 * @author jbercic, jhatje
 * 
 */
public final class FillOnConnected implements IRule {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "org.css.sds.fillOnConnected";

    /**
     * Standard constructor.
     */
    public FillOnConnected() {
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(final Object[] arguments) {
        int fill = 0;
        if ((arguments != null) && (arguments.length > 0)) {
            if (arguments[0] instanceof Double) {
                Double d = (Double) arguments[0];
                if (Math.abs(d - ConnectionState.CONNECTED.ordinal()) < 0.00001) {
                    fill = 100;
                }
            } else if (arguments[0] instanceof Long) {
                long l = ((Long) arguments[0]);
                if (l == ConnectionState.CONNECTED.ordinal()) {
                    fill = 100;
                }

            } else if (arguments[0] instanceof String) {
                String s = (String) arguments[0];
                if (s.equals(ConnectionState.CONNECTED.name())) {
                    fill = 100;
                }

            }
        }
        return fill;
    }
}
