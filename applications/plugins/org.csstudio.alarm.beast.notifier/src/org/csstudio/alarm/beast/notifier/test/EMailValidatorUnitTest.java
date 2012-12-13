/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

import org.csstudio.alarm.beast.notifier.util.EMailCommandValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link EMailCommandValidator}
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class EMailValidatorUnitTest {
	
	@Test
	public void testValidator() {
		String st1 = "mailto:rf_support@iter.org ; rf_operator@iter.org?cc=rf.ro@iter.org&subject=*>> RF Source 1 in error <<&body=Major Alarm raised";
		EMailCommandValidator cmd = new EMailCommandValidator();
		cmd.init(st1);
		try {
			Assert.assertTrue(cmd.validate());
			System.out.println(cmd.getHandler().toString());
			Assert.assertTrue(cmd.getHandler().isComplete());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String st2 = "mailto:rf_support@iter.org,rf_operator@iter.org";
		EMailCommandValidator cmd2 = new EMailCommandValidator();
		cmd2.init(st2);
		try {
			Assert.assertTrue(cmd2.validate());
			System.out.println(cmd2.getHandler().toString());
			Assert.assertFalse(cmd2.getHandler().isComplete());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String st3 = "mailto:rf_support@iter.org;rf_operator%iter.org?cc=rf.ro@iter.org&subject=RF Source 1 in error&body=*{0} Alarm raised {1}";
		EMailCommandValidator cmd3 = new EMailCommandValidator();
		cmd3.init(st3);
		try {
			cmd3.validate();
		} catch (Exception e) {
			Assert.assertTrue(true);
			System.out.println(e.getMessage());
		}
	}
	
}
