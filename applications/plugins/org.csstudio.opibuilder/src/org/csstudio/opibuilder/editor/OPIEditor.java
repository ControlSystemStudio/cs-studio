package org.csstudio.opibuilder.editor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;


public class OPIEditor extends GraphicalEditorWithFlyoutPalette {

	private PaletteRoot paletteRoot;
	/** the undoable <code>IPropertySheetPage</code> */
	private PropertySheetPage undoablePropertySheetPage;
	
	public OPIEditor() {
		setEditDomain(new DefaultEditDomain(this));
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
