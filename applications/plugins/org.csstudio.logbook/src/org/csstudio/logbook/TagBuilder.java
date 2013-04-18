/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.csstudio.logbook;

/**
 * 
 * A builder for a default implementation of the {@link Tag}
 * 
 * @author shroffk
 */
public class TagBuilder {
    // Required
    private String name;
    // Optional
    private String state = null;

    public static TagBuilder tag(Tag tag) {
	if (tag.getName() == null) {
	    throw new NullPointerException("Tag name cannot be null");
	} else {
	    TagBuilder builder = new TagBuilder();
	    builder.name = tag.getName();
	    builder.state = tag.getState();
	    return builder;
	}

    }

    public static TagBuilder tag(String name) {
	if (name == null) {
	    throw new NullPointerException("Tag name cannot be null");
	} else {
	    TagBuilder builder = new TagBuilder();
	    builder.name = name;
	    return builder;
	}
    }

    public static TagBuilder tag(String name, String state) {
	TagBuilder builder = new TagBuilder();
	builder.name = name;
	builder.state = state;
	return builder;
    }

    public TagBuilder state(String state) {
	this.state = state;
	return this;
    }

    public Tag build() {
	return new TagImpl(name, state);
    }

    /**
     * A Default implementation for the {@link Tag}
     * 
     * @author shroffk
     * 
     */
    private class TagImpl implements Tag {

	private final String name;
	private final String state;

	public TagImpl(String name, String state) {
	    super();
	    this.name = name;
	    this.state = state;
	}

	@Override
	public String getName() {
	    return name;
	}

	@Override
	public String getState() {
	    return state;
	}

    }

}
