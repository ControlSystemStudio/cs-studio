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
package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.actions.CommitValueActionFactory;
import org.csstudio.sds.model.properties.actions.CommitValueWidgetAction;
import org.csstudio.sds.model.properties.actions.WidgetAction;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 28.09.2007
 */
public class MenuActionData implements IRule {
    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "org.css.sds.color.menu_action_data";

    /**
     * Standard constructor.
     */
    public MenuActionData() {
        // TODO Auto-generated constructor stub
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(Object[] arguments) {
        ActionData actionData = new ActionData();
        if (arguments[0] instanceof String) {
            String inputValue = (String) arguments[0];
            String[] values = inputValue.split(",");
            for (String value : values) {
                CommitValueWidgetAction widgetAction = (CommitValueWidgetAction) ActionType.COMMIT_VALUE.getActionFactory().createWidgetAction();
                widgetAction.getProperty(CommitValueWidgetAction.VALUE).setPropertyValue(value);
                actionData.addAction(widgetAction);
            }
        }
        return actionData;
    }

}
