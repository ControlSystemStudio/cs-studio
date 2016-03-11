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
 * Color rule Sy-status, translated from an ADL file.
 *
 * @author jbercic
 *
 */
public final class Sy_status implements IRule {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "cosyrules.color.sy-status";

    /**
     * Standard constructor.
     */
    public Sy_status() {
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
            if (Math.abs(d-0.0)<0.00001) {
                return ColorAndFontUtil.toHex(78,165,249);
            }
            if (Math.abs(d-1.0)<0.00001) {
                return ColorAndFontUtil.toHex(45,127,0);
            }
            if (Math.abs(d-2.0)<0.00001) {
                return ColorAndFontUtil.toHex(225,144,21);
            }
            if (Math.abs(d-3.0)<0.00001) {
                return ColorAndFontUtil.toHex(190,25,11);
            }
            if (Math.abs(d-4.0)<0.00001) {
                return ColorAndFontUtil.toHex(251,243,74);
            }
            if (Math.abs(d-5.0)<0.00001) {
                return ColorAndFontUtil.toHex(253,0,0);
            }
            if (Math.abs(d-6.0)<0.00001) {
                return ColorAndFontUtil.toHex(115,255,107);
            }
            if (Math.abs(d-7.0)<0.00001) {
                return ColorAndFontUtil.toHex(51,153,0);
            }
            if (Math.abs(d-8.0)<0.00001) {
                return ColorAndFontUtil.toHex(26,115,9);
            }
            if (Math.abs(d-9.0)<0.00001) {
                return ColorAndFontUtil.toHex(145,145,145);
            }
            if (Math.abs(d-10.0)<0.00001) {
                return ColorAndFontUtil.toHex(235,241,181);
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
