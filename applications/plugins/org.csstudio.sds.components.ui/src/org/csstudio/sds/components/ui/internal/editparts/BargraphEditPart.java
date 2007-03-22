/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableBargraphFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the Bargraph widget. The controller mediates between
 * {@link BargraphModel} and {@link RefreshableBargraphFigure}.
 * 
 * @author Kai Meyer
 * 
 */
public final class BargraphEditPart extends AbstractWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		BargraphModel model = (BargraphModel)getCastedModel();
		
		RefreshableBargraphFigure bargraph = new RefreshableBargraphFigure();
		bargraph.setFill(model.getFillLevel());
		bargraph.setOrientation(model.getOrientation());
		bargraph.setLoloColor(model.getLoloColor());
		//bargraph.setLoloMax(model.getLoloMax());
		
		return bargraph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		//fill
		IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setFill((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_FILL, fillHandler);
		//orientation
		IWidgetPropertyChangeHandler orientationHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setOrientation((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_ORIENTATION, orientationHandler);
		//lolo Color
		IWidgetPropertyChangeHandler loloColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setLoloColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_LOLO_COLOR, loloColorHandler);
		IWidgetPropertyChangeHandler loColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setLoColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_LO_COLOR, loColorHandler);
		IWidgetPropertyChangeHandler mColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setMColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_M_COLOR, mColorHandler);
		IWidgetPropertyChangeHandler hiColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setHiColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_HI_COLOR, hiColorHandler);
		IWidgetPropertyChangeHandler hihiColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setHihiColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_HIHI_COLOR, hihiColorHandler);
		//lolo max
//		IWidgetPropertyChangeHandler loloMaxHandler = new IWidgetPropertyChangeHandler() {
//			public boolean handleChange(final Object oldValue,
//					final Object newValue,
//					final IRefreshableFigure refreshableFigure) {
//				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
//				bargraph.setLoloMax((Double) newValue);
//				return true;
//			}
//		};
//		setPropertyChangeHandler(BargraphModel.PROP_LOLO_MAX, loloMaxHandler);
	}

}
