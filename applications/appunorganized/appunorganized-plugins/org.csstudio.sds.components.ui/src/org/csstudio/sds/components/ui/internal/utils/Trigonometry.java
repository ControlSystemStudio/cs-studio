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
 package org.csstudio.sds.components.ui.internal.utils;

/**
 * A utility class for drawing.
 * This one contains various trigonometric functions.
 *
 * @author jbercic
 *
 */
public final class Trigonometry {

    private static double [] cos_array;
    private static double [] sin_array;

    /**
     * Initializes the lookup tables for cosine and sine functions.
     */
    static {
        int i;
        double trnt=0.0;

        cos_array=new double[36000];
        sin_array=new double[36000];

        for (i=0;i<36000;i++) {
            cos_array[i]=Math.cos(Math.toRadians(trnt));
            sin_array[i]=Math.sin(Math.toRadians(trnt));
            trnt+=0.01;
        }
    }

    public static double cos(double angle) {
        if (angle<0) {
            return cos_array[((int)(-angle*100))%36000];
        }
        return cos_array[((int)(angle*100))%36000];
    }

    public static double sin(double angle) {
        if (angle<0) {
            return -sin_array[((int)(-angle*100))%36000];
        }
        return sin_array[((int)(angle*100))%36000];
    }
}
