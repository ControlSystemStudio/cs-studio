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

import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.optionEnums.TextTypeEnum;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
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
import org.omg.CORBA._PolicyStub;

/**
 * EditPart controller for <code>TextInputModel</code> elements with support for direct editing.
 * 
 * EditPart controller for the TextInput widget. The controller mediates between
 * {@link TextInputModel} and {@link RefreshableLabelFigure}.
 * 
 * @author Alexander Will
 * 
 */
public final class TextInputEditPart extends AbstractWidgetEditPart implements
        IProcessVariableWithSamples {
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
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        TextInputModel model = (TextInputModel) getWidgetModel();

        RefreshableLabelFigure label = new RefreshableLabelFigure();

        label.setTextValue(determineLabel(null));
        label.setFont(CustomMediaFactory.getInstance().getFont(model.getFont()));
        label.setTextAlignment(model.getTextAlignment());
        label.setTransparent(model.getTransparent());

        label.setEnabled(model.isEnabled() && getExecutionMode().equals(ExecutionMode.RUN_MODE));

        return label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPoliciesHook() {
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new LabelDirectEditPolicy());
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
                        .equals(RequestConstants.REQ_DIRECT_EDIT))) {
            if (getExecutionMode() == ExecutionMode.RUN_MODE && getCastedModel().isEnabled()) {
                performDirectEdit();
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
                TextInputModel.PROP_INPUT_TEXT);

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
                    getWidgetModel().setPropertyValue(TextInputModel.PROP_INPUT_TEXT,text.getText());
                } else if (e.keyCode == SWT.ESC) {
                    result.deactivate();
                }
            }

        });
        // get the chosen font
        FontData fontData = (FontData) getWidgetModel().getProperty(TextInputModel.PROP_FONT)
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
     * Open the cell editor for direct editing.
     */
    private void performDirectEdit() {
        CellEditor cellEditor = createCellEditor();
        locateCellEditor(cellEditor);
        cellEditor.activate();
        cellEditor.setFocus();
    }

    /**
     * Create the cell editor for direct editing.
     * 
     * @return The cell editor for direct editing.
     */
    private CellEditor createCellEditor() {
        final CellEditor result = new TextCellEditor((Composite) getViewer().getControl());

        // init cell editor...
        String currentValue = "N/A"; //$NON-NLS-1$
        WidgetProperty inputTextProperty = getWidgetModel().getProperty(
                TextInputModel.PROP_INPUT_TEXT);

        if (inputTextProperty != null) {
            currentValue = inputTextProperty.getPropertyValue().toString();
        }

        result.setValue(currentValue);
        final Text text = (Text) result.getControl();
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    DirectEditCommand cmd = new DirectEditCommand(text.getText());
                    cmd.execute();
                } else if (e.keyCode == SWT.ESC) {
                    result.deactivate();
                }
            }

        });

        // get the chosen font
        FontData fontData = (FontData) getWidgetModel().getProperty(TextInputModel.PROP_FONT)
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
        Rectangle rect = TextInputEditPart.this.figure.getBounds().getCopy();
        rect.x = rect.x + FRAME_WIDTH;
        rect.y = rect.y + FRAME_WIDTH;
        rect.height = rect.height - (FRAME_WIDTH * 1);
        rect.width = rect.width - (FRAME_WIDTH * 1);
        getFigure().translateToAbsolute(rect);

        cellEditor.getControl().setBounds(rect.x, rect.y, rect.width, rect.height);
        cellEditor.getControl().setLayoutData(new GridData(SWT.CENTER));
        cellEditor.getControl().setVisible(true);
    }

    /**
     * The direct edit command that changes the input text.
     */
    private class DirectEditCommand extends Command {
        /**
         * The entered input text.
         */
        private String _text;

        /**
         * Standard constructor.
         * 
         * @param text
         *            The entered input text.
         */
        public DirectEditCommand(final String text) {
            _text = text;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute() {
            WidgetProperty inputTextProperty = getWidgetModel().getProperty(
                    TextInputModel.PROP_INPUT_TEXT);

            if (inputTextProperty != null) {
                inputTextProperty.setManualValue(_text);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canUndo() {
            return false;
        }
    }

    /**
     * The direct edit policy.
     */
    private class LabelDirectEditPolicy extends DirectEditPolicy {
        /**
         * {@inheritDoc}
         */
        @Override
        protected Command getDirectEditCommand(final DirectEditRequest request) {
            DirectEditCommand command = new DirectEditCommand((String) request.getCellEditor()
                    .getValue());
            return command;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void showCurrentEditValue(final DirectEditRequest request) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // input text
        IWidgetPropertyChangeHandler textHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
                label.setTextValue(determineLabel(TextInputModel.PROP_INPUT_TEXT));
                return true;
            }
        };
        setPropertyChangeHandler(TextInputModel.PROP_INPUT_TEXT, textHandler);

        // font
        IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
                FontData fontData = (FontData) newValue;
                label.setFont(CustomMediaFactory.getInstance().getFont(fontData.getName(),
                        fontData.getHeight(), fontData.getStyle()));
                return true;
            }
        };
        setPropertyChangeHandler(TextInputModel.PROP_FONT, fontHandler);
        // text alignment
        IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
                label.setTextAlignment((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(TextInputModel.PROP_TEXT_ALIGNMENT, alignmentHandler);

        // transparent background
        IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
                label.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(TextInputModel.PROP_TRANSPARENT, transparentHandler);
        // type
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
                labelFigure.setTextValue(determineLabel(null));
                return true;
            }
        };
        setPropertyChangeHandler(TextInputModel.PROP_VALUE_TYPE, handle);

        // precision
        IWidgetPropertyChangeHandler precisionHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
                label.setTextValue(determineLabel(TextInputModel.PROP_PRECISION));
                return true;
            }
        };
        setPropertyChangeHandler(TextInputModel.PROP_PRECISION, precisionHandler);

        // aliases
        IWidgetPropertyChangeHandler aliasHandler = new IWidgetPropertyChangeHandler() {
            @SuppressWarnings("unchecked")
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
                label.setTextValue(determineLabel(TextInputModel.PROP_ALIASES));
                return true;
            }
        };
        setPropertyChangeHandler(TextInputModel.PROP_ALIASES, aliasHandler);

        // primary pv
        IWidgetPropertyChangeHandler pvHandler = new IWidgetPropertyChangeHandler() {
            @SuppressWarnings("unchecked")
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
                label.setTextValue(determineLabel(TextInputModel.PROP_PRIMARY_PV));
                return true;
            }
        };
        setPropertyChangeHandler(TextInputModel.PROP_PRIMARY_PV, pvHandler);
    }

    private String determineLabel(String updatedPropertyId) {
        TextInputModel model = (TextInputModel) getWidgetModel();

        TextTypeEnum type = model.getValueType();
        String text = model.getInputText();

        String toprint = "none";

        switch (type) {
            case TEXT:
                if (updatedPropertyId == null
                        || updatedPropertyId.equals(TextInputModel.PROP_INPUT_TEXT)) {
                    toprint = text;
                }
                break;
            case DOUBLE:
                if (updatedPropertyId == null
                        || updatedPropertyId.equals(TextInputModel.PROP_INPUT_TEXT)
                        || updatedPropertyId.equals(TextInputModel.PROP_PRECISION)) {
                    try {
                        try {
                            double d = Double.parseDouble(text);
                            numberFormat.setMaximumFractionDigits(model.getPrecision());
                            numberFormat.setMinimumFractionDigits(model.getPrecision());
                            toprint = numberFormat.format(d);
                        } catch (NumberFormatException e) {
                            toprint = text;
                        }

                    } catch (Exception e) {
                        toprint = text;
                    }
                }
                break;
            case ALIAS:
                if (updatedPropertyId == null
                        || updatedPropertyId.equals(TextInputModel.PROP_ALIASES)
                        || updatedPropertyId.equals(TextInputModel.PROP_PRIMARY_PV)) {
                    try {
                        toprint = ChannelReferenceValidationUtil.createCanonicalName(model
                                .getPrimaryPV(), model.getAllInheritedAliases());
                    } catch (ChannelReferenceValidationException e) {
                        toprint = model.getPrimaryPV();
                    }
                }
                break;
            case HEX:
                if (updatedPropertyId == null
                        || updatedPropertyId.equals(TextInputModel.PROP_INPUT_TEXT)) {
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
                if (updatedPropertyId == null
                        || updatedPropertyId.equals(TextInputModel.PROP_INPUT_TEXT)
                        || updatedPropertyId.equals(TextInputModel.PROP_PRECISION)) {
                    try {
                        String pattern = "0.";
                        for (int i = 0; i < model.getPrecision(); i++) {
                            if (i == 0) {
                                pattern = pattern.concat("0");
                            } else {
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
    public IValue getSample(final int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(index + " is not a valid sample index");
        }

        TextInputModel model = (TextInputModel) getWidgetModel();
        ITimestamp timestamp = TimestampFactory.now();

        // Note: the IValue implementations require a Severity, otherwise the
        // format() method will throw a NullPointerException. We don't really
        // have a severity here, so we fake one. This may cause problems for
        // clients who rely on getting a meaningful severity from the IValue.
        ISeverity severity = ValueFactory.createOKSeverity();

        IValue result;
        switch (model.getValueType()) {
            case DOUBLE:
                // try to convert the input text to a double
                double value = 0.0;
                try {
                    value = Double.parseDouble(model.getInputText());
                } catch (NumberFormatException e) {
                    // The interface IProcessVariableWithSamples doesn't define
                    // what to do in case of error and there aren't any declared
                    // checked exceptions for this method. So, the best we can
                    // do is to rethrow an unchecked exception and hope that the
                    // caller will handle it.
                    throw new IllegalStateException("Text input type is Double,"
                            + " but text is not a floating point value.", e);
                }
                // Have to create a meta data object because otherwise DoubleValue's
                // format() method might throw a NullPointerException :(
                int precision = model.getPrecision();
                INumericMetaData md = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0,
                        precision, "");

                result = ValueFactory.createDoubleValue(timestamp, severity, null, md,
                        Quality.Original, new double[] { value });
                break;
            case TEXT:
            case HEX: // hex and alias are undocumented, so treating them
            case ALIAS: // like text for now
                result = ValueFactory.createStringValue(timestamp, severity, null,
                        Quality.Original, new String[] { model.getInputText() });
                break;
            default:
                throw new AssertionError("Never get here");
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        // we always have one sample
        return 1;
    }

}
