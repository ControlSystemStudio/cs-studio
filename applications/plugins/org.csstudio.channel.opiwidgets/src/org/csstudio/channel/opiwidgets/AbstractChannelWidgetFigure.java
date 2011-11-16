package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.ui.util.AdapterUtil;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Base class for all figures that are based on SWTWidgets and channel finder.
 * 
 * @author carcassi
 *
 * @param <T> the widget type
 */
public abstract class AbstractChannelWidgetFigure<T extends Composite> extends AbstractSWTWidgetFigure {
	
	/**
	 * Creates a new figure based on the give swt widget and the selection provider.
	 * 
	 * @param composite pass through
	 * @param parentModel pass through
	 * @param swtWidget the SWT widget
	 * @param selectionProvider a corresponding selection provider
	 */
	public AbstractChannelWidgetFigure(Composite composite, AbstractContainerModel parentModel) {
		super(composite, parentModel);
	}
	
	protected T widget;
	protected ISelectionProvider selectionProvider;
	protected Control control;

	@Override
	public T getSWTWidget() {
		return widget;
	}
	
	public ISelectionProvider getSelectionProvider() {
		return selectionProvider;
	}
	
	public Control getControlForPopup() {
		return control;
	}
	
	public Channel[] getSelectedChannels() {
		if (selectionProvider == null)
			return null;
		return AdapterUtil.convert(getSelectionProvider().getSelection(), Channel.class);
	}
	
	public ChannelQuery[] getSelectedChannelQuery() {
		if (selectionProvider == null)
			return null;
		return AdapterUtil.convert(getSelectionProvider().getSelection(), ChannelQuery.class);
	}
	
	public boolean isRunMode() {
		return runmode;
	}
	
	@Override
	public void dispose() {
		if(runmode) {
			super.dispose();
			getSWTWidget().dispose();
		}
	}
}
