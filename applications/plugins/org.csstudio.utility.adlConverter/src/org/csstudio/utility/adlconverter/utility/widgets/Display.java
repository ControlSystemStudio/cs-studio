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

import org.csstudio.sds.model.DisplayModel;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLObject;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 11.09.2007
 */
public class Display extends Widget{

    private ADLObject _object;
    private String _clr;
    private String _bclr;
    private String _cmap;

    /**
     * @param display
     * @param root 
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public Display(final ADLWidget display, final DisplayModel root) throws WrongADLFormatException {
        super(display, root);
        for (ADLWidget widget : display.getObjects()) {
            if(widget.isType("object")){
                    _object = new ADLObject(widget,_widget);
                    _object.setHeight(_object.getHeight()+20);
            }else {
                throw new WrongADLFormatException("This "+widget.getType()+" is a wrong ADL Display parameter");
            } 
        }
        for (String parameter : display.getBody()) {
            String[] row = parameter.split("=");
            if(row.length!=2){
                throw new WrongADLFormatException("This "+parameter+" is a wrong Display Attribute");
            }
            if(row[0].trim().toLowerCase().equals("clr")){
                _clr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("bclr")){
                _bclr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("cmap")){
                _cmap=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("type")){
                System.out.println(display.toString());
                // TODO: Display --> type
            }else {
                throw new WrongADLFormatException("This "+parameter+" is a wrong ADL Display parameter");
            }

        }
        makeElemnet();

    }

    /**
     * 
     */
    private void makeElemnet() {
        setDefaults();
        //TODO: Rausgenommen! Muss noch was eingefügt werden?
//        for (Element ele : _object.getAdlObject()) {
//            if(ele!=null){
//                _display.add(ele);
//            }
//        }
        if(_clr!=null){
            _widget.setForegroundColor(ADLHelper.getRGB(_clr));
        }
        if(_bclr!=null){
            _widget.setBackgroundColor(ADLHelper.getRGB(_bclr));
        }
        if(_cmap!=null){
            //TODO: Muss das übernommen werden ???
//            temp = new Element("property");
//            temp.setAttribute("type", "sds.option");
//            temp.setAttribute("id", "border.style");
//            temp.setAttribute("value", _fill);
//            _display.add(temp);
        }
    }
    /**
     * 
     */
    private void setDefaults() {
        String[] id= new String[]{DisplayModel.PROP_PRIMARY_PV,
                                  DisplayModel.PROP_VISIBILITY,
                                  DisplayModel.PROP_PERMISSSION_ID,
                                  DisplayModel.PROP_LAYER,
                                  DisplayModel.PROP_ENABLED,
                                  DisplayModel.PROP_NAME};
        String[] value= new String[]{"","true","","","true","PolygonModel"};
        assert (id.length==value.length) :"Anzahl der Parameter stimmen nicht überein";
        for (int i = 0; i < value.length; i++) {
            _widget.setPropertyValue(id[i], value[i]);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel("org.csstudio.sds.components.ActionButton");    }

}
