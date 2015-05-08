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

import org.csstudio.sds.components.model.MenuButtonModel;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgetparts.RelatedDisplayItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.09.2007
 */
public class RelatedDisplay extends Widget {

    private static final Logger LOG = LoggerFactory.getLogger(RelatedDisplay.class);

    /**
     * If MenuButton used as Menu, control contain the dynamics Descriptor for the actionData
     * with the Rule=rule.actionData.
     */
    private DynamicsDescriptor _control;

    /**
     * @param relatedDisplay ADLWidget that describe the RelatedDisplay.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public RelatedDisplay(final ADLWidget relatedDisplay, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) throws WrongADLFormatException {
        super(relatedDisplay, storedBasicAttribute, storedDynamicAttribute);
        String clr=null;
        String bclr=null;
        String label = null;
        int n =0;
        for (ADLWidget obj : relatedDisplay.getObjects()) {
            if(obj.isType("display["+n+"]")){ //$NON-NLS-1$ //$NON-NLS-2$
                if(n==0){
                    label=new RelatedDisplayItem(obj, _widget).getLabel();
                }else{
                    new RelatedDisplayItem(obj, _widget);
                }
                n++;
            }else if(obj.isType("sensitive")){ //$NON-NLS-1$
                //TODO: RelatedDisplay(Menu)-->sensitive
            }
        }
        if(getControl()!=null){

//          <property type="sds.action" id="actionData">
//          <actionData />
//          <dynamicsDescriptor ruleId="rule.actionData">
//            <inputChannel name="$channel$[enumDescriptions], enum" type="java.lang.Object" />
//            <outputChannel name="local://out" type="java.lang.Object" />
//          </dynamicsDescriptor>
//          </property>
            _control = new DynamicsDescriptor("rule.actionData"); //$NON-NLS-1$
            _control.addInputChannel(new ParameterDescriptor("$channel$[enumDescriptions], enum", "")); //$NON-NLS-1$
//            _control.setOutputChannel(new ParameterDescriptor("local://out", "")); //$NON-NLS-1$
            _control.setOutputChannel(new ParameterDescriptor("$channel$, string", "")); //$NON-NLS-1$

            // Der ConnectionState wird an die Acrtion Data gesetzt das macht nun überhaupt kein sinn!
//            HashMap<ConnectionState, Object> values = new HashMap<ConnectionState, Object>();
//
//            CommitValueWidgetAction action = (CommitValueWidgetAction) ActionType.COMMIT_VALUE
//            .getActionFactory().createWidgetAction();
//
//            action.getProperty(CommitValueWidgetAction.PROP_VALUE)
//            .setPropertyValue(ColorConstants.white.getRGB());
//            ActionData actionData = new ActionData();
//            actionData .addAction(action);
//
//
//            values.put(ConnectionState.INITIAL, actionData);//ColorConstants.white.getRGB());
//            _control.setConnectionStateDependentPropertyValues(values);
        }

        if(n<2) {
            label=". . .";
        }
        if(getControl()!=null||n>1){ // if Label text Dyn?
            // <dynamicsDescriptor ruleId="directConnection">
            //   <inputChannel name="local://out" type="java.lang.Object" />
            // </dynamicsDescriptor>
            DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
            dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$, string","")); //$NON-NLS-1$
            _widget.setDynamicsDescriptor(MenuButtonModel.PROP_LABEL, dynamicsDescriptor);
            _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_ACTIONDATA, _control);
        }


        for (FileLine fileLine : relatedDisplay.getBody()) {
            String obj = fileLine.getLine();
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.RelatedDisplay_WrongADLFormatException_Parameter_Begin+fileLine);
            }

            if(row[0].equals("clr")){ //$NON-NLS-1$
                clr = row[1];
            }else if(row[0].equals("bclr")){ //$NON-NLS-1$
                bclr = row[1];
            }else if(row[0].equals("label")){ //$NON-NLS-1$
                label=row[1].replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
            }else if(row[0].equals("visual")){ //$NON-NLS-1$
//              TODO: RelatedDisplay-->visual
//                LOG.info(this, "Unsupported Property: "+fileLine);
            }else if(row[0].equals("clrmod")){ //$NON-NLS-1$
//              TODO: RelatedDisplay-->clrmod
                LOG.info("Unsupported Property: {}",fileLine);
            }else if(row[0].equals("selfkill")){ //$NON-NLS-1$
//              TODO: RelatedDisplay-->selfkill
                LOG.info("MEDM TODO: {}",fileLine);
            }else if(row[0].equals("menu_title")){ //$NON-NLS-1$
//              TODO: RelatedDisplay-->menu_title
                LOG.info("MEDM TODO: {}",fileLine);
            }else if(row[0].equals("decorate")){ //$NON-NLS-1$
//              TODO: RelatedDisplay-->decorate
                LOG.info("MEDM TODO: {}",fileLine);
            }else{
                throw new WrongADLFormatException(Messages.RelatedDisplay_WrongADLFormatException_Parameter_Begin+fileLine);
            }
        }
        if(clr!=null){
            _widget.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,ADLHelper.getRGB(clr));
        }
        if(bclr!=null){
            _widget.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, ADLHelper.getRGB(bclr));
        }
        _widget.setPropertyValue(MenuButtonModel.PROP_LABEL, label);

        // <property type="sds.integer" id="border.width" value="1" />
        _widget.setPropertyValue(MenuButtonModel.PROP_BORDER_WIDTH, 1);
        // <property type="sds.option" id="border.style">
        //   <option id="2" />
        // </property>
        _widget.setPropertyValue(MenuButtonModel.PROP_BORDER_STYLE, 3);
        int h = ADLHelper.getFontSize("Times New Roman",label, getObject().getHeight(), getObject().getWidth(),"0"); //$NON-NLS-1$ //$NON-NLS-2$
        _widget.setFont(MenuButtonModel.PROP_FONT, new FontData("Times New Roman",h,SWT.NONE)); //$NON-NLS-1$
        _widget.setLayer(Messages.ADLDisplayImporter_ADLActionLayerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(MenuButtonModel.ID);
    }

}
