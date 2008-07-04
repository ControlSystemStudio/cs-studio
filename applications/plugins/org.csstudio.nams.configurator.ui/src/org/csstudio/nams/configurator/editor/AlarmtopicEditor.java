package org.csstudio.nams.configurator.editor;

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

public class AlarmtopicEditor extends AbstractEditor<FilterBean> {

	private Text _idTextEntry;
	private Text _topicIdTextEntry;
	private Combo _groupComboEntry;
	private Text _topicNameTextEntry;
	private Text _descriptionTextEntry;
	
	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.AlarmtopicEditor";
	private ComboViewer _groupComboEntryViewer;

	public static String getId() {
		return EDITOR_ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		_idTextEntry = this.createTextEntry(main, "ID:", false);
		_idTextEntry.setText("Topic");
		this.addSeparator(main);
		_topicIdTextEntry = this.createTextEntry(main, "Name:", true);
		_groupComboEntryViewer = this.createComboEntry(main, "Group:", true, groupDummyContent);
		_groupComboEntry = _groupComboEntryViewer.getCombo();
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
		//TODO add group binding
		DataBindingContext context = new DataBindingContext();

		IObservableValue nameTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmtopicBean.PropertyNames.humanReadableName.name());

		IObservableValue topicNameTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmtopicBean.PropertyNames.topicName.name());

		IObservableValue descriptionTextObservable = BeansObservables.observeValue(
				this.beanClone, AlarmtopicBean.PropertyNames.description
						.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_topicIdTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(SWTObservables.observeText(_topicNameTextEntry,
				SWT.Modify), topicNameTextObservable, null, null);

		context.bindValue(
				SWTObservables.observeText(_descriptionTextEntry, SWT.Modify),
				descriptionTextObservable, null, null);
	}

	@Override
	public void setFocus() {
		_idTextEntry.setFocus();
	}

}
