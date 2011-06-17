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
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.internal.ProjectFactory;
import org.csstudio.dct.model.visitors.ProblemVisitor;
import org.csstudio.dct.model.visitors.ProblemVisitor.MarkableError;
import org.csstudio.dct.model.visitors.SearchVisitor;
import org.csstudio.dct.ui.editor.outline.internal.OutlinePage;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DCT Editor implementation.
 * 
 * @author Sven Wende
 * 
 */
public final class DctEditor extends MultiPageEditorPart implements CommandStackListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(DctEditor.class);
    
	private Project project;
	private final CommandStack commandStack;
	private final ISelectionChangedListener outlineSelectionListener;
	private ProjectForm projectForm;
	private FolderForm folderForm;
	private PrototypeForm prototypeForm;
	private InstanceForm instanceForm;
	private RecordForm recordForm;
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

					// .. set form inputs
					projectForm.setInput(o instanceof IProject ? o : null);
					folderForm.setInput(o instanceof IFolder ? o : null);
					prototypeForm.setInput(o instanceof IPrototype ? o : null);
					instanceForm.setInput(o instanceof IInstance ? o : null);
					recordForm.setInput(o instanceof IRecord ? o : null);

					// .. bring current form to top
					if (o instanceof IProject) {
						stackLayout.topControl = projectForm.getMainComposite();
					} else if (o instanceof IFolder) {
						stackLayout.topControl = folderForm.getMainComposite();
					} else if (o instanceof IPrototype) {
						stackLayout.topControl = prototypeForm.getMainComposite();
					} else if (o instanceof IInstance) {
						stackLayout.topControl = instanceForm.getMainComposite();
					} else if (o instanceof IRecord) {
						stackLayout.topControl = recordForm.getMainComposite();
					}

					contentPanel.layout();
				}
			}
		};
	}

	public Project getProject() {
		if (project == null) {
			project = ProjectFactory.createNewDCTProject();
			project.addMember(new Folder("Test"));
		}

		return project;
	}

	void createPage0() {
		contentPanel = new Composite(getContainer(), SWT.NONE);
		stackLayout = new StackLayout();
		contentPanel.setLayout(stackLayout);

		projectForm = new ProjectForm(this);
		projectForm.createControl(contentPanel);

		folderForm = new FolderForm(this);
		folderForm.createControl(contentPanel);

		prototypeForm = new PrototypeForm(this);
		prototypeForm.createControl(contentPanel);

		instanceForm = new InstanceForm(this);
		instanceForm.createControl(contentPanel);

		recordForm = new RecordForm(this);
		recordForm.createControl(contentPanel);

		// .. initially, the project itself is selected
		projectForm.setInput(getProject());
		stackLayout.topControl = projectForm.getMainComposite();

		int index = addPage(contentPanel);
		setPageText(index, "Edit");
	}

	void createPage1() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		composite.setLayout(LayoutUtil.createGridLayout(1, 5, 5, 5, 5, 5, 5));

		Composite c = new Composite(composite, SWT.NONE);
		c.setLayoutData(LayoutUtil.createGridData());
		FillLayout layout = new FillLayout();
		layout.spacing=5;
		c.setLayout(layout);

		ComboViewer viewer = new ComboViewer(new CCombo(c, SWT.READ_ONLY | SWT.BORDER));
		viewer.getCCombo().setEditable(false);
		viewer.getCCombo().setVisibleItemCount(20);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		List<ExporterDescriptor> exporterExtensions = Extensions.lookupExporterExtensions();
		viewer.setInput(exporterExtensions);
		
		// .. choose default exporter
		for(ExporterDescriptor d : exporterExtensions) {
			if(d.isStandard()) {
				exporterDescriptor = d;
				viewer.setSelection(new StructuredSelection(exporterDescriptor));
			}
		}

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				ExporterDescriptor descriptor = (ExporterDescriptor) sel.getFirstElement();
				exporterDescriptor = descriptor;
				updatePreview();
			}
		});

		final Text searchBox = new Text(c, SWT.None);
		searchBox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				searchAndMarkInPreview(searchBox.getText(), false);
			}
		});

		Button searchButton = new Button(c, SWT.NONE);
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				searchAndMarkInPreview(searchBox.getText(), true);
			}
		});
		searchButton.setText("Search");

		Button saveToFileButton = new Button(c, SWT.NONE);
		saveToFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent event) {
				if (exporterDescriptor != null) {
					FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
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

		// saveToFileButton.setLayoutData(LayoutUtil.createGridData());
		saveToFileButton.setText("Save To File");

		dbFilePreviewText = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		dbFilePreviewText.setLayoutData(LayoutUtil.createGridDataForFillingCell());
		dbFilePreviewText.setEditable(false);
		dbFilePreviewText.setFont(CustomMediaFactory.getInstance().getFont("Courier", 11, SWT.NORMAL));

		int index = addPage(composite);
		setPageText(index, "Preview DB-File");
	}

	private void searchAndMarkInPreview(String criteria, boolean startFromCaret) {

		if (criteria != null && criteria.length() > 0) {
			int offset = startFromCaret?dbFilePreviewText.getCaretOffset() : 0;
			String text = dbFilePreviewText.getText();
			int index = text.substring(offset).indexOf(criteria);
			
			if(index>-1) {
				int pos = offset + index;
				dbFilePreviewText.setSelection(pos, pos + criteria.length());
			} else {
				searchAndMarkInPreview(criteria, false);
			}
		}

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
	@Override
    protected void createPages() {
		createPage0();
		createPage1();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
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
	@Override
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
	@Override
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
			LOG.error("Error: ", e);
			project = null;
		}

		// .. fallback
		if (project == null) {
			project = ProjectFactory.createNewDCTProject();
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
	@Override
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
	@Override
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
						LOG.info("Info: ", e);
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
			
			// .. mark file as changed
			file.touch(new NullProgressMonitor());
			
			// .. save workspace changes to persist the new markers
			ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());
			
		} catch (CoreException e) {
			LOG.info("Info:", e);
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
