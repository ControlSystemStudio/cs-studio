package org.csstudio.rap.core.preferences;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;


/**
 * Object representing the server scope in the Eclipse preferences
 * hierarchy. Can be used as a context for searching for preference
 * values (in the IPreferenceService APIs) or for determining the
 * corrent preference node to set values in the store.
 * <p>
 * Server preferences are stored on my_product.war/plugin_customization.ini.
 * <p>
 * The path for preferences defined in the server scope hierarchy is: 
 * <code>/server/&lt;qualifier&gt;</code>
 * <p>
 * This class is not intented to be subclassed. It may be instantiated.
 *
 */
public final class ServerScope implements IScopeContext {

  /**
   * String constant (value of <code>"session"</code>) used for the 
   * scope name for the session preference scope.
   */
  public static final String SCOPE = "server"; //$NON-NLS-1$
  
  /**
   * Create and return a new session scope instance.
   */
  public ServerScope() {
    super();
  }

  public IPath getLocation() {
    return null;
  }

  public String getName() {
    return SCOPE;
  }

  public IEclipsePreferences getNode( String qualifier ) {
    IEclipsePreferences root = Platform.getPreferencesService().getRootNode();
    return ( IEclipsePreferences ) root.node( SCOPE ).node( qualifier );
  }
}
