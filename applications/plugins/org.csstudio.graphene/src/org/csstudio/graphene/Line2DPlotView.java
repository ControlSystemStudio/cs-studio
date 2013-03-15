/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.ui.util.PopupMenuUtil;
import org.csstudio.csdata.ProcessVariable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * @author shroffk
 * 
 */
public class Line2DPlotView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.graphene.Line2DPlotView";

    /** Memento */
    private IMemento memento = null;

    /** Memento tag */
    private static final String MEMENTO_PVNAME = "PVName"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public Line2DPlotView() {
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
    }

    @Override
    public void init(final IViewSite site, final IMemento memento)
	    throws PartInitException {
	super.init(site, memento);
	// Save the memento
	this.memento = memento;
    }

    @Override
    public void saveState(final IMemento memento) {
	super.saveState(memento);
	// Save the currently selected variable
	if (processVariableInputBar.getProcessVariable() != null) {
	    memento.putString(MEMENTO_PVNAME, processVariableInputBar
		    .getProcessVariable().getName());
	}
    }

    public void setProcessVariable(ProcessVariable processVariable) {
	processVariableInputBar.setProcessVariable(processVariable);
	line2DPlotWidget.setPvName(processVariable.getName());
    }

    private ProcessVariableInputBar processVariableInputBar;
    private Line2DPlotWidget line2DPlotWidget;
    private Label lblXPvName;
    private ProcessVariableInputBar xProcessVariableInputBar;

    @Override
    public void createPartControl(Composite parent) {
	parent.setLayout(new GridLayout(2, false));

	Label lblPvName = new Label(parent, SWT.NONE);
	lblPvName.setText("PV Name:");

	processVariableInputBar = new ProcessVariableInputBar(parent, SWT.NONE,
		Activator.getDefault().getDialogSettings(), "histogram.query");
	processVariableInputBar.setLayoutData(new GridData(SWT.FILL,
		SWT.CENTER, true, false, 1, 1));
	processVariableInputBar
		.addPropertyChangeListener(new PropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent event) {
			if ("processVariable".equals(event.getPropertyName())) {
			    line2DPlotWidget.setPvName(processVariableInputBar
				    .getProcessVariable().getName());
			}
		    }
		});

	PopupMenuUtil.installPopupForView(processVariableInputBar, getSite(),
		processVariableInputBar);

	lblXPvName = new Label(parent, SWT.NONE);
	lblXPvName.setText("X PV(optional):");

	xProcessVariableInputBar = new ProcessVariableInputBar(parent,
		SWT.NONE, (IDialogSettings) null, "histogram.query");
	xProcessVariableInputBar.setLayoutData(new GridData(SWT.FILL,
		SWT.CENTER, true, false, 1, 1));
	xProcessVariableInputBar
		.addPropertyChangeListener(new PropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent event) {
			if ("processVariable".equals(event.getPropertyName())) {
			    line2DPlotWidget
				    .setXPvName(xProcessVariableInputBar
					    .getProcessVariable().getName());
			}
		    }
		});

	line2DPlotWidget = new Line2DPlotWidget(parent, SWT.NONE);
	line2DPlotWidget.setConfigurable(true);
	line2DPlotWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		true, 2, 1));
	PopupMenuUtil.installPopupForView(line2DPlotWidget, getSite(),
		line2DPlotWidget);

	if (memento != null && memento.getString(MEMENTO_PVNAME) != null) {
	    setProcessVariable(new ProcessVariable(
		    memento.getString(MEMENTO_PVNAME)));
	}
    }
}
