
package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.Messages;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class AlarmbearbeiterEditor extends AbstractEditor<AlarmbearbeiterBean> {

	private final static String EDITOR_ID = "org.csstudio.nams.configurator.editor.AlarmbearbeiterEditor"; //$NON-NLS-1$

	public static String getId() {
		return AlarmbearbeiterEditor.EDITOR_ID;
	}

	private Text _nameTextEntry;
	private Text _emailTextEntry;
	private Combo _rubrikComboEntry;
	private Text _smsTextEntry;
	private Text _voiceMailTextEntry;
	private Combo _prefAlarmingTypeComboEntry;
	private Text _statusCodeTextEntry;
	private Text _confirmCodeTextEntry;

	private Button _activeCheckBoxEntry;

	private ComboViewer _rubrikComboEntryViewer;

	private ComboViewer _prefAlarmingTypeComboEntryViewer;
	private FormToolkit formToolkit;

	private ScrolledForm mainForm;

	@Override
	public void createPartControl(final Composite parent) {
		this.formToolkit = new FormToolkit(parent.getDisplay());
		this.mainForm = this.formToolkit.createScrolledForm(parent);
		final Composite main = this.mainForm.getBody();
		main.setBackground(parent.getBackground());
		main.setLayout(new GridLayout(this.NUM_COLUMNS, false));
		this.addSeparator(main);
		this._nameTextEntry = this.createTextEntry(main, Messages.AlarmbearbeiterEditor_name, true);
		this._rubrikComboEntryViewer = this.createComboEntry(main, Messages.AlarmbearbeiterEditor_category,
				true, AbstractEditor.getConfigurationBeanService()
						.getRubrikNamesForType(RubrikTypeEnum.USER));
		this._rubrikComboEntry = this._rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);
		this._emailTextEntry = this.createTextEntry(main, Messages.AlarmbearbeiterEditor_email, true);
		this._smsTextEntry = this.createTextEntry(main, Messages.AlarmbearbeiterEditor_sms, true);
		this._voiceMailTextEntry = this.createTextEntry(main,
				Messages.AlarmbearbeiterEditor_voicemail, true);
		this._prefAlarmingTypeComboEntryViewer = this
				.createTitledComboForEnumValues(main,
						Messages.AlarmbearbeiterEditor_prefered_alarm_type, PreferedAlarmType.values(),
						this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.preferedAlarmType
								.name());
		this._prefAlarmingTypeComboEntry = this._prefAlarmingTypeComboEntryViewer
				.getCombo();
		this.addSeparator(main);
		this._statusCodeTextEntry = this.createTextEntry(main, Messages.AlarmbearbeiterEditor_status_code,
				true);
		this._confirmCodeTextEntry = this.createTextEntry(main,
				Messages.AlarmbearbeiterEditor_confirm_code, true);
		this.addSeparator(main);
		this._activeCheckBoxEntry = this.createCheckBoxEntry(main,
				Messages.AlarmbearbeiterEditor_user_is_active, true);
		this.initDataBinding();
	}

	@Override
	public void setFocus() {
		this._nameTextEntry.setFocus();
	}

	@Override
	protected void doInit(final IEditorSite site, final IEditorInput input) {
	    // Nothing to do
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected void initDataBinding() {
		final DataBindingContext context = new DataBindingContext();

		final IObservableValue nameTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.name.name());

		final IObservableValue emailTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.email.name());

		final IObservableValue smsTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.mobilePhone.name());

		final IObservableValue voicemailTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.phone.name());

		final IObservableValue statusTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.statusCode.name());

		final IObservableValue confirmTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.confirmCode.name());

		final IObservableValue activeCheckboxObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.active.name());

		final IObservableValue prefAlarmingTypeObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.PropertyNames.preferedAlarmType
								.name());

		final IObservableValue rubrikTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmbearbeiterBean.AbstractPropertyNames.rubrikName
								.name());

		// bind observables
		context.bindValue(SWTObservables.observeText(this._nameTextEntry,
				SWT.Modify), nameTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(this._emailTextEntry,
				SWT.Modify), emailTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(this._smsTextEntry,
				SWT.Modify), smsTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(this._voiceMailTextEntry,
				SWT.Modify), voicemailTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(this._statusCodeTextEntry,
				SWT.Modify), statusTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(
				this._confirmCodeTextEntry, SWT.Modify), confirmTextObservable,
				null, null);

		context.bindValue(SWTObservables
				.observeSelection(this._activeCheckBoxEntry),
				activeCheckboxObservable, null, null);

		context.bindValue(SWTObservables
				.observeSelection(this._prefAlarmingTypeComboEntry),
				prefAlarmingTypeObservable, new UpdateValueStrategy() {
					@Override
					public Object convert(final Object value) {
						return PreferedAlarmType.valueOf((String) value);
					}
				}, new UpdateValueStrategy() {
					@Override
					public Object convert(final Object value) {
						return ((PreferedAlarmType) value).name();
					}
				});

		context.bindValue(SWTObservables
				.observeSelection(this._rubrikComboEntry),
				rubrikTextObservable, null, null);
	}

}
