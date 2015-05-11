package com.cosylab.vdct.graphics;

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

import java.io.*;

/**
 * Insert the type's description here.
 * Creation date: (4.2.2001 15:04:32)
 * @author Matej Sekoranja
 */
public interface GUIMenuInterface {
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:12:21)
 */
void moveOrigin(int direction);
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:12:21)
 */
void baseView();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:07:46)
 */
void copy();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:07:40)
 */
void cut();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:08:30)
 */
void delete();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:08:14)
 */
void group(String groupName);
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:10:27)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void importDB(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:10:27)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void importTemplateDB(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:10:27)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void importFields(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:10:27)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void importBorder(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:10:38)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void importDBD(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (29.4.2001 11:37:15)
 * @return boolean
 */
boolean isModified();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:11:41)
 */
void levelUp();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:04:49)
 */
void newCmd();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:10:15)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void openDB(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:10:50)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void openDBD(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:07:54)
 */
void paste();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:07:54)
 */
void pasteAtPosition(int pX, int pY);
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:07:19)
 */
void print();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:07:33)
 */
void redo();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:08:06)
 */
void rename();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:08:06)
 */
void rename(String oldName, String newName);

void morph();

void morph(String name, String newType);
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:48:15)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void save(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:11:04)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void saveAsGroup(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:11:04)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void saveAsTemplate(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:48:15)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void export(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:11:04)
 * @param file java.io.File
 * @exception java.io.IOException The exception description.
 */
void exportAsGroup(File file) throws IOException;
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:08:39)
 */
void selectAll();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:11:32)
 * @param state boolean
 */
void setFlatView(boolean state);
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:48:02)
 * @param scale double
 */
void setScale(double scale);
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:13:01)
 * @param state boolean
 */
void showGrid(boolean state);
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 19:54:15)
 * @param state boolean
 */
void showNavigator(boolean state);
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:57:42)
 */
void smartZoom();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:13:22)
 * @param state boolean
 */
void snapToGrid(boolean state);
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:07:26)
 */
void undo();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:08:21)
 */
void ungroup();
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:08:21)
 */
void updateGroupLabel();

void systemCopy();
void systemPaste();

}
