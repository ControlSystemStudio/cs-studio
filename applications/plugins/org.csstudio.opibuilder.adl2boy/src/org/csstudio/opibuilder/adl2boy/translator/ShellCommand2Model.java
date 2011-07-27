package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.ExecuteCommandAction;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.CommandItem;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;
import org.csstudio.utility.adlparser.fileParser.widgets.ShellCommand;
import org.eclipse.swt.graphics.RGB;

public class ShellCommand2Model extends AbstractADL2Model {

	public ShellCommand2Model(ADLWidget adlWidget, RGB[] colorMap,
			AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		ShellCommand commandWidget = new ShellCommand(adlWidget);
		if (commandWidget != null) {
			setADLObjectProps(commandWidget, widgetModel);
		}
		setWidgetColors(commandWidget);
		CommandItem[] cmds = commandWidget.getCommandItems();
		if (cmds.length > 0) {
			ActionsInput ai = widgetModel.getActionsInput();
			for (int ii = 0; ii < cmds.length; ii++) {
				if (!(cmds[ii].getCommandName().replaceAll("\"", "").equals(""))) {
					ExecuteCommandAction exeAction = createExecuteCommandAction(cmds[ii]);
					ai.addAction(exeAction);
				}
			}
		}

	}

	private ExecuteCommandAction createExecuteCommandAction(
			CommandItem commandItem) {
		ExecuteCommandAction exeAction = new ExecuteCommandAction();
		exeAction.setPropertyValue(ExecuteCommandAction.PROP_DESCRIPTION,
				commandItem.getLabel());

		return exeAction;
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new ActionButtonModel();
		parentModel.addChild(widgetModel, true);

	}

}
