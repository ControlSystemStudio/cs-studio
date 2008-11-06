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

import java.util.ArrayList;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.widgets.ActionButton;
import org.csstudio.utility.adlconverter.utility.widgets.Arc;
import org.csstudio.utility.adlconverter.utility.widgets.Bargraph;
import org.csstudio.utility.adlconverter.utility.widgets.ChoiceButton;
import org.csstudio.utility.adlconverter.utility.widgets.Ellipse;
import org.csstudio.utility.adlconverter.utility.widgets.GroupingContainer;
import org.csstudio.utility.adlconverter.utility.widgets.Image;
import org.csstudio.utility.adlconverter.utility.widgets.Label;
import org.csstudio.utility.adlconverter.utility.widgets.Meter;
import org.csstudio.utility.adlconverter.utility.widgets.Polygon;
import org.csstudio.utility.adlconverter.utility.widgets.Polyline;
import org.csstudio.utility.adlconverter.utility.widgets.Rectangle;
import org.csstudio.utility.adlconverter.utility.widgets.RelatedDisplay;
import org.csstudio.utility.adlconverter.utility.widgets.SixteenBinaryBar;
import org.csstudio.utility.adlconverter.utility.widgets.StripChart;
import org.csstudio.utility.adlconverter.utility.widgets.Symbol;
import org.csstudio.utility.adlconverter.utility.widgets.Textinput;
import org.csstudio.utility.adlconverter.utility.widgets.Valuator;
import org.csstudio.utility.adlconverter.utility.widgets.Waveform;
import org.csstudio.utility.adlconverter.utility.widgets.Widget;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 21.09.2007
 */
public class ADLChildren {

    /**
     * ADL Grouping Container Children's.
     */
    private ArrayList<Widget> _childrens = new ArrayList<Widget>();

