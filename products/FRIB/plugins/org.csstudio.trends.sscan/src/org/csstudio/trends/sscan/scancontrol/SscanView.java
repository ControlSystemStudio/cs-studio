package org.csstudio.trends.sscan.scancontrol;

import javax.annotation.PreDestroy;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.trends.sscan.propsheet.SscanPropertySheetPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;

public class SscanView extends ViewPart{

	public SscanView() {
	}
	/** Memento used to preserve the PV name. */
	private IMemento memento = null;
	 /** View ID registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.sscan.scancontrol.SscanView";

	/** Memento tag */
	private static final String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$
    private static final String SSCAN_PV_TAG = "sscan_pv"; //$NON-NLS-1$
    private ProcessVariable SscanPVName;
    private ControlView controlGui;
    
	
	@Override
	public void init(final IViewSite site, final IMemento memento)
			throws PartInitException {
		super.init(site, memento);
		// Save the memento
		this.memento = memento;
	}
	
	@Override
	public void saveState(final IMemento memento) {
		super.saveState(memento);
		// Save the currently selected variable
		if (SscanPVName != null) {
			//memento.putString(SSCAN_PV_TAG, SscanPVName.getName());
			//memento.putString(PV_LIST_TAG, SscanPVName.getName());
		}
	}
	
	/**
	 * Create contents of the view part.
	 */
	public void createPartControl(Composite parent) {
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setSashWidth(1);
		
		controlGui = new ControlView(sashForm, SWT.NONE, SSCAN_PV_TAG, PV_LIST_TAG);
		final PropertiesView propertiesGui = new PropertiesView(sashForm, SWT.NONE, SSCAN_PV_TAG);	
		final PositionerView positionerGui = new PositionerView(sashForm, SWT.NONE, SSCAN_PV_TAG);
		final DetectorView detectorGui = new DetectorView(sashForm, SWT.NONE, SSCAN_PV_TAG);
		final SscanListener sscan_listener = new SscanListener()
	    {

			@Override
			public void PVName(String name) {
				if (ID.equals(getSite().getId())) {
					setPartName(name);
				}
			}

			@Override
			public void PVName(ProcessVariable name) {
				propertiesGui.setPVName(name);
				positionerGui.setPVName(name);
				detectorGui.setPVName(name);
			}

			@Override
			public void ScanEvent(String name, int status) {
				// TODO Auto-generated method stub
				
			}
			
	    };
		controlGui.addListener(sscan_listener);
		sashForm.setWeights(new int[] {143, 144, 240, 321});
		
	}
	
	public ControlView getControlComposite(){
		return controlGui;
	}

	@PreDestroy
	public void dispose() {
	}

	public void setFocus() {
		controlGui.setFocus();
	}
}
