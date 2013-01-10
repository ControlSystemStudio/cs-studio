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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MkkPvValidationService implements IProcessVariableAddressValidationService {

	private static ExecutorService executor = Executors.newSingleThreadExecutor();
	
    private static final Logger LOG = LoggerFactory.getLogger(MkkPvValidationService.class);

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

		LOG.debug("MKK pv validation service, number of records: " + pvAddresses.size());

		final Future<?> submittedValidation = executor.submit(new ValidationRunnable(pvAddresses, callback));
		
		return new IValidationProcess() {
			
			@Override
			public void cancel() {
					submittedValidation.cancel(true);
			}
		};
	}
	
	private class ValidationRunnable implements Runnable {
		
		private IProcessVariableAddressValidationCallback _callback;
		private List<IProcessVariableAddress> _pvAdresses;

		public ValidationRunnable(List<IProcessVariableAddress> pvAddresses, IProcessVariableAddressValidationCallback callback) {
			_pvAdresses = pvAddresses;
			_callback = callback;
		}

		@Override
		public void run() {
			MkkDbExample validator = new MkkDbExample();
			validator.checkPv(_pvAdresses, _callback);
		}
	}
}
