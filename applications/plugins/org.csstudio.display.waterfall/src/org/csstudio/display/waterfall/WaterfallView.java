package org.csstudio.display.waterfall;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.csstudio.utility.pvmanager.widgets.WaterfallWidget;

/**
 * View that allows to create a waterfall plot out of a given PV.
 */
public class WaterfallView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.display.waterfall.WaterfallView";

	/** Memento */
	private IMemento memento = null;
	
	/** Memento tag */
	private static final String MEMENTO_PVNAME = "PVName"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public WaterfallView() {
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
		if (waterfallComposite.getWaveformPVName() != null) {
			memento.putString(MEMENTO_PVNAME, waterfallComposite.getWaveformPVName());
		}
	}
	
	public void setPVName(String name) {
		combo.setText(name);
		resolveAndSetPVName(name);
	}
	
	private Combo combo;
	private WaterfallWidget waterfallComposite;
	
	private void resolveAndSetPVName(String text) {
		List<String> pv = ChannelResolver.resolveTag(text);
		if (pv != null && !pv.isEmpty()) {
			waterfallComposite.setScalarPVNames(pv);
		} else {
			waterfallComposite.setWaveformPVName(text);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		
		Label lblPvName = new Label(parent, SWT.NONE);
		FormData fd_lblPvName = new FormData();
		fd_lblPvName.top = new FormAttachment(0, 13);
		fd_lblPvName.left = new FormAttachment(0, 10);
		lblPvName.setLayoutData(fd_lblPvName);
		lblPvName.setText("PV Name:");
		
		ComboViewer comboViewer = new ComboViewer(parent, SWT.NONE);
		combo = comboViewer.getCombo();
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 10);
		fd_combo.left = new FormAttachment(lblPvName, 6);
		fd_combo.right = new FormAttachment(100, -10);
		combo.setLayoutData(fd_combo);
		
		waterfallComposite = new WaterfallWidget(parent, SWT.NONE);
		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.bottom = new FormAttachment(100, -10);
		fd_waterfallComposite.left = new FormAttachment(0, 10);
		fd_waterfallComposite.top = new FormAttachment(combo, 6);
		fd_waterfallComposite.right = new FormAttachment(combo, 0, SWT.RIGHT);
		waterfallComposite.setLayoutData(fd_waterfallComposite);
		
		ComboHistoryHelper name_helper =
			new ComboHistoryHelper(Activator.getDefault()
				.getDialogSettings(), "WaterfallPVs", combo, 20, true) {
			@Override
			public void newSelection(final String pv_name) {
				resolveAndSetPVName(pv_name);
			}
		};
		name_helper.loadSettings();
		
		if (memento != null && memento.getString(MEMENTO_PVNAME) != null) {
			setPVName(memento.getString(MEMENTO_PVNAME));
		}
	}

}