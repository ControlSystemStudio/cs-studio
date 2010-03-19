package org.csstudio.sds.components.ui.internal.actions;

import org.csstudio.sds.components.model.SimpleSliderModel;
import org.csstudio.sds.components.ui.internal.editparts.SimpleSliderEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SimpleSliderIncrementAction extends Action implements
		IObjectActionDelegate {

	private SimpleSliderModel _widgetModel;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		if (_widgetModel != null) {
			String title = "Change increment";
			String message = "Enter the new increment for the selected SimpleSlider";
			String initialValue = String.valueOf(_widgetModel.getIncrement());

			InputDialog dialog = new InputDialog(null, title, message,
					initialValue, new IInputValidator() {

						public String isValid(String newText) {
							try {
								double d = Double.parseDouble(newText);
								if (d <= 0)
									return "Value has to be greater than 0";

							} catch (NumberFormatException x) {
								return "Only numbers are allowed";
							}

							return null;
						}
					});
			int returnCode = dialog.open();

			if (returnCode == Window.OK) {
				Double increment = Double.valueOf(dialog.getValue());
				_widgetModel.setPropertyValue(SimpleSliderModel.PROP_INCREMENT,
						increment);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof SimpleSliderEditPart) {
				_widgetModel = (SimpleSliderModel) ((SimpleSliderEditPart) element).getWidgetModel();
			}
		}
	}

}
