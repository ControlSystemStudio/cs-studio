package org.csstudio.utility.pvamanger.widgets.test;

import static org.epics.pvmanager.data.ExpressionLanguage.*;
import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.util.TimeDuration.*;

import java.util.Arrays;
import java.util.List;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.csstudio.utility.pvmanager.widgets.VTableDisplay;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.data.VTable;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;

public class VTableDisplayDemo extends ViewPart {

	public static final String ID = "org.csstudio.utility.pvamanger.widgets.test.VImageDisplayDemo"; //$NON-NLS-1$

	
	private VTableDisplay table;
	private PVReader<VTable> pv;

	
	public VTableDisplayDemo() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		table = new VTableDisplay(parent);

		List<String> names = Arrays.asList("One", "Ten", "Hundred");
		List<String> ramps = Arrays.asList("sim://ramp(0,1,0.1,1)", "sim://ramp(0,10,0.1,0.75)", "sim://ramp(0,100,0.1,0.5)");
		List<String> sines = Arrays.asList("sim://sine(0,1,1)", "sim://sine(0,10,0.75)", "sim://sine(0,100,0.5)");
		pv = PVManager.read(vTable(column("Names", vStringConstants(names)),
				column("Ramps", latestValueOf(vDoubles(ramps))),
				column("Sines", latestValueOf(vDoubles(sines))))).notifyOn(SWTUtil.swtThread()).every(ms(250));
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				table.setVTable(pv.getValue());
			}
		});

		//createActions();
		//initializeToolBar();
		//initializeMenu();
		
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
		if (pv != null) {
			pv.close();
		}
	}
}
