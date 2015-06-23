/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.util;

/**
 * SWT Constants. Some SWT constants doesn't exist in org.eclipse.rap.ui, but exist
 * in org.eclipse.draw2d.rap.swt. So I copy them to here to achieve the compatibility.
 * @author Xihui Chen
 *
 */
public class SWTConstants {

     /**
       * Line drawing style for solid lines (value is 1).
       */
      public static final int LINE_SOLID = 1;
      /**
       * Line drawing style for dashed lines (value is 2).
       */
      public static final int LINE_DASH = 2;
      /**
       * Line drawing style for dotted lines (value is 3).
       */
      public static final int LINE_DOT = 3;
      /**
       * Line drawing style for alternating dash-dot lines (value is 4).
       */
      public static final int LINE_DASHDOT = 4;
      /**
       * Line drawing style for dash-dot-dot lines (value is 5).
       */
      public static final int LINE_DASHDOTDOT = 5;
      /**
       * Line drawing style for custom dashed lines (value is 6).
       *
       * @see org.eclipse.swt.graphics.GC#setLineDash(int[])
       * @see org.eclipse.swt.graphics.GC#getLineDash()
       * @since 3.1
       */
      public static final int LINE_CUSTOM = 6;


      /**
       * The <code>Image</code> constructor argument indicating that the new image
       * should have the appearance of a "disabled" (using the platform's rules for
       * how this should look) copy of the image provided as an argument (value is
       * 1).
       */
      public static final int IMAGE_DISABLE = 1;
      /**
       * The <code>Image</code> constructor argument indicating that the new image
       * should have the appearance of a "gray scaled" copy of the image provided as
       * an argument (value is 2).
       */
      public static final int IMAGE_GRAY = 2;

}
