package org.csstudio.utility.pvmanager.widgets;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.formula.ExpressionLanguage;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.Alarm;
import org.epics.vtype.VImage;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;

/**
 * Widget that can display a formula that returns a VImage.
 * 
 * @author carcassi
 */
public class VImageWidget extends SelectionBeanComposite implements ISelectionProvider {

	private String pvFormula;
	private PVReader<?> pv;

	/**
	 * Creates a new display.
	 * 
	 * @param parent
	 */
	public VImageWidget(Composite parent) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);

		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setMarginBottom(5);

		imageDisplay = new VImageDisplay(this);
		imageDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		forwardPropertyChange(imageDisplay, "vImage", "value");
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
		imageDisplay.setMenu(menu);
	}

	private VImageDisplay imageDisplay;
	private ErrorBar errorBar;

	private void reconnect() {
		if (pv != null) {
			pv.close();
			pv= null;
		}

		if (pvFormula == null || pvFormula.trim().equals("")) {
			return;
		}

		pv = PVManager.read(ExpressionLanguage.formula(pvFormula, VImage.class))
				.notifyOn(SWTUtil.swtThread(this))
				.readListener(new PVReaderListener<Object>() {

					@Override
					public void pvChanged(
							final PVReaderEvent<Object> event) {
						errorBar.setException(event.getPvReader().lastException());
						Object value = event.getPvReader().getValue();
						imageDisplay.setVImage((VImage) value);
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
	
	public VImage getValue() {
		return imageDisplay.getVImage();
	}
	
	public Alarm getAlarm() {
		if (pv == null) {
			return ValueFactory.alarmNone();
		}
		return ValueUtil.alarmOf(pv.getValue(), pv.isConnected());
	}

	@Override
	public Object createSelection() {
		return new VImageWidgetSelection(this);
	}

}
