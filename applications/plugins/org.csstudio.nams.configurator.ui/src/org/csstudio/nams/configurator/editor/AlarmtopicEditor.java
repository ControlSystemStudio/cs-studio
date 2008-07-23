package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
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

	private Text _topicIdTextEntry;
	private Combo _rubrikComboEntry;
	private Text _topicNameTextEntry;
	private Text _descriptionTextEntry;
	
	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.AlarmtopicEditor";
	private ComboViewer _rubrikComboEntryViewer;
	private FormToolkit formToolkit;
	private ScrolledForm mainForm;

	public static String getId() {
		return EDITOR_ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		formToolkit = new FormToolkit(parent.getDisplay());
		mainForm = formToolkit.createScrolledForm(parent);
		Composite main = mainForm.getBody();
		main.setBackground(parent.getBackground());
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		this.addSeparator(main);
		_topicIdTextEntry = this.createTextEntry(main, "Name:", true);
		_rubrikComboEntryViewer = this.createRubrikCombo(main, "Rubrik:", true, getConfigurationBeanService()
				.getRubrikNamesForType(RubrikTypeEnum.TOPIC));
		_rubrikComboEntry = _rubrikComboEntryViewer.getCombo();
		this.addSeparator(main);
		_topicNameTextEntry = this.createTextEntry(main, "Topic name:", true);
		_descriptionTextEntry = this.createDescriptionTextEntry(main,
				"Description:");	
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
				this.getWorkingCopyOfEditorInput(), AlarmtopicBean.PropertyNames.humanReadableName.name());

		IObservableValue topicNameTextObservable = BeansObservables.observeValue(
				this.getWorkingCopyOfEditorInput(), AlarmtopicBean.PropertyNames.topicName.name());

		IObservableValue descriptionTextObservable = BeansObservables.observeValue(
				this.getWorkingCopyOfEditorInput(), AlarmtopicBean.PropertyNames.description
						.name());
		
		IObservableValue rubrikTextObservable = BeansObservables.observeValue(
				this.getWorkingCopyOfEditorInput(), AlarmtopicBean.AbstractPropertyNames.rubrikName.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_topicIdTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(_topicNameTextEntry,
				SWT.Modify), topicNameTextObservable, null, null);

		context.bindValue(
				SWTObservables.observeText(_descriptionTextEntry, SWT.Modify),
				descriptionTextObservable, null, null);
		
		context.bindValue(SWTObservables
				.observeSelection(_rubrikComboEntry),
				rubrikTextObservable, null, null);
	}

	@Override
	public void setFocus() {
		_topicNameTextEntry.setFocus();
	}

}
