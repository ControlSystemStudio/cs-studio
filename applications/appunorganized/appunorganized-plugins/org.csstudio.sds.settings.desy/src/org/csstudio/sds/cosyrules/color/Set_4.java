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

import org.csstudio.sds.model.IRule;
import org.csstudio.sds.util.ColorAndFontUtil;

/**
 * Color rule Set#4, translated from an ADL file.
 *
 * @author jbercic
 *
 */
public final class Set_4 implements IRule {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "cosyrules.color.set#4";

    /**
     * Standard constructor.
     */
    public Set_4() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Object[] arguments) {
        if ((arguments != null) && (arguments.length > 0)) {
            double d = 0.0;
            if (arguments[0] instanceof Double) {
                 d = (Double) arguments[0];
            }else if (arguments[0] instanceof Long) {
                d = ((Long)  arguments[0]).doubleValue();
            }
            if (Math.abs(d-0.00)<0.00001) {
                return ColorAndFontUtil.toHex(0,216,0);
            }
            if (Math.abs(d-1.00)<0.00001) {
                return ColorAndFontUtil.toHex(253,0,0);
            }
            if (Math.abs(d-2.00)<0.00001) {
                return ColorAndFontUtil.toHex(253,0,0);
            }
            if (Math.abs(d-3.00)<0.00001) {
                return ColorAndFontUtil.toHex(255,255,255);
            }
            if (Math.abs(d-4.00)<0.00001) {
                return ColorAndFontUtil.toHex(255,176,255);
            }
            if (Math.abs(d-5.00)<0.00001) {
                return ColorAndFontUtil.toHex(235,241,181);
            }
            if (Math.abs(d-6.00)<0.00001) {
                return ColorAndFontUtil.toHex(164,170,255);
            }
            if (Math.abs(d-7.00)<0.00001) {
                return ColorAndFontUtil.toHex(164,170,255);
            }
            if (Math.abs(d-8.00)<0.00001) {
                return ColorAndFontUtil.toHex(251,243,74);
            }
            if (Math.abs(d-9.00)<0.00001) {
                return ColorAndFontUtil.toHex(238,182,43);
            }
            if (Math.abs(d-10.00)<0.00001) {
                return ColorAndFontUtil.toHex(251,243,74);
            }
            if (Math.abs(d-11.00)<0.00001) {
                return ColorAndFontUtil.toHex(238,182,43);
            }
            if (Math.abs(d-12.00)<0.00001) {
                return ColorAndFontUtil.toHex(251,243,74);
            }
            if (Math.abs(d-13.00)<0.00001) {
                return ColorAndFontUtil.toHex(238,182,43);
            }
            if (Math.abs(d-14.00)<0.00001) {
                return ColorAndFontUtil.toHex(251,243,74);
            }
            if (Math.abs(d-15.00)<0.00001) {
                return ColorAndFontUtil.toHex(238,182,43);
            }
        }

        return ColorAndFontUtil.toHex(0,0,0);
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
