package org.csstudio.dct.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;

/**
 * Utility class that helps with the resolution of names.
 *
 * @author Sven Wende
 *
 */
public final class ResolutionUtil {
    private ResolutionUtil() {
    }

    /**
     * Resolves all variables and functions in the specified source String.
     *
     * @param source
     *            the source String
     * @param element
     *            the model element
     * @return a resolved String
     *
     * @throws AliasResolutionException
     */
    public static String resolve(String source, IElement element) throws AliasResolutionException {
        Map<String, String> aliases = new HashMap<String, String>();

        if (element instanceof IRecord) {
            IRecord record = (IRecord) element;
            aliases = AliasResolutionUtil.getFinalAliases(record.getContainer());
        } else if (element instanceof IContainer) {
            aliases = AliasResolutionUtil.getFinalAliases((IContainer) element);
        }

        String result = DctActivator.getDefault().getFieldFunctionService().resolve(source, aliases);

        return result;
    }

    public static Map<String, String> resolveFields(IRecord record) {
        Map<String, String> result = new LinkedHashMap<String, String>();

        Map<String, String> aliases = AliasResolutionUtil.getFinalAliases(record.getContainer());

        Map<String, String> fields = record.getFinalFields();
        for(String key : fields.keySet()) {
            String value = fields.get(key);

            String resolved = "";
            try {
                // .. resolve with multiple steps
                resolved = DctActivator.getDefault().getFieldFunctionService().resolve(value, aliases);
                resolved = DctActivator.getDefault().getFieldFunctionService().evaluate(resolved, record, key);
                resolved = DctActivator.getDefault().getFieldFunctionService().resolve(resolved, aliases);
            } catch (Exception e) {
                resolved = "<Error: "+e.getMessage()+">";
            }

            result.put(key, resolved);
        }

        return result;
    }

}
