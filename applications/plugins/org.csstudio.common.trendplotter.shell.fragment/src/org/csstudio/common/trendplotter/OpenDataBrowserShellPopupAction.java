package org.csstudio.common.trendplotter;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.ArchiveRescale;
import org.csstudio.common.trendplotter.model.AxisConfig;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.ui.Controller;
import org.csstudio.common.trendplotter.ui.Plot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;

public class OpenDataBrowserShellPopupAction extends ProcessVariablePopupAction {


	@Override
	public void handlePVs(IProcessVariable[] pv_names) {
		Shell shell = new Shell();
		shell.setText(pv_names[0].getName());
		shell.setLocation(10, 10);
		shell.setSize(800, 600);
        Model model = new Model();
        //Create a default model because there is no workspace file.
        createModel(model, pv_names);
        
        // Create GUI elements (Plot)
        GridLayout layout = new GridLayout();
		shell.setLayout(layout);
        
        // Canvas that holds the graph
        final Canvas plot_box = new Canvas(shell, 0);
        plot_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        Plot plot = Plot.forCanvas(plot_box);
        
        // Create and start controller
        Controller controller = new Controller(shell, model, plot);
        try
        {
            controller.start();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.ControllerStartErrorFmt, ex.getMessage()));
        }

		// open the shell
		shell.open();

	}

	private void createModel(Model model, IProcessVariable[] pv_names) {

//        scroll_enabled = DOMHelper.getSubelementBoolean(root_node, TAG_SCROLL, scroll_enabled);
//        update_period = DOMHelper.getSubelementDouble(root_node, TAG_PERIOD, update_period);
        
//        final String start = DOMHelper.getSubelementString(root_node, TAG_START);
//        final String end = DOMHelper.getSubelementString(root_node, TAG_END);
//        if (start.length() > 0  &&  end.length() > 0)
//        {
//            final StartEndTimeParser times = new StartEndTimeParser(start, end);
//            setTimerange(TimestampFactory.fromCalendar(times.getStart()),
//                         TimestampFactory.fromCalendar(times.getEnd()));
//        }
        
//        RGB color = loadColorFromDocument(root_node, TAG_BACKGROUND);
//        if (color != null)
//            background = color;
//        
		model.setArchiveRescale(ArchiveRescale.AUTOZOOM);

//        // Load Axes
//        Element list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_AXES);
//        if (list != null)
//        {
//            // Load PV items
//            Element item = DOMHelper.findFirstElementNode(
//                    list.getFirstChild(), TAG_AXIS);
//            while (item != null)
//            {
//                addAxis(AxisConfig.fromDocument(item));
//                item = DOMHelper.findNextElementNode(item, TAG_AXIS);
//            }
//        }

		model.addAxis(new AxisConfig(true, pv_names[0].getName(), new RGB(0, 0, 0), 0.0, 10.0, true, false));
		PVItem item;
		try {
			item = new PVItem(pv_names[0].getName(), 3);
			item.setLiveCapacity(300);
			item.useDefaultArchiveDataSources();
                // Adding item creates the axis for it if not already there
                model.addItem(item);
                // Backwards compatibility with previous data browser which
                // stored axis configuration with each item: Update axis from that.
                final AxisConfig axis = item.getAxis();
                    axis.setAutoScale(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//            // Load Formulas
//            item = DOMHelper.findFirstElementNode(
//                    list.getFirstChild(), TAG_FORMULA);
//            while (item != null)
//            {
//                addItem(FormulaItem.fromDocument(this, item));
//                item = DOMHelper.findNextElementNode(item, TAG_FORMULA);
//            }
        }
    
		
	

}
