package org.csstudio.nams.configurator.editor.stackparts;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
@Deprecated
public class UserStackPart extends AbstractStackPart<AlarmbearbeiterBean> {

	private Text _nameTextEntry;
	private Text _emailTextEntry;
	private Combo _groupComboEntry;
	private Text _smsTextEntry;
	private Text _voiceMailTextEntry;
	private Combo _prefAlarmingTypeComboEntry;
	private Text _statusCodeTextEntry;
	private Text _confirmCodeTextEntry;
	private Button _activeCheckBoxEntry;




	public UserStackPart(DirtyFlagProvider flagProvider, Composite parent) {
		super(flagProvider, AlarmbearbeiterBean.class, 2);
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		this.addSeparator(main);
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_groupComboEntry = this.createComboEntry(main, "Group:", true);
//		_groupComboEntry.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				/*
//				 * Feuer property change event ab, damit der Dirty-Flag geändert
//				 * wird. Persistiert wird erst beim Klick auf "Speichern"
//				 */
//				listener.propertyChange(null);
//			}
//		});
		this.addSeparator(main);
		_emailTextEntry = this.createTextEntry(main, "Email:", true);
		_smsTextEntry = this.createTextEntry(main, "SMS number:", true);
		_voiceMailTextEntry = this.createTextEntry(main, "VoiceMail number:",
				true);
		_prefAlarmingTypeComboEntry = this.createComboEntry(main,
				"Prefered alarming type:", false);
		this.initPrefAlarmingCombo();
		this.addSeparator(main);
		_statusCodeTextEntry = this
				.createTextEntry(main, "Status code:", true);
		_confirmCodeTextEntry = this.createTextEntry(main, "Confirm code:",
				true);
		this.addSeparator(main);
		_activeCheckBoxEntry = this.createCheckBoxEntry(main,
				"User is active:", true);
	}

//	@Override
//	public void setInput(IConfigurationBean input, IConfigurationModel model) {
//		super.setInput(input, model);
//		// TODO redo group initialization may 
//		// this.initGroupCombo();
//	}

	// TODO get AMS_UserGroups DTO's and insert them into the HibernateMapping
	// private void initGroupCombo() {
	// Collection<String> sortgroupNames = this.model.getSortgroupNames();
	// Object[] groupNames = sortgroupNames.toArray();
	//
	// int selection = 0;
	// for (int groupName = 0; groupName < groupNames.length; groupName++) {
	// this._groupComboEntry.add(((String) groupNames[groupName]).trim());
	// }
	//
	// this._groupComboEntry.select(selection);
	// }

	private void initPrefAlarmingCombo() {
		for (PreferedAlarmType alarm : PreferedAlarmType.values()) {
			_prefAlarmingTypeComboEntry.add(alarm.name());
		}
	}

	// @Override
	// public void save() {
	// // welche gruppe hat user gewählt?
	// //TODO may bla
	// // String group = this._groupComboEntry.getItem(this._groupComboEntry
	// // .getSelectionIndex());
	//
	// // speicher Änderungen im lokalen Model
	// IConfigurationBean updatedBean = this.model.save(
	// this.beanClone);
	//
	// // copy clone state to original bean
	// this.bean = (AlarmbearbeiterBean) updatedBean;
	//
	// // create new clone
	// this.beanClone = this.bean.getClone();
	// }
	@Override
	protected void initDataBinding() {
		DataBindingContext context = new DataBindingContext();

		IObservableValue nameTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmbearbeiterBean.PropertyNames.name.name());

		IObservableValue emailTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmbearbeiterBean.PropertyNames.email.name());

		IObservableValue smsTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmbearbeiterBean.PropertyNames.mobilePhone
						.name());

		IObservableValue voicemailTextObservable = BeansObservables
				.observeValue(this.beanClone,
						AlarmbearbeiterBean.PropertyNames.phone.name());

		IObservableValue statusTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmbearbeiterBean.PropertyNames.statusCode
						.name());

		IObservableValue confirmTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmbearbeiterBean.PropertyNames.confirmCode
						.name());

		IObservableValue activeCheckboxObservable = BeansObservables
				.observeValue(this.beanClone,
						AlarmbearbeiterBean.PropertyNames.active.name());

		IObservableValue prefAlarmingTypeObservable = BeansObservables
				.observeValue(this.beanClone,
						AlarmbearbeiterBean.PropertyNames.preferedAlarmType
								.name());

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

		context.bindValue(
				SWTObservables.observeSelection(_activeCheckBoxEntry),
				activeCheckboxObservable, null, null);

		context.bindValue(SWTObservables
				.observeSelection(_prefAlarmingTypeComboEntry),
				prefAlarmingTypeObservable, new UpdateValueStrategy() {
					@Override
					public Object convert(Object value) {
						return PreferedAlarmType.valueOf((String) value);
					}
				}, new UpdateValueStrategy() {
					@Override
					public Object convert(Object value) {
						return ((PreferedAlarmType) value).name();
					}
				});
	}

	@Override
	public void setFocus() {
		this._nameTextEntry.setFocus();
	}

}
