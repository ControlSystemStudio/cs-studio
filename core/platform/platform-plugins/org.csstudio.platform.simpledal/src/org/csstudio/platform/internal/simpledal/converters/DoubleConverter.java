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
 /**
 *
 */
package org.csstudio.platform.internal.simpledal.converters;


/**
 * Converter for double values.
 *
 * @author Sven Wende
 *
 */
class DoubleConverter implements IValueTypeConverter<Double> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Double convert(Object value) throws NumberFormatException {
        Double result = 0.0;
//        System.out.println("Converter"+value);
        if (value != null) {
            if (value instanceof Double) {
                result = (Double) value;
            } else if (value instanceof Number) {
                Number n = (Number) value;
                result = n.doubleValue();
            } else {
                try {
                    result = Double.valueOf(value.toString());
                } catch (NumberFormatException e) {
                    if(value.toString().matches("[-+]?0[xX].*")) {
                        // if Hex
                        result = new Double(Long.parseLong(value.toString().replaceFirst("(\\+)?0[xX]", ""),16));
                    }else {
                        // if Exp
                        result = new Double(Long.parseLong(value.toString()));
                    }
                }
            }
        }

        assert result != null;
        return result;
    }
}