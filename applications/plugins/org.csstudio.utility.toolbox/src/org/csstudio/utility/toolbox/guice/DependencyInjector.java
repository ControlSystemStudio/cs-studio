package org.csstudio.utility.toolbox.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public enum  DependencyInjector {

	INSTANCE;

	private Injector injector;
	private  PersistService persistService;
	
	private DependencyInjector() {
		injector = Guice.createInjector(new GuiceModule(), new JpaPersistModule("desy"));
		persistService = injector.getInstance(PersistService.class);
	}

	public void startPersistService() {
      persistService.start();
	}

	public Injector getInjector() {
		return injector;
	}

}
