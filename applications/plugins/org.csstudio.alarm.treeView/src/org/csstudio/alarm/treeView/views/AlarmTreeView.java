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
 package org.csstudio.alarm.treeView.views;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.jms.AlarmQueueSubscriber;
import org.csstudio.alarm.treeView.ldap.DirectoryEditException;
import org.csstudio.alarm.treeView.ldap.DirectoryEditor;
import org.csstudio.alarm.treeView.ldap.LdapDirectoryReader;
import org.csstudio.alarm.treeView.ldap.LdapDirectoryStructureReader;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * Tree view of process variables and their alarm state. This view uses LDAP
 * to get a hierarchy of process variables and displays them in a tree view.
 * Process variables which are in an alarm state are visually marked in the
 * view.
 */
public class AlarmTreeView extends ViewPart {

	/**
	 * The ID of this view.
	 */
	private static final String ID = "org.csstudio.alarm.treeView.views.LdapTView";
	
	/**
	 * The tree viewer that displays the alarm objects.
	 */
	private TreeViewer _viewer;
	
	/**
	 * The reload action.
	 */
	private Action _reloadAction;
	
	/**
	 * The subscriber to the JMS alarm topic.
	 */
	private AlarmQueueSubscriber _alarmTopicSubscriber;
	
	/**
	 * The acknowledge action.
	 */
	private Action _acknowledgeAction;
	
	/**
	 * The Run CSS Alarm Display action.
	 */
	private Action _runCssAlarmDisplayAction;
	
	/**
	 * The Show Help Page action.
	 */
	private Action _showHelpPageAction;
	
	/**
	 * The Show Help Guidance action.
	 */
	private Action _showHelpGuidanceAction;
	
	/**
	 * The Create Record action.
	 */
	private Action _createRecordAction;

	/**
	 * The Create Component action.
	 */
	private Action _createComponentAction;

	/**
	 * The Delete action.
	 */
	private Action _deleteNodeAction;
	
	/**
	 * Returns the id of this view.
	 * @return the id of this view.
	 */
	public static String getID(){
		return ID;
	}
	
	/**
	 * Creates an LDAP tree viewer.
	 */
	public AlarmTreeView() {
	}

