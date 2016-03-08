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
package org.csstudio.sds;

import org.csstudio.sds.cursorservice.ICursorService;
import org.csstudio.sds.internal.SdsResourceChangeListener;
import org.csstudio.sds.internal.eventhandling.BehaviorService;
import org.csstudio.sds.internal.eventhandling.IBehaviorService;
import org.csstudio.sds.internal.eventhandling.IWidgetPropertyPostProcessingService;
import org.csstudio.sds.internal.eventhandling.WidgetPropertyPostProcessingService;
import org.csstudio.sds.internal.rules.RuleService;
import org.csstudio.sds.util.StringUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle.
 *
 * @author Alexander Will
 * @version $Revision: 1.28 $
 */
public final class SdsPlugin extends Plugin {

    /**
     * The ID of this plugin.
     */
    public static final String PLUGIN_ID = "org.csstudio.sds"; //$NON-NLS-1$

    /**
     * The ID of the rules extension point.
     */
    public static final String EXTPOINT_RULES = PLUGIN_ID + ".rules"; //$NON-NLS-1$

    /**
     * The ID of the widget model factories extension point.
     */
    public static final String EXTPOINT_WIDGET_MODEL_FACTORIES = PLUGIN_ID
            + ".widgetModelFactories"; //$NON-NLS-1$

    /**
     * Extension point ID for the <b>propertyPersistenceHandlers</b> extension
     * point.
     */
    public static final String EXTPOINT_PROPERTY_PERSISTENCE_HANDLERS = PLUGIN_ID
            + ".propertyPersistenceHandlers"; //$NON-NLS-1$

    /**
     * Extension point ID for the <b>widgetModelInitializers</b> extension
     * point.
     */
    public static final String EXTPOINT_WIDGET_MODEL_INITIALIZERS = PLUGIN_ID
            + ".widgetModelInitializers"; //$NON-NLS-1$

    /**
     * Extension point ID for the <b>cursors</b> extension point.
     */
    public static final String EXPOINT_CURSORS = PLUGIN_ID + ".cursors"; //$NON-NLS-1$

    /**
     * The name of the default SDS workspace project.
     */
    public static final String DEFAULT_PROJECT_NAME = "SDS"; //$NON-NLS-1$

    /**
     * The name of the resource folder which contains the scripts.
     */
    public static final String RESOURCE_SCRIPT_FOLDER_NAME = "scripts"; //$NON-NLS-1$

    /**
     * Extension point ID for the {@code cursorSelectionRules} extension point.
     */
    public static final String EXTPOINT_CURSOR_SELECTION_RULES = PLUGIN_ID
            + ".cursorSelectionRules"; //$NON-NLS-1$

    /**
     * Extension point ID for the widgetPropertyPostProcessors extension point.
     */
    public static final String EXTPOINT_WIDGET_PROPERTY_POSTPROCESSORS = PLUGIN_ID
        + ".widgetPropertyPostProcessors"; //$NON-NLS-1$

    /**
     * The ID of the behavior extension point.
     */
    public static final String EXTPOINT_BEHAVIORS = PLUGIN_ID + ".behaviors"; //$NON-NLS-1$

    /**
     * The shared instance of this plugin activator.
     */
    private static SdsPlugin _plugin;

    private IBehaviorService _behaviourService;

    private IWidgetPropertyPostProcessingService _widgetPropertyPostProcessingService;
    /**
     * Change listener for SDS resources.
     */
    private SdsResourceChangeListener _resourceChangeListener;

    private static final Logger LOG = LoggerFactory.getLogger(SdsPlugin.class);

//    TODO (jhatje): remove if patch in jca lib works
//    /**
//     * List of Strings to match records that should be asked for Strings.
//     * (For workaround in ConnectionUtilNew)
//     */
//    private ArrayList<String> _recordTails = new ArrayList<String>();
//    private ArrayList<String> _recordTailsRegExp = new ArrayList<String>();




    /**
     * Standard constructor.
     */
    public SdsPlugin() {
        _plugin = this;
    }

    /**
     * Returns the shared instance of this _plugin activator.
     *
     * @return The shared instance of this _plugin activator.
     */
    public static SdsPlugin getDefault() {
        return _plugin;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        _resourceChangeListener = new SdsResourceChangeListener();

        // create the default SDS project
        ResourceService.getInstance().createWorkspaceProject(
                DEFAULT_PROJECT_NAME);

        // put the script rules into the workspace
        IProject scriptProject = ResourceService.getInstance()
                .createWorkspaceProject(RuleService.SCRIPT_PROJECT_NAME);
        ResourceService.getInstance()
                .copyResources(scriptProject,
                        SdsPlugin.getDefault().getBundle(),
                        RESOURCE_SCRIPT_FOLDER_NAME);

        // put the cursors project into the workspace
        ResourceService.getInstance().createWorkspaceProject(
                ICursorService.CURSORS_PROJECT_NAME);

        // register the workspace listener that keeps track of script file
        // changes
        ResourceService.getInstance().addResourceChangeListener(
                _resourceChangeListener);

        // initialize the rules for the very first time
        if (RuleService.getInstance().isErrorOccurred()) {
            LOG.error(StringUtil.convertListToSingleString(RuleService
                            .getInstance().getErrorMessages()));
        }

        _behaviourService = new BehaviorService();
        _widgetPropertyPostProcessingService = new WidgetPropertyPostProcessingService();
//        TODO (jhatje): remove if patch in jca lib works
//        readWorkaroundStrings();
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        // de-register the workspace listener
        ResourceService.getInstance().removeResourceChangeListener(
                _resourceChangeListener);
    }

    public IBehaviorService getBehaviourService() {
        return _behaviourService;
    }

    public IWidgetPropertyPostProcessingService getWidgetPropertyPostProcessingService() {
        return _widgetPropertyPostProcessingService;
    }

//    TODO (jhatje): remove if patch in jca lib works
//    public ArrayList<String> getRecordTails() {
//        return _recordTails;
//    }
//
//    public ArrayList<String> getRecordTailsRegExp() {
//        return _recordTailsRegExp;
//    }
//
//    private void readWorkaroundStrings() {
//        try {
//            URL urlRecTails = FileLocator.toFileURL( SdsPlugin.getDefault().getBundle().getEntry("/RecordTails"));
//            File f = new File(urlRecTails.getPath());
//            FileReader fr = new FileReader(f);
//            BufferedReader br = new BufferedReader(fr);
//            String s;
//            while(null!= (s = br.readLine())){
//                _recordTails.add(s);
//            }
//            URL urlRecTailsRegExp = FileLocator.toFileURL( SdsPlugin.getDefault().getBundle().getEntry("/RecordTailsRegExp"));
//            f = new File(urlRecTailsRegExp.getPath());
//            fr = new FileReader(f);
//            br = new BufferedReader(fr);
//            while(null!= (s = br.readLine())){
//                _recordTailsRegExp.add(s);
//            }
//        } catch (IOException e) {
//            CentralLogger.getInstance().error(this, "Error reading file");
//        }
//    }
}
