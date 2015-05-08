package org.csstudio.utility.singlesource.rap;

import org.csstudio.rap.core.security.SecurityService;
import org.csstudio.utility.singlesource.UIHelper;
import org.eclipse.swt.widgets.Display;

public class RAPUIHelper extends UIHelper {

    public RAPUIHelper() {
    }


    @Override
    public boolean rapAuthenticate(Display display) {
        return SecurityService.authenticate(display);
    }

    @Override
    public boolean rapIsLoggedIn(Display display) {
        return SecurityService.isLoggedIn(display);
    }

}
