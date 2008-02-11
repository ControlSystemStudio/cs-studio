/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.diag.IOCremoteManagement.ui;

/**
 * @author Albert Kagarmanov
 *
 */

import java.util.Observable;
import java.util.Observer;
import org.csstudio.diag.IOCremoteManagement.ui.RichTable.typeOfCell;
import org.csstudio.utility.ioc_socket_communication.IOCAnswer;
import org.csstudio.utility.ioc_socket_communication.RMTControl;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;

public class PropertyPart implements Observer  {
	private final static boolean debug=false;
	private final static String nameOfNextCommand="nextCommand";
	private Group propTable=null;
	private Composite secondTabMenu;
	private Display disp;
	private Request request=null;
	private Request parentReq;
	private IOCAnswer iocAnswer;
	private String host;
	public Node endNode;
	ListViewer listMB3=null;
	IWorkbenchPartSite site;
	Button refreshButton;
	Button callButton;
	ParametersTable parTable;
	XMLData data;
	
	PropertyPart(final Composite parent,IWorkbenchPartSite site) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL|SWT.V_SCROLL);
		this.disp=parent.getDisplay();
		this.site = site;
		this.iocAnswer=new IOCAnswer();
		this.iocAnswer.addObserver(this);
		secondTabMenu = new Composite(sc,SWT.NONE);
		secondTabMenu.setLayout(new GridLayout(1, false));
		sc.setContent(secondTabMenu);
	    sc.setMinSize(500, 500);      // Set the minimum size
	    sc.setExpandHorizontal(true); // Expand both horizontally and vertically
	    sc.setExpandVertical(true);
	}
	
	public void setActualData (String host,Request parentReq) {
	       this.parentReq=parentReq;
	       this.host=host;

	}
	private boolean prepareInfoTable(String result,String draiverName,String draiverStatus, String operationStatus) {
		int lenX=2;
		int lenY=4;
		String[][] dataArray=new String[lenX][lenY];
		dataArray[0][0]="TCP/IP status";
		dataArray[0][1]="Driver Name";
		dataArray[0][2]="Driver Status";
		dataArray[0][3]="Operation Status";
		dataArray[1][0]=result;
		dataArray[1][1]=draiverName;
		dataArray[1][2]=draiverStatus;
		dataArray[1][3]=operationStatus;
		typeOfCell[][] type = new typeOfCell[lenX][lenY];
		Object[][] extra = new Object[lenX][lenY];
		String[] columnName = {"variable","value"};
		for (int i=0;i<lenX;i++) {
			for (int j=0;j<lenY;j++) {
				type[i][j]=typeOfCell.String;
				extra[i][j]=null;
			}
		}

		RichTable st=new RichTable(propTable,"propTable",site,parentReq,host,endNode,this);
		st.setColumnName(columnName);
		st.setWarning(false);
		st.createTable(lenX,lenY,dataArray,type,extra);
		return true;
	}
	public boolean createInfoTabForVariable(String result,String draiverName,String draiverStatus, String operationStatus){
		if(propTable !=null) {
			propTable.dispose();
			if(debug) System.out.println("propTable is disposed");
		}		
		propTable = new Group(secondTabMenu,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		propTable.setLayout(new GridLayout());
		propTable.setLayoutData(new GridData(GridData.FILL_BOTH));		
		propTable.setText("infoWindow"); //$NON-NLS-1$	
		
		prepareInfoTable(result,draiverName,draiverStatus,operationStatus);
		
		secondTabMenu.layout(true);
		return true;
	}
	public void createFinalLevelScreen(XMLData _data,Node end)  { 
		this.endNode=end;
		this.data=_data;
		if(propTable !=null) {
			propTable.dispose();
			if(debug) System.out.println("propTable is disposed");
		}		 

		GridLayout layout = new GridLayout();
		layout.numColumns = 2; // TODO
		secondTabMenu.setLayout(layout);

		propTable = new Group(secondTabMenu,SWT.NULL);
		propTable.setLayout(new GridLayout());
		propTable.setLayoutData(new GridData(GridData.FILL_BOTH));		
		propTable.setText("Property"); //$NON-NLS-1$
		///////////////
		if (data != null) {
			prepareInfoTable(data.infoResult,data.infoName,data.infoStatus,data.operationStatus);
		
		//////////////	
		refreshButton = new Button(propTable, SWT.PUSH);
		refreshButton.setText("Refresh");
		refreshButton.setToolTipText("Send Request to IOC");
		GridData gd = new GridData();
        refreshButton.setLayoutData(gd);
        refreshButton.setEnabled(true);
        refreshButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	 refreshButton.setEnabled(false);
            	 endNode.askNextLevel(null);
            	 refreshButton.setEnabled(true);
            }
        });
               
        RichTable propertyRichTable=new RichTable(propTable,"propTable",site,parentReq,host,endNode,this);
		RichTablePrepare preparation= new RichTablePrepare(propTable,data);
		preparation.parser();
		if(preparation.lengthY>0) propertyRichTable.createTable(preparation);
                
		parTable=new ParametersTable(propTable,"paramTable");
		ParametersTablePrepare ParPreparation= new ParametersTablePrepare(propTable,data);
		ParPreparation.parser();
		if(ParPreparation.lengthY>0) {
			parTable.createTable(ParPreparation);
			callButton = new Button(propTable, SWT.PUSH);
			callButton.setText("Call It");
			callButton.setToolTipText("Call function with this param");
			GridData gdata = new GridData();
	        callButton.setLayoutData(gdata);
	        callButton.setEnabled(true);
	        callButton.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	 String replace=null;
	            	 callButton.setEnabled(false);
	            	 Text[] text = parTable.txt; 
	            	 String parAsArray[][] = new String [2][text.length]; 
	            	 for (int i=0;i<text.length;i++) {
	            		 parAsArray[0][i]=parTable.paramNameArray[i];
	            		 parAsArray[1][i]=text[i].getText();
	            	 }
	            	 boolean find=false;
	            	 for (int i=0;i<data.data.length;i++) {
	            		 if (data.searchAtr(nameOfNextCommand, i) != null) {
	            			 find=true;
	            			 replace=data.data[i].searchAtr(nameOfNextCommand);
	            			 if (replace==null) {
	            				 System.out.println("IOCremoteManagement:PropertyPart: Bad Atr "+ nameOfNextCommand);  // TODO
	            				 find=false;
	            			 }
	            			 break;
	            		 }
	            	 }
	            	 if (!find) { 
	            		 System.out.println("IOCremoteManagement:PropertyPart: Wrong Atr "+ nameOfNextCommand);  // TODO
	            		 return;
	            	 }
	         		 Request parRequest = new Request(parentReq,parAsArray,text.length,replace);
	         		if(debug) System.out.println("parRequest.getDocument="+parRequest.getDocument() );
	        		 final RMTControl iocContr = RMTControl.getInstance();
	        		 iocContr.send(host,parRequest.getDocument(), iocAnswer);	
	            	 callButton.setEnabled(true);
	            }
	        });
		}
		
		} // data != null
		secondTabMenu.layout(true);		
	}
	public void update(final Observable arg0, final Object arg1) {
		disp.syncExec(new Runnable() {
			public void run() {
				if(debug) System.out.println("update Run:\n");
				final String text = iocAnswer.getAnswer();
				if(debug) System.out.println("RMT ans="+text);
				analyzeAnswer(text);
				disp.update();
			}
		});
	}
	public int analyzeAnswer(String text) {
		Parsing parser = new Parsing(text);
		XMLData data=parser.Parse();	
		if (endNode != null) endNode.checkRootStatus(data.isMaster);
		setActualData (host,request);
		createFinalLevelScreen(data, endNode);
		return 0;
	}	
}
