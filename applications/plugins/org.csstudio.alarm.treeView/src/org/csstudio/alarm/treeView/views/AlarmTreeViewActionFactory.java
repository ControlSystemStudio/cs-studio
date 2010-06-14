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
package org.csstudio.alarm.treeView.views;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmTreeNodePropertyId;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.jobs.ImportXmlFileJob;
import org.csstudio.alarm.treeView.ldap.AlarmTreeContentModelBuilder;
import org.csstudio.alarm.treeView.ldap.DirectoryEditException;
import org.csstudio.alarm.treeView.ldap.DirectoryEditor;
import org.csstudio.alarm.treeView.model.AbstractAlarmTreeNode;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.service.AlarmMessageListener;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.EditorUtil;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ExportContentModelException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.osgi.framework.Bundle;

/**
 * Action factory for the alarm tree view.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 18.05.2010
 */
public final class AlarmTreeViewActionFactory {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(AlarmTreeViewActionFactory.class);

    /**
     * The ID of the property view.
     */
    private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

    /**
     * Constructor.
     */
    private AlarmTreeViewActionFactory() {
        // Don't instantiate
    }

    /**
     * @param site
     * @return
     */
    @Nonnull
    public static Action createShowPropertyViewAction(@Nonnull final IWorkbenchPartSite site) {
        final Action showPropertyViewAction = new Action() {
            @Override
            public void run() {
                try {
                    site.getPage().showView(PROPERTY_VIEW_ID);
                } catch (final PartInitException e) {
                    MessageDialog.openError(site.getShell(), "Alarm Tree", e.getMessage());
                }
            }
        };
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
     * @return
     */
    @Nonnull
    public static Action createRenameAction(@Nonnull final IWorkbenchPartSite site,
                                            @Nonnull final TreeViewer viewer) {
        final Action renameAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final IAlarmTreeNode selected = (IAlarmTreeNode) selection.getFirstElement();
                final String name = promptForNewName(selected.getName());
                if (name != null) {
                    try {
                        DirectoryEditor.rename(selected, name);
                    } catch (final DirectoryEditException e) {
                        MessageDialog.openError(site.getShell(),
                                                "Rename",
                                                "Could not rename the entry: " + e.getMessage());
                    }
                    viewer.refresh(selected);
                }
            }
            @CheckForNull
            private String promptForNewName(@Nullable final String oldName) {
                final InputDialog dialog = new InputDialog(site.getShell(),
                                                           "Rename",
                                                           "Name:",
                                                           oldName,
                                                           new NodeNameInputValidator());
                if (Window.OK == dialog.open()) {
                    return dialog.getValue();
                }
                return null;
            }
        };
        renameAction.setText("Rename...");
        return renameAction;
    }

    /**
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createDeleteNodeAction(@Nonnull final IWorkbenchPartSite site,
                                                @Nonnull final TreeViewer viewer) {
        final Action deleteNodeAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode nodeToDelete = (IAlarmTreeNode) selected;
                    final IAlarmSubtreeNode parent = nodeToDelete.getParent();
                    try {
                        DirectoryEditor.delete(nodeToDelete);
                        if (parent != null) {
                            viewer.refresh(parent);
                        }
                    } catch (final DirectoryEditException e) {
                        MessageDialog.openError(site.getShell(),
                                                "Delete",
                                                "Could not delete this node: " + e.getMessage());
                    }
                }
            }
        };
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
                                                     @Nonnull final TreeViewer viewer) {
        final Action createComponentAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof SubtreeNode) {
                    final SubtreeNode parent = (SubtreeNode) selected;
                    final String name = promptForRecordName();
                    if ( (name != null) && !name.equals("")) {
                        try {
                            DirectoryEditor.createComponent(parent, name);
                        } catch (final DirectoryEditException e) {
                            MessageDialog.openError(site.getShell(),
                                                    "Create New Component",
                                                    "Could not create the new component: "
                                                    + e.getMessage());
                        }
                        viewer.refresh(parent);
                    }
                }
            }
            @CheckForNull
            private String promptForRecordName() {
                final InputDialog dialog = new InputDialog(site.getShell(),
                                                           "Create New Component",
                                                           "Component name:",
                                                           null,
                                                           new NodeNameInputValidator());
                if (Window.OK == dialog.open()) {
                    return dialog.getValue();
                }
                return null;
            }
        };
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
        final Action saveAsXmlFileAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof SubtreeNode) {
                    final SubtreeNode root = (SubtreeNode) selected;

                    if (LdapEpicsAlarmCfgObjectClass.FACILITY.equals(root.getObjectClass())) {

                        final String filePath = getFileToSaveTo(site);
                        if (filePath != null) {
                            createModelAndWriteXmlFile(site, Collections.<IAlarmTreeNode>singletonList(root), filePath);
                        }

                    } else {
                        LOG.error("Saving XML file is only possible on " + LdapEpicsAlarmCfgObjectClass.FACILITY.getDescription() + " type components.");
                        MessageDialog.openError(site.getShell(),
                                                "Save as XML file",
                                                "Internal error: XML files can only be saved for complete facility subtrees.");
                    }
                }
            }
        };
        saveAsXmlFileAction.setText("Save as XML...");
        return saveAsXmlFileAction;
    }

    @CheckForNull
    private static String getFileToSaveTo(@Nonnull final IWorkbenchPartSite site) {
        final FileDialog dialog = new FileDialog(site.getShell(), SWT.SAVE);
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
                                                   @Nonnull final SubtreeNode rootNode) {
        final Action exportXmlFileAction = new Action() {
            @Override
            public void run() {
                final String filePath = getFileToSaveTo(site);
                if (filePath != null) {
                    createModelAndWriteXmlFile(site, Arrays.asList(rootNode.getChildren()), filePath);
                }
            }
        };
        exportXmlFileAction.setText("Export XML...");
        exportXmlFileAction.setToolTipText("Export XML");
        exportXmlFileAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor("./icons/exportxml.png"));
        return exportXmlFileAction;
    }


    @Nonnull
    public static Action createImportXmlFileAction(@Nonnull final ImportXmlFileJob importXmlFileJob,
                                                   @Nonnull final IWorkbenchPartSite site,
                                                   @Nonnull final AlarmMessageListener alarmListener,
                                                   @Nonnull final TreeViewer viewer) {

        final Action importXmlFileAction = new Action() {
            @Override
            public void run() {
                final String filePath = getFileNameToLoadFrom();
                if (filePath != null) {
                    importXmlFileJob.setXmlFilePath(filePath);
                    LOG.debug("Starting XML file importer.");
                    final IWorkbenchSiteProgressService progressService =
                        (IWorkbenchSiteProgressService) site.getAdapter(IWorkbenchSiteProgressService.class);

                    // Set the tree to which updates are applied to null. This means updates
                    // will be queued for later application.
                    alarmListener.setUpdater(null);
                    // The directory is read in the background. Until then, set the viewer's
                    // input to a placeholder object.
                    viewer.setInput(new Object[] {new PendingUpdateAdapter()});
                    // Start the directory reader job.
                    progressService.schedule(importXmlFileJob, 0, true);
                }
            }

            @CheckForNull
            private String getFileNameToLoadFrom() {
                final FileDialog dialog = new FileDialog(site.getShell(), SWT.OPEN);
                dialog.setText("Load alarm tree configuration file (.xml)");
                return dialog.open();
            }
        };
        importXmlFileAction.setText("Import XML...");
        importXmlFileAction.setToolTipText("Import XML");
        importXmlFileAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor("./icons/importxml.gif"));

        return importXmlFileAction;
    }


    private static void createModelAndWriteXmlFile(@Nonnull final IWorkbenchPartSite site,
                                                   @Nonnull final List<IAlarmTreeNode> list,
                                                   @Nonnull final String filePath) {
        try {

            final AlarmTreeContentModelBuilder builder = new AlarmTreeContentModelBuilder(list);
            builder.build();
            final ContentModel<LdapEpicsAlarmCfgObjectClass> model = builder.getModel();

            final IAlarmConfigurationService configService = AlarmTreePlugin.getDefault().getAlarmConfigurationService();

            if (model != null) {
                final Bundle bundle = AlarmTreePlugin.getDefault().getBundle();
                final File loc = FileLocator.getBundleFile(bundle);
                final String dtdFilePath = new File(loc, "dtd/epicsAlarmCfg.dtd").toString();
                configService.exportContentModelToXmlFile(filePath, model, dtdFilePath);
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
            LOG.error("Bundle file path to compose dtd file path could not be identified.");
            MessageDialog.openError(site.getShell(), "Save as XML file", "Internal error: Bundle path not identifiable.");
        }
    }


    /**
     * @param site
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createCreateRecordAction(@Nonnull final IWorkbenchPartSite site,
                                                  @Nonnull final TreeViewer viewer) {
        final Action createRecordAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof SubtreeNode) {
                    final SubtreeNode parent = (SubtreeNode) selected;
                    final String name = promptForRecordName();
                    if ( (name != null) && !name.equals("")) {
                        try {
                            DirectoryEditor.createProcessVariableRecord(parent, name);
                        } catch (final DirectoryEditException e) {
                            MessageDialog.openError(site.getShell(),
                                                    "Create New Record",
                                                    "Could not create the new record: "
                                                    + e.getMessage());
                        }
                        viewer.refresh(parent);
                    }
                }
            }

            @CheckForNull
            private String promptForRecordName() {
                final InputDialog dialog = new InputDialog(site.getShell(),
                                                           "Create New Record",
                                                           "Record name:",
                                                           null,
                                                           new NodeNameInputValidator());
                if (Window.OK == dialog.open()) {
                    return dialog.getValue();
                }
                return null;
            }
        };
        createRecordAction.setText("Create Record...");
        return createRecordAction;
    }

    /**
     * @param viewer
     * @return
     */
    @Nonnull
    public static Action createShowHelpPageAction(@Nonnull final TreeViewer viewer) {
        final Action showHelpPageAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode node = (IAlarmTreeNode) selected;
                    URL helpPage;
                    try {
                        helpPage = new URL(node.getProperty(AlarmTreeNodePropertyId.HELP_PAGE));
                    } catch (final MalformedURLException e1) {
                        LOG.warn("URL property of node " + node.getName() + " was malformed.");
                        helpPage = null;
                    }
                    if (helpPage != null) {
                        try {
                            // Note: we have to pass a browser id here to work
                            // around a bug in eclipse. The method documentation
                            // says that createBrowser accepts null but it will
                            // throw a NullPointerException.
                            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=194988
                            final IWebBrowser browser =
                                PlatformUI.getWorkbench().getBrowserSupport().createBrowser("workaround");
                            browser.openURL(helpPage);
                        } catch (final PartInitException e) {
                            LOG.error("Failed to initialize workbench browser.", e);
                        }
                    }
                }
            }
        };
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
        final Action showHelpGuidanceAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode node = (IAlarmTreeNode) selected;
                    final String helpGuidance = node.getProperty(AlarmTreeNodePropertyId.HELP_GUIDANCE);
                    if (helpGuidance != null) {
                        MessageDialog.openInformation(site.getShell(),
                                                      node.getName(),
                                                      helpGuidance);
                    }
                }
            }
        };
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
        final Action openCssStripChartAction = new Action() {
            @Override
            public void run() {
                final IAlarmTreeNode node = getSelectedNode();
                if (node != null) {
                    final IPath path = new Path(node.getProperty(AlarmTreeNodePropertyId.CSS_STRIP_CHART));

                    // The following code assumes that the path is relative to
                    // the Eclipse workspace.
                    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                    final IWorkbenchPage page = site.getPage();
                    try {
                        EditorUtil.openEditor(page, file);
                    } catch (final PartInitException e) {
                        MessageDialog.openError(site.getShell(), "Alarm Tree", e.getMessage());
                    }
                }
            }
            /**
             * Returns the node that is currently selected in the tree.
             *
             * @return the selected node, or <code>null</code> if the selection is empty or the selected
             *         node is not of type <code>IAlarmTreeNode</code>.
             */
            @CheckForNull
            private IAlarmTreeNode getSelectedNode() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    return (IAlarmTreeNode) selected;
                }
                return null;
            }
        };
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

        final Action runCssDisplayAction = new Action() {
            @Override
            public void run() {

                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode node = (IAlarmTreeNode) selected;
                    final IPath path = new Path(node.getProperty(AlarmTreeNodePropertyId.CSS_DISPLAY));
                    final Map<String, String> aliases = new HashMap<String, String>();
                    if (node instanceof ProcessVariableNode) {
                        aliases.put("channel", node.getName());
                    }
                    LOG.debug("Opening display: " + path);
                    RunModeService.getInstance().openDisplayShellInRunMode(path, aliases);
                }
            }
        };
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
        final Action runCssAlarmDisplayAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode node = (IAlarmTreeNode) selected;
                    final IPath path = new Path(node.getProperty(AlarmTreeNodePropertyId.CSS_ALARM_DISPLAY));
                    final Map<String, String> aliases = new HashMap<String, String>();
                    if (node instanceof ProcessVariableNode) {
                        aliases.put("channel", node.getName());
                    }
                    LOG.debug("Opening display: " + path);
                    RunModeService.getInstance().openDisplayShellInRunMode(path, aliases);
                }
            }
        };
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
        final Action acknowledgeAction = new Action() {
            @Override
            public void run() {
                final Set<Map<String, String>> messages = new HashSet<Map<String, String>>();

                final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

                for (final Iterator<?> i = selection.iterator(); i.hasNext();) {
                    final Object o = i.next();
                    if (o instanceof SubtreeNode) {
                        final SubtreeNode snode = (SubtreeNode) o;
                        for (final AbstractAlarmTreeNode pvnode : snode.collectUnacknowledgedAlarms()) {
                            final String name = pvnode.getName();
                            final Severity severity = pvnode.getUnacknowledgedAlarmSeverity();
                            final Map<String, String> properties = new HashMap<String, String>();
                            properties.put("NAME", name);
                            properties.put("SEVERITY", severity.toString());
                            messages.add(properties);
                        }
                    } else if (o instanceof ProcessVariableNode) {
                        final AbstractAlarmTreeNode pvnode = (AbstractAlarmTreeNode) o;
                        final String name = pvnode.getName();
                        final Severity severity = pvnode.getUnacknowledgedAlarmSeverity();
                        final Map<String, String> properties = new HashMap<String, String>();
                        properties.put("NAME", name);
                        properties.put("SEVERITY", severity.toString());
                        messages.add(properties);
                    }
                }
                if (!messages.isEmpty()) {
                    LOG.debug("Scheduling send acknowledgement (" + messages.size() + " messages)");
                    final SendAcknowledge ackJob = SendAcknowledge.newFromProperties(messages);
                    ackJob.schedule();
                }
            }
        };
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
                                            @Nonnull final TreeViewer viewer) {
        final Action reloadAction = new Action() {
            @Override
            public void run() {
                LOG.debug("Starting directory reader.");
                final IWorkbenchSiteProgressService progressService =
                    (IWorkbenchSiteProgressService) site.getAdapter(IWorkbenchSiteProgressService.class);

                // Set the tree to which updates are applied to null. This means updates
                // will be queued for later application.
                alarmListener.setUpdater(null);

                // The directory is read in the background. Until then, set the viewer's
                // input to a placeholder object.
                viewer.setInput(new Object[] {new PendingUpdateAdapter()});

                // Start the directory reader job.
                progressService.schedule(directoryReaderJob, 0, true);
            }
        };
        reloadAction.setText("Reload");
        reloadAction.setToolTipText("Reload");
        reloadAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor("./icons/refresh.gif"));

        return reloadAction;
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
        final Action toggleFilterAction = new Action("Show Only Alarms", IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
                if (alarmTreeView.getIsFilterActive()) {
                    viewer.removeFilter(currentAlarmFilter);
                    alarmTreeView.setIsFilterActive(Boolean.FALSE);
                } else {
                    viewer.addFilter(currentAlarmFilter);
                    alarmTreeView.setIsFilterActive(Boolean.TRUE);
                }
            }
        };
        toggleFilterAction.setToolTipText("Show Only Alarms");
        toggleFilterAction.setChecked(alarmTreeView.getIsFilterActive().booleanValue());
        toggleFilterAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor("./icons/no_alarm_filter.png"));

        return toggleFilterAction;
    }

}
