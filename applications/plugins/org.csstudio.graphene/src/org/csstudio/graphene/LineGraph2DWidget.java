/**
 *
 */
package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.formula.ExpressionLanguage.formulaArg;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraph2DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.LineGraph2DExpression;
import org.epics.util.array.ArrayDouble;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.table.VTableFactory;

/**
 * A simple Line 2D plot which can handle both waveforms and a list of PVs
 *
 * @author shroffk
 *
 */
public class LineGraph2DWidget
        extends
        AbstractPointDatasetGraph2DWidget<LineGraph2DRendererUpdate, LineGraph2DExpression>
        implements ConfigurableWidget, ISelectionProvider {

    private PVWriter<Object> selectionValueWriter;

    public LineGraph2DWidget(Composite parent, int style) {
        super(parent, style);
        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("highlightSelectionValue") && getGraph() != null) {
                    getGraph().update(getGraph().newUpdate().highlightFocusValue((Boolean) evt.getNewValue()));
                }

            }
        });
        getImageDisplay().addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                if (isHighlightSelectionValue() && getGraph() != null) {
                    getGraph().update(getGraph().newUpdate().focusPixel(e.x));
                }
            }
        });

        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selectionValuePv") && getGraph() != null) {
                    if (selectionValueWriter != null) {
                        selectionValueWriter.close();
                        selectionValueWriter = null;
                    }

                    if (getSelectionValuePv() == null || getSelectionValuePv().trim().isEmpty()) {
                        return;
                    }

                    selectionValueWriter = PVManager.write(formula(getSelectionValuePv()))
                            .writeListener(new PVWriterListener<Object>() {
                                @Override
                                public void pvChanged(
                                        PVWriterEvent<Object> event) {
                                    if (event.isWriteFailed()) {
                                        Logger.getLogger(LineGraph2DWidget.class.getName())
                                        .log(Level.WARNING, "Line graph selection notification failed", event.getPvWriter().lastWriteException());
                                    }
                                }
                            })
                            .async();
                    if (getSelectionValue() != null) {
                        selectionValueWriter.write(getSelectionValue());
                    }

                }

            }
        });
        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selectionValue") && selectionValueWriter != null) {
                    if (getSelectionValue() != null) {
                        selectionValueWriter.write(getSelectionValue());
                    }
                }

            }
        });
    }

    protected LineGraph2DExpression createGraph() {
        LineGraph2DExpression graph = ExpressionLanguage.lineGraphOf(
                formula(getDataFormula()), formulaArg(getXColumnFormula()),
                formulaArg(getYColumnFormula()),
                formulaArg(getTooltipColumnFormula()));
        graph.update(graph.newUpdate()
                .interpolation(InterpolationScheme.LINEAR)
                .highlightFocusValue(isHighlightSelectionValue()));
        return graph;
    }

    private boolean highlightSelectionValue = false;
    private VTable selectionValue;
    private String selectionValuePv;

    public String getSelectionValuePv() {
        return selectionValuePv;
    }

    public void setSelectionValuePv(String selectionValuePv) {
        String oldValue = this.selectionValuePv;
        this.selectionValuePv = selectionValuePv;
        changeSupport.firePropertyChange("selectionValuePv", oldValue, this.selectionValuePv);
    }

    public boolean isHighlightSelectionValue() {
        return highlightSelectionValue;
    }

    public void setHighlightSelectionValue(boolean highlightSelectionValue) {
        boolean oldValue = this.highlightSelectionValue;
        this.highlightSelectionValue = highlightSelectionValue;
        changeSupport.firePropertyChange("highlightSelectionValue", oldValue, this.highlightSelectionValue);
    }

    public VTable getSelectionValue() {
        return selectionValue;
    }

    private void setSelectionValue(VTable selectionValue) {
        VTable oldValue = this.selectionValue;
        this.selectionValue = selectionValue;
        changeSupport.firePropertyChange("selectionValue", oldValue, this.selectionValue);
    }

    private static final String MEMENTO_HIGHLIGHT_SELECTION_VALUE = "highlightSelectionValue"; //$NON-NLS-1$

    @Override
    public void loadState(IMemento memento) {
        super.loadState(memento);
        if (memento != null) {
            if (memento.getBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE) != null) {
                setHighlightSelectionValue(memento.getBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE));
            }
        }
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        memento.putBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE, isHighlightSelectionValue());
    }

    @Override
    protected void processInit() {
        super.processInit();
        processValue();
    }

    @Override
    protected void processValue() {
        Graph2DResult result = getCurrentResult();
        if (result == null || result.getData() == null) {
            setSelectionValue(null);
        } else {
            int index = result.focusDataIndex();
            if (index == -1) {
                setSelectionValue(null);
            } else {
                if (result.getData() instanceof VTable) {
                    VTable data = (VTable) result.getData();
                    setSelectionValue(VTableFactory.extractRow(data, index));
                    return;
                }
                if (result.getData() instanceof VNumberArray) {
                    VNumberArray data = (VNumberArray) result.getData();
                    VTable selection = ValueFactory.newVTable(Arrays.<Class<?>>asList(double.class, double.class),
                            Arrays.asList("X", "Y"),
                            Arrays.<Object>asList(new ArrayDouble(index), new ArrayDouble(data.getData().getDouble(index))));
                    setSelectionValue(selection);
                    return;
                }
                setSelectionValue(null);
            }
        }
    }

    @Override
    public ISelection getSelection() {
        return new StructuredSelection(new LineGraph2DSelection(this));
    }

    @Override
    public void addSelectionChangedListener(
            final ISelectionChangedListener listener) {
    }

    @Override
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
    }

    @Override
    public void setSelection(ISelection selection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private boolean configurable = true;

    private LineGraph2DConfigurationDialog dialog;

    @Override
    public boolean isConfigurable() {
        return this.configurable;
    }

    @Override
    public void setConfigurable(boolean configurable) {
        boolean oldValue = this.configurable;
        this.configurable = configurable;
        changeSupport.firePropertyChange("configurable", oldValue,
                this.configurable);
    }

    @Override
    public void openConfigurationDialog() {
        if (dialog != null)
            return;
        dialog = new LineGraph2DConfigurationDialog(this, "Configure Line Graph");
        dialog.open();
    }

    @Override
    public boolean isConfigurationDialogOpen() {
        return dialog != null;
    }

    @Override
    public void configurationDialogClosed() {
        dialog = null;
    }
}
