/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.ams.filter.ui;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.csstudio.ams.AMSException;
import org.csstudio.ams.Activator;
import org.csstudio.ams.Log;
import org.csstudio.ams.Messages;
import org.csstudio.ams.Utils;
import org.csstudio.ams.dbAccess.ItemInterface;
import org.csstudio.ams.dbAccess.configdb.AggrFilterConditionArrayStringDAO;
import org.csstudio.ams.dbAccess.configdb.AggrFilterConditionArrayStringTObject;
import org.csstudio.ams.dbAccess.configdb.FilterConditionArrayStringTObject;
import org.csstudio.ams.dbAccess.configdb.FilterConditionArrayStringValuesTObject;
import org.csstudio.ams.filter.FilterConditionArrayString;
import org.csstudio.ams.internal.SampleService;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FilterConditionArrayStringUI extends FilterConditionUI
{
	private DateFormat storeTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
	private DateFormat showTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
	private NumberFormat storeNumberFormat = NumberFormat.getInstance( Locale.US);
	private NumberFormat showNumberFormat = NumberFormat.getInstance();	
	
	private String maskTime   = ((SimpleDateFormat)showTimeFormat).toLocalizedPattern();
	
	private final static int TYPE_STRING = 0;
	private final static int TYPE_NUMBER = 1;
	private final static int TYPE_TIME = 2;

	private AggrFilterConditionArrayStringTObject condition = null;
	private AggrFilterConditionArrayStringTObject conditionOrg = null;
	
	private final ArrayList<FilterConditionArrayStringValuesTObject> arrayValues = new ArrayList<FilterConditionArrayStringValuesTObject>();
	
	public FilterConditionArrayStringUI()
	{
		super();
		arrayValues.add(new FilterConditionArrayStringValuesTObject(-1, ""));
	}
	
	public String getDisplayName()
	{
		return Messages.FilterConditionArrayStringUI_DisplayName;
	}

	private void set(AggrFilterConditionArrayStringTObject condition)
	{
		this.condition = condition;
		this.conditionOrg = (AggrFilterConditionArrayStringTObject)cloneObject(condition);
		
		setComboBoxValueUI(display, cboKeyValue, condition.getArrayString().getKeyValue());
		setComboBoxValueUI(display, cboKeyValueType, getValueTypeForOperator(condition.getArrayString().getOperator()));
		
		if(condition.getArrayStringValues().isEmpty())
		{
			condition.getArrayStringValues().add(new FilterConditionArrayStringValuesTObject(1,"1"));
			condition.getArrayStringValues().add(new FilterConditionArrayStringValuesTObject(2,"2"));
		}
		
		ArrayList<FilterConditionArrayStringValuesTObject> tmpValues = new ArrayList<FilterConditionArrayStringValuesTObject>();
		
		int type = getValueTypeForOperator(condition.getArrayString().getOperator());
		Iterator<FilterConditionArrayStringValuesTObject> iter = condition.getArrayStringValues().iterator();
		
		while(iter.hasNext())
		{
			try
			{
				FilterConditionArrayStringValuesTObject item = iter.next();
				
				if(type == TYPE_NUMBER)
					tmpValues.add(new FilterConditionArrayStringValuesTObject(item.getFilterConditionRef(),
							showNumberFormat.format(storeNumberFormat.parse(item.getCompValue()))));
				else if(type == TYPE_TIME)
					tmpValues.add(new FilterConditionArrayStringValuesTObject(item.getFilterConditionRef(),
							showTimeFormat.format(storeTimeFormat.parse(item.getCompValue()))));
				else
					tmpValues.add(new FilterConditionArrayStringValuesTObject(item.getFilterConditionRef(),item.getCompValue()));
				
			}
			catch(Exception ex)
			{
			}	
		}
		tmpValues.add(new FilterConditionArrayStringValuesTObject(-1, ""));
		arrayValues.removeAll(arrayValues);
		arrayValues.addAll(tmpValues);
		refresh(display, tblValueViewer);
	}
	
	private void get(AggrFilterConditionArrayStringTObject condition)
	{
		condition.getArrayString().setKeyValue(
				getSelectionIndex(display, cboKeyValue) < 0 ? "" : getItem(display, cboKeyValue, getSelectionIndex(display, cboKeyValue)));		
		condition.getArrayString().setOperator(getOperatorForValueType(getSelectedComboBoxIdValueUI(display, this.cboKeyValueType)));
		
		ArrayList<FilterConditionArrayStringValuesTObject> tmpValues = new ArrayList<FilterConditionArrayStringValuesTObject>();
		
		int type = getValueTypeForOperator(condition.getArrayString().getOperator());
		Iterator<FilterConditionArrayStringValuesTObject> iter = arrayValues.iterator();
			
		while(iter.hasNext())
		{
			try
			{
				FilterConditionArrayStringValuesTObject item = iter.next();
				
				if(Utils.isEmpty(item.getCompValue()))
					continue;
				
				if(type == TYPE_NUMBER)
					tmpValues.add(new FilterConditionArrayStringValuesTObject(condition.getArrayString().getFilterConditionRef(),
							storeNumberFormat.format(showNumberFormat.parse(item.getCompValue()))));
				else if(type == TYPE_TIME)
					tmpValues.add(new FilterConditionArrayStringValuesTObject(condition.getArrayString().getFilterConditionRef(),
							storeTimeFormat.format(showTimeFormat.parse(item.getCompValue()))));
				else
					tmpValues.add(new FilterConditionArrayStringValuesTObject(condition.getArrayString().getFilterConditionRef(),item.getCompValue()));
				
			}
			catch(Exception ex)
			{
			}	
		}
		condition.getArrayStringValues().removeAll(condition.getArrayStringValues());
		condition.getArrayStringValues().addAll(tmpValues);
	}
	
	public void reset()
	{
		cboKeyValue.deselectAll();
		cboKeyValueType.deselectAll();
		arrayValues.removeAll(arrayValues);
		arrayValues.add(new FilterConditionArrayStringValuesTObject(-1, ""));
	}
	
	private void createControls()
	{
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginLeft = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		
		composite.setLayout(gridLayout);
		
		lblKeyValue = new Label(composite, SWT.None);
		lblKeyValue.setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));		
		cboKeyValue = new Combo(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		cboKeyValue.setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, true, false));
		
		lblKeyValueType = new Label(composite, SWT.None);
		lblKeyValueType.setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));	
		cboKeyValueType = new Combo(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		cboKeyValueType.setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));
		
		lblValue = new Label(composite, SWT.None);
		lblValue.setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, true));	
		Composite compValueList = new Composite(composite, SWT.NONE | SWT.BORDER);
		compValueList.setLayoutData(getGridData(220,200, 1,1, SWT.BEGINNING, SWT.CENTER, false, true));
		
		createValueList(compValueList);
		initText();
	}
	
	// Set the table column property names
	private final String COLUMN_VALUE = "$Value";

	// Set column names
	private String[] columnNames = new String[] {COLUMN_VALUE};
	
	/**
	 * Create a new shell, add the widgets, open the shell
	 * @return the shell that was created	 
	 */
	private void createValueList(Composite composite)
	{
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout (layout);

		// Create the table 
		int style = SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | 
		SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		tblValue = new Table(composite, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 0;
		gridData.verticalSpan = 0;
		tblValue.setLayoutData(gridData);		
		
		tblValue.setLinesVisible(false);
		tblValue.setHeaderVisible(false);

		TableColumn column = new TableColumn(tblValue, SWT.LEFT, 0);
		//column.setText(Messages.FilterConditionArrayStringUI_tbl_Column1);
		column.setWidth(400);

			// Create and setup the TableViewer
		tblValueViewer = new TableViewer(tblValue);
		tblValueViewer.setUseHashlookup(true);
		
		tblValueViewer.setColumnProperties(columnNames);		

		// Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tblValue);

		// Assign the cell editors to the viewer 
		tblValueViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tblValueViewer.setCellModifier(new TableCellModifier());
		
		tblValueViewer.getTable().addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if(e.keyCode == SWT.DEL)
				{
					StructuredSelection sel = (StructuredSelection)tblValueViewer.getSelection();
					
					if(!sel.isEmpty())
					{
						Object[] values = sel.toArray();
						for(int i = 0; i < values.length; i++)
							if(arrayValues.indexOf(values[i]) != arrayValues.size() - 1)
								arrayValues.remove(values[i]);
						tblValueViewer.refresh();
					}	
				}
			}
		});
		tblValueViewer.setContentProvider(new ArrayContentProvider());
		tblValueViewer.setLabelProvider(new TableLabelProvider());
		tblValueViewer.setInput(arrayValues);
		
		new ProcessVariableDropTarget(tblValueViewer.getTable())
        {
            @Override
            public void handleDrop(IProcessVariable name, DropTargetEvent event)
            {
            	try
            	{ 
            		if(arrayValues.size() > 0)
	            	{
	            		FilterConditionArrayStringValuesTObject item = (FilterConditionArrayStringValuesTObject)arrayValues.get(arrayValues.size() - 1);
	            		if(Utils.isEmpty(item.getCompValue()))
	            		{
	            			item.setCompValue(name.getName());
	            			return;
	            		}
	            	}
	            	arrayValues.add(new FilterConditionArrayStringValuesTObject(-1, name.getName()));
            	}
            	finally
            	{
            		tblValueViewer.refresh();
            	}
            }
        };
	}

	public void initControls()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] columnNames = store.getString(SampleService.P_FILTER_KEYFIELDS).split(";");
		
		initComboBoxUI(display, cboKeyValue, new ArrayList<String>(Arrays.asList(columnNames)));

		Vector<ItemInterface> vec = new Vector<ItemInterface>();
		vec.add(new DataItem(TYPE_STRING, Messages.FilterConditionArrayStringUI_String));
		vec.add(new DataItem(TYPE_NUMBER, Messages.FilterConditionArrayStringUI_Number));
		vec.add(new DataItem(TYPE_TIME, Messages.FilterConditionArrayStringUI_Time));
		
		initComboBoxUI(display, cboKeyValueType, vec);
	}
	
	private short getOperatorForValueType(int valueType)
	{
		switch(valueType)
		{
		case TYPE_STRING:
			return FilterConditionArrayString.OPERATOR_TEXT_EQUAL;
		case TYPE_NUMBER:
			return FilterConditionArrayString.OPERATOR_NUMERIC_EQUAL;
		case TYPE_TIME:
			return FilterConditionArrayString.OPERATOR_TIME_EQUAL;
		default:
			return -1;
		}
	}
	
	private int getValueTypeForOperator(int operator)
	{
		switch(operator)
		{
		case FilterConditionArrayString.OPERATOR_TEXT_EQUAL:
			return TYPE_STRING;
		case FilterConditionArrayString.OPERATOR_NUMERIC_EQUAL:
			return TYPE_NUMBER;
		case FilterConditionArrayString.OPERATOR_TIME_EQUAL:
			return TYPE_TIME;
		default:
			return -1;
		}
	}
	
	public void createUI(Composite parent)
	{		
		if(composite == null)
		{
			composite = new Composite(parent, SWT.NONE);		
			shell = composite.getShell();
			display = shell.getDisplay();
			createControls();
			initControls();
		}
	}
	
	public void dispose()
	{
		if(composite != null)
			composite.dispose();
	}	

	public boolean check()
	{
		String  text = null;
		
		while(true)
		{
			if(getSelectionIndex(display, cboKeyValue) < 0)
			{	
				text = getText(display, lblKeyValue);
				break;
			}
			
			if(getSelectionIndex(display, cboKeyValueType) < 0)
			{	
				text = getText(display, lblKeyValueType);
				break;
			}
		
			int type  = getSelectedComboBoxIdValueUI(display, cboKeyValueType);
			int count = 0;
			
			Iterator<FilterConditionArrayStringValuesTObject> iter = arrayValues.iterator();
				
			while(iter.hasNext())
			{
				FilterConditionArrayStringValuesTObject item = iter.next();
				if(Utils.isEmpty(item.getCompValue()))
					continue;
				
				count++;
				switch(type)
				{
					case TYPE_STRING:
						break;
					case TYPE_NUMBER:
					{
						try
						{
							showNumberFormat.parse(item.getCompValue());
							break;
						}
						catch(Exception ex)
						{	
							showMessageDialog(shell, 
									"FilterConditionArrayStringUI.Error.InputFormatNumber.Msg", 
									"FilterConditionArrayStringUI.Error.InputFormatNumber.Title",
									SWT.ICON_ERROR);
							return false;
						}
					}
					case TYPE_TIME:
					{
						try
						{
							showTimeFormat.parse(item.getCompValue());
							break;
						}
						catch(Exception ex)
						{
							showMessageDialog(shell, 
									NLS.bind(Messages.FilterConditionArrayStringUI_Error_InputFormatTime_Msg, maskTime),  
									Messages.FilterConditionArrayStringUI_Error_InputFormattime_Title,
									SWT.ICON_ERROR);
							return false;					
						}
					}
				}
				
			}
			if(count == 0)
			{
				text = getText(display, lblValue);
				break;
			}			
			return true;
		}
		text = NLS.bind(Messages.FilterConditionArrayStringUI_Error_MandantoryField_Msg, text);
		showMessageDialog(shell, text, Messages.FilterConditionArrayStringUI_Error_MandantoryField_Title, SWT.ICON_ERROR);
		return false;
	}
	
	public boolean isChanged()
	{
		AggrFilterConditionArrayStringTObject newElement = 
			new AggrFilterConditionArrayStringTObject(
					new FilterConditionArrayStringTObject(), new ArrayList<FilterConditionArrayStringValuesTObject>());		
		get(newElement);	
		
		if(condition != null)
			newElement.getArrayString().setFilterConditionRef(condition.getArrayString().getFilterConditionRef());
		
		Iterator<FilterConditionArrayStringValuesTObject> iter = newElement.getArrayStringValues().iterator();
		
		while(iter.hasNext())
			iter.next().setFilterConditionRef(newElement.getArrayString().getFilterConditionRef());
		
		return !newElement.isEquals(conditionOrg);
	}

	public void create(Connection conDb, int iFilterConditionRef) throws AMSException 
	{
		try
		{
			condition = new AggrFilterConditionArrayStringTObject(new FilterConditionArrayStringTObject(), new ArrayList<FilterConditionArrayStringValuesTObject>());
			get(condition);
			condition.getArrayString().setFilterConditionRef(iFilterConditionRef);
			Iterator<FilterConditionArrayStringValuesTObject> iter = condition.getArrayStringValues().iterator();
			
			while(iter.hasNext())
				iter.next().setFilterConditionRef(iFilterConditionRef);
			AggrFilterConditionArrayStringDAO.insert(conDb, condition);
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
		}
	}

	public void delete(Connection conDb, int iFilterConditionID) throws AMSException 
	{
		try
		{	
			AggrFilterConditionArrayStringDAO.remove(conDb, iFilterConditionID);
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
		}
	}

	public void load(Connection conDb, int iFilterConditionID) throws AMSException 
	{
		try
		{
			AggrFilterConditionArrayStringTObject condition = AggrFilterConditionArrayStringDAO.select(conDb, iFilterConditionID);
			if(condition == null)
				throw new AMSException("Can not load AggrFilterConditionArrayStringTObject completely.");
			set(condition);
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
			throw new AMSException(ex);
		}
	}

	public void save(Connection conDb) throws AMSException 
	{
		try
		{
			if(condition == null)
				condition = new AggrFilterConditionArrayStringTObject(new FilterConditionArrayStringTObject(), new ArrayList<FilterConditionArrayStringValuesTObject>());
			get(condition);
			AggrFilterConditionArrayStringDAO.update(conDb, condition);			
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
			throw new AMSException(ex);
		}
	}
	
	private void initText()
	{
		lblKeyValue.setText(Messages.FilterConditionArrayStringUI_lblKeyValue);
		lblKeyValueType.setText(Messages.FilterConditionArrayStringUI_lblKeyValueType);
		lblValue.setText(Messages.FilterConditionArrayStringUI_lblValue);
	}
	
	private Label lblKeyValue = null;
	private Label lblKeyValueType = null;
	private Label lblValue = null;	
	
	private Composite 	composite = null;
	private Display 	display	= null;
	private Shell 		shell	= null;
	private Combo 		cboKeyValue = null;
	private Combo 		cboKeyValueType = null;
	private Table       tblValue = null;
	private TableViewer tblValueViewer = null;
	
	private class DataItem implements ItemInterface
	{
		private int id      = -1;
		private String text = "";
		
		public DataItem(int id, String text)
		{
			this.id = id;
			this.text = text;
		}
		
		public int getID()
		{
			return id;
		}
		
		public String toString()
		{
			return text == null ? "" : text;
		}
	}
	
	public class VerifyInput implements VerifyListener
	{
		private String validChars = null;
		
		public VerifyInput(String validChars)
		{
			this.validChars = validChars;
		}
		
		public void verifyText(VerifyEvent e)
		{
			switch(e.keyCode)
			{
			case 0:
			case 8:
			case 127:
				return;
			}
			if(validChars != null && validChars.indexOf(e.character) == -1)
				e.doit = false;
		}
	}
	
	public class TableLabelProvider extends LabelProvider implements ITableLabelProvider 
	{
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) 
		{
			String result = "";
			FilterConditionArrayStringValuesTObject item = (FilterConditionArrayStringValuesTObject) element;
			switch (columnIndex) 
			{
				case 0:
					return item.getCompValue();
				default :
					break; 	
			}
			return result;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) 
		{
			return null;
		}

	}
	
	public class TableCellModifier implements ICellModifier 
	{
		/**
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
		 */
		public boolean canModify(Object element, String property) 
		{
			return true;
		}

		/**
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
		 */
		public Object getValue(Object element, String property) 
		{
			FilterConditionArrayStringValuesTObject item = (FilterConditionArrayStringValuesTObject) element;
			return item.getCompValue();
		}

		/**
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value) 
		{
			FilterConditionArrayStringValuesTObject item = (FilterConditionArrayStringValuesTObject)((TableItem) element).getData();

			item.setCompValue((String)value);
			
			if(arrayValues.size() - 1 == arrayValues.indexOf(item) && !Utils.isEmpty(item.getCompValue()))
				arrayValues.add(new FilterConditionArrayStringValuesTObject(-1, ""));
			
			tblValueViewer.refresh();
			
		}
	}
}
