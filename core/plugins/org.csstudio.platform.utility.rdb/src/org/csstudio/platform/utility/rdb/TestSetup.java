/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

@SuppressWarnings("nls")
public interface TestSetup
{
    /** Default database URL */
    final public static String URL =
    // SNS Test w/ write access
    "jdbc:oracle:thin:chan_arch1/sns@//snsdb1.sns.ornl.gov:1521/prod";
}
