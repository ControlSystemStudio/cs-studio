package com.cosylab.vdct.vdb;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.regex.Pattern;

import com.cosylab.vdct.dbd.DBDConstants;
import com.cosylab.vdct.graphics.objects.Descriptable;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.Template;
import com.cosylab.vdct.inspector.ChangableVisibility;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.inspector.InspectorManager;

/**
 * @author Matej
 */
public class VDBTemplateMacro extends VDBFieldData implements Descriptable, ChangableVisibility
{
    protected VDBMacro macro = null;
    protected VDBTemplateInstance templateInstance = null;

    protected int visibility = UNDEFINED_VISIBILITY;

    private static final String nullString = "";

    /**
     */
    public VDBTemplateMacro(VDBTemplateInstance templateInstance, VDBMacro macro)
    {
        super();
        this.macro = macro;
        this.templateInstance = templateInstance;
        this.visibility = macro.getVisibility();    // obtain default
        this.value = nullString;
    }


    /**
     * Insert the method's description here.
     * Creation date: (1.2.2001 12:11:29)
     * @return java.lang.String
     */
    public String toString() {
        return templateInstance.getName()+com.cosylab.vdct.Constants.FIELD_SEPARATOR+macro.getName();
    }

    /**
     * Insert the method's description here.
     * Creation date: (1.2.2001 12:11:29)
     * @return java.lang.String
     */
    public String getName() {
        return macro.getName();
    }

    /**
     * Insert the method's description here.
     * Creation date: (1.2.2001 12:11:29)
     * @return java.lang.String
     */
    public String getFullName() {
        StringBuffer fullName = new StringBuffer();
        fullName.append("$(");
        fullName.append(getName());
        fullName.append(")");
        return fullName.toString();
    }

    /**
     * Insert the method's description here.
     * Creation date: (11.1.2001 21:30:04)
     * @return int
     */
    public int getVisibility()
    {
        return visibility;
    }

    /**
     * Sets the visibility.
     * @param visibility The visibility to set
     */
    public void setVisibility(int visibility)
    {
        int oldValue = this.visibility;
        this.visibility = visibility;

        if (oldValue != visibility)
        {
            boolean hasDefaultValue = hasDefaultValue();
            boolean oldVisible = (oldValue == InspectableProperty.ALWAYS_VISIBLE ||
                                 (oldValue == InspectableProperty.NON_DEFAULT_VISIBLE && !hasDefaultValue));
            boolean newVisible = (visibility == InspectableProperty.ALWAYS_VISIBLE ||
                                 (visibility == InspectableProperty.NON_DEFAULT_VISIBLE && !hasDefaultValue));
            if (oldVisible != newVisible)
            {
                Template visualTemplate = (Template)Group.getRoot().findObject(templateInstance.getName(), true);
                if (visualTemplate!=null)
                    visualTemplate.fieldVisibilityChange(this, newVisible);
            }
        }

        updateInspector();
    }

    /**
     * Insert the method's description here.
     * Creation date: (26.1.2001 15:03:07)
     * @return java.lang.String
     */
    public java.lang.String getHelp() {
        return macro.getDescription();
    }

    /**
     * @see com.cosylab.vdct.graphics.objects.Descriptable#getDescription()
     */
    public String getDescription()
    {
        return macro.getDescription();
    }

    /**
     * @see com.cosylab.vdct.graphics.objects.Descriptable#setDescription(String)
     */
    public void setDescription(String description)
    {
        //macro.setDescription(description);
    }

    /**
     * Returns the templateInstance.
     * @return VDBTemplateInstance
     */
    public VDBTemplateInstance getTemplateInstance()
    {
        return templateInstance;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9.12.2000 18:11:46)
     */
    public void updateInspector()
    {
        Template visualTemplate = (Template)Group.getRoot().findObject(templateInstance.getName(), true);
        if (visualTemplate==null) {
            //com.cosylab.vdct.Console.getInstance().println("o) Internal error: no visual representation of record "+getName()+" found.");
            return;
        }

        InspectorManager.getInstance().updateProperty(visualTemplate, this);
    }

    /**
     * Returns the macro.
     * @return VDBmacro
     */
    public VDBMacro getMacro()
    {
        return macro;
    }

    /**
     * Sets the macro.
     * @param macro The macro to set
     */
    public void setMacro(VDBMacro macro)
    {
        this.macro = macro;
    }

/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 */
public String getToolTipText()
{
    return null;
}

/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.util.regex.Pattern
 */
public Pattern getEditPattern()
{
    return null;
}

/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:28:51)
 * @return boolean
 */
public boolean isValid()
{
    return true;
}

/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:47:04)
 * @return java.lang.String[]
 */
public java.lang.String[] getSelectableValues() {
    return null;
}

/**
 * @see com.cosylab.vdct.inspector.InspectableProperty#isEditable()
 */
public boolean isEditable() {
    return true;
}

/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:29:48)
 * @return java.lang.String
 */
public String getInitValue()
{
    return null;
}
/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 16:08:45)
 * @return boolean
 */
public boolean hasDefaultValue() {
    return getValue().length() == 0;
}


/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newValue java.lang.String
 */
public void setValue(java.lang.String newValue) {

    boolean oldVisible = (getVisibility() == InspectableProperty.ALWAYS_VISIBLE ||
                         (getVisibility() == InspectableProperty.NON_DEFAULT_VISIBLE && !hasDefaultValue()));

    // buffer value
    super.setValue(newValue);

    boolean newVisible = (getVisibility() == InspectableProperty.ALWAYS_VISIBLE ||
                         (getVisibility() == InspectableProperty.NON_DEFAULT_VISIBLE && !hasDefaultValue()));

    Template visualTemplate = null;
    if (oldVisible != newVisible)
    {
        visualTemplate = (Template)Group.getRoot().findObject(templateInstance.getName(), true);
        if (visualTemplate!=null)
            visualTemplate.fieldVisibilityChange(this, newVisible);
    }

    // mapping to property
    updateProperty();

    // field changed
    if (visualTemplate==null)
        visualTemplate = (Template)Group.getRoot().findObject(templateInstance.getName(), true);
    if (visualTemplate!=null)
    {
        visualTemplate.fieldChanged(this);
        InspectorManager.getInstance().updateProperty(visualTemplate, this);
    }

}

/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @param newValue java.lang.String
 */
public void setValueSilently(java.lang.String newValue) {

    // buffer value
    super.setValueSilently(newValue);

    // mapping to property
    updateProperty();

}

/**
 * Insert the method's description here.
 */
private void updateProperty() {

    // mapping to properties
    if (hasDefaultValue())
        templateInstance.removeProperty(macro.getName());
    else if (!templateInstance.properties.containsKey(macro.getName()))
        templateInstance.addProperty(macro.getName(), value);
    else
        templateInstance.getProperties().put(macro.getName(), value);

}

/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:11:46)
 * @return java.lang.String
 */
public java.lang.String getValue() {
    if (!com.cosylab.vdct.plugin.debug.PluginDebugManager.isDebugState())
        return value;
    else
        return debugValue;
}

/**
 * @see com.cosylab.vdct.graphics.objects.LinkSource#getType()
 */
public int getType()
{
    return DBDConstants.DBF_TEMPLATE_MACRO;
}

}
