package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class AxisMinMaxEditAction extends Action
{

    protected AxisConfig axis_config;

    public AxisMinMaxEditAction(final AxisConfig axis_config)
    {
        super(Messages.AxisEditMinMax, Action.AS_PUSH_BUTTON);
        this.axis_config = axis_config;
    }

    @Override
    public void run()
    {
        InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),"Set Axis Range","Min:Max",this.axis_config.getMin()+":"+this.axis_config.getMax(),
                new IInputValidator() {
                    @Override
                    public String isValid(String input) {
                        if (input.matches("(-)?[0-9]+(.[0-9]+)?\\:(-)?[0-9]+(.[0-9]+)?"))
                            return null;
                        else
                            return "Invalid Min:Max specification";
                    }
        });
        if (dlg.open() == Window.OK) {
            String rangeString = dlg.getValue();
            String[] rangeStringComps = rangeString.split(":");
            this.axis_config.setAutoScale(false);
            this.axis_config.setRange(Double.parseDouble(rangeStringComps[0]), Double.parseDouble(rangeStringComps[1]));
        }
    }
}
