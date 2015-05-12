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
 * $Id: MenuButtonBehavior.java,v 1.2 2010/03/30 21:55:54 swende Exp $
 */
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.MenuButtonModel;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.CursorStyleEnum;
import org.csstudio.dal.simple.MetaData;

/**
 * @author hrickens
 * @author $Author: swende $
 * @version $Revision: 1.2 $
 * @since 26.03.2010
 */
public class MenuButtonBehavior extends AbstractDesyConnectionBehavior<MenuButtonModel> {

    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return new String[] { AbstractWidgetModel.PROP_NAME, MenuButtonModel.PROP_CURSOR,
                MenuButtonModel.PROP_BORDER_COLOR, MenuButtonModel.PROP_BORDER_STYLE,
                MenuButtonModel.PROP_BORDER_WIDTH };
    }

    @Override
    protected void doInitialize(MenuButtonModel widget) {
        super.doInitialize(widget);
        widget.setCursorId(CursorStyleEnum.HAND.name());
    }

    @Override
    protected void doProcessMetaDataChange(MenuButtonModel widget, MetaData metaData) {
        if (metaData != null) {
            switch (metaData.getAccessType()) {
                case NONE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(7));
                    break;
                case READ:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(7));
                    break;
                case READ_WRITE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
                    break;
                case WRITE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
                    break;
                default:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
            }
        }
    }

//    @Override
//    protected void doProcessValueChange(MenuButtonModel model, AnyData anyData) {
//
//    }

}
