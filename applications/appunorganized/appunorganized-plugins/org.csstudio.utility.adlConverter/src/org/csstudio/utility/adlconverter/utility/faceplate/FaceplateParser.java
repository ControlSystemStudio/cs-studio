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
package org.csstudio.utility.adlconverter.utility.faceplate;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.LinkingContainerModel;
import org.csstudio.utility.adlconverter.Activator;
import org.csstudio.utility.adlconverter.ui.preferences.ADLConverterPreferenceConstants;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 06.05.2009
 */
public class FaceplateParser {

    private static final Logger LOG = LoggerFactory.getLogger(FaceplateParser.class);

    private static final int WIDTH = 151;
    private static final int HIGHT = 455;
    private static final int X_OFFSET = WIDTH + 5;
    private static final int Y_OFFSET = HIGHT;
    // private static final int WIDTH = 75;
    // private static final int HIGHT = 145;
    // private static final int X_OFFSET = WIDTH + 5;
    // private static final int Y_OFFSET = HIGHT;

    private static Map<Point, LinkingContainerModel> _facePlateMap = new HashMap<Point, LinkingContainerModel>(20);
    private static int _maxX;
    private static int _maxY;

    private FaceplateParser() {
        // Private Constructor
    }

    public static void parse(ADLWidget root, DisplayModel displayModel) {
        int groupX = 0;
        _maxX = 0;
        _maxY = 0;
        _facePlateMap.clear();
        LinkingContainerModel facePlate = null;
        LabelModel header = null;
        for (FileLine fileLine : root.getBody()) {
            String line = fileLine.getLine().trim();
            if(line == null || line.length() < 1 || line.startsWith(";")) {
                continue;
            }
            String[] lineParts = line.split("[=,]");
            if(lineParts.length < 2) {
                continue;
            }
            String attribute = lineParts[0].trim();
            if(attribute.equals("faceplateAdl")) {
                // e.g. faceplateAdl=/applic/graphic/common/FP_xctl.adl
                String source = Activator
                        .getDefault()
                        .getPreferenceStore()
                        .getString(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part);
                String target = Activator.getDefault().getPreferenceStore()
                        .getString(ADLConverterPreferenceConstants.P_STRING_Path_Target);
                String path = lineParts[1].replace(source + "/", target + "/").replace(".adl",
                                                                                       ".css-sds");
                if(facePlate!=null) {
                    facePlate.setPropertyValue(LinkingContainerModel.PROP_RESOURCE, new Path(path));
                }

            } else if(attribute.equals("faceplateHeight")) {
                // e.g. faceplateHeight=800

                // Use the default Value
                // Größe der einzelnen Elemente 125,350
                if(facePlate == null) {
                    LOG.error("Wrong FacePlate Format!");
                    continue;
                }
                // int w = Integer.parseInt(lineParts[1]);
                int w = 350;
                facePlate.setWidth(w);
            } else if(attribute.equals("faceplateMacro")) {
                // e.g.
                // faceplateMacro=PSC_chan=TTF:MAG:STEE:V9UND3_ps,MAX_chan=V9UND3:Imax_calc,MIN_chan=V9UND3:Imin_calc
                if(facePlate!=null && lineParts.length > 2) {
                    for (int i = 1; i < lineParts.length; i += 2) {
                        facePlate.addAlias(lineParts[i], lineParts[i + 1]);
                    }
                }
            } else if(attribute.equals("faceplatePosition")) {
                // e.g. faceplatePosition=7,1
                if(facePlate != null) {
                    try {
                        displayModel.addWidget(facePlate);
                    } catch (RuntimeException rte) {
                        LOG.warn("Can not added:\nLine: {}\nFacePlate: {}", new Object[] {line, facePlate, rte});
                    }
                }
                int x = 0;
                int y = 0;
                if(lineParts.length > 1) {
                    x = Integer.parseInt(lineParts[1]);
                    y = Integer.parseInt(lineParts[2]);
                }
                facePlate = getFacePlate(x, y);
            } else if(attribute.equals("faceplateWidth")) {
                // e.g. faceplateWidth=433
                // Use the default Value
                // Größe der einzelnen Elemente 125,350
                if(facePlate == null) {
                    LOG.error("Wrong FacePlate Format!");
                    continue;
                }
                // int w = Integer.parseInt(lineParts[1]);
                // facePlate.setWidth(w);
            } else if(attribute.equals("faceplateX")) {
                // e.g. faceplateX=720
            } else if(attribute.equals("faceplateY")) {
                // e.g. faceplateY=40
            } else if(attribute.equals("groupComments")) {
                // e.g. groupComments=Control panels for TTF PS of Undulator 3.
            } else if(attribute.equals("groupHeight")) {
                // e.g. groupHeight=860
                // Use the default Value
                // height= Integer.parseInt(lineParts[1]);
                // Größe der einzelnen Elemente 125,350

                int value = 2 * Y_OFFSET + 60;
                System.out.println("H: " + value);
                displayModel.setPropertyValue(AbstractWidgetModel.PROP_HEIGHT, value);
            } else if(attribute.equals("groupNotes")) {
                // e.g. groupNotes=Druecke FEL-BOX
                // what is the equivalent in SDS.
            } else if(attribute.equals("groupTitle")) {
                // e.g. groupTitle= "VAKUUM KOMPRESSO"
                displayModel.setPropertyValue(AbstractWidgetModel.PROP_NAME, lineParts[1]);
                try {
                    header = getHeader(lineParts[1]);
                } catch (RuntimeException rte) {
                    LOG.warn("Can not added:\nLine: {}\nFacePlate: {}", new Object[] {line, facePlate,
                                                     rte});
                }
            } else if(attribute.equals("groupWidth")) {
                // e.g. groupWidth=900
                // Use the default Value
                // width = Integer.parseInt(lineParts[1]);
                // Größe der einzelnen Elemente 125,350

                displayModel.setPropertyValue(AbstractWidgetModel.PROP_WIDTH, groupX * (5 + 125) + 5);
            } else if(attribute.equals("groupX")) {
                // e.g. groupX=0
                groupX = Integer.parseInt(lineParts[1]);
            } else if(attribute.equals("groupY")) {
                // e.g. groupY=10
                // groupY = Integer.parseInt(lineParts[1]);
                Integer.parseInt(lineParts[1]);
            } else {
                LOG.debug("found unhandle Line: {}", line);
            }

        }
        if(facePlate != null) {
            try {
                displayModel.addWidget(facePlate);
            } catch (RuntimeException rte) {
                LOG.warn("Can not added:\nFacePlate: {}",facePlate,
                                                 rte);
            }
        }
        if(header != null) {
            header.setWidth(X_OFFSET * (_maxX + 1));
            displayModel.addWidget(header);
        }
        // displayModel.setSize(width, height);
        displayModel.setSize(X_OFFSET * (_maxX + 1), Y_OFFSET * (_maxY + 1) + 60);
    }

