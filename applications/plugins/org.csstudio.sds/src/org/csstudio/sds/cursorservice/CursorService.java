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
package org.csstudio.sds.cursorservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for using mouse cursors in SDS displays.
 *
 * @author Sven Wende, Joerg Rathlev
 */
public final class CursorService implements ICursorService {
    /**
     * The supported file extensions for workspace cursors.
     */
    private static final Set<String> SUPPORTED_FILE_EXTENSIONS;

    /**
     * The default cursor selection rule.
     */
    private static final CursorSelectionRule DEFAULT_RULE = new SystemCursorOnlyRule();

    /**
     * Descriptor for the default rule.
     */
    private static final RuleDescriptor DEFAULT_RULE_DESCRIPTOR = new RuleDescriptor(
            DEFAULT_RULE_ID, "System cursor only",
            Collections.<CursorState>emptyList(), null);

    static {
        SUPPORTED_FILE_EXTENSIONS = new HashSet<String>();
        SUPPORTED_FILE_EXTENSIONS.add("gif");
        SUPPORTED_FILE_EXTENSIONS.add("jpg");
        SUPPORTED_FILE_EXTENSIONS.add("png");
    }

    /**
     * The singleton instance.
     */
    private static ICursorService _instance;

    /**
     * The cursor descriptors.
     */
    private List<AbstractCursor> _cursors;

    /**
     * The cursor strategy descriptors.
     */
    private List<RuleDescriptor> _ruleDescriptors;

    /**
     * The current cursor preferences.
     */
    private CursorSettings _preferences;

    private static final Logger LOG = LoggerFactory.getLogger(CursorService.class);

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static synchronized ICursorService getInstance() {
        if (_instance == null) {
            _instance = new CursorService();
        }

        return _instance;
    }

    /**
     * Constructor.
     */
    private CursorService() {
        _cursors = new ArrayList<AbstractCursor>();
        addSystemCursors();
        addExtensionCursors();

        _ruleDescriptors = new ArrayList<RuleDescriptor>();
        addDefaultCursorSelectionRule();
        addExtensionCursorSelectionRules();

        loadPreferences();
    }

    /**
     * Returns a list of the available cursors.
     *
     * @return a list of the available cursors.
     */
    public List<AbstractCursor> availableCursors() {
        List<AbstractCursor> result = new ArrayList<AbstractCursor>();
        result.addAll(_cursors);
        result.addAll(getWorkSpaceCursors());
        return result;
    }

    /**
     * Returns an unmodifiable list of the available cursor selection rules.
     *
     * @return an unmodifiable list of rule descriptors for the available cursor
     *         selection rules.
     */
    public List<RuleDescriptor> availableRules() {
        return Collections.unmodifiableList(_ruleDescriptors);
    }

    /**
     * Applies the cursor to the given widget based on the selection rule
     * configured by the user.
     *
     * @param widget
     *            the widget.
     */
    public void applyCursor(final AbstractWidgetModel widget) {
        RuleDescriptor ruleDesc = getPreferredRule();
        CursorSelectionRule rule = executableRule(ruleDesc);
        String stateId = rule.determineState(widget);
        String cursorId = cursorIdFromState(ruleDesc, stateId);
        if (cursorId != null) {
            widget.setCursorId(cursorId);
        }
    }

    /**
     * Returns the executable rule described by the specified descriptor.
     *
     * @param ruleDesc
     *            the rule descriptor.
     * @return the executable rule. If an executable rule for the specified
     *         descriptor cannot be created, this method returns the default
     *         rule.
     */
    private CursorSelectionRule executableRule(final RuleDescriptor ruleDesc) {
        IConfigurationElement config = ruleDesc.configurationElement();
        CursorSelectionRule rule = null;
        if (config != null && config.isValid()) {
            try {
                rule = (CursorSelectionRule) config.createExecutableExtension("class");
            } catch (CoreException e) {
                rule = null;
            }
        }
        if (rule == null) {
            rule = DEFAULT_RULE;
        }
        return rule;
    }

    /**
     * Determines the cursor id from the given cursor state.
     *
     * @param rule
     *            the cursor selection rule.
     * @param stateId
     *            the ID of the state.
     * @return the cursor id.
     */
    private String cursorIdFromState(final RuleDescriptor rule,
            final String stateId) {
        String cursorId = null;
        if (stateId != null) {
            CursorState state = rule.state(stateId);
            AbstractCursor cursor = _preferences.getCursor(rule, state);
            if (cursor == null) {
                cursor = ICursorService.SYSTEM_DEFAULT_CURSOR;
            }
            cursorId = cursor.getIdentifier();
        }
        return cursorId;
    }

    /**
     * Returns the preferred rule.
     *
     * @return a descriptor of the preferred rule.
     */
    public RuleDescriptor getPreferredRule() {
        RuleDescriptor result = null;
        String id = Platform.getPreferencesService().getString(
                SdsPlugin.getDefault().getBundle().getSymbolicName(),
                PreferenceConstants.CURSOR_SELECTION_RULE, DEFAULT_RULE_ID,
                null);
        if (id != null) {
            result = findRuleDescriptor(id);
        }
        return result;
    }

