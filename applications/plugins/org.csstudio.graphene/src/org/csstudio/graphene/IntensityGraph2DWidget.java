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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.epics.graphene.BubbleGraph2DRendererUpdate;
import org.epics.graphene.IntensityGraph2DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.graphene.BubbleGraph2DExpression;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.IntensityGraph2DExpression;
import org.epics.util.array.ArrayDouble;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.table.VTableFactory;

/**
 * @author shroffk
 * 
 */
public class IntensityGraph2DWidget extends AbstractGraph2DWidget<IntensityGraph2DRendererUpdate, IntensityGraph2DExpression>
	implements ISelectionProvider {
	
	public IntensityGraph2DWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected IntensityGraph2DExpression createGraph() {
		IntensityGraph2DExpression graph = ExpressionLanguage.intensityGraphOf(formula(getDataFormula()));
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
