/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link SmsCommandValidator}. Parses "sms" automated action
 * command and validate its format.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class SMSCommandValidatorUnitTest {

	/**
	 * Test that submitted commands are well validated.
	 */
	@Test
	public void testValidator() {
		SmsCommandValidator cmd = new SmsCommandValidator();

		cmd.init(null);
		try {
			cmd.validate();
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		cmd.init("sms: +33 4 42 17 64 21, +33-6.03.74.36-61; +33 4 42 17 61 08");
		try {
			Assert.assertTrue(cmd.validate());
			Assert.assertNotNull(cmd.getHandler());
			Assert.assertEquals(3, cmd.getHandler().getTo().size());
			Assert.assertTrue(cmd.getHandler().getBody().isEmpty());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		cmd.init("sms: +33 6 (03) 74 36 61?body={0} Alarm raised - Water below {1} m3");
		try {
			Assert.assertTrue(cmd.validate());
			Assert.assertNotNull(cmd.getHandler());
			Assert.assertEquals(1, cmd.getHandler().getTo().size());
			Assert.assertEquals("{0} Alarm raised - Water below {1} m3", cmd.getHandler().getBody());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		cmd.init("sms: +33 4 42 17 64 21, +33-6.03.74n36-61; +33 4 42 17 61 08");
		try {
			cmd.validate();
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
}
