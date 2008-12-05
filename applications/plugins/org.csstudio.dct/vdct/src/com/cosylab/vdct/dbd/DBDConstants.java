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
public interface DBDConstants {
	public static final int NOT_DEFINED	= -1;
	public static final int DBF_STRING 	=  0;
	public static final int DBF_CHAR	=  1;
	public static final int DBF_UCHAR	=  2;
	public static final int DBF_SHORT	=  3;
	public static final int DBF_USHORT	=  4;
	public static final int DBF_LONG	=  5;
	public static final int DBF_ULONG	=  6;
	public static final int DBF_FLOAT	=  7;
	public static final int DBF_DOUBLE	=  8;
	public static final int DBF_ENUM	=  9;
	public static final int DBF_MENU	= 10;
	public static final int DBF_DEVICE	= 11;
	public static final int DBF_INLINK	= 12;
	public static final int DBF_OUTLINK	= 13;
	public static final int DBF_FWDLINK	= 14;
	public static final int DBF_NOACCESS= 15;

	// artificial type
	public static final int DBF_PORT	 		= Integer.MAX_VALUE-1;
	public static final int DBF_MACRO    		= Integer.MAX_VALUE-2;
	public static final int DBF_TEMPLATE_PORT	= Integer.MAX_VALUE-3;
	public static final int DBF_TEMPLATE_MACRO	= Integer.MAX_VALUE-4;
	
	public static final int DECIMAL	= 0;
	public static final int HEX		= 1;
	

	
	// lower number means higer pos. in property-window
	public static final int GUI_UNDEFINED = Integer.MAX_VALUE;
	
	public static final int GUI_COMMON	 =  0;
	public static final int GUI_LINKS	 =  1;
	public static final int GUI_INPUTS	 =  2;
	public static final int GUI_OUTPUT	 =  3;
	public static final int GUI_SCAN	 =  4;
	public static final int GUI_ALARMS 	 =  5;
	public static final int GUI_DISPLAY  =  6;
	public static final int GUI_BITS1	 =  7;
	public static final int GUI_BITS2 	 =  8;
	public static final int GUI_CALC	 =  9;
	public static final int GUI_CLOCK	 = 10;
	public static final int GUI_COMPRESS = 11;
	public static final int GUI_CONVERT  = 12;
	public static final int GUI_HIST	 = 13;
	public static final int GUI_MBB		 = 14;
	public static final int GUI_MOTOR	 = 15;
	public static final int GUI_PID		 = 16;
	public static final int GUI_PULSE	 = 17;
	public static final int GUI_SELECT	 = 18;
	public static final int GUI_SEQ1	 = 19;
	public static final int GUI_SEQ2	 = 20;
	public static final int GUI_SEQ3	 = 21;
	public static final int GUI_SUB	 	 = 22;
	public static final int GUI_TIMER	 = 23;
	public static final int GUI_WAVE	 = 24;

	public static final char quoteChar = '"';
}
