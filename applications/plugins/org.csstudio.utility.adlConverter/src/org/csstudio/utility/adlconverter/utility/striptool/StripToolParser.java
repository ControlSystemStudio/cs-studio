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
package org.csstudio.utility.adlconverter.utility.striptool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.csstudio.sds.components.model.StripChartModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.IWidgetModelFactory;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.eclipse.swt.graphics.RGB;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 19.08.2009
 */
public class StripToolParser {

    private static final double faktor = 1d/256d;
    private static RGB _backgrund;
    private static RGB _foregrund;
    private static RGB _grid;
    private static boolean _gridXon;
    private static boolean _gridYon;
    private static boolean _numSamples;
    private static int _axisYcolorStat;
    private static int _graphLineWidth;
    private static double _timespan;
    private static double _sampleInterval;
    private static double _refreshInterval;
    private static ArrayList<Boolean> _scale;
    private static ArrayList<Boolean> _plotStatus;
    private static ArrayList<String> _name;
    private static ArrayList<String> _units;
    private static ArrayList<Integer> _precision;
    private static double _min;
    private static double _max;
    private static ArrayList<RGB> _colors;

    public static void parse(String sourceFile, DisplayModel displayModel) {
        _backgrund = null;
        _foregrund = null;
        _grid = null;
        _gridXon = false;
        _gridYon = false;
        _axisYcolorStat = 0;
        _graphLineWidth = 0;
        _timespan = 0.0;
        _sampleInterval = 0.0;
        _refreshInterval = 0.0;
        _scale = new ArrayList<Boolean>();
        _plotStatus = new ArrayList<Boolean>();
        _name = new ArrayList<String>();
        _units = new ArrayList<String>();
        _precision = new ArrayList<Integer>();
        _min = Double.MAX_VALUE;
        _max = Double.MIN_VALUE;
        _colors = new ArrayList<RGB>();

        parseFile(sourceFile);
        setAttriebutes(displayModel);

    }

    private static void setAttriebutes(DisplayModel displayModel) {
        displayModel.setSize(800, 600);
        StripChartModel model = null;

        IWidgetModelFactory factory = WidgetModelFactoryService.getInstance()
                .getWidgetModelFactory(StripChartModel.ID);

        if (factory != null) {
            model = (StripChartModel) factory.createWidgetModel();
            model.setX(0);
            model.setY(0);
            model.setSize(800, 600);
            model.setPropertyValue(StripChartModel.PROP_X_AXIS_TIMESPAN, _timespan);
            model.setPropertyValue(StripChartModel.PROP_UPDATE_INTERVAL, _refreshInterval);
            model.setPropertyValue(StripChartModel.PROP_PLOT_LINE_WIDTH, _graphLineWidth);
            model.setPropertyValue(StripChartModel.PROP_MIN, _min);
            model.setPropertyValue(StripChartModel.PROP_MAX, _max);
            if (_backgrund != null) {
                model.setPropertyValue(StripChartModel.PROP_COLOR_BACKGROUND, _backgrund);
            }
            if (_foregrund != null) {
                model.setPropertyValue(StripChartModel.PROP_COLOR_FOREGROUND, _foregrund);
            }
            if (_grid!= null) {
                model.setPropertyValue(StripChartModel.PROP_GRID_LINE_COLOR, _grid);
            }
            if (_units != null && _units.size() > 0) {
                model.setPropertyValue(StripChartModel.PROP_Y_AXIS_LABEL, Arrays.toString(_units.toArray()).replaceAll(",", "\r\n"));
            }
            if (_name != null && _name.size() > 0) {
                for (int i = 0; i < _name.size()&&i<StripChartModel.NUMBER_OF_CHANNELS; i++) {
                    ParameterDescriptor parameterDescriptor = new ParameterDescriptor(_name.get(i),
                            Double.class);
                    DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
                    dynamicsDescriptor.addInputChannel(parameterDescriptor);
                    model.setDynamicsDescriptor(StripChartModel.valuePropertyId(i),
                            dynamicsDescriptor);
                    model.setPropertyValue(StripChartModel.enablePlotPropertyId(i), true);
                    if(_colors.size()>i) {
                        model.setPropertyValue(StripChartModel.plotColorPropertyId(i), _colors.get(i));
                    }
                }
            }
            if(_gridXon&&_gridYon) {
                model.setPropertyValue(StripChartModel.PROP_SHOW_GRID_LINES, "Both" );
            } else if(_gridXon) {
                model.setPropertyValue(StripChartModel.PROP_SHOW_GRID_LINES, "X-Axis" );
            } else if(_gridYon) {
                model.setPropertyValue(StripChartModel.PROP_SHOW_GRID_LINES, "Y-Axis" );
            }
//            model.setParent(displayModel);
            displayModel.addWidget(model);
        }
    }

