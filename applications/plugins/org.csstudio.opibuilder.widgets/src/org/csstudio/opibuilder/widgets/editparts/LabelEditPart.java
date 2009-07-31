package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.sds.components.ui.internal.figures.LabelFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

public class LabelEditPart extends AbstractPVWidgetEditPart {

	
	@Override
	protected IFigure createFigure() {
		return new LabelFigure(false);
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		if(getExecutionMode() == ExecutionMode.EDIT_MODE)
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new LabelDirectEditPolicy());
		
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((LabelFigure)figure).setText((String)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TEXT, handler);
		
	  handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((LabelFigure)figure).setFill(!(Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TRANSPARENT, handler);
		
	}

	private void performDirectEdit(){
		new LabelEditManager(this, new LabelCellEditorLocator((LabelFigure)getFigure())).show();
	}
	
	@Override
	public void performRequest(Request request){
		if (getExecutionMode() == ExecutionMode.EDIT_MODE &&( 
				request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN))
			performDirectEdit();
	}

	class LabelCellEditorLocator
		implements CellEditorLocator
	{

		private LabelFigure stickyNote;
	
		public LabelCellEditorLocator(LabelFigure stickyNote) {
			setLabel(stickyNote);
		}
	
		public void relocate(CellEditor celleditor) {
			Text text = (Text)celleditor.getControl();
			Rectangle rect = stickyNote.getClientArea();
			stickyNote.translateToAbsolute(rect);
			org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
			rect.translate(trim.x, trim.y);
			rect.width += trim.width;
			rect.height += trim.height;
			text.setBounds(rect.x, rect.y, rect.width, rect.height);
		}
	
		/**
		 * Returns the stickyNote figure.
		 */
		protected LabelFigure getLabel() {
			return stickyNote;
		}
	
		/**
		 * Sets the Sticky note figure.
		 * @param stickyNote The stickyNote to set
		 */
		protected void setLabel(LabelFigure stickyNote) {
			this.stickyNote = stickyNote;
		}


	}
}
