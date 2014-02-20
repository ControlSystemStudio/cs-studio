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
import org.eclipse.ui.IMemento;
import org.epics.graphene.BubbleGraph2DRendererUpdate;
import org.epics.pvmanager.graphene.BubbleGraph2DExpression;
import org.epics.pvmanager.graphene.ExpressionLanguage;

/**
 * @author shroffk
 * 
 */
public class BubbleGraph2DWidget extends AbstractPointDatasetGraph2DWidget<BubbleGraph2DRendererUpdate, BubbleGraph2DExpression>
	implements ISelectionProvider {
	
	public BubbleGraph2DWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected BubbleGraph2DExpression createGraph() {
		BubbleGraph2DExpression graph = ExpressionLanguage.bubbleGraphOf(formula(getDataFormula()),
				formulaArg(getXColumnFormula()),
				formulaArg(getYColumnFormula()),
				formulaArg(getSizeColumnFormula()),
				formulaArg(getTooltipColumnFormula()));
		return graph;
	}

	private String sizeColumnFormula;
	
	private static final String MEMENTO_SIZE_COLUMN_FORMULA = "sizeColumnFormula"; //$NON-NLS-1$

	public String getSizeColumnFormula() {
		return this.sizeColumnFormula;
	}

	public void setSizeColumnFormula(String sizeColumnFormula) {
		String oldValue = this.sizeColumnFormula;
		this.sizeColumnFormula = sizeColumnFormula;
		changeSupport.firePropertyChange("xColumnFormula", oldValue,
				this.sizeColumnFormula);
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		if (getSizeColumnFormula() != null) {
			memento.putString(MEMENTO_SIZE_COLUMN_FORMULA, getSizeColumnFormula());
		}
	}

	public void loadState(IMemento memento) {
		if (memento != null) {
			if (memento.getString(MEMENTO_SIZE_COLUMN_FORMULA) != null) {
				setSizeColumnFormula(memento.getString(MEMENTO_SIZE_COLUMN_FORMULA));
			}
		}
	}

	@Override
	public ISelection getSelection() {
		if (getDataFormula() != null) {
			return new StructuredSelection(new BubbleGraph2DSelection(this));
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

	private BubbleGraph2DConfigurationDialog dialog;

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
		dialog = new BubbleGraph2DConfigurationDialog(this, "Configure Bubble Graph");
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
