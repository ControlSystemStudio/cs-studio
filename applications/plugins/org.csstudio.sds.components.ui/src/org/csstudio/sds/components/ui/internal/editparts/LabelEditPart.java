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

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.optionEnums.TextTypeEnum;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * EditPart controller for the label widget.
 * 
 * @author jbercic
 * 
 */
public final class LabelEditPart extends AbstractWidgetEditPart {
    
    /**
     * The actual figure will be surrounded with a small frame that can be used to drag the figure
     * around (even if the cell editor is activated).
     */
    private static final int FRAME_WIDTH = 1;

    /**
     * The input field will be slightly brighter than the actual figure so it can be easily
     * recognized.
     */
    private static final int INPUT_FIELD_BRIGHTNESS = 10;

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
	
     /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request req) {
        Object type = req.getType();

        // entering a value is only allowed in run mode and when the widget is
        // enabled
        if (type != null
                && (type.equals(RequestConstants.REQ_OPEN) || type
                        .equals(RequestConstants.REQ_DIRECT_EDIT)) ) {
            if (getExecutionMode() == ExecutionMode.RUN_MODE&& getCastedModel().isEnabled()) {
                super.performRequest(req);
            } else if(getExecutionMode() == ExecutionMode.EDIT_MODE){
                performEditTextValue();
            }
        }
        
    }
	
    private void performEditTextValue() {
        CellEditor cellEditor = createCellEditor2();
        locateCellEditor(cellEditor);
        cellEditor.activate();
        cellEditor.setFocus();
    }

    private CellEditor createCellEditor2() {
        final CellEditor result = new TextCellEditor((Composite) getViewer().getControl());

        // init cell editor...
        String currentValue = "N/A"; //$NON-NLS-1$
        WidgetProperty inputTextProperty = getWidgetModel().getProperty(
                LabelModel.PROP_TEXTVALUE);

        if (inputTextProperty != null) {
            currentValue = inputTextProperty.getPropertyValue().toString();
        }

        result.setValue(currentValue);
        final Text text = (Text) result.getControl();
        // input text
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    getWidgetModel().setPropertyValue(LabelModel.PROP_TEXTVALUE,text.getText());
                } else if (e.keyCode == SWT.ESC) {
                    result.deactivate();
                }
            }

        });
        // get the chosen font
        FontData fontData = (FontData) getWidgetModel().getProperty(LabelModel.PROP_FONT)
                .getPropertyValue();
        Font font = CustomMediaFactory.getInstance().getFont(new FontData[] { fontData });

        // get the chosen foreground color
        Color foregroundColor = CustomMediaFactory.getInstance().getColor(
                getWidgetModel().getForegroundColor());

        // get the chosen background color
      RGB backgroundRgb = getWidgetModel().getBackgroundColor();

      int red = Math.min(backgroundRgb.red + INPUT_FIELD_BRIGHTNESS, 255);
      int green = Math.min(backgroundRgb.green + INPUT_FIELD_BRIGHTNESS, 255);
      int blue = Math.min(backgroundRgb.blue + INPUT_FIELD_BRIGHTNESS, 255);

      Color backgroundColor = CustomMediaFactory.getInstance()
              .getColor(new RGB(red, green, blue));

        text.setForeground(foregroundColor);
        text.setBackground(backgroundColor);
        text.setFont(font);
        text.selectAll();
        
        return result;
    }
    /**
     * Locate the given cell editor .
     * 
     * @param cellEditor
     *            A cell editor.
     */
    private void locateCellEditor(final CellEditor cellEditor) {
        Rectangle rect = LabelEditPart.this.figure.getBounds().getCopy();
        rect.x = rect.x + FRAME_WIDTH;
        rect.y = rect.y + FRAME_WIDTH;
        rect.height = rect.height - (FRAME_WIDTH * 1);
        rect.width = rect.width - (FRAME_WIDTH * 1);
        getFigure().translateToAbsolute(rect);

        cellEditor.getControl().setBounds(rect.x, rect.y, rect.width, rect.height);
        cellEditor.getControl().setLayoutData(new GridData(SWT.CENTER));
        cellEditor.getControl().setVisible(true);
    }
}
