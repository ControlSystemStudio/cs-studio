/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.optionEnums.TextTypeEnum;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;

/**
 * EditPart controller for the label widget.
 * 
 * @author jbercic
 * 
 */
public final class LabelEditPart extends AbstractWidgetEditPart {

	private NumberFormat numberFormat = NumberFormat.getInstance();
	
	/**
	 * Returns the casted model. This is just for convenience.
	 * 
	 * @return the casted {@link LabelModel}
	 */
	protected LabelModel getCastedModel() {
		return (LabelModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		LabelModel model = getCastedModel();
		// create AND initialize the view properly
		final RefreshableLabelFigure figure = new RefreshableLabelFigure();

		figure.setFont(CustomMediaFactory.getInstance()
				.getFont(model.getFont()));
		figure.setTextAlignment(model.getTextAlignment());
		figure.setTransparent(model.getTransparent());
		figure.setRotation(model.getRotation());
		figure.setXOff(model.getXOff());
		figure.setYOff(model.getYOff());

		figure.setTextValue(determineLabel(null));

		return figure;
	}

	/**
	 * Registers handlers for changes of the different value properties.
	 */
	protected void registerValueChangeHandlers() {
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setTextValue(determineLabel(null));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_VALUE_TYPE, handle);

		// text value
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setTextValue(determineLabel(LabelModel.PROP_TEXTVALUE));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TEXTVALUE, handle);

		// precision
		IWidgetPropertyChangeHandler precisionHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) refreshableFigure;
				labelFigure.setTextValue(determineLabel(LabelModel.PROP_PRECISION));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_PRECISION, precisionHandler);
		// aliases
		IWidgetPropertyChangeHandler aliasHandler = new IWidgetPropertyChangeHandler() {
			@SuppressWarnings("unchecked")
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) refreshableFigure;
				labelFigure.setTextValue(determineLabel(LabelModel.PROP_ALIASES));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_ALIASES, aliasHandler);
		// primary pv
		IWidgetPropertyChangeHandler pvHandler = new IWidgetPropertyChangeHandler() {
			@SuppressWarnings("unchecked")
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) refreshableFigure;
				labelFigure.setTextValue(determineLabel(LabelModel.PROP_ALIASES));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_PRIMARY_PV, pvHandler);
	}

	private String determineLabel(String updatedPropertyId) {
		LabelModel model = getCastedModel();

		TextTypeEnum type = model.getValueType();
		String text = model.getTextValue();

		String toprint = "none";

		switch (type) {
		case TEXT:
			if (updatedPropertyId==null || updatedPropertyId.equals(LabelModel.PROP_TEXTVALUE)) {
				toprint = text;
			}
			break;
		case DOUBLE:
			if (updatedPropertyId==null || updatedPropertyId.equals(LabelModel.PROP_TEXTVALUE)
					|| updatedPropertyId.equals(LabelModel.PROP_PRECISION)) {
				try {
					double d = Double.parseDouble(text);
					numberFormat.setMaximumFractionDigits(model.getPrecision());
					numberFormat.setMinimumFractionDigits(model.getPrecision());
					toprint = numberFormat.format(d);
				} catch (Exception e) {
					toprint = text;
				}
			}
			break;
		case ALIAS:
			if (updatedPropertyId==null || updatedPropertyId.equals(LabelModel.PROP_ALIASES) || updatedPropertyId.equals(LabelModel.PROP_PRIMARY_PV)) {
				try {
					toprint = ChannelReferenceValidationUtil
							.createCanonicalName(model.getPrimaryPV(), model
									.getAllInheritedAliases());
				} catch (ChannelReferenceValidationException e) {
					toprint = model.getPrimaryPV();
				}
			}
			break;
		case HEX:
			if (updatedPropertyId==null || updatedPropertyId.equals(LabelModel.PROP_TEXTVALUE)) {
				try {
					long l = Long.parseLong(text);
					toprint = Long.toHexString(l);
				} catch (Exception e1) {
					try {
						double d = Double.parseDouble(text);
						toprint = Double.toHexString(d);
					} catch (Exception e2) {
						toprint = text;
					}
				}
			}
			break;
		case EXP:
		    if (updatedPropertyId==null || updatedPropertyId.equals(LabelModel.PROP_TEXTVALUE)
                    || updatedPropertyId.equals(LabelModel.PROP_PRECISION)) {
                try {
                    String pattern = "0.";
                    for(int i=0;i<model.getPrecision();i++){
                        if(i==0){
                            pattern = pattern.concat("0");
                        }else{
                            pattern = pattern.concat("#");
                        }
                    }
                    pattern = pattern.concat("E00");
                    DecimalFormat expFormat = new DecimalFormat(pattern);
                    double d = Double.parseDouble(text);
                    toprint = expFormat.format(d);
                } catch (Exception e) {
                    toprint = text;
                }
            }
            break;
		default:
		    
			toprint = "unknown value type";
		}
		return toprint;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// changes to the font property
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				FontData fontData = (FontData) newValue;
				labelFigure.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_FONT, handle);

		// changes to the text alignment property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setTextAlignment((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TEXT_ALIGN, handle);

		// changes to the transparency property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TRANSPARENT, handle);

		// changes to the text rotation property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setRotation((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_ROTATION, handle);

		// changes to the x offset property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setXOff((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_XOFF, handle);

		// changes to the y offset property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setYOff((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_YOFF, handle);

		registerValueChangeHandlers();
	}
}
