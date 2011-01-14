/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.formula;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.apputil.ui.formula.messages"; //$NON-NLS-1$
    
    public static String Formula_EmptyFormulaError;
    public static String Formula_ParsedFormulaFmt;
    public static String Formula_Title;
    public static String Formula_Formula;
    public static String Formula_Formula_TT;
    public static String Formula_Inputs;
    public static String Formula_InputsTT;
    public static String Formula_AddVar;
    public static String Formula_AddVar_TT;
    public static String Formula_Functions;
    public static String Formula_sqrt_TT;
    public static String Formula_pwr_TT;
    public static String Formula_exp_TT;
    public static String Formula_sin_TT;
    public static String Formula_asin_TT;
    public static String Formula_log_TT;
    public static String Formula_log10_TT;
    public static String Formula_cos_TT;
    public static String Formula_acos_TT;
    public static String Formula_abs_TT;
    public static String Formula_floor_TT;
    public static String Formula_ceil_TT;
    public static String Formula_tan_TT;
    public static String Formula_atan_TT;
    public static String Formula_atan2_TT;
    public static String Formula_if_else_TT;
    public static String Formula_PI_TT;
    public static String Formula_E_TT;
    public static String Formula_min_TT;
    public static String Formula_max_TT;
    public static String Formula_BasicCalcs;
    public static String Formula_Clear;
    public static String Formula_Clear_TT;
    public static String Formula_Open_TT;
    public static String Formula_Close_TT;
    public static String Formula_Backspace;
    public static String Formula_Backspace_TT;
    public static String Formula_7_TT;
    public static String Formula_8_TT;
    public static String Formula_9_TT;
    public static String Formula_Mult_TT;
    public static String Formula_4_TT;
    public static String Formula_5_TT;
    public static String Formula_6_TT;
    public static String Formula_Div_TT;
    public static String Formula_1_TT;
    public static String Formula_2_TT;
    public static String Formula_3_TT;
    public static String Formula_Add_TT;
    public static String Formula_0_TT;
    public static String Formula_Decimal_TT;
    public static String Formula_Sub_TT;
    public static String InputName;
    public static String VariableName;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
