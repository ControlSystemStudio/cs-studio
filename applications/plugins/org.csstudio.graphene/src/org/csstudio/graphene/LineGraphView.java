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

/**
 * View that allows to create a waterfall plot out of a given PV.
 */
public class LineGraphView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.graphene.LineGraphView";

	/** Memento */
	private IMemento memento = null;
	
	/** Memento tag */
	private static final String MEMENTO_PVNAME = "PVName"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public LineGraphView() {
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
		if (inputBar.getProcessVariable() != null) {
			memento.putString(MEMENTO_PVNAME, inputBar.getProcessVariable().getName());
		}
	}
	
	public void setProcessVariable(ProcessVariable processVariable) {
		inputBar.setProcessVariable(processVariable);
		lineGraphWidget.setProcessVariable(processVariable);
	}
	
	private ProcessVariableInputBar inputBar;
	private LineGraphWidget lineGraphWidget;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		
		Label lblPvName = new Label(parent, SWT.NONE);
		FormData fd_lblPvName = new FormData();
		fd_lblPvName.top = new FormAttachment(0, 8);
		fd_lblPvName.left = new FormAttachment(0, 5);
		lblPvName.setLayoutData(fd_lblPvName);
		lblPvName.setText("PV Name:");
		
		inputBar = new ProcessVariableInputBar(parent, SWT.NONE, 
				Activator.getDefault().getDialogSettings(), "histogram.query");
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 5);
		fd_combo.left = new FormAttachment(lblPvName, 6);
		fd_combo.right = new FormAttachment(100, -5);
		inputBar.setLayoutData(fd_combo);
		inputBar.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("processVariable".equals(event.getPropertyName())) {
					lineGraphWidget.setProcessVariable(inputBar.getProcessVariable());
				}
			}
		});
		
		lineGraphWidget = new LineGraphWidget(parent, SWT.NONE);
		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.bottom = new FormAttachment(100, -5);
		fd_waterfallComposite.left = new FormAttachment(0, 5);
		fd_waterfallComposite.top = new FormAttachment(inputBar, 6);
		fd_waterfallComposite.right = new FormAttachment(inputBar, 0, SWT.RIGHT);
		lineGraphWidget.setLayoutData(fd_waterfallComposite);
		
		if (memento != null && memento.getString(MEMENTO_PVNAME) != null) {
			setProcessVariable(new ProcessVariable(memento.getString(MEMENTO_PVNAME)));
		}
		
		PopupMenuUtil.installPopupForView(inputBar, getSite(), inputBar);
	}

}