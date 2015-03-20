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
 * <code>SubValidationFailure</code> is a validation failure that was detected on a property, which is a subproperty
 * of a widget's property, such as actions, scripts, or rules.  
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SubValidationFailure extends ValidationFailure {

    private ValidationFailure parent;
    private final String subPropertyTag;
    private final String subPropertyDesc;
    private final String forcedMessage;
    
    private boolean isFixed = false;
    
    /**
     * Constructs a new sub validation failure.
     * 
     * @param path the path to the file in which the failure was detected
     * @param wuid the widget unique id that the failure was detected on
     * @param widgetType the typeId of the widget
     * @param widgetName the name of the widget
     * @param property the name of the parent property
     * @param subPropertyTag the tag of the subproperty 
     * @param subPropertyDesc the descriptive string of the sub property (e.g. name, or type)
     * @param expected the expected value of the property (from the schema)
     * @param actual the actual value of the property (from the validated model)
     * @param rule the rule that was violated
     * @param isCritical true if this is a critical failure or false otherwise
     * @param isFixable true if the failure can be quick fixed
     */
    public SubValidationFailure(IPath path, String wuid, String widgetType, String widgetName, String property, 
            String subPropertyTag, String subPropertyDesc, Object expected, Object actual, ValidationRule rule, 
            boolean isCritical, boolean isFixable, String forcedMessage) {
        super(path, wuid, widgetType, widgetName, property, expected, actual, rule, isCritical, isFixable, forcedMessage);
        this.subPropertyTag = subPropertyTag;
        this.subPropertyDesc = subPropertyDesc;
        this.forcedMessage = forcedMessage;
    }
    
    /**
     * @return the description of the sub property
     */
    public String getSubPropertyDesc() {
        return subPropertyDesc;
    }
    
    /**
     * @return the xml tag of the sub property
     */
    public String getSubPropertyTag() {
        return subPropertyTag;
    }
    
    /**
     * Set the parent validation failure. Only to be called from the {@link ValidationFailure}.
     * 
     * @param parent the parent
     */
    void setParent(ValidationFailure parent) {
        this.parent = parent;
    }
    
    /**
     * @return the parent failure of this sub validation failure
     */
    public ValidationFailure getParent() {
        return parent;
    }
    
    void setFixed(boolean fixed) {
        this.isFixed = fixed;
    }
    
    boolean isFixed() {
        return isFixed;
    }
    
    

    /*
     * (non-Javadoc)
     * @see org.csstudio.opibuilder.validation.core.ValidationFailure#getMessage()
     */
    @Override
    public String getMessage() {
        if (forcedMessage == null) {
            if (getRule() == ValidationRule.RO) {
                if (getActualValue() == null) {
                    return new StringBuilder(parent.getProperty().length() + subPropertyDesc.length() + 40)
                            .append(parent.getProperty()).append(": '").append(subPropertyDesc)
                            .append("' expected to be defined, but was not").toString();
                } else if (getExpectedValue() == null) {
                    return new StringBuilder(parent.getProperty().length() + subPropertyDesc.length() + 26)
                            .append(parent.getProperty()).append(": '").append(subPropertyDesc)
                            .append("' should not be defined").toString();
                } else {
                    return new StringBuilder(parent.getProperty().length() + subPropertyDesc.length() + getExpected().length() 
                            + getActual().length() + 29 ).append(parent.getProperty()).append(": '").append(subPropertyDesc)
                            .append("': expected: '").append(getExpected()).append("' but was: '").append(getActual())
                            .append('\'').toString();
                }
            } else if (getRule() == ValidationRule.WRITE) {
                return new StringBuilder(parent.getProperty().length() + subPropertyDesc.length() + 40)
                            .append(parent.getProperty()).append(": '").append(subPropertyDesc)
                            .append("' expected to be defined, but was not").toString();
            }
        } else {
            return new StringBuilder(parent.getProperty().length() + subPropertyDesc.length() 
                    + forcedMessage.length() +6)
                    .append(parent.getProperty()).append(": '").append(subPropertyDesc)
                    .append("': ").append(forcedMessage).toString();
        }
        return super.getMessage();
    }    
    
}
