package org.csstudio.ams.performancetesttool;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.jms.JMSException;
import javax.jms.MapMessage;

public class MapMessageTemplate {
    
    /**
     * A template for a value in a message.
     */
    private interface TemplateValue {
        /**
         * Instantiates this template value, returning an actual value for a message.
         */
        public String instantiate();
    }
    
    private static class DateValue implements TemplateValue {
        private String value;
        
        DateValue(String format) {
            // Always return the same value for performance reasons
            this.value = new SimpleDateFormat(format).format(new Date());
        }
        
        @Override
        public String instantiate() {
            return value;
        }
    }
    
    private static class RandomValue implements TemplateValue {
        private String[] values;
        private Random random;
        
        RandomValue(String values) {
            this.values = values.split(",\\s*");
            this.random = new Random();
        }
        
        @Override
        public String instantiate() {
            return values[random.nextInt(values.length)];
        }
    }
    
    private Map<String, TemplateValue> template;
    private Map<String, String> constantValues;
    
    public MapMessageTemplate(File templateFile) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(templateFile));
            template = new HashMap<String, TemplateValue>();
            constantValues = new HashMap<String, String>();
            Enumeration<Object> keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = properties.getProperty(key);
                if (isVariable(value)) {
                    template.put(key, propertyToTemplateValue(value));
                } else {
                    constantValues.put(key, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load message template file: " + templateFile
                    + "; Exception: " + e.getMessage());
        }
    }
    
    private TemplateValue propertyToTemplateValue(String propertyValue) {
        int indexOfColon = propertyValue.indexOf(":");
        if (indexOfColon == -1) {
            throw new RuntimeException("Template contains invalid variable: " + propertyValue);
        }
        String type = propertyValue.substring(1, indexOfColon);
        String params = propertyValue.substring(indexOfColon + 1, propertyValue.length() - 1);
        if ("date".equalsIgnoreCase(type)) {
            return new DateValue(params);
        } else if ("random".equalsIgnoreCase(type)) {
            return new RandomValue(params);
        } else {
            throw new RuntimeException("Unknown type of variable: " + propertyValue);
        }
    }
    
    private boolean isVariable(String propertyValue) {
        return propertyValue.startsWith("{") && propertyValue.endsWith("}");
    }
    
    /**
     * Sets the fields of the given MapMessage with values based on this template.
     */
    public void applyTo(MapMessage message) throws JMSException {
        for (Map.Entry<String, TemplateValue> entries : template.entrySet()) {
            message.setString(entries.getKey(), entries.getValue().instantiate());
        }
        for (Map.Entry<String, String> entries : constantValues.entrySet()) {
            message.setString(entries.getKey(), entries.getValue());
        }
    }
}
