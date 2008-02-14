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

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.ioc_socket_communication.IOCAnswer;
import org.csstudio.utility.ioc_socket_communication.RMTControl;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control; 
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

public class RichTable  implements Observer {
	private final static boolean debug=false;
 	private final static int normalWidth=150;
	private final static int Width0=100;
    public enum typeOfCell {String,Combo,EditableText,MB3_member,Message};
	private Composite _parent;
	private String _label;
	private String[] _columnName;
	private IWorkbenchPartSite _site;
	Request _parentReq;
	Request request;
	String _host;
	private IOCAnswer iocAnswer;
	public Node endNode;

	private boolean _warning;
	private Display _display;
	CCombo combo;
	private CCombo comboSsRunCtrl=null;
	private CCombo comboCurrentState=null;
	private final static String ssRunCtrlStr="ssRunControl";
	private final static String currentStateStr="currentState";
	PropertyPart _part;
	
	RichTable(final Composite parent,String label,IWorkbenchPartSite site,Request parentReq,String host,Node endNode,PropertyPart part) {
		_parent=parent;
		_label=label;
		_display=parent.getDisplay();
		_site=site;
		_parentReq=parentReq;
		_host = host;
		this.endNode = endNode;
		this.iocAnswer=new IOCAnswer();
		this.iocAnswer.addObserver(this);
		_part=part;
	}
	public void setColumnName(String[] column){_columnName=column;}
	public void setWarning(boolean warn){_warning=warn;}
	public boolean createTable( RichTablePrepare parser) {
		setColumnName(parser.columnNameArr);
		setWarning(parser.isWarning);
		createTable(parser.lengthX,parser.lengthY,parser.stringDataArr,parser.cellsTypeArr,parser.extraDataArr);
		return true; 
	}
	public boolean createTable(int lenX,int lenY, String[][] dataArray, typeOfCell[][] type,Object[][] extra){
		int[] widthArr = new int[lenX];
		for (int i=0;i<lenX;i++) widthArr[i]=normalWidth;
		widthArr[0]=Width0;
		Table varTable = new Table(_parent, SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		if (lenY >30){
			if (debug)System.out.println("lenY="+lenY);
			varTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}
			//else varTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10));
			
		varTable.setHeaderVisible(true);
		TableColumn tableColumn[] = new TableColumn[lenX];
		for (int i=0;i<lenX;i++) {
			tableColumn[i] = new TableColumn(varTable, SWT.LEFT);
			tableColumn[i].setText(_columnName[i]);
			tableColumn[i].setWidth(widthArr[i]);
		}
		TableItem Sp[] =  new TableItem[lenY];
		String[] value= new String[lenX];
		for (int j=0;j<lenY;j++) {
			for (int i=0;i<lenX;i++) value[i]=dataArray[i][j];
			Sp[j]=new TableItem(varTable, SWT.NONE);
			Sp[j].setText(value);
			
			for (int i=0;i<lenX;i++) {
				switch (type[i][j]) {
				case Combo:
					int current=-1;
					combo = new CCombo(varTable, SWT.NONE);
					Object valueArr = extra[i][j];
					if (valueArr instanceof String[]) {
						String[] valueAsString=	(String[]) valueArr;
						for(int k=0;k<valueAsString.length;k++) {	
							combo.add(valueAsString[k]);				
							if( valueAsString[k].compareTo(dataArray[i][j]) == 0 ) current=k;
						}
						if(current==-1) {
							//org.csstudio.diag.IOCremoteManagement.Activator.errorPrint ("Wrong comboSelect");
							System.out.println("Wrong comboSelect");
						} else combo.select(current); 
						
						TableEditor editor = new TableEditor(varTable);
				        editor.grabHorizontal = editor.grabVertical = true;
				        editor.setEditor (combo, Sp[j], i);
				        if (dataArray[0][j].compareTo(ssRunCtrlStr) == 0) {
				        	comboSsRunCtrl=combo;
				    		comboSsRunCtrl.addSelectionListener(new SelectionAdapter() {
				    	    	public void widgetSelected(SelectionEvent e) {
				        			overwriteSS(); 
				        	}	    		    	
				        });
				        } else if (dataArray[0][j].compareTo(this.currentStateStr) == 0) {
				        	comboCurrentState=combo;
				        	comboCurrentState.addSelectionListener(new SelectionAdapter() {
				    	    	public void widgetSelected(SelectionEvent e) {
				        			overwriteCS(); 
				        	}	    		    	
				        });
				        }

				    	
					} else {
						//org.csstudio.diag.IOCremoteManagement.Activator.errorPrint ("Wrong instance");
						System.out.println("Wrong instance");
					}
				break;
				
				case EditableText:
					Text txt= new Text(varTable, SWT.SINGLE | SWT.BORDER);
					txt.setText(dataArray[i][j]);
					txt.addSelectionListener(new SelectionAdapter() {
					public void widgetDefaultSelected(SelectionEvent e) {
					  Text t = (Text) e.widget;
					  valueChanged(t.getText());
					  System.out.println("DummyListenerText");
					}
					});   
					TableEditor editor = new TableEditor(varTable);
					editor.grabHorizontal = editor.grabVertical = true;
					editor.setEditor(txt, Sp[j], i);
				break;
				
				case MB3_member:
					ListViewer listMB3 = new ListViewer(varTable, SWT.NONE);
					final String[] arr = new String[1];
					arr[0]=dataArray[i][j];
					listMB3.setContentProvider(new IStructuredContentProvider() {
					public void dispose() {
					}
					public Object[] getElements(Object inputElement) {
					IProcessVariable[] ipv = new IProcessVariable[1];
					ipv[0] = CentralItemFactory.createProcessVariable(arr[0]);
					return ipv;
					}
					public void inputChanged(Viewer viewer,Object oldInput, Object newInput) {							
					}
					});

					listMB3.setLabelProvider(new ILabelProvider() {
					public Image getImage(Object element) {
					return null;
					}
					public String getText(Object element) {
					IProcessVariable ipv = (IProcessVariable) element;
					return ipv.getName();
					}
					public void addListener(ILabelProviderListener listener) {							
					}
					public void dispose() {
					}
					public boolean isLabelProperty(Object element,String property) {
					return false;
					}
					public void removeListener(
						ILabelProviderListener listener) {
					}
					});
					listMB3.setInput(arr);
					editor = new TableEditor(varTable);
					editor.grabHorizontal = editor.grabVertical = true;

					List list =  listMB3.getList();
					list.setForeground(_display.getSystemColor(SWT.COLOR_BLUE));
					editor.setEditor( list, Sp[j], 1);
					new ProcessVariableDragSource (listMB3.getControl(), listMB3);
					makeContextMenu(listMB3); 	
				break;
				
				case Message:
					Group textGroup = new Group(_parent, SWT.NONE);
				    GridLayout gridLayout = new GridLayout();
				    textGroup.setLayout(gridLayout);
				    gridLayout.numColumns = 1;
				    textGroup.setLayoutData(new GridData(/*GridData.GRAB_HORIZONTAL|GridData.HORIZONTAL_ALIGN_FILL|*/GridData.VERTICAL_ALIGN_FILL));
				    textGroup.setText("Message:");
					Label labelWrap = new Label(textGroup, SWT.WRAP|SWT.BORDER);
					labelWrap.setText(dataArray[i][j]);
				    labelWrap.setSize(20, 50);
				break;
				
				case String:			
				break;
				default:
					//org.csstudio.diag.IOCremoteManagement.Activator.errorPrint ("Wrong switch");
					System.out.println("Wrong switch");
				break;
				}
			}
		}
		
		if(_warning) {
			varTable.setForeground(_display.getSystemColor(SWT.COLOR_RED));
			varTable.setBackground(_display.getSystemColor(SWT.COLOR_YELLOW));
		}
		
		
		if(debug) System.out.println("result is OK");
		//org.csstudio.diag.IOCremoteManagement.Activator.errorPrint ("result is OK");
		return true;
	}
	private void valueChanged(String txt) {		
		if (txt==null){
			System.out.println("SNLdebugger Error: valueChanged"); //TODO
		    return;
		}
       /*		try {
		double fValue = Double.parseDouble(txt);
		} catch (NumberFormatException e) {
			System.out.println("SNLdebugger Error: valueChanged bad Double fromat"); //TODO
			 return;
		}
		 Varible can be not only number but also STRING for variableAsAstring */
		String var = "newVarValue";
		overwrite(var,txt);
	}
	
	private int overwriteCS() {
		String CurrentText = comboCurrentState.getText();
		String var = "newStateName";
		if(debug) System.out.println("overwriteCS="+CurrentText);
		return overwrite(var,CurrentText);
	}
	private int overwriteSS() {
		String CurrentText = comboSsRunCtrl.getText();
		String var = "ssRunControl";
		if(debug) System.out.println("overwriteSS="+CurrentText);
		return overwrite(var,CurrentText);
	}	
	private int overwrite(String tagName,String newValue) {		
		request = new Request(_parentReq,tagName,newValue);
		final RMTControl iocContr = RMTControl.getInstance();
		if(debug) System.out.println("host="+ _host+" RMT req="+request.getDocument()+"\ndisp="+_display);
		iocContr.send(_host,request.getDocument(), iocAnswer);	
		return 0;
	}

	private void makeContextMenu(ListViewer listMB3) {
		MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		Control contr = listMB3.getControl();
		manager.addMenuListener(new IMenuListener() {
		public void menuAboutToShow(IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}
		});
		Menu menu = manager.createContextMenu(contr);
		contr.setMenu(menu);
		if(_site != null) {
			_site.registerContextMenu(manager, listMB3);
		}else {
			//org.csstudio.diag.IOCremoteManagement.Activator.errorPrint ("Wrong site");
			System.out.println("Wrong site");
		}
	}
	
	public void update(final Observable arg0, final Object arg1) {
		_display.syncExec(new Runnable() {
			public void run() {
				if(debug) System.out.println("update Run:\n");
				final String text = iocAnswer.getAnswer();
				if(debug) System.out.println("RMT ans="+text);
				analyzeAnswer(text);
				_display.update();
			}
		});
	}
	
	public int analyzeAnswer(String text) {
		Parsing parser = new Parsing(text);
		XMLData data=parser.Parse();
		if ((data.internalStatus.compareToIgnoreCase("locked") == 0)||(data.infoResult.compareToIgnoreCase("failed") == 0)||(data.internalStatus.compareToIgnoreCase("error") == 0)) {
			setActualData (_host,request);
			_part.createFinalLevelScreen(data, endNode);
		} else {
			if(debug) System.out.println("*************** ss RMT 2nd request"+text);
			endNode.askNextLevel(null);
		}
		return 0;
	}	
	private void setActualData (String host,Request parentReq) {
	       this._parentReq=parentReq;
	       this._host=host;
	}
	
	public static void main(String[] args) {
		int lenX=5;
		int lenY=15;
		int comboLen=7;
		String[][] dataArray = new String[lenX][lenY];
		typeOfCell[][] type = new typeOfCell[lenX][lenY];
		Object[][] extra = new Object[lenX][lenY];
		String[] columnName = new String[lenX];
		for (int i=0;i<lenX;i++) {
			for (int j=0;j<lenY;j++) {
				type[i][j]=typeOfCell.String;
				extra[i][j]=null;
				dataArray[i][j]="elem"+i+"_"+j;
				if ((i==1)&(j==0)) {
					type[i][j]=typeOfCell.Combo;
					String[] combo = new String[comboLen];
					for (int k=0;k<comboLen;k++) combo[k]="com"+"_"+k;
					combo[2]=dataArray[i][j];
					extra[i][j]=(Object) combo;
				}
				if ((i==1)&(j==1)) type[i][j]=typeOfCell.EditableText;
				if ((i==1)&(j==2)) type[i][j]=typeOfCell.MB3_member;
				if ((i==1)&(j==2)) type[i][j]=typeOfCell.Message;
			}
			columnName[i]="col_"+i;
		}
		Display display = new Display ();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		IWorkbenchPartSite site=null;
		
		RichTable st=new RichTable(shell,"test",site,null,null,null,null);
		st.setColumnName(columnName);
		st.setWarning(false);
		st.createTable(lenX,lenY,dataArray,type,extra);
		
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}	
		display.dispose ();
	}
}
