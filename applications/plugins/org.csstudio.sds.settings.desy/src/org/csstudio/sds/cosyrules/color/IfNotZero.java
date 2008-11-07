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
 * $Id$
 */
package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 27.09.2007
 */
public class IfNotZero implements IRule {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "org.css.sds.color.if_not_zero";

    /**
     * Standard constructor.
     */
    public IfNotZero() {
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(Object[] arguments) {
        if (arguments[0] instanceof Double) {
            Double d = (Double) arguments[0];
            d=d+0.5;
            return d.intValue()==0;
        }else if (arguments[0] instanceof Long) {
            return ((Long)  arguments[0])==0;
        }else if(arguments[0] instanceof String) {
            try{
                Double d = Double.parseDouble((String) arguments[0]);  
                d=d+0.5;
                return d.intValue()==0;
            }catch (NumberFormatException e) {
                return true;
            }
                
        }
        return true;
    }

}
