package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.EllipseFigure;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.EllipseModel;
import org.eclipse.draw2d.IFigure;

/**The controller for ellipse widget.
 * @author Xihui Chen
 *
 */
public class EllipseEditpart extends AbstractShapeEditPart {

	
	@Override
	protected IFigure doCreateFigure() {
		EllipseFigure figure = new EllipseFigure();
		EllipseModel model = getCastedModel();
		figure.setFill(model.getFillLevel());
		figure.setOrientation(model.isHorizontalFill());
		figure.setTransparent(model.isTransparent());
		figure.setAntiAlias(model.isAntiAlias());
		return figure;
	}	
	
	@Override
	public EllipseModel getCastedModel() {
		return (EllipseModel)getModel();
	}
	

	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
		// fill
		IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				EllipseFigure ellipseFigure = (EllipseFigure) refreshableFigure;
				ellipseFigure.setFill((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_FILL_LEVEL, fillHandler);	
		
		// fill orientaion
		IWidgetPropertyChangeHandler fillOrientHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				EllipseFigure ellipseFigure = (EllipseFigure) refreshableFigure;
				ellipseFigure.setOrientation((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_HORIZONTAL_FILL, fillOrientHandler);	
		
		// transparent
		IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				EllipseFigure ellipseFigure = (EllipseFigure) refreshableFigure;
				ellipseFigure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_TRANSPARENT, transparentHandler);	
		
		// anti alias
		IWidgetPropertyChangeHandler antiAliasHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				EllipseFigure ellipseFigure = (EllipseFigure) refreshableFigure;
				ellipseFigure.setAntiAlias((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_ANTIALIAS, antiAliasHandler);
		
	}


}
