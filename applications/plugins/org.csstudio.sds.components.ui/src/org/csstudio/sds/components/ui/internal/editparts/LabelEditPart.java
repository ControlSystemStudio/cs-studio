/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.figures.ITextFigure;
import org.csstudio.ui.util.CustomMediaFactory;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * EditPart controller for the label widget.
 *
 * @author jbercic
 */
public final class LabelEditPart extends AbstractTextTypeWidgetEditPart {

    /**
     * Returns the casted model. This is just for convenience.
     *
     * @return the casted {@link LabelModel}
     */
    @Override
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
        final RefreshableLabelFigure refreshableFigure = new RefreshableLabelFigure();

        refreshableFigure.setFont(getModelFont(LabelModel.PROP_FONT));
        refreshableFigure.setTextAlignment(model.getTextAlignment());
        refreshableFigure.setTransparent(model.getTransparent());
        refreshableFigure.setRotation(model.getRotation());
        refreshableFigure.setXOff(model.getXOff());
        refreshableFigure.setYOff(model.getYOff());

        refreshableFigure.setTextValue(determineLabel(null));

        return refreshableFigure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        super.registerPropertyChangeHandlers();

        registerTextvaluePropertyChangeHandlers();
        registerFontPropertyChangeHandlers();
        registerTextAlignPropertyChangeHandlers();
        registerTextTransparentPropertyChangeHandlers();
        registerTextRotationPropertyChangeHandlers();
        registerXOffPropertyChangeHandlers();
        registerYOffPropertyChangeHandlers();

    }

    private void registerYOffPropertyChangeHandlers() {
        // changes to the y offset property
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) refreshableFigure;
                labelFigure.setYOff((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_YOFF, handle);
    }

    private void registerXOffPropertyChangeHandlers() {
        // changes to the x offset property
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) refreshableFigure;
                labelFigure.setXOff((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_XOFF, handle);
    }

    /**
     *
     */
    private void registerTextRotationPropertyChangeHandlers() {
        // changes to the text rotation property
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) refreshableFigure;
                labelFigure.setRotation((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_TEXT_ROTATION, handle);
    }

    private void registerTextTransparentPropertyChangeHandlers() {
        // changes to the transparency property
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) refreshableFigure;
                labelFigure.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_TRANSPARENT, handle);
    }

    private void registerTextAlignPropertyChangeHandlers() {
        // changes to the text alignment property
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                                        final Object newValue,
                                        final IFigure refreshableFigure) {
                RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) refreshableFigure;
                labelFigure.setTextAlignment((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_TEXT_ALIGN, handle);
    }

    private void registerFontPropertyChangeHandlers() {
        // changes to the font property
        setPropertyChangeHandler(LabelModel.PROP_FONT,
                                 new FontChangeHandler<RefreshableLabelFigure>() {

                                     @Override
                                     protected void doHandle(final RefreshableLabelFigure refreshableFigure,
                                                             final Font font) {
                                         refreshableFigure.setFont(font);
                                     }

                                 });
    }

    private void registerTextvaluePropertyChangeHandlers() {
        // Text
        IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ITextFigure textFigure = (ITextFigure) refreshableFigure;
                textFigure.setTextValue(determineLabel(null));
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_TEXTVALUE, labelHandler);
        setPropertyChangeHandler(LabelModel.PROP_TEXT_UNIT, labelHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request req) {
        Object type = req.getType();

        // entering a value is only allowed in run mode and when the widget is
        // enabled
        if ( (type != null)
                && (type.equals(RequestConstants.REQ_OPEN) || type
                        .equals(RequestConstants.REQ_DIRECT_EDIT))) {
            if ( (getExecutionMode() == ExecutionMode.RUN_MODE) && getCastedModel().isAccesible()) {
                super.performRequest(req);
            } else if (getExecutionMode() == ExecutionMode.EDIT_MODE) {
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
        currentValue = getWidgetModel().getStringProperty(LabelModel.PROP_TEXTVALUE);

        result.setValue(currentValue);
        final Text text = (Text) result.getControl();
        // input text
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if ( (e.keyCode == SWT.CR) || (e.keyCode == SWT.KEYPAD_CR)) {
                    SetPropertyCommand setPropertyCommand = new SetPropertyCommand(getWidgetModel(), LabelModel.PROP_TEXTVALUE, text.getText());
                    getViewer().getEditDomain().getCommandStack().execute(setPropertyCommand);
                } else if (e.keyCode == SWT.ESC) {
                    result.deactivate();
                }
            }

        });

        text.setForeground(getModelColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
        text.setFont(getModelFont(LabelModel.PROP_FONT));

        // calculate the chosen background color
        RGB backgroundRgb = getModelColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND).getRGB();

        int red = Math.min(backgroundRgb.red + INPUT_FIELD_BRIGHTNESS, 255);
        int green = Math.min(backgroundRgb.green + INPUT_FIELD_BRIGHTNESS, 255);
        int blue = Math.min(backgroundRgb.blue + INPUT_FIELD_BRIGHTNESS, 255);

        Color backgroundColor = CustomMediaFactory.getInstance()
                .getColor(new RGB(red, green, blue));

        text.setBackground(backgroundColor);
        text.selectAll();

        return result;
    }

    /**
     * Locate the given cell editor .
     *
     * @param cellEditor A cell editor.
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String determineLabel(final String updatedPropertyId) {
        String determineLabel = super.determineLabel(updatedPropertyId);
        LabelModel model = getCastedModel();
        String egu = handleText(updatedPropertyId, model, model.getStringProperty(LabelModel.PROP_TEXT_UNIT), "");

        if((determineLabel!=null)&&!determineLabel.isEmpty()&&(egu!=null)&&!egu.isEmpty()) {
            return determineLabel+" "+egu;
        }
        return determineLabel+egu;
    }
}
