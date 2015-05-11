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
 package org.csstudio.sds.internal.rules;

import java.util.Arrays;

import org.csstudio.sds.model.IRule;

public class ConcatEnumValuesRule implements IRule {

    public Object evaluate(final Object[] arguments) {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < arguments.length; i++) {
            if(i>0) {
                result.append(" ");
            }
            Object potentialArray = arguments[i];

            if (potentialArray instanceof int[]) {
                result.append(Arrays.toString((int[]) potentialArray));
            } else if (potentialArray instanceof boolean[]) {
                result.append(Arrays.toString((boolean[]) potentialArray));
            } else if (potentialArray instanceof long[]) {
                result.append(Arrays.toString((long[]) potentialArray));
            } else if (potentialArray instanceof byte[]) {
                result.append(Arrays.toString((byte[]) potentialArray));
            } else if (potentialArray instanceof char[]) {
                result.append(Arrays.toString((char[]) potentialArray));
            } else if (potentialArray instanceof double[]) {
                result.append(Arrays.toString((double[]) potentialArray));
            } else if (potentialArray instanceof float[]) {
                result.append(Arrays.toString((float[]) potentialArray));
            } else if (potentialArray instanceof short[]) {
                result.append(Arrays.toString((short[]) potentialArray));
            } else if (potentialArray instanceof Object[]) {
                result.append(Arrays.toString((Object[]) potentialArray));
            } else {
                result.append(potentialArray.toString());
            }
        }
        return result.toString();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "";
    }

}
