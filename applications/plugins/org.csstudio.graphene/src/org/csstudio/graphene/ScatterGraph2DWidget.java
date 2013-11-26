/**
 * 
 */
package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.formula.ExpressionLanguage.formulaArg;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.ScatterGraph2DExpression;

/**
 * @author shroffk
 * 
 */
public class ScatterGraph2DWidget extends AbstractPointDatasetGraph2DWidget<ScatterGraph2DRendererUpdate, ScatterGraph2DExpression>
	implements ISelectionProvider {
	
	public ScatterGraph2DWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected ScatterGraph2DExpression createGraph() {
		ScatterGraph2DExpression graph = ExpressionLanguage.scatterGraphOf(formula(getDataFormula()),
				formulaArg(getXColumnFormula()),
				formulaArg(getYColumnFormula()),
				formulaArg(getTooltipColumnFormula()));		
		return graph;
	}


	@Override
	public ISelection getSelection() {
		if (getDataFormula() != null) {
			return new StructuredSelection(new ScatterGraph2DSelection(this));
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

	private ScatterGraph2DConfigurationDialog dialog;

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
		dialog = new ScatterGraph2DConfigurationDialog(this, "Configure Scatter Graph");
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
