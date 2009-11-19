package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.ArcFigure;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.ArcModel;
import org.eclipse.draw2d.IFigure;

/**The controller for arc widget.
 * @author jbercic (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class ArcEditpart extends AbstractShapeEditPart {

	
	@Override
	protected IFigure doCreateFigure() {
		ArcFigure figure = new ArcFigure();
		ArcModel model = getWidgetModel();
		figure.setFill(model.isFill());		
		figure.setAntiAlias(model.isAntiAlias());
		figure.setStartAngle(model.getStartAngle());
		figure.setTotalAngle(model.getTotalAngle());		
		return figure;
	}	
	
	@Override
	public ArcModel getWidgetModel() {
		return (ArcModel)getModel();
	}
	

	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
		// fill
		IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ArcFigure figure = (ArcFigure) refreshableFigure;
				figure.setFill((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_FILL, fillHandler);	
		
		
		// anti alias
		IWidgetPropertyChangeHandler antiAliasHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ArcFigure figure = (ArcFigure) refreshableFigure;
				figure.setAntiAlias((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_ANTIALIAS, antiAliasHandler);
		
		
		//start angle
		IWidgetPropertyChangeHandler startAngleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ArcFigure figure = (ArcFigure) refreshableFigure;
				figure.setStartAngle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_START_ANGLE, startAngleHandler);
		
		//total angle
		IWidgetPropertyChangeHandler totalAngleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ArcFigure figure = (ArcFigure) refreshableFigure;
				figure.setTotalAngle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_TOTAL_ANGLE, totalAngleHandler);
		
	}


}
