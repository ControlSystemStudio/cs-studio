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
 * Color rule Set#3, translated from an ADL file.
 *
 * @author jbercic
 *
 */
public final class Set_3 implements IRule {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "cosyrules.color.set#3";

    /**
     * Standard constructor.
     */
    public Set_3() {
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
            if ((d>=-0.01) && (d<=0.01)) {
                return ColorAndFontUtil.toHex(238,182,43);
            }
            if ((d>=0.99) && (d<=1.01)) {
                return ColorAndFontUtil.toHex(225,144,21);
            }
            if ((d>=1.99) && (d<=2.01)) {
                return ColorAndFontUtil.toHex(205,97,0);
            }
            if ((d>=2.99) && (d<=3.01)) {
                return ColorAndFontUtil.toHex(255,176,255);
            }
            if ((d>=3.99) && (d<=4.01)) {
                return ColorAndFontUtil.toHex(214,127,226);
            }
            if ((d>=4.99) && (d<=5.01)) {
                return ColorAndFontUtil.toHex(174,78,188);
            }
            if ((d>=5.99) && (d<=6.01)) {
                return ColorAndFontUtil.toHex(139,26,150);
            }
            if ((d>=6.99) && (d<=7.01)) {
                return ColorAndFontUtil.toHex(97,10,117);
            }
            if ((d>=7.99) && (d<=8.01)) {
                return ColorAndFontUtil.toHex(164,170,255);
            }
            if ((d>=8.99) && (d<=9.01)) {
                return ColorAndFontUtil.toHex(135,147,226);
            }
            if ((d>=9.99) && (d<=10.01)) {
                return ColorAndFontUtil.toHex(106,115,193);
            }
            if ((d>=10.99) && (d<=11.01)) {
                return ColorAndFontUtil.toHex(77,82,164);
            }
            if ((d>=11.99) && (d<=12.01)) {
                return ColorAndFontUtil.toHex(52,51,134);
            }
            if ((d>=12.99) && (d<=13.01)) {
                return ColorAndFontUtil.toHex(199,187,109);
            }
            if ((d>=13.99) && (d<=14.01)) {
                return ColorAndFontUtil.toHex(183,157,92);
            }
            if ((d>=14.99) && (d<=15.01)) {
                return ColorAndFontUtil.toHex(164,126,60);
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
