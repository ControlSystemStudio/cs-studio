package org.csstudio.utility.toolbox.view.support;

import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.entities.OrderPos;
import org.csstudio.utility.toolbox.framework.celleditors.CustomDialogCellEditor;
import org.csstudio.utility.toolbox.func.Func2;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;

public class ArticleDescriptionEditingSupport extends EditingSupport {

	private final Func2<Object, CustomDialogCellEditor, Control> openCellDialog;
	
	public ArticleDescriptionEditingSupport(TableViewer viewer,
				Func2<Object, CustomDialogCellEditor, Control> openCellDialog) {
		super(viewer);
		this.openCellDialog = openCellDialog;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CustomDialogCellEditor(((TableViewer) getViewer()).getTable()) {
			@Override
			protected Object openDialogBox(Control cellEditorWindow) {
				return openCellDialog.apply(this, cellEditorWindow);
			}
		};
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}
	
	@Override
	protected Object getValue(Object element) {
		OrderPos orderPos = (OrderPos) element;
		// Return the object here. Returning getArticleDescription().getBeschreibung()
		// freezes the GUI. Why ? Don't know...
		return orderPos.getArticle().getArticleDescription();
	}

	@Override
	protected void setValue(Object element, Object value) {
		OrderPos orderPos = (OrderPos) element;
		if (orderPos != null) {
			orderPos.setArticleDescription((ArticleDescription) value);
			if ((orderPos.getArticle() != null) && (orderPos.getArticle().getArticleDescription() != null)) {
				orderPos.setEinzelPreis(orderPos.getArticle().getArticleDescription().getLieferantStueckpreis());
			}
		}
		((TableViewer) getViewer()).refresh();
	}

}
