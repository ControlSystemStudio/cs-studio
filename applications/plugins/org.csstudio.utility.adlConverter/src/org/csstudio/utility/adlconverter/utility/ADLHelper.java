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
package org.csstudio.utility.adlconverter.utility;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.Activator;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.ui.preferences.ADLConverterPreferenceConstants;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextLayout;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.09.2007
 */
public final class ADLHelper {
    /** Contain all colors of an ADL Colormap as a RGBColor. */
    private static RGBColor[] _rgbColor;
    private static String PATH_REMOVE_PART = Activator.getDefault().getPreferenceStore().getString(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part);
    /**
     * The default Constructor.
     */
    private ADLHelper() {
    }

    /**
     * Convert a adl color value to a RGB.
     * 
     * @param clr
     *            the adl color value.
     * @return the converted RGB
     */
    public static RGB getRGB(final String clr) {
        String color = clr.replaceAll("\"", "");
        int colorID = Integer.parseInt(color);
        if (_rgbColor ==null || 0 > colorID || colorID >= _rgbColor.length) {
            return null;
        }

        return _rgbColor[colorID].getRGB();
    }

    /**
     * @param colorMap
     *            The ADL Colormap String. Each element a line
     * @throws WrongADLFormatException
     *             this exception was thrown the String not an valid ADL Color
     *             Map String.
     */
    public static void setColorMap(final ADLWidget colorMap) throws WrongADLFormatException {

        assert colorMap.isType("color map") : Messages.ADLHelper_AssertError_Begin + colorMap.getType() + Messages.ADLHelper_AssertError_End; //$NON-NLS-1$

        String[] anz = colorMap.getBody().get(0).getLine()
                .replaceAll("\\{", "").trim().toLowerCase().split("="); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (!anz[0].equals("ncolors") || anz.length != 2) { //$NON-NLS-1$
            throw new WrongADLFormatException(Messages.ADLHelper_WrongADLFormatException_Begin
                    + colorMap.getBody().get(0) + Messages.ADLHelper_WrongADLFormatException_End);
        }

        ADLWidget colors = colorMap.getObjects().get(0);
        if (colors.isType("dl_color")) { //$NON-NLS-1$
            int i = 0;
            _rgbColor = new RGBColor[Integer.parseInt(anz[1])];
            for (ADLWidget dlColorObj : colorMap.getObjects()) {
                if(dlColorObj.isType("dl_color")&&i<_rgbColor.length){
                    int r = 0;
                    int g = 0;
                    int b = 0;
                    int inten = 0;
                    for (FileLine fileLine : dlColorObj.getBody()) {
                        String line = fileLine.getLine();
                        String[] row = line.split("=");
                        String type = row[0].trim();
                        if(type.equals("r")){
                            r = Integer.parseInt(row[1]);
                        }else if(type.equals("g")){
                            g = Integer.parseInt(row[1]);
                        }else if(type.equals("b")){
                            b = Integer.parseInt(row[1]);
                        }else if(type.equals("inten")){
                            inten = Integer.parseInt(row[1]);
                        }else{
                            CentralLogger.getInstance().info(ADLWidget.class, new WrongADLFormatException("Wrong Color Map dl_color Property."+fileLine));
                        }
                    }
                    _rgbColor[i] = new RGBColor(r,g,b,inten);
                    i++;
                }
            }
        } else if (colors.isType("colors")) { //$NON-NLS-1$
            _rgbColor = new RGBColor[Integer.parseInt(anz[1])];
            for (int j = 0; j < colors.getBody().size() && j < _rgbColor.length; j++) {
                _rgbColor[j] = new RGBColor(colors.getBody().get(j).getLine().replaceAll(",", "").trim()); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            throw new WrongADLFormatException(Messages.ADLHelper_WrongADLFormatException_Begin
                    + colors.getType() + Messages.ADLHelper_WrongADLFormatException_End);
        }
    }

    /**
     * Remove all ",Whitspace and change $(name) to $channel$.
     * 
     * @param dirtyString
     *            The string to clean it.
     * @return the cleaned string.
     */
    public static String[] cleanString(final String dirtyString) {
        String delimiter = ""; //$NON-NLS-1$
        String[] tempString; // 0=record, 1=_pid, 2=.LOLO
        String[] cleanString;
        delimiter = dirtyString.replaceAll("\"", "").trim(); // use variable
                                                                // delimiter in
                                                                // wrong
                                                                // context.
                                                                // //$NON-NLS-1$
                                                                // //$NON-NLS-2$
        tempString = delimiter.split("[\\.]"); //$NON-NLS-1$
        delimiter = ""; //$NON-NLS-1$
        if (tempString.length < 1) {
            cleanString = new String[] { dirtyString, "" }; //$NON-NLS-1$
        } else {
            cleanString = new String[tempString.length + 1];
            cleanString[0] = ""; //$NON-NLS-1$
        }
        for (int i = 0; i < tempString.length; i++) {
            if (i > tempString.length - 2 && dirtyString.contains(".")) { //$NON-NLS-1$
                delimiter = "."; //$NON-NLS-1$
            }
            String string = tempString[i];
            cleanString[0] = cleanString[0] + delimiter + string;
            cleanString[i + 1] = string;
        }
        if (cleanString[0].endsWith(".adl")) { //$NON-NLS-1$
            if(cleanString[0].startsWith(PATH_REMOVE_PART)) {
                cleanString[0] = cleanString[0].replaceAll(PATH_REMOVE_PART, ""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            cleanString[0] = cleanString[0].replaceAll("\\.adl", ".css-sds"); //$NON-NLS-1$ //$NON-NLS-2$
        }
//        if (cleanString[1].startsWith("$")) { //$NON-NLS-1$
//        // String temp =
//        // cleanString[1].substring(cleanString[1].indexOf(')')+1);
//            cleanString[1] = cleanString[1].replace("(", "").replace(')', '$'); //$NON-NLS-1$ //$NON-NLS-2$
//            // cleanString[1] ="$channel$"+temp; //$NON-NLS-1$
//        }
//        // if(cleanString.length>2){
//        // cleanString[1]=cleanString[1]+"_"+cleanString[2]; //$NON-NLS-1$
//        // if(param!=null){
//        // cleanString[2]=param+"_"+cleanString[2]; //$NON-NLS-1$
//        // }
//        // }
        if (cleanString.length > 3) {
            cleanString[cleanString.length - 1] = delimiter + cleanString[cleanString.length - 1];
        }
        return cleanString;
    }

    /**
     * 
     * @param dynamicsDescriptor
     *            set at this dynamicsDescriptor the ConnectionState.
     */
    public static void setConnectionState(final DynamicsDescriptor dynamicsDescriptor) {
        Map<ConnectionState, Object> connectionState = new HashMap<ConnectionState, Object>();
//TODO: Check new Connection State !!!!        connectionState.put(ConnectionState.INITIAL, ColorConstants.white.getRGB());
        connectionState.put(ConnectionState.DISCONNECTED, ColorConstants.white.getRGB());
        dynamicsDescriptor.setConnectionStateDependentPropertyValues(connectionState);
    }

    /**
     * Calculate the best fit font size for a horizontal text in a rectangle
     * area. (e.g. Label)
     * 
     * @param font
     *            The font name.
     * @param text
     *            The Text to pass in the field.
     * @param maxHigh
     *            The max high in px that the text can have.
     * @param maxWidth
     *            The width high in px that the text can have.
     * @param style
     *            The font style "0" = none. "1" = bold,
     * 
     * @return the 'optimal' font size.
     */
    public static int getFontSize(final String font, final String text, final int maxHigh,
            final int maxWidth, final String style) {
        int fontSize = (int) Math.ceil(maxHigh * 0.6);
        if (fontSize <= 0) {
            fontSize = 10;
        }

        if (text != null && text.length() > 0) {
            TextLayout tl = new TextLayout(null);
            tl.setText(text);
            Font f = CustomMediaFactory.getInstance().getFont(font, fontSize, 0);
            tl.setFont(f);
            while (maxWidth < tl.getBounds().width && fontSize > 1) {
                fontSize--;
                f = CustomMediaFactory.getInstance().getFont(font, fontSize, 0);
                tl.setFont(f);
            }
            tl.dispose();
        }
        return fontSize;
    }

    /**
     * @param widgetModel the widget model to set the chan. 
     * @param chan
     *            the was set to Primery PV and as channel alias
     * @return the Channel field.
     */
    public static String setChan(final AbstractWidgetModel widgetModel, final String[] chan) {
        String postfix = ""; //$NON-NLS-1$
        if (chan[0].length() == 0) {
            return ""; //$NON-NLS-1$
        } else if (chan.length > 2 && chan[1].startsWith("$")) { //$NON-NLS-1$
            widgetModel.setAliasValue("channel", chan[1]); //$NON-NLS-1$
            widgetModel.setPrimarPv("$channel$");
        } else {
            widgetModel.setAliasValue("channel", chan[1]); //$NON-NLS-1$
            widgetModel.setPrimarPv("$channel$");
        }
        // if(chan.length>2&&chan[chan.length-1].startsWith(".")){ //$NON-NLS-1$
        // postfix = chan[chan.length-1];
        // }
        if (chan.length > 2) { //$NON-NLS-1$
            postfix = "." + chan[chan.length - 1]; //$NON-NLS-1$
        }
//        assert chan.length > 3 : "Length =<3 is "+chan.length; //$NON-NLS-1$
        return postfix;
    }

    /**
     * Test the proportion of a Widget to the parent (Display) and exceed the width or
     * high a 1/4 from parent set the widget to the background layer.
     * 
     * @param widget The widget to test the size.
     * @param parent the parent to compare.
     */
    public static void checkAndSetLayer(final AbstractWidgetModel widget,
            final AbstractWidgetModel parent) {
        if (parent instanceof DisplayModel) {
            if ((parent.getWidth() / 4) < widget.getWidth()
                    || (parent.getHeight() / 4) < widget.getHeight()) {
                widget.setLayer(Messages.ADLDisplayImporter_ADLBackgroundLayerDes);
            }
        }

    }
    
    /**
     * Some paths in adl files starts with /applic/graphic that is an error
     * in SDS.
     * 
     * @param path
     * @return
     */
    public static String cleanFilePath(String path) {
        String source = Activator.getDefault().getPreferenceStore().getString(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part);
        do {
            path = path.replace(source, "");
            source = source.substring(source.indexOf('/', 1));
        } while(source.lastIndexOf('/')>0);
        return path;
    }

}
