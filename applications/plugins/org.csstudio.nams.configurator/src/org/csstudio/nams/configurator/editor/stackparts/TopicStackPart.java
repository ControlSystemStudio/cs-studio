package org.csstudio.nams.configurator.editor.stackparts;

import java.beans.PropertyChangeListener;

import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.csstudio.nams.configurator.treeviewer.model.AlarmtopicBean;
import org.csstudio.nams.configurator.treeviewer.model.IConfigurationModel;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TopicStackPart extends AbstractStackPart<AlarmtopicBean> {

	private Text _idTextEntry;
	private Text _topicIdTextEntry;
	private Composite _main;
	private Combo _groupComboEntry;
	private Text _topicNameTextEntry;
	private Text _descriptionTextEntry;

	private boolean _isDirty = false;

	public TopicStackPart(DirtyFlagProvider flagProvider, Composite parent) {
		super(flagProvider, AlarmtopicBean.class, 2);
		_main = new Composite(parent, SWT.NONE);
		_main.setLayout(new GridLayout(NUM_COLUMNS, false));
		_idTextEntry = this.createTextEntry(_main, "ID:", false);
		_idTextEntry.setText("Topic");
		this.addSeparator(_main);
		_topicIdTextEntry = this.createTextEntry(_main, "Name:", true);
		_groupComboEntry = this.createComboEntry(_main, "Group:", true);
		this.addSeparator(_main);
		_topicNameTextEntry = this.createTextEntry(_main, "Topic name:", true);
		_descriptionTextEntry = this.createDescriptionTextEntry(_main,
				"Description:");

		// TODO only for simulation! has to be removed soon (2008-05-16: Kai
		// Meyer)
		_topicIdTextEntry.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				_isDirty = true;
				getDirtyFlagProvider().fireDirtyFlagChanged();
			}
		});
	}

	private Text createDescriptionTextEntry(Composite parent, String labeltext) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		Text textWidget = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		gridData.heightHint = 80;
		textWidget.setLayoutData(gridData);
		textWidget.setTextLimit(256);
		return textWidget;
	}

	@Override
	public Control getMainControl() {
		return _main;
	}

	@Override
	public boolean isDirty() {
		return _isDirty;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}

	@Override
	public void setPropertyChangedListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInput(IConfigurationBean input, IConfigurationModel model) {
		// TODO Auto-generated method stub

	}

}
