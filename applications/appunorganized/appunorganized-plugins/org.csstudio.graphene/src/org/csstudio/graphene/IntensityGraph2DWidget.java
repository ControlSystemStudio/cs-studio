/**
 *
 */
package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.formula;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.epics.graphene.IntensityGraph2DRenderer;
import org.epics.graphene.IntensityGraph2DRendererUpdate;
import org.epics.graphene.NumberColorMap;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.IntensityGraph2DExpression;

/**
 * @author shroffk
 *
 */
public class IntensityGraph2DWidget extends AbstractGraph2DWidget<IntensityGraph2DRendererUpdate, IntensityGraph2DExpression>
    implements ISelectionProvider {

    private NumberColorMap colorMap;
    private boolean drawLegend;

    {
        IntensityGraph2DRenderer renderer = new IntensityGraph2DRenderer();
        colorMap = renderer.getColorMap();
        drawLegend = renderer.isDrawLegend();
    }

    public IntensityGraph2DWidget(Composite parent, int style) {
        super(parent, style);
        final List<String> updatePropertyNames = Arrays.asList("colorMap", "drawLegend");
        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (getGraph() != null && updatePropertyNames.contains(evt.getPropertyName())) {
                    updateGraph(getGraph());
                }

            }
        });
    }

    private void updateGraph(IntensityGraph2DExpression graph) {
        graph.update(graph.newUpdate()
                .colorMap(colorMap)
                .drawLegend(drawLegend));
    }

    @Override
    protected IntensityGraph2DExpression createGraph() {
        IntensityGraph2DExpression graph = ExpressionLanguage.intensityGraphOf(formula(getDataFormula()));
        updateGraph(graph);
        return graph;
    }


    public void saveState(IMemento memento) {
        super.saveState(memento);
    }

    public void loadState(IMemento memento) {
        super.loadState(memento);
    }

    @Override
    protected void processInit() {
        super.processInit();
        processValue();
    }

    @Override
    protected void processValue() {
        Graph2DResult result = getCurrentResult();
    }

    public boolean isDrawLegend() {
        return drawLegend;
    }

    public void setDrawLegend(boolean drawLegend) {
        boolean oldValue = this.drawLegend;
        this.drawLegend = drawLegend;
        changeSupport.firePropertyChange("drawLegend", oldValue,
                this.drawLegend);
    }

    public NumberColorMap getColorMap() {
        return colorMap;
    }

    public void setColorMap(NumberColorMap colorMap) {
        NumberColorMap oldValue = this.colorMap;
        this.colorMap = colorMap;
        changeSupport.firePropertyChange("colorMap", oldValue,
                this.colorMap);
    }

    @Override
    public ISelection getSelection() {
        if (getDataFormula() != null) {
            return new StructuredSelection(new IntensityGraph2DSelection(this));
        }
        return null;
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

    private IntensityGraph2DConfigurationDialog dialog;

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
        dialog = new IntensityGraph2DConfigurationDialog(this, "Configure Intensity Graph");
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
