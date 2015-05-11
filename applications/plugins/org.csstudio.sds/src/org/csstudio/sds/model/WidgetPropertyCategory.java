/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.model;

import org.csstudio.sds.internal.localization.Messages;

/**
 * Categories of widget properties. Order of the enum entries is important and will be respected in the tabbed property view.
 *
 * @author Sven Wende
 *
 * @version $Revision: 1.9 $
 *
 */
public enum WidgetPropertyCategory {
    CONNECTION(Messages.getString("PropertyCategory_Connection")),
    ACTIONS(Messages.getString("PropertyCategory_Actions")),
    IMAGE(Messages.getString("PropertyCategory_Image")),
    BEHAVIOR(Messages.getString("PropertyCategory_Behaviour")),
    DISPLAY(Messages.getString("PropertyCategory_Display")),
    FORMAT(Messages.getString("PropertyCategory_Format")),
    SCALE(Messages.getString("PropertyCategory_Scale")),
    MISC(Messages.getString("PropertyCategory_Misc")),
    POSITION(Messages.getString("PropertyCategory_Position")),
    BORDER(Messages.getString("PropertyCategory_Border"));

    private String description;

    private WidgetPropertyCategory(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
