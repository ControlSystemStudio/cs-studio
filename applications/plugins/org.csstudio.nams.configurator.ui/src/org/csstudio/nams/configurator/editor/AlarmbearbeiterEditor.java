package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;


public class AlarmbearbeiterEditor extends AbstractEditor<AlarmbearbeiterBean> {

	private final static String EDITOR_ID = "org.csstudio.nams.configurator.editor.AlarmbearbeiterEditor";
	
	private Text _nameTextEntry;
	private Text _emailTextEntry;
	private Combo _groupComboEntry;
	private Text _smsTextEntry;
	private Text _voiceMailTextEntry;
	private Combo _prefAlarmingTypeComboEntry;
	private Text _statusCodeTextEntry;
	private Text _confirmCodeTextEntry;
	private Button _activeCheckBoxEntry;

	private ComboViewer _groupComboEntryViewer;

	private ComboViewer _prefAlarmingTypeComboEntryViewer;
	
	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		this.addSeparator(main);
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_groupComboEntryViewer = this.createComboEntry(main, "Group:", true, groupDummyContent);
		_groupComboEntry = _groupComboEntryViewer.getCombo();
		this.addSeparator(main);
		_emailTextEntry = this.createTextEntry(main, "Email:", true);
		_smsTextEntry = this.createTextEntry(main, "SMS number:", true);
		_voiceMailTextEntry = this.createTextEntry(main, "VoiceMail number:",
				true);
		_prefAlarmingTypeComboEntryViewer = this.createComboEntry(main,
				"Prefered alarming type:", false, array2StringArray(PreferedAlarmType.values()));
		_prefAlarmingTypeComboEntry = _prefAlarmingTypeComboEntryViewer.getCombo();
		this.addSeparator(main);
		_statusCodeTextEntry = this
				.createTextEntry(main, "Status code:", true);
		_confirmCodeTextEntry = this.createTextEntry(main, "Confirm code:",
				true);
		this.addSeparator(main);
		_activeCheckBoxEntry = this.createCheckBoxEntry(main,
				"User is active:", true);
		initDataBinding();
	}
	
	@Override
	protected void doInit(IEditorSite site, IEditorInput input) {
		
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

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

	public static String getId() {
		return EDITOR_ID;
	}
	
}
