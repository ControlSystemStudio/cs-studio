package org.csstudio.utility.pvamanger.widgets.test;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.dnd.SwtUtil;
import org.eclipse.ui.part.ViewPart;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VImage;

import com.swtdesigner.SWTResourceManager;
import static org.epics.pvmanager.extra.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ExpressionLanguage.*;

public class VImageDisplayDemo extends ViewPart {

	public static final String ID = "org.csstudio.utility.pvamanger.widgets.test.VImageDisplayDemo"; //$NON-NLS-1$

	private PV<VImage> pv;
	
	public VImageDisplayDemo() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER);
		FillLayout fl_container = new FillLayout(SWT.VERTICAL);
		fl_container.spacing = 10;
		container.setLayout(fl_container);
		
		Composite composite = new Composite(container, SWT.NONE);
		FillLayout fl_composite = new FillLayout(SWT.HORIZONTAL);
		fl_composite.spacing = 10;
		composite.setLayout(fl_composite);
		
		final VImageDisplay topLeft = new VImageDisplay(composite);
		topLeft.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		topLeft.setAlignment(SWT.TOP | SWT.LEFT);
		
		final VImageDisplay top = new VImageDisplay(composite);
		top.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		top.setAlignment(SWT.TOP);
		
		final VImageDisplay topRight = new VImageDisplay(composite);
		topRight.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		topRight.setAlignment(SWT.TOP | SWT.RIGHT);
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_1.spacing = 10;
		composite_1.setLayout(fl_composite_1);
		
		final VImageDisplay centerLeft = new VImageDisplay(composite_1);
		centerLeft.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		centerLeft.setAlignment(SWT.CENTER | SWT.LEFT);
		
		final VImageDisplay center = new VImageDisplay(composite_1);
		center.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		center.setAlignment(SWT.CENTER);
		
		final VImageDisplay centerRight = new VImageDisplay(composite_1);
		centerRight.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		centerRight.setAlignment(SWT.CENTER | SWT.RIGHT);
		
		Composite composite_2 = new Composite(container, SWT.NONE);
		FillLayout fl_composite_2 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_2.spacing = 10;
		composite_2.setLayout(fl_composite_2);
		
		final VImageDisplay bottomLeft = new VImageDisplay(composite_2);
		bottomLeft.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bottomLeft.setAlignment(SWT.BOTTOM | SWT.LEFT);
		
		final VImageDisplay bottom = new VImageDisplay(composite_2);
		bottom.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bottom.setAlignment(SWT.BOTTOM);
		
		final VImageDisplay bottomRight = new VImageDisplay(composite_2);
		bottomRight.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bottomRight.setAlignment(SWT.BOTTOM | SWT.RIGHT);

		createActions();
		initializeToolBar();
		initializeMenu();
		
		if (pv != null) {
			pv.close();
		}
		pv = PVManager.read(waterfallPlotOf(vDoubleArray("sim://gaussianWaveform(1, 50, 0.1)")))
		.andNotify(SWTUtil.onSWTThread()).atHz(50);
		pv.addPVValueChangeListener(new PVValueChangeListener() {
			
			@Override
			public void pvValueChanged() {
				topLeft.setVImage(pv.getValue());
				top.setVImage(pv.getValue());
				topRight.setVImage(pv.getValue());
				centerLeft.setVImage(pv.getValue());
				center.setVImage(pv.getValue());
				centerRight.setVImage(pv.getValue());
				bottomLeft.setVImage(pv.getValue());
				bottom.setVImage(pv.getValue());
				bottomRight.setVImage(pv.getValue());
			}
		});

	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
	
	@Override
	public void dispose() {
		super.dispose();
		pv.close();
	}
}
