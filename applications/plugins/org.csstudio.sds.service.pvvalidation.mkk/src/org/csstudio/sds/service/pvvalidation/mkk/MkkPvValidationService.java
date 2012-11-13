package org.csstudio.sds.service.pvvalidation.mkk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationService;
import org.csstudio.platform.simpledal.IValidationProcess;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback.ValidationResult;


public class MkkPvValidationService implements IProcessVariableAddressValidationService {

	private static ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Override
	public String getServiceName() {
		return "MKK process validation service.";
	}

	@Override
	public String getServiceDescription() {
		return "Process validation with oracle DB from MKK";
	}

	@Override
	public IValidationProcess validateProcessVariableAddresses(
			List<IProcessVariableAddress> pvAddresses,
			IProcessVariableAddressValidationCallback callback) {

		final List<Future<?>> submittedValidations = new ArrayList<Future<?>>(pvAddresses.size());
		
		for (IProcessVariableAddress iPvAdress : pvAddresses) {
			final Future<?> submittedValidation = executor.submit(new ValidationRunnable(iPvAdress, callback));
			
		}
		
		return new IValidationProcess() {
			
			@Override
			public void cancel() {
				for (Future<?> ivalidationProcess : submittedValidations) {
					ivalidationProcess.cancel(true);
				}
				
			}
		};
	}
	
	private class ValidationRunnable implements Runnable {
		
		private IProcessVariableAddress _pvAdress;
		private IProcessVariableAddressValidationCallback _callback;

		public ValidationRunnable(IProcessVariableAddress pvAdress, IProcessVariableAddressValidationCallback callback) {
			_pvAdress = pvAdress;
			_callback = callback;
		}

		@Override
		public void run() {
			ValidationResult validationResult = null;
			String comment = null;
			
			MkkDbExample validator = new MkkDbExample();
			validationResult = validator.checkPv(_pvAdress);
			
			_callback.onValidate(_pvAdress, validationResult, comment);
		}
		
		
	}

}
