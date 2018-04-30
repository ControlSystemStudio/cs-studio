/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.csstudio.opibuilder.converter.model.EdmEntity;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Singleton class for writing EdmModel data to XML output.
 * @author Matevz
 */
public class OpiWriter {

    private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.OpiWriter");

    private static OpiWriter instance;

    /**
     * Instantiates EdmModel.
     * @throws EdmException if an error occurs.
     */
    private OpiWriter() throws EdmException {
        EdmModel.getInstance();
    }

    /**
     * Returns an instance of OpiWriter
     * @return OpiWriter instance.
     * @throws EdmException if error occurs when instantiating EdmModel.
     */
    public synchronized static OpiWriter getInstance() throws EdmException {
        if (instance == null)
            instance = new OpiWriter();
        return instance;
    }

    /**
     * Outputs EdmColorsList data from EdmModel into colors.def file.
     * @throws EdmException if there is write error.
     */
    public void writeColorDef(String fileName) throws EdmException {
        OpiColorDef.writeDefFile(EdmModel.getColorsList(), fileName);
    }

    /**
     * Outputs EdmDisplay data with automatically generated output OPI
     * file name (auto added .opi extension).
     * @param displayFile    EDM Display file.
     * @throws EdmException if there is an write error.
     */
    public void writeDisplayFile(String displayFile) throws EdmException {

        String opiName = displayFile;
        if (opiName.endsWith(".edl"))
            opiName = opiName.substring(0, opiName.length() - 4);
        opiName = opiName + ".opi";

        writeDisplayFile(displayFile, opiName);

    }

    /**
     * Outputs EdmDisplay data from EDL file to OPI XML file.
     * @param displayFile EDL Edm Display file.
     * @param opiFile Output OPI file.
     * @throws EdmException if there is an write error.
     */
    public void writeDisplayFile(String displayFile, String opiFile) throws EdmException {

        EdmDisplay display = EdmModel.getDisplay(displayFile);
        Document doc = createDomDocument();
        new OpiDisplay(doc, display, displayFile);
        writeXML(doc, opiFile);

    }

    /**
     * Generates the DOM XML model for a group of entities.
     */
    public static void writeWidgets(Context context, Vector<? extends EdmEntity> entities) {

        boolean robust = Boolean.parseBoolean(System.getProperty("edm2xml.robustParsing"));

        log.config("Generating XML model for widgets.");

        for (EdmEntity e : entities) {
            Class<? extends EdmEntity> edmClass = e.getClass();
            String opiClassName = edmClass.getName().replaceFirst("model", "writer")
                .replaceFirst("Edm_", "Opi_");

            log.config("Generating XML model for widget: " + opiClassName);
            try {
                Class<?> opiClass = Class.forName(opiClassName);
                Constructor<?> opiConstructor = opiClass.getConstructor(Context.class, edmClass);
                OpiWidget widget = (OpiWidget) opiConstructor.newInstance(context, e);
                // Sort widget properties into alphabetical order as this is the order in which
                // CS-Studio will save them. This makes diffing the xml easier.
                sortChildNodes(widget);
            } catch (ClassNotFoundException exception) {
                log.warning("Class not declared: " + opiClassName);
            } catch (Exception exception) {
                log.log(Level.WARNING, "Error in converting " + e, exception);
            }
        }
    }

    /**
     * Prepares DOM Document for building XML.
     * @return DOM XML document.
     * @throws EdmException if there is an DOM builder error.
     */
    private Document createDomDocument() throws EdmException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.newDocument();

        } catch (ParserConfigurationException e) {
            throw new EdmException(EdmException.DOM_BUILDER_EXCEPTION,
                    "Error instantiating DOM document.", e);
        }
    }

    /**
     * Outputs XML Document data to specified file name.
     * @param doc XML Document.
     * @param fileName Output file name.
     * @throws EdmException if there is a write error.
     */
    private void writeXML(Document doc, String fileName) throws EdmException {

        log.config("Writing XML file: " + fileName);

        try (FileOutputStream fos = new FileOutputStream(new File(fileName))) {
            // To make the output as similar as possible to the previous version,
            // manually write the XML declaration.
            fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer idTransform = transFactory.newTransformer();
            // Don't write the XML declaration as we have already done so.
            idTransform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            idTransform.setOutputProperty(OutputKeys.INDENT,"yes");
            idTransform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source input = new DOMSource(doc);
            Result output = new StreamResult(fos);
            idTransform.transform(input, output);
            log.config("Completed.");
        } catch (Exception e) {
            throw new EdmException(EdmException.OPI_WRITER_EXCEPTION, "Error writing to file " + fileName, e);
        }
    }

    /**
     * Sort child nodes of the current <code>widgetContext</code> into alphabetical order.
     * @param widget The widget who's current context will be sorted.
     */
    private static void sortChildNodes(OpiWidget widget) {
        Element element = widget.widgetContext.getElement();
        NodeList unsortedNodes = element.getChildNodes();
        IntStream.range(0, unsortedNodes.getLength())
            .mapToObj(i -> unsortedNodes.item(i))
            .sorted((o1, o2) -> o1.getNodeName().compareTo(o2.getNodeName()))
            .forEach(n -> {element.removeChild(n); element.appendChild(n);});
    }
}
