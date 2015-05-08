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

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ActionData;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.properties.actions.CommitValueActionModel;
import org.csstudio.sds.model.properties.actions.CommitValueActionModelFactory;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.11.2008
 */
public class ChoiceButton extends Widget {

    /**
     * @param choiceButton The ADLWidget that describe the ActionButton.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ChoiceButton(final ADLWidget choiceButton, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) throws WrongADLFormatException {
        super(choiceButton, storedBasicAttribute, storedDynamicAttribute);
        _widget.setPropertyValue(ActionButtonModel.PROP_TOGGLE_BUTTON, true);
        ActionData actionData = new ActionData();
        CommitValueActionModelFactory fac = new CommitValueActionModelFactory();
        CommitValueActionModel action = (CommitValueActionModel) fac.createWidgetActionModel();
        action.getProperty(CommitValueActionModel.PROP_VALUE).setPropertyValue(0);
        action.getProperty(CommitValueActionModel.PROP_DESCRIPTION).setPropertyValue("0");
        actionData.addAction(action);
        action = (CommitValueActionModel) fac.createWidgetActionModel();
        action.getProperty(CommitValueActionModel.PROP_VALUE).setPropertyValue(1);
        action.getProperty(CommitValueActionModel.PROP_DESCRIPTION).setPropertyValue("1");
        actionData.addAction(action);
        _widget.setPropertyValue(ActionButtonModel.PROP_ACTION_PRESSED_INDEX, 0);
        _widget.setPropertyValue(ActionButtonModel.PROP_ACTION_RELEASED_INDEX, 1);
        _widget.setPropertyValue(AbstractWidgetModel.PROP_ACTIONDATA, actionData);
        ParameterDescriptor parameterDescriptor = new ParameterDescriptor("$channel$");
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
        dynamicsDescriptor.setOutputChannel(parameterDescriptor);
        _widget.setDynamicsDescriptor(ActionButtonModel.PROP_ACTIONDATA, dynamicsDescriptor);
        _widget.setLayer(Messages.ADLDisplayImporter_ADLActionLayerName); //$NON-NLS-1$
        dynamicsDescriptor = new DynamicsDescriptor();
        dynamicsDescriptor.addInputChannel(parameterDescriptor);
        _widget.setDynamicsDescriptor(ActionButtonModel.PROP_LABEL, dynamicsDescriptor);


        for (FileLine fileLine : choiceButton.getBody()) {
            String obj = fileLine.getLine();
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.ActionButton_WrongADLFormatException+fileLine);
            }
//            if(row[0].equals("type")){ //$NON-NLS-1$
//                if(row[1].contains("toggle")){ //$NON-NLS-1$
//                    _widget.setPropertyValue(ActionButtonModel.PROP_TOGGLE_BUTTON, true);
//                }else{
//                    _widget.setPropertyValue(ActionButtonModel.PROP_TOGGLE_BUTTON, false);
//                }
//            }else if(row[0].equals("label")){ //$NON-NLS-1$
//                // <property type="sds.string" id="label" value="AButton" />
//                _widget.setPropertyValue(ActionButtonModel.PROP_LABEL, row[1].replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
//            }else if(row[0].equals("press_msg")){ //$NON-NLS-1$
//                // <property type="sds.double" id="click_value" value="0.0" />
//                _widget.setPropertyValue("click_value", row[1].replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                CommitValueActionModelFactory fac = new CommitValueActionModelFactory();
//                CommitValueActionModel action = (CommitValueActionModel) fac.createWidgetAction();
//                action.setEnabled(true);
//                action.getProperty(CommitValueActionModel.PROP_VALUE).setPropertyValue(row[1].replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
//                result.addAction(action);
//                pressIndex = actionIndex++;
//            }else if(row[0].equals("release_msg")){ //$NON-NLS-1$
//                _widget.setPropertyValue("click_value", row[1].replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                CommitValueActionModelFactory fac = new CommitValueActionModelFactory();
//                CommitValueActionModel action = (CommitValueActionModel) fac.createWidgetAction();
//                action.getProperty(CommitValueActionModel.PROP_VALUE).setPropertyValue(row[1].replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
//                result.addAction(action);
//                releasIndex = actionIndex++;
//            }else if(row[0].equals("clrmod")){ //$NON-NLS-1$
//                // TODO: ActionButton-->clrmod
//            }else if(row[0].equals("pressed_label")){ //$NON-NLS-1$
//                // TODO: ActionButton-->pressed_label (Not Supported from SDS)
            if(row[0].equals("stacking")){ //$NON-NLS-1$
                // not needed.
            }else if(row[0].equals("clrmod")){ //$NON-NLS-1$
//              // TODO: ActionButton-->clrmod
            }else{

                throw new WrongADLFormatException(Messages.ActionButton_WrongADLFormatException+fileLine);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
       _widget = createWidgetModel(ActionButtonModel.ID);
    }


}
