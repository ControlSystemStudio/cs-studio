package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.widgets.figures.LabelFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

public class LabelCellEditorLocator
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