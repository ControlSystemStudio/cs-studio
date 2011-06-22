/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;


/** Helper for merging archived samples.
 *  <p>
 *  New data is to some extend 'merged' with existing data:
 *  Where the time ranges overlap, the data replaces the old data.
 *
 *  @author Kay Kasemir
 */
public class PlotSampleMerger
{
    
     public static PlotSample[] merge(final PlotSample[] old, final PlotSample[] add) {
         final PlotSample[] A = old; 
         final PlotSample[] B = add;
         
         if (A == null) {
             return B;
         }
         if (B == null) {
             return A;
         }
         
         PlotSample C[] = new PlotSample[A.length + B.length];
         
         int a = 0;
         int b = 0;
         int c = 0;
         while (a < A.length && b < B.length) {
             if (A[a].getTime().isLessThan(B[b].getTime())) { 
                 C[c] = A[a];
                 a++;
             } else if (A[a].getTime().isGreaterThan(B[b].getTime())) {
                 C[c] = B[b];
                 b++;
             } else { // equal time stamp - no behaviour specified - last test says, take 'new', omit 'old', well then...
                 C[c] = B[b];
                 b++;
                 a++;
             }
             c++;
         }
         if (a < A.length) {
             for (int aa = a; aa < A.length; aa++, c++) {
                 C[c] = A[aa];
             }
         } else {
             for (int bb = b; bb < B.length; bb++, c++) {
                 C[c] = B[bb];
             }
         }
         
         PlotSample result[] = new PlotSample[c];
         System.arraycopy(C, 0, result, 0, c);
         return result;
     }
}
