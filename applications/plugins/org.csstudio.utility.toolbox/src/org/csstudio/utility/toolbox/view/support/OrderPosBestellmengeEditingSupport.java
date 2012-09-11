package org.csstudio.utility.toolbox.view.support;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.entities.OrderPos;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class OrderPosBestellmengeEditingSupport extends EditingSupport {

	public OrderPosBestellmengeEditingSupport(TableViewer viewer) {
		super(viewer);
	}

	static class NumericVerifier implements VerifyListener {
		@Override
		public void verifyText(VerifyEvent e) {
			e.doit = e.text.matches("\\d*");
		}		
	}
	
	@Override
	protected TextCellEditor getCellEditor(Object element) {
		TextCellEditor textCellEditor = new TextCellEditor(((TableViewer) getViewer()).getTable());
		Text text = (Text) textCellEditor.getControl();
		text.addVerifyListener(new NumericVerifier());
		return textCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		OrderPos orderPos = (OrderPos) element;
		return orderPos.getAnzahlBestellt().toString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (value instanceof String) {
			String stringValue = (String) value;
			if (!StringUtils.isEmpty(stringValue)) {
				OrderPos orderPos = (OrderPos) element;
				orderPos.setAnzahlBestellt(new BigDecimal(stringValue));
				((TableViewer) getViewer()).refresh();
			}
		}
	}
	
}
