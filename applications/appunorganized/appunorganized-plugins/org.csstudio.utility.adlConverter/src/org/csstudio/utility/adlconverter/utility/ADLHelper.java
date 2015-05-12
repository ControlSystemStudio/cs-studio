/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id$
 */
package org.csstudio.utility.adlconverter.utility;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.utility.adlconverter.Activator;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.ui.preferences.ADLConverterPreferenceConstants;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.09.2007
 */
public final class ADLHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ADLHelper.class);

    /**
     * The minimum font size that can calculated.
     */
    private static final int MIN_FONT_SIZE = 6;
    /** Contain all colors of an ADL Colormap as a RGBColor. */
    private static RGBColor[] _rgbColor;
    private static String PATH_REMOVE_PART = Activator.getDefault().getPreferenceStore()
            .getString(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part);
    private static String STRIP_TOOL_PATH_REMOVE_PART = Activator
            .getDefault()
            .getPreferenceStore()
            .getString(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part_Strip_Tool);
    private static String targetpath;
    private static String _fullpath;

    /**
     * The default Constructor.
     */
    private ADLHelper() {
        // Static Constructor.
    }

    /**
     * Convert a adl color value to a RGB.
     *
     * @param clr the adl color value.
     * @return the converted RGB
     */
    public static String getRGB(final String clr) {
        String color = clr.replaceAll("\"", "");
        int colorID = Integer.parseInt(color);
        if ( (_rgbColor == null) || (0 > colorID) || (colorID >= _rgbColor.length)) {
            return null;
        }

        return _rgbColor[colorID].getHex();
    }

    /**
     * @param colorMap The ADL Colormap String. Each element a line
     * @throws WrongADLFormatException this exception was thrown the String not an valid ADL Color
     *             Map String.
     */
    public static void setColorMap(final ADLWidget colorMap) throws WrongADLFormatException {

        assert colorMap.isType("color map") : Messages.ADLHelper_AssertError_Begin + colorMap.getType() + Messages.ADLHelper_AssertError_End; //$NON-NLS-1$

        String[] anz = colorMap.getBody().get(0).getLine()
                .replaceAll("\\{", "").trim().toLowerCase().split("="); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (!anz[0].equals("ncolors") || (anz.length != 2)) { //$NON-NLS-1$
            throw new WrongADLFormatException(Messages.ADLHelper_WrongADLFormatException_Begin
                    + colorMap.getBody().get(0) + Messages.ADLHelper_WrongADLFormatException_End);
        }

        ADLWidget colors = colorMap.getObjects().get(0);
        if (colors.isType("dl_color")) { //$NON-NLS-1$
            int i = 0;
            _rgbColor = new RGBColor[Integer.parseInt(anz[1])];
            for (ADLWidget dlColorObj : colorMap.getObjects()) {
                if (dlColorObj.isType("dl_color") && (i < _rgbColor.length)) {
                    int r = 0;
                    int g = 0;
                    int b = 0;
                    for (FileLine fileLine : dlColorObj.getBody()) {
                        String line = fileLine.getLine();
                        String[] row = line.split("=");
                        String type = row[0].trim();
                        if (type.equals("r")) {
                            r = Integer.parseInt(row[1]);
                        } else if (type.equals("g")) {
                            g = Integer.parseInt(row[1]);
                        } else if (type.equals("b")) {
                            b = Integer.parseInt(row[1]);
                        } else if (type.equals("inten")) {
//                            inten = Integer.parseInt(row[1]);
                        } else {
                            LOG.info("Wrong Format: ",new WrongADLFormatException("Wrong Color Map dl_color Property."
                                                  + fileLine));
                        }
                    }
                    _rgbColor[i] = new RGBColor(r, g, b);
                    i++;
                }
            }
        } else if (colors.isType("colors")) { //$NON-NLS-1$
            _rgbColor = new RGBColor[Integer.parseInt(anz[1])];
            for (int j = 0; (j < colors.getBody().size()) && (j < _rgbColor.length); j++) {
                _rgbColor[j] = new RGBColor(colors.getBody().get(j).getLine()
                        .replaceAll(",", "").trim()); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            throw new WrongADLFormatException(Messages.ADLHelper_WrongADLFormatException_Begin
                    + colors.getType() + Messages.ADLHelper_WrongADLFormatException_End);
        }
    }

    /**
     * Remove all ",Whitspace and change $(name) to $channel$.
     *
     * @param dirtyString The string to clean it.
     * @return the cleaned string.
     */
    public static String[] cleanString(final String dirtyString) {

        String delimiter = dirtyString.replaceAll("\"", "").trim(); // use variable delimiter in
                                                                    // wrong context. $NON-NLS-1$
                                                                    // $NON-NLS-2$
        if (delimiter.endsWith(".adl") || delimiter.endsWith(".stc")) {
            return cleanFileName(delimiter);
        }

        String[] tempString; // 0=record, 1=_pid, 2=.LOLO
        String[] cleanString;

        tempString = delimiter.split("[\\.]"); //$NON-NLS-1$
        delimiter = ""; //$NON-NLS-1$
        if (tempString.length < 1) {
            cleanString = new String[] { dirtyString, "" }; //$NON-NLS-1$
        } else {
            cleanString = new String[tempString.length + 1];
            cleanString[0] = ""; //$NON-NLS-1$
        }
        for (int i = 0; i < tempString.length; i++) {
            if ( (i > tempString.length - 2) && dirtyString.contains(".")) { //$NON-NLS-1$
                delimiter = "."; //$NON-NLS-1$
            }
            String string = tempString[i];
            cleanString[0] = cleanString[0] + delimiter + string;
            cleanString[i + 1] = string;
        }
        if (cleanString[0].endsWith(".HOPR")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.HOPR", "[graphMax]");
        } else if (cleanString[0].endsWith(".LOPR")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.LOPR", "[graphMin]");
        } else if (cleanString[0].endsWith(".EGU")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.EGU", "[units]");
        } else if (cleanString[0].endsWith(".EGUF")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.EGUF", "[maximum]");
        } else if (cleanString[0].endsWith(".EGUL")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.EGUL", "[minimum]");
        } else if (cleanString[0].endsWith(".HIGH")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.HIGH", "[warningMax]");
        } else if (cleanString[0].endsWith(".LOW")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.LOW", "[warningMin]");
        } else if (cleanString[0].endsWith(".HIHI")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.HIHI", "[alarmMax]");
        } else if (cleanString[0].endsWith(".LOLO")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.LOLO", "[alarmMin]");
        } else if (cleanString[0].endsWith(".NAME")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.NAME", "[displayName]");
        } else if (cleanString[0].endsWith(".LINR")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.LINR", "[scaleType]");
        } else if (cleanString[0].endsWith(".SEVR")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.SEVR", "[severity]");
        } else if (cleanString[0].endsWith(".STAT")) { //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.STAT", "[status]");
        }
        //        if (cleanString[1].startsWith("$")) { //$NON-NLS-1$
        // // String temp =
        // // cleanString[1].substring(cleanString[1].indexOf(')')+1);
        //            cleanString[1] = cleanString[1].replace("(", "").replace(')', '$'); //$NON-NLS-1$ //$NON-NLS-2$
        //            // cleanString[1] ="$channel$"+temp; //$NON-NLS-1$
        // }
        // // if(cleanString.length>2){
        //        // cleanString[1]=cleanString[1]+"_"+cleanString[2]; //$NON-NLS-1$
        // // if(param!=null){
        //        // cleanString[2]=param+"_"+cleanString[2]; //$NON-NLS-1$
        // // }
        // // }
        if (cleanString.length > 3) {
            cleanString[cleanString.length - 1] = delimiter + cleanString[cleanString.length - 1];
        }
        return cleanString;
    }

    /**
     * @param dirtyString
     * @return
     */
    private static String[] cleanFileName(final String dirtyString2) {
        String dirtyString = dirtyString2;
        if (dirtyString.endsWith(".adl")) { //$NON-NLS-1$
            if (dirtyString.startsWith(PATH_REMOVE_PART)) {
                dirtyString = dirtyString.replaceAll(PATH_REMOVE_PART, ""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            dirtyString = dirtyString.replaceAll("\\.adl", ".css-sds"); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (dirtyString.endsWith(".stc")) { //$NON-NLS-1$
            if (dirtyString.startsWith(STRIP_TOOL_PATH_REMOVE_PART)) {
                dirtyString = dirtyString.replaceAll(STRIP_TOOL_PATH_REMOVE_PART, ""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            dirtyString = dirtyString.replace(".stc", ".css-plt"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return new String[] { dirtyString };
    }

    /**
     * @param dynamicsDescriptor set at this dynamicsDescriptor the ConnectionState.
     */
    public static void setConnectionState(final DynamicsDescriptor dynamicsDescriptor) {
        Map<ConnectionState, Object> connectionState = new HashMap<ConnectionState, Object>();
        // TODO: Check new Connection State !!!! connectionState.put(ConnectionState.INITIAL,
        // ColorConstants.white.getRGB());
        connectionState.put(ConnectionState.DISCONNECTED, ColorConstants.white.getRGB());
        dynamicsDescriptor.setConnectionStateDependentPropertyValues(connectionState);
    }

    /**
     * Calculate the best fit font size for a horizontal text in a rectangle area. (e.g. Label)
     *
     * @param font The font name.
     * @param text The Text to pass in the field.
     * @param maxHigh The max high in px that the text can have.
     * @param maxWidth The width high in px that the text can have.
     * @param style The font style "0" = none. "1" = bold,
     * @return the 'optimal' font size.
     */
    public static int getFontSize(final String font,
                                  final String text,
                                  final int maxHigh,
                                  final int maxWidth,
                                  final String style) {
        int fontSize = (int) Math.ceil(maxHigh * 0.6);
        if (fontSize < MIN_FONT_SIZE) {
            fontSize = 10;
        }

        if ( (text != null) && (text.length() > 0)) {
            TextLayout tl = new TextLayout(null);
            tl.setText(text);
            Font f = CustomMediaFactory.getInstance().getFont(font, fontSize, 0);
            tl.setFont(f);
            while ( (maxWidth < tl.getBounds().width) && (fontSize > MIN_FONT_SIZE)) {
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
     * @param chan the was set to Primery PV and as channel alias
     * @return the Channel field.
     */
    public static String setChan(final AbstractWidgetModel widgetModel, final String[] chan) {
        return setChan(widgetModel, chan, "");
    }

    public static String setChan(final AbstractWidgetModel widgetModel,
                                 final String[] chan,
                                 final String which) {
        String postfix = ""; //$NON-NLS-1$
        if (chan[0].length() == 0) {
            return ""; //$NON-NLS-1$
        }
        if ( (chan.length > 2) && chan[1].startsWith("$")) { //$NON-NLS-1$
            LOG.debug(Arrays.toString(chan));
            widgetModel.setAliasValue("channel".concat(which), chan[0]); //$NON-NLS-1$
        } else {
            LOG.debug(Arrays.toString(chan));
            widgetModel.setAliasValue("channel".concat(which), chan[1]); //$NON-NLS-1$
        }
        widgetModel.setLayer(Messages.ADLDisplayImporter_ADLDynamicLayerName);
        if (which.equals("")) {
            widgetModel.setPrimarPv("$channel$");
        }
        // if(chan.length>2&&chan[chan.length-1].startsWith(".")){ //$NON-NLS-1$
        // postfix = chan[chan.length-1];
        // }
        // Beim Bargraph wird der Postfix doppelt gesetzt.
        //        if (chan.length > 2) { //$NON-NLS-1$
        //            postfix = "." + chan[chan.length - 1]; //$NON-NLS-1$
        // }
        //        assert chan.length > 3 : "Length =<3 is "+chan.length; //$NON-NLS-1$
        return postfix;
    }

    /**
     * Test the proportion of a Widget to the parent (Display) and exceed the width or high a 1/4
     * from parent set the widget to the background layer.
     *
     * @param widget The widget to test the size.
     * @param parent the parent to compare.
     */
    public static void checkAndSetLayer(final AbstractWidgetModel widget,
                                        final AbstractWidgetModel parent) {
        if (parent instanceof DisplayModel) {
            if ( ( (parent.getWidth() / 4) < widget.getWidth())
                    || ( (parent.getHeight() / 4) < widget.getHeight())) {
                widget.setLayer(Messages.ADLDisplayImporter_ADLBackgroundLayerDes);
            }
        }

    }

    /**
     * Some paths in adl files starts with /applic/graphic that is an error in SDS.
     *
     * @param path
     * @return
     */
    public static String cleanFilePath(final String path) {
        String copyPath = path;
        String source = Activator.getDefault().getPreferenceStore()
                .getString(ADLConverterPreferenceConstants.P_STRING_Path_Remove_Absolut_Part);
        if ( (source == null) || source.equals("")) {
            return copyPath;
        }

        do {
            copyPath = copyPath.replace(source, "");
            source = source.substring(source.indexOf('/', 1));
        } while (source.lastIndexOf('/') > 0);
        return copyPath;
    }

    public static void setPath(final ADLWidget wid) {
        for (FileLine line : wid.getBody()) {
            String[] row = line.getLine().trim().split("=");
            if (row[0].equals("name")) {
                _fullpath = row[1].replaceAll("\"", "");
                return;
            }
        }
    }

    public static String getPath() {
        return _fullpath;
    }

    public static String getFolderPath() {
        String[] tmp = _fullpath.split("/");
        String folder = "";
        for (int i = 0; i < tmp.length - 1; i++) {
            folder = folder.concat(tmp[i]).concat("/");
        }

        return folder;
    }

    public static void setTargetPath(final String path) {
        targetpath = path;
        // targetpath =
        // ResourcesPlugin.getWorkspace().getRoot().getRawLocation().append(path).toString();
    }

    public static String getTargetPath() {
        return targetpath;
    }

    /**
     * Helper method for findWidgetPath
     */
    private static String checkDisplayPath(final String path, final String name) {
        String copyPath = path.trim();
        if (copyPath.endsWith("/")) {
            copyPath = copyPath.substring(0, copyPath.length() - 1);
        }

        // Both file formats are checked, to ensure that if multiple displays are being
        // converted out of order, this method doesn't break by failing to find
        // the yet to be converted <filename>.adl display.
        File file1 = new File(copyPath + "/" + name + ".adl");
        File file2 = new File(copyPath + "/" + name + ".css-sds");
        Path path1 = new Path(copyPath + "/" + name + ".adl");
        Path path2 = new Path(copyPath + "/" + name + ".css-sds");

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if (file1.exists() || file2.exists()) {
            return copyPath.replaceAll(root.getRawLocation().toString(), "");
        }
        if (root.exists(path1) || root.exists(path2)) {
            return root.getFullPath().append(copyPath).toString();
        }

        return null;
    }

    /**
     * Helper method for findWidgetPath
     */
    private static String checkImagePath(final String path, final String name) {
        String copyPath = path.trim();
        if (copyPath.endsWith("/")) {
            copyPath = copyPath.substring(0, copyPath.length() - 1);
        }

        File file = new File(copyPath + "/" + name);
        Path path1 = new Path(copyPath + "/" + name);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if (file.exists()) {
            return copyPath.replaceAll(root.getRawLocation().toString(), "");
        }
        if (root.exists(path1)) {
            return root.getFullPath().append(copyPath).toString();
        }

        return null;
    }

    /**
     * First checks the parent path of the calling display, then the workspace path, then each of
     * the display paths provided in the preferences for the source file of this widget. Returns the
     * FIRST directory where the display/image exists.
     */
    public static String findWidgetPath(final String name) {
        String copyName = name;
        String parent = getTargetPath();
        String path = null;
        String allpaths = Activator.getDefault().getPreferenceStore()
                .getString(ADLConverterPreferenceConstants.P_STRING_Display_Paths);
        String[] displaypaths = allpaths.split(",");
        if (copyName.startsWith("/")) {
            copyName = copyName.substring(1, copyName.length());
        }

        if (copyName.endsWith(".css-sds")) {
            copyName = copyName.replaceAll(".css-sds", "");

            path = checkDisplayPath(parent, copyName);
            if (path != null) {
                return path;
            }
            path = checkDisplayPath("", copyName);
            if (path != null) {
                return path;
            }

            for (String dpath : displaypaths) {
                path = checkDisplayPath(dpath, copyName);
                if (path != null) {
                    return path;
                }
            }
        }
        if (copyName.endsWith(".gif")) {
            path = checkImagePath(parent, copyName);
            if (path != null) {
                return path;
            }
            path = checkImagePath("", copyName);
            if (path != null) {
                return path;
            }

            for (String dpath : displaypaths) {
                path = checkImagePath(dpath, copyName);
                if (path != null) {
                    return path;
                }
            }
        }
        // Return the default if nothing else is found
        return Activator.getDefault().getPreferenceStore()
                .getString(ADLConverterPreferenceConstants.P_STRING_Path_Target);
    }

}
