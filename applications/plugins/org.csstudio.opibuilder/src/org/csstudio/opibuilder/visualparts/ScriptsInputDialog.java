package org.csstudio.opibuilder.visualparts;

import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.properties.support.ScriptData;
import org.csstudio.opibuilder.properties.support.ScriptsInput;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.properties.ActionDataCellEditor.ActionDataDialog.TypeAction;
import org.eclipse.draw2d.GridData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ScriptsInputDialog extends TitleAreaDialog {
	
	private Action addAction;
	private Action removeAction;
	private Action moveUpAction;
	private Action moveDownAction;
	
	private TableViewer scriptsViewer;
	private TableViewer pvsViewer;
	
	
	private List<ScriptData> scriptDataList;	
	public ScriptsInputDialog(Shell parentShell, ScriptsInput scriptsInput, String dialogTitle) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.scriptDataList = scriptsInput.getCopy().getScriptList();
		setTitle(dialogTitle);
	}
	
	/**
	 * Creates a label with the given text.
	 * 
	 * @param parent
	 *            The parent for the label
	 * @param text
	 *            The text for the label
	 */
	private void createLabel(final Composite parent, final String text) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,
				false, 2, 1));
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite parent_Composite = (Composite) super.createDialogArea(parent);
		
		final Composite mainComposite = new Composite(parent_Composite, SWT.None);		
		mainComposite.setLayout(new GridLayout(2, false));
		
		final Composite leftComposite = new Composite(mainComposite, SWT.None);
		leftComposite.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 200;
		leftComposite.setLayoutData(gd);
		createLabel(leftComposite, "The Scripts:");
		
		Composite toolBarComposite = new Composite(leftComposite, SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginBottom = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		toolBarComposite.setLayout(gridLayout);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 200;
		toolBarComposite.setLayoutData(gd);
		
		ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
		GridData grid = new GridData();
		grid.horizontalAlignment = GridData.FILL;
		grid.verticalAlignment = GridData.BEGINNING;
		toolBar.setLayoutData(grid);
		toolbarManager.add(addAction);
		toolbarManager.add(removeAction);
		toolbarManager.add(moveUpAction);
		toolbarManager.add(moveDownAction);
		toolbarManager.update(true);
		
		
		
		
		
		return parent_Composite;
		
		
	}
	
	/**
	 * Refreshes the enabled-state of the actions.
	 */
	private void refreshGUI() {
		
		IStructuredSelection selection = (IStructuredSelection) scriptsViewer
				.getSelection();
		if (!selection.isEmpty()
				&& selection.getFirstElement() instanceof ScriptData) {
			removeAction.setEnabled(true);
			moveUpAction.setEnabled(true);
			moveDownAction.setEnabled(true);
			pvsViewer.setInput(((ScriptData) selection
					.getFirstElement()).getPVList());
		} else {
			removeAction.setEnabled(false);
			moveUpAction.setEnabled(false);
			moveDownAction.setEnabled(false);
		}
	}
	
	
	
	
	/**
	 * Creates and configures a {@link TableViewer}.
	 * 
	 * @param parent
	 *            The parent for the table
	 * @return The {@link TableViewer}
	 */
	private TableViewer createActionTableViewer(final Composite parent) {
		TableViewer viewer = new TableViewer(parent, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
		viewer.setContentProvider(new BaseWorkbenchContentProvider() {
			@Override
			public Object[] getElements(final Object element) {
				return (((List<ScriptData>)element).toArray());
			}
		});
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				refreshPVs();
			}
		});
		viewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getTable().setMenu(this.createMenu(viewer.getTable(), true));
		return viewer;
	}
	
	
	
	
	/**
	 * Creates the actions.
	 */
	private void createActions() {
		// _addAction = new Action("Add") {
		// @Override
		// public void run() {
		// _actionMenu.setVisible(true);
		// while(!_actionMenu.isDisposed() && _actionMenu.isVisible()){
		// // FIXME: SWENDE: Was ist das denn hier? Sieht böse aus!
		// if(!Display.getCurrent().readAndDispatch()){
		// Display.getCurrent().sleep();
		// }
		// }
		// _actionMenu.setVisible(false);
		// }
		// };
		addAction = new Action("Add") {
			@Override
			public void run() {
				// System.out.println(".createActions()");
			}
		};

	
		addAction.setToolTipText("Adds an action");
		addAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/add.gif"));
		
		removeAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) actionViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof ScriptData) {
					scriptDataList.remove((ScriptData)selection.getFirstElement());
					refreshActionViewer(null);
					this.setEnabled(false);
				}
			}
		};
		removeAction.setText("Remove Action");
		removeAction
				.setToolTipText("Removes the selected Action from the list");
		removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
						"icons/delete.gif"));
		removeAction.setEnabled(false);

		moveUpAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) _actionViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof AbstractWidgetActionModel) {
					AbstractWidgetActionModel widgetAction = (AbstractWidgetActionModel) selection
							.getFirstElement();
					_actionData.upAction(widgetAction);
					refreshActionViewer(widgetAction);
					this.setEnabled(false);
				}
			}
		};
		moveUpAction.setText("Move Up Action");
		moveUpAction.setToolTipText("Move up the selected Action");
		moveUpAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
						"icons/search_prev.gif"));
		moveUpAction.setEnabled(false);

		moveDownAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) _actionViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof AbstractWidgetActionModel) {
					AbstractWidgetActionModel widgetAction = (AbstractWidgetActionModel) selection
							.getFirstElement();
					_actionData.downAction(widgetAction);
					refreshActionViewer(widgetAction);
					this.setEnabled(false);
				}
			}
		};
		moveDownAction.setText("Move Down Action");
		moveDownAction.setToolTipText("Move down the selected Action");
		moveDownAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
						"icons/search_next.gif"));
		moveDownAction.setEnabled(false);
	}
	
	

	

}
