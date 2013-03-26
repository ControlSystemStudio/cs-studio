/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.PopupMenuUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelectionProvider;
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
public abstract class AbstractGraph2DView<Widget extends AbstractGraph2DWidget & ISelectionProvider>
	extends ViewPart {

    private Widget widget;

    /** Memento */
    private IMemento memento = null;

    /** Memento tag */
    private static final String MEMENTO_YPVNAME = "YPVName"; //$NON-NLS-1$
    private static final String MEMENTO_XPVNAME = "XPVName"; //$NON-NLS-1$

    @Override
    public void init(final IViewSite site, final IMemento memento)
	    throws PartInitException {
	super.init(site, memento);
	// Save the memento
	this.memento = memento;
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
    }
    
    protected abstract Widget createAbstractGraph2DWidget(Composite parent, int style);
    
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
	widget.setPvName(processVariable.getName());
    }

    public void setXProcessVariable(ProcessVariable processVariable) {
	xProcessVariableInputBar.setProcessVariable(processVariable);
	widget.setXPvName(processVariable.getName());
    }

    private ProcessVariableInputBar yProcessVariableInputBar;
    private ProcessVariableInputBar xProcessVariableInputBar;

    @Override
    public void createPartControl(Composite parent) {
	parent.setLayout(new GridLayout(2, false));

	Label lblYPvName = new Label(parent, SWT.NONE);
	lblYPvName.setText("Y PV:");

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
			    widget.setPvName(yProcessVariableInputBar
				    .getProcessVariable().getName());
			}
		    }
		});

	PopupMenuUtil.installPopupForView(yProcessVariableInputBar, getSite(),
		yProcessVariableInputBar);

	Label lblXPvName = new Label(parent, SWT.NONE);
	lblXPvName.setText("X PV:");

	xProcessVariableInputBar = new ProcessVariableInputBar(parent,
		SWT.NONE, (IDialogSettings) null, "histogram.query");
	xProcessVariableInputBar.setLayoutData(new GridData(SWT.FILL,
		SWT.CENTER, true, false, 1, 1));
	xProcessVariableInputBar
		.addPropertyChangeListener(new PropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent event) {
			if ("processVariable".equals(event.getPropertyName())) {
			    widget.setXPvName(xProcessVariableInputBar
				    .getProcessVariable().getName());
			}
		    }
		});

	PopupMenuUtil.installPopupForView(xProcessVariableInputBar, getSite(),
		xProcessVariableInputBar);

	widget = createAbstractGraph2DWidget(parent, SWT.NONE);
	widget.setConfigurable(true);
	widget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

	PopupMenuUtil.installPopupForView(widget, getSite(), widget);

	if (memento != null && memento.getString(MEMENTO_YPVNAME) != null) {
	    setYProcessVariable(new ProcessVariable(
		    memento.getString(MEMENTO_YPVNAME)));
	}
	if (memento != null && memento.getString(MEMENTO_XPVNAME) != null) {
	    setXProcessVariable(new ProcessVariable(
		    memento.getString(MEMENTO_XPVNAME)));
	}
    }

    /**
     * @return the widget
     */
    public Widget getWidget() {
        return widget;
    }    

}
