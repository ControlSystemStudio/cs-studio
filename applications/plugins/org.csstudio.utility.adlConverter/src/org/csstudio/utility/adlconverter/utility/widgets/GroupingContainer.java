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
package org.csstudio.utility.adlconverter.utility.widgets;

import java.util.ArrayList;

import org.csstudio.sds.model.GroupingContainerModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLChildren;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLMenuItem;
import org.eclipse.core.runtime.IPath;

/**
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 21.09.2007
 */
public class GroupingContainer extends Widget {

    /**
     * The Object that contain the ADLChildren.
     */
    private ADLChildren _children;

    /**
     * @param groupingContainer ADLWidget that describe the groupingContainer.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     * @param targetPath
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public GroupingContainer(final ADLWidget groupingContainer, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute, IPath targetPath) throws WrongADLFormatException {
        super(groupingContainer, storedBasicAttribute, storedDynamicAttribute);
        getObject().setHeight(getObject().getHeight()+5);
        getObject().setWidth(getObject().getWidth()+5);
        handleObject(groupingContainer.getObjects(), targetPath);
        handleBody(groupingContainer.getBody());
//        <property type="sds.boolean" id="transparency" value="true" />
        _widget.setPropertyValue(GroupingContainerModel.PROP_TRANSPARENT, true);
    }


    /**
     * @param objects the Object to handle
     * @param targetPath
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    private void handleObject(final ArrayList<ADLWidget> objects, IPath targetPath) throws WrongADLFormatException {
        for (ADLWidget obj : objects) {
            if(obj.isType("children")){ //$NON-NLS-1$
                _children = new ADLChildren(obj,_widget, targetPath);
                for (Widget elem : _children.getAdlChildrens()) {
                    elem.convertCoordinate(getObject().getX(), getObject().getY());
                    ((GroupingContainerModel) _widget).addWidget(elem.getElement());
                }
            }else if(obj.isType("menuItem")){ //$NON-NLS-1$
                new ADLMenuItem(obj, _widget);
            }
        }
    }

    /**
     * @param arrayList the body elements to handle
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    private void handleBody(final ArrayList<FileLine> arrayList) throws WrongADLFormatException {
        for (FileLine fileLine : arrayList) {
            String obj = fileLine.getLine();
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.GroupingContainer_WrongADLFormatException+row[0]); //$NON-NLS-1$
            }
            if(row[0].equals("vis")){ //$NON-NLS-1$
                //TODO: GroupingContainer-->vis
            }else if(row[0].equals("\"composite name\"")){ //$NON-NLS-1$
                //TODO: GroupingContainer-->composite name
            }else if(row[0].equals("chan")){ //$NON-NLS-1$
                //TODO: GroupingContainer-->chan
            }else{
                throw new WrongADLFormatException(Messages.GroupingContainer_WrongADLFormatException+row[0]); //$NON-NLS-1$
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(GroupingContainerModel.ID);
    }
}
