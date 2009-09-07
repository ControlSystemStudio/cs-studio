package com.cosylab.vdct;

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

import javax.swing.*;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

/**
 * Insert the class' description here.
 * Creation date: (7.12.2001 17:15:12)
 * @author Matej Sekoranja
 */
public class RecentFilesMenu extends JMenu
{

	class RecentFileMenuItem extends JMenuItem
	{
		private static final String invalid = "<invalid>";
		private File file;
		
		public RecentFileMenuItem(File file)
		{
			super();
			this.file = file;
		}
		
		/**
		 * Returns the file.
		 * @return File
		 */
		public File getFile()
		{
			return file;
		}

		/**
		 * Sets the file.
		 * @param file The file to set
		 */
		public void setFile(File file)
		{
			this.file = file;
		}


		/**
		 * @see javax.swing.AbstractButton#getText()
		 */
		public String getText()
		{
			if (file != null)
				return file.toString();
			else
				return invalid;
		}

	}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:08:53)
 * @param
 */
public RecentFilesMenu()
{
	setEnabled(false);
	setText("Recent files...");
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:09:23)
 * @param
 */
public void addFile(File file)
{
	addFile(file, false);
}

/**
 * Insert the method's description here.
 * Creation date: (7.12.2001 17:09:23)
 * @param
 */
public synchronized void addFile(File file, boolean makeLast)
{
	
	RecentFileMenuItem mi = null;

	for (int i = 0; i < getItemCount(); i++)
	{
		RecentFileMenuItem rfmi = (RecentFileMenuItem)getItem(i);
		if (rfmi.getFile().equals(file))
		{
			remove(i);
			mi = rfmi;
		}
	}
	
	// none found, create a new item
	if (mi == null)
	{
		mi = new RecentFileMenuItem(file);
		
		ActionListener[] ais = getActionListeners();
		for (int i = 0; i < ais.length; i++)
			mi.addActionListener(ais[i]);
	}
	
	
	// add new one at the top
	if (!makeLast)
		insert(mi, 0);
	else
		add(mi);
	
	// remove last
	if (getItemCount()>Constants.MAX_RECENT_FILES)
		remove(getItemCount()-1);

	if (getItemCount()>0)	
		setEnabled(true);
}

/**
 * Returns the files.
 * @return Vector
 */
public ArrayList getFiles()
{
	ArrayList list = new ArrayList();
	
	for (int i = 0; i < getItemCount(); i++)
	{
		RecentFileMenuItem rfmi = (RecentFileMenuItem)getItem(i);
		list.add(rfmi.getFile());
	}
	
	return list;
}

}
