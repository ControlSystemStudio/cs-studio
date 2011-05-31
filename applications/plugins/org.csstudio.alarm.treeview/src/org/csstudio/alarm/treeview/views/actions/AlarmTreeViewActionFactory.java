/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.alarm.treeview.views.actions;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.alarm.treeview.jobs.ImportXmlFileJob;
import org.csstudio.alarm.treeview.jobs.RetrieveInitialStateJob;
import org.csstudio.alarm.treeview.ldap.AlarmTreeContentModelBuilder;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.preferences.AlarmTreePreference;
import org.csstudio.alarm.treeview.service.AlarmMessageListener;
import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.csstudio.alarm.treeview.views.ITreeModificationItem;
import org.csstudio.alarm.treeview.views.MessageArea;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ExportContentModelException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * Action factory for the alarm tree view.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 18.05.2010
 */
public final class AlarmTreeViewActionFactory {

    static final Logger LOG =
        CentralLogger.getInstance().getLogger(AlarmTreeViewActionFactory.class);

    /**
     * The ID of the property view.
     */
    public static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";
    
    /**
     * @param messageArea
     * @return
     */
    @Nonnull
    public static Action createShowMessageAreaAction(@Nonnull final MessageArea messageArea) {
        
        ShowMessageAreaAction action = new ShowMessageAreaAction(messageArea);
        action.setText("msg");
        action.setToolTipText("Show message area");
        action.setImageDescriptor(AlarmTreePlugin
                .getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() + "/details_view.gif"));
        
