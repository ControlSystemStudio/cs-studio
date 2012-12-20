package org.csstudio.common.trendplotter;


import org.csstudio.common.trendplotter.model.ArchiveDataSource;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.preferences.Preferences;
import org.csstudio.common.trendplotter.ui.Controller;
import org.csstudio.common.trendplotter.ui.Plot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;
//TODO jhatje: implement new datatype
public class OpenDataBrowserShellPopupAction {//extends ProcessVariablePopupAction {
//
//    /** {@inheritDoc} */
//    @Override
//    public void handlePVs(final IProcessVariable[] pv_names) {
//
//        //Create new shell
//        final Shell shell = new Shell();
//        shell.setText(pv_names[0].getName());
//        shell.setLocation(10, 10);
//        shell.setSize(800, 600);
//
//        final Model model = new Model();
//        for (final IProcessVariable iProcessVariable : pv_names) {
//            try {
//                add(model, iProcessVariable, null);
//            } catch (final Exception ex) {
//                MessageDialog.openError(shell,
//                                        Messages.Error,
//                                        NLS.bind(Messages.ControllerStartErrorFmt, ex.getMessage()));
//            }
//        }
//
//        // Create GUI elements (Plot)
//        final GridLayout layout = new GridLayout();
//        shell.setLayout(layout);
//
//        // Canvas that holds the graph
//        final Canvas plot_box = new Canvas(shell, 0);
//        plot_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
//
//        final Plot plot = Plot.forCanvas(plot_box);
//
//        // Create and start controller
//        final Controller controller = new Controller(shell, model, plot);
//        try {
//            controller.start();
//        } catch (final Exception ex) {
//            MessageDialog.openError(shell,
//                                    Messages.Error,
//                                    NLS.bind(Messages.ControllerStartErrorFmt, ex.getMessage()));
//        }
//
//        // open the shell
//        shell.open();
//
//    }
//
//    /** Add item
//     *  @param model Model to which to add the item
//     *  @param pv PV to add
//     *  @param archive Archive to use or <code>null</code>
//     *  @throws Exception on error
//     */
//    private void add(final Model model, final IProcessVariable pv,
//            final ArchiveDataSource archive) throws Exception
//    {
//        final double period = Preferences.getScanPeriod();
//        final PVItem item = new PVItem(pv.getName(), period);
//        if (archive == null) {
//            item.useDefaultArchiveDataSources();
//        } else {
//            item.addArchiveDataSource(archive);
//        }
//        // Add item to new axes
//        item.setAxis(model.addAxis());
//        model.addItem(item);
//    }
//
}
