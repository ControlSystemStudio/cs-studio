package org.csstudio.askap.sb;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.ui.SBTemplateContentProvider;
import org.csstudio.askap.sb.ui.SBTemplateContentProvider.MajorVersion;
import org.csstudio.askap.sb.ui.VersionDialog;
import org.csstudio.askap.sb.util.SBTemplate;
import org.csstudio.askap.sb.util.SBTemplateDataModel;
import org.csstudio.askap.sb.util.ScheduleFileUtil;
import org.csstudio.askap.sb.util.SchedulingBlock;
import org.csstudio.askap.utility.AskapHelper;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.part.ViewPart;

import askap.util.ParameterSet;

public class SBTemplateView extends ViewPart {
	private static final Logger logger = Logger.getLogger(SBTemplateContentProvider.class.getName());
	
	public static final String ID = "org.csstudio.askap.sb.SBTemplateView";

	private TreeViewer treeViewer;
	private SBTemplateContentProvider contentProvider;

	private SBTemplateDataModel sbDataModel;

	public SBTemplateView() {
	}

	@Override
	public void createPartControl(final Composite parent) {
		contentProvider = new SBTemplateContentProvider();
		
	    Composite treeView = new Composite(parent, SWT.BORDER);
		treeView.setLayout(new GridLayout(1, false));
		treeViewer = new TreeViewer(treeView, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(contentProvider);
		// Expand the tree 
		treeViewer.setAutoExpandLevel(2);
		// Provide the input to the ContentProvider
		try {
			sbDataModel = new SBTemplateDataModel();
			treeViewer.setInput(sbDataModel);
		} catch (Exception e) {
            ExceptionDetailsErrorDialog.openError(parent.getShell(),
                    "ERROR",
                    "Could retrieve templates",
                    e);
			logger.log(Level.WARNING, "Could retrieve templates", e);
		}
		
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		treeViewer.getTree().setLayoutData(gd);
		
		Button newTemplate = new Button(treeView, SWT.PUSH);
		newTemplate.setText("New Template");
		
		newTemplate.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					newTemplate();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Could not create new template", e);
					
		            ExceptionDetailsErrorDialog.openError(parent.getShell(),
		                    "ERROR",
		                    "Error occured while trying to create new template",
		                    e);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
			       if(event.getSelection() instanceof IStructuredSelection) {
			           IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			           if (!selection.isEmpty()) {
			        	   // only display details of the first selected template or the SB 
			        	   Object item = selection.getFirstElement();
			        	   logger.log(Level.INFO, "selected " + item.toString());
			        	   try {
			        		   displayDetails(item);
			        	   } catch (Exception e) {
			        		   logger.log(Level.WARNING, "Could not display " + item.toString(), e);
					           ExceptionDetailsErrorDialog.openError(parent.getShell(),
					                    "ERROR",
					                    "Could not display " + item.toString(),
					                    e);
			        	   }
			           }
			       }
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
		
	protected void displayDetails(Object item) throws Exception {
		if (item instanceof String) // if selected template name
			return;
		
		if (item instanceof MajorVersion) {
			// selected a major version
			MajorVersion version = (MajorVersion) item;
			SBTemplate template = sbDataModel.getLatestVersion(version.templateName, version.majorVersion);					
			SBMaintenanceView.openSBMaintenanceView().display(template, sbDataModel);			
		}
		
		if (item instanceof SBTemplate) {	
			SBTemplate template = (SBTemplate) item;
			SBMaintenanceView.openSBMaintenanceView().display(template, sbDataModel);			
		}
		
		if (item instanceof SchedulingBlock) {
			SchedulingBlock sb = (SchedulingBlock) item;
			
			SBMaintenanceView.openSBMaintenanceView().display(sb, sbDataModel);
		}
	}

	private void newTemplate() throws Exception {
		FileDialog dialog = new FileDialog(treeViewer.getControl().getShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[]{"*.sch"});
        String fileName = dialog.open();
        
        if (fileName==null || fileName.length()==0)
        	return;
        
		String schemaFileName = fileName;
		String pythonScript = null;
		ParameterSet paramSet = null;
		
		// load the python script and the paramSet
		String pythonScriptName = ScheduleFileUtil.getPythonFileName(schemaFileName);
		pythonScript = ScheduleFileUtil.loadPythonScript(pythonScriptName);
		paramSet = new ParameterSet(fileName);
		
		
		// retrieve all templates for the schema name
		String str[] = schemaFileName.split(System.getProperty("file.separator"));
		String schemaName = str[str.length-1];
		schemaName = schemaName.substring(0, schemaName.indexOf(".sch"));
		
		if (sbDataModel.containsTemplate(schemaName)) {
			// if template of the same name already exists popup to get version
			SBTemplate oldTemplate = sbDataModel.getLatestVersion(schemaName);
			SBTemplate newTemplate = new SBTemplate();
			newTemplate.setPythonScript(pythonScript);
			newTemplate.setParameterMap(AskapHelper.propertiesToParameterMap(paramSet));
			
			VersionDialog versionDialog = new VersionDialog(this.getSite().getShell(), newTemplate, oldTemplate);
			Boolean isMajor = versionDialog.open();
			if (isMajor!=null)
				sbDataModel.updateTemplate(schemaName, pythonScript, paramSet, isMajor);
			else
				return;
		} else {	
			// otherwise go ahead and create the template
			sbDataModel.createNewTemplate(schemaName, pythonScript,
					paramSet);			
		}
		
		treeViewer.refresh();			
		// select the latest major version for the template
		MajorVersion version = contentProvider.getLatestVersion(schemaName);
//		TreePath treePath = new TreePath(new Object[]{schemaName, version});
		
		treeViewer.setExpandedState(version, true);
		treeViewer.setSelection(new StructuredSelection(version), true);
	}

	public void refreshAndSelect(String templateName, long sbId) throws Exception {
		SchedulingBlock sb = sbDataModel.getSB(sbId);
		refreshAndSelect(sb);
	}
	
	public void refreshAndSelect(SchedulingBlock sb) {
		treeViewer.refresh();
		treeViewer.setSelection(new StructuredSelection(sb), true);
	}


}
