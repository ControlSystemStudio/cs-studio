/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import org.eclipse.core.runtime.IPath;

/**
 * 
 * <code>ValidationFailure</code> represents a single validation failure, which is a result of validating the OPI
 * content against the schema.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ValidationFailure {
    
    private final String widgetType;
    private final String widgetName;
    private final String property;
    private final String expected;
    private final String actual;
    private final IPath path;
    private final Object expectedValue;
    private final Object actualValue;
    private final ValidationRule rule;
    private final boolean isCritical;
    
    private int lineNumber;
    
    /**
     * Constructs a new validation failure.
     * 
     * @param path the path to the file, in which the failure originates
     * @param widgetType the type of the widget
     * @param widgetName the name of the widget
     * @param property the property name which is failing
     * @param expected the expected value of the property (schema value)
     * @param actual the actual value of the property (validated opi value)
     * @param rule the rule, which was violated
     * @param isCritical defines if the failure is critical or not (failure is critical when a RO rule is violated
     *          using a non-predefined value)
     */
    ValidationFailure(IPath path, String widgetType, String widgetName, 
            String property, Object expected, Object actual, ValidationRule rule, boolean isCritical) {
        this.widgetType = widgetType;
        this.widgetName = widgetName;
        this.property = property;
        this.expectedValue = expected;
        this.actualValue = actual;
        this.expected = String.valueOf(expected);
        this.actual = String.valueOf(actual);
        this.path = path;
        this.rule = rule;
        this.isCritical = isCritical;
    }
    
    /**
     * Constructs the message describing this validation failure. The message is used for the contents of the marker.
     * 
     * @return the message describing the failure
     */
    public String getMessage() {
        if (rule == ValidationRule.RO) {
            return property + ": expected: '" + String.valueOf(expected) + "' but was: '" + String.valueOf(actual) + "'";
        } else if (rule == ValidationRule.WRITE) {
            return property + " is not set";
        } else {
            return "What could be wrong?";
        }
    }
    
    /**
     * Constructs the location message containing the line number and widget name.
     * 
     * @return the location
     */
    public String getLocation() {
        return "line " + lineNumber + " (Widget: " + widgetName + ")";
    }
    
    /**
     * @return the line number at which the failure was recorder
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * Sets the lin number at which this failure was recorded.
     * 
     * @param lineNumber the line number
     */
    void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the widget typeId.
     */
    public String getWidgetType() {
        return widgetType;
    }

    /**
     * @return the widget name
     */
    public String getWidgetName() {
        return widgetName;
    }

    /**
     * @return the property name
     */
    public String getProperty() {
        return property;
    }

    /**
     * @return the expected property value transformed to string
     */
    public String getExpected() {
        return expected;
    }

    /**
     * @return the actual property value transformed to string
     */
    public String getActual() {
        return actual;
    }

    /**
     * @return the path to the file on which this failure occurred
     */
    public IPath getPath() {
        return path;
    }   
    
    /**
     * @return the expected property value as a real property object
     */
    public Object getExpectedValue() {
        return expectedValue;
    }
    
    /**
     * @return the actual property value as a real property object
     */
    public Object getActualValue() {
        return actualValue;
    }
    
    /**
     * @return the violated validation rule
     */
    public ValidationRule getRule() {
        return rule;
    }
    
    /**
     * @return true if this validation represents a critical failure (error) or false if not critical (warning)
     */
    public boolean isCritical() {
        return isCritical;
    }
    
}
