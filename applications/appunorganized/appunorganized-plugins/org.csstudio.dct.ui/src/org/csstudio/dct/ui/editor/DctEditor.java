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
import org.csstudio.dct.ui.editor.highlighter.EpicsDBSyntaxHighlighterImpl;
import org.csstudio.dct.ui.editor.highlighter.IEpicsDBSyntaxHighlighter;
import org.csstudio.dct.ui.editor.outline.internal.OutlinePage;
import org.csstudio.domain.common.strings.StringUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
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
import org.csstudio.domain.common.LayoutUtil;
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

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 01.08.2011
     */
    private final class SearchListener implements SelectionListener {
        private final Text _searchBox;

        /**
         * Constructor.
         */
        public SearchListener(final Text searchBox) {
            _searchBox = searchBox;
        }

        @Override
        public void widgetSelected(final SelectionEvent e) {
            search();
        }

        private void search() {
            searchAndMarkInPreview(_searchBox.getText(), true, isCaseSensitive());
        }

        @Override
        public void widgetDefaultSelected(final SelectionEvent e) {
            search();
        }
    }


    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 01.08.2011
     */
    private final class SearchModifyListener implements ModifyListener {
        private final Text _searchBox;
        Color red = Display.getDefault().getSystemColor(SWT.COLOR_RED);
        Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
        Color black = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

        /**
         * Constructor.
         */
        private SearchModifyListener(final Text searchBox) {
            _searchBox = searchBox;
        }

        @Override
        public void modifyText(final ModifyEvent e) {

            final boolean found = searchAndMarkInPreview(_searchBox.getText(),
                    false, isCaseSensitive());
            if(found) {
                _searchBox.setBackground(white);
                _searchBox.setForeground(black);
            } else {
                _searchBox.setForeground(white);
                _searchBox.setBackground(red);
            }
        }
    }

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

    private boolean _caseSensitive;

    private Text _searchBox;

    /**
     * Constructor.
     */
    public DctEditor() {
        super();
        commandStack = new CommandStack();
        commandStack.addCommandStackListener(this);

        outlineSelectionListener = new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final IStructuredSelection sel = (IStructuredSelection) event.getSelection();

                if (sel != null && sel.getFirstElement() != null) {
                    final Object o = sel.getFirstElement();

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

        final int index = addPage(contentPanel);
        setPageText(index, "Edit");
    }

    void createPage1() {
        final Composite composite = new Composite(getContainer(), SWT.NONE);
        composite.setLayout(LayoutUtil.createGridLayout(1, 5, 5, 5, 5, 5, 5));

        final Composite c = new Composite(composite, SWT.NONE);
        c.setLayoutData(LayoutUtil.createGridData());
        final GridLayout gridLayout = GridLayoutFactory.swtDefaults().numColumns(5).create();
//        FillLayout layout = new FillLayout();
//        layout.spacing=5;
//        c.setLayout(layout);
        c.setLayout(gridLayout);

        final CCombo list = new CCombo(c, SWT.READ_ONLY | SWT.BORDER);
        final GridDataFactory swtDefaults = GridDataFactory.swtDefaults();
        list.setLayoutData(swtDefaults.create());
        final ComboViewer viewer = new ComboViewer(list);
        viewer.getCCombo().setEditable(false);
        viewer.getCCombo().setVisibleItemCount(20);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        final List<ExporterDescriptor> exporterExtensions = Extensions.lookupExporterExtensions();
        viewer.setInput(exporterExtensions);

        // .. choose default exporter
        for(final ExporterDescriptor d : exporterExtensions) {
            if(d.isStandard()) {
                exporterDescriptor = d;
                viewer.setSelection(new StructuredSelection(exporterDescriptor));
            }
        }

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                final ExporterDescriptor descriptor = (ExporterDescriptor) sel.getFirstElement();
                exporterDescriptor = descriptor;
                updatePreview();
            }
        });

        _searchBox = new Text(c, SWT.SEARCH);
        _searchBox.setMessage("Search");
        _searchBox.setLayoutData(GridDataFactory.fillDefaults().hint(200, 0).create());
        _searchBox.addModifyListener(new SearchModifyListener(_searchBox));
        _searchBox.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(final KeyEvent e) {
                if(e.keyCode==SWT.KEYPAD_CR||e.keyCode==SWT.CR||e.keyCode==SWT.F3) {
                    searchAndMarkInPreview(_searchBox.getText(), true, isCaseSensitive());
                }
            }
        });



        final Button searchButton = new Button(c, SWT.NONE);
        searchButton.setLayoutData(swtDefaults.create());
        searchButton.addSelectionListener(new SearchListener(_searchBox));
        searchButton.setText("Search");

        final Button caseSensitiveButton = new Button(c, SWT.CHECK);
        caseSensitiveButton.setLayoutData(swtDefaults.create());
        caseSensitiveButton.setText("Case Sensitive");
        caseSensitiveButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                setCaseSensitive(caseSensitiveButton.getSelection());
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                setCaseSensitive(caseSensitiveButton.getSelection());
            }
        });


        final Button saveToFileButton = new Button(c, SWT.NONE);
        saveToFileButton.setLayoutData(swtDefaults.grab(true, false).align(SWT.END, SWT.CENTER).create());
        saveToFileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(final MouseEvent event) {
                if (exporterDescriptor != null) {
                    final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
                    final String path = dialog.open();

                    if (path != null) {
                        try {
                            File file = new File(path);

                            if (!file.exists()) {
                                file.createNewFile();
                            }

                            if (file.canWrite()) {
                                final FileWriter writer = new FileWriter(file);
                                writer.write(exporterDescriptor.getExporter().export(getProject()));
                                writer.close();
                            }

                            file = null;
                        } catch (final IOException e) {
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
        dbFilePreviewText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(final KeyEvent e) {
                if(e.keyCode==SWT.F3) {
                    searchAndMarkInPreview(_searchBox.getText(), true, isCaseSensitive());
                }
            }
        });
        final int index = addPage(composite);
        setPageText(index, "Preview DB-File");
    }

    protected boolean searchAndMarkInPreview(final String criteria2, final boolean startFromCaret, final boolean caseSensitiv) {
        boolean found = true;
        if (criteria2 != null && criteria2.length() > 0) {
            final int offset = startFromCaret?dbFilePreviewText.getCaretOffset() : 0;
            String text;
            String criteria;
            if(caseSensitiv) {
               text = dbFilePreviewText.getText();
               criteria = criteria2;
            } else {
                text = dbFilePreviewText.getText().toLowerCase();
                criteria = criteria2.toLowerCase();
            }
            final int index = text.substring(offset).indexOf(criteria);

            if(index>-1) {
                final int pos = offset + index;
                dbFilePreviewText.setSelection(pos, pos + criteria.length());
                found = true;
            } else if(startFromCaret) {
                found = searchAndMarkInPreview(criteria, false, caseSensitiv);
            } else {
                found = false;
            }
        }
        return found;
    }

    protected boolean isCaseSensitive() {
        return _caseSensitive;
    }

    protected void setCaseSensitive(final boolean caseSensitive) {
        _caseSensitive = caseSensitive;
    }
    /**
     * Updates the preview of the db file.
     */
    private void updatePreview() {
        if (exporterDescriptor != null) {
            final String export = exporterDescriptor.getExporter().export(getProject());
            final StyleRange[] ranges = buildStyleRange(export);
            dbFilePreviewText.setText(export);
            dbFilePreviewText.setStyleRanges(ranges);
        }
        _searchBox.setFocus();

    }

    private StyleRange[] buildStyleRange(final String export) {
        final IEpicsDBSyntaxHighlighter highlighter = new EpicsDBSyntaxHighlighterImpl();
        highlighter.append(export);
        return highlighter.getStyleRange();
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
    public void doSave(final IProgressMonitor monitor) {
        final FileEditorInput in = (FileEditorInput) getEditorInput();
        try {
            DctActivator.getDefault().getPersistenceService().saveProject(in.getFile(), getProject());
            commandStack.markSaveLocation();
            markErrorsAndWarnings();
            firePropertyChange(PROP_DIRTY);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void doSaveAs() {
        final IEditorPart editor = getEditor(0);
        editor.doSaveAs();
        setPageText(0, editor.getTitle());
        setInput(editor.getEditorInput());
    }

    @Override
    protected void pageChange(final int newPageIndex) {
        if (newPageIndex == 1) {
            updatePreview();
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput editorInput) throws PartInitException {
        if (!(editorInput instanceof IFileEditorInput)) {
            throw new PartInitException("Invalid Input: Must be IFileEditorInput");
        }
        super.init(site, editorInput);

        final IFile file = ((IFileEditorInput) editorInput).getFile();

        // .. set editor title
        setPartName(file.getName());

        try {
            // .. load the file contents
            project = DctActivator.getDefault().getPersistenceService().loadProject(file);
        } catch (final Exception e) {
            LOG.error("Error: ", e);
            project = null;
        }

        // .. fallback
        if (project == null) {
            project = ProjectFactory.createNewDCTProject();
        }

        // .. refresh markers
        markErrorsAndWarnings();
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
    public Object getAdapter(final Class adapter) {
        Object result = null;

        if (adapter == IContentOutlinePage.class) {
            final OutlinePage outline = new OutlinePage(getProject(), commandStack);
            outline.setInput(getProject());
            outline.setCommandStack(commandStack);
            outline.addSelectionChangedListener(outlineSelectionListener);
            outline.setSelection(new StructuredSelection(getProject()));
            result = outline;
            getSite().setSelectionProvider(outline);
        } else if (adapter == IGotoMarker.class) {
            return new IGotoMarker() {
                @Override
                public void gotoMarker(final IMarker marker) {
                    try {
                        final String location = (String) marker.getAttribute(IMarker.LOCATION);

                        if (StringUtil.hasLength(location)) {
                            final UUID id = UUID.fromString(location);
                            selectItemInOutline(id);
                        }
                    } catch (final CoreException e) {
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
    @Override
    public void commandStackChanged(final EventObject event) {
        firePropertyChange(PROP_DIRTY);
        markErrorsAndWarnings();

        if (getActivePage() == 1) {
            updatePreview();
        }
    }


    private void markErrorsAndWarnings() {
        // .. find problems
        final ProblemVisitor visitor = new ProblemVisitor();
        getProject().accept(visitor);
        final Set<MarkableError> errors = visitor.getErrors();
        final Set<MarkableError> warnigs = visitor.getWarnnings();

        final IFile file = ((IFileEditorInput) getEditorInput()).getFile();

        try {
            // .. clear old markers
            file.deleteMarkers(IMarker.PROBLEM, true, 1);

            // .. add new error markers
            for (final MarkableError e : errors) {
                final IMarker marker = file.createMarker(IMarker.PROBLEM);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.LOCATION, e.getId().toString());
                marker.setAttribute(IMarker.MESSAGE, e.getErrorMessage());
            }

            // .. add new warning markers
            for (final MarkableError e : warnigs) {
                final IMarker marker = file.createMarker(IMarker.PROBLEM);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                marker.setAttribute(IMarker.LOCATION, e.getId().toString());
                marker.setAttribute(IMarker.MESSAGE, e.getErrorMessage());
            }

            // .. mark file as changed
            file.touch(new NullProgressMonitor());

            // .. save workspace changes to persist the new markers
            ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());

        } catch (final CoreException e) {
            LOG.info("Info:", e);
        }
    }

    /**
     * Selects the model element with the specified id in the outline view.
     *
     * @param id
     *            the element id
     */
    public void selectItemInOutline(final UUID id) {
        final IElement element = new SearchVisitor().search(project, id);

        if (element != null) {
            final List<Object> path = new ArrayList<Object>();
            findPathToRoot(element, path);
            getSite().getSelectionProvider().setSelection(new TreeSelection(new TreePath(path.toArray())));
        }
    }

    private void findPathToRoot(final IElement element, final List<Object> path) {
        if (element != null) {
            path.add(0, element);

            if (element instanceof IRecord) {
                final IRecord record = (IRecord) element;
                findPathToRoot(record.getContainer(), path);
            } else if (element instanceof IContainer) {
                final IContainer container = (IContainer) element;

                if (container.getContainer() != null) {
                    findPathToRoot(container.getContainer(), path);
                } else {
                    findPathToRoot(container.getParentFolder(), path);
                }
            } else if (element instanceof IFolder) {
                final IFolder folder = (IFolder) element;
                findPathToRoot(folder.getParentFolder(), path);
            }
        }
    }
}
