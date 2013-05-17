/**
 * 
 */
package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.formula.ExpressionLanguage.formulaArg;

import org.csstudio.ui.util.ConfigurableWidget;
import org.eclipse.swt.widgets.Composite;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraph2DRendererUpdate;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.LineGraph2DExpression;

/**
 * A simple Line 2D plot which can handle both waveforms and a list of PVs
 * 
 * @author shroffk
 * 
 */
public class LineGraph2DWidget extends AbstractPointDatasetGraph2DWidget<LineGraph2DRendererUpdate, LineGraph2DExpression>
		implements ConfigurableWidget {
	
	public LineGraph2DWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	protected LineGraph2DExpression createGraph() {
		LineGraph2DExpression graph = ExpressionLanguage.lineGraphOf(formula(getDataFormula()),
				formulaArg(getXColumnFormula()),
				formulaArg(getYColumnFormula()),
				formulaArg(getTooltipFormula()));
		graph.update(graph.newUpdate()
				.interpolation(InterpolationScheme.LINEAR));
		return graph;
	}

	/** Memento tag */
	private static final String MEMENTO_PVNAME = "PVName"; //$NON-NLS-1$
	//
	// public void saveState(IMemento memento) {
	// if (getPvName() != null) {
	// memento.putString(MEMENTO_PVNAME, getPvName());
	// }
	// }
	//
	// public void loadState(IMemento memento) {
	// if (memento != null) {
	// if (memento.getString(MEMENTO_PVNAME) != null) {
	// setPvName(memento.getString(MEMENTO_PVNAME));
	// }
	// }
	// }

	// @Override
	// public ISelection getSelection() {
	// if (getPvName() != null) {
	// return new StructuredSelection(new LineGraph2DSelection(
	// new ProcessVariable(getPvName()),
	// getXpvName() != null ? new ProcessVariable(getXpvName())
	// : null, this));
	// }
	// return null;
	// }
	//
	// @Override
	// public void addSelectionChangedListener(
	// final ISelectionChangedListener listener) {
	// }
	//
	// @Override
	// public void removeSelectionChangedListener(
	// ISelectionChangedListener listener) {
	// }
	//
	// @Override
	// public void setSelection(ISelection selection) {
	// throw new UnsupportedOperationException("Not implemented yet");
	// }

	private boolean configurable = true;

	private Graph2DConfigurationDialog dialog;

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
		dialog = new Graph2DConfigurationDialog(this, "Configure Line Graph");
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
