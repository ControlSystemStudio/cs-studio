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

import org.csstudio.sds.components.model.WaveformModel;
import org.csstudio.sds.components.ui.internal.figures.WaveformFigure;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the Waveform widget. The controller mediates between
 * {@link WaveformModel} and {@link WaveformFigure}.
 *
 * @author Sven Wende, Kai Meyer, Joerg Rathlev
 *
 */
public final class WaveformEditPart extends AbstractChartEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        WaveformModel model = (WaveformModel) getWidgetModel();
        WaveformFigure figure = new WaveformFigure(WaveformModel.NUMBER_OF_ARRAYS);
        initializeCommonFigureProperties(figure, model);
        initializeDataProperties(figure, model);
        return figure;
    }

    /**
     * Initializes the data properties of the figure.
     *
     * @param figure
     *            the figure.
     * @param model
     *            the model.
     */
    private void initializeDataProperties(final WaveformFigure figure,
            final WaveformModel model) {
        for (int i = 0; i < model.numberOfDataSeries(); i++) {
            figure.setData(i, model.getData(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();
        registerDataPropertyChangeHandlers();
    }

    /**
     * Registers the property change handlers for the waveform data properties.
     */
    private void registerDataPropertyChangeHandlers() {
        /**
         * Change handler for the waveform data properties.
         */
        class DataChangeHandler implements IWidgetPropertyChangeHandler {

            private final int _index;

            /**
             * Constructor.
             * @param index the index of the data array.
             */
            DataChangeHandler(final int index) {
                _index = index;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                WaveformFigure figure = (WaveformFigure) refreshableFigure;
                figure.setData(_index, (double[]) newValue);
                return true;
            }
        }

        for (int i = 0; i < WaveformModel.NUMBER_OF_ARRAYS; i++) {
            setPropertyChangeHandler(WaveformModel.dataPropertyId(i),
                    new DataChangeHandler(i));
        }
    }
}
