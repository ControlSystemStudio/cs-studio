/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.File;
import java.util.logging.Logger;


/**
 * Handle the DIIRT preferences, reading them from the configuration files
 * and applying the overrides.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 4 Nov 2016
 */
@SuppressWarnings( "FinalClass" )
public final class DIIRTPreferencesHandler {

	public static final String PROP_CONFIGURATIONDIRECTORY = "configurationDirectory";
	public static final String PROP_OVERRIDE = "override";
	private static final Logger LOGGER = Logger.getLogger(DIIRTPreferencesHandler.class.getName());

	public static DIIRTPreferencesHandler get() {
		return DIIRTPreferencesHandlerInstance.SINGLETON_INSTANCE;
	}

	private File configurationDirectory = null;
	private boolean override = false;

	private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private final transient VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);

	private DIIRTPreferencesHandler() {
	}

	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener ) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void addVetoableChangeListener( VetoableChangeListener listener ) {
		vetoableChangeSupport.addVetoableChangeListener(listener);
	}

	public void addVetoableChangeListener( String propertyName, VetoableChangeListener listener ) {
		vetoableChangeSupport.addVetoableChangeListener(propertyName, listener);
	}

	/**
	 * @return The {@link File} pointing at the configuration directory, that
	 *         should contain a {@code datasource} sub-directory with the
	 *         {@code datasource.xml} file and a sub-sub-directory for each
	 *         data source supported containing the corresponding configuration
	 *         file(s).
	 *         Can be {@code null}, or pointing to a non valid directory, in
	 *         which case hard-coded defaults will be used for the configuration.
	 */
	public File getConfigurationDirectory() {
		return configurationDirectory;
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return propertyChangeSupport.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners( String propertyName ) {
		return propertyChangeSupport.getPropertyChangeListeners(propertyName);
	}

	public VetoableChangeListener[] getVetoableListeners() {
		return vetoableChangeSupport.getVetoableChangeListeners();
	}

	public VetoableChangeListener[] getVetoableListeners( String propertyName ) {
		return vetoableChangeSupport.getVetoableChangeListeners(propertyName);
	}

	public boolean hasPropertyChangeListeners( String propertyName ) {
		return propertyChangeSupport.hasListeners(propertyName);
	}

	public boolean hasVetoableListeners( String propertyName ) {
		return vetoableChangeSupport.hasListeners(propertyName);
	}

	/**
	 * @return {@code false} when all getters will return the value read from
	 *         the files configuration or the default one if no files exist,
	 *         {@code true} when the values of the corresponding fields are
	 *         returned.
	 * @see #setOverride(boolean)
	 */
	public boolean isOverride() {
		return override;
	}

	public void removePropertyChangeListener( PropertyChangeListener listener ) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener( String propertyName, PropertyChangeListener listener ) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void removeVetoableChangeListener( VetoableChangeListener listener ) {
		vetoableChangeSupport.removeVetoableChangeListener(listener);
	}

	public void removeVetoableChangeListener( String propertyName, VetoableChangeListener listener ) {
		vetoableChangeSupport.removeVetoableChangeListener(propertyName, listener);
	}

	/**
	 * @param configurationDirectory The new configuration directory. Can be
	 *                               {@code null} or pointing to an invalid
	 *                               directory.
	 * @see #getConfigurationDirectory()
	 */
	public void setConfigurationDirectory( File configurationDirectory ) {

		File oldConfigurationDirectory = this.configurationDirectory;

		this.configurationDirectory = configurationDirectory;

		propertyChangeSupport.firePropertyChange(PROP_CONFIGURATIONDIRECTORY, oldConfigurationDirectory, configurationDirectory);

	}

	/**
	 * Tell the handler what the getters will return. The property is bounded and
	 * constrained allowing, for example, to control who can change the value.
	 *
	 * @param override If {@code false}, then all getters will return the value
	 *                 read from the files configuration or the default one if
	 *                 no files exist. If {@code true}, then the values of the
	 *                 corresponding fields are returned.
	 * @throws PropertyVetoException If one of the registered
	 *                               {@link VetoableChangeListener} has vetoed
	 *                               the change.
	 */
	public void setOverride( boolean override ) throws PropertyVetoException {

		boolean oldOverride = this.override;

		vetoableChangeSupport.fireVetoableChange(PROP_OVERRIDE, oldOverride, override);

		this.override = override;

		propertyChangeSupport.firePropertyChange(PROP_OVERRIDE, oldOverride, override);

	}

	private interface DIIRTPreferencesHandlerInstance {

		DIIRTPreferencesHandler SINGLETON_INSTANCE = new DIIRTPreferencesHandler();
	}

}
