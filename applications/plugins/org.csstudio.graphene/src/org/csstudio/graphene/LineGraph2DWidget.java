/**
 * 
 */
package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.*;
import static org.epics.pvmanager.formula.ExpressionLanguage.formulaArg;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.ui.util.ConfigurableWidget;
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
import org.epics.pvmanager.formula.TableFunctionSet;
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
	
	private PVWriter<Object> focusValueWriter;

	public LineGraph2DWidget(Composite parent, int style) {
		super(parent, style);
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("highlightFocusValue") && getGraph() != null) {
					getGraph().update(getGraph().newUpdate().highlightFocusValue((Boolean) evt.getNewValue()));
				}
				
			}
		});
		getImageDisplay().addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				if (isHighlightFocusValue() && getGraph() != null) {
					getGraph().update(getGraph().newUpdate().focusPixel(e.x));
				}
			}
		});
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("focusValuePv") && getGraph() != null) {
					if (focusValueWriter != null) {
						focusValueWriter.close();
						focusValueWriter = null;
					}
					
					if (getFocusValuePv() == null || getFocusValuePv().trim().isEmpty()) {
						return;
					}
					
					focusValueWriter = PVManager.write(formula(getFocusValuePv()))
							.writeListener(new PVWriterListener<Object>() {
								@Override
								public void pvChanged(
										PVWriterEvent<Object> event) {
									if (event.isWriteFailed()) {
										Logger.getLogger(LineGraph2DWidget.class.getName())
										.log(Level.WARNING, "Line graph focus notification failed", event.getPvWriter().lastWriteException());
									}
								}
							})
							.async();
					if (getFocusValue() != null) {
						focusValueWriter.write(getFocusValue());
					}
							
				}
				
			}
		});
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("focusValue") && focusValueWriter != null) {
					if (getFocusValue() != null) {
						focusValueWriter.write(getFocusValue());
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
				.highlightFocusValue(isHighlightFocusValue()));
		return graph;
	}
	
	private boolean highlightFocusValue = false;
	private VTable focusValue;
	private String focusValuePv;
	
	public String getFocusValuePv() {
		return focusValuePv;
	}
	
	public void setFocusValuePv(String focusValuePv) {
		String oldValue = this.focusValuePv;
		this.focusValuePv = focusValuePv;
		changeSupport.firePropertyChange("focusValuePv", oldValue, this.focusValuePv);
	}
	
	public boolean isHighlightFocusValue() {
		return highlightFocusValue;
	}
	
	public void setHighlightFocusValue(boolean highlightFocusValue) {
		boolean oldValue = this.highlightFocusValue;
		this.highlightFocusValue = highlightFocusValue;
		changeSupport.firePropertyChange("highlightFocusValue", oldValue, this.highlightFocusValue);
	}
	
	public VTable getFocusValue() {
		return focusValue;
	}
	
	private void setFocusValue(VTable focusValue) {
		VTable oldValue = this.focusValue;
		this.focusValue = focusValue;
		changeSupport.firePropertyChange("focusValue", oldValue, this.focusValue);
	}
	
	private static final String MEMENTO_HIGHLIGHT_FOCUS_VALUE = "highlightFocusValue"; //$NON-NLS-1$
	
	@Override
	public void loadState(IMemento memento) {
		super.loadState(memento);
		if (memento != null) {
			if (memento.getBoolean(MEMENTO_HIGHLIGHT_FOCUS_VALUE) != null) {
				setHighlightFocusValue(memento.getBoolean(MEMENTO_HIGHLIGHT_FOCUS_VALUE));
			}
		}
	}
	
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putBoolean(MEMENTO_HIGHLIGHT_FOCUS_VALUE, isHighlightFocusValue());
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
			setFocusValue(null);
		} else {
			int index = result.focusDataIndex();
			if (index == -1) {
				setFocusValue(null);
			} else {
				if (result.getData() instanceof VTable) {
					VTable data = (VTable) result.getData();
					setFocusValue(VTableFactory.extractRow(data, index));
					return;
				}
				if (result.getData() instanceof VNumberArray) {
					VNumberArray data = (VNumberArray) result.getData();
					VTable selection = ValueFactory.newVTable(Arrays.<Class<?>>asList(double.class, double.class),
							Arrays.asList("X", "Y"), 
							Arrays.<Object>asList(new ArrayDouble(index), new ArrayDouble(data.getData().getDouble(index))));
					setFocusValue(selection);
					return;
				}
				setFocusValue(null);
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
