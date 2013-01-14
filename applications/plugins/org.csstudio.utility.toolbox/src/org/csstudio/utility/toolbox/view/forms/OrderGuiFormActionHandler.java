package org.csstudio.utility.toolbox.view.forms;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.csstudio.utility.toolbox.actions.OpenArticleDescriptionSearchAction;
import org.csstudio.utility.toolbox.actions.OpenFirmaSearchAction;
import org.csstudio.utility.toolbox.actions.OpenOrderEditorAction;
import org.csstudio.utility.toolbox.actions.OpenOrderSearchAction;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.entities.OrderPos;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.celleditors.CustomDialogCellEditor;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Combo;

import com.google.inject.Inject;

public class OrderGuiFormActionHandler {

	private boolean isSearchMode;

	private GenericEditorInput<Order> editorInput;

	private WidgetFactory<Order> wf;
	
	@Inject
	private OpenFirmaSearchAction openFirmaSearchAction;

	@Inject
	private OpenOrderEditorAction openOrderEditorAction;

	@Inject
	private OpenOrderSearchAction openOrderSearchAction;

	@Inject
	private OpenArticleDescriptionSearchAction openArticleDescriptionSearchAction;

	public void init(boolean isSearchMode, GenericEditorInput<Order> editorInput, WidgetFactory<Order> wf) {
		Validate.notNull(editorInput, "editorInput must not be null");
		this.isSearchMode = isSearchMode;
		this.editorInput = editorInput;
		this.wf = wf;
	}

	public void selectFirma(final Combo company) {
		openFirmaSearchAction.run(Some.some(new Func1Void<IStructuredSelection>() {
			@Override
			public void apply(IStructuredSelection selection) {
				if (selection != null) {
					Firma firma = (Firma) selection.getFirstElement();
					wf.setText(P("firmaName"), firma.getName());
					goBackToCallingForm();
				}
			}
		}));
	}

	public void selectBa(final Combo previousBa) {
		openOrderSearchAction.run(Some.some(new Func1Void<IStructuredSelection>() {
			@Override
			public void apply(IStructuredSelection selection) {
				if (selection != null) {
					Order order = (Order) selection.getFirstElement();
					previousBa.setText(order.getNummer().toString());
					goBackToCallingForm();
				}
			}
		}));
	}

	private void goBackToCallingForm() {
		if (isSearchMode) {
			openOrderSearchAction.goBack(editorInput);
		} else {
			openOrderEditorAction.goBack(editorInput);
		}
	}

	public void addNewDetail(TableViewer detailTableViewer, Option<CrudController<Order>> crudController) {
		@SuppressWarnings("unchecked")
		List<OrderPos> orderPositions = (List<OrderPos>) detailTableViewer.getInput();
		OrderPos orderPos = OrderPos.buildNewOrderPos(new BigDecimal(orderPositions.size() + 1));
		detailTableViewer.add(orderPos);
		orderPositions.add(orderPos);
		crudController.get().setDirty(true);
	}

	public void removeDetail(TableViewer detailTableViewer, Option<CrudController<Order>> crudController) {
		@SuppressWarnings("unchecked")
		List<OrderPos> orderPositions = (List<OrderPos>) detailTableViewer.getInput();
		int rowIndex = detailTableViewer.getTable().getSelectionIndex();
		OrderPos orderPos = orderPositions.get(rowIndex);
		orderPositions.remove(orderPos);
		detailTableViewer.remove(orderPos);
		crudController.get().setDirty(true);
	}

	public void selectArticleDescription(final CustomDialogCellEditor dialogCellEditor, IStructuredSelection selection) {
		openArticleDescriptionSearchAction.run(Some.some(new Func1Void<IStructuredSelection>() {
			/**
			 * After selecting an ArticelDescription go back to the form from
			 * which the selection was triggered.
			 */
			@Override
			public void apply(IStructuredSelection selection) {
				if (selection != null) {
					ArticleDescription ad = (ArticleDescription) selection.getFirstElement();
					dialogCellEditor.setValue(ad);
					dialogCellEditor.fireApplyEditorValueChanged();
					openOrderEditorAction.goBack(editorInput);
				}
			}
		}), selection);
	}

}
