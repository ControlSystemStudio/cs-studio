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
		String st1 = "mailto:rf_support@iter.org; rf_operator@iter.org?cc=rf.ro@iter.org&subject=RF Source 1 in error&body=Major Alarm raised";
		EMailCommandValidator cmd = new EMailCommandValidator();
		cmd.init(st1);
		System.out.println(cmd.getHandler().toString());
		try {
			Assert.assertTrue(cmd.validate());
			Assert.assertTrue(cmd.isComplete());
		} catch (Exception e) {}
		
		String st2 = "mailto:rf_support@iter.org; rf_operator@iter.org";
		EMailCommandValidator cmd2 = new EMailCommandValidator();
		cmd2.init(st2);
		System.out.println(cmd2.getHandler().toString());
		try {
			Assert.assertTrue(cmd2.validate());
			Assert.assertFalse(cmd2.isComplete());
		} catch (Exception e) {}
		
		String st3 = "mailto:rf_support@iter&&.org; rf_operator@iter.org,cc=rf.ro@iter.org,subject=RF Source 1 in error,body=Major Alarm raised";
		EMailCommandValidator cmd3 = new EMailCommandValidator();
		cmd3.init(st3);
		System.out.println(cmd3.getHandler().toString());
		try {
			cmd3.validate();
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	
}
