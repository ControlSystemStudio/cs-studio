package org.csstudio.pvmanager.formula.channelfinder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.diirt.datasource.formula.StatefulFormulaFunction;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceRegistry;
import org.diirt.vtype.VString;
import org.diirt.vtype.VTable;

/**
 *
 * @author Kunal Shroff
 *
 */
public class CFQueryFunction extends StatefulFormulaFunction {

    private VString currentQuery;
    private volatile VTable currentResult;
    private volatile Exception currentException;

    private ServiceMethod serviceMethod;

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public String getName() {
        return "cfQuery";
    }

    @Override
    public String getDescription() {
        return "Query ChannelFinder";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VString.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("query");
    }

    @Override
    public Class<?> getReturnType() {
        return VTable.class;
    }

    @Override
    public Object calculate(List<Object> args) {
        if (currentQuery == null || !((VString) args.get(0)).getValue().equals(currentQuery.getValue())) {
            currentQuery = (VString) args.get(0);
            serviceMethod = ServiceRegistry.getDefault().findServiceMethod("cf/find");
            serviceMethod.executeAsync(Collections.<String, Object>singletonMap("query", currentQuery), new Consumer<Map<String, Object>>(){

                @Override
                public void accept(Map<String, Object> newValue) {
                    currentResult = (VTable) newValue.get("result");
                    currentException = null;
                }

            } , new Consumer<Exception>(){

                @Override
                public void accept(Exception newValue) {
                    currentException = newValue;
                    currentResult = null;
                }

            });
        }
        return currentResult;
    }

}
