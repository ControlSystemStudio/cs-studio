package org.csstudio.platform.ui.internal.developmentsupport.util;

import java.util.List;

import org.csstudio.platform.internal.developmentsupport.util.DummyContentModelProvider;
import org.csstudio.platform.internal.developmentsupport.util.TextContainer;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.dnd.DnDUtil;
import org.csstudio.platform.ui.dnd.FilteredDragSourceAdapter;
import org.csstudio.platform.ui.workbench.ControlSystemItemEditorInput;
import org.csstudio.platform.ui.workbench.IWorkbenchIds;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

/**
 * A view, which is used to display and navigate and open editors for workspace
 * resources.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ContainerView extends ViewPart {

	/**
	 * The ID of this view as configured in the plugin manifest.
	 */
	public static final String ID = "org.csstudio.platform.developmentsupport.util.ui.ContainerView"; //$NON-NLS-1$

	/**
	 * The data model that is provided by the tree viewer.
	 */
	private List<IAdaptable> _treeModel = DummyContentModelProvider
			.getInstance().getModel();

	/**
	 * A treeviewer, which is used to display the resources.
	 */
	private TreeViewer _treeViewer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		_treeViewer = new TreeViewer(parent);

		_treeViewer.setContentProvider(new BaseWorkbenchContentProvider() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] getElements(final Object element) {
				return ((List) element).toArray();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] getChildren(final Object element) {
				return super.getChildren(element);
			}
		});

		_treeViewer.setLabelProvider(new WorkbenchLabelProvider());

		_treeViewer.setInput(_treeModel);

		getViewSite().setSelectionProvider(_treeViewer);

		// add drag support
		addDragSupport();

		// add listeners
		configureListeners();

		// create context menu
		configureContextMenu();
	}

	/**
	 * Equip the tree viewer with drag&drop support.
	 */
	private void addDragSupport() {
		FilteredDragSourceAdapter dragSourceListener = new FilteredDragSourceAdapter(
				new Class[] { IProcessVariable.class, IArchiveDataSource.class,
						TextContainer.class/*, ProcessVariable.class*/}) {
			public List getCurrentSelection() {
				return ((IStructuredSelection) _treeViewer.getSelection())
						.toList();
			}
		};

		DnDUtil.enableForDrag(_treeViewer.getTree(), DND.DROP_MOVE
				| DND.DROP_COPY, dragSourceListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		if (_treeViewer != null) {
			_treeViewer.setInput(DummyContentModelProvider.getInstance()
					.getModel());
		}
	}

	/**
	 * Configures all listeners for the TreeViewer.
	 */
	private void configureListeners() {
		_treeViewer.addOpenListener(new IOpenListener() {
			public void open(final OpenEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();

				Object obj = selection.getFirstElement();
				openEditor(obj);
			}
		});
	}

	/**
	 * Configures all listeners for the TreeViewer.
	 */
	private void configureContextMenu() {
		MenuManager menuMgr = new MenuManager("", ID); //$NON-NLS-1$
		menuMgr.add(new GroupMarker(IWorkbenchIds.GROUP_CSS_MB3));
		menuMgr.add(new Separator());
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		menuMgr.setRemoveAllWhenShown(true);

		Menu contextMenu = menuMgr.createContextMenu(_treeViewer.getTree());
		_treeViewer.getTree().setMenu(contextMenu);

		// Register viewer with site. This has to be done before making the
		// actions.
		getViewSite().registerContextMenu(menuMgr, _treeViewer);
		getViewSite().setSelectionProvider(_treeViewer);
	}

	/**
	 * Opens an appropriate editor for the specified object.
	 * 
	 * @param obj
	 *            the object
	 */
	private void openEditor(final Object obj) {
		String query = ""; //$NON-NLS-1$
		IEditorInput editorInput = null;

		if (obj instanceof IControlSystemItem) {
			query = "x." + ((IControlSystemItem) obj).getTypeId(); //$NON-NLS-1$
			editorInput = new ControlSystemItemEditorInput(
					(IControlSystemItem) obj);
		}

		IEditorRegistry editorRegistry = PlatformUI.getWorkbench()
				.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(query);

		if (descriptor != null && editorInput != null) {
			IWorkbenchPage page = getSite().getPage();
			try {
				page.openEditor(editorInput, descriptor.getId());
			} catch (PartInitException e) {
				CentralLogger.getInstance().error("Cannot open editor", e); //$NON-NLS-1$
			}
		} else {
			MessageDialog.openInformation(getSite().getShell(),
					"NoEditorInformationDialogTitle", //$NON-NLS-1$
					"ResourceNavigationView.NoEditorInformationDialogText"); //$NON-NLS-1$
		}
	}
}
