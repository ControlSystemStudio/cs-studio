/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.channel.widgets.PopupMenuUtil;
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
	    memento.putString(MEMENTO_PVNAME, processVariableInputBar.getProcessVariable()
		    .getName());
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
	parent.setLayout(new FormLayout());
	
	Label lblPvName = new Label(parent, SWT.NONE);
	FormData fd_lblPvName = new FormData();
	fd_lblPvName.top = new FormAttachment(0, 8);
	fd_lblPvName.left = new FormAttachment(0, 5);
	lblPvName.setLayoutData(fd_lblPvName);
	lblPvName.setText("PV Name:");

	processVariableInputBar = new ProcessVariableInputBar(parent, SWT.NONE, Activator
		.getDefault().getDialogSettings(), "histogram.query");
	FormData fd_combo = new FormData();
	fd_combo.top = new FormAttachment(0, 5);
	fd_combo.left = new FormAttachment(lblPvName, 6);
	fd_combo.right = new FormAttachment(100, -5);
	processVariableInputBar.setLayoutData(fd_combo);
	processVariableInputBar.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
		if ("processVariable".equals(event.getPropertyName())) {
		    line2DPlotWidget.setPvName(processVariableInputBar.getProcessVariable()
			    .getName());
		}
	    }
	});
	
	lblXPvName = new Label(parent, SWT.NONE);
	FormData fd_lblXPvName = new FormData();
	fd_lblXPvName.left = new FormAttachment(0,5);
	fd_lblXPvName.top = new FormAttachment(processVariableInputBar, 8);
	lblXPvName.setLayoutData(fd_lblXPvName);
	lblXPvName.setText("X PV(optional)");
	
	xProcessVariableInputBar = new ProcessVariableInputBar(parent, SWT.NONE, (IDialogSettings) null, "histogram.query");
	FormData fd_processVariableInputBar = new FormData();
	fd_processVariableInputBar.top = new FormAttachment(processVariableInputBar, 5);
	fd_processVariableInputBar.left = new FormAttachment(lblXPvName, 5);
	fd_processVariableInputBar.right = new FormAttachment(100, -5);
	xProcessVariableInputBar.setLayoutData(fd_processVariableInputBar);
	xProcessVariableInputBar.addPropertyChangeListener(new PropertyChangeListener() {
	    
	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
		if ("processVariable".equals(event.getPropertyName())) {
		    line2DPlotWidget.setXPvName(xProcessVariableInputBar.getProcessVariable()
			    .getName());
		}
	    }
	});

	line2DPlotWidget = new Line2DPlotWidget(parent, SWT.NONE);
	FormData fd_waterfallComposite = new FormData();
	fd_waterfallComposite.top = new FormAttachment(xProcessVariableInputBar, 5);
	fd_waterfallComposite.bottom = new FormAttachment(100, -5);
	fd_waterfallComposite.left = new FormAttachment(0, 5);
	fd_waterfallComposite.right = new FormAttachment(100, -5);
	line2DPlotWidget.setLayoutData(fd_waterfallComposite);

	if (memento != null && memento.getString(MEMENTO_PVNAME) != null) {
	    setProcessVariable(new ProcessVariable(
		    memento.getString(MEMENTO_PVNAME)));
	}

	PopupMenuUtil.installPopupForView(processVariableInputBar, getSite(), processVariableInputBar);
	PopupMenuUtil.installPopupForView(line2DPlotWidget, getSite(), line2DPlotWidget);
    }
}

