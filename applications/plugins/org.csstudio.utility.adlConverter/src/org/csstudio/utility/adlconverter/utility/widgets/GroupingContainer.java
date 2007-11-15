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
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLChildren;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLMenuItem;

/**
 * 
 * TODO: The GroupingContainer must the Action Data hands on his children. 
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
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public GroupingContainer(final ADLWidget groupingContainer) throws WrongADLFormatException {
        super(groupingContainer);
        getObject().setHeight(getObject().getHeight()+5);
        getObject().setWidth(getObject().getWidth()+5);
        handleObject(groupingContainer.getObjects());
        handleBody(groupingContainer.getBody());
//        <property type="sds.boolean" id="transparency" value="true" />
        _widget.setPropertyValue(GroupingContainerModel.PROP_TRANSPARENT, true);
    }


    /**
     * @param objects the Object to handle
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    private void handleObject(final ArrayList<ADLWidget> objects) throws WrongADLFormatException {
        for (ADLWidget obj : objects) {
            if(obj.isType("children")){
                _children = new ADLChildren(obj);
                for (Widget elem : _children.getAdlChildrens()) {
                    elem.convertCoordinate(getObject().getX(), getObject().getY());
                    ((GroupingContainerModel) _widget).addWidget(elem.getElement());
                }
            }else if(obj.isType("menuItem")){
                new ADLMenuItem(obj, _widget);
            }
        }
    }
    
    /**
     * @param body the body elements to handle
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    private void handleBody(final ArrayList<String> body) throws WrongADLFormatException {
        for (String obj : body) {
            String[] row = obj.trim().split("=");
            if(row.length!=2){
                throw new WrongADLFormatException("wrong parameter");
            }
            if(row[0].equals("vis")){
                //TODO: GroupingContainer-->vis
            }else if(row[0].equals("\"composite name\"")){
                //TODO: GroupingContainer-->composite name 
            }else if(row[0].equals("chan")){
                //TODO: GroupingContainer-->chan 
            }else{                
                throw new WrongADLFormatException("wrong parameter: "+row[0]);
            } 
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel("org.csstudio.sds.components.GroupingContainer");
    }
}
