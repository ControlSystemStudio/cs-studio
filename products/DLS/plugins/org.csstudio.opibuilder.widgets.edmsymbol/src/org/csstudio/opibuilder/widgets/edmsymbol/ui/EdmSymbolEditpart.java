package org.csstudio.opibuilder.widgets.edmsymbol.ui;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.simplepv.VTypeHelper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.epics.vtype.VType;


public class EdmSymbolEditpart extends AbstractPVWidgetEditPart {
	
	@Override
	protected IFigure doCreateFigure() {
		EdmSymbolModel model = (EdmSymbolModel) getModel();
		EdmSymbolFigure figure = new EdmSymbolFigure(model.getFilename());
		figure.setSubImageSelection(/* TODO: Start with invalid value */ 0);
		figure.setSubImageWidth(model.getSubImageWidth());
		return figure;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// changes to the filename property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {				
				EdmSymbolFigure imageFigure = (EdmSymbolFigure) figure;
				IPath absolutePath = (IPath)newValue;
				if(!absolutePath.isAbsolute()) {
					absolutePath = ResourceUtil.buildAbsolutePath(getWidgetModel(), absolutePath);
				}
				imageFigure.setImage(absolutePath);
				return false;
			}
		};
		setPropertyChangeHandler(EdmSymbolModel.PROP_EDM_IMAGE_FILE, handler);
		
		// changes to sub image width property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {				
				EdmSymbolFigure imageFigure = (EdmSymbolFigure) figure;
				Double val = (Double) newValue;
				imageFigure.setSubImageWidth(val.intValue());
				return false;
			}
		};
		setPropertyChangeHandler(EdmSymbolModel.PROP_SUB_IMAGE_WIDTH, handler);

		// changes to PV value
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				if(newValue == null || !(newValue instanceof VType)) return false;
				EdmSymbolFigure edmFigure = (EdmSymbolFigure) figure;
				edmFigure.setSubImageSelection((int) VTypeHelper.getDouble((VType)newValue));
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);
	}

}
