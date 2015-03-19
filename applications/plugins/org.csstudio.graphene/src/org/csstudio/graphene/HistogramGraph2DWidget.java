package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.graphene.ExpressionLanguage.histogramGraphOf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.epics.graphene.AreaGraph2DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.HistogramGraph2DExpression;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueUtil;

public class HistogramGraph2DWidget
		extends
		AbstractGraph2DWidget<AreaGraph2DRendererUpdate, HistogramGraph2DExpression>
		implements ISelectionProvider {

	private PVWriter<Object> selectionValueWriter;
	
	private boolean highlightSelectionValue = false;
	
	private static final String MEMENTO_HIGHLIGHT_SELECTION_VALUE = "highlightSelectionValue"; //$NON-NLS-1$

	private MouseMoveListener hoverSelection = new MouseMoveListener() {
		
		@Override
		public void mouseMove(MouseEvent e) {
			if (isHighlightSelectionValue() && getGraph() != null) {
				getGraph().update(getGraph().newUpdate().focusPixel(e.x));
			}
		}
	};

	private ClickSelectionMethodListener clickSelection = new ClickSelectionMethodListener();
	
	private class ClickSelectionMethodListener implements MouseListener, MouseMoveListener {

		@Override
		public void mouseMove(MouseEvent e) {
			if ((e.stateMask & SWT.BUTTON1) != 0) {
				if (isHighlightSelectionValue() && getGraph() != null) {
					getGraph().update(getGraph().newUpdate().focusPixel(e.x));
				}
			}
			
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
			if (isHighlightSelectionValue() && getGraph() != null && e.button == 1) {
				getGraph().update(getGraph().newUpdate().focusPixel(e.x));
			}
		}

		@Override
		public void mouseUp(MouseEvent e) {
		}
		
	}
	
	
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
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("highlightSelectionValue") && getGraph() != null) {
					getGraph().update(getGraph().newUpdate().highlightFocusValue((Boolean) evt.getNewValue()));
				}
				
			}
		});
		getImageDisplay().addMouseMoveListener(hoverSelection);
		
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
										Logger.getLogger(BubbleGraph2DWidget.class.getName())
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
    
	protected HistogramGraph2DExpression createGraph() {
		HistogramGraph2DExpression graph = histogramGraphOf(formula(getDataFormula()));
		graph.update(graph.newUpdate().highlightFocusValue(isHighlightSelectionValue()));
		return graph;
	}
	
	public boolean isHighlightSelectionValue() {
		return highlightSelectionValue;
	}
	
	public void setHighlightSelectionValue(boolean highlightSelectionValue) {
		boolean oldValue = this.highlightSelectionValue;
		this.highlightSelectionValue = highlightSelectionValue;
		changeSupport.firePropertyChange("highlightSelectionValue", oldValue, this.highlightSelectionValue);
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE, isHighlightSelectionValue());
	}

	public void loadState(IMemento memento) {
		super.loadState(memento);
		if (memento != null) {
			if (memento.getBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE) != null) {
				setHighlightSelectionValue(memento.getBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE));
			}
		}
	}
	
	private VNumberArray selectionValue;
	private String selectionValuePv;
	private MouseSelectionMethod mouseSelectionMethod = MouseSelectionMethod.HOVER;
	
	public String getSelectionValuePv() {
		return selectionValuePv;
	}
	
	public void setSelectionValuePv(String selectionValuePv) {
		String oldValue = this.selectionValuePv;
		this.selectionValuePv = selectionValuePv;
		changeSupport.firePropertyChange("selectionValuePv", oldValue, this.selectionValuePv);
	}
	
	public VNumberArray getSelectionValue() {
		return selectionValue;
	}
	
	private void setSelectionValue(VNumberArray selectionValue) {
		VNumberArray oldValue = this.selectionValue;
		this.selectionValue = selectionValue;
		if (oldValue != this.selectionValue) {
			changeSupport.firePropertyChange("selectionValue", oldValue, this.selectionValue);
		}
	}
	
	public MouseSelectionMethod getMouseSelectionMethod() {
		return mouseSelectionMethod;
	}
	
	public void setMouseSelectionMethod(
			MouseSelectionMethod mouseSelectionMethod) {
		MouseSelectionMethod oldValue = this.mouseSelectionMethod;
		this.mouseSelectionMethod = mouseSelectionMethod;
		if (oldValue != this.mouseSelectionMethod) {
			// Remove old listener
			switch(oldValue) {
			case CLICK:
				getImageDisplay().removeMouseListener(clickSelection);
				getImageDisplay().removeMouseMoveListener(clickSelection);
				break;
			case HOVER:
				getImageDisplay().removeMouseMoveListener(hoverSelection);
				break;
			default:
				break;
			}
			// Add new listener
			switch(mouseSelectionMethod) {
			case CLICK:
				getImageDisplay().addMouseListener(clickSelection);
				getImageDisplay().addMouseMoveListener(clickSelection);
				break;
			case HOVER:
				getImageDisplay().addMouseMoveListener(hoverSelection);
				break;
			default:
				break;
			}
			changeSupport.firePropertyChange("mouseSelectionMethod", oldValue, this.mouseSelectionMethod);
		}
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
				if (result.getData() instanceof VNumberArray) {
					VNumberArray data = (VNumberArray) result.getData();
					VNumberArray selection = ValueUtil.subArray(data, index);
					setSelectionValue(selection);
					return;
				}
				setSelectionValue(null);
			}
		}
	}

	@Override
	public ISelection getSelection() {
		if (getDataFormula() != null) {
			return new StructuredSelection(new HistogramGraph2DSelection(this));
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

	private HistogramGraph2DConfigurationDialog dialog;

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
		dialog = new HistogramGraph2DConfigurationDialog(this, "Configure Histogram");
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
