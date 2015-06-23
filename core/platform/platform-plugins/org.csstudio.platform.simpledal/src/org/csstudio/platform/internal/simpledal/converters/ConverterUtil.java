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
 package org.csstudio.platform.internal.simpledal.converters;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.model.pvs.ValueType;

public class ConverterUtil {
    private static Map<ValueType, IValueTypeConverter> converters;

    static {
        converters = new HashMap<ValueType, IValueTypeConverter>();
        converters.put(ValueType.DOUBLE, new DoubleConverter());
        converters.put(ValueType.LONG, new LongConverter());
        converters.put(ValueType.STRING, new StringConverter());
        converters.put(ValueType.OBJECT, new ObjectConverter());
        converters
                .put(ValueType.DOUBLE_SEQUENCE, new DoubleSequenceConverter());
        converters.put(ValueType.LONG_SEQUENCE, new LongSequenceConverter());
        converters
                .put(ValueType.STRING_SEQUENCE, new StringSequenceConverter());
        converters
                .put(ValueType.OBJECT_SEQUENCE, new ObjectSequenceConverter());
        converters
        .put(ValueType.ENUM, new ObjectConverter());

    }

    public static <E> E convert(Object value, ValueType valueType) {
        IValueTypeConverter converter = converters.get(valueType);
        assert converter != null : "Converter for type ["+valueType + "] is missing!";

        // converters do always return a value (even a fall back for a provided null value)
        E result = (E) converter.convert(value);

        return result;
    }
}