    /**
     * Sets the preferred cursor selection rule. The preferred rule is stored in
     * the preference store.
     *
     * @param rule
     *            the preferred rule.
     */
    public void setPreferredRule(final RuleDescriptor rule) {
        IEclipsePreferences node = new InstanceScope().getNode(SdsPlugin.PLUGIN_ID);
        node.put(PreferenceConstants.CURSOR_SELECTION_RULE, rule.getId());
    }

    /**
     * Finds the rule with the specified id.
     *
     * @param id
     *            the id
     * @return the rule descriptor. If the rule was not found, returns the
     *         descriptor of the default rule.
     */
    private RuleDescriptor findRuleDescriptor(final String id) {
        assert id != null;

        RuleDescriptor result = null;
        for (RuleDescriptor d : _ruleDescriptors) {
            if (d.getId().equalsIgnoreCase(id)) {
                result = d;
            }
        }
        if (result == null) {
            result = defaultRuleDescriptor();
        }
        return result;
    }

    /**
     * Returns a descriptor for the default cursor selection rule.
     *
     * @return a descriptor for the default cursor selection rule.
     */
    private RuleDescriptor defaultRuleDescriptor() {
        return DEFAULT_RULE_DESCRIPTOR;
    }

    /**
     * Find the cursor with the specified id.
     *
     * @param id
     *            the id.
     * @return a cursor, or <code>null</code> if none was found for that id.
     */
    public AbstractCursor findCursor(final String id) {
        assert id != null;

        AbstractCursor result = null;

        for (AbstractCursor d : availableCursors()) {
            if (d.getIdentifier().equalsIgnoreCase(id)) {
                result = d;
            }
        }

        return result;
    }


    /**
     * Returns the current cursor preferences. Changes in the returned object
     * are not immediately reflected in the preference store. Use the
     * {@link #setPreferences(CursorSettings)} method to change the stored
     * preferences.
     *
     * @return the current cursor settings from the preferences.
     */
    public CursorSettings getPreferences() {
        return new CursorSettings(_preferences);
    }

    /**
     * Sets the cursor preferences to the specified settings.
     *
     * @param settings
     *            the settings.
     */
    public void setPreferences(final CursorSettings settings) {
        if (settings == null) {
            throw new NullPointerException();
        }

        _preferences = new CursorSettings(settings);
        storePreferences();
    }

    /**
     * Loads the cursor settings from the preference store.
     */
    private void loadPreferences() {
        _preferences = new CursorSettings(_ruleDescriptors);

        /*
         * The structure of the preferences: there is a node for the cursor
         * preferences. Below that node, there is one node for each cursor
         * selection rule, and below that node, one preference for each cursor
         * state.
         *
         * Example:
         *
         * <SdsPlugin.PLUGIN_ID>
         *   + <PreferenceConstants.CURSOR_SETTINGS>
         *       + de.desy.DesyCursorStrategy
         *       |   + default
         *       |   + disabled
         *       |   + enabled
         *       |   + ...
         *       + org.example.OtherCursorStrategy
         *           + enabled
         *           + disabled
         */
        Preferences cursorSettings = new InstanceScope().
                getNode(SdsPlugin.PLUGIN_ID).
                node(PreferenceConstants.CURSOR_SETTINGS);
        for (RuleDescriptor rule : _ruleDescriptors) {
            try {
                if (cursorSettings.nodeExists(rule.getId())) {
                    Preferences ruleNode = cursorSettings.node(rule.getId());
                    for (String stateId : ruleNode.keys()) {
                        CursorState state = rule.state(stateId);
                        String cursorId = ruleNode.get(stateId, null);
                        if (state != null && cursorId != null) {
                            AbstractCursor cursor = findCursor(cursorId);
                            if (cursor != null) {
                                _preferences.setCursor(rule, state, cursor);
                            }
                        }
                    }
                }
            } catch (BackingStoreException e) {
                LOG.warn("BackingStoreException while reading preferences", e);
            }
        }
    }

    /**
     * Stores the cursor settings in the preference store.
     */
    private void storePreferences() {
        Preferences cursorSettings = new InstanceScope().
                getNode(SdsPlugin.PLUGIN_ID).
                node(PreferenceConstants.CURSOR_SETTINGS);
        for (RuleDescriptor rule : _ruleDescriptors) {
            Preferences ruleNode = cursorSettings.node(rule.getId());
            for (CursorState state : rule.cursorStates()) {
                AbstractCursor cursor = _preferences.getCursor(rule, state);
                if (cursor == null) {
                    cursor = ICursorService.SYSTEM_DEFAULT_CURSOR;
                }
                ruleNode.put(state.getId(),
                        cursor.getIdentifier());
            }
        }
    }