    private static LabelModel getHeader(String header) {
        try {
            int h = 40;
            int w = X_OFFSET * 4;
            LabelModel headerLabel = new LabelModel();
            headerLabel.setTextValue(header);
            headerLabel.setSize(w, h);
            headerLabel.setLocation(0, 10);
            int height = ADLHelper.getFontSize("Tahoma", header, h, w, "1");
            headerLabel.setFont(new FontData("Tahoma", height, SWT.BOLD));
            headerLabel.setTextValue(header);
            headerLabel.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "#000000");
            return headerLabel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static LinkingContainerModel getFacePlate(int x, int y) {
        Point key = new Point(x, y);
        LinkingContainerModel linkingContainerModel = _facePlateMap.get(key);
        while (linkingContainerModel != null) {
            if(key.x < 9) {
                key.x++;
            } else {
                key.x = 0;
                key.y++;
            }
            linkingContainerModel = _facePlateMap.get(key);
        }
        if(key.x > _maxX) {
            _maxX = key.x;
        }
        if(key.y > _maxY) {
            _maxY = key.y;
        }
        if(linkingContainerModel == null) {
            linkingContainerModel = new LinkingContainerModel();
            linkingContainerModel.setLocation(key.x * X_OFFSET, key.y * Y_OFFSET + 60);
            linkingContainerModel.setSize(WIDTH, HIGHT);
            _facePlateMap.put(key, linkingContainerModel);
        }
        return linkingContainerModel;
    }

}
