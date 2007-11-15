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
package org.csstudio.utility.adlconverter.utility.widgetparts;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.actions.OpenDisplayWidgetAction;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.eclipse.core.runtime.Path;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.09.2007
 */
public class RelatedDisplayItem extends WidgetPart{

    /**
     * The Button Label Text.
     */
    private String _label;
    /**
     * The display to open.
     */
    private String _name;
    /**
     * the record for the new Display.
     */
    private String[] _args;
    /**
     * The root path for Widget.
     */
    private String _path; 

    /**
     * The default constructor.
     * 
     * @param display An ADLWidget that correspond a ADL Related Display Item. 
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public RelatedDisplayItem(final ADLWidget display, final AbstractWidgetModel parentWidgetModel) throws WrongADLFormatException {
        super(display, parentWidgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void init() {
        _path = "/SDS";//"C:/Helge_Projekte/CSS 3.3 Workspace/SDS/"; //$NON-NLS-1$
    }
    /**
     * {@inheritDoc}
     */
    @Override
    final void parseWidgetPart(final ADLWidget display) throws WrongADLFormatException {
//      assert !display.isType("display[n]") : "This "+display.getType()+" is not a ADL displayItem";
      
      for (String parameter : display.getBody()) {
          if(parameter.trim().startsWith("//")){ //$NON-NLS-1$
              continue;
          }
          String head = parameter.split("=")[0]; //$NON-NLS-1$
          String tmp = parameter.substring(head.length()+1);
          String[] row=ADLHelper.cleanString(tmp);
          head = head.trim().toLowerCase();
          if(head.equals("label")){ //$NON-NLS-1$
              _label=row[0];
          }else if(head.equals("name")){ //$NON-NLS-1$
              _name=row[0];
          }else if(head.equals("args")){ //$NON-NLS-1$
              _args=row;
          }else if(head.equals("policy")){ //$NON-NLS-1$
              //TODO: RelatedDisplay --> policy="replace display"
          }else {
              throw new WrongADLFormatException(Messages.RelatedDisplayItem_WrongADLFormatException_Begin+head+Messages.RelatedDisplayItem_WrongADLFormatException_Middle+parameter);
          }
      }
    }

    /**
     * Generate all Elements from Related Display Item.
     */
    final void generateElements() {
        ActionData actionData = _widgetModel.getActionData();
        if(actionData==null){
            actionData = new ActionData();
        }
        OpenDisplayWidgetAction action = (OpenDisplayWidgetAction) ActionType.OPEN_SHELL
        .getActionFactory().createWidgetAction();
        
        if(_label!=null){
            //Set the Resource
            action.getProperty(OpenDisplayWidgetAction.PROP_DESCRIPTION)
            .setPropertyValue(_label);
        }
        if(_name!=null){
            action.getProperty(OpenDisplayWidgetAction.PROP_RESOURCE)
            .setPropertyValue(new Path(_path+_name));
        }
        if(_args!=null){
            Map<String, String> map = new HashMap<String, String>();
            map.put("param", _args[0]); //$NON-NLS-1$
            action.getProperty(OpenDisplayWidgetAction.PROP_ALIASES)
            .setPropertyValue(map);
        }
        actionData.addAction(action);
        _widgetModel.setPropertyValue(AbstractWidgetModel.PROP_ACTIONDATA, actionData);
    }
    
    /**
     * 
     * @return the Label of the Related Display Item. 
     */
    public final String getLabel() {
        return _label;
    }
}
