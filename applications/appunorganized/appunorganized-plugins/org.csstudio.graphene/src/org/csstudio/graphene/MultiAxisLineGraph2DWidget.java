/**
 *
 */
package org.csstudio.graphene;

import static org.csstudio.graphene.PropertyConstants.*;
import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.formula.ExpressionLanguage.formulaArg;

import java.util.Objects;

import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.MultiAxisLineGraph2DRenderer;
import org.epics.graphene.MultiAxisLineGraph2DRendererUpdate;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.MultiAxisLineGraph2DExpression;

/**
 * A simple Line 2D plot which can handle both waveforms and a list of PVs
 *
 * @author shroffk
 *
 */
public class MultiAxisLineGraph2DWidget
        extends
        AbstractPointDatasetGraph2DWidget<MultiAxisLineGraph2DRendererUpdate, MultiAxisLineGraph2DExpression>
        implements ConfigurableWidget, ISelectionProvider {

    private InterpolationScheme interpolation = MultiAxisLineGraph2DRenderer.DEFAULT_INTERPOLATION_SCHEME;
    private boolean separateAreas = false;

    public MultiAxisLineGraph2DWidget(Composite parent, int style) {
        super(parent, style);
        setResizableAxis(false);
    }

    @Override
    protected MultiAxisLineGraph2DRendererUpdate createUpdate() {
        return getGraph().newUpdate()
                .interpolation(interpolation)
                .separateAreas(separateAreas);
    }

    protected MultiAxisLineGraph2DExpression createGraph() {
        MultiAxisLineGraph2DExpression graph = ExpressionLanguage.multiAxisLineGraphOf(
                formula(getDataFormula()), formulaArg(getXColumnFormula()),
                formulaArg(getYColumnFormula()));
        return graph;
    }

    @Override
    public void loadState(IMemento memento) {
        super.loadState(memento);
        if (memento != null) {
            if (memento.getString(PROP_INTERPOLATION_SCHEME) != null) {
                setInterpolation(InterpolationScheme.valueOf(memento.getString(PROP_INTERPOLATION_SCHEME)));
            }
            if (memento.getBoolean(PROP_SEPARATE_AREAS) != null) {
                setSeparateAreas(memento.getBoolean(PROP_INTERPOLATION_SCHEME));
            }
        }
    }

    public InterpolationScheme getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(InterpolationScheme interpolation) {
        InterpolationScheme oldValue = this.interpolation;
        this.interpolation = interpolation;
        if (!Objects.equals(oldValue, interpolation)) {
            getGraph().update(getGraph().newUpdate().interpolation(interpolation));
            changeSupport.firePropertyChange("interpolation", oldValue, interpolation);
        }
    }

    public boolean isSeparateAreas() {
        return separateAreas;
    }

    public void setSeparateAreas(boolean separateAreas) {
        boolean oldValue = this.separateAreas;
        this.separateAreas = separateAreas;
        if (!Objects.equals(oldValue, separateAreas)) {
            getGraph().update(getGraph().newUpdate().separateAreas(separateAreas));
            changeSupport.firePropertyChange("separateAreas", oldValue, separateAreas);
        }
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        memento.putString(PROP_INTERPOLATION_SCHEME, getInterpolation().toString());
        memento.putBoolean(PROP_SEPARATE_AREAS, isSeparateAreas());
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

    @Override
    public ISelection getSelection() {
        return new StructuredSelection(new MultiAxisLineGraph2DSelection(this));
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

    private MultiAxisLineGraph2DConfigurationDialog dialog;

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
        dialog = new MultiAxisLineGraph2DConfigurationDialog(this, "Configure Multi-Axis Line Graph");
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
