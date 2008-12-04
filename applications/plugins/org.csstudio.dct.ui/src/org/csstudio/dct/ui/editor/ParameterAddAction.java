package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.Activator;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;

public class ParameterAddAction extends Action {
	private PrototypeForm editingComponent;

	public ParameterAddAction(PrototypeForm editingComponent) {
		assert editingComponent != null;
		this.editingComponent = editingComponent;

		setText("Add Parameter");
		setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/parameter_add.png"));
	}

	@Override
	public void run() {
		IPrototype prototype = editingComponent.getInput();
		if (prototype != null) {
			prototype.addParameter(new Parameter("newParameter", ""));
			editingComponent.refreshParameters();
		}
	}
}