    private static void parseFile(String sourceFile) {
        BufferedReader buffRead;
        try {
            buffRead = new BufferedReader(new FileReader(sourceFile));
            String line;
            int lineNumber = 0;
            int lastDirtyLine = 0;
            while ((line = buffRead.readLine()) != null) {
                line = line.trim();
                line = line.replaceAll("  +", " ");
                String[] splitLine = line.split("\\s");
                String[] splitKey = splitLine[0].split("\\.");
                if (splitKey.length > 2) {
                    if (splitKey[1].equals("Time")) {
                        time(splitKey[2], splitLine);
                    } else if (splitKey[1].equals("Color")) {
                        color(splitKey[2], splitLine);
                    } else if (splitKey[1].equals("Option")) {
                        option(splitKey[2], splitLine);
                    } else if (splitKey[1].equals("Curve") && splitKey.length > 3) {
                        curve(splitKey[3], splitLine);
                    }
                }
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void curve(String key4, String[] splitLine) {
        if (splitLine.length == 2) {
            if (key4.equals("Name")) {
                _name.add(splitLine[1]);
            } else if (key4.equals("Units")) {
                _units.add(splitLine[1]);
            } else if (key4.equals("Precision")) {
                _precision.add(Integer.parseInt(splitLine[1]));
            } else if (key4.equals("Min")) {
                double parseDouble = Double.parseDouble(splitLine[1]);
                if(parseDouble<_min) {
                    _min=parseDouble;
                }
            } else if (key4.equals("Max")) {
                double parseDouble = Double.parseDouble(splitLine[1]);
                if(parseDouble>_max) {
                    _max=parseDouble;
                }
            } else if (key4.equals("Scale")) {
                _scale.add(Integer.parseInt(splitLine[1]) == 1);
            } else if (key4.equals("PlotStatus")) {
                _plotStatus.add(Integer.parseInt(splitLine[1]) == 1);
            }
        }
    }

    private static void option(String key3, String[] splitLine) {
        if (splitLine.length == 2) {
            if (key3.equals("GridXon")) {
                _gridXon = Integer.parseInt(splitLine[1]) == 1;
            } else if (key3.equals("GridYon")) {
                _gridYon = Integer.parseInt(splitLine[1]) == 1;
            } else if (key3.equals("AxisYcolorStat")) {
                _axisYcolorStat = Integer.parseInt(splitLine[1]);
            } else if (key3.equals("GraphLineWidth")) {
                _graphLineWidth = Integer.parseInt(splitLine[1]);
            }
        }
    }

    private static void color(String key3, String[] splitLine) {
        if (splitLine.length == 4) {
            if (key3.equals("Background")) {
                int r = (int)(faktor * Integer.parseInt(splitLine[1]));
                int g = (int)(faktor * Integer.parseInt(splitLine[2]));
                int b = (int)(faktor * Integer.parseInt(splitLine[3]));
                _backgrund = new RGB(r,g,b);
            } else if (key3.equals("Foreground")) {
                int r = (int)(faktor * Integer.parseInt(splitLine[1]));
                int g = (int)(faktor * Integer.parseInt(splitLine[2]));
                int b = (int)(faktor * Integer.parseInt(splitLine[3]));
                _foregrund = new RGB(r,g,b);
            } else if (key3.equals("Grid")) {
                int r = (int)(faktor * Integer.parseInt(splitLine[1]));
                int g = (int)(faktor * Integer.parseInt(splitLine[2]));
                int b = (int)(faktor * Integer.parseInt(splitLine[3]));
                _grid = new RGB(r,g,b);
            } else if (key3.startsWith("Color")) {
                int r = (int)(faktor * Integer.parseInt(splitLine[1]));
                int g = (int)(faktor * Integer.parseInt(splitLine[2]));
                int b = (int)(faktor * Integer.parseInt(splitLine[3]));
                _colors.add(new RGB(r,g,b));
            }
        }
    }

    private static void time(String key3, String[] splitLine) {
        if (splitLine.length == 2) {
            if (key3.equals("Timespan")) {
                _timespan = Double.parseDouble(splitLine[1]);
            } else if (key3.equals("NumSamples")) {
                _numSamples = Integer.parseInt(splitLine[1]) == 1;
            } else if (key3.equals("SampleInterval")) {
                _sampleInterval = Double.parseDouble(splitLine[1]);
            } else if (key3.equals("RefreshInterval")) {
                _refreshInterval = Double.parseDouble(splitLine[1]);
            }
        }
    }
}
