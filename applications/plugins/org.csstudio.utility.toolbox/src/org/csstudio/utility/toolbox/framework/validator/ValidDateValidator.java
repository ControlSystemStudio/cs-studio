package org.csstudio.utility.toolbox.framework.validator;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidDateValidator implements ConstraintValidator<ValidDate, Date> {

	@Override
	public void initialize(ValidDate validDate) {
	}

	@Override
	public boolean isValid(Date value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return value.getTime() > 10;
	}
	
}