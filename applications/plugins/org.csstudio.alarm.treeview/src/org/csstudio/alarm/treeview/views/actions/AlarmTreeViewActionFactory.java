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
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.alarm.treeview.jobs.ImportXmlFileJob;
import org.csstudio.alarm.treeview.jobs.RetrieveInitialStateJob;
import org.csstudio.alarm.treeview.ldap.AlarmTreeContentModelBuilder;
import org.csstudio.alarm.treeview.localization.Messages;
import org.csstudio.alarm.treeview.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.model.PVNodeItem;
import org.csstudio.alarm.treeview.preferences.AlarmTreePreference;
import org.csstudio.alarm.treeview.service.AlarmMessageListener;
import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.csstudio.alarm.treeview.views.ITreeModificationItem;
import org.csstudio.alarm.treeview.views.MessageArea;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.csstudio.servicelocator.ServiceLocator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action factory for the alarm tree view.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 18.05.2010
 */
public final class AlarmTreeViewActionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AlarmTreeViewActionFactory.class);

    /**
     * The ID of the property view.
     */
    public static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$
    
    /**
     * @param messageArea
     * @return
     */
    @Nonnull
    public static Action createShowMessageAreaAction(@Nonnull final MessageArea messageArea) {
        
        ShowMessageAreaAction action = new ShowMessageAreaAction(messageArea);
        action.setText(Messages.AlarmTreeViewActionFactory_ShowMessageArea_Text);
        action.setToolTipText(Messages.AlarmTreeViewActionFactory_ShowMessageArea_TooltipText);
        action.setImageDescriptor(AlarmTreePlugin
                .getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() + "/details_view.gif")); //$NON-NLS-1$
        
        return action;
    }
    
    /**
     * @param site
     * @return
     */
    @Nonnull
    public static Action createShowPropertyViewAction(@Nonnull final IWorkbenchPartSite site) {
        final Action showPropertyViewAction = new ShowPropertyViewAction(site);
        showPropertyViewAction.setText(Messages.AlarmTreeViewActionFactory_ShowProperties_Text);
        showPropertyViewAction.setToolTipText(Messages.AlarmTreeViewActionFactory_ShowProperties_TooltipText);

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
        renameAction.setText(Messages.AlarmTreeViewActionFactory_Rename_Text);
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
        deleteNodeAction.setText(Messages.AlarmTreeViewActionFactory_Delete_Text);
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
        createComponentAction.setText(Messages.AlarmTreeViewActionFactory_CreateComponent_Text);
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
        saveAsXmlFileAction.setText(Messages.AlarmTreeViewActionFactory_SaveAsXml_Text);
        return saveAsXmlFileAction;
    }

    @CheckForNull
    public static String getFileToSaveTo(@Nonnull final IWorkbenchPartSite site) {
        final FileDialog dialog = new FileDialog(site.getShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[] {"*.xml"}); //$NON-NLS-1$
        dialog.setText(Messages.AlarmTreeViewActionFactory_SaveDialog_Text);
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
        exportXmlFileAction.setText(Messages.AlarmTreeViewActionFactory_Export_Text);
        exportXmlFileAction.setToolTipText(Messages.AlarmTreeViewActionFactory_Export_TooltipText);
        exportXmlFileAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                                  "/exportxml.png")); //$NON-NLS-1$
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
        importXmlFileAction.setText(Messages.AlarmTreeViewActionFactory_ImportXml_Text);
        importXmlFileAction.setToolTipText(Messages.AlarmTreeViewActionFactory_ImportXml_TooltipText);
        importXmlFileAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                                  "/importxml.gif")); //$NON-NLS-1$

        return importXmlFileAction;
    }


    public static void createModelAndWriteXmlFile(@Nonnull final IWorkbenchPartSite site,
                                                   @Nonnull final List<IAlarmTreeNode> list,
                                                   @Nonnull final String filePath) {
        try {

            final AlarmTreeContentModelBuilder builder = new AlarmTreeContentModelBuilder(list);
            builder.build();
            final ContentModel<LdapEpicsAlarmcfgConfiguration> model = builder.getModel();

            final IAlarmConfigurationService configService = ServiceLocator.getService(IAlarmConfigurationService.class);

            if (model != null) {
                configService.exportContentModelToXmlFile(filePath, 
                                                          model, 
                                                          LdapEpicsAlarmcfgConfiguration.getDtdFilePath());
            }

        } catch (final CreateContentModelException e) {
            LOG.error("Creating content model from facility was not possible due to invalid LDAP name."); //$NON-NLS-1$
            MessageDialog.openError(site.getShell(),
                                    Messages.AlarmTreeViewActionFactory_SaveXml_Failed_DialogTitle,
            Messages.AlarmTreeViewActionFactory_SaveXml_Failed_ModelCreation);

        } catch (final ExportContentModelException e) {
            LOG.error("Exporting content model for facility to XML file has not been successful."); //$NON-NLS-1$
            MessageDialog.openError(site.getShell(),
                                    Messages.AlarmTreeViewActionFactory_SaveXml_Failed_DialogTitle,
            Messages.AlarmTreeViewActionFactory_SaveXml_Failed_ModelExport);
        } catch (final IOException e) {
            LOG.error("Exporting content model for facility to XML file has not been successful."); //$NON-NLS-1$
            MessageDialog.openError(site.getShell(),
                                    Messages.AlarmTreeViewActionFactory_SaveXml_Failed_DialogTitle,
            Messages.AlarmTreeViewActionFactory_SaveXml_Failed_DTD_Missing);
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
        createRecordAction.setText(Messages.AlarmTreeViewActionFactory_CreateRecord_Text);
        return createRecordAction;
    }

    /**
     * Helper function to retrieve the initial alarm and acknowledge state of the pv of the given node
     * 
     * @param treeNode
     * @throws AlarmServiceException
     */
    public static void retrieveInitialStateSynchronously(@Nonnull final IAlarmTreeNode treeNode) throws AlarmServiceException {
        IAlarmProcessVariableNode node = null;
        try {
            node = (IAlarmProcessVariableNode) treeNode;
        } catch (ClassCastException e) {
            throw new AlarmServiceException("Cannot retrieve initial state: Wrong type of node");
        }
        
        final List<IAlarmInitItem> initItems = Collections
                .singletonList((IAlarmInitItem) new PVNodeItem(node));
        
        final IAlarmService alarmService = ServiceLocator.getService(IAlarmService.class);
        if (alarmService != null) {
            alarmService.retrieveInitialState(initItems);
        } else {
            throw new AlarmServiceException("Cannot retrieve initial state: Alarm service not available");
        }
    }
    
    /**
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createShowHelpPageAction(@Nonnull final TreeViewer viewer) {
        final Action showHelpPageAction = new ShowHelpPageAction(viewer);
        showHelpPageAction.setText(Messages.AlarmTreeViewActionFactory_OpenHelp_Text);
        showHelpPageAction.setToolTipText(Messages.AlarmTreeViewActionFactory_OpenHelp_TooltipText);
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
        showHelpGuidanceAction.setText(Messages.AlarmTreeViewActionFactory_ShowHelpGuidance_Text);
        showHelpGuidanceAction.setToolTipText(Messages.AlarmTreeViewActionFactory_ShowHelpGuidance_TooltipText);
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
        openCssStripChartAction.setText(Messages.AlarmTreeViewActionFactory_OpenStripChart_Text);
        openCssStripChartAction.setToolTipText(Messages.AlarmTreeViewActionFactory_OpenStripChart_TooltipText);
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
        runCssDisplayAction.setText(Messages.AlarmTreeViewActionFactory_RunDisplay_Text);
        runCssDisplayAction.setToolTipText(Messages.AlarmTreeViewActionFactory_RunDisplay_TooltipText);
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
        runCssAlarmDisplayAction.setText(Messages.AlarmTreeViewActionFactory_RunAlarmDisplay_Text);
        runCssAlarmDisplayAction.setToolTipText(Messages.AlarmTreeViewActionFactory_RunAlarmDisplay_TooltipText);
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
        acknowledgeAction.setText(Messages.AlarmTreeViewActionFactory_SendAck_Text);
        acknowledgeAction.setToolTipText(Messages.AlarmTreeViewActionFactory_SendAck_TooltipText);
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
        reloadAction.setText(Messages.AlarmTreeViewActionFactory_Reload_Text);
        reloadAction.setToolTipText(Messages.AlarmTreeViewActionFactory_Reload_TooltipText);
        reloadAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                           "/refresh.gif")); //$NON-NLS-1$

        return reloadAction;
    }


    /**
     * Send the reload command via reload service to all clients.
     * 
     * @return action
     */
    @Nonnull
    public static Action createRemoteReloadAction(@Nonnull final MessageArea messageArea) {
        final Action action = new RemoteReloadSecureAction(messageArea);
        action.setText(Messages.AlarmTreeViewActionFactory_RemoteReload_Text);
        action.setToolTipText(Messages.AlarmTreeViewActionFactory_RemoteReload_TooltipText);
        action.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                           "/remoteReload.gif")); //$NON-NLS-1$
        return action;
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
        action.setText(Messages.AlarmTreeViewActionFactory_RetrieveInitialState_Text);
        action.setToolTipText(Messages.AlarmTreeViewActionFactory_RetrieveInitialState_TooltipText);
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
        final Action toggleFilterAction = new ToggleFilterAction(Messages.AlarmTreeViewActionFactory_ShowOnlyAlarms_Text, IAction.AS_CHECK_BOX, alarmTreeView, currentAlarmFilter, viewer);
        toggleFilterAction.setToolTipText(Messages.AlarmTreeViewActionFactory_ShowOnlyAlarms_TooltipText);
        toggleFilterAction.setChecked(alarmTreeView.getIsFilterActive().booleanValue());
        toggleFilterAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                                 "/no_alarm_filter.png")); //$NON-NLS-1$

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
        saveInLdapAction.setToolTipText(Messages.AlarmTreeViewActionFactory_SaveInLdap_TooltipText);
        saveInLdapAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor(AlarmTreePreference.RES_ICON_PATH.getValue() +
                                                                               "/ldapsave.gif")); //$NON-NLS-1$
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
