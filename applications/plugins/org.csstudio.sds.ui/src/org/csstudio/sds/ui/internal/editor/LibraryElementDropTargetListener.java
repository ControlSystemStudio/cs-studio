package org.csstudio.sds.ui.internal.editor;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.internal.actions.WidgetModelTransfer;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.swt.dnd.DND;

public class LibraryElementDropTargetListener extends
		AbstractTransferDropTargetListener {

	public LibraryElementDropTargetListener(EditPartViewer viewer) {
		super(viewer, WidgetModelTransfer.getInstance());
	}
	
	List<AbstractWidgetModel> widgets;

	@Override
	protected Request createTargetRequest() {
		CreateRequest request = new CreateRequest();
		CreationFactory creationFactory = new CreationFactory() {

			@Override
			public Object getObjectType() {
				return "List_AbstractWidgetModel";
			}

			@Override
			public Object getNewObject() {
				return widgets;
			}
		};
		request.setFactory(creationFactory);
		
		return request;

	}

	@Override
	protected void updateTargetRequest() {
		((CreateRequest) getTargetRequest())
				.setLocation(getDropLocation());
	}

	@Override
	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOver();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleDrop() {
		widgets = (List<AbstractWidgetModel>) getCurrentEvent().data;
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDrop();
	}
}
