package org.csstudio.startuphelper.extensions;

/**
 * 
 * <code>CSSExtensionPoint</code> is the default interface of the extension
 * points to be used by the CSS startup plug-in. It doesn't provide any
 * functionality. It serves only as an identifier that something is an extension
 * point to be handled at startup/shut down of the application
 * 
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * 
 */
public interface CSSStartupExtensionPoint {
	
	public static final String NAME = "org.csstudio.startuphelper.modules";
}
