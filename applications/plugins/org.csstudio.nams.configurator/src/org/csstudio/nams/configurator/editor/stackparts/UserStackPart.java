package org.csstudio.nams.configurator.editor.stackparts;

import org.csstudio.ams.configurationStoreService.knownTObjects.UserTObject;
import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class UserStackPart extends AbstractStackPart {
	
	private Composite _main;
	private Text _idTextEntry;
	private Text _nameTextEntry;
	private Text _emailTextEntry;
	private CCombo _groupComboEntry;
	private Text _smsTextEntry;
	private Text _voiceMailTextEntry;
	private CCombo _prefAlarmingTypeComboEntry;
	private Text _statusCodeTextEntry;
	private Text _confirmCodeTextEntry;
	private Button _activeCheckBoxEntry;

	public UserStackPart(DirtyFlagProvider flagProvider, Composite parent) {
		super(flagProvider, UserTObject.class, 2);
		_main = new Composite(parent, SWT.NONE);
		_main.setLayout(new GridLayout(NUM_COLUMNS,false));
		_idTextEntry = this.createTextEntry(_main, "ID:", false);
		_idTextEntry.setText("User");
		this.addSeparator(_main);
		_nameTextEntry = this.createTextEntry(_main, "Name:", true);
		_groupComboEntry = this.createComboEntry(_main, "Group:", true);
		this.addSeparator(_main);
		_emailTextEntry = this.createTextEntry(_main, "Email:", true);
		_smsTextEntry = this.createTextEntry(_main, "SMS number:", true);
		_voiceMailTextEntry = this.createTextEntry(_main, "VoiceMail number:", true);
		_prefAlarmingTypeComboEntry = this.createComboEntry(_main, "Prefered alarming type:", false);
		this.fillPrefAlarmingCombo();
		this.addSeparator(_main);
		_statusCodeTextEntry = this.createTextEntry(_main, "Status code:", true);
		_confirmCodeTextEntry = this.createTextEntry(_main, "Confirm code:", true);
		this.addSeparator(_main);
		_activeCheckBoxEntry = this.createCheckBoxEntry(_main, "User is active:", true);
	}
	
	private void fillPrefAlarmingCombo() {
		for (PreferedAlarm alarm : PreferedAlarm.values()) {
			_prefAlarmingTypeComboEntry.add(alarm.getDescription(), alarm.getIndex());
		}
	}

	public void setInput(UserTObject user) {
		
	}

	@Override
	public Control getMainControl() {
		return _main;
	}
	
	private enum PreferedAlarm {
		NOSELECTION("", 0),
		SMS("SMS",1),
		VOICEMAIL("VoiceMail",2),
		EMAIL("Email", 3);
		
		private String _description;
		private int _index;

		private PreferedAlarm(String description, int index) {
			_description = description;
			_index = index;
		}

		public String getDescription() {
			return _description;
		}

		public int getIndex() {
			return _index;
		}
		
		public static PreferedAlarm getAlarmForDescription(String description) {
			for (PreferedAlarm alarm : PreferedAlarm.values()) {
				if (alarm.getDescription().equals(description)) {
					return alarm;
				}
			}
			return PreferedAlarm.NOSELECTION;
		}
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
