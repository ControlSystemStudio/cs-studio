package org.csstudio.nams.configurator.editor.stackparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.csstudio.nams.configurator.treeviewer.model.IConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.IConfigurationModel;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeiterBean.PreferedAlarmType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class UserStackPart extends AbstractStackPart<AlarmbearbeiterBean> {

	private Composite _main;
	private Text _nameTextEntry;
	private Text _emailTextEntry;
	private Combo _groupComboEntry;
	private Text _smsTextEntry;
	private Text _voiceMailTextEntry;
	private Combo _prefAlarmingTypeComboEntry;
	private Text _statusCodeTextEntry;
	private Text _confirmCodeTextEntry;
	private Button _activeCheckBoxEntry;

	private AlarmbearbeiterBean alarmbearbeiterBean;

	private AlarmbearbeiterBean alarmbearbeiterClone;
	private IConfigurationModel model;

	public UserStackPart(DirtyFlagProvider flagProvider, Composite parent) {
		super(flagProvider, AlarmbearbeiterBean.class, 2);
		_main = new Composite(parent, SWT.NONE);
		_main.setLayout(new GridLayout(NUM_COLUMNS, false));
		this.addSeparator(_main);
		_nameTextEntry = this.createTextEntry(_main, "Name:", true);
		_groupComboEntry = this.createComboEntry(_main, "Group:", true);
		this.addSeparator(_main);
		_emailTextEntry = this.createTextEntry(_main, "Email:", true);
		_smsTextEntry = this.createTextEntry(_main, "SMS number:", true);
		_voiceMailTextEntry = this.createTextEntry(_main, "VoiceMail number:",
				true);
		_prefAlarmingTypeComboEntry = this.createComboEntry(_main,
				"Prefered alarming type:", false);
		this.initPrefAlarmingCombo();
		this.addSeparator(_main);
		_statusCodeTextEntry = this
				.createTextEntry(_main, "Status code:", true);
		_confirmCodeTextEntry = this.createTextEntry(_main, "Confirm code:",
				true);
		this.addSeparator(_main);
		_activeCheckBoxEntry = this.createCheckBoxEntry(_main,
				"User is active:", true);
	}

	@Override
	public Control getMainControl() {
		return _main;
	}

	@Override
	public void setInput(IConfigurationBean input, IConfigurationModel model) {
		this.model = model;
		this.alarmbearbeiterBean = (AlarmbearbeiterBean) input;
		this.alarmbearbeiterClone = ((AlarmbearbeiterBean) input).getClone();

		// init JFaceDatabinding after input is set
		this.initDataBinding();
		this.initGroupCombo();
	}

	private void initGroupCombo() {
		for (String groupName : this.model.getSortgroupNames()) {
			this._groupComboEntry.add(groupName);
		}
	}

	private void initPrefAlarmingCombo() {
		for (PreferedAlarmType alarm : PreferedAlarmType.values()) {
			_prefAlarmingTypeComboEntry.add(alarm.name());
		}
	}

	@Override
	public boolean isDirty() {
		if (this.alarmbearbeiterBean != null
				&& this.alarmbearbeiterClone != null) {
			boolean dirty = !this.alarmbearbeiterBean
					.equals(this.alarmbearbeiterClone);
			return dirty;
		} else
			return false;
	}

	@Override
	public void save() {
		// welche gruppe hat user gewählt?
		String group = this._groupComboEntry.getItem(this._groupComboEntry
				.getSelectionIndex());

		// speicher Änderungen im lokalen Model
		this.model.save(this.alarmbearbeiterClone, group);

		// copy clonse state to original bean
		this.alarmbearbeiterBean.updateState(this.alarmbearbeiterClone);

		// create new clone
		this.alarmbearbeiterClone = this.alarmbearbeiterBean.getClone();
	}

	private void initDataBinding() {
		DataBindingContext context = new DataBindingContext();

		IObservableValue nameTextObservable = BeansObservables.observeValue(
				this.alarmbearbeiterClone,
				AlarmbearbeiterBean.PropertyNames.name.name());

		IObservableValue emailTextObservable = BeansObservables.observeValue(
				this.alarmbearbeiterClone,
				AlarmbearbeiterBean.PropertyNames.email.name());

		IObservableValue smsTextObservable = BeansObservables.observeValue(
				this.alarmbearbeiterClone,
				AlarmbearbeiterBean.PropertyNames.mobilePhone.name());

		IObservableValue voicemailTextObservable = BeansObservables
				.observeValue(this.alarmbearbeiterClone,
						AlarmbearbeiterBean.PropertyNames.phone.name());

		IObservableValue statusTextObservable = BeansObservables.observeValue(
				this.alarmbearbeiterClone,
				AlarmbearbeiterBean.PropertyNames.statusCode.name());

		IObservableValue confirmTextObservable = BeansObservables.observeValue(
				this.alarmbearbeiterClone,
				AlarmbearbeiterBean.PropertyNames.confirmCode.name());

		// IObservableValue activeCheckboxObservable = BeansObservables
		// .observeValue(this.alarmbearbeiterClone,
		// AlarmbearbeiterBean.PropertyNames.active.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_nameTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(_emailTextEntry,
				SWT.Modify), emailTextObservable, null, null);

		context.bindValue(
				SWTObservables.observeText(_smsTextEntry, SWT.Modify),
				smsTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(_voiceMailTextEntry,
				SWT.Modify), voicemailTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(_statusCodeTextEntry,
				SWT.Modify), statusTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(_confirmCodeTextEntry,
				SWT.Modify), confirmTextObservable, null, null);

		// context.bindValue(SWTObservables.observeEditable(_activeCheckBoxEntry),
		// activeCheckboxObservable, null, null);
	}

	@Override
	public void setPropertyChangedListener(PropertyChangeListener listener) {
		this.alarmbearbeiterClone.addPropertyChangeListener(listener);
	}

	@Override
	public void setFocus() {
		this._nameTextEntry.setFocus();

	}

}
