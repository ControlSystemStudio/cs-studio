package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.ui.Activator;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Action that adds a property to a {@link IPropertyContainer}.
 * 
 * @author Sven Wende
 */
public class OpenRecordMetaDataAction extends Action {
	private RecordForm recordForm;
	private IRecord record;
	private String fieldKey;
	
	
	/**
	 * Constructor.
	 * 
	 * @param form
	 *            a component that provides access to a property container
	 */
	public OpenRecordMetaDataAction(RecordForm form) {
		assert form != null;
		this.recordForm = form;
		
		setText("Metadata");
		setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/parameter_add.png"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Metadata", "... TODO ... for ... " + recordForm.getSelectedField());
	}
}
