/**
 *
 */
package org.csstudio.opibuilder.properties;

import static org.csstudio.opibuilder.properties.ServiceMethodDescription.createServiceMethodDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Content;
import org.jdom.Element;

/**
 * @author shroffk
 *
 */
public class ServiceMethodProperty extends AbstractWidgetProperty {

    public ServiceMethodProperty(String prop_id, String description,
        WidgetPropertyCategory category) {
    super(prop_id, description, category, createServiceMethodDescription());
    }

    @Override
    public Object checkValue(Object value) {
    return value;
    }

    @Override
    protected PropertyDescriptor createPropertyDescriptor() {
    return new ServiceMethodPropertyDescriptor(prop_id, description, widgetModel);
    }

    private static final String XML_ELEMENT_SERVICE_METHOD_DESCRIPTION = "serviceMethodDescription";
    private static final String XML_ELEMENT_SERVICE_NAME = "service";
    private static final String XML_ELEMENT_METHOD_NAME = "method";
    private static final String XML_ELEMENT_METHOD_DESCRIPTION = "method";
    private static final String XML_ELEMENT_ARGUMENTS = "arguments";
    private static final String XML_ELEMENT_RESULTS = "results";

    @Override
    public void writeToXML(Element propElement) {
    ServiceMethodDescription serviceMethodDescription = (ServiceMethodDescription) getPropertyValue();
    Element serviceMethod;
    serviceMethod = new Element(XML_ELEMENT_SERVICE_METHOD_DESCRIPTION);

    serviceMethod.addContent(new Element(XML_ELEMENT_SERVICE_NAME)
        .addContent(serviceMethodDescription.getService()));

    serviceMethod.addContent(new Element(XML_ELEMENT_METHOD_NAME)
        .addContent(serviceMethodDescription.getMethod()));
    List<Content> arguments = new ArrayList<Content>();
    for (Entry<String, String> argument : serviceMethodDescription
        .getArgumentPvs().entrySet()) {
        arguments
            .add(new Element("entry").addContent(
                new Element("key").addContent(argument.getKey()))
                .addContent(
                    new Element("value").addContent(argument
                        .getValue())));
    }
    serviceMethod.addContent(new Element(XML_ELEMENT_ARGUMENTS)
        .addContent(arguments));
    List<Content> results = new ArrayList<Content>();
    for (Entry<String, String> result : serviceMethodDescription
        .getResultPvs().entrySet()) {
        results.add(new Element("entry").addContent(
            new Element("key").addContent(result.getKey())).addContent(
            new Element("value").addContent(result.getValue())));
    }
    serviceMethod.addContent(new Element(XML_ELEMENT_RESULTS)
        .addContent(results));
    propElement.addContent(serviceMethod);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object readValueFromXML(Element propElement) throws Exception {
    Element serviceMethodDescriptionElement = propElement
        .getChild(XML_ELEMENT_SERVICE_METHOD_DESCRIPTION);
    if (serviceMethodDescriptionElement != null) {
        String service = serviceMethodDescriptionElement.getChild(XML_ELEMENT_SERVICE_NAME).getText();
        String method = serviceMethodDescriptionElement.getChild(XML_ELEMENT_METHOD_NAME).getText();
        String description = serviceMethodDescriptionElement.getChild(XML_ELEMENT_METHOD_DESCRIPTION).getText();
        List<Element> arguments = serviceMethodDescriptionElement.getChild(XML_ELEMENT_ARGUMENTS).getChildren();
        Map<String, String> argPvs = new HashMap<String, String>();
        for (Element argument : arguments) {
        argPvs.put(argument.getChildText("key"), argument.getChildText("value"));
        }
        List<Element> results = serviceMethodDescriptionElement.getChild(XML_ELEMENT_RESULTS).getChildren();
        Map<String, String> resultPvs = new HashMap<String, String>();
        for (Element result : results) {
        resultPvs.put(result.getChildText("key"), result.getChildText("value"));
        }
        return ServiceMethodDescription.createServiceMethodDescription(service, method, description, argPvs, resultPvs);
    }
    return null;
    }

    @Override
    public Object getPropertyValue() {
    if(widgetModel !=null && widgetModel.getExecutionMode() == ExecutionMode.RUN_MODE){
            ServiceMethodDescription value = (ServiceMethodDescription) super.getPropertyValue();

        Map<String, String> argumentPvs = new HashMap<String, String>();
        for (Entry<String, String> arg : value.getArgumentPvs().entrySet()) {
            argumentPvs.put(arg.getKey(), OPIBuilderMacroUtil.replaceMacros(widgetModel, arg.getValue()));
        }

        Map<String, String> resultPvs = new HashMap<String, String>();
        for (Entry<String, String> result : value.getResultPvs().entrySet()) {
            resultPvs.put(result.getKey(), OPIBuilderMacroUtil.replaceMacros(widgetModel, result.getValue()));
        }

        return createServiceMethodDescription(value.getService(), value.getMethod(), value.getDescription(), argumentPvs, resultPvs);
    } else {
        return super.getPropertyValue();
    }
    }
}
