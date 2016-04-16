/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email.encoder;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.junit.Test;

/** JUnit demo of the Base64Encoder.
 *  <p>
 *  Just dumps the encoded output. Unclear with what to compare the output
 *  for correctness.
 *  @author Kay Kasemir
 */
public class Base64Test
{
    @SuppressWarnings("nls")
    @Test
    public void testEncode() throws Exception
    {
        final BufferedInputStream input = new BufferedInputStream(new FileInputStream("./testfile.txt"));
        final Base64Encoder encoder = new Base64Encoder(System.out);
        encoder.encode(input);
    }
}
