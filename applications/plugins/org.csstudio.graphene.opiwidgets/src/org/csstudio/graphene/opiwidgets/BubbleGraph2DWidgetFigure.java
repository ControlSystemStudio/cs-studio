/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.BubbleGraph2DWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 */
public class BubbleGraph2DWidgetFigure extends
		AbstractSWTWidgetFigure<BubbleGraph2DWidget> {

	private ISelectionProvider selectionProvider;

	public BubbleGraph2DWidgetFigure(AbstractBaseEditPart editpart) {
		super(editpart);
		selectionProvider = retrieveSelectionProvider(getSWTWidget());
	}

	@Override
	protected BubbleGraph2DWidget createSWTWidget(Composite parent, int style) {
		return new BubbleGraph2DWidget(parent, style);
	}

	private ISelectionProvider retrieveSelectionProvider(
			BubbleGraph2DWidget widget) {
		if (widget instanceof ISelectionProvider) {
			return (ISelectionProvider) widget;
		}
		return null;
	}

	/**
	 * The selection provider to be used for the pop-up.
	 * 
	 * @return the selection provider or null
	 */
	public ISelectionProvider getSelectionProvider() {
		return selectionProvider;
	}

	public boolean isRunMode() {
		return runmode;
	}

}
