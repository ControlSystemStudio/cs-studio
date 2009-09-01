package org.csstudio.dct.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.visitors.SearchInstancesVisitor;
import org.csstudio.dct.ui.editor.DctEditor;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;

public class HierarchyView extends ViewPart implements IPartListener, ISelectionListener {
	private DctEditor editor;
	private TreeViewer treeViewer;
	private Set<UUID> visibleIds;

	public HierarchyView() {
		visibleIds = new HashSet<UUID>();
	}

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent);
		treeViewer.getTree().setLayoutData(LayoutUtil.createGridDataForFillingCell(200, 400));
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setAutoExpandLevel(4);

		treeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				boolean result = false;

				if (element instanceof IFolder) {
					result = true;
				} else if (element instanceof IElement) {
					result = visibleIds.contains(((IElement) element).getId());
				}

				return result;
			}
		});

		treeViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				StructuredSelection sel = (StructuredSelection) event.getSelection();

				IElement e = (IElement) sel.getFirstElement();

				if (!(e instanceof IFolder)) {
					editor.selectItemInOutline(e.getId());
				}
			}
		});

		getSite().getPage().addPartListener(this);

		getSite().getPage().addSelectionListener("org.eclipse.ui.views.ContentOutline", this);
	}

	@Override
	public void setFocus() {

	}

	public void partActivated(IWorkbenchPart part) {
		if (part instanceof DctEditor) {
			editor = (DctEditor) part;
			treeViewer.setInput(editor.getProject());
			selectionChanged(null, null);
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {

	}

	public void partClosed(IWorkbenchPart part) {

	}

	public void partDeactivated(IWorkbenchPart part) {

	}

	public void partOpened(IWorkbenchPart part) {

	}

	private IPrototype currentPrototype;

	public void selectionChanged(IWorkbenchPart part, ISelection s2) {
		ISelection selection = getSite().getPage().getSelection("org.eclipse.ui.views.ContentOutline");

		IStructuredSelection sel = (IStructuredSelection) selection;

		Object e = sel.getFirstElement();

		if (sel != null && e != null && e != currentPrototype) {

			currentPrototype = null;

			if (e instanceof IPrototype) {
				currentPrototype = (IPrototype) e;
			} else if (e instanceof IInstance) {
				currentPrototype = ((IInstance) e).getPrototype();
			} else if (e instanceof IRecord) {
				IRecord r = (IRecord) e;

				if (r.getContainer() instanceof IPrototype) {
					currentPrototype = (IPrototype) r.getContainer();
				} else if (r.getContainer() instanceof IInstance) {
					currentPrototype = ((IInstance) r.getContainer()).getPrototype();
				}
			}

			if (currentPrototype != null) {
				setPartName("Instances of [" + currentPrototype.getName() + "]");

				List<IInstance> instances = new SearchInstancesVisitor().search(editor.getProject(), currentPrototype.getId());

				visibleIds = new HashSet<UUID>();
				for (IInstance instance : instances) {
					visibleIds.add(instance.getId());
					if (instance.getContainer() != null) {
						visibleIds.add(instance.getContainer().getId());
					}
				}

				treeViewer.refresh();
				treeViewer.expandAll();
			}
		}
	}
}
