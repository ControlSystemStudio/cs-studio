/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.csstudio.sds.ErrorMessagesTracker;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.logic.ScriptedRule;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.model.IRule;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.mozilla.javascript.RhinoException;

/**
 * This class provides access to all available rules.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class RuleService extends ErrorMessagesTracker {
    /**
     * The workspace project that holds all the rules.
     */
    public static final String SCRIPT_PROJECT_NAME = "SDS Script Rules"; //$NON-NLS-1$

    /**
     * The file extension of SDS scripts.
     */
    public static final String SCRIPT_FILE_EXTENSION = "css-sdss"; //$NON-NLS-1$

    /**
     * The shared instance of this class.
     */
    private static RuleService _instance = null;

    /**
     * All registered Java rules that are registered via an extension point.
     */
    private Map<String, RuleDescriptor> _javaRuleDescriptors = null;

    /**
     * All registered script rules.
     */
    private Map<String, RuleDescriptor> _scriptRuleDescriptors = null;

    private String defaultAdditionalScriptFolder = "CSS/SDS/SDS_Script_Rules";

    /**
     * Private constructor due to the singleton pattern.
     */
    private RuleService() {
        _javaRuleDescriptors = Collections
                .synchronizedMap(new HashMap<String, RuleDescriptor>());
        _scriptRuleDescriptors = Collections
                .synchronizedMap(new HashMap<String, RuleDescriptor>());

        lookupExtensions();
        lookupScripts();
    }

    /**
     * Return the shared instance of this class.
     *
     * @return The shared instance of this class.
     */
    public static RuleService getInstance() {
        if (_instance == null) {
            _instance = new RuleService();
        }

        return _instance;
    }

    /**
     * Return the rule descriptor for the given rulee ID.
     *
     * @param ruleId
     *            A rule ID.
     * @return The rule descriptor for the given type ID.
     */
    public RuleDescriptor getRuleDescriptor(final String ruleId) {
        if (_javaRuleDescriptors.containsKey(ruleId)) {
            return _javaRuleDescriptors.get(ruleId);
        }
        return _scriptRuleDescriptors.get(ruleId);
    }

    /**
     * Perform a lookup for plugins that provide extensions for the
     * <code>rules</code> extension point.
     */
    private void lookupExtensions() {
        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        String id = SdsPlugin.EXTPOINT_RULES;
        IConfigurationElement[] confElements = extReg
                .getConfigurationElementsFor(id);

        for (IConfigurationElement element : confElements) {
            IRule type = null;
            String ruleId = element.getAttribute("ruleId"); //$NON-NLS-1$
            String description = element.getAttribute("name"); //$NON-NLS-1$

            String[] parameterDescriptions = null;

            IConfigurationElement[] parameterElements = element
                    .getChildren("parameterDescription"); //$NON-NLS-1$

            if (parameterElements != null) {
                parameterDescriptions = new String[parameterElements.length];
                int i = 0;
                for (IConfigurationElement parameterElement : parameterElements) {
                    String parameterDescription = parameterElement
                            .getAttribute("name"); //$NON-NLS-1$
                    parameterDescriptions[i] = parameterDescription;
                    i++;
                }
            }

            try {
                type = (IRule) element.createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                trackException(e);
            }

            PropertyTypesEnum[] compatiblePropertyIds = getPropertyTypes(element
                    .getAttribute("compatibleProperties"));
            if (type != null && ruleId != null) {
                RuleDescriptor descriptor = new RuleDescriptor(ruleId,
                        description, parameterDescriptions,
                        compatiblePropertyIds, type, false);
                _javaRuleDescriptors.put(ruleId, descriptor);
            }
        }
    }

    private PropertyTypesEnum[] getPropertyTypes(String attribute) {
        List<PropertyTypesEnum> result = new LinkedList<PropertyTypesEnum>();
        if (attribute != null) {
            if (attribute.contains("*")) {
                return PropertyTypesEnum.values();
            }
            String[] ids = attribute.split(",");
            for (String id : ids) {
                try {
                    result.add(PropertyTypesEnum.createFromPortable(id.trim()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toArray(new PropertyTypesEnum[result.size()]);
    }

    /**
     * Register the scripted rules.
     */
    private void lookupScripts() {
        IProject scriptProject = ResourcesPlugin.getWorkspace().getRoot()
                .getProject(SCRIPT_PROJECT_NAME);

        try {
            IResource[] resources = scriptProject.members();
            findFiles(resources);
        } catch (Exception ex) {
            trackException(ex);
        }
        this.lookAtAdditionalLocations();
    }

    private void lookAtAdditionalLocations() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (String folder : determineAdditionalFolders()) {
            IPath path = new Path(folder);
            IFolder folder2 = root.getFolder(path);
            if (folder2.exists()) {
                try {
                    this.findFiles(folder2.members());
                } catch (CoreException e) {
                    // nothing to do
                    trackException(e);
                }
            }
        }
    }

    private List<String> determineAdditionalFolders() {
        String stringList = Platform.getPreferencesService().getString(
                SdsPlugin.getDefault().getBundle().getSymbolicName(),
                PreferenceConstants.PROP_RULE_FOLDERS, "", null);
        List<String> result = parseString(stringList);
        result.add(defaultAdditionalScriptFolder);
        return result;
    }

    /*
     * (non-Javadoc) Method declared on ListEditor.
     */
    private List<String> parseString(String stringList) {
        StringTokenizer st = new StringTokenizer(stringList, File.pathSeparator
                + "\n\r");//$NON-NLS-1$
        List<String> result = new ArrayList<String>();
        while (st.hasMoreElements()) {
            result.add((String) st.nextElement());
        }
        return result;
    }

    private void findFiles(IResource[] resources) throws CoreException {
        for (IResource resource : resources) {
            if (resource instanceof IFile
                    && SCRIPT_FILE_EXTENSION.equalsIgnoreCase(resource
                            .getFileExtension())) {

                IFile scriptFile = (IFile) resource;

                updateScriptedRule(scriptFile.getName(), scriptFile);
            } else if (resource instanceof IFolder) {
                findFiles(((IFolder) resource).members());
            }
        }
    }

    /**
     * Update the definition of a scripted rule.
     *
     * @param ruleId
     *            The ID of the rule.
     * @param scriptFile
     *            The file resource that contains a script rule or
     *            <code>null</code> to remove the rule definition.
     */
    public void updateScriptedRule(final String ruleId, final IFile scriptFile) {
        try {
            if (_scriptRuleDescriptors.containsKey(ruleId)) {
                _scriptRuleDescriptors.remove(ruleId);
            }

            if (scriptFile != null) {
                ScriptedRule type = new ScriptedRule(ruleId, scriptFile
                        .getContents());

                RuleDescriptor typeDescriptor = new RuleDescriptor(ruleId, type
                        .getDescription(), type.getParameterDescriptions(),
                        type.getCompatiblePropertyTypes(), type, true);

                _scriptRuleDescriptors.put(ruleId, typeDescriptor);
            }
        } catch (Exception ex) {
            trackException(ex);
        }
    }

    /**
     * Track the given exception.
     *
     * @param e
     *            The exception to track.
     */
    private void trackException(final Exception e) {
        Throwable cause = e.getCause();

        String errorMessage = e.getMessage();

        if (cause != null) {
            if (cause instanceof RhinoException) {
                RhinoException re = (RhinoException) cause;
                errorMessage += "\n    The cause was [" + re.getMessage() + "]."; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        trackErrorMessage(errorMessage);
    }

    /**
     * @return A list which contains all rule descriptors.
     */
    public List<RuleDescriptor> getRegisteredRuleDescriptors() {
        lookupScripts();

        ArrayList<RuleDescriptor> result = new ArrayList<RuleDescriptor>();

        result.addAll(_javaRuleDescriptors.values());
        result.addAll(_scriptRuleDescriptors.values());

        return result;
    }

}
