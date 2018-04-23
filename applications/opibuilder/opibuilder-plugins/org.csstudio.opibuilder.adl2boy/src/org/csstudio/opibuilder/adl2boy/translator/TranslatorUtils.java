/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.opibuilder.widgets.model.RectangleModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.ColorMap;
import org.csstudio.utility.adlparser.fileParser.ParserADL;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLDynamicAttribute;
import org.eclipse.swt.graphics.RGB;

/**
 * Utilities to aid in translating ADL files to OPI files.
 *
 * @author John Hammonds, Argonne National Laboratory
 *
 */
public class TranslatorUtils {
    private static ADLBasicAttribute defaultBasicAttribute = new ADLBasicAttribute();
    private static ADLDynamicAttribute defaultDynamicAttribute = new ADLDynamicAttribute();

    public static void ConvertChildren(ArrayList<ADLWidget> childWidgets, AbstractContainerModel parentModel, RGB colorMap[]){

        for (ADLWidget adlWidget : childWidgets){
            try {
                String widgetType = adlWidget.getType();
                printHandlingMessage(widgetType);
                if (widgetType.equals("arc")){
                    new Arc2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("bar")){
                    new Bar2Model(adlWidget, colorMap, parentModel);
                    printNotCompletelyHandledMessage(widgetType);

                }
                else if (widgetType.equals("byte")){
                    new Byte2Model(adlWidget, colorMap,parentModel);
                }
                else if (widgetType.equals("cartesian plot")){
                    new CartesianPlot2Model(adlWidget, colorMap,parentModel);
                    printNotCompletelyHandledMessage(widgetType);

                }
                else if (widgetType.equals("choice button")){
                    new ChoiceButton2Model(adlWidget, colorMap,parentModel);
                }
                else if (widgetType.equals("composite")){
                    new Composite2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("dynamic symbol")){
                    printNotHandledMessage(widgetType);
                }
                else if (widgetType.equals("file")){
                    //There is really nothing to do here.  This is in every adl
                    //file and there is no translating to be done
                }
                else if (widgetType.equals("image")){
                    new Image2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("indicator")){
                    printNotHandledMessage(widgetType);
                }
                else if (widgetType.equals("menu")){
                    new Menu2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("message button")){
                    new MessageButton2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("toggle button")){
                    printNotHandledMessage(widgetType);
                }
                else if (widgetType.equals("meter")){
                    new Meter2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("oval")){
                    new Oval2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("polygon")){
                    new Polygon2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("polyline")){
                    new PolyLine2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("line")){
                    new PolyLine2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("rectangle")){
                    new Rectangle2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("related display")){
                    new RelatedDisplay2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("strip chart")){
                    printNotHandledMessage(widgetType);
                }
                else if (widgetType.equals("text")){
                    new Text2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("text update")){
                    new TextUpdate2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("text entry")){
                    new TextEntry2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("valuator")){
                    new Valuator2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("shell command")){
                    new ShellCommand2Model(adlWidget, colorMap, parentModel);
                }
                else if (widgetType.equals("basic attribute")){
                    ArrayList<ADLWidget> children = adlWidget.getObjects();
                    for (ADLWidget child : children){
                        setDefaultBasicAttribute(child);
                    }
                }
                else if (widgetType.equals("dynamic attribute")){
                    ArrayList<ADLWidget> children = adlWidget.getObjects();
                    for (ADLWidget child : children){
                        setDefaultBasicAttribute(child);
                    }
                }

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
//        Class classToMove = RectangleModel.class;

        lowerWidgetType(parentModel, AbstractContainerModel.class);
        lowerWidgetType(parentModel, RectangleModel.class);
        lowerWidgetType(parentModel, ImageModel.class);
    }

    private static void lowerWidgetType(AbstractContainerModel parentModel,
            Class classToMove) {
        List<AbstractWidgetModel> moveToBack = new LinkedList<AbstractWidgetModel>();
        ListIterator<AbstractWidgetModel> childIter = parentModel.getChildren().listIterator();
        while (childIter.hasNext()){
            AbstractWidgetModel widget = childIter.next();
            if ( classToMove.isInstance(widget) ){
                moveToBack.add(widget);
            }
        }
        int numMoved = 0;
        if (moveToBack.size() > 0){
            ListIterator<AbstractWidgetModel> iter = moveToBack.listIterator();
            while (iter.hasNext()){
                parentModel.changeChildOrder(iter.next(), numMoved);
                numMoved++;
            }

        }
    }

    /**
     * @param child
     * @throws WrongADLFormatException
     */
    public static void setDefaultBasicAttribute(ADLWidget child)
            throws WrongADLFormatException {
        if (child.getType().equals("attr")){
            child.setType("basic attribute");
            TranslatorUtils.defaultBasicAttribute = new ADLBasicAttribute(child);
        }
    }

    /**
     * Print message that a given ADL file structure is not handled.
     */
    private static void printNotHandledMessage(String type) {
        System.out.println("TranslatorUtils: " + type + " is not handled");
    }
    private static void printNotCompletelyHandledMessage(String type) {
        System.out.println("TranslatorUtils: " + type + " is not completely handled");
    }

    /**
     *
     * @param type
     */
    private static void printHandlingMessage(String type) {
        System.out.println("Handling: " + type);
    }

    public static void printNotHandledWarning(String translator, String message){
        System.out.println("---Warning - " + translator + ": " + message + " is not handled" );
    }
    public static ADLBasicAttribute getDefaultBasicAttribute(){
        return TranslatorUtils.defaultBasicAttribute;
    }

    public static ADLDynamicAttribute getDefaultDynamicAttribute(){
        return TranslatorUtils.defaultDynamicAttribute;
    }

    public static void initDefaultBasicAttribute(){
        TranslatorUtils.defaultBasicAttribute = new ADLBasicAttribute();
    }

    public static void initDefaultDynamicAttribute(){
        TranslatorUtils.defaultDynamicAttribute = new ADLDynamicAttribute();
    }

    /**
     * @param fullADLFileName
     * @return
     */
    public static DisplayModel convertAdlToModel(String fullADLFileName) {
        File adlFile = new File(fullADLFileName);
        ADLWidget root = ParserADL.getNextElement(new File(fullADLFileName));
        // Get the color map
        RGB[] colorMap = getColorMap(root);
        // Configure the display

        DisplayModel displayModel = initializeDisplayModel(root, colorMap);
        //Dynamic and basic attribute are static in Translator utils to allow for defaults to be set (used before vers 020200)
        initDefaultBasicAttribute();
        initDefaultDynamicAttribute();

        displayModel.setName(adlFile.getName().substring(0, adlFile.getName().indexOf(".")));
        ConvertChildren(root.getObjects(), displayModel, colorMap);
        return displayModel;
    }

    public static String patchXML(final String xml) throws IOException
    {
        final BufferedReader reader = new BufferedReader(new StringReader(xml));
        final StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            // Skip the wuid:
            // Not generally useful, and causes *.opi to look
            // different each time the same *.adl is converted
            if (line.trim().startsWith("<wuid"))
                continue;
            out.append(line).append("\n");
        }
        return out.toString();
    }

    /**
     * Get the colorMap from an ADLroot ADLWidget
     * @param root
     * @return
     */
    protected static RGB[] getColorMap(ADLWidget root) {
        RGB colorMap[] = new RGB[0];
        for (ADLWidget adlWidget : root.getObjects()){
            String widgetType = adlWidget.getType();
            try {
                if (widgetType.equals("color map")){
                    ColorMap tempColorMap = new ColorMap(adlWidget);
                    colorMap = tempColorMap.getColors();
                }
            }
            catch (Exception ex) {
                System.out.println("Error reading ColorMap");
                ex.printStackTrace();
            }
        }
        return colorMap;
    }

    /**
     * get an initial DisplayModel from a given ADLWidget using a colorMap
     * @param root
     * @param colorMap
     * @return
     */
    protected static DisplayModel initializeDisplayModel(ADLWidget root,
            RGB[] colorMap) {
        DisplayModel displayModel = new DisplayModel();

        for (ADLWidget adlWidget : root.getObjects()){
            String widgetType = adlWidget.getType();
            if (widgetType.equals("display")){
                displayModel = (DisplayModel)(new Display2Model(adlWidget, colorMap, null)).getWidgetModel();
            }
        }
        return displayModel;
    }

}
