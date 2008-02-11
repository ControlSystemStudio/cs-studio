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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;

public class ParametersTable   {
	private final static boolean debug=false;
	private final static int normalWidth=150;
	private final static int Width0=100;
	private Composite _parent;
	private String _label;
	private String[] _columnName;
	private Display _display;
	public Text[] txt;
	public String[] paramNameArray;
	
	ParametersTable(final Composite parent,String label) {
		_parent=parent;
		_label=label;
		_display=parent.getDisplay();
	}
	public void setColumnName(String[] column){_columnName=column;}
	
	public boolean createTable( ParametersTablePrepare parser) {
		setColumnName(parser.columnNameArr);
		createTable(parser.lengthX,parser.lengthY,parser.dataArr);
		return true; 
	}
	
	public boolean createTable(int lenX,int lenY, String[][] dataArray){
		int[] widthArr = new int[lenX];
		for (int i=0;i<lenX;i++) widthArr[i]=normalWidth;
		widthArr[0]=Width0;
		Table varTable = new Table(_parent, SWT.BORDER);
		varTable.setHeaderVisible(true);
		TableColumn tableColumn[] = new TableColumn[lenX];
		for (int i=0;i<lenX;i++) {
			tableColumn[i] = new TableColumn(varTable, SWT.LEFT);
			tableColumn[i].setText(_columnName[i]);
			tableColumn[i].setWidth(widthArr[i]);
		}
		TableItem Sp[] =  new TableItem[lenY];
		String[] value= new String[lenX];
		txt = new Text[lenY];
		paramNameArray=new String[lenY];
		for (int j=0;j<lenY;j++) paramNameArray[j]=dataArray[0][j];
		
		for (int j=0;j<lenY;j++) {
			for (int i=0;i<lenX;i++) value[i]=dataArray[i][j];
			Sp[j]=new TableItem(varTable, SWT.NONE);
			Sp[j].setText(value);			
				txt[j]= new Text(varTable, SWT.SINGLE | SWT.BORDER);
				txt[j].setText(dataArray[1][j]);
				/* txt[j].addSelectionListener(new SelectionAdapter() {
				public void widgetDefaultSelected(SelectionEvent e) {
				  Text t = (Text) e.widget;
				  //valueChanged(t.getText());
				  System.out.println("DummyListenerPar");
				}
				});*/   
				TableEditor editor = new TableEditor(varTable);
				editor.grabHorizontal = editor.grabVertical = true;
				editor.setEditor(txt[j], Sp[j], 1);
			}
				
		if(debug) System.out.println("result is OK");
		//org.csstudio.diag.IOCremoteManagement.Activator.errorPrint ("result is OK");
		return true;
	}
	
	public static void main(String[] args) {
		int lenX=2;
		int lenY=15;
		int comboLen=7;
		String[][] dataArray = new String[lenX][lenY];
		String[] columnName = new String[lenX];
		for (int i=0;i<lenX;i++) {
			for (int j=0;j<lenY;j++) {
				dataArray[i][j]="elem"+i+"_"+j;
			}
			columnName[i]="col_"+i;
		}
		Display display = new Display ();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		IWorkbenchPartSite site=null;
		
		ParametersTable st=new ParametersTable(shell,"test");
		st.setColumnName(columnName);
		st.createTable(lenX,lenY,dataArray);
		
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}	
		display.dispose ();
	}
}
