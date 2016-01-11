/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 *
 * <code>SubValidationFailure</code> is a validation failure that was detected on a property, which is a subproperty of
 * a widget's property, such as actions, scripts, or rules.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SubValidationFailure extends ValidationFailure {

    private ValidationFailure parent;
    private final String subPropertyTag;
    private final String subPropertyDesc;
    private final String forcedMessage;
    private final boolean toRemove;
    private final IResource resource;

    private boolean isFixed = false;

    private boolean onlyMessage = false;

    /**
     * Constructs a new sub validation failure that only displays the message without any decorations.
     *
     * @param path the path to the file in which the failure was detected
     * @param message the message
     */
    public SubValidationFailure(IPath path, String message) {
        this(path, null, null, null, null, null, null, null, null, ValidationRule.WRITE, false, false, message, -1,
            null);
        this.onlyMessage = true;
    }

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
     * @param lineNumber the line number at which the failure occurred
     * @param model the model class that this validation failure originates from
     */
    public SubValidationFailure(IPath path, String wuid, String widgetType, String widgetName, String property,
        String subPropertyTag, String subPropertyDesc, Object expected, Object actual, ValidationRule rule,
        boolean isCritical, boolean isFixable, String forcedMessage, int lineNumber,
        Class<? extends AbstractWidgetModel> model) {
        this(path, wuid, widgetType, widgetName, property, subPropertyTag, subPropertyDesc, expected, actual, rule,
            isCritical, isFixable, forcedMessage, lineNumber, false, model, null);
    }

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
     * @param lineNumber the line number at which the failure occurred
     * @param toRemove a flag indicating if this failure is about a sub property that should be removed
     * @param model the model class that this validation failure originates from
     *
     */
    public SubValidationFailure(IPath path, String wuid, String widgetType, String widgetName, String property,
        String subPropertyTag, String subPropertyDesc, Object expected, Object actual, ValidationRule rule,
        boolean isCritical, boolean isFixable, String forcedMessage, int lineNumber, boolean toRemove,
        Class<? extends AbstractWidgetModel> model, IResource resource) {
        super(path, wuid, widgetType, widgetName, property, expected, actual, rule, isCritical, isFixable,
            forcedMessage, lineNumber, false, model);
        this.subPropertyTag = subPropertyTag == null ? "" : subPropertyTag;
        this.subPropertyDesc = subPropertyDesc == null ? "" : subPropertyDesc;
        this.forcedMessage = forcedMessage;
        this.toRemove = toRemove;
        this.resource = resource;
    }

    /**
     * @return true if describes a property that should be removed from the model
     */
    public boolean isToBeRemoved() {
        return toRemove;
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

    /**
     * Mark the failure as being fixed or not.
     *
     * @param fixed true if it is fixed or false otherwise
     */
    void setFixed(boolean fixed) {
        this.isFixed = fixed;
    }

    /**
     * @return true if the failure has been fixed or false otherwise
     */
    boolean isFixed() {
        return isFixed;
    }

    /**
     * Returns the resource on which the failure was detected. The resource is available only if different than the
     * resource that is being validated.
     *
     * @return the resource that failures relates to
     */
    public IResource getResource() {
        return resource;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.validation.core.ValidationFailure#setLineNumber(int)
     */
    @Override
    void setLineNumber(int lineNumber) {
        if (resource == null) {
            super.setLineNumber(lineNumber);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.validation.core.ValidationFailure#getMessage()
     */
    @Override
    public String getMessage() {
        if (forcedMessage == null) {
            if (toRemove) {
                return new StringBuilder(parent.getProperty().length() + subPropertyDesc.length() + 22)
                    .append(parent.getProperty()).append(": '").append(subPropertyDesc).append("' should be removed")
                    .toString();
            }
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
                    return new StringBuilder(parent.getProperty().length() + subPropertyDesc.length()
                        + getExpected().length() + getActual().length() + 29).append(parent.getProperty()).append(": '")
                            .append(subPropertyDesc).append("': expected: '").append(getExpected())
                            .append("' but was: '").append(getActual()).append('\'').toString();
                }
            } else if (getRule() == ValidationRule.WRITE) {
                return new StringBuilder(parent.getProperty().length() + subPropertyDesc.length() + 40)
                    .append(parent.getProperty()).append(": '").append(subPropertyDesc)
                    .append("' expected to be defined, but was not").toString();
            }
        } else {
            if (onlyMessage) {
                return forcedMessage;
            } else {
                if (subPropertyDesc.isEmpty()) {
                    return new StringBuilder(parent.getProperty().length() + forcedMessage.length() + 2)
                        .append(parent.getProperty()).append(':').append(' ').append(forcedMessage).toString();
                } else {
                    return new StringBuilder(
                        parent.getProperty().length() + subPropertyDesc.length() + forcedMessage.length() + 6)
                            .append(parent.getProperty()).append(": '").append(subPropertyDesc).append("': ")
                            .append(forcedMessage).toString();
                }
            }
        }
        return super.getMessage();
    }
}
