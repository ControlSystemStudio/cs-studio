package org.csstudio.nams.configurator.editor.stackparts;

import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
@Deprecated
public class TopicStackPart extends AbstractStackPart<AlarmtopicBean> {

	private Text _idTextEntry;
	private Text _topicIdTextEntry;
	private Combo _groupComboEntry;
	private Text _topicNameTextEntry;
	private Text _descriptionTextEntry;

	public TopicStackPart(DirtyFlagProvider flagProvider, Composite parent) {
		super(flagProvider, AlarmtopicBean.class, 2);
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		_idTextEntry = this.createTextEntry(main, "ID:", false);
		_idTextEntry.setText("Topic");
		this.addSeparator(main);
		_topicIdTextEntry = this.createTextEntry(main, "Name:", true);
		_groupComboEntry = this.createComboEntry(main, "Group:", true);
		this.addSeparator(main);
		_topicNameTextEntry = this.createTextEntry(main, "Topic name:", true);
		_descriptionTextEntry = this.createDescriptionTextEntry(main,
				"Description:");

		// TODO only for simulation! has to be removed soon (2008-05-16: Kai
		// Meyer)
		_topicIdTextEntry.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getDirtyFlagProvider().fireDirtyFlagChanged();
			}
		});
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
		_topicNameTextEntry.setFocus();
	}
}
