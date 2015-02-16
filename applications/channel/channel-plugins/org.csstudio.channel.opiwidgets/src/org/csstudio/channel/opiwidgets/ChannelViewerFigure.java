/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.ChannelViewerWidget;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public class ChannelViewerFigure extends AbstractChannelWidgetFigure<ChannelViewerWidget> {

	public ChannelViewerFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}

	@Override
	protected ChannelViewerWidget createSWTWidget(Composite parent, int style) {
		return new ChannelViewerWidget(parent, SWT.None);
	}

}
