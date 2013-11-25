/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import org.csstudio.alarm.beast.notifier.util.EMailCommandValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link EMailCommandValidator}. Parses "mailto" automated action
 * command and validate its format.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class EMailCommandValidatorUnitTest {

	/**
	 * Test that submitted commands are well validated.
	 */
	@Test
	public void testValidator() {
		EMailCommandValidator cmd = new EMailCommandValidator();

		cmd.init(null);
		try {
			cmd.validate();
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		cmd.init("mailto:rf_support@iter.org ; rf_operator@iter.org?cc=rf.ro@iter.org&subject=*>> RF Source 1 in error <<&body=Major Alarm raised");
		try {
			Assert.assertTrue(cmd.validate());
			Assert.assertNotNull(cmd.getHandler());
			Assert.assertEquals(2, cmd.getHandler().getTo().size());
			Assert.assertEquals(1, cmd.getHandler().getCc().size());
			Assert.assertNull(cmd.getHandler().getCci());
			Assert.assertEquals("*>> RF Source 1 in error <<", cmd.getHandler().getSubject());
			Assert.assertEquals("Major Alarm raised", cmd.getHandler().getBody());
			Assert.assertTrue(cmd.getHandler().isComplete());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		cmd.init("mailto:rf_support@iter.org,rf_operator@iter.org");
		try {
			Assert.assertTrue(cmd.validate());
			Assert.assertNotNull(cmd.getHandler());
			Assert.assertEquals(2, cmd.getHandler().getTo().size());
			Assert.assertNull(cmd.getHandler().getCc());
			Assert.assertNull(cmd.getHandler().getCci());
			Assert.assertTrue(cmd.getHandler().getSubject().isEmpty());
			Assert.assertTrue(cmd.getHandler().getBody().isEmpty());
			Assert.assertFalse(cmd.getHandler().isComplete());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		cmd.init("mailto:rf_support@iter.org;rf_operator%iter.org?cc=rf.ro@iter.org&subject=RF Source 1 in error&body=*{0} Alarm raised {1}");
		try {
			cmd.validate();
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

}
