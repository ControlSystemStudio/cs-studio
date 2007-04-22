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
		BargraphModel model = (BargraphModel) getCastedModel();

		RefreshableBargraphFigure bargraph = new RefreshableBargraphFigure();
		bargraph.setFill(model.getFillLevel());
		bargraph.setOrientation(model.getOrientation());
		// Colors
		bargraph.setDefaultFillColor(model.getDefaultFillColor());
		bargraph.setLoloColor(model.getLoloColor());
		bargraph.setLoColor(model.getLoColor());
		bargraph.setMColor(model.getMColor());
		bargraph.setHiColor(model.getHiColor());
		bargraph.setHihiColor(model.getHihiColor());
		bargraph.setBorderColor(model.getBorderColor());
		bargraph.setFillBackgroundColor(model.getFillbackgroundColor());
		// Levels
		bargraph.setMinimum(model.getMinimum());
		bargraph.setLoloLevel(model.getLoloLevel());
		bargraph.setLoLevel(model.getLoLevel());
		bargraph.setMLevel(model.getMLevel());
		bargraph.setHiLevel(model.getHiLevel());
		bargraph.setHihiLevel(model.getHihiLevel());
		bargraph.setMaximum(model.getMaximum());
		// show_Value
		bargraph.setShowValues(model.getShowValues());
		bargraph.setShowMarks(model.getShowMarks());
		bargraph.setShowScale(model.getShowScale());
		bargraph.setScaleSectionCount(model.getScaleSectionCount());
		return bargraph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// fill
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
		// orientation
		IWidgetPropertyChangeHandler orientationHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setOrientation((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_ORIENTATION,
				orientationHandler);

		this.registerColorPropertyChangeHandler();

		this.registerLevelPropertyChangeHandler();
		// Show_value
		IWidgetPropertyChangeHandler showValuesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setShowValues((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_SHOW_VALUES,
				showValuesHandler);
		// Show_marks
		IWidgetPropertyChangeHandler showMarksHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setShowMarks((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_SHOW_MARKS,
				showMarksHandler);
		// Show_scale
		IWidgetPropertyChangeHandler showScaleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setShowScale((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_SHOW_SCALE,
				showScaleHandler);
		// Scale_count
		IWidgetPropertyChangeHandler scaleCountHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setScaleSectionCount((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_SCALE_SECTION_COUNT,
				scaleCountHandler);
	}

	/**
	 * Registers PropertyChangeHandler for the color properties.
	 */
	private void registerColorPropertyChangeHandler() {
		IWidgetPropertyChangeHandler loloColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setLoloColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_LOLO_COLOR,
				loloColorHandler);
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
		setPropertyChangeHandler(BargraphModel.PROP_HIHI_COLOR,
				hihiColorHandler);
		IWidgetPropertyChangeHandler defaultFillColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setDefaultFillColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_DEFAULT_FILL_COLOR,
				defaultFillColorHandler);
	}

	/**
	 * Registers PropertyChangeHandler for the level properties.
	 */
	private void registerLevelPropertyChangeHandler() {
		IWidgetPropertyChangeHandler minimumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setMinimum((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_MIN, minimumHandler);
		
		IWidgetPropertyChangeHandler loloHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setLoloLevel((Double) newValue);

				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_LOLO_LEVEL, loloHandler);
		IWidgetPropertyChangeHandler loHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setLoLevel((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_LO_LEVEL, loHandler);
		IWidgetPropertyChangeHandler mHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setMLevel((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_M_LEVEL, mHandler);
		IWidgetPropertyChangeHandler hiHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setHiLevel((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_HI_LEVEL, hiHandler);
		IWidgetPropertyChangeHandler hihiHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setHihiLevel((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_HIHI_LEVEL, hihiHandler);
		IWidgetPropertyChangeHandler maximumHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableBargraphFigure bargraph = (RefreshableBargraphFigure) refreshableFigure;
				bargraph.setMaximum((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_MAX, maximumHandler);
	}

}
