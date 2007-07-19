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
package org.csstudio.diag.snlDebugger.ui;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.diag.snlDebugger.Activator;
import org.csstudio.diag.snlDebugger.Messages;
import org.csstudio.diag.snlDebugger.Preference.SampleService;
import org.csstudio.utility.ioc_socket_communication.IOCAnswer;
import org.csstudio.utility.ioc_socket_communication.RMTControl;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class View extends ViewPart implements Observer {
	IOCAnswer iocAnswer;
	Display disp;
	final static boolean debug=false;
	final static boolean debugStrong=false;
	final static boolean debugData=false;
	ListCompositeUI iocList;
	ListCompositeUI stateProgramList;
	ListCompositeUI stateSetList;
	ListCompositeUI variableList;
	StyledText messageList;
	Composite secondTabMenu;
	TabItem firstTab;
	TabItem secondTab;
	TabFolder tabFolder;
	Group propTable;
	final static int ASK_STATE_PROGRAM=0;
	final static int ASK_STATE_and_VAR=1;
	final static int ASK_STATE_PROPERTY=2;
	final static int ASK_VARIABLE_PROPERTY=3;
	
	int waitingForAnswerType=0;
	
	String AskProgList = 
		"<Root version=\"1.0.0\" invokeid=\"4\"><Command destination=\"SNLEXEC\"></Command></Root>"; 
	String AskStateListTemplate = 
		"<Root version=\"1.0.0\" invokeid=\"4\"><Command program=\"sncDemo\" destination=\"SNLEXEC\"></Command></Root>"; 
	String AskStatePropertyTemplate =
		"<Root version=\"1.0.0\" invokeid=\"4\"><Command program=\"sncDemo\" stateset=\"ramp\" destination=\"SNLEXEC\"></Command></Root>";
	String AskVariablePropertyTemplate =
		"<Root version=\"1.0.0\" invokeid=\"4\"><Command program=\"sncDemo\" variable=\"voltage\" destination=\"SNLEXEC\"></Command></Root>";

	String FirstTemplate="<ResultRoot version=\"1.0.0\" invokeid=\"4\"><NAME>SNLEXEC</NAME><STATUS>inactive</STATUS><SNLDebugger><spNumber>3</spNumber><spName>sncExample</spName><spName>sncGliu</spName><spName>sncDemo</spName></SNLDebugger></ResultRoot>";
	String SecondTemplate="<ResultRoot version=\"1.0.0\" invokeid=\"4\"><NAME>SNLEXEC</NAME><STATUS>inactive</STATUS><SNLDebugger><spName>sncDemo</spName><ssNumber>3</ssNumber><chanNumber>7</chanNumber><ssName>light</ssName><ssName>ramp</ssName><ssName>limit</ssName><pVarName>light</pVarName><pVarName>upDown</pVarName><pVarName>lightOn</pVarName><pVarName>lightOff</pVarName><pVarName>voltage</pVarName><pVarName>loLimit</pVarName><pVarName>hiLimit</pVarName></SNLDebugger></ResultRoot>";
	String ThirdTemplate="<ResultRoot version=\"1.0.0\" invokeid=\"4\"><NAME>SNLEXEC</NAME><STATUS>inactive</STATUS><SNLDebugger><spName>sncDemo</spName><ssName>ramp</ssName><threadName>sncDemo_1</threadName><dbgRunState>RUNNING</dbgRunState><stateList>START</stateList><stateList>RAMP_UP</stateList><stateList>RAMP_DOWN</stateList><firstState>START</firstState><currentState>RAMP_DOWN</currentState><previousState>RAMP_DOWN</previousState><nextState>RAMP_DOWN</nextState><Msg>Elapsed time since state was entered = 1.6 seconds Queued time delays:	delay[ 0]=5.000000</Msg></SNLDebugger></ResultRoot>";	  
	String ForthTemplate="<ResultRoot version=\"1.0.0\" invokeid=\"4\"><NAME>SNLEXEC</NAME><STATUS>inactive</STATUS><SNLDebugger><spName>sncDemo</spName><dbName>demo:voltage</dbName><pVarName>voltage</pVarName><pVarType>double</pVarType><pVar>0.500000</pVar></SNLDebugger></ResultRoot>";

	 
	public View() {
		iocAnswer = new IOCAnswer();
		iocAnswer.addObserver(this);
		propTable=null;
	}
	private Control getTabOneControl(TabFolder tabFolder) {
			final Composite menu = new Composite(tabFolder,SWT.NONE);
			menu.setLayout(new GridLayout(2, false));
			// - IOClist	
			Group IOCnames = new Group(menu,SWT.NULL);
			IOCnames.setLayout(new GridLayout());
			IOCnames.setLayoutData(new GridData(GridData.FILL_BOTH));
			//IOCnames.setBackground(disp.getSystemColor(SWT.COLOR_GREEN));		
			IOCnames.setText(Messages.getString("View.0")); //$NON-NLS-1$
				iocList = new ListCompositeUI(IOCnames);
				List IPinput = new ArrayList();
				int i=0;			
				IPinput.add(new ListItem( Activator.getDefault().getPluginPreferences().getString(SampleService.IOC_ADDRESS1) , i++));
				IPinput.add(new ListItem( Activator.getDefault().getPluginPreferences().getString(SampleService.IOC_ADDRESS2) , i++));
				IPinput.add(new ListItem( Activator.getDefault().getPluginPreferences().getString(SampleService.IOC_ADDRESS3) , i++));
				iocList.getListViewer().setInput(IPinput);	    
				GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
				gridData.minimumHeight = 30;
				gridData.heightHint = 60;
				gridData.minimumWidth = 25;
				iocList.setLayoutData(gridData);
				iocList.getListViewer().addDoubleClickListener(new IDoubleClickListener() {
			        public void doubleClick(DoubleClickEvent event) {
			          IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			          ListItem item= (ListItem) selection.getFirstElement();
			          if (debug) System.out.println("doubleClick!!!!!!!! "+ item.name );
			          final RMTControl iocContr = RMTControl.getInstance();
			          String text = prepareXMLfromCommand(ASK_STATE_PROGRAM,item.name,null);
			          iocContr.send(item.name,text, iocAnswer);
			          if (waitingForAnswerType >= ASK_STATE_PROGRAM) {
			        	  stateProgramList.getListViewer().setInput(null);
			        	  stateSetList.getListViewer().setInput(null);
			        	  variableList.getListViewer().setInput(null);
			        	  messageList.setText(" ");
			          }
			          waitingForAnswerType = ASK_STATE_PROGRAM;	          
			        }
			      });   
			// - StateProgramList
			Group stateProgram = new Group(menu,SWT.NULL);
			stateProgram.setLayout(new GridLayout());
			stateProgram.setLayoutData(new GridData(GridData.FILL_BOTH));
			stateProgram.setText(Messages.getString("View.1")); //$NON-NLS-1$
				stateProgramList = new ListCompositeUI(stateProgram);
				List stateProgramInput = new ArrayList();
				stateProgramList.getListViewer().setInput(stateProgramInput);			
				gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
				gridData.minimumHeight = 30;
				gridData.heightHint = 60;
				gridData.minimumWidth = 25;
				stateProgramList.setLayoutData(gridData);
				stateProgramList.getListViewer().addDoubleClickListener(new IDoubleClickListener() {
			        public void doubleClick(DoubleClickEvent event) {
				      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				      ListItem item= (ListItem) selection.getFirstElement();
				      final RMTControl iocContr = RMTControl.getInstance();
				      String text = prepareXMLfromCommand(ASK_STATE_and_VAR,item.name,null);
				      if (debug)  System.out.println("stateProgramList "+item.name+" text="+text );

				       IStructuredSelection selectionIP = (IStructuredSelection) iocList.getListViewer().getSelection();
				       ListItem itemIP= (ListItem) selectionIP.getFirstElement();
				       if (debug) System.out.println("IP= "+ itemIP.name );			          
				
				       iocContr.send(itemIP.name,text, iocAnswer);
				       if (waitingForAnswerType >= ASK_STATE_and_VAR) {
				        	  stateSetList.getListViewer().setInput(null);
				        	  variableList.getListViewer().setInput(null);
				        	  messageList.setText(" ");
				          }
				       waitingForAnswerType = ASK_STATE_and_VAR;	
				       }
				      });   
				
	   
			// - StateSetList
			Group stateSet = new Group(menu,SWT.NULL);
			stateSet.setLayout(new GridLayout());
			stateSet.setLayoutData(new GridData(GridData.FILL_BOTH));
			stateSet.setText(Messages.getString("View.2")); //$NON-NLS-1$
				stateSetList = new ListCompositeUI(stateSet);
				List stateSetInput = new ArrayList();
				stateSetList.getListViewer().setInput(stateSetInput);
				gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
				gridData.minimumHeight = 60;
				gridData.minimumWidth = 25;
				stateSetList.setLayoutData(gridData);	
				stateSetList.getListViewer().addDoubleClickListener(new IDoubleClickListener() {
			        public void doubleClick(DoubleClickEvent event) {
				      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				      ListItem item= (ListItem) selection.getFirstElement();
				      final RMTControl iocContr = RMTControl.getInstance();
				          
				      IStructuredSelection selectionProccess = (IStructuredSelection) stateProgramList.getListViewer().getSelection();
				      ListItem itemProcsess= (ListItem) selectionProccess.getFirstElement();
				      if (debug) System.out.println("itemProcsess "+ itemProcsess.name );	
				          
				      String text = prepareXMLfromCommand(ASK_STATE_PROPERTY,itemProcsess.name,item.name);
				      if (debug)  System.out.println("****stateSetList "+item.name+" text="+text );
				      IStructuredSelection selectionIP = (IStructuredSelection) iocList.getListViewer().getSelection();
				      ListItem itemIP= (ListItem) selectionIP.getFirstElement();
				      if (debug) System.out.println("IP= "+ itemIP.name );	
				      iocContr.send(itemIP.name,text, iocAnswer);
				      waitingForAnswerType = ASK_STATE_PROPERTY;
				        }
				      });   
				
	     
			// - VariablesList
			Group variables = new Group(menu,SWT.NULL);
			variables.setLayout(new GridLayout());
			variables.setLayoutData(new GridData(GridData.FILL_BOTH));
			variables.setText(Messages.getString("View.3")); //$NON-NLS-1$
			variableList = new ListCompositeUI(variables);
				List variablesInput = new ArrayList();
				variableList.getListViewer().setInput(variablesInput);
				gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
				gridData.minimumHeight = 60;
				gridData.minimumWidth = 25;
				variableList.setLayoutData(gridData);
				variableList.getListViewer().addDoubleClickListener(new IDoubleClickListener() {
			        public void doubleClick(DoubleClickEvent event) {
				      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				      ListItem item= (ListItem) selection.getFirstElement();
				      final RMTControl iocContr = RMTControl.getInstance();
				         
				       IStructuredSelection selectionProccess = (IStructuredSelection) stateProgramList.getListViewer().getSelection();
				       ListItem itemProcsess= (ListItem) selectionProccess.getFirstElement();
				       if (debug) System.out.println("itemState= "+ itemProcsess.name );	
				          
				       String text = prepareXMLfromCommand(ASK_VARIABLE_PROPERTY,itemProcsess.name,item.name);
				       if (debug)  System.out.println("stateProgramList "+item.name+" text="+text );
				       IStructuredSelection selectionIP = (IStructuredSelection) iocList.getListViewer().getSelection();
				       ListItem itemIP= (ListItem) selectionIP.getFirstElement();
				       if (debug) System.out.println("IP= "+ itemIP.name );	
				       iocContr.send(itemIP.name,text, iocAnswer);
					   waitingForAnswerType = ASK_VARIABLE_PROPERTY;
				        }
				      });   
				

			// - Messages
			Group message = new Group(menu,SWT.NULL);
			message.setLayout(new GridLayout());
			message.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			message.setText(Messages.getString("View.4")); //$NON-NLS-1$
			messageList = new StyledText(message,SWT.V_SCROLL);
				gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
				gridData.minimumHeight = 60;
				gridData.minimumWidth = 25;
				messageList.setLayoutData(gridData);	
				messageList.setText("                                          ");
		return menu;
		}

	private Control getTabTwoControl(TabFolder tabFolder) {
		  // Create second tab:
		secondTabMenu = new Composite(tabFolder,SWT.NONE);
		  return secondTabMenu;
		}
	private boolean createPropertyTabForState (
			String SpName, String Sname, String Thread,String dbgRunState,String firstState,String currentState,String previousState,String nextState,List stateList){

		if(propTable !=null) {
			propTable.dispose();
			System.out.println("propTable is disposed");
		}
		 
		Iterator iter ;
		TableEditor editor;
		secondTabMenu.setLayout(new GridLayout(1, false));
		propTable = new Group(secondTabMenu,SWT.NULL);
		propTable.setLayout(new GridLayout());
		propTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		//propTable.setBackground(disp.getSystemColor(SWT.COLOR_GREEN));		
		propTable.setText(Messages.getString("View.5")); //$NON-NLS-1$
		
		Table varTable = new Table(propTable, SWT.BORDER);
		TableColumn tableColumn1 = new TableColumn(varTable, SWT.CENTER);
		TableColumn tableColumn2 = new TableColumn(varTable, SWT.CENTER);
		TableColumn tableColumn3 = new TableColumn(varTable, SWT.CENTER);
		tableColumn1.setText("Var");
		tableColumn2.setText("Value");
		tableColumn3.setText("Change");
		
		tableColumn1.setWidth(125);
		tableColumn2.setWidth(100);
		tableColumn3.setWidth(100);
		
		varTable.setHeaderVisible(true);
		TableItem Sp = new TableItem(varTable, SWT.NONE);
		Sp.setText(new String[] { "State Program name:", SpName,null });
		TableItem sName = new TableItem(varTable, SWT.NONE);
		sName.setText(new String[] { "Stateset name:", Sname,null});
		TableItem thread = new TableItem(varTable, SWT.NONE);
		thread.setText(new String[] { "Thread name:", Thread,null });
		TableItem dbgState = new TableItem(varTable, SWT.NONE);
		dbgState.setText(new String[] { "dbgRunState:", dbgRunState,null});
		
		TableItem firstSt = new TableItem(varTable, SWT.NONE);
		firstSt.setText(new String[] { "firstState:", firstState,null});
		CCombo firstStCombo = new CCombo(varTable, SWT.NONE);
		iter = stateList.iterator();
		while(iter.hasNext()){ListItem list = (ListItem)iter.next(); firstStCombo.add(list.name);}
		editor = new TableEditor(varTable);
        editor.grabHorizontal = editor.grabVertical = true;
        editor.setEditor(firstStCombo, firstSt, 2);
		
		
		TableItem currentSt = new TableItem(varTable, SWT.NONE);
		currentSt.setText(new String[] { "currentState:", currentState,null});
		CCombo currentStCombo = new CCombo(varTable, SWT.NONE);
		iter = stateList.iterator();
		while(iter.hasNext()){ListItem list = (ListItem)iter.next(); currentStCombo.add(list.name);}
		editor = new TableEditor(varTable);
        editor.grabHorizontal = editor.grabVertical = true;
        editor.setEditor(currentStCombo, currentSt, 2);
		
		TableItem previousSt = new TableItem(varTable, SWT.NONE);
		previousSt.setText(new String[] { "previousState:", previousState,null});
		CCombo previousStCombo = new CCombo(varTable, SWT.NONE);
		iter = stateList.iterator();
		while(iter.hasNext()){ListItem list = (ListItem)iter.next(); previousStCombo.add(list.name);}
		editor = new TableEditor(varTable);
        editor.grabHorizontal = editor.grabVertical = true;
        editor.setEditor(previousStCombo, previousSt, 2);
		
		TableItem nextSt = new TableItem(varTable, SWT.NONE);
		nextSt.setText(new String[] { "nextState:", nextState,null});
		CCombo nextStCombo = new CCombo(varTable, SWT.NONE);
		iter = stateList.iterator();
		while(iter.hasNext()){ListItem list = (ListItem)iter.next(); nextStCombo.add(list.name);}
		editor = new TableEditor(varTable);
        editor.grabHorizontal = editor.grabVertical = true;
        editor.setEditor(nextStCombo, nextSt, 2);
		
        secondTabMenu.layout(true);
		return true;
	}
	private boolean createPropertyTabForVariable(
			String spName,String dbName,String pVarName,String pVarType,String pVar){
		if(propTable !=null) {
			propTable.dispose();
			System.out.println("propTable is disposed");
		}
	        
		secondTabMenu.setLayout(new GridLayout(1, false));
		propTable = new Group(secondTabMenu,SWT.NULL);
		propTable.setLayout(new GridLayout());
		propTable.setLayoutData(new GridData(GridData.FILL_BOTH));		
		propTable.setText(Messages.getString("View.5")); //$NON-NLS-1$	
		Table varTable = new Table(propTable, SWT.BORDER);
		TableColumn tableColumn1 = new TableColumn(varTable, SWT.CENTER);
		TableColumn tableColumn2 = new TableColumn(varTable, SWT.CENTER);
		tableColumn1.setText("Var");
		tableColumn2.setText("Value");		
		tableColumn1.setWidth(125);
		tableColumn2.setWidth(100);	
		varTable.setHeaderVisible(true);
		
		TableItem Sp = new TableItem(varTable, SWT.NONE);
		Sp.setText(new String[] { "State Program name:", spName});
		
		TableItem pvName = new TableItem(varTable, SWT.NONE);
		pvName.setText(new String[] { "pVar name:", pVarName});
		
		TableItem DbName = new TableItem(varTable, SWT.NONE);
		DbName.setText(new String[] { "db name:", dbName});
		
		TableItem vType = new TableItem(varTable, SWT.NONE);
		vType.setText(new String[] { "pVar type:", pVarType});
		
		TableItem val = new TableItem(varTable, SWT.NONE);
		val.setText(new String[] { "pVar value:", pVar});

		secondTabMenu.layout(true);
		return true;
	}
	
	
	@Override
	public void createPartControl(final Composite parent) {
		disp = parent.getDisplay();
		 tabFolder = new TabFolder(parent, SWT.NONE);
		 TabItem firstTab = new TabItem(tabFolder, SWT.NONE);
		 firstTab.setText("SnlDebugMain");
		 firstTab.setToolTipText("Main Window for SNL Debugger");
		  //one.setImage(circle);
		 firstTab.setControl(getTabOneControl(tabFolder));

		 TabItem secondTab = new TabItem(tabFolder, SWT.NONE);
		 secondTab.setText("PropertyWin");
		 secondTab.setToolTipText("SNL Debugger Property Window");
		 //two.setImage(square);
		 secondTab.setControl( getTabTwoControl(tabFolder) );
		  
		  
		// -Layout
		// -Menu
	}

	@Override
	public void setFocus() {	}
	
	public void update(final Observable arg0, final Object arg1) {
		disp.syncExec(new Runnable() {
			public void run() {
				final String text = iocAnswer.getAnswer();
				System.out.println("View tet : "+ text);
				List input ;
				String Str="";
				Iterator iter ;
				ListItem list;
				switch (waitingForAnswerType) {
				case ASK_STATE_PROGRAM :
					if(debugData)input = parserAnswer(FirstTemplate ,"spName");
					else input = parserAnswer(iocAnswer ,"spName");
			        stateProgramList.getListViewer().setInput(input); 
					break;
				case ASK_STATE_and_VAR :
					if(debugData)input = parserAnswer(SecondTemplate ,"ssName");
					else input = parserAnswer(iocAnswer,"ssName");
				    stateSetList.getListViewer().setInput(input);
				    
				    if(debugData)input = parserAnswer(SecondTemplate ,"pVarName");
				    else input = parserAnswer(iocAnswer,"pVarName");
				    variableList.getListViewer().setInput(input); 
					break;
				case ASK_STATE_PROPERTY :
					List procNameInput,stateNameInput,ThreadInput,dbgRunStateInput,stateListInput,firstStateInput,currentStateInput,previousStateInput,nextStateInput;
					String procName,ssName,thread,dbgRun,firstSt,currentSt,previousSt,nextSt ;
					if(debugData) {
					procNameInput =    parserAnswer(ThirdTemplate,"spName");
					stateNameInput =   parserAnswer(ThirdTemplate,"ssName");					
			        ThreadInput =      parserAnswer(ThirdTemplate,"threadName");			      
			        dbgRunStateInput = parserAnswer(ThirdTemplate,"dbgRunState");	
			        stateListInput =   parserAnswer(ThirdTemplate,"stateList");
			        firstStateInput =  parserAnswer(ThirdTemplate,"firstState");
			        currentStateInput =parserAnswer(ThirdTemplate,"currentState");
			        previousStateInput=parserAnswer(ThirdTemplate,"previousState");
			        nextStateInput =   parserAnswer(ThirdTemplate,"nextState");
			        input =            parserAnswer(ThirdTemplate,"Msg");
					} else {
						procNameInput =    parserAnswer(iocAnswer,"spName");
						stateNameInput =   parserAnswer(iocAnswer,"ssName");					
				        ThreadInput =      parserAnswer(iocAnswer,"threadName");			      
				        dbgRunStateInput = parserAnswer(iocAnswer,"dbgRunState");	
				        stateListInput =   parserAnswer(iocAnswer,"stateList");
				        firstStateInput =  parserAnswer(iocAnswer,"firstState");
				        currentStateInput =parserAnswer(iocAnswer,"currentState");
				        previousStateInput=parserAnswer(iocAnswer,"previousState");
				        nextStateInput =   parserAnswer(iocAnswer,"nextState");
				        input =            parserAnswer(iocAnswer,"Msg");
					}

					procName = ((ListItem) procNameInput.get(0)).name;
					ssName =   ((ListItem) stateNameInput.get(0)).name;
				    thread =   ((ListItem) ThreadInput.get(0)).name;
					dbgRun =   ((ListItem) dbgRunStateInput.get(0)).name;
					firstSt =  ((ListItem) firstStateInput.get(0)).name;
					currentSt =((ListItem) currentStateInput.get(0)).name;
					previousSt=((ListItem) previousStateInput.get(0)).name;
					nextSt =   ((ListItem) nextStateInput.get(0)).name;
					createPropertyTabForState(procName,ssName,thread,dbgRun,firstSt,currentSt,previousSt,nextSt,stateListInput);
					iter = input.iterator();
			        while(iter.hasNext()){list = (ListItem)iter.next(); Str += list.name + " .";}
			        
			        if (debug) System.out.println("++++++++++++" + Str);
					messageList.setText(Str);
					
					tabFolder.setSelection(1);
					
					break;
				case ASK_VARIABLE_PROPERTY :
					List prNameInput,dbNameInput,pVarNameInput,pVarTypeInput,pVarInput;
					if(debugData) {
						prNameInput = parserAnswer(ForthTemplate,"spName");
				        dbNameInput = parserAnswer(ForthTemplate,"dbName");
		                pVarNameInput=parserAnswer(ForthTemplate,"pVarName");;
				        pVarTypeInput=parserAnswer(ForthTemplate,"pVarType");
				        pVarInput =   parserAnswer(ForthTemplate,"pVar");
				        input =       parserAnswer(ForthTemplate,"Msg");
					} else {
						prNameInput =  parserAnswer(iocAnswer,"spName");
				        dbNameInput =  parserAnswer(iocAnswer,"dbName");
		                pVarNameInput= parserAnswer(iocAnswer,"pVarName");;
				        pVarTypeInput= parserAnswer(iocAnswer,"pVarType");
				        pVarInput =    parserAnswer(iocAnswer,"pVar");
				        input =        parserAnswer(iocAnswer,"Msg");
					}
			        
			        String prName = ((ListItem) prNameInput.get(0)).name;
			        String dbName = ((ListItem) dbNameInput.get(0)).name;
			        String pVarName = ((ListItem) pVarNameInput.get(0)).name;
			        String pVarType = ((ListItem) pVarTypeInput.get(0)).name;
			        String pVar = ((ListItem) pVarInput.get(0)).name;
			        
			        createPropertyTabForVariable(prName,dbName,pVarName,pVarType,pVar);
			        

			        iter = input.iterator();
			        while(iter.hasNext()){list = (ListItem)iter.next(); Str += list.name + " .";}
			         
			        if (debug) System.out.println("==========" + Str);
		    		messageList.setText(Str);
		    		tabFolder.setSelection(1);
					break;					
				default:
					Activator.logError(Messages.getString("SNLdebugger Error: update.disp.syncExec  wrong waitingForAnswerType"));
					System.out.println("SNLdebugger Error: update.disp.syncExec  wrong waitingForAnswerType");
					waitingForAnswerType = 0;
				}					
				//waitingForAnswerType = 0;
				disp.update();
			}
		});
	}
	
	String prepareXMLfromCommand(int modif, String name, String var) {
		String ans=null;
		switch (modif) {
		case ASK_STATE_PROGRAM :
			ans =  AskProgList;
			break;
		case ASK_STATE_and_VAR :
			ans = new String(AskStateListTemplate);
			ans=ans.replace("sncDemo", name);
			break;
		case ASK_STATE_PROPERTY :
			ans = new String(AskStatePropertyTemplate);
			ans = ans.replace("sncDemo", name);
			ans = ans.replace("ramp", var);
			break;
		case ASK_VARIABLE_PROPERTY :
			ans = new String(AskVariablePropertyTemplate);
			ans = ans.replace("sncDemo", name);
			ans = ans.replace("voltage", var);
			break;
			
		default:
			Activator.logError(Messages.getString("SNLdebugger Error:  bad prepareXMLfromCommand"));
			System.out.println("SNLdebugger Error:  bad prepareXMLfromCommand");
			return null;
		}		
		return  ans;
	}
	
	List parserAnswer(IOCAnswer iocAnswer,String pattern) {
		String wholeXML=new String (iocAnswer.getAnswer());
		return parserAnswer(wholeXML, pattern);
	}
	
	List parserAnswer(String wholeXML,String pattern) {
		if(wholeXML.length() < 5) {
			Activator.logError(Messages.getString("SNLdebugger Error:  bad parserAnswer wholeXML is small"));
			System.out.println("SNLdebugger Error:  bad parserAnswer wholeXML is small ='" + wholeXML+"'");
			return null;		
		}
		List input = new ArrayList();
		if (debug)  System.out.println(" parserAnswer  iocAnswer.getAnswer()=" + iocAnswer.getAnswer() );
		final SAXBuilder saxb = new SAXBuilder(false);
		Document xmlDoc;
		try {
			xmlDoc = saxb.build(new StringReader(wholeXML));
			Element root = xmlDoc.getRootElement();
			final List varList = root.getChildren("SNLDebugger");
			final Iterator varIterator = varList.iterator();
			int i=0;
			while(varIterator.hasNext()){
				final Element elm = (Element) varIterator.next();
				final List spName = elm.getChildren(pattern);
				final Iterator spNameIterrator = spName.iterator();
				while(spNameIterrator.hasNext()){
					final Element spNameEl = (Element) spNameIterrator.next();
					if (debug)  System.out.println("elm.getTextNormalize() =" + spNameEl.getTextNormalize() +" i=" +i);
					input.add( new ListItem(spNameEl.getTextNormalize(),i++) );
				}
			}
	 
		} catch (final JDOMException e1) {
			Activator.logException(Messages.getString("SNLdebugger: View.ExceptionJDOM"),e1);
			return null;
		} catch (final IOException e1) {
			Activator.logException(Messages.getString("View.ExceptionIO"), e1); 
			return null;
		}
		return input;
	}
}
