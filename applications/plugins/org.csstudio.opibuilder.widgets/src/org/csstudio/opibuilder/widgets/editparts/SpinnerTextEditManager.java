package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.IPVWidgetEditpart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

public class SpinnerTextEditManager extends TextEditManager {

private boolean multiLine = true;
private AbstractBaseEditPart editPart;
private double step_increment;
private double page_increment;

public SpinnerTextEditManager(AbstractBaseEditPart source, CellEditorLocator locator, boolean multiline, double step_increment, double page_increment) {
	super(source, locator, multiline);
	this.editPart = source;
	this.multiLine = multiline;
	this.step_increment = step_increment;
	this.page_increment = page_increment;
}

@Override
protected CellEditor createCellEditorOn(Composite composite) {	
	CellEditor editor =  new TextCellEditor(composite, (multiLine ? SWT.MULTI : SWT.SINGLE) | SWT.WRAP){
		@Override
		protected void focusLost() {			
			//in run mode, if the widget has a PV attached, 
			//lose focus should cancel the editing except mobile.
				if (editPart.getExecutionMode() == ExecutionMode.RUN_MODE
						&& !OPIBuilderPlugin.isMobile(getControl().getDisplay())
						&& editPart instanceof IPVWidgetEditpart
						&& ((IPVWidgetEditpart) editPart).getPV() != null) {
					if (isActivated()) {
						fireCancelEditor();
						deactivate();
					}
					editPart.getFigure().requestFocus();
				} else
					super.focusLost();
		}
		
		@Override
		protected void handleDefaultSelection(SelectionEvent event) {
			//In run mode, hit ENTER should force to write the new value even it doesn't change.
			if(editPart.getExecutionMode() == ExecutionMode.RUN_MODE) {
				setDirty(true);
			}
			super.handleDefaultSelection(event);
		}
		
		@Override
		protected void keyReleaseOccured(KeyEvent keyEvent) {
			//In run mode, CTRL+ENTER will always perform a write if it is multiline text input
			if (keyEvent.character == '\r' && 
					editPart.getExecutionMode() == ExecutionMode.RUN_MODE) { // Return key	            
	            if (text != null && !text.isDisposed()
	                    && (text.getStyle() & SWT.MULTI) != 0) {
	                if ((keyEvent.stateMask & SWT.CTRL) != 0) {
	                  setDirty(true);
	                }
	            }
			}
			if (keyEvent.keyCode == SWT.ARROW_UP) {
				text.selectAll();
				text.setText(String.valueOf(Double.valueOf(text.getSelectionText()) + step_increment));
				setDirty(true);
			}
			if (keyEvent.keyCode == SWT.ARROW_DOWN) {
				text.selectAll();
				text.setText(String.valueOf(Double.valueOf(text.getSelectionText()) - step_increment));
				setDirty(true);
			}
			if (keyEvent.keyCode == SWT.PAGE_UP) {
				text.selectAll();
				text.setText(String.valueOf(Double.valueOf(text.getSelectionText()) + page_increment));
				setDirty(true);
			}
			if (keyEvent.keyCode == SWT.PAGE_DOWN) {
				text.selectAll();
				text.setText(String.valueOf(Double.valueOf(text.getSelectionText()) - page_increment));
				setDirty(true);
			}
			super.keyReleaseOccured(keyEvent);
		}
	};
	editor.getControl().moveAbove(null);
	return editor;
}
}
