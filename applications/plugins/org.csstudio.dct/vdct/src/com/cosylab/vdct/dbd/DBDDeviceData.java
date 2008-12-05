package com.cosylab.vdct.dbd;

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
 * This type was created in VisualAge.
 */
public class DBDDeviceData {
	protected String record_type;
	protected String link_type;
	protected String dset_name;
	protected String choice_string;
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:23:28)
 * @return java.lang.String
 */
public java.lang.String getChoice_string() {
	return choice_string;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:23:28)
 * @return java.lang.String
 */
public java.lang.String getDset_name() {
	return dset_name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:23:28)
 * @return java.lang.String
 */
public java.lang.String getLink_type() {
	return link_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:23:28)
 * @return java.lang.String
 */
public java.lang.String getRecord_type() {
	return record_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:23:28)
 * @param newChoice_string java.lang.String
 */
public void setChoice_string(java.lang.String newChoice_string) {
	choice_string = newChoice_string;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:23:28)
 * @param newDset_name java.lang.String
 */
public void setDset_name(java.lang.String newDset_name) {
	dset_name = newDset_name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:23:28)
 * @param newLink_type java.lang.String
 */
public void setLink_type(java.lang.String newLink_type) {
	link_type = newLink_type;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 16:23:28)
 * @param newRecord_type java.lang.String
 */
public void setRecord_type(java.lang.String newRecord_type) {
	record_type = newRecord_type;
}
}
