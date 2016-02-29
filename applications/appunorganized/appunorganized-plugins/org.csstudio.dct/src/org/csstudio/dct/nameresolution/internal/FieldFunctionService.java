package org.csstudio.dct.nameresolution.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.FieldFunctionExtension;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.dct.nameresolution.IFieldFunctionService;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.domain.common.strings.StringUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Default implementation of {@link IFieldFunctionService}.
 *
 * @author Sven Wende
 *
 */
public final class FieldFunctionService implements IFieldFunctionService {

    private static final Pattern FIND_FUNCTION_PATTERN = Pattern.compile("^>([a-z]+)\\((.*)\\)$");

    /**
     * Regular expression which is used to find alias names in an arbitrary
     * string.
     */
    private static final Pattern FIND_VARIABLES_PATTERN = Pattern.compile("\\$\\(([^$()]*)\\)");

    private Map<String, IFieldFunction> functions;

    /**
     * Constructor.
     */
    public FieldFunctionService() {
        functions = new HashMap<String, IFieldFunction>();

        List<FieldFunctionExtension> extensions = lookupExtensions();

        for (FieldFunctionExtension extension : extensions) {
            registerFunction(extension.getName(), extension.getFunction());
        }

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String resolve(String source, Map<String, String> vars) throws AliasResolutionException {
        if (!StringUtil.hasLength(source)) {
            return source;
        } else {
            return resolveVariables(source, vars);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Set<String> findRequiredVariables(final String source) {
        Set<String> result = new HashSet<String>();

        if (StringUtil.hasLength(source)) {
            // Get a Matcher based on the target string.
            Matcher matcher = FIND_VARIABLES_PATTERN.matcher(source);

            // Find all the matches.
            while (matcher.find()) {
                String name = matcher.group(1);
                result.add(name);
            }
        }
        return result;
    }

    /**
     * Resolves all variables in a source String.
     *
     * @param source
     *            the source String
     * @param aliases
     *            a map, which contains values for variables
     * @throws AliasResolutionException
     *
     * @return the resolved String in which all variables are replaced by their
     *         values
     */
    public String resolveVariables(final String source, final Map<String, String> aliases) throws AliasResolutionException {
        return doResolveVariablesRecursively(source, new ArrayList<String>(), false, aliases);
    }

    /**
     * This method is called recursively, to apply alias substitutions to the
     * provided input text.
     *
     * @param input
     *            the text input
     * @param markerList
     *            a marker list, which contains aliases that are already on the
     *            stack
     * @param isAlias
     *            flag, which indicates whether the input is a alias (this is
     *            necessary because aliases might contain other aliases which is
     *            handled via the same recursive call)
     * @param aliases
     *            the existing aliases as provided by the user
     *
     * @return a canonical name in which all aliases are replaced by their real
     *         values
     *
     * @throws AliasResolutionException
     */
    private static String doResolveVariablesRecursively(final String input, final List<String> markerList, final boolean isAlias,
            final Map<String, String> aliases) throws AliasResolutionException {

        String result = "";

        // .. special treatment if we are resolving an alias
        if (isAlias) {
            if (!aliases.containsKey(input)) {
                throw new AliasResolutionException("Variable \"" + input + "\" cannot be resolved.");
            } else {
                if (markerList.contains(input)) {
                    throw new AliasResolutionException("Variable \"" + input + "\" contains an endless loop.");
                } else {
                    result = aliases.get(input);
                    markerList.add(input);
                }
            }
        } else {
            result = input;
        }

        // .. find all variables that needs to be replaced by real values
        if (result != null) {
            Matcher matcher = FIND_VARIABLES_PATTERN.matcher(result);

            while (matcher.find()) {
                String alias = matcher.group(1);

                // .. recursively resolve the alias itself
                String resolvedAlias = doResolveVariablesRecursively(alias, markerList, true, aliases);
                assert resolvedAlias != null;
                markerList.clear();

                // .. replace all occurences of the alias in the initial string
                Matcher matcher2 = createSearchPattern(alias).matcher(result);

                // .. the resolved aliases may contain dollar signs ($) which
                // need to be quoted before they can be used as replacement
                // construct for java regular expressions
                result = matcher2.replaceAll(quoteDollarSigns(resolvedAlias));

                matcher = FIND_VARIABLES_PATTERN.matcher(result);
            }
        }

        return result != null ? result : "";
    }

    private static String quoteDollarSigns(String unquoted) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < unquoted.length(); i++) {
            String t = unquoted.substring(i, i + 1);

            if ("$".equals(t)) {
                sb.append("\\");
            }

            sb.append(t);
        }

        return sb.toString();
    }

    /**
     * Generates a regular expression which is used to replace aliases in
     * arbitrary texts.
     *
     * @param aliasName
     *            the alias name, which should be found (without the bordering
     *            "$" signs
     *
     * @return a regular expression which is used to replace aliases in
     *         arbitrary texts
     *
     */
    private static Pattern createSearchPattern(final String aliasName) {
        return Pattern.compile("(\\$\\(" + aliasName + "\\))");
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String evaluate(String source, IRecord record, String fieldName) throws Exception {
        return findAndApplyFunctions(source, record, fieldName);
    }

    /**
     * Registers a field function.
     *
     * @param name
     *            the function name
     * @param function
     *            the function
     */
    public void registerFunction(String name, IFieldFunction function) {
        assert name != null;
        assert function != null;

        if (functions.containsKey(name)) {
            throw new IllegalArgumentException("Only 1 function can be registered for name \"" + name + "\"");
        }

        functions.put(name, function);
    }

    /**
     * Applies all function in the specified source String recursively.
     *
     * @param source
     *            the source String
     * @param record
     *            the record which contains the field with this function
     * @param fieldName
     *            the name of the field that contains this function
     *
     * @return the evaluated target String
     *
     * @throws Exception
     */
    private String findAndApplyFunctions(String source, IRecord record, String fieldName) throws Exception {
        String result = source;

        if (source != null) {
            // Get a Matcher based on the target string.
            Matcher matcher = FIND_FUNCTION_PATTERN.matcher(source);

            // Find all the matches.
            if (matcher.find()) {
                String name = matcher.group(1);
                String[] params = matcher.group(2).split(",");
                for (int i = 0; i < params.length; i++) {
                    params[i] = params[i].trim();
                }

                result = applyFunction(name, params, record, fieldName);
            }
        }

        return result;
    }

    /**
     * Resolves a function to a plain String.
     *
     * @param functionName
     *            the name of the function
     * @param parameters
     *            the function parameters
     * @param record
     *            the record which contains the field with this function
     * @param fieldName
     *            the name of the field that contains this function
     *
     * @return a plain String with the evaluation result of the function
     *
     * @throws Exception
     */
    private String applyFunction(String functionName, String[] parameters, IRecord record, String fieldName) throws Exception {
        assert functionName != null;
        assert parameters != null;
        assert record != null;
        assert fieldName != null;

        if (functions.containsKey(functionName)) {
            IFieldFunction function = functions.get(functionName);
            assert function != null;
            return function.evaluate(functionName, parameters, record, fieldName);
        } else {
            throw new Exception("Function " + functionName + "() unknown.");
        }
    }

    private static List<FieldFunctionExtension> lookupExtensions() {
        List<FieldFunctionExtension> extensions = new ArrayList<FieldFunctionExtension>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(DctActivator.EXTPOINT_FIELDFUNCTIONS);

        for (IConfigurationElement c : configurationElements) {
            String name = c.getAttribute("name"); //$NON-NLS-1$
            String description = c.getAttribute("description"); //$NON-NLS-1$
            String signature = c.getAttribute("signature"); //$NON-NLS-1$
            IFieldFunction function = null;
            try {
                function = (IFieldFunction) c.createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                e.printStackTrace();
            }

            FieldFunctionExtension extension = new FieldFunctionExtension();
            extension.setName(name);
            extension.setFunction(function);
            extension.setDescription(description);
            extension.setSignature(signature);
            extensions.add(extension);

        }

        return extensions;

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public List<FieldFunctionExtension> getFieldFunctionExtensions() {
        return lookupExtensions();
    }
}