	/**
	 * {@inheritDoc}
	 */
	public final void createPartControl(final Composite parent) {
		_viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		_viewer.setContentProvider(new AlarmTreeContentProvider());
		_viewer.setLabelProvider(new AlarmTreeLabelProvider());
		_viewer.setComparator(new ViewerComparator());

		initializeContextMenu();
		makeActions();
		contributeToActionBars();
		
		getSite().setSelectionProvider(_viewer);

		startDirectoryReaderJob();
		
		_viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				AlarmTreeView.this.selectionChanged(event);
			}
		});
	}

	/**
	 * Starts a job which reads the contents of the directory in the background.
	 */
	private void startDirectoryReaderJob() {
		final IWorkbenchSiteProgressService progressService =
			(IWorkbenchSiteProgressService) getSite().getAdapter(
					IWorkbenchSiteProgressService.class);
		final SubtreeNode rootNode = new SubtreeNode("ROOT");
		Job directoryReader = new LdapDirectoryReader(rootNode);
		
		// Add a listener that sets the viewers input to the root node
		// when the reader job is finished.
		directoryReader.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				setJmsListenerTree(rootNode);
				asyncSetViewerInput(rootNode);
				
				Job directoryUpdater = new LdapDirectoryStructureReader(rootNode);
				directoryUpdater.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(final IJobChangeEvent event) {
						getSite().getShell().getDisplay().asyncExec(new Runnable() {
							public void run() {
								_viewer.refresh();
							}
						});
					}
				});
				progressService.schedule(directoryUpdater, 0, true);
			}
		});
		// The directory is read in the background. Until then, set the viewer's
		// input to a placeholder object.
		_viewer.setInput(new Object[] {new PendingUpdateAdapter()});
		progressService.schedule(directoryReader, 0, true);
	}
	
	/**
	 * Sets the tree to which the JMS listener will apply updates. If the
	 * JMS listener is not started yet, this will also initialize and start
	 * the JMS listener.
	 * @param tree the tree to which updates should be applied.
	 */
	private void setJmsListenerTree(final SubtreeNode tree) {
		if (_alarmTopicSubscriber == null) {
			_alarmTopicSubscriber = new AlarmQueueSubscriber(tree);
			_alarmTopicSubscriber.openConnection();
		} else {
			_alarmTopicSubscriber.setTree(tree);
		}
	}
	
	/**
	 * Stops the alarm queue subscriber.
	 */
	private void disposeJmsListener() {
		_alarmTopicSubscriber.closeConnection();
	}

	/**
	 * Sets the input for the tree. The actual work will be done asynchronously
	 * in the UI thread.
	 * @param inputElement the new input element.
	 */
	private void asyncSetViewerInput(final SubtreeNode inputElement) {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				_viewer.setInput(inputElement);
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void dispose() {
		disposeJmsListener();
		super.dispose();
	}
	
	/**
	 * Called when the selection of the tree changes.
	 * @param event the selection event.
	 */
	private void selectionChanged(final SelectionChangedEvent event) {
		IStructuredSelection sel = (IStructuredSelection) event.getSelection();
		_acknowledgeAction.setEnabled(containsNodeWithUnackAlarm(sel));
		_runCssAlarmDisplayAction.setEnabled(hasCssAlarmDisplay(sel.getFirstElement()));
		_showHelpGuidanceAction.setEnabled(hasHelpGuidance(sel.getFirstElement()));
		_showHelpPageAction.setEnabled(hasHelpPage(sel.getFirstElement()));
	}
	
	/**
	 * Return whether help guidance is available for the given node.
	 * @param node the node.
	 * @return <code>true</code> if the node has a help guidance string,
	 * <code>false</code> otherwise.
	 */
	private boolean hasHelpGuidance(final Object node) {
		if (node instanceof IAlarmTreeNode) {
			return ((IAlarmTreeNode) node).getHelpGuidance() != null;
		}
		return false;
	}

	/**
	 * Return whether the given node has an associated help page.
	 * 
	 * @param node the node.
	 * @return <code>true</code> if the node has an associated help page,
	 * <code>false</code> otherwise.
	 */
	private boolean hasHelpPage(final Object node) {
		if (node instanceof IAlarmTreeNode) {
			return ((IAlarmTreeNode) node).getHelpPage() != null;
		}
		return false;
	}

	/**
	 * Returns whether the given process variable node in the tree has an
	 * associated CSS alarm display configured.
	 * 
	 * @param node the node.
	 * @return <code>true</code> if a CSS alarm display is configured for the
	 * node, <code>false</code> otherwise.
	 */
	private boolean hasCssAlarmDisplay(final Object node) {
		if (node instanceof IAlarmTreeNode) {
			String display = ((IAlarmTreeNode) node).getCssAlarmDisplay();
			return display != null && display.matches(".+\\.css-sds");
		}
		return false;
	}

	/**
	 * Returns whether the given selection contains at least one node with
	 * an unacknowledged alarm.
	 * 
	 * @param sel the selection.
	 * @return <code>true</code> if the selection contains a node with an
	 * unacknowledged alarm, <code>false</code> otherwise.
	 */
	private boolean containsNodeWithUnackAlarm(final IStructuredSelection sel) {
		Object selectedElement = sel.getFirstElement();
		// Note: selectedElement is not instance of IAlarmTreeNode if nothing
		// is selected (selectedElement == null), and during initialization,
		// when it is an instance of PendingUpdateAdapter.
		if (selectedElement instanceof IAlarmTreeNode) {
			return ((IAlarmTreeNode) selectedElement)
					.getUnacknowledgedAlarmSeverity() != Severity.NO_ALARM;
		}
		return false;
	}

	/**
	 * Adds a context menu to the tree view.
	 */
	private void initializeContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		
		// add menu items to the context menu when it is about to show
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				AlarmTreeView.this.fillContextMenu(manager);
			}
		});
		
		// add the context menu to the tree viewer
		Menu contextMenu = menuMgr.createContextMenu(_viewer.getTree());
		_viewer.getTree().setMenu(contextMenu);
		
		// register the context menu for extension by other plug-ins
		getSite().registerContextMenu(menuMgr, _viewer);
	}

	/**
	 * Adds tool buttons and menu items to the action bar of this view.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Adds the actions for the action bar's pull down menu.
	 * @param manager the menu manager.
	 */
	private void fillLocalPullDown(final IMenuManager manager) {
		// currently there are no actions in the pulldown menu
	}

	/**
	 * Adds the context menu actions.
	 * @param menu the menu manager.
	 */
	private void fillContextMenu(final IMenuManager menu) {
		IStructuredSelection selection = (IStructuredSelection) _viewer
				.getSelection();
		if (selection.size() > 0) {
			menu.add(_acknowledgeAction);
		}
		if (selection.size() == 1) {
			menu.add(_runCssAlarmDisplayAction);
			menu.add(_showHelpGuidanceAction);
			menu.add(_showHelpPageAction);
			menu.add(new Separator("edit"));
			menu.add(_deleteNodeAction);
		}
		if (selection.size() == 1
				&& selection.getFirstElement() instanceof SubtreeNode) {
			menu.add(_createRecordAction);
			menu.add(_createComponentAction);
		}
		
		// adds a separator after which contributed actions from other plug-ins
		// will be displayed
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	/**
	 * Adds the tool bar actions.
	 * @param manager the menu manager.
	 */
	private void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(_reloadAction);
	}

	/**
	 * Creates the actions offered by this view.
	 */
	private void makeActions() {
		_reloadAction = new Action() {
			public void run() {
				startDirectoryReaderJob();
			}
		};
		_reloadAction.setText("Reload");
		_reloadAction.setToolTipText("Reload");
		_reloadAction.setImageDescriptor(
				AlarmTreePlugin.getImageDescriptor("./icons/refresh.gif"));
		
		_acknowledgeAction = new Action() {
			@Override
			public void run() {
				Set<Map<String, String>> messages = new HashSet<Map<String, String>>();
				IStructuredSelection selection = (IStructuredSelection) _viewer
						.getSelection();
				for (Iterator<?> i = selection.iterator(); i
						.hasNext();) {
					Object o = i.next();
					if (o instanceof SubtreeNode) {
						SubtreeNode snode = (SubtreeNode) o;
						for (ProcessVariableNode pvnode : snode.collectUnacknowledgedAlarms()) {
							String name = pvnode.getName();
							Severity severity = pvnode.getUnacknowledgedAlarmSeverity();
							Map<String, String> properties = new HashMap<String, String>();
							properties.put("NAME", name);
							properties.put("SEVERITY", severity.toString());
							messages.add(properties);
						}
					} else if (o instanceof ProcessVariableNode) {
						ProcessVariableNode pvnode = (ProcessVariableNode) o;
						String name = pvnode.getName();
						Severity severity = pvnode.getUnacknowledgedAlarmSeverity();
						Map<String, String> properties = new HashMap<String, String>();
						properties.put("NAME", name);
						properties.put("SEVERITY", severity.toString());
						messages.add(properties);
					}
				}
				if (!messages.isEmpty()) {
					CentralLogger.getInstance().debug(this, "Scheduling send acknowledgement (" + messages.size() + " messages)");
					SendAcknowledge ackJob = SendAcknowledge.newFromProperties(messages);
					ackJob.schedule();
				}
			}
		};
		_acknowledgeAction.setText("Send Acknowledgement");
		_acknowledgeAction.setToolTipText("Send alarm acknowledgement");
		_acknowledgeAction.setEnabled(false);
		
		_runCssAlarmDisplayAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) _viewer
						.getSelection();
				Object selected = selection.getFirstElement();
				if (selected instanceof IAlarmTreeNode) {
					IAlarmTreeNode node = (IAlarmTreeNode) selected;
					IPath path = new Path(node.getCssAlarmDisplay());
					Map<String, String> aliases = new HashMap<String, String>();
					if (node instanceof ProcessVariableNode) {
						aliases.put("channel", node.getName());
					}
					CentralLogger.getInstance().debug(this, "Opening display: " + path);
					RunModeService.getInstance().openDisplayShellInRunMode(path, aliases);
				}
			}
		};
		_runCssAlarmDisplayAction.setText("Run Alarm Display");
		_runCssAlarmDisplayAction.setToolTipText("Run the alarm display for this PV");
		_runCssAlarmDisplayAction.setEnabled(false);
		
		_showHelpGuidanceAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) _viewer
						.getSelection();
				Object selected = selection.getFirstElement();
				if (selected instanceof IAlarmTreeNode) {
					IAlarmTreeNode node = (IAlarmTreeNode) selected;
					String helpGuidance = node.getHelpGuidance();
					if (helpGuidance != null) {
						MessageDialog.openInformation(getSite().getShell(),
								node.getName(), helpGuidance);
					}
				}
			}
		};
		_showHelpGuidanceAction.setText("Show Help Guidance");
		_showHelpGuidanceAction.setToolTipText("Show the help guidance for this node");
		_showHelpGuidanceAction.setEnabled(false);
		
		_showHelpPageAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) _viewer
						.getSelection();
				Object selected = selection.getFirstElement();
				if (selected instanceof IAlarmTreeNode) {
					IAlarmTreeNode node = (IAlarmTreeNode) selected;
					URL helpPage = node.getHelpPage();
					if (helpPage != null) {
						try {
							// Note: we have to pass a browser id here to work
							// around a bug in eclipse. The method documentation
							// says that createBrowser accepts null but it will
							// throw a NullPointerException.
							// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=194988
							IWebBrowser browser = PlatformUI.getWorkbench()
									.getBrowserSupport()
									.createBrowser("workaround");
							browser.openURL(helpPage);
						} catch (PartInitException e) {
							CentralLogger.getInstance().error(this,
									"Failed to initialize workbench browser.", e);
						}
					}
				}
			}
		};
		_showHelpPageAction.setText("Open Help Page");
		_showHelpPageAction.setToolTipText("Open the help page for this node in the web browser");
		_showHelpPageAction.setEnabled(false);
		
		_createRecordAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection =
					(IStructuredSelection) _viewer.getSelection();
				Object selected = selection.getFirstElement();
				if (selected instanceof SubtreeNode) {
					SubtreeNode parent = (SubtreeNode) selected;
					String name = promptForRecordName();
					if (name != null && !name.equals("")) {
						try {
							DirectoryEditor.createProcessVariableRecord(parent,
									name);
						} catch (DirectoryEditException e) {
							MessageDialog.openError(getSite().getShell(), 
									"Create New Record",
									"Could not create the new record: " + e.getMessage());
						}
						_viewer.refresh(parent);
					}
				}
			}

			private String promptForRecordName() {
				InputDialog dialog = new InputDialog(getSite().getShell(),
						"Create New Record", "Record name:", null,
						new IInputValidator() {
					public String isValid(final String newText) {
						if (newText.equals("")) {
							return "Please enter a name.";
						} else if (newText.indexOf("=") != -1
								|| newText.indexOf("/") != -1
								|| newText.indexOf(",") != -1) {
							return "The following characters are not allowed "
									+ "in names: = / ,";
						} else {
							return null;
						}
					}
				});
				if (Window.OK == dialog.open()) {
					return dialog.getValue();
				}
				return null;
			}
		};
		_createRecordAction.setText("Create Record");

		_createComponentAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection =
					(IStructuredSelection) _viewer.getSelection();
				Object selected = selection.getFirstElement();
				if (selected instanceof SubtreeNode) {
					SubtreeNode parent = (SubtreeNode) selected;
					String name = promptForRecordName();
					if (name != null && !name.equals("")) {
						try {
							DirectoryEditor.createComponent(parent, name);
						} catch (DirectoryEditException e) {
							MessageDialog.openError(getSite().getShell(), 
									"Create New Component",
									"Could not create the new component: " + e.getMessage());
						}
						_viewer.refresh(parent);
					}
				}
			}

			private String promptForRecordName() {
				InputDialog dialog = new InputDialog(getSite().getShell(),
						"Create New Component", "Component name:", null,
						new IInputValidator() {
					public String isValid(final String newText) {
						if (newText.equals("")) {
							return "Please enter a name.";
						} else if (newText.indexOf("=") != -1
								|| newText.indexOf("/") != -1
								|| newText.indexOf(",") != -1) {
							return "The following characters are not allowed "
									+ "in names: = / ,";
						} else {
							return null;
						}
					}
				});
				if (Window.OK == dialog.open()) {
					return dialog.getValue();
				}
				return null;
			}
		};
		_createComponentAction.setText("Create Component");

		_deleteNodeAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection =
					(IStructuredSelection) _viewer.getSelection();
				Object selected = selection.getFirstElement();
				if (selected instanceof IAlarmTreeNode) {
					IAlarmTreeNode nodeToDelete = (IAlarmTreeNode) selected;
					SubtreeNode parent = nodeToDelete.getParent();
					try {
						DirectoryEditor.delete(nodeToDelete);
						parent.remove(nodeToDelete);
						_viewer.refresh(parent);
					} catch (DirectoryEditException e) {
						MessageDialog.openError(getSite().getShell(), 
								"Delete",
								"Could not delete this node: " + e.getMessage());
					}
				}
			}
		};
		_deleteNodeAction.setText("Delete");
	}
	
	/**
	 * Passes the focus request to the viewer's control.
	 */
	public final void setFocus() {
		_viewer.getControl().setFocus();
	}
	
	/**
	 * Refreshes this view.
	 */
	public final void refresh(){
		_viewer.refresh();
	}
}
