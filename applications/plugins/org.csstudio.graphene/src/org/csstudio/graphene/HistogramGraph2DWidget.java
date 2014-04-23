package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.formula.ExpressionLanguage.formulaArg;
import static org.epics.pvmanager.graphene.ExpressionLanguage.histogramGraphOf;
import static org.epics.pvmanager.vtype.ExpressionLanguage.vDouble;
import static org.epics.util.time.TimeDuration.ofHertz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.epics.graphene.AreaGraph2DRendererUpdate;
import org.epics.graphene.Graph2DRendererUpdate;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraph2DRendererUpdate;
import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.graphene.AreaGraph2DExpression;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.Graph2DExpression;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.HistogramGraph2DExpression;
import org.epics.pvmanager.graphene.LineGraph2DExpression;

public class HistogramGraph2DWidget
	extends
	AbstractPointDatasetGraph2DWidget<AreaGraph2DRendererUpdate, HistogramGraph2DExpression> {

    /**
     * Creates a new widget.
     * 
     * @param parent
     *            the parent
     * @param style
     *            the style
     */
    public HistogramGraph2DWidget(Composite parent, int style) {
    	super(parent, style);
    }
    
	protected HistogramGraph2DExpression createGraph() {
		return histogramGraphOf(formula(getDataFormula()));
	}

	@Override
	public boolean isConfigurable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openConfigurationDialog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConfigurationDialogOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void configurationDialogClosed() {
		// TODO Auto-generated method stub
		
	}

}
