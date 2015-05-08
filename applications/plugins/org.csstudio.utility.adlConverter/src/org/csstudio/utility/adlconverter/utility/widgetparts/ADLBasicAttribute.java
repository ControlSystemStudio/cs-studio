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

import org.csstudio.sds.components.model.ArcModel;
import org.csstudio.sds.components.model.RectangleModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.09.2007
 */
public class ADLBasicAttribute extends WidgetPart{

    /**
     * The default constructor.
     *
     * @param adlBasicAttribute An ADLWidget that correspond a ADL Basic Attribute.
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLBasicAttribute(final ADLWidget adlBasicAttribute, final AbstractWidgetModel parentWidgetModel) throws WrongADLFormatException {
        super(adlBasicAttribute, parentWidgetModel);
    }

    /** The Color of (front?) Object. */
    private String _clr;
    /** width of the Border. */
    private String _width;
    /** The style of border. */
    private String _style;
    /** Is type of fill.*/
    private String _fill;


    /**
     * {@inheritDoc}
     */
    @Override
    void init() {
        /* Not to initialization*/
    }

    /**
     * @param adlBasicAttribute The ADL String for an ADL Basic Attribute.
     * @throws WrongADLFormatException this exception was thrown the String not an valid ADL Basic Attribute String.
     */
    @Override
    final void parseWidgetPart(final ADLWidget adlBasicAttribute) throws WrongADLFormatException {
        assert adlBasicAttribute.isType("basic attribute") : Messages.ADLBasicAttribute_AssertError_Begin+adlBasicAttribute.getType()+Messages.ADLBasicAttribute_AssertError_End; //$NON-NLS-1$
        for (ADLWidget adlWidget : adlBasicAttribute.getObjects()) {
            if(adlWidget.getType().equals("attr")){
                for (FileLine fileLine : adlWidget.getBody()) {
                    adlBasicAttribute.addBody(fileLine);
                }
            }
        }
        for (FileLine parameter : adlBasicAttribute.getBody()) {
            if(parameter.getLine().trim().startsWith("//")){ //$NON-NLS-1$
                continue;
            }
            String[] row = parameter.getLine().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.ADLBasicAttribute_WrongADLFormatException_Begin+parameter+Messages.ADLBasicAttribute_WrongADLFormatException_End);
            }
            if(row[0].trim().toLowerCase().equals("clr")){ //$NON-NLS-1$
                _clr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("width")){ //$NON-NLS-1$
                _width=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("style")){ //$NON-NLS-1$
                _style=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("fill")){ //$NON-NLS-1$
                _fill=row[1].trim();
            }else {
                throw new WrongADLFormatException(Messages.ADLBasicAttribute_WrongADLFormatException_Parameter_Begin+parameter+Messages.ADLBasicAttribute_WrongADLFormatException_Parameter_End);
            }
        }
    }

    /**
     * Generate all Elements from ADL Basic Attributes.
     */
    @Override
    final void generateElements() {
        if(_clr!=null){
            _widgetModel.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,ADLHelper.getRGB(_clr));
        }
        boolean transperncy=false;
        if(_fill!=null){
            if(_fill.equals("\"outline\"")){ //$NON-NLS-1$
                transperncy =true;
                _fill="0.0"; //$NON-NLS-1$
            }
            if(_width==null){
                _width="1"; //$NON-NLS-1$
            }
        }else if(_fill==null){
            _fill="100.0"; //$NON-NLS-1$
        }
        _widgetModel.setPropertyValue(RectangleModel.PROP_TRANSPARENT, transperncy);
        _widgetModel.setPropertyValue(ArcModel.PROP_TRANSPARENT, transperncy);

        String style = "1"; // Line //$NON-NLS-1$
        if (_style==null){
            style = "1";  // Line //$NON-NLS-1$
        }else if (_style.equals("none")){ //$NON-NLS-1$
            style = "0";  // none //$NON-NLS-1$
        }else if (_style.equals("shape")){ //$NON-NLS-1$
            style = "6";  // none //$NON-NLS-1$
        }else if(_style.equals("\"dash\"")){ //$NON-NLS-1$
            style = "5";  // Striated //$NON-NLS-1$
//            if(_width!=null&&_width.equals("1")){ //$NON-NLS-1$
//                _width="2"; //$NON-NLS-1$
//            }
        }
        if(_width!=null){
            // <property type="sds.integer" id="border.width" value="0" />
            setWidth(_width);
            _widgetModel.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR, ADLHelper.getRGB(_clr));
        }

        setStyle(style);
    }

    /**
     * @param adlDynamicAttributes set a dynamic attribute for the Color element.
     */
    public final void setDynamicColor(final DynamicsDescriptor adlDynamicAttributes) {
        _widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,adlDynamicAttributes);
    }

    /** @return the Color */
    public final String getClr() {
        return _clr;
    }
    /** @param clr set the Color */
    public final void setClr(final String clr) {
        _clr = clr;
    }
    /** @return the width */
    public final String getWidth() {
        return _width;
    }
    /** @param width set the width */
    public final void setWidth(final String width) {
        _width = width;
        _widgetModel.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH, _width);
    }
    /** @return the style */
    public final String getStyle() {
        return _style;
    }
    /** @param style set the style */
    public final void setStyle(final String style) {
        _style = style;
//      <property type="sds.option" id="border.style">
//          <option id="3" />
//      </property>
        if(_style!=null){
            _widgetModel.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE, _style);
        }
    }

    /** @return the fill */
    public final Double getFill() {
        try{
            return Double.parseDouble(_fill);
        } catch (NumberFormatException nfe) {
            if(_fill.equals("\"solid\"")){
                return 100d;
            }else{
                return 0d;
            }
        }
    }
    /** @param fill set the fill */
    public final void setFill(final String fill) {
        _fill = fill;
    }
}
