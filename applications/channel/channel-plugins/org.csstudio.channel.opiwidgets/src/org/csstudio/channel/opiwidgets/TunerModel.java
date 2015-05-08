/**
 *
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.TunerWidget;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;


/**
 * @author shroffk
 *
 */
public class TunerModel extends AbstractChannelWidgetModel {

    public TunerModel() {
        super(AbstractSelectionWidgetModelDescription.newModelFrom(TunerWidget.class));
    }

    public final String ID = "org.csstudio.channel.opiwidgets.AbstractChannelWidget"; //$NON-NLS-1$
    @Override
    protected void configureProperties() {
    }

    @Override
    public String getTypeID() {
        return ID;
    }

}
