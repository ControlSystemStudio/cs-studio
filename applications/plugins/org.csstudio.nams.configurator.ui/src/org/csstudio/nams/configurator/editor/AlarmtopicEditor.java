
package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class AlarmtopicEditor extends AbstractEditor<FilterBean> {

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.AlarmtopicEditor"; //$NON-NLS-1$

	public static String getId() {
		return AlarmtopicEditor.EDITOR_ID;
	}

	private Text _topicIdTextEntry;
	private Combo _rubrikComboEntry;

	private Text _topicNameTextEntry;
	private Text _descriptionTextEntry;
	private ComboViewer _rubrikComboEntryViewer;
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
		this._topicIdTextEntry = this.createTextEntry(main, Messages.AlarmtopicEditor_name, true);
		this._rubrikComboEntryViewer = this.createComboEntry(main, Messages.AlarmtopicEditor_category,
				true, AbstractEditor.getConfigurationBeanService()
						.getRubrikNamesForType(RubrikTypeEnum.TOPIC));
		this._rubrikComboEntry = this._rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);
		this._topicNameTextEntry = this.createTextEntry(main, Messages.AlarmtopicEditor_topic_name,
				true);
		this._descriptionTextEntry = this.createDescriptionTextEntry(main,
				Messages.AlarmtopicEditor_description);
		this.initDataBinding();
	}

	@Override
	public void setFocus() {
		this._topicIdTextEntry.setFocus();
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
						AlarmtopicBean.PropertyNames.humanReadableName.name());

		final IObservableValue topicNameTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmtopicBean.PropertyNames.topicName.name());

		final IObservableValue descriptionTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmtopicBean.PropertyNames.description.name());

		final IObservableValue rubrikTextObservable = BeansObservables
				.observeValue(this.getWorkingCopyOfEditorInput(),
						AlarmtopicBean.AbstractPropertyNames.rubrikName.name());

		// bind observables
		context.bindValue(SWTObservables.observeText(this._topicIdTextEntry,
				SWT.Modify), nameTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(this._topicNameTextEntry,
				SWT.Modify), topicNameTextObservable, null, null);

		context.bindValue(SWTObservables.observeText(
				this._descriptionTextEntry, SWT.Modify),
				descriptionTextObservable, null, null);

		context.bindValue(SWTObservables
				.observeSelection(this._rubrikComboEntry),
				rubrikTextObservable, null, null);
	}
}
