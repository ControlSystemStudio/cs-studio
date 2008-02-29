package org.csstudio.diag.postanalyser;

import org.csstudio.diag.postanalyser.model.Channel;
import org.csstudio.diag.postanalyser.model.Model;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** Eclipse ViewPart for the post analyzer Model and GUI.
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
public final class View extends ViewPart
{
    final static String ID = "org.csstudio.diag.postanalyser.view"; //$NON-NLS-1$

    final private Model model = new Model();
    private GUI gui;
    
    /** Called by <code>ObjectContribPopupAction</code> with received data. */
    public static void activateWithPVs(final IProcessVariableWithSamples[] pvs)
    {
        try
        {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            final View view = (View) page.showView(View.ID);
            if (view == null)
            {
                Activator.getLogger().error("Cannot activate view"); //$NON-NLS-1$
                return;
            }
            for (IProcessVariableWithSamples pv : pvs)
                view.addPVSamples(pv);
        }
        catch (Exception ex)
        {
            Activator.getLogger().error(ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        gui = new GUI(model, parent);
        
        // TODO Accept 'dropped' PVs?
        // Does not work from Data Browser Config View;
        // never see PV-with-samples
//        new ProcessVariableWithSamplesDropTarget(gui.getMainControl())
//        {
//            @Override
//            public void handleDrop(IProcessVariableWithSamples pv,
//                    DropTargetEvent event)
//            {
//                addPVSamples(pv);
//            }
//        };
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }
    
    /** Add samples of PV to model. */
    private void addPVSamples(final IProcessVariableWithSamples pv)
    {
        // Convert the sequence of IValue into simple doubles
        final int N = pv.size();
        double time[] = new double[N];
        double value[] = new double[N];
        // This skips all samples that won't map to a number,
        // because most analyzer methods can't handle them.
        int j = 0;
        for (int i = 0; i < N; ++i)
        {
            final IValue v = pv.getSample(i);
            final double dbl = ValueUtil.getDouble(v);
            if (Double.isNaN(dbl) || Double.isInfinite(dbl))
                continue;
            value[j] = dbl;
            time[j] = v.getTime().toDouble();
            ++j;
        }
        // Add as new channel to the model
        if (j == N)
        {
            model.addChannel(new Channel(pv.getName(), time, value));
            return;
        }
        // We skipped some values, so create "shorter" arrays
        final double fixed_time[] = new double[j];
        final double fixed_value[] = new double[j];
        System.arraycopy(time,  0, fixed_time, 0, j);
        System.arraycopy(value, 0, fixed_value, 0, j);
        time = null;
        value = null;
        model.addChannel(new Channel(pv.getName(), fixed_time, fixed_value));
    }
}
