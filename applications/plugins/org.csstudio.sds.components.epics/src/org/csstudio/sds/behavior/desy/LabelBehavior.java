/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id$
 */
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.model.LabelModel;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;
import org.epics.css.dal.simple.Severity;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 26.03.2010
 */
public class LabelBehavior extends AbstractDesyBehavior<LabelModel> {

    private String _normalBackgroundColor;

    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return new String[] { LabelModel.PROP_NAME, LabelModel.PROP_TEXTVALUE,
                LabelModel.PROP_BORDER_COLOR, LabelModel.PROP_BORDER_STYLE,
                LabelModel.PROP_BORDER_WIDTH, LabelModel.PROP_ACTIONDATA,
                LabelModel.PROP_PERMISSSION_ID, LabelModel.PROP_CURSOR};
    }

    @Override
    protected void doInitialize(LabelModel widget) {
        _normalBackgroundColor = widget.getPropertyInternal(LabelModel.PROP_COLOR_BACKGROUND).getPropertyValue().toString();
    }

    @Override
    protected void doProcessConnectionStateChange(LabelModel widget, ConnectionState connectionState) {
        if(connectionState!=null && connectionState == ConnectionState.CONNECTED) {
            widget.setPropertyValue(LabelModel.PROP_COLOR_BACKGROUND, _normalBackgroundColor);
        }else {
            widget.setPropertyValue(LabelModel.PROP_COLOR_BACKGROUND, determineBackgroundColor(connectionState));
        }
    }

    @Override
    protected void doProcessMetaDataChange(LabelModel widget, MetaData metaData) {
        // nothing
    }

    @Override
    protected void doProcessValueChange(LabelModel model, AnyData anyData) {
        // .. update Label value
        model.setPropertyValue(LabelModel.PROP_TEXTVALUE, anyData.stringValue());
        // .. update Label Alarm Border
        Severity severity = anyData.getSeverity();
        if (severity != null) {
            model.setPropertyValue(LabelModel.PROP_BORDER_COLOR, SeverityUtil
                    .determineColorBySeverity(severity));
            model.setPropertyValue(LabelModel.PROP_BORDER_STYLE, SeverityUtil
                    .determineBorderStyleBySeverity(severity).getIndex());
            model.setPropertyValue(LabelModel.PROP_BORDER_WIDTH, SeverityUtil
                    .determineBorderWidthBySeverity(severity));
        }
    }

}
