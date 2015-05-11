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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.cosylab.vdct.util.StringUtils;

/**
 * @author Matej
 */
public class VDBTemplateInstance implements Commentable
{
    protected String name = null;
    protected VDBTemplate template = null;
    protected Vector propertiesV = null;
    protected Hashtable properties = null;

    private String comment = null;

    /**
     * Constructor.
     */
    public VDBTemplateInstance(String name, VDBTemplate template)
    {
        this.name = name;
        this.template = template;
    }

    /**
     * Returns the properties.
     * @return Hashtable
     */
    public Hashtable getProperties()
    {
        return properties;
    }

    /**
     * Returns the properties.
     * @return Vector
     */
    public Vector getPropertiesV()
    {
        return propertiesV;
    }

    /**
     */
    public void addProperty(Object key, String value)
    {
        if (!propertiesV.contains(key))
        {
            properties.put(key, value);
            propertiesV.addElement(key);
        }
    }

    /**
     */
    public void removeProperty(Object key)
    {
        propertiesV.remove(key);
        properties.remove(key);
    }

    /**
     * Returns the template.
     * @return VDBTemplate
     */
    public VDBTemplate getTemplate()
    {
        return template;
    }

    /**
     * Sets the properties.
     * @param properties The properties to set
     */
    public void setProperties(Hashtable properties, Vector propertiesV)
    {
        this.properties = properties;
        this.propertiesV = propertiesV;
    }

    /**
     * Make macro substitutions on a string using properties table.
     * @param s The string to be applied
     * @param properties The properties to use
     * @return String
     */
    public static String applyProperties(String s, Map properties)
    {
        // TODO !!! algorithm is wrong, try applying macros on string $(macro1$(macro2))($macro2$(macro1)), where macro1="", macro2=""
        Iterator e = properties.keySet().iterator();
        while (s.indexOf('$')>=0 && e.hasNext())
        {
            String key = e.next().toString();
            String val = properties.get(key).toString();

            s = StringUtils.replace(s, "$("+key+")", val);
            s = StringUtils.replace(s, "${"+key+"}", val);

        }
        return s;
    }

    /**
     * Make port substitutions on a string using port table.
     * @param s The string to be applied
     * @param ports The ports to use
     * @return String
     */
    public static String applyPorts(String value, Map ports)
    {
        Iterator i = ports.keySet().iterator();
        while (value.indexOf('$')>=0 && i.hasNext())
        {
            String key = i.next().toString();
            String val = ports.get(key).toString();

            value = StringUtils.replace(value, key, val);

        }
        return value;
    }



/**
 * Returns the name.
 * @return String
 */
public String getName()
{
    return name;
}

/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 18:13:17)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
    name = newName;
}

    /**
     * Returns the comment.
     * @return String
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * Sets the comment.
     * @param comment The comment to set
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * Sets the template.
     * @param template The template to set
     */
    public void setTemplate(VDBTemplate template)
    {
        this.template = template;
    }

}
