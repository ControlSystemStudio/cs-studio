/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 22 Dec 2016
 */
public class ExceptionUtilitiesTest {

    @Test
    public void testReducedStackTrace ( ) {

        Throwable t = new NullPointerException("A message.");

        //  Printed as a reference.
        t.printStackTrace(System.out);

        String orgcsstudio = ExceptionUtilities.reducedStackTrace(t, "org.csstudio");
        String javalangdeflect = ExceptionUtilities.reducedStackTrace(t, "java.lang.reflect");
        String orgjunitrunners = ExceptionUtilities.reducedStackTrace(t, "org.junit.runners");

        assertNull(ExceptionUtilities.reducedStackTrace(null, "java.lang.reflect"));
        assertEquals(ExceptionUtils.getStackTrace(t), ExceptionUtilities.reducedStackTrace(t, "xxx.yyy.zzz"));
        assertEquals(ExceptionUtils.getStackTrace(t), ExceptionUtilities.reducedStackTrace(t, ""));
        assertEquals(ExceptionUtils.getStackTrace(t), ExceptionUtilities.reducedStackTrace(t, null));
        assertThat(orgcsstudio, containsString("org.csstudio"));
        assertThat(javalangdeflect, containsString("org.csstudio"));
        assertThat(javalangdeflect, containsString("java.lang.reflect"));
        assertThat(orgjunitrunners, containsString("org.csstudio"));
        assertThat(orgjunitrunners, containsString("java.lang.reflect"));
        assertThat(orgjunitrunners, containsString("org.junit.runners"));
        assertThat(orgcsstudio, not(containsString("org.junit.runners")));
        assertTrue(0 <= orgcsstudio.length());
        assertTrue(orgcsstudio.length() <= javalangdeflect.length());
        assertTrue(javalangdeflect.length() <= orgjunitrunners.length());
        assertTrue(orgjunitrunners.length() <= ExceptionUtils.getStackTrace(t).length());

    }

}
