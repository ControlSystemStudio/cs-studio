package org.csstudio.utility.pvamanger.widgets.test;

import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleArray;
import static org.epics.pvmanager.extra.ExpressionLanguage.waterfallPlotOf;
import static org.epics.pvmanager.util.TimeDuration.hz;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VImage;

public class VImageDisplayDemo extends ViewPart {

	public static final String ID = "org.csstudio.utility.pvamanger.widgets.test.VImageDisplayDemo"; //$NON-NLS-1$

	private PVReader<VImage> pv;
	private Action horizontalStretch;
	
	VImageDisplay topLeft;
	VImageDisplay top;
	VImageDisplay topRight;
	VImageDisplay centerLeft;
	VImageDisplay center;
	VImageDisplay centerRight;
	VImageDisplay bottomLeft;
	VImageDisplay bottom;
	VImageDisplay bottomRight;
	private Action verticalStretch;

	
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
		
		topLeft = new VImageDisplay(composite);
		topLeft.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		topLeft.setAlignment(SWT.TOP | SWT.LEFT);
		
		top = new VImageDisplay(composite);
		top.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		top.setAlignment(SWT.TOP);
		
		topRight = new VImageDisplay(composite);
		topRight.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		topRight.setAlignment(SWT.TOP | SWT.RIGHT);
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_1.spacing = 10;
		composite_1.setLayout(fl_composite_1);
		
		centerLeft = new VImageDisplay(composite_1);
		centerLeft.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		centerLeft.setAlignment(SWT.CENTER | SWT.LEFT);
		
		center = new VImageDisplay(composite_1);
		center.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		center.setAlignment(SWT.CENTER);
		
		centerRight = new VImageDisplay(composite_1);
		centerRight.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		centerRight.setAlignment(SWT.CENTER | SWT.RIGHT);
		
		Composite composite_2 = new Composite(container, SWT.NONE);
		FillLayout fl_composite_2 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_2.spacing = 10;
		composite_2.setLayout(fl_composite_2);
		
		bottomLeft = new VImageDisplay(composite_2);
		bottomLeft.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bottomLeft.setAlignment(SWT.BOTTOM | SWT.LEFT);
		
		bottom = new VImageDisplay(composite_2);
		bottom.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bottom.setAlignment(SWT.BOTTOM);
		
		bottomRight = new VImageDisplay(composite_2);
		bottomRight.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bottomRight.setAlignment(SWT.BOTTOM | SWT.RIGHT);

		createActions();
		initializeToolBar();
		initializeMenu();
		
		if (pv != null) {
			pv.close();
		}
		pv = PVManager.read(waterfallPlotOf(vDoubleArray("sim://gaussianWaveform(1, 50, 0.1)")))
		.notifyOn(SWTUtil.swtThread()).every(hz(50));
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
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
		updateStretch();
	}
	
	private void updateStretch() {
		int stretched = 0;
		if (horizontalStretch.isChecked()) {
			stretched |= SWT.HORIZONTAL;
		}
		if (verticalStretch.isChecked()) {
			stretched |= SWT.VERTICAL;
		}
		topLeft.setStretched(stretched);
		top.setStretched(stretched);
		topRight.setStretched(stretched);
		centerLeft.setStretched(stretched);
		center.setStretched(stretched);
		centerRight.setStretched(stretched);
		bottomLeft.setStretched(stretched);
		bottom.setStretched(stretched);
		bottomRight.setStretched(stretched);
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			horizontalStretch = new Action("Horizontal Stretch") {
			};
			horizontalStretch.setChecked(true);
			horizontalStretch.setImageDescriptor(ResourceManager.getImageDescriptor(VImageDisplayDemo.class, "/org/csstudio/utility/pvamanger/widgets/test/stretchHorizontal.png"));
			horizontalStretch.addPropertyChangeListener(new IPropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if ("checked".equals(event.getProperty())) {
						updateStretch();
					}
				}
			});
		}
		{
			verticalStretch = new Action("Vertical Stretch") {
			};
			verticalStretch.setChecked(true);
			verticalStretch.setImageDescriptor(ResourceManager.getImageDescriptor(VImageDisplayDemo.class, "/org/csstudio/utility/pvamanger/widgets/test/stretchVertical.png"));
			verticalStretch.addPropertyChangeListener(new IPropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if ("checked".equals(event.getProperty())) {
						updateStretch();
					}
				}
			});
		}
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(horizontalStretch);
		toolbarManager.add(verticalStretch);
	}

	/**
	 * Initialize the menu.
	 */
	@SuppressWarnings("unused")
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
