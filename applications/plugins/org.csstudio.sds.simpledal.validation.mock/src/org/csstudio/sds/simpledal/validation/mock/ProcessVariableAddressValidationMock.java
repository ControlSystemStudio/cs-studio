package org.csstudio.sds.simpledal.validation.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationCallback.ValidationResult;
import org.csstudio.platform.simpledal.IProcessVariableAddressValidationService;
import org.csstudio.platform.simpledal.IValidationProcess;

public class ProcessVariableAddressValidationMock implements
		IProcessVariableAddressValidationService {

	private static ExecutorService executor = Executors.newFixedThreadPool(4);
	private final String serviceName;

	public ProcessVariableAddressValidationMock(String serviceName) {
		this.serviceName = serviceName;
		assert serviceName != null : "Precondition failed: serviceName != null";
		
		
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String getServiceDescription() {
		return "This is a mocked service implementation";
	}

	@Override
	public IValidationProcess validateProcessVariableAddresses(
			List<IProcessVariableAddress> pvAddresses,
			IProcessVariableAddressValidationCallback callback) {

		final List<IValidationProcess> validationProcesses = new ArrayList<IValidationProcess>(
				pvAddresses.size());

		for (IProcessVariableAddress iProcessVariableAddress : pvAddresses) {
			IValidationProcess validationProcess = validateProcessVariableAddress(
					iProcessVariableAddress, callback);
			validationProcesses.add(validationProcess);
		}
		return new IValidationProcess() {

			@Override
			public void cancel() {
				for (IValidationProcess iValidationProcess : validationProcesses) {
					iValidationProcess.cancel();
				}
			}
		};
	}

	public IValidationProcess validateProcessVariableAddress(
			IProcessVariableAddress pvAddress,
			IProcessVariableAddressValidationCallback callback) {
		final Future<?> submitted = executor.submit(new ValidationRunnable(
				pvAddress, callback));

		return new IValidationProcess() {
			@Override
			public void cancel() {
				submitted.cancel(true);
			}
		};
	}

	private class ValidationRunnable implements Runnable {

		private final IProcessVariableAddress pvAddress;
		private final IProcessVariableAddressValidationCallback callback;

		public ValidationRunnable(IProcessVariableAddress pvAddress,
				IProcessVariableAddressValidationCallback callback) {
			this.pvAddress = pvAddress;
			this.callback = callback;
		}

		@Override
		public void run() {
			long waitTime = (long) (Math.random() * 400);

			ValidationResult validationResult = ValidationResult.values()[new Random()
					.nextInt(3)];
			try {
				Thread.sleep(waitTime);
				this.callback.onValidate(pvAddress, validationResult,
						validationResult.name());
			} catch (InterruptedException e) {
				this.callback.onValidate(pvAddress, ValidationResult.INVALID,
						e.getMessage());
			}
		}
	}

}
