package org.csstudio.utility.adlparser.fileParser.widgets;

import java.util.ArrayList;

import org.csstudio.utility.adlparser.Activator;
import org.csstudio.utility.adlparser.IImageKeys;
import org.csstudio.utility.adlparser.fileParser.ADLResource;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLLimits;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.internationalization.Messages;

/**
 *
 * @author hammonds
 *
 */
public class TextEntryWidget extends ADLAbstractWidget implements ITextWidget {
    private String color_mode = new String("static");
    private String format = new String("decimal");
    private String alignment = new String("horiz. left");

    public TextEntryWidget(ADLWidget adlWidget) {
        super(adlWidget);
        name = new String("text entry");
        descriptor = Activator.getImageDescriptor(IImageKeys.ADL_TEXT_ENTRY);
        try {
            for (ADLWidget childWidget : adlWidget.getObjects()) {
                if (childWidget.getType().equals("object")){
                    _adlObject = new ADLObject(childWidget);
                    if (_adlObject != null){
                        _hasObject = true;
                    }

                }
                else if (childWidget.getType().equals("control")){
                    _adlControl = new ADLControl(childWidget);
                    if (_adlControl != null){
                        _hasControl = true;
                    }
                }
                else if (childWidget.getType().equals("limits")){
                    _adlLimits = new ADLLimits(childWidget);
                    if (_adlLimits != null){
                        _hasLimits = true;
                    }
                }
            }
            for (FileLine fileLine : adlWidget.getBody()){
                String bodyPart = fileLine.getLine();
                String[] row = bodyPart.trim().split("=");
                if (row.length < 2){
                    throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin + bodyPart + Messages.Label_WrongADLFormatException_Parameter_End);
                }
                if (FileLine.argEquals(row[0], "clrmod")){
                    setColor_mode(FileLine.getTrimmedValue(row[1]));
                }
                else if (FileLine.argEquals(row[0], "format")){
                    setFormat(FileLine.getTrimmedValue(row[1]));
                }
                else if (FileLine.argEquals(row[0], "align")){
                    setAlignment(FileLine.getTrimmedValue(row[1]));
                }
            }
        }
        catch (WrongADLFormatException ex) {

        }

    }
    /**
     * @param color_mode the color_mode to set
     */
    private void setColor_mode(String color_mode) {
        this.color_mode = color_mode;
    }

    /**
     * @return the color_mode
     */
    public String getColor_mode() {
        return color_mode;
    }

    /**
     * @param format the format to set
     */
    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the format
     */
    @Override
    public String getFormat() {
        return format;
    }

    /**
     * @param alignment the alignment to set
     */
    @Override
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    /**
     * @return the alignment
     */
    @Override
    public String getAlignment() {
        return alignment;
    }
    @Override
    public Object[] getChildren() {
        ArrayList<Object> ret = new ArrayList<Object>();
        if (_adlObject != null) ret.add( _adlObject);
        if (_adlControl != null) ret.add( _adlControl);
        if (_adlLimits != null) ret.add( _adlLimits);
        if (!(color_mode.equals(""))) ret.add(new ADLResource(ADLResource.COLOR_MODE, color_mode));
        if (!(alignment.equals(""))) ret.add(new ADLResource(ADLResource.TEXT_ALIGNMENT, alignment));
        if (!(format.equals(""))) ret.add(new ADLResource(ADLResource.TEXT_FORMAT, format));
        return ret.toArray();
    }

}


