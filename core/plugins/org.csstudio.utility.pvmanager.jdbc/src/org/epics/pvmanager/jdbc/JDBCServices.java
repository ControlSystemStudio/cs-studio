/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class to create JDBCServices.
 *
 * @author carcassi
 */
public class JDBCServices {

    private JDBCServices() {
        // Prevent instanciation
    }
    
    private static ExecutorService defaultExecutor = Executors.newSingleThreadExecutor(org.epics.pvmanager.util.Executors.namedPool("JDBC services"));

    /**
     * Creates a JDBCService based on the description of an XML file.
     * 
     * @param input a stream with an xml file
     * @return the new service
     */
    public static JDBCService createFromXml(InputStream input) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);
            
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xPath = xpathFactory.newXPath();
            
            String ver = xPath.evaluate("/jdbcService/@ver", document);
            String serviceName = xPath.evaluate("/jdbcService/@name", document);
            String serviceDesecription = xPath.evaluate("/jdbcService/@description", document);
            String jdbcUrl = xPath.evaluate("/jdbcService/jdbcUrl", document);
            if (!ver.equals("1")) {
                throw new IllegalArgumentException("Unsupported version " + ver);
            }
            
            JDBCServiceDescription service = new JDBCServiceDescription(serviceName, serviceDesecription);
            service.dataSource(new SimpleDataSource(jdbcUrl));
            service.executorService(defaultExecutor);

            NodeList methods = (NodeList) xPath.evaluate("/jdbcService/methods/method", document, XPathConstants.NODESET);
            for (int i = 0; i < methods.getLength(); i++) {
                Node method = methods.item(i);
                String methodName = xPath.evaluate("@name", method);
                String methodDescription = xPath.evaluate("@description", method);
                String query = xPath.evaluate("query", method);
                String resultName = xPath.evaluate("result/@name", method);
                String resultDescription = xPath.evaluate("result/@description", method);
                
                JDBCServiceMethodDescription jdbcMethod = new JDBCServiceMethodDescription(methodName, methodDescription);
                jdbcMethod.query(query);
                if (!resultName.trim().isEmpty()) {
                    jdbcMethod.queryResult(resultName, resultDescription);
                }
                
                NodeList arguments = (NodeList) xPath.evaluate("argument", method, XPathConstants.NODESET);
                for (int nArg = 0; nArg < arguments.getLength(); nArg++) {
                    Node argument = arguments.item(nArg);
                    String argName = xPath.evaluate("@name", argument);
                    String argDescription = xPath.evaluate("@description", argument);
                    String argType = xPath.evaluate("@type", argument);
                    Class<?> argClass = null;
                    switch(argType) {
                        case "VNumber": argClass = VNumber.class;
                            break;
                        case "VString": argClass = VString.class;
                            break;
                        default: throw new IllegalArgumentException("Type " + argType + " not supported.");
                    }
                    if (!argName.trim().isEmpty()) {
                        jdbcMethod.addArgument(argName, argDescription, argClass);
                    }
                }
                service.addServiceMethod(jdbcMethod);
            }
            
            return new JDBCService(service);
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
            Logger.getLogger(JDBCServices.class.getName()).log(Level.FINEST, "Couldn't create service", ex);
            throw new IllegalArgumentException("Couldn't create service", ex);
        }
    }
}
