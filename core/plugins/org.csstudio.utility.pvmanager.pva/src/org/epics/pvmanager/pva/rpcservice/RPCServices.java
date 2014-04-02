/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.rpcservice;

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

import org.epics.pvmanager.pva.rpcservice.rpcclient.PooledRPCClientFactory;
import org.epics.vtype.VType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class to create pvAccess RPC Services.
 *
 * @author dkumar
 */
public class RPCServices {


  private static ExecutorService defaultExecutor = Executors.newSingleThreadExecutor(org.epics.pvmanager.util.Executors.namedPool("pvAccess RPC services"));


  /**
   * Creates a pvAccess RPC Service based on the description in the XML file.
   *
   * @param input a stream with an xml file
   * @return a new RPC rpcservice
   */
  public static RPCService createFromXml(InputStream input) {
    try {

      if (input == null) {
        throw new IllegalArgumentException("input stream is not valid: null");
      }

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(input);

      XPathFactory xpathFactory = XPathFactory.newInstance();
      XPath xPath = xpathFactory.newXPath();

      String ver = xPath.evaluate("/pvAccessRPCService/@ver", document).trim();
      String serviceName = xPath.evaluate("/pvAccessRPCService/@name", document).trim();
      String serviceDescription = xPath.evaluate("/pvAccessRPCService/@description", document).trim();
      if (!ver.equals("1")) {
        throw new IllegalArgumentException("Unsupported version " + ver);
      }
      String channelName = xPath.evaluate("/pvAccessRPCService/channelName/text()", document).trim();
      if (channelName.isEmpty()) {
        throw new IllegalArgumentException("channelName element is missing");
      }

      String poolPropertiesAsText = xPath.evaluate("/pvAccessRPCService/poolproperties/text()", document).trim();

      if (!poolPropertiesAsText.isEmpty()) {
        //TODO CLOSING THE POOLED RPCCLIENT FACTORY
        PooledRPCClientFactory.setPoolConfiguration(poolPropertiesAsText);
      }

      String hostName = xPath.evaluate("/pvAccessRPCService/host/text()", document).trim();
      if (hostName.isEmpty()) {hostName = null;}

      String methodFieldName = xPath.evaluate("/pvAccessRPCService/methodFieldName/text()", document).trim();
      if (methodFieldName.isEmpty()) {methodFieldName = null;}

      boolean useNTQuery = false;
      String useNTQueryStr = xPath.evaluate("/pvAccessRPCService/useNTQuery/text()", document).trim();
      if (!useNTQueryStr.isEmpty()) useNTQuery = Boolean.valueOf(useNTQueryStr);

      RPCServiceDescription service = new RPCServiceDescription(serviceName, serviceDescription, hostName,
        channelName, methodFieldName, useNTQuery);

      service.executorService(defaultExecutor);

      NodeList methods = (NodeList) xPath.evaluate("/pvAccessRPCService/methods/method", document, XPathConstants.NODESET);

      for (int i = 0; i < methods.getLength(); i++) {
        Node method = methods.item(i);
        String structureId = xPath.evaluate("structureid/text()", method).trim();
        String methodName = xPath.evaluate("@name", method).trim();
        String methodDescription = xPath.evaluate("@description", method).trim();
        String resultName = xPath.evaluate("result/@name", method).trim();
        String resultType = xPath.evaluate("result/@type", method).trim();
        String resultFieldName = xPath.evaluate("result/@fieldName", method).trim();
        if (resultFieldName.isEmpty()) {resultFieldName = null;}
        String operationName = xPath.evaluate("@operationName", method).trim();
        if (operationName.isEmpty()) {operationName = null;}

        boolean isResultStandalone = Boolean.parseBoolean(xPath.evaluate("result/@standalone", method));
        String resultDescription = xPath.evaluate("result/@description", method).trim();
        
        //we are allowing only one result and checking that only one result was provided
        NodeList multipleResultsList = (NodeList) xPath.evaluate("result", method, XPathConstants.NODESET);
        if ((multipleResultsList != null) && (multipleResultsList.getLength() > 1)) {
          throw new IllegalArgumentException(methodName + ":The pvAccess RPC rpcservice can only have one result");
        }
        
        //if the result exists and is nonstandalone then it must be named
        if ((multipleResultsList.getLength() > 0) && !isResultStandalone && resultName.isEmpty() ) {
          throw new IllegalArgumentException("nonstandalone result must be named.");   
        }
        
        //in case a result is standalone and a result name is not set, we need to set it due to ServiceMethod name check
        if (isResultStandalone && resultName.isEmpty()){
          resultName = "result";
        }
        
        RPCServiceMethodDescription pvAccessRPCMethodDescription = 
                new RPCServiceMethodDescription(methodName, methodDescription, operationName,
                   structureId, isResultStandalone);
        
        if (!resultName.isEmpty()) {
          pvAccessRPCMethodDescription.addRPCResult(resultName, resultFieldName, resultDescription, getClassFromString(resultType));
        }

        NodeList arguments = (NodeList) xPath.evaluate("argument", method, XPathConstants.NODESET);
        for (int nArg = 0; nArg < arguments.getLength(); nArg++) {
          Node argument = arguments.item(nArg);
          String argName = xPath.evaluate("@name", argument).trim();
          String argDescription = xPath.evaluate("@description", argument).trim();
          String argType = xPath.evaluate("@type", argument).trim();
          String argFieldName = xPath.evaluate("@fieldName", argument).trim();
          if (argFieldName.isEmpty()) { argFieldName = null; }

          if (!argName.isEmpty()) {
            pvAccessRPCMethodDescription.addArgument(argName, argFieldName, argDescription, getClassFromString(argType));
          }
        }
        service.addServiceMethod(pvAccessRPCMethodDescription);
      }

      return new RPCService(service);

    } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
      Logger.getLogger(RPCServices.class.getName()).log(Level.FINEST, "Couldn't create rpcservice", ex);
      throw new IllegalArgumentException("Couldn't create rpcservice", ex);
    }
  }


  private static final String rootPackagePrefix = VType.class.getPackage().getName() + ".";
  
  private static Class<?> getClassFromString(String argType) {
	
	String fullClassName = rootPackagePrefix + argType;
	try {
      return Class.forName(fullClassName);
	}
	catch (Throwable th) {
        throw new IllegalArgumentException("Type " + argType + " not supported, '" + fullClassName + "' does not exist.");
	}
  }
}
