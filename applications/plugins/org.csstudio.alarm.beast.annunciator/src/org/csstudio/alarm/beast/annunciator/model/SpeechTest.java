/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import org.csstudio.utility.speech.Annunciator;
import org.csstudio.utility.speech.AnnunciatorFactory;

/** Test access to Annunciator with usual java main() routine
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SpeechTest
{
    public static void main(String[] args) throws Exception
	{
        for (int i=0; i<10; ++i)
        {
    		final Annunciator speech = AnnunciatorFactory.getAnnunciator();
    		speech.say("Hello, this is a test.");
    		speech.close();
        }
	}
}
