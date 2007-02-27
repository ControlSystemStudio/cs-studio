package org.csstudio.diag.interconnectionServer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class testJob extends Job {

	public testJob(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		for (int i = 0; i < 5000; i++) {
			System.out.println("nummer " + i);
		}
        System.out.println("Hello World (from a background job)");
        return Status.OK_STATUS;

	}

}
