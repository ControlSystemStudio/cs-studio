package org.csstudio.rap.core.security;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.login.LoginException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class DefaultLoginModule implements javax.security.auth.spi.LoginModule {

  private static final Map USERS = new HashMap();
  {
    USERS.put( "user1", "rap" );
    USERS.put( "user2", "equinox" );
  }
  private CallbackHandler callbackHandler;
  private boolean loggedIn;
  private Subject subject;

  public DefaultLoginModule() {
  }

  public void initialize( Subject subject,
                          CallbackHandler callbackHandler,
                          Map sharedState,
                          Map options )
  {
    this.subject = subject;
    this.callbackHandler = callbackHandler;
  }

  public boolean login() throws LoginException {
    Callback label = new TextOutputCallback( TextOutputCallback.INFORMATION,
                                             "Please login! Hint: user1/rap" );
    NameCallback nameCallback = new NameCallback( "Username:" );
    PasswordCallback passwordCallback = new PasswordCallback( "Password:", false );
    try {
      callbackHandler.handle( new Callback[]{
        label, nameCallback, passwordCallback
      } );
    } catch( ThreadDeath death ) {
      LoginException loginException = new LoginException();
      loginException.initCause( death );
      throw loginException;
    } catch( Exception exception ) {
      LoginException loginException = new LoginException();
      loginException.initCause( exception );
      throw loginException;
    }
    String username = nameCallback.getName();
    String password = "";
    if( passwordCallback.getPassword() != null ) {
      password = String.valueOf( passwordCallback.getPassword() );
    }
    loggedIn = password.equals( USERS.get( username ) );
    return loggedIn;
  }

  public boolean commit() throws LoginException {
    subject.getPublicCredentials().add( USERS );
    subject.getPrivateCredentials().add( Display.getCurrent() );
    subject.getPrivateCredentials().add( SWT.getPlatform() );
    return loggedIn;
  }

  public boolean abort() throws LoginException {
    loggedIn = false;
    return true;
  }

  public boolean logout() throws LoginException {
    loggedIn = false;
    return true;
  }
}
