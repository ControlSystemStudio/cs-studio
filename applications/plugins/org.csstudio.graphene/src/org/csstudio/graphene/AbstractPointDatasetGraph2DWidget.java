/**
 * 
 */
package org.csstudio.graphene;

import static org.epics.util.time.TimeDuration.ofHertz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.ui.util.composites.BeanComposite;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.RangeListener;
import org.csstudio.ui.util.widgets.StartEndRangeWidget;
import org.csstudio.ui.util.widgets.StartEndRangeWidget.ORIENTATION;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.epics.graphene.AxisRanges;
import org.epics.graphene.Graph2DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.graphene.Graph2DExpression;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.GraphDataRange;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractPointDatasetGraph2DWidget<U extends Graph2DRendererUpdate<U>, T extends Graph2DExpression<U>>
		extends AbstractGraph2DWidget<U,T> implements ConfigurableWidget {

	public AbstractPointDatasetGraph2DWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	private String xColumnFormula;
	private String yColumnFormula;
	private String tooltipColumnFormula;

	private static final String MEMENTO_X_COLUMN_FORMULA = "xColumnFormula"; //$NON-NLS-1$
	private static final String MEMENTO_Y_COLUMN_FORMULA = "yColumnFormula"; //$NON-NLS-1$
	private static final String MEMENTO_TOOLTIP_COLUMN_FORMULA = "tooltipFormula"; //$NON-NLS-1$

	public String getXColumnFormula() {
		return this.xColumnFormula;
	}

	public void setXColumnFormula(String xColumnFormula) {
		String oldValue = this.xColumnFormula;
		this.xColumnFormula = xColumnFormula;
		changeSupport.firePropertyChange("xColumnFormula", oldValue,
				this.xColumnFormula);
	}

	public String getYColumnFormula() {
		return this.yColumnFormula;
	}

	public void setYColumnFormula(String yColumnFormula) {
		String oldValue = this.yColumnFormula;
		this.yColumnFormula = yColumnFormula;
		changeSupport.firePropertyChange("yColumnFormula", oldValue,
				this.yColumnFormula);
	}

	public String getTooltipColumnFormula() {
		return this.tooltipColumnFormula;
	}

	public void setTooltipColumnFormula(String tooltipColumnFormula) {
		String oldValue = this.tooltipColumnFormula;
		this.tooltipColumnFormula = tooltipColumnFormula;
		changeSupport.firePropertyChange("tooltipFormula", oldValue,
				this.tooltipColumnFormula);
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		if (getXColumnFormula() != null) {
			memento.putString(MEMENTO_X_COLUMN_FORMULA, getXColumnFormula());
		}
		if (getYColumnFormula() != null) {
			memento.putString(MEMENTO_Y_COLUMN_FORMULA, getYColumnFormula());
		}
		if (getTooltipColumnFormula() != null) {
			memento.putString(MEMENTO_TOOLTIP_COLUMN_FORMULA, getTooltipColumnFormula());
		}
	}

	public void loadState(IMemento memento) {
		super.loadState(memento);
		if (memento != null) {
			if (memento.getString(MEMENTO_X_COLUMN_FORMULA) != null) {
				setXColumnFormula(memento.getString(MEMENTO_X_COLUMN_FORMULA));
			}
			if (memento.getString(MEMENTO_Y_COLUMN_FORMULA) != null) {
				setYColumnFormula(memento.getString(MEMENTO_Y_COLUMN_FORMULA));
			}
			if (memento.getString(MEMENTO_TOOLTIP_COLUMN_FORMULA) != null) {
				setTooltipColumnFormula(memento.getString(MEMENTO_TOOLTIP_COLUMN_FORMULA));
			}
		}
	}

}
