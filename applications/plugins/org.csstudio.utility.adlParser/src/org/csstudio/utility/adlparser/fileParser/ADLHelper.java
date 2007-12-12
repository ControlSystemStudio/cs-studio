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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.util.CustomMediaFactory;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextLayout;
import org.epics.css.dal.context.ConnectionState;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.09.2007
 */
public final class ADLHelper {
    /** Contain all colors of an ADL Colormap as a RGBColor. */
    private static RGBColor[] _rgbColor;

    /**
     * The default Constructor.
     */
    private ADLHelper() {    }
    
    /**
     * Convert a adl color value to a RGB.
     * 
     * @param clr the adl color value.
     * @return the converted RGB
     */
    public static RGB getRGB(final String clr) {
        int colorID = Integer.parseInt(clr); 
        if(0>colorID&&colorID>=_rgbColor.length){
            return null;
        }

        return _rgbColor[colorID].getRGB();
    }
    
    /**
     * @param colorMap The ADL Colormap String. Each element a line
     * @throws WrongADLFormatException this exception was thrown the String not an valid ADL Color Map String. 
     */
    public static void setColorMap(final ADLWidget colorMap) throws WrongADLFormatException{
        
        assert !colorMap.isType("\"color map\"") : Messages.ADLHelper_AssertError_Begin+colorMap.getType()+Messages.ADLHelper_AssertError_End; //$NON-NLS-1$

        String[] anz = colorMap.getBody().get(0).replaceAll("\\{", "").trim().toLowerCase().split("="); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if(!anz[0].equals("ncolors")||anz.length!=2){ //$NON-NLS-1$
            throw new WrongADLFormatException(Messages.ADLHelper_WrongADLFormatException_Begin+colorMap.getBody().get(0)+Messages.ADLHelper_WrongADLFormatException_End);
        }

        ADLWidget colors = colorMap.getObjects().get(0); 
        if(colors.isType("dl_color")){ //$NON-NLS-1$
            int i=0;
            while(colors.isType("dl_color")&&i>=colorMap.getObjects().size()){  //$NON-NLS-1$
                //TODO: ColorMap --> dl_color
                colors = colorMap.getObjects().get(i++); 
            }
        }else if(colors.isType("colors")){ //$NON-NLS-1$
            _rgbColor = new RGBColor[Integer.parseInt(anz[1])];
            for(int j=0; j<colors.getBody().size()&&j<_rgbColor.length;j++){
                _rgbColor[j]=new RGBColor(colors.getBody().get(j).replaceAll(",", "").trim()); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }else{
            throw new WrongADLFormatException(Messages.ADLHelper_WrongADLFormatException_Begin+colors.getType()+Messages.ADLHelper_WrongADLFormatException_End);
        }
    }

    // Alte Version kommt nicht mit mehr als 2 '_' im Recordnamen zurecht.
    
   
//    /**
//     * Remove all ",Whitspace and change $(name) to $channel$.
//     * @param dirtyString The string to clean it.
//     * @return the cleaned string.
//     */
//    public static String[] cleanString(final String dirtyString){
//        String delimiter =""; //$NON-NLS-1$
//        String[] tempString; //0=record, 1=_pid, 2=.LOLO
//        String[] cleanString;
//        String param=null;
//        delimiter = dirtyString.replaceAll("\"", "").trim(); // use variable delimiter in wrong context. //$NON-NLS-1$ //$NON-NLS-2$
//        tempString = delimiter.split("[\\._]"); //$NON-NLS-1$
//        delimiter =""; //$NON-NLS-1$
//        if(tempString.length<1){
//            cleanString = new String[]{dirtyString,""}; //$NON-NLS-1$
//        }else {
//            cleanString = new String[tempString.length+1];
//            cleanString[0]=""; //$NON-NLS-1$
////            if(tempString[0].contains("=")){ //$NON-NLS-1$
////                tempString[0]=tempString[0].split("=")[1]; //$NON-NLS-1$
////            }
//        }
//        for (int i = 0; i < tempString.length; i++) {
//            if(i>tempString.length-2&&dirtyString.contains(".")){ //$NON-NLS-1$
//                 delimiter="."; //$NON-NLS-1$
//            }
//            String string = tempString[i];
//            cleanString[0] = cleanString[0]+ delimiter+string;
//            cleanString[i+1]=string;
//            if(i==0){
//                 delimiter="_"; //$NON-NLS-1$
//            }
//        }
//        if(cleanString[0].endsWith(".adl")){ //$NON-NLS-1$
//            cleanString[0] = cleanString[0].replaceAll("\\.adl", ".css-sds"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//        if(cleanString[1].startsWith("$")){ //$NON-NLS-1$
//            String temp = cleanString[1].substring(cleanString[1].indexOf(')')+1);
//            param=cleanString[1].replace("(", "").replace(')', '$');
//            cleanString[1] ="$channel$"+temp; //$NON-NLS-1$
//        }
//        if(cleanString.length>2){
//            cleanString[1]=cleanString[1]+"_"+cleanString[2]; //$NON-NLS-1$
//            if(param!=null){
//                cleanString[2]=param+"_"+cleanString[2]; //$NON-NLS-1$
//            }                                                                                            
//        }
//        if(cleanString.length>3){
//            cleanString[cleanString.length-1]= delimiter+cleanString[cleanString.length-1];
//        }
//        return cleanString;
//    }
    
    /**
     * Remove all ",Whitspace and change $(name) to $channel$.
     * @param dirtyString The string to clean it.
     * @return the cleaned string.
     */
    public static String[] cleanString(final String dirtyString){
        String delimiter =""; //$NON-NLS-1$
        String[] tempString; //0=record, 1=_pid, 2=.LOLO
        String[] cleanString;
        String param=null;
        delimiter = dirtyString.replaceAll("\"", "").trim(); // use variable delimiter in wrong context. //$NON-NLS-1$ //$NON-NLS-2$
        tempString = delimiter.split("[\\.]"); //$NON-NLS-1$
        delimiter =""; //$NON-NLS-1$
        if(tempString.length<1){
            cleanString = new String[]{dirtyString,""}; //$NON-NLS-1$
        }else {
            cleanString = new String[tempString.length+1];
            cleanString[0]=""; //$NON-NLS-1$
        }
        for (int i = 0; i < tempString.length; i++) {
            if(i>tempString.length-2&&dirtyString.contains(".")){ //$NON-NLS-1$
                 delimiter="."; //$NON-NLS-1$
            }
            String string = tempString[i];
            cleanString[0] = cleanString[0]+ delimiter+string;
            cleanString[i+1]=string;
        }
        if(cleanString[0].endsWith(".adl")){ //$NON-NLS-1$
            cleanString[0] = cleanString[0].replaceAll("\\.adl", ".css-sds"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if(cleanString[1].startsWith("$")){ //$NON-NLS-1$
            String temp = cleanString[1].substring(cleanString[1].indexOf(')')+1);
            cleanString[1]=cleanString[1].replace("(", "").replace(')', '$');
//            cleanString[1] ="$channel$"+temp; //$NON-NLS-1$
        }
//        if(cleanString.length>2){
//            cleanString[1]=cleanString[1]+"_"+cleanString[2]; //$NON-NLS-1$
//            if(param!=null){
//                cleanString[2]=param+"_"+cleanString[2]; //$NON-NLS-1$
//            }                                                                                            
//        }
        if(cleanString.length>3){
            cleanString[cleanString.length-1]= delimiter+cleanString[cleanString.length-1];
        }
        return cleanString;
    }

    
    /**
     *  
     * @param dynamicsDescriptor set at this dynamicsDescriptor the ConnectionState.    
     */
    public static void setConnectionState(final DynamicsDescriptor dynamicsDescriptor) {
        HashMap<ConnectionState, Object> connectionState = new HashMap<ConnectionState, Object>();
        connectionState.put(ConnectionState.INITIAL, ColorConstants.white.getRGB());
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
     *  
     * @return the 'optimal' font size. 
     */
    public static int getFontSize(final String font,final String text, final int maxHigh, final int maxWidth, final String style) {
        int fontSize = (int)Math.ceil(maxHigh*0.6);
        if (fontSize<=0) {
            fontSize =10; 
        }
        
        if(text!=null&&text.length()>0){
            TextLayout tl = new TextLayout(null);
            tl.setText(text);
            Device d = null;
            Font f = CustomMediaFactory.getInstance().getFont(font,fontSize,0);
            tl.setFont(f);
            while(maxWidth<tl.getBounds().width&&fontSize>1){
                fontSize--;
                f = CustomMediaFactory.getInstance().getFont(font,fontSize,0);
                tl.setFont(f);
            }
            tl.dispose();
        }
        return fontSize;
    }

    /**
     * @param widgetModel
     * @param chan
     * @return
     */
    public static String setChan(AbstractWidgetModel widgetModel, String[] chan) {
        String postfix = ""; //$NON-NLS-1$
        if(chan[0].length()==0){
            return ""; //$NON-NLS-1$
        }else if(chan.length>2&&chan[1].startsWith("$")){ //$NON-NLS-1$
            widgetModel.setAliasValue("channel",chan[1]); //$NON-NLS-1$
            widgetModel.setPrimarPv(chan[1]);   
        }else{
            widgetModel.setAliasValue("channel", chan[1]); //$NON-NLS-1$
            widgetModel.setPrimarPv(chan[0]);
        }
//        if(chan.length>2&&chan[chan.length-1].startsWith(".")){ //$NON-NLS-1$
//            postfix = chan[chan.length-1];
//        }
        if(chan.length>2){ //$NON-NLS-1$
            postfix = "."+chan[chan.length-1];
        }
        assert chan.length >3 : "Test Chan";
        return postfix;
    }
    
}
