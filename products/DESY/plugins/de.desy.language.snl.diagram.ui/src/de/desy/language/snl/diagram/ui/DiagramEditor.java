/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Elias Volanakis - initial API and implementation
 *******************************************************************************/
package de.desy.language.snl.diagram.ui;

import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.TransferDropTargetListener;
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
import de.desy.language.snl.diagram.ui.parts.ShapesEditPartFactory;
import de.desy.language.snl.diagram.ui.parts.ShapesTreeEditPartFactory;
import de.desy.language.snl.parser.SNLParser;

/**
 * A graphical editor with flyout palette that can edit .shapes files. The
 * binding between the .shapes file extension and this editor is done in
 * plugin.xml
 * 
 * @author Elias Volanakis
 */
public class DiagramEditor extends GraphicalEditorWithFlyoutPalette {

	/** This is the root of the editor's model. */
	private SNLDiagram diagram;
	private IDocumentProvider fImplicitDocumentProvider;
	/** Palette component, holding the tools and shapes. */
	private static PaletteRoot PALETTE_MODEL;
	private ScalableFreeformRootEditPart _scalableFreeformRootEditPart;

	/** Create a new ShapesEditor instance. This is called by the Workspace. */
	public DiagramEditor() {
		setEditDomain(new DefaultEditDomain(this));
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
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new ShapesEditPartFactory());
		_scalableFreeformRootEditPart = new ScalableFreeformRootEditPart();
		viewer.setRootEditPart(_scalableFreeformRootEditPart);
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

		// configure the context menu provider
		ContextMenuProvider cmProvider = new ShapesEditorContextMenuProvider(
				viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				// create a drag source listener for this palette viewer
				// together with an appropriate transfer drop target listener,
				// this will enable
				// model element creation by dragging a
				// CombinatedTemplateCreationEntries
				// from the palette into the editor
				// @see ShapesEditor#createTransferDropTargetListener()
				viewer
						.addDragSourceListener(new TemplateTransferDragSourceListener(
								viewer));
			}
		};
	}

	/**
	 * Create a transfer drop target listener. When using a
	 * CombinedTemplateCreationEntry tool in the palette, this will enable model
	 * element creation by dragging from the palette.
	 * 
	 * @see #createPaletteViewerProvider()
	 */
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			protected CreationFactory getFactory(Object template) {
				return new SimpleFactory((Class) template);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		try {
//			createOutputStream(out);
//			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
//			file.setContents(new ByteArrayInputStream(out.toByteArray()), true, 
			// keep saving, even if IFile is out of sync with the Workspace false, don't keep history
//					monitor); // progress monitor
//			getCommandStack().markSaveLocation();
//		} catch (CoreException ce) {
//			ce.printStackTrace();
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
//		// Show a SaveAs dialog
//		Shell shell = getSite().getWorkbenchWindow().getShell();
//		SaveAsDialog dialog = new SaveAsDialog(shell);
//		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
//		dialog.open();
//
//		IPath path = dialog.getResult();
//		if (path != null) {
//			// try to save the editor's contents under a different file name
//			final IFile file = ResourcesPlugin.getWorkspace().getRoot()
//					.getFile(path);
//			try {
//				new ProgressMonitorDialog(shell).run(false, // don't fork
//						false, // not cancelable
//						new WorkspaceModifyOperation() { // run this
//							// operation
//							public void execute(final IProgressMonitor monitor) {
//								try {
//									ByteArrayOutputStream out = new ByteArrayOutputStream();
//									createOutputStream(out);
//									file.create(new ByteArrayInputStream(out
//											.toByteArray()), // contents
//											true, // keep saving, even if
//											// IFile is out of sync with
//											// the Workspace
//											monitor); // progress monitor
//								} catch (CoreException ce) {
//									ce.printStackTrace();
//								} catch (IOException ioe) {
//									ioe.printStackTrace();
//								}
//							}
//						});
//				// set input to the new file
//				setInput(new FileEditorInput(file));
//				getCommandStack().markSaveLocation();
//			} catch (InterruptedException ie) {
//				// should not happen, since the monitor dialog is not cancelable
//				ie.printStackTrace();
//			} catch (InvocationTargetException ite) {
//				ite.printStackTrace();
//			}
//		}
	}
	
	@Override
	public boolean isDirty() {
		return false;
	}

	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class)
			return new ShapesOutlinePage(new TreeViewer());
		return super.getAdapter(type);
	}

	SNLDiagram getModel() {
		return diagram;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
	 */
	protected PaletteRoot getPaletteRoot() {
		if (PALETTE_MODEL == null)
			PALETTE_MODEL = ShapesEditorPaletteFactory.createPalette();
		return PALETTE_MODEL;
	}

	/**
	 * Set up the editor's inital content (after creation).
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getModel()); // set the contents of this editor
		
		ConnectionLayer layer = (ConnectionLayer) _scalableFreeformRootEditPart.getLayer(LayerConstants.CONNECTION_LAYER);
		
		ConnectionRouter connectionRouter = layer.getConnectionRouter();
		
		if (connectionRouter instanceof ShortestPathConnectionRouter) {
			ShortestPathConnectionRouter shortRouter = (ShortestPathConnectionRouter) connectionRouter;
			shortRouter.setSpacing(10);
			
			FanRouter fanRouter = new FanRouter();
			fanRouter.setSeparation(20);
			fanRouter.setNextRouter(shortRouter);
			
			layer.setConnectionRouter(fanRouter);
		}
		
		
		// listen for dropped parts
		viewer.addDropTargetListener(createTransferDropTargetListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		// try {
		IFile file = ((IFileEditorInput) input).getFile();
		fImplicitDocumentProvider = new TextFileDocumentProvider();
		try {
			fImplicitDocumentProvider.connect(input);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		final IDocument document = fImplicitDocumentProvider.getDocument(input);
		if (document != null) {
			IFile sourceRessource = null;
			if (input instanceof FileEditorInput) {
				sourceRessource = ((FileEditorInput) getEditorInput())
						.getFile();
			}
			Node rootNode = this.getLanguageParser().parse(document.get(), sourceRessource, new NullProgressMonitor());
			diagram = DiagramCreator.createDiagram(rootNode);
		} else {
			diagram = DiagramCreator.createDefaultDiagram();
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
		public ShapesOutlinePage(EditPartViewer viewer) {
			super(viewer);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			// create outline viewer page
			getViewer().createControl(parent);
			// configure outline viewer
			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(new ShapesTreeEditPartFactory());
			// configure & add context menu to viewer
			ContextMenuProvider cmProvider = new ShapesEditorContextMenuProvider(
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
		public Control getControl() {
			return getViewer().getControl();
		}

		/**
		 * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
		 */
		public void init(IPageSite pageSite) {
			super.init(pageSite);
			ActionRegistry registry = getActionRegistry();
			IActionBars bars = pageSite.getActionBars();
			String id = ActionFactory.UNDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.REDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.DELETE.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
		}
	}

}
