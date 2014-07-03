/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import java.util.List;
import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;

/**
 *
 * @author carcassi
 */
public class ArgumentExpressions {
    public static ReadFunctionArgument<String> stringArgument(ReadFunction<?> function, String argumentName) {
        if (function == null) {
            return new ReadFunctionArgument<>();
        } else {
            return new ReadFunctionArgument<>(new VStringToStringReadFunction(new CheckedReadFunction<>(function, argumentName, VString.class)));
        }
    }
    
    public static ReadFunctionArgument<List<String>> stringArrayArgument(ReadFunction<?> function, String argumentName) {
        if (function == null) {
            return new ReadFunctionArgument<>();
        } else {
            return new ReadFunctionArgument<>(new VStringArrayToListStringReadFunction(new CheckedReadFunction<>(function, argumentName, VStringArray.class)));
        }
    }
}
