/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 10 Nov 2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DIIRTPreferencesHandlerIT {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp ( ) throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown ( ) throws Exception {
    }

    @Test
    public void test_01_get ( ) {

        DIIRTPreferencesHandler handler = DIIRTPreferencesHandler.get();

        assertThat(handler, notNullValue());

    }

}
