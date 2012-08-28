package org.csstudio.utility.toolbox.framework.validator;

import java.util.Date;

import org.csstudio.utility.toolbox.func.Func1Void;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;

/**
 * Validates a date input. The validations always return OK_STATUS, since the
 * validator is just use to show the ControlDecoration. The final validation is done
 * using BeanValidation using the ValiDateValidator. 
 */
public class DateValidator implements IValidator {
	 
    private static final String ERROR_TEXT = "Please enter valid date";
    private final ControlDecoration controlDecoration;
    private final  Func1Void<IStatus> validationExecuted;
 
    // The Validator is called as soon as the data is loaded from the model.
    // Since we want to know when the first validation trigged by user input is executed, 
    // we use a counter to count the invocations of this validator.    
    private int callCounter = 0;
    
    public DateValidator(ControlDecoration controlDecoration, Func1Void<IStatus> validationExecuted) {
        super();
        this.controlDecoration = controlDecoration;
        this.validationExecuted = validationExecuted;
    }
 
    public IStatus validate(Object value) {
    	callCounter++;
        if (value instanceof java.util.Date) {
        	Date dateValue = (Date)value;
        	if (dateValue.getTime() == 0) {
                controlDecoration.show();
                controlDecoration.setDescriptionText(ERROR_TEXT);
                if (callCounter > 1) {
                	validationExecuted.apply(Status.OK_STATUS);
                }
                return ValidationStatus.OK_STATUS;
        	}
        }
        controlDecoration.hide();
        if (callCounter > 1) {
        	validationExecuted.apply(Status.OK_STATUS);
        }
        return Status.OK_STATUS;
    }
    
}