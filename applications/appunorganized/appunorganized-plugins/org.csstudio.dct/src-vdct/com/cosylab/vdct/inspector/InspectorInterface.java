package com.cosylab.vdct.inspector;

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

/**
 * Insert the type's description here.
 * Creation date: (8.1.2001 17:43:35)
 * @author Matej Sekoranja
 */
public interface InspectorInterface {
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:40:02)
 */
public void dispose();
/**
 *
 * @return com.cosylab.vdct.inspector.Inspectable
 */
Inspectable getInspectedObject();
/**
 *
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
void inspectObject(Inspectable object);
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:48:50)
 * @return boolean
 */
public boolean isFrozen();
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:45:56)
 */
public void reinitialize();
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:45:56)
 */
public void updateObject();
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:18:35)
 * @param help java.lang.String
 */
void setHelp(String help);
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:48:51)
 * @param state boolean
 */
public void setVisible(boolean state);
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 15:13:26)
 */
void updateComment();
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:46:28)
 */
public void updateObjectList();
/**
 *
 * @param property com.cosylab.vdct.inspector.InspectableProperty
 */
void updateProperty(InspectableProperty property);
/**
 */
int getMode();
}
