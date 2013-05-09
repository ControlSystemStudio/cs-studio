/**
 * 
 */
package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.PopupMenuUtil;
import org.csstudio.ui.util.widgets.PVFormulaInputBar;
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
	public AbstractGraph2DView() {
	}

	private Widget widget;

	/** Memento */
	private IMemento memento = null;

	/** Memento tag */
	private static final String MEMENTO_DATA_FORMULA = "DataFormul"; //$NON-NLS-1$

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

	protected abstract Widget createAbstractGraph2DWidget(Composite parent,
			int style);

	@Override
	public void saveState(final IMemento memento) {
		super.saveState(memento);
		// Save the currently selected variable
		if (dataFormulaInputBar.getPVFormula() != null) {
			memento.putString(MEMENTO_DATA_FORMULA, dataFormulaInputBar
					.getPVFormula());
		}
	}

	public void setDataFormula(String dataFormula) {
		dataFormulaInputBar.setPVFormula(dataFormula);
		widget.setXPvName(dataFormula);
	}

	private PVFormulaInputBar dataFormulaInputBar;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		dataFormulaInputBar = new PVFormulaInputBar(parent, SWT.NONE, (IDialogSettings) null, "dataFormula");
		dataFormulaInputBar.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));
		dataFormulaInputBar
				.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent event) {
						if ("pvFormula".equals(event.getPropertyName())) {
							widget.setDataFormula(dataFormulaInputBar.getPVFormula());
						}
					}
				});

//		PopupMenuUtil.installPopupForView(dataFormulaInputBar, getSite(),
//				dataFormulaInputBar);

		widget = createAbstractGraph2DWidget(parent, SWT.NONE);
		widget.setConfigurable(true);
		widget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		PopupMenuUtil.installPopupForView(widget, getSite(), widget);

		if (memento != null && memento.getString(MEMENTO_DATA_FORMULA) != null) {
			setDataFormula(memento.getString(MEMENTO_DATA_FORMULA));
		}
	}

	/**
	 * @return the widget
	 */
	public Widget getWidget() {
		return widget;
	}

}