        return action;
    }
    
    /**
     * @param site
     * @return
     */
    @Nonnull
    public static Action createShowPropertyViewAction(@Nonnull final IWorkbenchPartSite site) {
        final Action showPropertyViewAction = new ShowPropertyViewAction(site);
        showPropertyViewAction.setText("Properties");
        showPropertyViewAction.setToolTipText("Show property view");

        final IViewRegistry viewRegistry = site.getWorkbenchWindow().getWorkbench().getViewRegistry();
        final IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
        showPropertyViewAction.setImageDescriptor(viewDesc.getImageDescriptor());

        return showPropertyViewAction;
    }

    /**
     * @param site
     * @param viewer
     * @param modificationItems
     * @return
     */
    @Nonnull
    public static Action createRenameAction(@Nonnull final IWorkbenchPartSite site,
                                            @Nonnull final TreeViewer viewer,
                                            @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        final Action renameAction = new RenameAction(viewer, site, modificationItems);
        renameAction.setText("Rename...");
        return renameAction;
    }

    /**
     * @param site
     * @param viewer
     * @param modificationItems
     * @return
     */
    @Nonnull
    public static Action createDeleteNodeAction(@Nonnull final IWorkbenchPartSite site,
                                                @Nonnull final TreeViewer viewer,
                                                @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        final Action deleteNodeAction = new DeleteNodeAction(site, viewer, modificationItems);
        deleteNodeAction.setText("Delete");
        return deleteNodeAction;
    }

    /**
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createCreateComponentAction(@Nonnull final IWorkbenchPartSite site,
                                                     @Nonnull final TreeViewer viewer,
                                                     @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        final Action createComponentAction = new CreateComponentAction(site, viewer, modificationItems);
        createComponentAction.setText("Create Component...");
        return createComponentAction;
    }

    /**
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createSaveAsXmlFileAction(@Nonnull final IWorkbenchPartSite site,
                                                   @Nonnull final TreeViewer viewer) {
        final Action saveAsXmlFileAction = new SaveAsXmlAction(site, viewer);
        saveAsXmlFileAction.setText("Save as XML...");
        return saveAsXmlFileAction;
    }

    @CheckForNull
    public static String getFileToSaveTo(@Nonnull final IWorkbenchPartSite site) {
        final FileDialog dialog = new FileDialog(site.getShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[] {"*.xml"});
        dialog.setText("Save alarm tree configuration file (.xml)");
        dialog.setOverwrite(true);
        return dialog.open();
    }

    /**
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createExportXmlFileAction(@Nonnull final IWorkbenchPartSite site,
                                                   @Nonnull final IAlarmSubtreeNode rootNode) {
        final Action exportXmlFileAction = new ExportXmlFileAction(rootNode, site);
        exportXmlFileAction.setText("Export XML...");
        exportXmlFileAction.setToolTipText("Export XML");
        exportXmlFileAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                                  "/exportxml.png"));
        return exportXmlFileAction;
    }


    @Nonnull
    public static Action createImportXmlFileAction(@Nonnull final ImportXmlFileJob importXmlFileJob,
                                                   @Nonnull final IWorkbenchPartSite site,
                                                   @Nonnull final AlarmMessageListener alarmListener,
                                                   @Nonnull final TreeViewer viewer) {

        final Action importXmlFileAction = new ImportXmlFileAction(site,
                                                                   alarmListener,
                                                                   importXmlFileJob,
                                                                   viewer);
        importXmlFileAction.setText("Import XML...");
        importXmlFileAction.setToolTipText("Import XML");
        importXmlFileAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                                  "/importxml.gif"));

        return importXmlFileAction;
    }


    public static void createModelAndWriteXmlFile(@Nonnull final IWorkbenchPartSite site,
                                                   @Nonnull final List<IAlarmTreeNode> list,
                                                   @Nonnull final String filePath) {
        try {

            final AlarmTreeContentModelBuilder builder = new AlarmTreeContentModelBuilder(list);
            builder.build();
            final ContentModel<LdapEpicsAlarmcfgConfiguration> model = builder.getModel();

            final IAlarmConfigurationService configService = AlarmTreePlugin.getDefault().getAlarmConfigurationService();

            if (model != null) {
                configService.exportContentModelToXmlFile(filePath, 
                                                          model, 
                                                          LdapEpicsAlarmcfgConfiguration.getDtdFilePath());
            }

        } catch (final CreateContentModelException e) {
            LOG.error("Creating content model from facility was not possible due to invalid LDAP name.");
            MessageDialog.openError(site.getShell(),
                                    "Save as XML file",
            "Internal error: Content model could not be created for facility subtree.");

        } catch (final ExportContentModelException e) {
            LOG.error("Exporting content model for facility to XML file has not been successful.");
            MessageDialog.openError(site.getShell(),
                                    "Save as XML file",
            "Internal error: XML file could not be for facility.");
        } catch (final IOException e) {
            LOG.error("Exporting content model for facility to XML file has not been successful.");
            MessageDialog.openError(site.getShell(),
                                    "Save as XML file",
            "Internal error: DTD file could not be located.");
        }
    }


    /**
     * @param site
     * @param viewer
     * @param alarmTreeView
     * @param modificationItems
     * @return
     */
    @Nonnull
    public static Action createCreateRecordAction(@Nonnull final IWorkbenchPartSite site,
                                                  @Nonnull final TreeViewer viewer,
                                                  @Nonnull final AlarmTreeView alarmTreeView,
                                                  @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        final Action createRecordAction = new CreateRecordAction(site, viewer, alarmTreeView, modificationItems);
        createRecordAction.setText("Create Record...");
        return createRecordAction;
    }

    /**
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createShowHelpPageAction(@Nonnull final TreeViewer viewer) {
        final Action showHelpPageAction = new ShowHelpPageAction(viewer);
        showHelpPageAction.setText("Open Help Page");
        showHelpPageAction.setToolTipText("Open the help page for this node in the web browser");
        showHelpPageAction.setEnabled(false);
        return showHelpPageAction;
    }

    /**
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createShowHelpGuidanceAction(@Nonnull final IWorkbenchPartSite site,
                                                      @Nonnull final TreeViewer viewer) {
        final Action showHelpGuidanceAction = new ShowHelpGuidanceAction(viewer, site);
        showHelpGuidanceAction.setText("Show Help Guidance");
        showHelpGuidanceAction.setToolTipText("Show the help guidance for this node");
        showHelpGuidanceAction.setEnabled(false);
        return showHelpGuidanceAction;
    }

    /**
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createCssStripChartAction(@Nonnull final IWorkbenchPartSite site,
                                                   @Nonnull final TreeViewer viewer) {
        final Action openCssStripChartAction = new CssStripChartAction(viewer, site);
        openCssStripChartAction.setText("Open Strip Chart");
        openCssStripChartAction.setToolTipText("Open the strip chart for this node");
        openCssStripChartAction.setEnabled(false);
        return openCssStripChartAction;
    }

    /**
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createRunCssDisplayAction(@Nonnull final TreeViewer viewer) {

        final Action runCssDisplayAction = new RunCssDisplayAction(viewer);
        runCssDisplayAction.setText("Run Display");
        runCssDisplayAction.setToolTipText("Run the display for this PV");
        runCssDisplayAction.setEnabled(false);

        return runCssDisplayAction;
    }

    /**
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createCssAlarmDisplayAction(@Nonnull final TreeViewer viewer) {
        final Action runCssAlarmDisplayAction = new RunCssAlarmDisplayAction(viewer);
        runCssAlarmDisplayAction.setText("Run Alarm Display");
        runCssAlarmDisplayAction.setToolTipText("Run the alarm display for this PV");
        runCssAlarmDisplayAction.setEnabled(false);

        return runCssAlarmDisplayAction;
    }

    /**
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createAcknowledgeAction(@Nonnull final TreeViewer viewer) {
        final Action acknowledgeAction = new AcknowledgeSecureAction(viewer);
        acknowledgeAction.setText("Send Acknowledgement");
        acknowledgeAction.setToolTipText("Send alarm acknowledgement");
        acknowledgeAction.setEnabled(false);

        return acknowledgeAction;
    }

    /**
     *
     * @param directoryReaderJob
     * @param site
     * @param alarmListener
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createReloadAction(@Nonnull final Job directoryReaderJob,
                                            @Nonnull final IWorkbenchPartSite site,
                                            @Nonnull final AlarmMessageListener alarmListener,
                                            @Nonnull final TreeViewer viewer,
                                            @Nonnull final Queue<ITreeModificationItem> modificationItems) {
        final Action reloadAction = new ReloadFromLdapAction(site, viewer, alarmListener, modificationItems, directoryReaderJob);
        reloadAction.setText("Reload");
        reloadAction.setToolTipText("Reload");
        reloadAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                           "/refresh.gif"));

        return reloadAction;
    }


    /**
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createRetrieveInitialStateAction(@Nonnull final IWorkbenchPartSite site,
                                                          @Nonnull final RetrieveInitialStateJob retrieveInitialStateJob,
                                                          @Nonnull final TreeViewer viewer) {
        Action action = new RetrieveInitialStateAction(site, retrieveInitialStateJob, viewer);
        action.setText("Retrieve initial state");
        action.setToolTipText("Retrieve initial state");
        return action;
    }


    /**
     *
     * @param alarmTreeView
     * @param viewer
     * @param currentAlarmFilter
     * @return
     */
    @Nonnull
    public static Action createToggleFilterAction(@Nonnull final AlarmTreeView alarmTreeView,
                                                  @Nonnull final TreeViewer viewer,
                                                  @Nonnull final ViewerFilter currentAlarmFilter) {
        final Action toggleFilterAction = new ToggleFilterAction("Show Only Alarms", IAction.AS_CHECK_BOX, alarmTreeView, currentAlarmFilter, viewer);
        toggleFilterAction.setToolTipText("Show Only Alarms");
        toggleFilterAction.setChecked(alarmTreeView.getIsFilterActive().booleanValue());
        toggleFilterAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                                 "/no_alarm_filter.png"));

        return toggleFilterAction;
    }

    /**
     * @param rootNode
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static AbstractUserDependentAction createSaveInLdapAction(@Nonnull final IAlarmSubtreeNode rootNode,
                                                @Nonnull final IWorkbenchPartSite site,
                                                @Nonnull final TreeViewer viewer,
                                                @Nonnull final Queue<ITreeModificationItem> modifications) {

        final AbstractUserDependentAction saveInLdapAction = new SaveInLdapSecureAction(site, modifications);
        saveInLdapAction.setToolTipText("Save in LDAP");
        saveInLdapAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                               "/saveinldap.gif"));
        saveInLdapAction.setEnabled(false);
        return saveInLdapAction;
    }


    /**
     * Constructor.
     */
    private AlarmTreeViewActionFactory() {
        // Don't instantiate
    }

}
