package org.csstudio.dct.ui.editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.export.ExporterDescriptor;
import org.csstudio.dct.export.Extensions;
import org.csstudio.dct.export.IExporter;
import org.csstudio.dct.export.internal.AdvancedDbFileExporter;
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
import org.csstudio.platform.ui.util.LayoutUtil;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
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
public final class DctEditor extends MultiPageEditorPart implements CommandStackListener {
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
	private ExporterDescriptor exporterDescriptor;

	/**
	 * Constructor.
	 */
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
		composite.setLayout(LayoutUtil.createGridLayout(1, 5, 5, 5, 5, 5, 5));

		Composite c = new Composite(composite, SWT.NONE);
		c.setLayoutData(LayoutUtil.createGridData());
		c.setLayout(new FillLayout());

		Label label = new Label(c, SWT.NONE);
		label.setText("Choose Format:");
		ComboViewer viewer = new ComboViewer(new CCombo(c, SWT.READ_ONLY | SWT.BORDER));
		viewer.getCCombo().setEditable(false);
		viewer.getCCombo().setVisibleItemCount(20);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(Extensions.lookupExporterExtensions().toArray());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				ExporterDescriptor descriptor = (ExporterDescriptor) sel.getFirstElement();
				exporterDescriptor = descriptor;
				updatePreview();
			}
		});

		Button saveToFileButton = new Button(c, SWT.NONE);
		saveToFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent event) {
				if (exporterDescriptor != null) {
					FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
					String path = dialog.open();

					if (path != null) {
						try {
							File file = new File(path);

							if (!file.exists()) {
								file.createNewFile();
							}

							if (file.canWrite()) {
								FileWriter writer = new FileWriter(file);
								writer.write(exporterDescriptor.getExporter().export(getProject()));
								writer.close();
							}

							file = null;
						} catch (IOException e) {
							MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Export not possible", e.getMessage());
						}
					}

				}
			}
		});
		
//		saveToFileButton.setLayoutData(LayoutUtil.createGridData());
		saveToFileButton.setText("Save To File");
		

		dbFilePreviewText = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		dbFilePreviewText.setLayoutData(LayoutUtil.createGridDataForFillingCell());
		dbFilePreviewText.setEditable(false);
		dbFilePreviewText.setFont(CustomMediaFactory.getInstance().getFont("Courier", 11, SWT.NORMAL));

		int index = addPage(composite);
		setPageText(index, "Preview DB-File");
	}

	/**
	 * Updates the preview of the db file.
	 */
	private void updatePreview() {
		if (exporterDescriptor != null) {
			dbFilePreviewText.setText(exporterDescriptor.getExporter().export(getProject()));
		}

	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
	}

	/**
	 *{@inheritDoc}
	 */
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

	/**
	 *{@inheritDoc}
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	@Override
	protected void pageChange(int newPageIndex) {
		if (newPageIndex == 1) {
			updatePreview();
		}
	}

	/**
	 *{@inheritDoc}
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

	/**
	 *{@inheritDoc}
	 */
	@Override
	public boolean isDirty() {
		return commandStack.isDirty();
	}

	/**
	 *{@inheritDoc}
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Returns the command stack used by this editor.
	 * 
	 * @return the command stack used by this editor
	 */
	public CommandStack getCommandStack() {
		return commandStack;
	}

	/**
	 *{@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
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

	/**
	 *{@inheritDoc}
	 */
	public void commandStackChanged(EventObject event) {
		firePropertyChange(PROP_DIRTY);
		markErrors();

		if (getActivePage() == 1) {
			updatePreview();
		}
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

	/**
	 * Selects the model element with the specified id in the outline view.
	 * 
	 * @param id
	 *            the element id
	 */
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
