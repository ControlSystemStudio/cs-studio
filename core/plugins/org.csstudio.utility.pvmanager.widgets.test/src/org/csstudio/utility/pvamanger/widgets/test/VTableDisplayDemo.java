package org.csstudio.utility.pvamanger.widgets.test;

import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleArray;
import static org.epics.pvmanager.extra.ExpressionLanguage.waterfallPlotOf;
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
		table.setVTable(new VTable() {
			
			private List<Class<?>> classes = Arrays.<Class<?>>asList(String.class, Double.TYPE, Integer.TYPE);
			private List<String> names = Arrays.asList("Name", "Value", "Count");
			private List<Object> data = Arrays.<Object>asList(new String[] {"One", "Two", "Three"},
					new double[] {1.0, 2.0, 3.0}, new int[] {1, 2, 3});
			
			@Override
			public Class<?> getColumnType(int column) {
				return classes.get(column);
			}
			
			@Override
			public String getColumnName(int column) {
				return names.get(column);
			}
			
			@Override
			public int getColumnCount() {
				return classes.size();
			}
			
			@Override
			public Object getColumnArray(int column) {
				return data.get(column);
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
//	
//	@Override
//	public void dispose() {
//		super.dispose();
//		pv.close();
//	}
}
