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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 11.09.2007
 */
public class ADLDisplay extends Widget{

    private static final Logger LOG = LoggerFactory.getLogger(ADLDisplay.class);

    /**
     * The Color of (front?) Object.
     */
    private String _clr;

    /**
     * The background color of the Display.
     */
    private String _bclr;

    /**
     * The string from ADL File with the Colormap.
     */
    private String _cmap;

    /**
     *
     * @param display ADLWidget that describe the Display.
     * @param root Root Model for the Display.
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLDisplay(final ADLWidget display, final DisplayModel root) throws WrongADLFormatException {
        super(display, root);
        for (ADLWidget widget : display.getObjects()) {
            if(widget.isType("object")){ //$NON-NLS-1$
                    ADLObject object = new ADLObject(widget,_widget);
                    object.setHeight(object.getHeight());
            }else {
                throw new WrongADLFormatException(Messages.Display_WrongADLFormatException_Parameter_Begin+widget.getType()+Messages.Display_WrongADLFormatException_Parameter_End);
            }
        }
        for (FileLine fileLine : display.getBody()) {
            String parameter = fileLine.getLine();
            String[] row = parameter.split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Display_WrongADLFormatException_Begin+fileLine+Messages.Display_WrongADLFormatException_End);
            }
            if(row[0].trim().toLowerCase().equals("clr")){ //$NON-NLS-1$
                _clr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("bclr")){ //$NON-NLS-1$
                _bclr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("cmap")){ //$NON-NLS-1$
                _cmap=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("type")){ //$NON-NLS-1$
                LOG.debug("type: {}", display.toString());
                // TODO: Display --> type
            }else if(row[0].trim().toLowerCase().equals("gridspacing")){ //$NON-NLS-1$
                LOG.debug("gridspacing {}", display.toString());
                // TODO: SDS don't support yet. Display --> gridSpacing
            }else if(row[0].trim().toLowerCase().equals("gridon")){ //$NON-NLS-1$
                LOG.debug("gridon {}", display.toString());
                // TODO: SDS don't support yet. Display --> gridOn
            }else if(row[0].trim().toLowerCase().equals("snaptogrid")){ //$NON-NLS-1$
                _widget.setPropertyValue(DisplayModel.PROP_GRID_ON, row[1].equals("1"));
                LOG.debug("snaptogrid {}",display.toString());
            }else {//Unknown Property
                LOG.info("Unknown Property: {}",fileLine, new WrongADLFormatException(fileLine+Messages.Display_WrongADLFormatException_Parameter_End));
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
            _widget.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,ADLHelper.getRGB(_clr));
        }
        if(_bclr!=null){
            _widget.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, ADLHelper.getRGB(_bclr));
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
        String[] id= new String[]{AbstractWidgetModel.PROP_PRIMARY_PV,
                                  AbstractWidgetModel.PROP_VISIBILITY,
                                  AbstractWidgetModel.PROP_PERMISSSION_ID,
                                  AbstractWidgetModel.PROP_LAYER,
                                  AbstractWidgetModel.PROP_ENABLED,
                                  AbstractWidgetModel.PROP_NAME};
        String[] value= new String[]{"","true","","","true","PolygonModel"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assert (id.length==value.length) :Messages.Display_AssertError;
        for (int i = 0; i < value.length; i++) {
            _widget.setPropertyValue(id[i], value[i]);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
//        _widget = createWidgetModel(DisplayModel.ID);
    }

}
