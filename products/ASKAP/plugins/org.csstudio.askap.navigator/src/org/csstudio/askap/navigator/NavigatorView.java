package org.csstudio.askap.navigator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.navigator.model.ASKAP;
import org.csstudio.askap.utility.Preferences;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.widgetActions.AbstractOpenOPIAction;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgetActions.OpenOPIInViewAction;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.gson.Gson;

public class NavigatorView extends ViewPart {
	
	private static final String ID = "org.csstudio.askap.navigator.NavigatorView";

	private static final Logger logger = Logger.getLogger(NavigatorView.class.getName());

	ASKAPContentProvider provider;

	public NavigatorView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 20;
		parent.setLayout(gridLayout);

		createTreeView(parent);		
	}
	
	
	public void createTreeView(Composite parent) {
		ASKAP askap = null;
//		String fileName = "/Users/wu049/ASKAPsoft/Code/Components/CSS/current/files/css-config/navigator.json";
		String fileName = Preferences.getNavigatorConfigFile();
		try {
			FileReader fileReader = new FileReader(fileName);
			Gson gson = new Gson();
			askap = gson.fromJson(fileReader, ASKAP.class);
			askap.setupMacros();
			
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING, "Could not load ASKAP treeview file - " + fileName, e);
            ExceptionDetailsErrorDialog.openError(parent.getShell(),
                    "ERROR",
                    "Could not load ASKAP treeview file " + fileName,
                    e);
			return;
		}
		

		TreeViewer treeViewer = new TreeViewer(parent);		
	    GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;	
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;	
		gridData.grabExcessVerticalSpace = true;
		treeViewer.getTree().setLayoutData(gridData);
		
		provider = new ASKAPContentProvider(askap);
		
		treeViewer.setContentProvider(provider);
		treeViewer.setLabelProvider(provider);
		treeViewer.setInput("Antenna View");
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
			       if(event.getSelection() instanceof IStructuredSelection) {
			           IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			           if (!selection.isEmpty()) {
			        	   // only display details of the first selected template or the SB 
			        	   Object item = selection.getFirstElement();
			        	   openOpi(item);
			           }
			       }
			}
		});

	}

	protected void openOpi(Object item) {
		String opiName = provider.getOpi(item);
		if (opiName!=null && opiName.length()>0) {
			String macros[][] = provider.getMacros(item);
			MacrosInput macroInput = null;
			if (macros!=null && macros.length>0) {
				LinkedHashMap<String, String> macroMap = new LinkedHashMap<String, String>();
				for (String macro[] : macros) {
					macroMap.put(macro[0], macro[1]);
				}
				macroInput = new MacrosInput(macroMap, true);
			}
			openOPI(opiName, 0, macroInput);	
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	
	/**
	 * Open an OPI.
	 * 
	 * @param widget
	 *            the widget to which the script is attached.
	 * @param opiPath
	 *            the path of the OPI. It can be either an absolute path or a
	 *            relative path to the Display file of the widget.
	 * @param target
	 *            target place of the new OPI. 0: new tab; 1: replace current
	 *            one; 2: new window; 3: view on left; 4: view on right; 5: view
	 *            on top; 6: view on bottom; 7: detached view
	 * @param macrosInput
	 *            the macrosInput. null if no macros needed.
	 */	
	private final static void openOPI(String opiPath, int target, MacrosInput macrosInput) {
		AbstractOpenOPIAction action;
		if (target < 3) {
			action = new OpenDisplayAction();
			action.setPropertyValue(OpenDisplayAction.PROP_REPLACE, target);
		} else {
			action = new OpenOPIInViewAction();
			action.setPropertyValue(OpenOPIInViewAction.PROP_POSITION,
					target - 3);
		}
		action.setWidgetModel(new DisplayModel());
		action.setPropertyValue(OpenDisplayAction.PROP_PATH, opiPath);
		action.setPropertyValue(OpenDisplayAction.PROP_MACROS, macrosInput);
		action.run();
	}

	public static Object openNavigationView() {
        try {
	    	final IWorkbench workbench = PlatformUI.getWorkbench();
	    	final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	    	final IWorkbenchPage page = window.getActivePage();    	
			return page.showView(ID);
		} catch (PartInitException e) {
			logger.log(Level.WARNING, "Could not open view" + ID, e);
		}
        
        return null;
	}

}
