/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences.pojo;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 14 Dec 2016
 */
public class JCAContextTest {

    @Test
    public void testConstructor ( ) {

        JCAContext jcc1 = new JCAContext();

        assertEquals("localhost", jcc1.addrList);
        assertTrue(jcc1.autoAddrList);
        assertEquals(15.0, jcc1.beaconPeriod, 0.0001);
        assertEquals(30.0, jcc1.connectionTimeout, 0.0001);
        assertEquals(16384, jcc1.maxArrayBytes);
        assertEquals(5065, jcc1.repeaterPort);
        assertEquals(5064, jcc1.serverPort);

        jcc1.addrList = "qwer wert";
        jcc1.autoAddrList = false;
        jcc1.beaconPeriod = 2.345;
        jcc1.connectionTimeout = 3.456;
        jcc1.maxArrayBytes = 5678;
        jcc1.repeaterPort = 234;
        jcc1.serverPort = 123;

        JCAContext jcc2 = new JCAContext("qwer wert", false, 2.345, 3.456, 5678, 234, 123);

        assertEquals(jcc1,  jcc2);

    }

    /**
     * This test is made to fail if the structure of {@link JCAContext}
     * changed, ensuring that also the test classes are changed too.
     */
    @Test
    public void testStructure ( ) throws NoSuchFieldException, SecurityException {

        assertEquals(8, Arrays.asList(JCAContext.class.getDeclaredFields()).stream().filter(f -> !f.isSynthetic()).count());

        assertEquals(String.class, JCAContext.class.getDeclaredField("addrList").getType());
        assertEquals(boolean.class, JCAContext.class.getDeclaredField("autoAddrList").getType());
        assertEquals(double.class, JCAContext.class.getDeclaredField("beaconPeriod").getType());
        assertEquals(double.class, JCAContext.class.getDeclaredField("connectionTimeout").getType());
        assertEquals(int.class, JCAContext.class.getDeclaredField("maxArrayBytes").getType());
        assertEquals(boolean.class, JCAContext.class.getDeclaredField("pureJava").getType());
        assertEquals(int.class, JCAContext.class.getDeclaredField("repeaterPort").getType());
        assertEquals(int.class, JCAContext.class.getDeclaredField("serverPort").getType());

    }

}
