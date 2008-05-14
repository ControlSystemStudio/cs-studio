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

import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.widgets.ActionButton;
import org.csstudio.utility.adlconverter.utility.widgets.Arc;
import org.csstudio.utility.adlconverter.utility.widgets.Bargraph;
import org.csstudio.utility.adlconverter.utility.widgets.Ellipse;
import org.csstudio.utility.adlconverter.utility.widgets.GroupingContainer;
import org.csstudio.utility.adlconverter.utility.widgets.Image;
import org.csstudio.utility.adlconverter.utility.widgets.Label;
import org.csstudio.utility.adlconverter.utility.widgets.Meter;
import org.csstudio.utility.adlconverter.utility.widgets.Polygon;
import org.csstudio.utility.adlconverter.utility.widgets.Polyline;
import org.csstudio.utility.adlconverter.utility.widgets.Rectangle;
import org.csstudio.utility.adlconverter.utility.widgets.RelatedDisplay;
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
    public ADLChildren(final ADLWidget adlChildren) {
        for (ADLWidget strings : adlChildren.getObjects()) {
            try {
                
                // Der Alte stand an erlaubten Kindern
//                if(strings.getType().equals("arc")){ //$NON-NLS-1$
//                    _childrens.add(new Arc(strings));
//                }else if(strings.getType().equals("bar")){ //$NON-NLS-1$
//                    _childrens.add(new Bargraph(strings));
//                }else if(strings.getType().equals("composite")){ //$NON-NLS-1$
//                    _childrens.add(new GroupingContainer(strings));
//                }else if(strings.getType().equals("oval")){ //$NON-NLS-1$
//                    _childrens.add(new Ellipse(strings));
//                }else if(strings.getType().equals("\"message button\"")){ //$NON-NLS-1$
//                    _childrens.add(new ActionButton(strings));
//                }else if(strings.getType().equals("polygon")){ //$NON-NLS-1$
//                    _childrens.add(new Polygon(strings));
//                }else if(strings.getType().equals("polyline")){ //$NON-NLS-1$
//                    _childrens.add(new Polyline(strings));
//                }else if(strings.getType().equals("rectangle")){ //$NON-NLS-1$
//                    _childrens.add(new Rectangle(strings));
//                }else if(strings.getType().equals("\"related display\"")){ //$NON-NLS-1$
//                    _childrens.add(new RelatedDisplay(strings));
//                }else if(strings.getType().equals("\"strip chart\"")){ //$NON-NLS-1$
//                    _childrens.add(new Waveform(strings));
//                }else if(strings.getType().equals("text")){ //$NON-NLS-1$
//                    _childrens.add(new Label(strings));
//                }else if(strings.getType().equals("\"text update\"")){ //$NON-NLS-1$
//                    _childrens.add(new Textinput(strings));
//                }
//                
                //_-----------------
                if(strings.getType().equals("arc")){ //$NON-NLS-1$
                    _childrens.add(new Arc(strings));
                }else if(strings.getType().equals("bar")){ //$NON-NLS-1$
                    _childrens.add(new Bargraph(strings));
                }else if(strings.getType().equals("composite")){ //$NON-NLS-1$
                    _childrens.add(new GroupingContainer(strings));
                }else if(strings.getType().equals("image")){ //$NON-NLS-1$
                    _childrens.add(new Image(strings));
                }else if(strings.getType().equals("indicator")){ //$NON-NLS-1$
                    _childrens.add(new Bargraph(strings));
                }else if(strings.getType().equals("menu")){ //$NON-NLS-1$
                    _childrens.add(new RelatedDisplay(strings));
                }else if(strings.getType().equals("\"message button\"")){ //$NON-NLS-1$
                    _childrens.add(new ActionButton(strings));
                }else if(strings.getType().equals("meter")){ //$NON-NLS-1$
                    _childrens.add(new Meter(strings));
                }else if(strings.getType().equals("oval")){ //$NON-NLS-1$
                    _childrens.add(new Ellipse(strings));
                }else if(strings.getType().equals("polygon")){ //$NON-NLS-1$
                    _childrens.add(new Polygon(strings));
                }else if(strings.getType().equals("polyline")){ //$NON-NLS-1$
                    _childrens.add(new Polyline(strings));
                }else if(strings.getType().equals("rectangle")){ //$NON-NLS-1$
                    _childrens.add(new Rectangle(strings));
                }else if(strings.getType().equals("\"related display\"")){ //$NON-NLS-1$
                    _childrens.add(new RelatedDisplay(strings));
                }else if(strings.getType().equals("\"strip chart\"")){ //$NON-NLS-1$
                    _childrens.add(new Waveform(strings));
                }else if(strings.getType().equals("text")){ //$NON-NLS-1$
                    _childrens.add(new Label(strings));
                }else if(strings.getType().equals("\"text update\"")){ //$NON-NLS-1$
                    _childrens.add(new Label(strings));
                }else if(strings.getType().equals("\"text entry\"")){ //$NON-NLS-1$
                    _childrens.add(new Textinput(strings));
                }else if(strings.getType().equals("valuator")){ //$NON-NLS-1$
                    _childrens.add(new Valuator(strings));
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
