
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
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.csstudio.ams.AMSException;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.csstudio.ams.Messages;
import org.csstudio.ams.dbAccess.ItemInterface;
import org.csstudio.ams.dbAccess.configdb.FilterConditionStringDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionStringTObject;
import org.csstudio.ams.filter.FilterConditionString;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FilterConditionStringUI extends FilterConditionUI {
	
    private DateFormat storeTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
	private DateFormat showTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
	private NumberFormat storeNumberFormat = NumberFormat.getInstance( Locale.US);
	private NumberFormat showNumberFormat = NumberFormat.getInstance();
	
	
	private String maskTime   = ((SimpleDateFormat)showTimeFormat).toLocalizedPattern();
	
	private final static int TYPE_STRING = 0;
	private final static int TYPE_NUMBER = 1;
	private final static int TYPE_TIME = 2;
	
	private List<?>[] operatorLists = new List[3];
	
	private FilterConditionStringTObject condition = null;
	private FilterConditionStringTObject conditionOrg = null;
	
	public FilterConditionStringUI() {
		super();
	}
	
	@Override
    public String getDisplayName() {
		return Messages.FilterConditionStringBasedUI_DisplayName;
	}

	private void set(FilterConditionStringTObject condition)
	{
		this.condition = condition;
		this.conditionOrg = (FilterConditionStringTObject)cloneObject(condition);
		
		setComboBoxValueUI(display, cboKeyValue, condition.getKeyValue());
		setComboBoxValueUI(display, cboKeyValueType, getValueType(condition.getOperator()));
		actOnKeyValueTypeChanged();
		setComboBoxValueUI(display, cboOperator, condition.getOperator());
		setText(display, txtValue, condition.getCompValue());
		
		switch(getValueType(condition.getOperator()))
		{
		case TYPE_NUMBER:
		{
			try
			{
				setText(display, txtValue, showNumberFormat.format(storeNumberFormat.parse(condition.getCompValue())));
			}
			catch(Exception ex)
			{
				Log.log(Log.WARN, ex);
			}
			break;
		}
		case TYPE_TIME:
		{
			try
			{
				setText(display, txtValue, showTimeFormat.format(storeTimeFormat.parse(condition.getCompValue())));
			}
			catch(Exception ex)
			{
				Log.log(Log.WARN, ex);
			}
			break;
		}
		}
	}
	
	private void get(FilterConditionStringTObject condition)
	{
		condition.setKeyValue(
				getSelectionIndex(display, cboKeyValue) < 0 ? "" : getItem(display, cboKeyValue, getSelectionIndex(display, cboKeyValue)));		
		condition.setOperator((short)getSelectedComboBoxIdValueUI(display, cboOperator));
		condition.setCompValue(getText(display, txtValue));
		
		switch(getValueType(condition.getOperator()))
		{
		case TYPE_NUMBER:
		{
			try
			{
				condition.setCompValue(storeNumberFormat.format(showNumberFormat
						.parse(condition.getCompValue())));
			} catch(Exception ex) {
			    // Can be ignored
			}
			break;
		}
		case TYPE_TIME:
		{
			try
			{
				condition.setCompValue(storeTimeFormat.format(showTimeFormat
						.parse(condition.getCompValue())));
			} catch(Exception ex) {
			    // Can be ignored
			}
			break;
		}
		}
	}
	
	@Override
    public void reset()
	{
		cboKeyValue.deselectAll();
		cboKeyValueType.deselectAll();
		actOnKeyValueTypeChanged();
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
		lblKeyValue.setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, true));		
		cboKeyValue = new Combo(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		cboKeyValue.setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, true, false));
		
		lblKeyValueType = new Label(composite, SWT.None);
		lblKeyValueType.setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));	
		cboKeyValueType = new Combo(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		cboKeyValueType.setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));
		
		lblOperator = new Label(composite, SWT.None);
		lblOperator.setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));	
		cboOperator = new Combo(composite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		cboOperator.setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));
		
		lblValue = new Label(composite, SWT.None);
		lblValue.setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, true));	
		txtValue = new Text(composite, SWT.BORDER | SWT.SINGLE);
		txtValue.setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));
		
		cboKeyValueType.addSelectionListener(new SelectionAdapter()
		{
			@Override
            public void widgetSelected(SelectionEvent e) 
			{
				actOnKeyValueTypeChanged();
			}
		});
		
		new ProcessVariableDropTarget(txtValue)
        {
            @Override
            public void handleDrop(IProcessVariable name,
                                   DropTargetEvent event)
            {
            	txtValue.setText(name.getName());
            }
        };
		
		initText();
	}
	
	private VerifyInput currentValueVerify = null;
	
	private void actOnKeyValueTypeChanged()
	{
		int typeID = getSelectedComboBoxIdValueUI(display, cboKeyValueType);
		
		if(currentValueVerify != null)
		{
			removeVerifyListener(display, txtValue, currentValueVerify);
			currentValueVerify = null;
		}
		
		if(typeID < 0)
		{
			deselectAll(display, cboOperator);
			setEnabled(display, cboOperator, false);
			setText(display, txtValue, "");
			setEnabled(display, txtValue, false);
		}
		else
		{
			initComboBoxUI(display, cboOperator, operatorLists[typeID]);
			setEnabled(display, cboOperator, true);
			setText(display, txtValue, "");
			setEnabled(display, txtValue, true);
			
			if(typeID == TYPE_NUMBER)
			{
				currentValueVerify = new VerifyInput("1234567890,.-");
				addVerifyListener(display, txtValue, currentValueVerify);
			}
		}
	}
	
	
	public void initControls()
	{
		IPreferenceStore store = AmsActivator.getDefault().getPreferenceStore();
		String[] columnNames = store.getString(AmsPreferenceKey.P_FILTER_KEYFIELDS).split(";");
		
		initComboBoxUI(display, cboKeyValue, new ArrayList<String>(Arrays.asList(columnNames)));
		
		Vector<ItemInterface> vec = new Vector<ItemInterface>();
		vec.add(new ComboWidgetIdDataItem(TYPE_STRING, Messages.FilterConditionStringUI_String));
		vec.add(new ComboWidgetIdDataItem(TYPE_NUMBER, Messages.FilterConditionStringUI_Number));
		vec.add(new ComboWidgetIdDataItem(TYPE_TIME, Messages.FilterConditionStringUI_Time));
		
		initComboBoxUI(display, cboKeyValueType, vec);
		
		vec = new Vector<ItemInterface>();
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_TEXT_EQUAL, Messages.OPERATOR_TEXT_EQUAL));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_TEXT_NOT_EQUAL, Messages.OPERATOR_TEXT_NOT_EQUAL));
		operatorLists[TYPE_STRING] = vec;
		
		vec = new Vector<ItemInterface>();
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_NUMERIC_LT, Messages.OPERATOR_NUMERIC_LT));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_NUMERIC_LT_EQUAL, Messages.OPERATOR_NUMERIC_LT_EQUAL));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_NUMERIC_EQUAL, Messages.OPERATOR_NUMERIC_EQUAL));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_NUMERIC_GT_EQUAL, Messages.OPERATOR_NUMERIC_GT_EQUAL));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_NUMERIC_GT, Messages.OPERATOR_NUMERIC_GT));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_NUMERIC_NOT_EQUAL, Messages.OPERATOR_NUMERIC_NOT_EQUAL));
		operatorLists[TYPE_NUMBER] = vec;
		
		vec = new Vector<ItemInterface>();
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_TIME_BEFORE, Messages.OPERATOR_TIME_BEFORE));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_TIME_BEFORE_EQUAL, Messages.OPERATOR_TIME_BEFORE_EQUAL));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_TIME_EQUAL, Messages.OPERATOR_TIME_EQUAL));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_TIME_AFTER_EQUAL, Messages.OPERATOR_TIME_AFTER_EQUAL));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_TIME_AFTER, Messages.OPERATOR_TIME_AFTER));
		vec.add(new ComboWidgetIdDataItem(FilterConditionString.OPERATOR_TIME_NOT_EQUAL, Messages.OPERATOR_TIME_NOT_EQUAL));
		operatorLists[TYPE_TIME] = vec;
	}
	
	private int getValueType(int operatorID)
	{
		switch(operatorID)
		{
		case FilterConditionString.OPERATOR_TEXT_EQUAL:
		case FilterConditionString.OPERATOR_TEXT_NOT_EQUAL:
			return TYPE_STRING;
		case FilterConditionString.OPERATOR_NUMERIC_LT:
		case FilterConditionString.OPERATOR_NUMERIC_LT_EQUAL:
		case FilterConditionString.OPERATOR_NUMERIC_EQUAL:
		case FilterConditionString.OPERATOR_NUMERIC_GT_EQUAL:
		case FilterConditionString.OPERATOR_NUMERIC_GT:
		case FilterConditionString.OPERATOR_NUMERIC_NOT_EQUAL:
			return TYPE_NUMBER;
		case FilterConditionString.OPERATOR_TIME_BEFORE:
		case FilterConditionString.OPERATOR_TIME_BEFORE_EQUAL:
		case FilterConditionString.OPERATOR_TIME_EQUAL:
		case FilterConditionString.OPERATOR_TIME_AFTER_EQUAL:
		case FilterConditionString.OPERATOR_TIME_AFTER:
		case FilterConditionString.OPERATOR_TIME_NOT_EQUAL:
			return TYPE_TIME;
		default:
			return -1;
		}
	}
	
	@Override
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
	
	@Override
    public void dispose()
	{
		if(composite != null)
			composite.dispose();
	}
	
	@Override
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
			if(getSelectionIndex(display, cboOperator) < 0)
			{
				text = getText(display, lblOperator);
				break;				
			}
			if(isEmpty(display, txtValue))
			{
				text = getText(display, lblValue);
				break;				
			}
			switch(getSelectedComboBoxIdValueUI(display, cboKeyValueType))
			{
			case TYPE_STRING:
				break;
			case TYPE_NUMBER:
			{
				try
				{
					showNumberFormat.parse(getText(display, txtValue));
					break;
				}
				catch(Exception ex)
				{
					showMessageDialog(shell, 
							"FilterConditionStringUI.Error.InputFormatNumber.Msg", 
							"FilterConditionStringUI.Error.InputFormatNumber.Title",
							SWT.ICON_ERROR);
					return false;
				}
			}
			case TYPE_TIME:
			{
				try
				{
					showTimeFormat.parse(getText(display, txtValue));
					break;
				}
				catch(Exception ex)
				{
					showMessageDialog(shell, 
							NLS.bind(Messages.FilterConditionStringUI_Error_InputFormatTime_Msg, maskTime),  
							Messages.FilterConditionStringUI_Error_InputFormattime_Title,
							SWT.ICON_ERROR);
					return false;					
				}
			}
			}
			return true;
		}
		text = NLS.bind(Messages.FilterConditionStringUI_Error_MandantoryField_Msg, text);
		showMessageDialog(shell, text, Messages.FilterConditionStringUI_Error_MandantoryField_Title, SWT.ICON_ERROR);
		return false;
	}
	
	@Override
    public boolean isChanged()
	{
		FilterConditionStringTObject newElement = new FilterConditionStringTObject();
		get(newElement);	
		
		if(condition != null)
			newElement.setFilterConditionRef(condition.getFilterConditionRef());
		
		return !newElement.equals(conditionOrg);
	}

	@Override
    public void create(Connection conDb, int iFilterConditionID) throws AMSException 
	{
		try
		{
			condition = new FilterConditionStringTObject();
			get(condition);
			condition.setFilterConditionRef(iFilterConditionID);
			FilterConditionStringDAO.insert(conDb, condition);
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
		}
	}

	@Override
    public void delete(Connection conDb, int iFilterConditionID) throws AMSException 
	{
		try
		{	
			FilterConditionStringDAO.remove(conDb, iFilterConditionID);
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
		}
	}

	@Override
    public void load(Connection conDb, int iFilterConditionID) throws AMSException 
	{
		try
		{	
			set(FilterConditionStringDAO.select(conDb, iFilterConditionID));
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
		}
	}

	@Override
    public void save(Connection conDb) throws AMSException 
	{
		try
		{
			get(condition);			
			FilterConditionStringDAO.update(conDb, condition);			
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
			throw new AMSException(ex);
		}
	}
	
	private void initText()
	{
		lblKeyValue.setText(Messages.FilterConditionStringUI_lblKeyValue);
		lblKeyValueType.setText(Messages.FilterConditionStringUI_lblKeyValueType);
		lblOperator.setText(Messages.FilterConditionStringUI_lblOperator);
		lblValue.setText(Messages.FilterConditionStringUI_lblValue);
	}
	
	private Label lblKeyValue = null;
	private Label lblKeyValueType = null;
	private Label lblOperator = null;
	private Label lblValue = null;	
	
	private Composite 	composite = null;
	private Shell 		shell	= null;
	private Display 	display	= null;
	private Combo 		cboKeyValue = null;
	private Combo 		cboKeyValueType = null;
	private Combo 		cboOperator = null;
	private Text  		txtValue = null;
	
	public class VerifyInput implements VerifyListener
	{
		private String validChars = null;
		
		public VerifyInput(String validChars)
		{
			this.validChars = validChars;
		}
		
		@Override
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
}
