package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Action that adds a new PV to the model.
 *  <p>
 *  The actual model can change when this action
 *  is used within a config. view with changing models,
 *  including model==null, which disables the action.
 *  @author Kay Kasemir
 */
abstract class AbstractAddModelItemAction extends Action
{
    /** Current model or <code>null</code> */
    protected Model model;
    
    /** Basic name check */
    class PVNameValidator implements IInputValidator
    {
        public String isValid(String name)
        {
            if (name == null  ||  name.length() < 1)
                return (Messages.EnterPVName);
            // Is there a way to check the names validity?
            return null;
        }
    }
	
    /** Constructor */
	public AbstractAddModelItemAction(final Model model)
	{
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
        setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD_DISABLED));
		setModel(model);
	}

	/** Set the model on which this action acts.
	 *  @param model New model or <code>null</code>
	 */
	public void setModel(final Model model)
	{
	    this.model = model;
	    setEnabled(model != null);
	}
	
	@Override
    public void run()
    {
        final InputDialog dlg = new InputDialog(null,
                Messages.AddPV_TT,
                Messages.EnterNewPVName,
                null, new PVNameValidator());
        if (dlg.open() == InputDialog.OK)
            addPV(dlg.getValue().trim());
    }

    abstract protected void addPV(String pv_name);
}
