package org.csstudio.webopi.util;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.InfiniteLoopException;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.java.string.StringSplitter;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.rap.core.security.SecurityService;
import org.csstudio.webopi.WebOPIConstants;
import org.diirt.datasource.CompositeDataSource;
import org.diirt.datasource.CompositeDataSourceConfiguration;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.PVManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.widgets.Display;

/**
 * Utility for request related functions.
 * @author Xihui Chen
 *
 */
public class RequestUtil {



    public static boolean isSimpleMode(){
        HttpServletRequest request = RWT.getRequest();
        String mode = request.getParameter(WebOPIConstants.STARTUP);
        if (mode == null) {
            StartupParameters sp = RWT.getClient().getService(StartupParameters.class);
            mode = sp == null ? null : sp.getParameter(WebOPIConstants.STARTUP);
        }
         if(mode!=null && mode.equals(WebOPIConstants.SIMPLE_ENTRY_POINT))
             return true;
         String s = request.getServletPath();
        if(s.contains(WebOPIConstants.MOBILE_S_SERVELET_NAME)
                || s.contains(WebOPIConstants.STANDALONE_SERVELET_NAME))
            return true;
         return false;
    }

    /**
     * @return the opi path specified in URL. null if no opi parameter is specified.
     */
    public static RunnerInput getOPIPathFromRequest(){
        HttpServletRequest request = RWT.getRequest();
        String opiPath = request.getParameter(WebOPIConstants.OPI_PARAMETER ); //$NON-NLS-1$
        if (opiPath == null) {
            StartupParameters sp = RWT.getClient().getService(StartupParameters.class);
            opiPath = sp == null ? null : sp.getParameter(WebOPIConstants.OPI_PARAMETER);
        }
        if(opiPath == null){
            String referer = request.getHeader("Referer"); //$NON-NLS-1$
            if(referer != null && referer.contains("?opi=")){ //$NON-NLS-1$
                int i = referer.indexOf("?opi=")+5; //$NON-NLS-1$
                opiPath=referer.substring(i);
            }
        }
        IPath path = null;
        if(opiPath != null && !opiPath.isEmpty()){
            try {
                String newPath = MacroUtil.replaceMacros(opiPath,
                        new IMacroTableProvider() {
                            public String getMacroValue(String macroName) {
                                return PreferencesHelper.getMacros().get(
                                        macroName);

                            }
                        });
                opiPath = newPath;
            } catch (InfiniteLoopException e) {
                ErrorHandlerUtil.handleError("Failed to replace macros", e);
            }
            String[] inputs = null;
            try {
                inputs = StringSplitter.splitIgnoreInQuotes(opiPath, '|', true);
                opiPath = inputs[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(ResourceUtil.isURL(opiPath))
                path = new URLPath(opiPath);
            else{
                path = new Path(opiPath);
                if(!path.isAbsolute()){
                    IPath repoPath = PreferencesHelper.getOPIRepository();
                    if(repoPath == null)
                        path = null;
                    else
                        path=repoPath.append(path);
                }
            }
            MacrosInput macrosInput = null;
            if(inputs != null && inputs.length >1){
                int i=0;
                for(String m : inputs){
                    if(i++ ==0)
                        continue;
                    try {
                        String[] macro = StringSplitter.splitIgnoreInQuotes(m, '=', true);
                        if(macro.length == 2){
                            if(macrosInput == null)
                                macrosInput= new MacrosInput(
                                        new LinkedHashMap<String, String>(), true);
                            macrosInput.put(macro[0], macro[1]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return new RunnerInput(path, null, macrosInput);
        }

        return null;
    }

    /**Try to login.
     * @param display
     */
    public static void login(Display display) {
        if (PreferencesHelper.isWholeSiteSecured()) {
            try {
                //authentication will be done at loading opi
                if(PreferencesHelper.getUnSecuredOpiPaths() != null)
                    return;
            } catch (Exception e) {
            }

            if (!SecurityService.authenticate(display)){
                display.dispose();
                throw new RuntimeException("Failed to login.");
            }
        }
    }

    private static final String DEFAULT_DATASOURCE_NAME = "ca";

    public static void initDefaultDatasource() {
        DataSource dataSource = PVManager.getDefaultDataSource();
        if (dataSource instanceof CompositeDataSource) {
            CompositeDataSourceConfiguration conf = ((CompositeDataSource)dataSource).getConfiguration();
            conf.defaultDataSource(DEFAULT_DATASOURCE_NAME);
        }
    }

}
