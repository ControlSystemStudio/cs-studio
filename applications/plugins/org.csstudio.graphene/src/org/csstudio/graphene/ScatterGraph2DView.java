/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.ui.util.PopupMenuUtil;
import org.csstudio.csdata.ProcessVariable;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author shroffk
 * 
 */
public class ScatterGraph2DView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.graphene.ScatterGraph2DView";

    /** Memento */
    private IMemento memento = null;

    /** Memento tag */
    private static final String MEMENTO_YPVNAME = "YPVName"; //$NON-NLS-1$
    private static final String MEMENTO_XPVNAME = "XPVName"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public ScatterGraph2DView() {
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
	if (yProcessVariableInputBar.getProcessVariable() != null) {
	    memento.putString(MEMENTO_YPVNAME, yProcessVariableInputBar
		    .getProcessVariable().getName());
	}
	if (xProcessVariableInputBar.getProcessVariable() != null) {
	    memento.putString(MEMENTO_XPVNAME, xProcessVariableInputBar
		    .getProcessVariable().getName());
	}
    }

    public void setYProcessVariable(ProcessVariable processVariable) {
	yProcessVariableInputBar.setProcessVariable(processVariable);
	scatterGraph2DWidget.setPvName(processVariable.getName());
    }

    public void setXProcessVariable(ProcessVariable processVariable) {
	xProcessVariableInputBar.setProcessVariable(processVariable);
	scatterGraph2DWidget.setXPvName(processVariable.getName());
    }

    private ProcessVariableInputBar yProcessVariableInputBar;
    private ProcessVariableInputBar xProcessVariableInputBar;
    private ScatterGraph2DWidget scatterGraph2DWidget;
    private Label lblXPvName;

    @Override
    public void createPartControl(Composite parent) {
	parent.setLayout(new GridLayout(2, false));

	Label lblPvName = new Label(parent, SWT.NONE);
	lblPvName.setText("PV Name:");

	yProcessVariableInputBar = new ProcessVariableInputBar(parent,
		SWT.NONE, Activator.getDefault().getDialogSettings(),
		"histogram.query");
	yProcessVariableInputBar.setLayoutData(new GridData(SWT.FILL,
		SWT.CENTER, true, false, 1, 1));
	yProcessVariableInputBar
		.addPropertyChangeListener(new PropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent event) {
			if ("processVariable".equals(event.getPropertyName())) {
			    scatterGraph2DWidget
				    .setPvName(yProcessVariableInputBar
					    .getProcessVariable().getName());
			}
		    }
		});

	PopupMenuUtil.installPopupForView(yProcessVariableInputBar, getSite(),
		yProcessVariableInputBar);

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
			    scatterGraph2DWidget
				    .setXPvName(xProcessVariableInputBar
					    .getProcessVariable().getName());
			}
		    }
		});

	scatterGraph2DWidget = new ScatterGraph2DWidget(parent, SWT.NONE);
	scatterGraph2DWidget.setConfigurable(true);
	scatterGraph2DWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		true, true, 2, 1));
	PopupMenuUtil.installPopupForView(scatterGraph2DWidget, getSite(),
		scatterGraph2DWidget);

	if (memento != null && memento.getString(MEMENTO_YPVNAME) != null) {
	    setYProcessVariable(new ProcessVariable(
		    memento.getString(MEMENTO_YPVNAME)));
	}
	if (memento != null && memento.getString(MEMENTO_XPVNAME) != null) {
	    setXProcessVariable(new ProcessVariable(
		    memento.getString(MEMENTO_XPVNAME)));
	}
    }

}
