package org.csstudio.utility.pvmanager.widgets;

import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.datasource.formula.ExpressionLanguage;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.time.TimeDuration;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VTypeValueEquals;
import org.diirt.vtype.ValueFactory;
import org.diirt.vtype.ValueUtil;
import org.diirt.vtype.table.VTableFactory;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * Widget that can display a formula that returns a VTable.
 * 
 * @author carcassi
 */
public class VTableWidget extends SelectionBeanComposite implements AlarmProvider, ISelectionProvider {

	private String pvFormula;
	private PVReader<?> pv;

	/**
	 * Creates a new display.
	 * 
	 * @param parent
	 */
	public VTableWidget(Composite parent) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);

		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setMarginBottom(5);

		tableDisplay = new VTableDisplay(this);
		tableDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		tableDisplay.tableViewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				int index = tableDisplay.tableViewer.getTable().getSelectionIndex();
				if (index >= 0) {
					setSelectionValue(VTableFactory.select(getValue(), new ArrayInt(index)));
				}
			}
		});
		forwardPropertyChange(tableDisplay, "vTable", "value");
		forwardPropertyChangeToSelection("value");
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (pv != null) {
					pv.close();
					pv = null;
				}
			}
		});
	}
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		tableDisplay.setMenu(menu);
	}

	@Override
    public void setFont(final Font font)
    {
        super.setFont(font);
        tableDisplay.setFont(font);
        errorBar.setFont(font);
    }

    private VTableDisplay tableDisplay;
	private ErrorBar errorBar;

	private void reconnect() {
		if (pv != null) {
			pv.close();
			pv= null;
		}

		if (pvFormula == null || pvFormula.trim().equals("")) {
			return;
		}

		pv = PVManager.read(ExpressionLanguage.formula(pvFormula, VTable.class))
				.notifyOn(SWTUtil.swtThread(this))
				.readListener(new PVReaderListener<Object>() {

					@Override
					public void pvChanged(
							final PVReaderEvent<Object> event) {
						errorBar.setException(event.getPvReader().lastException());
						Object value = event.getPvReader().getValue();
						tableDisplay.setVTable((VTable) value);
						setAlarm(calculateAlarm());
					}
				}).maxRate(TimeDuration.ofHertz(25));

	}
	
	public String getPvFormula() {
		return pvFormula;
	}
	
	public void setPvFormula(String pvFormula) {
		this.pvFormula = pvFormula;
		reconnect();
	}
	
	public VTable getValue() {
		return tableDisplay.getVTable();
	}
	
	private VTable selectionValue;
	
	public VTable getSelectionValue() {
		return selectionValue;
	}
	
	public void setSelectionValue(VTable selectionValue) {
		VTable oldSelectionValue = this.selectionValue;
		this.selectionValue = selectionValue;
		changeSupport.firePropertyChange("selectionValue", oldSelectionValue, selectionValue);
	}
	
	VTableDisplayCell getCell() {
		if (tableDisplay.getSelection().isEmpty()) {
			return null;
		} else {
			return (VTableDisplayCell) ((StructuredSelection) tableDisplay.getSelection()).getFirstElement();
		}
	}
	
	private Alarm alarm = calculateAlarm();
	
	@Override
	public Alarm getAlarm() {
		return alarm;
	}
	
	public void setAlarm(Alarm alarm) {
		if (VTypeValueEquals.alarmEquals(this.alarm, alarm)) {
			return;
		}
		
		Alarm oldAlarm = this.alarm;
		this.alarm = alarm;
		changeSupport.firePropertyChange("alarm", oldAlarm, alarm);
	}
	
	private Alarm calculateAlarm() {
		if (pv == null) {
			return ValueFactory.alarmNone();
		}
		return ValueUtil.alarmOf(getValue(), pv.isConnected());
	}

	@Override
	public Object createSelection() {
		return new VTableWidgetSelection(this);
	}

}