package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;

public class FilterEditor extends AbstractEditor<FilterBean> {

	private Text _nameTextEntry;
	private Combo _groupComboEntry;
	private Text _defaultMessageTextEntry;
	private Composite filterSpecificComposite;

	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterEditor";

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		this.addSeparator(main);
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_groupComboEntry = this.createComboEntry(main, "Group:", true);
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
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
				this.beanClone, FilterBean.PropertyNames.name.name());

		IObservableValue descriptionTextObservable = BeansObservables.observeValue(
				this.beanClone, FilterBean.PropertyNames.defaultMessage
						.name());

		// bind observables
		context.bindValue(SWTObservables
				.observeText(_nameTextEntry, SWT.Modify), nameTextObservable,
				null, null);

		context.bindValue(
				SWTObservables.observeText(_defaultMessageTextEntry, SWT.Modify),
				descriptionTextObservable, null, null);
	}

	@Override
	public void setFocus() {
		_nameTextEntry.setFocus();
	}
	
	public static String getId(){
		return EDITOR_ID;
	}
}
