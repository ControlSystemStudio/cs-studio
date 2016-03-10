/*
        * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
        * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
        *
        * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
        * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
        NOT LIMITED
        * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
        AND
        * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
        BE LIABLE
        * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
        CONTRACT,
        * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
        SOFTWARE OR
        * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
        DEFECTIVE
        * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
        REPAIR OR
        * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
        OF THIS LICENSE.
        * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
        DISCLAIMER.
        * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
        ENHANCEMENTS,
        * OR MODIFICATIONS.
        * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
        MODIFICATION,
        * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
        DISTRIBUTION OF THIS
        * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
        MAY FIND A COPY
        * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
        */
package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.IRule;
import org.csstudio.dal.DynamicValueCondition;

/**
 * Check if the incoming Object represent a Invalid state
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 08.06.2010
 */
public class IsInvalidRule implements IRule {

    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "org.csstudio.rule.boolean.isInvalid";

    private static final int SEVERTIY = 3;
    /**
     * Standard constructor.
     */
    public IsInvalidRule() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Object[] arguments) {
        int severity = 0;
        if ((arguments != null) && (arguments.length > 0)) {
            if (arguments[0] instanceof Integer) {
                severity = (Integer) arguments[0];
            } else if(arguments[0] instanceof Long) {
                severity = ((Long) arguments[0]).intValue();
            } else if(arguments[0] instanceof Double) {
                Double d = (Double)arguments[0];
                if((d<3.00001)&&(d>2.99999)) {
                    severity = SEVERTIY;
                }
            } else if(arguments[0] instanceof String) {
                String serv = (String) arguments[0];
                if(serv.equals("ERROR")|| serv.equals("INVALID")) {
                    severity = SEVERTIY;
                }
            } else if(arguments[0] instanceof DynamicValueCondition) {
                DynamicValueCondition dvc = (DynamicValueCondition) arguments[0];
                if(dvc.isInvalid()) {
                    severity = SEVERTIY;
                }
            }
        }
        return severity == SEVERTIY;
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
