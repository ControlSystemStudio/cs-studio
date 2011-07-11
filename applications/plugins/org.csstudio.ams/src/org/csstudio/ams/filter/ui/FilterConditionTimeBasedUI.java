
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.csstudio.ams.AMSException;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.csstudio.ams.Messages;
import org.csstudio.ams.Utils;
import org.csstudio.ams.dbAccess.ItemInterface;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTimeBasedDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTimeBasedTObject;
import org.csstudio.ams.filter.FilterConditionString;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FilterConditionTimeBasedUI extends FilterConditionUI implements SelectionListener
{
	private NumberFormat storeNumberFormat = NumberFormat.getInstance( Locale.US);
	private NumberFormat showNumberFormat = NumberFormat.getInstance();
		
	private final static int TYPE_STRING = 0;
	private final static int TYPE_NUMBER = 1;
	
	private List<?>[] operatorLists = new List[2];
	
	private FilterConditionTimeBasedTObject condition = null;
	private FilterConditionTimeBasedTObject conditionOrg = null;

	public FilterConditionTimeBasedUI()
	{
		super();
	}

	@Override
    public String getDisplayName() {
		return Messages.FilterConditionTimeBasedUI_DisplayName;
	}

	private void set(FilterConditionTimeBasedTObject condition)
	{
		this.condition = condition;
		this.conditionOrg = (FilterConditionTimeBasedTObject)cloneObject(condition);
		
		setComboBoxValueUI(display, cboKeyValue[0], condition.getStartKeyValue());
		setComboBoxValueUI(display, cboKeyValueType[0], getValueType(condition.getStartOperator()));
		actOnKeyValueTypeChanged(0);
		setComboBoxValueUI(display, cboOperator[0], condition.getStartOperator());
		setText(display, txtValue[0], condition.getStartCompValue());
		
		try
		{
			switch(getValueType(condition.getStartOperator()))
			{
			case TYPE_NUMBER:			
				setText(display, txtValue[0], showNumberFormat.format(storeNumberFormat.parse(condition.getStartCompValue())));
				break;
			}	
		}
		catch(Exception ex)
		{
			Log.log(Log.WARN, ex);
		}
		
		setText(display, txtTimePeriod, "" + condition.getTimePeriod());

		// CHANGED BY: Markus Moeller, 22.08.2007
		if (condition.getTimeBehavior() == 1)
		{
			setSelection(display, optTimeRemoval, true);
			setSelection(display, optTimeConfirm, false);
		}
		else
		{
			setSelection(display, optTimeConfirm, true);
	         setSelection(display, optTimeRemoval, false);
		}
		
		setComboBoxValueUI(display, cboKeyValue[1], condition.getConfirmKeyValue());
		setComboBoxValueUI(display, cboKeyValueType[1], getValueType(condition.getConfirmOperator()));
		actOnKeyValueTypeChanged(1);
		setComboBoxValueUI(display, cboOperator[1], condition.getConfirmOperator());
		setText(display, txtValue[1], condition.getConfirmCompValue());
		
		try
		{
			switch(getValueType(condition.getStartOperator()))
			{
			case TYPE_NUMBER:			
				setText(display, txtValue[1], showNumberFormat.format(storeNumberFormat.parse(condition.getConfirmCompValue())));
				break;
			}	
		} catch(Exception ex) {
		    // Can be ignored
		}
	}
	
	private void get(FilterConditionTimeBasedTObject condition)
	{
		condition.setStartKeyValue(
				getSelectionIndex(display, cboKeyValue[0]) < 0 ? "" : getItem(display, cboKeyValue[0], 
						getSelectionIndex(display, cboKeyValue[0])));		
		condition.setStartOperator((short)getSelectedComboBoxIdValueUI(display, cboOperator[0]));
		condition.setStartCompValue(getText(display, txtValue[0]));
		
		try
		{
			switch(getValueType(condition.getStartOperator()))
			{
			case TYPE_NUMBER:
				condition.setStartCompValue(storeNumberFormat.format(showNumberFormat.parse(condition.getStartCompValue())));
				break;
			}
		}
		catch(Exception ex)
		{
			Log.log(Log.WARN, ex);
		}
		
		condition.setTimePeriod(Short.parseShort(getText(display, txtTimePeriod)));
		
		if (getSelection(display, optTimeRemoval))
			condition.setTimeBehavior((short)1);
		else
			condition.setTimeBehavior((short)0);
		
		condition.setConfirmKeyValue(
				getSelectionIndex(display, cboKeyValue[1]) < 0 ? "" : getItem(display, cboKeyValue[1], 
						getSelectionIndex(display, cboKeyValue[1])));		
		condition.setConfirmOperator((short)getSelectedComboBoxIdValueUI(display, cboOperator[1]));
		condition.setConfirmCompValue(getText(display, txtValue[1]));
		
		try
		{
			switch(getValueType(condition.getConfirmOperator()))
			{
			case TYPE_NUMBER:
				condition.setConfirmCompValue(storeNumberFormat.format(showNumberFormat.parse(condition.getConfirmCompValue())));
				break;
			}
		}
		catch(Exception ex)
		{
			Log.log(Log.WARN, ex);
		}
	}
	
	@Override
    public void reset() {
		setSelection(display, optTimeRemoval, true);
		for(int i = 0; i <= 1; i++) {
			deselectAll(display, cboKeyValue[i]);
			deselectAll(display, cboKeyValueType[i]);
			actOnKeyValueTypeChanged(i);
		}
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
		
		pnGroup[0] = new Group(composite, SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginLeft = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		pnGroup[0].setLayout(gridLayout);
		pnGroup[0].setLayoutData(getGridData(-1,-1, 2,1, SWT.FILL, SWT.END, false, true));
		
		grpOptions = new Group(composite, SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginLeft = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		grpOptions.setLayout(gridLayout);
		grpOptions.setLayoutData(getGridData(-1,-1, 2, 1, SWT.FILL, SWT.END, false, true));
		
		lblTimePeriod = new Label(grpOptions, SWT.None);
		lblTimePeriod.setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));	
		txtTimePeriod = new Text(grpOptions, SWT.BORDER | SWT.SINGLE);
		txtTimePeriod.setLayoutData(getGridData(50,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));
		txtTimePeriod.setTextLimit(5);
		txtTimePeriod.addVerifyListener(new org.csstudio.ams.gui.VerifyInput("1234567890"));
		
		optTimeRemoval = new Button(grpOptions, SWT.RADIO);
		optTimeRemoval.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.BEGINNING, SWT.CENTER, false, false));
		optTimeRemoval.addSelectionListener(this);
		
		new Label(grpOptions, SWT.NONE)
			.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.BEGINNING, SWT.CENTER, false, false));	
 
		optTimeConfirm = new Button(grpOptions, SWT.RADIO);
		optTimeConfirm.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.BEGINNING, SWT.CENTER, false, false));
		optTimeConfirm.addSelectionListener(this);
		
		pnGroup[1] = new Group(composite, SWT.NONE);
		gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginLeft = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		pnGroup[1].setLayout(gridLayout);
		pnGroup[1].setLayoutData(getGridData(-1,-1, 2,1, SWT.FILL, SWT.END, false, true));
		
		for(int i = 0; i <= 1; i++)
		{
			lblKeyValue[i] = new Label(pnGroup[i], SWT.None);
			lblKeyValue[i].setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, true));		
			cboKeyValue[i] = new Combo(pnGroup[i], SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
			cboKeyValue[i].setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, true, false));
			
			lblKeyValueType[i] = new Label(pnGroup[i], SWT.None);
			lblKeyValueType[i].setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));	
			cboKeyValueType[i] = new Combo(pnGroup[i], SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
			cboKeyValueType[i].setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));
			
			lblOperator[i] = new Label(pnGroup[i], SWT.None);
			lblOperator[i].setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));	
			cboOperator[i] = new Combo(pnGroup[i], SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
			cboOperator[i].setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));
			
			lblValue[i] = new Label(pnGroup[i], SWT.None);
			lblValue[i].setLayoutData(getGridData(-1,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, true));	
			txtValue[i] = new Text(pnGroup[i], SWT.BORDER | SWT.SINGLE);
			txtValue[i].setLayoutData(getGridData(200,-1, 1,1, SWT.BEGINNING, SWT.CENTER, false, false));
			
			cboKeyValueType[i].addSelectionListener(new SelectionAdapter()
			{
				@Override
                public void widgetSelected(SelectionEvent e) 
				{
					actOnKeyValueTypeChanged(e.getSource() == cboKeyValueType[0] ? 0 : 1);
				}
			});

			new ProcessVariableDropTarget(txtValue[i])
	        {
	            @Override
	            public void handleDrop(IProcessVariable name,
	                                   DropTargetEvent event)
	            {
	            	((Text)((DropTarget)event.getSource()).getControl())
	            		.setText(name.getName());
	            }
	        };
		}

		initText();
	}
	
	@Override
    public void widgetSelected(SelectionEvent e)
	{
		if(e.getSource() == optTimeRemoval)
		{
			pnGroup[1].setText(Messages.FilterConditionTimeBasedUI_pnConditionRemoval);
		}
		else
		{
			pnGroup[1].setText(Messages.FilterConditionTimeBasedUI_pnConditionConfirm);
		}
	}

	@Override
    public void widgetDefaultSelected(SelectionEvent e) {
	    // Nothing do do here?
	}
	
	private VerifyInput currentValueVerify = null;
	
	private void actOnKeyValueTypeChanged(int field)
	{
		int typeID = getSelectedComboBoxIdValueUI(display, cboKeyValueType[field]);
		
		if(currentValueVerify != null)
		{
			removeVerifyListener(display, txtValue[field], currentValueVerify);
			currentValueVerify = null;
		}
		
		if(typeID < 0)
		{
			deselectAll(display, cboOperator[field]);
			setEnabled(display, cboOperator[field], false);
			setText(display, txtValue[field], "");
			setEnabled(display, txtValue[field], false);
		}
		else
		{
			initComboBoxUI(display, cboOperator[field], operatorLists[typeID]);
			setEnabled(display, cboOperator[field], true);
			setText(display, txtValue[field], "");
			setEnabled(display, txtValue[field], true);
			
			if(typeID == TYPE_NUMBER)
			{
				currentValueVerify = new VerifyInput("1234567890,.-");
				addVerifyListener(display, txtValue[field], currentValueVerify);
			}
		}
	}
	
	public void initControls()
	{
		IPreferenceStore store = AmsActivator.getDefault().getPreferenceStore();
		String[] columnNames = store.getString(AmsPreferenceKey.P_FILTER_KEYFIELDS).split(";");
		
		initComboBoxUI(display, cboKeyValue[0], new ArrayList<String>(Arrays.asList(columnNames)));
		initComboBoxUI(display, cboKeyValue[1], new ArrayList<String>(Arrays.asList(columnNames)));
		
		Vector<ItemInterface> vec = new Vector<ItemInterface>();
		vec.add(new DataItem(TYPE_STRING, Messages.FilterConditionStringUI_String));
		vec.add(new DataItem(TYPE_NUMBER, Messages.FilterConditionStringUI_Number));
		
		initComboBoxUI(display, cboKeyValueType[0], vec);
		initComboBoxUI(display, cboKeyValueType[1], vec);
		
		vec = new Vector<ItemInterface>();
		vec.add(new DataItem(FilterConditionString.OPERATOR_TEXT_EQUAL, Messages.OPERATOR_TEXT_EQUAL));
		vec.add(new DataItem(FilterConditionString.OPERATOR_TEXT_NOT_EQUAL, Messages.OPERATOR_TEXT_NOT_EQUAL));
		operatorLists[TYPE_STRING] = vec;
		
		vec = new Vector<ItemInterface>();
		vec.add(new DataItem(FilterConditionString.OPERATOR_NUMERIC_LT, Messages.OPERATOR_NUMERIC_LT));
		vec.add(new DataItem(FilterConditionString.OPERATOR_NUMERIC_LT_EQUAL, Messages.OPERATOR_NUMERIC_LT_EQUAL));
		vec.add(new DataItem(FilterConditionString.OPERATOR_NUMERIC_EQUAL, Messages.OPERATOR_NUMERIC_EQUAL));
		vec.add(new DataItem(FilterConditionString.OPERATOR_NUMERIC_GT_EQUAL, Messages.OPERATOR_NUMERIC_GT_EQUAL));
		vec.add(new DataItem(FilterConditionString.OPERATOR_NUMERIC_GT, Messages.OPERATOR_NUMERIC_GT));
		vec.add(new DataItem(FilterConditionString.OPERATOR_NUMERIC_NOT_EQUAL, Messages.OPERATOR_NUMERIC_NOT_EQUAL));
		operatorLists[TYPE_NUMBER] = vec;

		reset();
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
		if(!(check(0) && check(1)))
			return false;
		
		String  text = null;
		
		while(true)
		{
			if(isEmpty(display, txtTimePeriod))
			{
				text = getText(display, lblTimePeriod);
				break;				
			}
			if(Short.parseShort(getText(display, txtTimePeriod)) < 1)
			{
				showMessageDialog(shell, "FilterConditionTimeBasedUI.Error.TimePeriod.Msg", "FilterConditionTimeBasedUI.Error.TimePeriod.Title", SWT.ICON_ERROR);
				return false;
			}
			return true;
		}
		text = NLS.bind(Messages.FilterConditionTimeBasedUI_Error_MandantoryField_Msg, text);
		showMessageDialog(shell, text, Messages.FilterConditionTimeBasedUI_Error_MandantoryField_Title, SWT.ICON_ERROR);
		return false;
	}

	public boolean check(int fieldID)
	{
		String  text = null;
		
		while(true)
		{
			if(getSelectionIndex(display, cboKeyValue[fieldID]) < 0)
			{	
				text = getText(display, lblKeyValue[fieldID]);
				break;
			}
			if(getSelectionIndex(display, cboKeyValueType[fieldID]) < 0)
			{
				text = getText(display, lblKeyValueType[fieldID]);
				break;				
			}
			if(getSelectionIndex(display, cboOperator[fieldID]) < 0)
			{
				text = getText(display, lblOperator[fieldID]);
				break;				
			}
			if(isEmpty(display, txtValue[fieldID]))
			{
				text = getText(display, lblValue[fieldID]);
				break;				
			}
			switch(getSelectedComboBoxIdValueUI(display, cboKeyValueType[fieldID]))
			{
			case TYPE_STRING:
				break;
			case TYPE_NUMBER:
			{
				try
				{
					showNumberFormat.parse(getText(display, txtValue[fieldID]));
					break;
				}
				catch(Exception ex)
				{
					showMessageDialog(shell, 
							"FilterConditionTimeBasedUI.Error.InputFormatNumber.Msg", 
							"FilterConditionTimeBasedUI.Error.InputFormatNumber.Title",
							SWT.ICON_ERROR);
					return false;
				}
			}
			}
			return true;
		}
		text = NLS.bind(Messages.FilterConditionTimeBasedUI_Error_MandantoryField_Msg, text);
		showMessageDialog(shell, text, Messages.FilterConditionTimeBasedUI_Error_MandantoryField_Title, SWT.ICON_ERROR);
		return false;
	}
	
	@Override
    public boolean isChanged()
	{
		FilterConditionTimeBasedTObject newElement = new FilterConditionTimeBasedTObject();
		
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
			condition = new FilterConditionTimeBasedTObject();
			get(condition);
			condition.setFilterConditionRef(iFilterConditionID);
			FilterConditionTimeBasedDAO.insert(conDb, condition);
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
			FilterConditionTimeBasedDAO.remove(conDb, iFilterConditionID);
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
			set(FilterConditionTimeBasedDAO.select(conDb, iFilterConditionID));
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
			FilterConditionTimeBasedDAO.update(conDb, condition);			
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
			throw new AMSException(ex);
		}
	}
	
	private void initText()
	{
		pnGroup[0].setText(Messages.FilterConditionTimeBasedUI_pnStartCondition);
		lblKeyValue[0].setText(Messages.FilterConditionTimeBasedUI_lblStartKeyValue);
		lblKeyValueType[0].setText(Messages.FilterConditionTimeBasedUI_lblStartKeyValueType);
		lblOperator[0].setText(Messages.FilterConditionTimeBasedUI_lblStartOperator);
		lblValue[0].setText(Messages.FilterConditionTimeBasedUI_lblStartValue);
		
		grpOptions.setText(Messages.FilterConditionTimeBasedUI_pnOptions);
		lblTimePeriod.setText(Messages.FilterConditionTimeBasedUI_lblTimePeriod);
		
		optTimeRemoval.setText(Messages.FilterConditionTimeBasedUI_optTimeRemoval);
		optTimeConfirm.setText(Messages.FilterConditionTimeBasedUI_optTimeConfirm);

		pnGroup[1].setText(Messages.FilterConditionTimeBasedUI_pnConditionRemoval);
		lblKeyValue[1].setText(Messages.FilterConditionTimeBasedUI_lblConfirmKeyValue);
		lblKeyValueType[1].setText(Messages.FilterConditionTimeBasedUI_lblConfirmKeyValueType);
		lblOperator[1].setText(Messages.FilterConditionTimeBasedUI_lblConfirmOperator);
		lblValue[1].setText(Messages.FilterConditionTimeBasedUI_lblConfirmValue);
	}
	
	private Label[] lblKeyValue = new Label[2];
	private Label[] lblKeyValueType = new Label[2];
	private Label[] lblOperator = new Label[2];
	private Label[] lblValue = new Label[2];	
	private Label 	lblTimePeriod = null;
	
	private Composite 	composite = null;
	private Shell 		shell	= null;
	private Display 	display	= null;
	
	private Group[]	    pnGroup = new Group[2];   
	private Group 		grpOptions = null;
	private Combo[] 	cboKeyValue = new Combo[2];
	private Combo[] 	cboKeyValueType = new Combo[2];
	private Combo[] 	cboOperator = new Combo[2];
	private Text[]  	txtValue = new Text[2];
	private Text		txtTimePeriod = null;

	private Button 		optTimeRemoval = null;
	private Button 		optTimeConfirm = null;
	
	private class DataItem implements ItemInterface
	{
		private int id      = -1;
		private String text = "";
		
		public DataItem(int id, String text)
		{
			this.id = id;
			this.text = text;
		}
		
		@Override
        public int getID()
		{
			return id;
		}
		
		@Override
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
