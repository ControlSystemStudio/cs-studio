/**
 * 
 */
package org.csstudio.dct;

public class ServiceExtension<E> {
	private String id;
	private String name;
	private E service;

	ServiceExtension(String id, String name, E service) {
		super();
		this.id = id;
		this.name = name;
		this.service = service;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public E getService() {
		return service;
	}

}