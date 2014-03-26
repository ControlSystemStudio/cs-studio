package org.csstudio.sds.ui.internal.pvlistview;

import java.util.HashMap;

import org.csstudio.domain.common.types.Tuple;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback.ValidationResult;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationService;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ValidatePvsAction extends Action {

	private final IProcessVariableAddressValidationService validationService;
	private final HashMap<IProcessVariableAddress, Tuple<ValidationResult,String>> serviceValidations;
	private final PvListView pvListView;

	public ValidatePvsAction(
			IProcessVariableAddressValidationService validationService,
			HashMap<IProcessVariableAddress, Tuple<ValidationResult,String>> serviceValidations,
			PvListView pvListView) {
				this.validationService = validationService;
				this.serviceValidations = serviceValidations;
				this.pvListView = pvListView;
	}
	
	@Override
	public String getText() {
		return validationService.getServiceName();
	}
	
	@Override
	public String getToolTipText() {
		return validationService.getServiceDescription();
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
				"icons/validationButtonImage.png");
	}
	
	@Override
	public void run() {
		pvListView.handleValidationAction(validationService, serviceValidations);
	}

}
