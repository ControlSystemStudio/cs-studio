package de.desy.language.snl.diagram.ui;

import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.desy.language.editor.core.parser.AbstractLanguageParser;
import de.desy.language.editor.core.parser.Node;
import de.desy.language.snl.diagram.model.SNLDiagram;
import de.desy.language.snl.diagram.persistence.IPersistenceHandler;
import de.desy.language.snl.diagram.persistence.StateLayoutData;
import de.desy.language.snl.diagram.persistence.XMLPersistenceHandler;
import de.desy.language.snl.diagram.ui.parts.ShapesEditPartFactory;
import de.desy.language.snl.diagram.ui.parts.ShapesTreeEditPartFactory;
import de.desy.language.snl.parser.SNLParser;

public class DiagramEditor extends GraphicalEditor {
//public class DiagramEditor extends GraphicalEditorWithFlyoutPalette {

    private static final int ROUTING_SEPARATION = 20;

    /** This is the root of the editor's model. */
    private SNLDiagram diagram;
    private IDocumentProvider _implicitDocumentProvider;
//    /** Palette component, holding the tools and shapes. */
//    private static PaletteRoot PALETTE_MODEL;
    private ScalableFreeformRootEditPart _scalableFreeformRootEditPart;
    private final IPersistenceHandler _persistenceHandler;

    /** Create a new DiagrammEditor instance. This is called by the Workspace. */
    public DiagramEditor() {
        setEditDomain(new DefaultEditDomain(this));
        _persistenceHandler = new XMLPersistenceHandler();
    }

    /**
     * Configure the graphical viewer before it receives contents.
     * <p>
     * This is the place to choose an appropriate RootEditPart and
     * EditPartFactory for your editor. The RootEditPart determines the behavior
     * of the editor's "work-area". For example, GEF includes zoomable and
     * scrollable root edit parts. The EditPartFactory maps model elements to
     * edit parts (controllers).
     * </p>
     *
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
     */
    @Override
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();

        final GraphicalViewer viewer = getGraphicalViewer();
        viewer.setEditPartFactory(new ShapesEditPartFactory());
        _scalableFreeformRootEditPart = new ScalableFreeformRootEditPart();
        viewer.setRootEditPart(_scalableFreeformRootEditPart);
        viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

