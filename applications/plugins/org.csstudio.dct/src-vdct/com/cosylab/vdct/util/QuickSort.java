package com.cosylab.vdct.util;

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

import java.util.*;

/**
 * QuickSort algorithm (divide & conquer)
 * !!! replace qs alg with other !!!
 */

public abstract class QuickSort
{
public abstract boolean lessThan(Object oFirst, Object oSecond);      
public void sort(Object[] table) {
	sort(table, 0, table.length - 1);
}      
public void sort(Object[] table, int nLow0, int nHigh0) {
  int nLow = nLow0;
  int nHigh = nHigh0;
  Object pivot;
	  
  if (nHigh0 > nLow0) {  
	pivot = table[(nLow0 + nHigh0)/2];

   while(nLow <= nHigh) {
	 while ((nLow < nHigh0) && lessThan(table[nLow], pivot)) nLow++;
	 while ((nLow0 < nHigh) && lessThan(pivot, table[nHigh])) nHigh--;
	   
	 if (nLow <= nHigh) swap(table, nLow++, nHigh--);
   }
	  
   if (nLow0 < nHigh) sort(table, nLow0, nHigh);
   if (nLow < nHigh0) sort(table, nLow, nHigh0);
  } 
}
/**
 * This method was created in VisualAge.
 * @param e Enumeration
 */
public Object[] sortEnumeration(Enumeration e) {
	Vector tmp = new Vector();
	while (e.hasMoreElements())
		tmp.addElement(e.nextElement());

	Object[] items = new Object[tmp.size()];
	tmp.copyInto(items);
	sort(items);

	return items;
}
private static void swap(Object[] table, int i, int j) {
  Object temp = table[i]; 
  table[i] = table[j];
  table[j] = temp;
}
}
