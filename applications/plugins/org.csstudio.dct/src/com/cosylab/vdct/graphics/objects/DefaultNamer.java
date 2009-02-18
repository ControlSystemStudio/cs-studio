package com.cosylab.vdct.graphics.objects;

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

import java.io.File;
import java.util.Map;

import com.cosylab.vdct.util.StringUtils;
import com.cosylab.vdct.vdb.VDBTemplateInstance;

/**
 * Insert the type's description here.
 * Creation date: (27.12.2000 11:47:12)
 * @author Matej Sekoranja
 */
public class DefaultNamer implements NameManipulator {

	protected File file;
	protected String removedPrefix;
	protected String addedPrefix;
	protected Map properties;
	protected Map ports;

	/**
	 *
	 */
	public DefaultNamer(File file, String removedPrefix, String addedPrefix, Map properties, Map ports)
	{
		this.file=file;
		this.removedPrefix=removedPrefix;
		this.addedPrefix=addedPrefix;
		this.properties=properties;
		this.ports=ports;
	} 

	/**
	 * @see com.cosylab.vdct.graphics.objects.NameManipulator#getAddedPrefix()
	 */
	public String getAddedPrefix()
	{
		return addedPrefix;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.NameManipulator#getRemovedPrefix()
	 */
	public String getRemovedPrefix()
	{
		return removedPrefix;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.NameManipulator#getResolvedName(String)
	 */
	public String getResolvedName(String name)
	{
		if (removedPrefix!=null)
			name = StringUtils.removeBegining(name, removedPrefix); 
		if (addedPrefix!=null)
			name = addedPrefix + name;
		if (properties!=null)
			name = VDBTemplateInstance.applyProperties(name, properties);
		return name;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.NameManipulator#getSubstitutions()
	 */
	public Map getSubstitutions()
	{
		return properties;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.NameManipulator#getFile()
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * @see com.cosylab.vdct.graphics.objects.NameManipulator#getPorts()
	 */
	public Map getPorts()
	{
		return ports;
	}

}
