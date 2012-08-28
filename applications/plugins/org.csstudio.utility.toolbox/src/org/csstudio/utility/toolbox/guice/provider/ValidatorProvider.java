package org.csstudio.utility.toolbox.guice.provider;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.inject.Provider;

public class ValidatorProvider implements Provider<Validator> {

	@Override
	public Validator get() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory.getValidator();
	}

}
