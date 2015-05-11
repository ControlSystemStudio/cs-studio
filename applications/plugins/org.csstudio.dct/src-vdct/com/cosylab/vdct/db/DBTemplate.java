package com.cosylab.vdct.db;

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
import java.util.Vector;

/**
 * @author Matej
 */
public class DBTemplate extends DBComment
{
    protected String id = null;
    protected String fileName = null;
    protected String description = null;

    protected Hashtable ports = null;
    protected Vector portsV = null;

    protected Hashtable macros = null;
    protected Vector macrosV = null;

    protected boolean initialized;

    // data
    protected DBData data = null;

    /**
     * Constructor.
     */
    public DBTemplate(String id, String fileName)
    {
        this.id=id;
        this.fileName=fileName;

        ports = new Hashtable();
        portsV = new Vector();

        macros = new Hashtable();
        macrosV = new Vector();
    }


    /**
     * Returns the description.
     * @return String
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns the fileName.
     * @return String
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Returns the id.
     * @return String
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the description.
     * @param description The description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Sets the fileName.
     * @param fileName The fileName to set
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Sets the id.
     * @param id The id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }


    /**
     * Sets the data.
     * @param data The data to set
     */
    public void setData(DBData data)
    {
        this.data = data;
    }

    /**
     * Returns the initialized.
     * @return boolean
     */
    public boolean isInitialized()
    {
        return initialized;
    }

    /**
     * Sets the initialized.
     * @param initialized The initialized to set
     */
    public void setInitialized(boolean initialized)
    {
        this.initialized = initialized;
    }

    /**
     * Returns the ports.
     * @return Hashtable
     */
    public Hashtable getPorts()
    {
        return ports;
    }

    /**
     * Returns the portsV.
     * @return Vector
     */
    public Vector getPortsV()
    {
        return portsV;
    }

    /**
     */
    public void addPort(DBPort port)
    {
        if (!ports.containsKey(port.getName()))
        {
            ports.put(port.getName(), port);
            portsV.addElement(port);
        }
    }

    /**
     */
    public void addMacro(DBMacro macro)
    {
        if (!macros.containsKey(macro.getName()))
        {
            macros.put(macro.getName(), macro);
            macrosV.addElement(macro);
        }
    }

    /**
     * Returns the data.
     * @return DBData
     */
    public DBData getData()
    {
        return data;
    }

    /**
     * Returns the macros.
     * @return Hashtable
     */
    public Hashtable getMacros()
    {
        return macros;
    }

    /**
     * Returns the macrosV.
     * @return Vector
     */
    public Vector getMacrosV()
    {
        return macrosV;
    }

}
