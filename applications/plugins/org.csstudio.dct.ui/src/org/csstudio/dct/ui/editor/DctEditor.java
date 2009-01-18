package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.export.IRecordRenderer;
import org.csstudio.dct.export.internal.DbFileRecordRenderer;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.visitors.ProblemVisitor;
import org.csstudio.dct.model.visitors.SearchVisitor;
import org.csstudio.dct.model.visitors.ProblemVisitor.MarkableError;
import org.csstudio.dct.ui.editor.outline.internal.OutlinePage;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * The DCT Editor implementation.
 * 
 * @author Sven Wende
 * 
 */
public class DctEditor extends MultiPageEditorPart implements CommandStackListener {
	private Project project;
	private CommandStack commandStack;
	private ISelectionChangedListener outlineSelectionListener;
	private ProjectForm projectEditingComponent;
	private FolderForm folderEditingComponent;
	private PrototypeForm prototypeEditingComponent;
	private InstanceForm instanceEditingComponent;
	private RecordForm recordEditingComponent;

	private StackLayout stackLayout;
	private Composite contentPanel;

	private StyledText dbFilePreviewText;

	public DctEditor() {
		super();
		commandStack = new CommandStack();
		commandStack.addCommandStackListener(this);

		outlineSelectionListener = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();

				if (sel != null && sel.getFirstElement() != null) {
					Object o = sel.getFirstElement();

					// FIXME: Editing Components in Enumeration abspeichern und
					// Code verschlanken!
					if (o instanceof IProject) {
						stackLayout.topControl = projectEditingComponent.getMainComposite();
						projectEditingComponent.setInput(o);
					} else if (o instanceof IFolder) {
						stackLayout.topControl = folderEditingComponent.getMainComposite();
						folderEditingComponent.setInput(o);
					} else if (o instanceof IPrototype) {
						stackLayout.topControl = prototypeEditingComponent.getMainComposite();
						prototypeEditingComponent.setInput(o);
					} else if (o instanceof IInstance) {
						stackLayout.topControl = instanceEditingComponent.getMainComposite();
						instanceEditingComponent.setInput(o);
					} else if (o instanceof IRecord) {
						stackLayout.topControl = recordEditingComponent.getMainComposite();
						recordEditingComponent.setInput(o);
					}

					contentPanel.layout();
				}
			}
		};
	}

	private Project getProject() {
		if (project == null) {
			project = new Project("Project", UUID.randomUUID());
			project.addMember(new Folder("Test"));
		}

		return project;
	}

	void createPage0() {
		contentPanel = new Composite(getContainer(), SWT.NONE);
		stackLayout = new StackLayout();
		contentPanel.setLayout(stackLayout);

		projectEditingComponent = new ProjectForm(this);
		projectEditingComponent.createControl(contentPanel);

		folderEditingComponent = new FolderForm(this);
		folderEditingComponent.createControl(contentPanel);

		prototypeEditingComponent = new PrototypeForm(this);
		prototypeEditingComponent.createControl(contentPanel);

		instanceEditingComponent = new InstanceForm(this);
		instanceEditingComponent.createControl(contentPanel);

		recordEditingComponent = new RecordForm(this);
		recordEditingComponent.createControl(contentPanel);

		// .. initially, the project itself is selected
		projectEditingComponent.setInput(getProject());
		stackLayout.topControl = projectEditingComponent.getMainComposite();

		int index = addPage(contentPanel);
		setPageText(index, "Edit");
	}

	void createPage1() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		dbFilePreviewText = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		dbFilePreviewText.setEditable(false);
		dbFilePreviewText.setFont(CustomMediaFactory.getInstance().getFont("Courier", 11, SWT.NORMAL));

		int index = addPage(composite);
		setPageText(index, "Preview DB-File");
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
	}

	public void doSave(IProgressMonitor monitor) {
		FileEditorInput in = (FileEditorInput) getEditorInput();
		try {
			DctActivator.getDefault().getPersistenceService().saveProject(in.getFile(), getProject());
			commandStack.markSaveLocation();
			markErrors();
			firePropertyChange(PROP_DIRTY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		super.init(site, editorInput);

		IFile file = ((IFileEditorInput) editorInput).getFile();

		// .. set editor title
		setPartName(file.getName());

		try {
			// .. load the file contents
			project = DctActivator.getDefault().getPersistenceService().loadProject(file);
		} catch (Exception e) {
			e.printStackTrace();
			project = null;
		}

		// .. fallback
		if (project == null) {
			project = new Project("New Project", UUID.randomUUID());
		}

		// .. refresh markers
		markErrors();
	}

	@Override
	public boolean isDirty() {
		return commandStack.isDirty();
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 1) {
			renderDbFilePreview();
		}
	}

	void renderDbFilePreview() {
		StringBuffer sb = new StringBuffer();

		IRecordRenderer renderer = new DbFileRecordRenderer(false);

		for (IRecord r : getProject().getFinalRecords()) {
			sb.append(renderer.render(r));
			sb.append("\r\n\r\n");
		}

		dbFilePreviewText.setText(sb.toString());
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}

	public Object getAdapter(Class adapter) {
		Object result = null;

		if (adapter == IContentOutlinePage.class) {
			OutlinePage outline = new OutlinePage(getProject(), commandStack);
			outline.setInput(getProject());
			outline.setCommandStack(commandStack);
			outline.addSelectionChangedListener(outlineSelectionListener);
			outline.setSelection(new StructuredSelection(getProject()));
			result = outline;
			getSite().setSelectionProvider(outline);
		} else if (adapter == IGotoMarker.class) {
			return new IGotoMarker() {
				public void gotoMarker(IMarker marker) {
					try {
						String location = (String) marker.getAttribute(IMarker.LOCATION);

						if (StringUtil.hasLength(location)) {
							UUID id = UUID.fromString(location);
							selectItemInOutline(id);
						}
					} catch (CoreException e) {
						CentralLogger.getInstance().info(this, e);
					}
				}
			};
		}
		return result;
	}

	public void commandStackChanged(EventObject event) {
		firePropertyChange(PROP_DIRTY);
		// FIXME: Auskommentieren, wenn Markierung nach jeder Änderung gewünscht
		// und performant genug!!
		// markErrors();
	}

	private void markErrors() {
		// .. find problems
		ProblemVisitor visitor = new ProblemVisitor();
		getProject().accept(visitor);
		Set<MarkableError> errors = visitor.getErrors();

		IFile file = ((IFileEditorInput) getEditorInput()).getFile();

		try {
			// .. clear old markers
			file.deleteMarkers(IMarker.PROBLEM, true, 1);

			// .. add new markers
			for (MarkableError e : errors) {
				IMarker marker = file.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				marker.setAttribute(IMarker.LOCATION, e.getId().toString());
				marker.setAttribute(IMarker.MESSAGE, e.getErrorMessage());
			}
		} catch (CoreException e) {
			CentralLogger.getInstance().info(this, e);
		}
	}

	public void selectItemInOutline(UUID id) {
		IElement element = new SearchVisitor().search(project, id);

		if (element != null) {
			List<Object> path = new ArrayList<Object>();
			findPathToRoot(element, path);
			getSite().getSelectionProvider().setSelection(new TreeSelection(new TreePath(path.toArray())));
		}
	}

	private void findPathToRoot(IElement element, List<Object> path) {
		if (element != null) {
			path.add(0, element);

			if (element instanceof IRecord) {
				IRecord record = (IRecord) element;
				findPathToRoot(record.getContainer(), path);
			} else if (element instanceof IContainer) {
				IContainer container = (IContainer) element;

				if (container.getContainer() != null) {
					findPathToRoot(container.getContainer(), path);
				} else {
					findPathToRoot(container.getParentFolder(), path);
				}
			} else if (element instanceof IFolder) {
				IFolder folder = (IFolder) element;
				findPathToRoot(folder.getParentFolder(), path);
			}
		}
	}
}
