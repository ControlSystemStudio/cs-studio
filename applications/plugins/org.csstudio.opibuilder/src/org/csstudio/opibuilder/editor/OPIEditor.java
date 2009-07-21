package org.csstudio.opibuilder.editor;
import java.util.EventObject;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.DisplayModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;


public class OPIEditor extends GraphicalEditorWithFlyoutPalette {

	private PaletteRoot paletteRoot;
	
	/** the undoable <code>IPropertySheetPage</code> */
	private PropertySheetPage undoablePropertySheetPage;
	
	private DisplayModel displayModel;

	private RulerComposite rulerComposite;
	
	public OPIEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	protected void createGraphicalViewer(Composite parent) {
		rulerComposite = new RulerComposite(parent, SWT.NONE);

		GraphicalViewer viewer = new PatchedScrollingGraphicalViewer();
		viewer.createControl(rulerComposite);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();

		rulerComposite
				.setGraphicalViewer((ScrollingGraphicalViewer) getGraphicalViewer());
			
	}
	
	
	@Override
	protected Control getGraphicalControl() {
		return rulerComposite;
	}
	
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		viewer.setEditPartFactory(new WidgetEditPartFactory(ExecutionMode.EDIT_MODE));
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
		ContextMenuProvider cmProvider = 
			new OPIEditorContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);		
	}
	
	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		
		displayModel = new DisplayModel();
		viewer.setContents(displayModel);
				
		viewer.addDropTargetListener(createTransferDropTargetListener());

	}
	
	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}
	
	
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				// create a drag source listener for this palette viewer
				// together with an appropriate transfer drop target listener, this will enable
				// model element creation by dragging a CombinatedTemplateCreationEntries 
				// from the palette into the editor
				// @see ShapesEditor#createTransferDropTargetListener()
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}
	
	/**
	 * Create a transfer drop target listener. When using a CombinedTemplateCreationEntry
	 * tool in the palette, this will enable model element creation by dragging from the palette.
	 * @see #createPaletteViewerProvider()
	 */
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			protected CreationFactory getFactory(Object template) {
				return new SimpleFactory((Class) template);
			}
		};
	}
	
	@Override
	protected PaletteRoot getPaletteRoot() {
		if(paletteRoot == null)
			paletteRoot = new PaletteRoot();
		return paletteRoot;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}
	
	/**
	* Returns the undoable <code>PropertySheetPage</code> for
	* this editor.
	*
	* @return the undoable <code>PropertySheetPage</code>
	*/
	protected PropertySheetPage getPropertySheetPage(){
		if(undoablePropertySheetPage == null){
			undoablePropertySheetPage = new PropertySheetPage();
			undoablePropertySheetPage.setRootEntry(
					new UndoablePropertySheetEntry(getCommandStack()));
		}
		return undoablePropertySheetPage;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class type) {
		if(type == IPropertySheetPage.class)
			return getPropertySheetPage();
		return super.getAdapter(type);
	}
}
