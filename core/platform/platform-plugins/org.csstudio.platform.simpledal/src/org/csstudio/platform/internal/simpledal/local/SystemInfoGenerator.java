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
 package org.csstudio.platform.internal.simpledal.local;

import java.lang.reflect.Method;

public class SystemInfoGenerator extends AbstractDataGenerator<String> {
    private static final Class INFO_CLASS = Environment.class;

    private static final String METHOD_PREFIX = "get";

    private Method _staticMethod;

    public SystemInfoGenerator(LocalChannel localChannel, int defaultPeriod,
            String[] options) {
        super(localChannel, defaultPeriod, options);

        assert options.length == 3 : "options.length==3";

        if (options[2] != null) {
            try {
                int period = Integer.valueOf(options[2]);
                setPeriod(period);
            } catch (NumberFormatException e) {
                assert false : "Should be ensured by regular expression.";
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init(String[] options) {
        String methodName = options[0];

        try {
            _staticMethod = INFO_CLASS.getMethod(METHOD_PREFIX + methodName,
                    null);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            int period = Integer.parseInt(options[1]);
            setPeriod(period);
        } catch (NumberFormatException nfe) {
            // ignore
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String generateNextValue() {
        Object result = "";

        if (_staticMethod != null) {
            try {
                result = _staticMethod.invoke(null, null);
            } catch (Exception e) {
                result = e.getMessage();
            }
        }

        return result.toString();
    }

}
