package org.csstudio.dct.ui.editor;

import java.util.EventObject;

import org.csstudio.dct.export.IRecordRenderer;
import org.csstudio.dct.export.internal.DbFileRecordRenderer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.persistence.Service;
import org.csstudio.dct.ui.editor.outline.internal.OutlinePage;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
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

//FIXME: Editing Components in Enumeration abspeichern und Code verschlanken!
					if (o instanceof Project) {
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
			project = new Project("Project");
			project.addMember(new Folder("Test"));
		}

		return project;
	}

	void createPage0() {
		contentPanel = new Composite(getContainer(), SWT.NONE);
		stackLayout = new StackLayout();
		contentPanel.setLayout(stackLayout);

		projectEditingComponent = new ProjectForm(commandStack);
		projectEditingComponent.createControl(contentPanel);
		
		folderEditingComponent = new FolderForm(commandStack);
		folderEditingComponent.createControl(contentPanel);

		prototypeEditingComponent = new PrototypeForm(commandStack);
		prototypeEditingComponent.createControl(contentPanel);

		instanceEditingComponent = new InstanceForm(commandStack);
		instanceEditingComponent.createControl(contentPanel);

		recordEditingComponent = new RecordForm(commandStack);
		recordEditingComponent.createControl(contentPanel);
		stackLayout.topControl = recordEditingComponent.getMainComposite();

		int index = addPage(contentPanel);
		setPageText(index, "dd");
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
			Service.save(in.getFile(), getProject());
			commandStack.markSaveLocation();
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
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");

		IFile file = ((IFileEditorInput) editorInput).getFile();

		// .. set editor title
		setPartName(file.getName());

		try {
			// .. load the file contents
			project = Service.load(file);
		} catch (Exception e) {
			project = null;
		}

		// .. fallback
		if (project == null) {
			project = new Project("New Project");
		}

		super.init(site, editorInput);
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

		IRecordRenderer renderer = new DbFileRecordRenderer();

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
			result = outline;
		}
		return result;
	}

	public void commandStackChanged(EventObject event) {
		firePropertyChange(PROP_DIRTY);
	}
}