    /**
     * @param adlChildren 
     */
    public ADLChildren(final ADLWidget adlChildren, AbstractWidgetModel displayModel) {
//        for (ADLWidget strings : adlChildren.getObjects()) {
//            try {
//               
//                if(strings.getType().equals("arc")){ //$NON-NLS-1$
//                    _childrens.add(new Arc(strings,abstractWidgetModel));
//                }else if(strings.getType().equals("bar")){ //$NON-NLS-1$
//                    _childrens.add(new Bargraph(strings));
//                }else if(strings.getType().equals("byte")){ //$NON-NLS-1$
//                    _childrens.add(new SixteenBinaryBar(strings));
//                }else if(strings.getType().equals("composite")){ //$NON-NLS-1$
//                    _childrens.add(new GroupingContainer(strings));
//                }else if(strings.getType().equals("image")){ //$NON-NLS-1$
//                    _childrens.add(new Image(strings, abstractWidgetModel));
//                }else if(strings.getType().equals("indicator")){ //$NON-NLS-1$
//                    _childrens.add(new Bargraph(strings));
//                }else if(strings.getType().equals("menu")){ //$NON-NLS-1$
//                    _childrens.add(new RelatedDisplay(strings));
//                }else if(strings.getType().equals("\"message button\"")){ //$NON-NLS-1$
//                    _childrens.add(new ActionButton(strings));
//                }else if(strings.getType().equals("meter")){ //$NON-NLS-1$
//                    _childrens.add(new Meter(strings));
//                }else if(strings.getType().equals("oval")){ //$NON-NLS-1$
//                    _childrens.add(new Ellipse(strings, abstractWidgetModel));
//                }else if(strings.getType().equals("polygon")){ //$NON-NLS-1$
//                    _childrens.add(new Polygon(strings, abstractWidgetModel));
//                }else if(strings.getType().equals("polyline")){ //$NON-NLS-1$
//                    _childrens.add(new Polyline(strings, abstractWidgetModel));
//                }else if(strings.getType().equals("rectangle")){ //$NON-NLS-1$
//                    _childrens.add(new Rectangle(strings, abstractWidgetModel));
//                }else if(strings.getType().equals("\"related display\"")){ //$NON-NLS-1$
//                    _childrens.add(new RelatedDisplay(strings));
//                }else if(strings.getType().equals("\"strip chart\"")){ //$NON-NLS-1$
//                    _childrens.add(new Waveform(strings));
//                }else if(strings.getType().equals("text")){ //$NON-NLS-1$
//                    _childrens.add(new Label(strings));
//                }else if(strings.getType().equals("\"text update\"")){ //$NON-NLS-1$
//                    _childrens.add(new Label(strings));
//                }else if(strings.getType().equals("\"text entry\"")){ //$NON-NLS-1$
//                    _childrens.add(new Textinput(strings));
//                }else if(strings.getType().equals("valuator")){ //$NON-NLS-1$
//                    _childrens.add(new Valuator(strings));
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        
        ADLWidget storedBasicAttribute=null;
        ADLWidget storedDynamicAttribute=null;

        for (ADLWidget adlWidget : adlChildren.getObjects()) {
            try {
                if (adlWidget.getType().equals("arc")) { //$NON-NLS-1$
                    _childrens.add(new Arc(adlWidget, displayModel, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("bar")) { //$NON-NLS-1$
                    _childrens.add(new Bargraph(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if(adlWidget.getType().equals("byte")){ //$NON-NLS-1$
                    _childrens.add(new SixteenBinaryBar(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("\"cartesian plot\"")) { //$NON-NLS-1$
                    _childrens.add(new Waveform(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                }else if (adlWidget.getType().equals("\"choice button\"")) { //$NON-NLS-1$
                    _childrens.add(new ChoiceButton(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("composite")) { //$NON-NLS-1$
                    _childrens.add(new GroupingContainer(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("\"dynamic symbol\"")) { //$NON-NLS-1$
                    _childrens.add(new Symbol(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("file")) { //$NON-NLS-1$
                    // TODO: FILE --> Name and Version
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("image")) { //$NON-NLS-1$
                    _childrens.add(new Image(adlWidget, displayModel, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("indicator")) { //$NON-NLS-1$
                    _childrens.add(new Bargraph(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("menu")) { //$NON-NLS-1$
                    _childrens.add(new RelatedDisplay(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("\"message button\"")) { //$NON-NLS-1$
                    _childrens.add(new ActionButton(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("meter")) { //$NON-NLS-1$
                    _childrens.add(new Meter(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("oval")) { //$NON-NLS-1$
                    _childrens.add(new Ellipse(adlWidget, displayModel, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("polygon")) { //$NON-NLS-1$
                    _childrens.add(new Polygon(adlWidget, displayModel, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("polyline")) { //$NON-NLS-1$
                    Polyline polyline = new Polyline(adlWidget, displayModel, storedBasicAttribute, storedDynamicAttribute);
                    _childrens.add(polyline);
                    polyline = null;
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("rectangle")) { //$NON-NLS-1$
                    _childrens.add(new Rectangle(adlWidget, displayModel, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("\"related display\"")) { //$NON-NLS-1$
                    _childrens.add(new RelatedDisplay(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("\"strip chart\"")) { //$NON-NLS-1$
                    _childrens.add(new StripChart(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("text")) { //$NON-NLS-1$
                    _childrens.add(new Label(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("\"text update\"")) { //$NON-NLS-1$
                    _childrens.add(new Label(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("\"text entry\"")) { //$NON-NLS-1$
                    _childrens.add(new Textinput(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("valuator")) { //$NON-NLS-1$
                    _childrens.add(new Valuator(adlWidget, storedBasicAttribute, storedDynamicAttribute));
                    storedBasicAttribute = null;
                    storedDynamicAttribute = null;
                } else if (adlWidget.getType().equals("\"basic attribute\"")) { //$NON-NLS-1$
                    storedBasicAttribute = adlWidget;
                } else if (adlWidget.getType().equals("\"dynamic attribute\"")) { //$NON-NLS-1$
                    storedDynamicAttribute = adlWidget;
                } else {
                    CentralLogger.getInstance().warn(this,
                            Messages.ADLDisplayImporter_WARN_UNHANDLED_TYPE + adlWidget.getType()+ "(ObjectNo:"+adlWidget.getObjectNr()+" Widget: "+adlWidget);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    
    
    /**
     * @return the ADL Grouping Container Children's.
     */
    public final Widget[] getAdlChildrens() {
        return _childrens.toArray(new Widget[0]);
    }
}
