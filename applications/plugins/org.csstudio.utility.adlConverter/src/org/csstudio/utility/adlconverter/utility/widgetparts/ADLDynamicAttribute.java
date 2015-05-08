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

import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.DebugHelper;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 11.09.2007
 */
public class ADLDynamicAttribute extends WidgetPart{

    /**
     * The Color.
     */
    private String _clr;
    /**
     * Visibility of the Widget.
     */
    private String _vis;
    /**
     * The Channel.
     */
    private String[] _chan;
    /**
     * The Color rule.
     */
    private String _colorRule;
    /**
     * The dynamic attribute for a boolean.
     */
    private DynamicsDescriptor _adlBooleanDynamicAttribute;
    /**
     * The dynamic attribute for a color.
     */
    private DynamicsDescriptor _adlColorDynamicAttribute;
    /**
     * If the Dynamic Attribute a boolean Attribute.
     */
    private boolean _bool;
    /**
     * If the Dynamic Attribute a color Attribute.
     */
    private boolean _color;

    /**
     * The default constructor.
     *
     * @param adlDynamicAttribute An ADLWidget that correspond a ADL Dynamic Attribute.
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLDynamicAttribute(final ADLWidget adlDynamicAttribute, final AbstractWidgetModel parentWidgetModel) throws WrongADLFormatException {
        super(adlDynamicAttribute, parentWidgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void init() {
        /* Not to initialization*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void parseWidgetPart(final ADLWidget adlDynamicAttribute) throws WrongADLFormatException {

        assert adlDynamicAttribute.isType("dynamic attribute") : Messages.ADLDynamicAttribute_AssertError_Begin+adlDynamicAttribute.getType()+Messages.ADLDynamicAttribute_AssertError_End; //$NON-NLS-1$

        for (ADLWidget adlWidget : adlDynamicAttribute.getObjects()) {
            if(adlWidget.getType().equals("attr")){
                for (FileLine fileLine : adlWidget.getBody()) {
                    adlDynamicAttribute.addBody(fileLine);
                }
            }
        }

        _bool=false;
        _color=false;

        for (FileLine parameter : adlDynamicAttribute.getBody()) {
            if(parameter.getLine().trim().startsWith("//")){ //$NON-NLS-1$
                continue;
            }
            String head = parameter.getLine().split("=")[0]; //$NON-NLS-1$
            DebugHelper.add(this, parameter.getLine().substring(head.length()+1));
            String[] row=ADLHelper.cleanString(parameter.getLine().substring(head.length()+1));
            head=head.trim().toLowerCase();
            if(head.equals("clr")){ //$NON-NLS-1$
                _clr=row[0];
            }else if(head.equals("vis")){ //$NON-NLS-1$
                _vis=row[0];
            }else if(head.equals("chan")){ //$NON-NLS-1$
                _chan=row;
                if(_chan[0].contains("[")) {
                    uninit();
                }
            }else if(head.equals("chanb")){ //$NON-NLS-1$
//                LOG.debug(this, "chanB"+adlDynamicAttribute.toString());
            }else if(head.equals("colorrule")){ //$NON-NLS-1$
                _colorRule=row[0];
            }else if(head.equals("calc")){ //$NON-NLS-1$
//                LOG.debug(this, "calc"+adlDynamicAttribute.toString());
            }else {
                throw new WrongADLFormatException(Messages.ADLDynamicAttribute_WrongADLFormatException_Parameter_Begin+parameter+Messages.ADLDynamicAttribute_WrongADLFormatException_Parameter_End);
            }
        }
    }

    /**
     * Generate all Elements from ADL dynamic Attributes.
     */
    @Override
    final void generateElements() {
//        _adlDynamicAttribute= new Element[1];
        if(_chan!=null){
//            ADLHelper.setChan(_parentWidgetModel, _chan);
            ADLHelper.setChan(_widgetModel, _chan);
            String channel = "$channel$";
            if(_chan.length>2){ //$NON-NLS-1$
                // Beim Oval als Led AN/Aus wird post fix doppelet gesetzt.
                //channel = channel.concat("."+_chan[2]);
            }
            if(_vis!=null && _vis.equals("if not zero")){ //$NON-NLS-1$
                _bool=true;
                _adlBooleanDynamicAttribute = new DynamicsDescriptor("org.css.sds.color.if_zero"); //$NON-NLS-1$
                _adlBooleanDynamicAttribute.addInputChannel(new ParameterDescriptor(channel,"")); //$NON-NLS-1$
            }else if(_vis!=null && _vis.equals("if zero")){ //$NON-NLS-1$
                _bool=true;
                _adlBooleanDynamicAttribute = new DynamicsDescriptor("org.css.sds.color.if_not_zero"); //$NON-NLS-1$
                _adlBooleanDynamicAttribute.addInputChannel(new ParameterDescriptor(channel,"")); //$NON-NLS-1$
            }
            if( _colorRule!=null){
                _color = true;
            //            <dynamicsDescriptor ruleId="cosyrules.color.aend_dlog">
            //                <inputChannel name="$channel$" type="java.lang.Double" />
            //            </dynamicsDescriptor>
                _adlColorDynamicAttribute = new DynamicsDescriptor("cosyrules.color."+_colorRule.toLowerCase()); //$NON-NLS-1$
                _adlColorDynamicAttribute.addInputChannel(new ParameterDescriptor(channel,""));
//                if(_chan.length>2&&_chan[2].startsWith("$")){ //$NON-NLS-1$
//                    ADLHelper.setChan(_parentWidgetModel, _chan);
//                }
//                ADLHelper.setConnectionState(_adlColorDynamicAttribute);
            }
//            else {
//                _color = true;
//                _adlColorDynamicAttribute = new Element("dynamicsDescriptor");
//                _adlColorDynamicAttribute.setAttribute("ruleId", "org.css.sds.color.default_epics_alarm_background");
//                Element inputChannel = new Element("inputChannel");
//                inputChannel.setAttribute("name", _chan[0]);
//                inputChannel.setAttribute("type", "java.lang.Double");
//                _adlColorDynamicAttribute.addContent(inputChannel);
//            }
            if(_clr!=null){
                if(_clr.equals("alarm")){ //$NON-NLS-1$
                    _color = true;
                    _adlColorDynamicAttribute = new DynamicsDescriptor("org.css.sds.color.default_epics_alarm_background"); //$NON-NLS-1$
                    String temp = _chan[0];
                    if(temp.contains("[")) {
                        temp = String.format("%1$s[severity]", temp.substring(0,temp.indexOf('[')));
                    }
//                    if(!temp.endsWith(".SEVR")){
//                        temp= temp.concat("[severity]");
//                    }
                    _adlColorDynamicAttribute.addInputChannel(new ParameterDescriptor(temp,"")); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * @return the boolean dynamic Attributes DynamicsDescriptor.
     */
    public final DynamicsDescriptor getBooleanAdlDynamicAttributes() {
        return _adlBooleanDynamicAttribute;
    }
    /**
     * @return the boolean dynamic Attributes Elememt.
     */
    public final DynamicsDescriptor getColorAdlDynamicAttributes() {
        return _adlColorDynamicAttribute;
    }

    /**
     * @return true when the DynamicAttribute have a <b>boolean rule</b> otherwise false.
     */
    public final boolean isBoolean() {
        return _bool;
    }

    /**
     * @return true when the DynamicAttribute have a <b>color rule</b> otherwise false.
     */
    public final boolean isColor() {
        return _color;
    }
}