    /**
     * Adds the cursors contributed via the {@code cursors} extension point to
     * the list of available cursors.
     */
    private void addExtensionCursors() {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] cursorConfigurationElements =
            extensionRegistry.getConfigurationElementsFor(SdsPlugin.EXPOINT_CURSORS);
        for (IConfigurationElement element : cursorConfigurationElements) {
            String bundle = element.getContributor().getName();
            String id = element.getAttribute("id");
            String name = element.getAttribute("name");
            String image = element.getAttribute("image");
            _cursors.add(new ContributedCursor(id, name, bundle, image));
        }
    }

    /**
     * Adds the cursor selection rules contributed via the
     * {@code cursorSelectionRules} extension point to the list of available
     * cursor selection rules.
     */
    private void addExtensionCursorSelectionRules() {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] ruleConfigurationElements =
            extensionRegistry.getConfigurationElementsFor(SdsPlugin.EXTPOINT_CURSOR_SELECTION_RULES);
        for (IConfigurationElement element : ruleConfigurationElements) {
            String id = element.getAttribute("id");
            String label = element.getAttribute("label");
            if (label == null) {
                label = id;
            }
            Collection<CursorState> states = readCursorStates(element);
            RuleDescriptor rule = new RuleDescriptor(id, label, states, element);
            _ruleDescriptors.add(rule);
        }
    }

    /**
     * Reads the cursor states of a rule from the extension registry.
     *
     * @param rule
     *            the configuration element which declares the rule.
     * @return a collection of the cursor states declared for the rule.
     */
    private Collection<CursorState> readCursorStates(final IConfigurationElement rule) {
        Collection<CursorState> result = new ArrayList<CursorState>();
        IConfigurationElement[] stateConfigs = rule.getChildren("state");
        for (IConfigurationElement stateElement : stateConfigs) {
            String stateId = stateElement.getAttribute("id");
            String stateLabel = stateElement.getAttribute("label");
            if (stateLabel == null) {
                stateLabel = stateId;
            }
            CursorState state = new CursorState(stateId, stateLabel);
            result.add(state);
        }
        return result;
    }

    /**
     * Adds the default cursor selection rule to the list of available rules.
     */
    private void addDefaultCursorSelectionRule() {
        _ruleDescriptors.add(DEFAULT_RULE_DESCRIPTOR);
    }

    /**
     * Adds the system cursors to the list of available cursors.
     */
    private void addSystemCursors() {
        _cursors.add(SYSTEM_DEFAULT_CURSOR);
        _cursors.add(new SWTCursor("cursor.system.arrow",
                "Arrow (System)", SWT.CURSOR_ARROW));
        _cursors.add(new SWTCursor("cursor.system.appStart",
                "Application Startup (System)", SWT.CURSOR_APPSTARTING));
        _cursors.add(new SWTCursor("cursor.system.cross",
                "Cross Hair (System)", SWT.CURSOR_CROSS));
        _cursors.add(new SWTCursor("cursor.system.hand",
                "Hand (System)", SWT.CURSOR_HAND));
        _cursors.add(new SWTCursor("cursor.system.help",
                "Help (System)", SWT.CURSOR_HELP));
        _cursors.add(new SWTCursor("cursor.system.ibeam",
                "i-beam (System)", SWT.CURSOR_IBEAM));
        _cursors.add(new SWTCursor("cursor.system.no",
                "not allowed (System)", SWT.CURSOR_NO));
        _cursors
                .add(new SWTCursor("cursor.system.resizeall",
                        "Resize all directions (System)", SWT.CURSOR_SIZEALL));
        _cursors.add(new SWTCursor("cursor.system.uparrow",
                "Up Arrow (System)", SWT.CURSOR_UPARROW));
        _cursors.add(new SWTCursor("cursor.system.wait",
                "Wait (System)", SWT.CURSOR_WAIT));
    }

    /**
     * Lookup cursors in the workspace.
     *
     * @return a list of cursors found in the workspace.
     */
    private List<AbstractCursor> getWorkSpaceCursors() {
        final List<AbstractCursor> cursors = new ArrayList<AbstractCursor>();

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
                CURSORS_PROJECT_NAME);
        if (project != null && project.isOpen()) {
            try {
                project.accept(new IResourceVisitor() {

                    public boolean visit(final IResource resource)
                            throws CoreException {
                        if (resource instanceof IFile
                                && SUPPORTED_FILE_EXTENSIONS.contains(resource
                                        .getFileExtension())) {
                            IPath path = resource.getProjectRelativePath();
                            AbstractCursor descriptor = new WorkspaceCursor(
                                    path.toPortableString(), path
                                            .lastSegment()
                                            + " (Workspace local)", resource
                                            .getLocation().toPortableString());
                            cursors.add(descriptor);
                        }

                        return resource instanceof IContainer;
                    }

                });
            } catch (CoreException e) {
                LOG.warn(e.toString());
            }
        }

        return cursors;
    }
}