        // configure the context menu provider
        final ContextMenuProvider cmProvider = new ShapesEditorContextMenuProvider(
                viewer, getActionRegistry());
        viewer.setContextMenu(cmProvider);
        getSite().registerContextMenu(cmProvider, viewer);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util
     * .EventObject)
     */
    @Override
    public void commandStackChanged(final EventObject event) {
        firePropertyChange(IEditorPart.PROP_DIRTY);
        super.commandStackChanged(event);
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @seeorg.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#
//     * createPaletteViewerProvider()
//     */
//    @Override
//    protected PaletteViewerProvider createPaletteViewerProvider() {
//        return new PaletteViewerProvider(getEditDomain()) {
//            @Override
//            protected void configurePaletteViewer(final PaletteViewer viewer) {
//                super.configurePaletteViewer(viewer);
//                // create a drag source listener for this palette viewer
//                // together with an appropriate transfer drop target listener,
//                // this will enable
//                // model element creation by dragging a
//                // CombinatedTemplateCreationEntries
//                // from the palette into the editor
//                // @see ShapesEditor#createTransferDropTargetListener()
//                viewer
//                        .addDragSourceListener(new TemplateTransferDragSourceListener(
//                                viewer));
//            }
//        };
//    }

//    /**
//     * Create a transfer drop target listener. When using a
//     * CombinedTemplateCreationEntry tool in the palette, this will enable model
//     * element creation by dragging from the palette.
//     *
//     * @see #createPaletteViewerProvider()
//     */
//    private TransferDropTargetListener createTransferDropTargetListener() {
//        return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
//            @Override
//            protected CreationFactory getFactory(final Object template) {
//                return new SimpleFactory((Class) template);
//            }
//        };
//    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor
     * )
     */
    @Override
    public void doSave(final IProgressMonitor monitor) {
        final IPath path = ((IFileEditorInput) getEditorInput()).getFile()
                .getFullPath();
        try {
            _persistenceHandler.store(path, diagram);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCommandStack().markSaveLocation();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
    }

    // @Override
    // public boolean isDirty() {
    // return false;
    // }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class type) {
        if (type == IContentOutlinePage.class)
            return new ShapesOutlinePage(new TreeViewer());
        return super.getAdapter(type);
    }

    SNLDiagram getModel() {
        return diagram;
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot
//     * ()
//     */
//    @Override
//    protected PaletteRoot getPaletteRoot() {
//        if (PALETTE_MODEL == null)
//            PALETTE_MODEL = ShapesEditorPaletteFactory.createPalette();
//        return PALETTE_MODEL;
//    }

    /**
     * Set up the editor's inital content (after creation).
     *
     */
    @Override
    protected void initializeGraphicalViewer() {
//        super.initializeGraphicalViewer();
        final GraphicalViewer viewer = getGraphicalViewer();
        viewer.setContents(getModel()); // set the contents of this editor
        final ConnectionLayer layer = (ConnectionLayer) _scalableFreeformRootEditPart
                .getLayer(LayerConstants.CONNECTION_LAYER);

        final ShortestPathConnectionRouter pathRouter = (ShortestPathConnectionRouter) layer
                .getConnectionRouter();
        pathRouter.setSpacing(ROUTING_SEPARATION);

        // listen for dropped parts
//        viewer.addDropTargetListener(createTransferDropTargetListener());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    @Override
    protected void setInput(final IEditorInput input) {
        super.setInput(input);
        // try {
        final IFile file = ((IFileEditorInput) input).getFile();
        _implicitDocumentProvider = new TextFileDocumentProvider();
        try {
            _implicitDocumentProvider.connect(input);
        } catch (final CoreException e) {
            e.printStackTrace();
        }
        final IDocument document = _implicitDocumentProvider.getDocument(input);
        if (document != null) {
            IFile sourceRessource = null;
            if (input instanceof FileEditorInput) {
                sourceRessource = ((FileEditorInput) getEditorInput())
                        .getFile();
            }
            final Node rootNode = this.getLanguageParser().parse(
                    document.get(), sourceRessource, new NullProgressMonitor());

            if (rootNode.hasChildren() && sourceRessource != null) {
                Map<String, StateLayoutData> stateData = new HashMap<String, StateLayoutData>();
                Map<String, List<Point>> connectionData = new HashMap<String, List<Point>>();
                try {
                    stateData = _persistenceHandler
                            .loadStateLayoutData(sourceRessource.getFullPath());
                    connectionData = _persistenceHandler
                            .loadConnectionLayoutData(sourceRessource
                                    .getFullPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                diagram = DiagramCreator.getInstance().createDiagram(rootNode,
                        stateData, connectionData, ROUTING_SEPARATION);
            } else {
                diagram = DiagramCreator.getInstance().createDefaultDiagram();
            }
        } else {
            diagram = DiagramCreator.getInstance().createDefaultDiagram();
        }
        setPartName(file.getName());
    }

    private AbstractLanguageParser getLanguageParser() {
        return new SNLParser();
    }

    /**
     * Creates an outline pagebook for this editor.
     */
    public class ShapesOutlinePage extends ContentOutlinePage {
        /**
         * Create a new outline page for the shapes editor.
         *
         * @param viewer
         *            a viewer (TreeViewer instance) used for this outline page
         * @throws IllegalArgumentException
         *             if editor is null
         */
        public ShapesOutlinePage(final EditPartViewer viewer) {
            super(viewer);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite
         * )
         */
        @Override
        public void createControl(final Composite parent) {
            // create outline viewer page
            getViewer().createControl(parent);
            // configure outline viewer
            getViewer().setEditDomain(getEditDomain());
            getViewer().setEditPartFactory(new ShapesTreeEditPartFactory());
            // configure & add context menu to viewer
            final ContextMenuProvider cmProvider = new ShapesEditorContextMenuProvider(
                    getViewer(), getActionRegistry());
            getViewer().setContextMenu(cmProvider);
            getSite().registerContextMenu(
                    "org.eclipse.gef.examples.shapes.outline.contextmenu",
                    cmProvider, getSite().getSelectionProvider());
            // hook outline viewer
            getSelectionSynchronizer().addViewer(getViewer());
            // initialize outline viewer with model
            getViewer().setContents(getModel());
            // show outline viewer
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.ui.part.IPage#dispose()
         */
        @Override
        public void dispose() {
            // unhook outline viewer
            getSelectionSynchronizer().removeViewer(getViewer());
            // dispose
            super.dispose();
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.ui.part.IPage#getControl()
         */
        @Override
        public Control getControl() {
            return getViewer().getControl();
        }

        /**
         * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
         */
        @Override
        public void init(final IPageSite pageSite) {
            super.init(pageSite);
            final ActionRegistry registry = getActionRegistry();
            final IActionBars bars = pageSite.getActionBars();
            String id = ActionFactory.UNDO.getId();
            bars.setGlobalActionHandler(id, registry.getAction(id));
            id = ActionFactory.REDO.getId();
            bars.setGlobalActionHandler(id, registry.getAction(id));
            id = ActionFactory.DELETE.getId();
            bars.setGlobalActionHandler(id, registry.getAction(id));
        }
    }

}
