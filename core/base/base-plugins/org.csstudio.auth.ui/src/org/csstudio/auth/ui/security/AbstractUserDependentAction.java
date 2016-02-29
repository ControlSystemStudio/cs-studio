/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.auth.ui.security;

import org.csstudio.auth.internal.rightsmanagement.RightsManagementEvent;
import org.csstudio.auth.internal.usermanagement.IUserManagementListener;
import org.csstudio.auth.internal.usermanagement.UserManagementEvent;
import org.csstudio.auth.security.ActivationService;
import org.csstudio.auth.security.SecurityFacade;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

/**
 * This is the superclass for any Action that depends on the rights of the
 * current user. <br>
 * The subclass must be implemented by following these rules: <br>
 * <ul>
 * <li>move the codes from {@link #run()} to {@link #doWork()}</li>
 * <li>call {@link #setEnabledWithoutAuthorization(boolean)}
 * whenever the enabled state of the action regardless authorization changed.</li>
 * <li>call <code>setEnabled(SecurityFacade.getInstance().canExecute(AUTH_ID, defaultPermission))</code>
 * whenever its enabled state is supposed to become <code>true</code></li>
 * </ul>
 *
 *
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende & Stefan
 *         Hofer
 * @author Xihui Chen
 */
public abstract class AbstractUserDependentAction extends Action implements
        IUserManagementListener {

    /**
     * ID of the right necessary to execute this action.
     */
    private String _rightId;

    /**
     * Whether the action should be permitted by default.
     */
    private boolean _defaultPermission;

    /**
     *  The enabled state of the action regardless the authorization
     */
    private boolean enabledWithoutAuthorization = false;

    /**
     * The enable flag.
     */
    private boolean _enable = true;

    /**
     * Registers this action as UserManagementListener and
     * RightsManagementListener.
     *
     * @param rightId
     *            ID of the right necessary to execute this action.
     * @param defaultPermission
     *            whether this action should be permitted if no rights are
     *            configured for the action.
     */
    protected void init(final String rightId,
            final boolean defaultPermission) {
        assert rightId != null;
        _rightId = rightId;
        _defaultPermission = defaultPermission;

        SecurityFacade.getInstance().addUserManagementListener(this);
        ActivationService.getInstance().registerWidget(_rightId, null, this, new EnableActionAdapter());
        updateState();
    }

    /**
     * Constructor. Registers this action as UserManagementListener and
     * RightsManagementListener.
     *
     * @param rightId
     *            ID of the right necessary to execute this action.
     * @see Action#Action()
     */
    public AbstractUserDependentAction(final String rightId) {
        super();
        init(rightId, true);
    }


    /**
     * Constructor. Registers this action as UserManagementListener and
     * RightsManagementListener.
     * @param rightID
     *            ID of the right necessary to execute this action.
     * @param defaultPermission
     *            whether this action should be permitted if no rights are
     *            configured for the action.
     * @see Action#Action()
     */
    public AbstractUserDependentAction(final String rightID,
            final boolean defaultPermission) {
        super();
        init(rightID,defaultPermission);
    }

    /**
     * Constructor. Registers this action as UserManagementListener and
     * RightsManagementListener.
     * @param text
     *               the string used as the text for the action, or null if there is no text
     * @param rightID
     *            ID of the right necessary to execute this action.
     * @param defaultPermission
     *            whether this action should be permitted if no rights are
     *            configured for the action.
     * @see Action#Action(String)
     */
    public AbstractUserDependentAction(String text, final String rightID,
            final boolean defaultPermission) {
        super(text);
        init(rightID,defaultPermission);
    }

    /**
     * Constructor. Registers this action as UserManagementListener and
     * RightsManagementListener.
     * @param text
     *            the action's text, or <code>null</code> if there is no text
     * @param image
     *            the action's image, or <code>null</code> if there is no
     *            image     *
     * @param rightID
     *            ID of the right necessary to execute this action.
     * @param defaultPermission
     *            whether this action should be permitted if no rights are
     *            configured for the action.
     * @see Action#Action(String, ImageDescriptor)
     */
    public AbstractUserDependentAction(String text, ImageDescriptor image, final String rightID,
            final boolean defaultPermission) {
        super(text,image);
        init(rightID,defaultPermission);
    }

    /**
     * Constructor. Registers this action as UserManagementListener and
     * RightsManagementListener.
     * @param text
     *            the action's text, or <code>null</code> if there is no text
     * @param style
     *            one of <code>AS_PUSH_BUTTON</code>,
     *            <code>AS_CHECK_BOX</code>, <code>AS_DROP_DOWN_MENU</code>,
     *            <code>AS_RADIO_BUTTON</code>, and
     *            <code>AS_UNSPECIFIED</code>.
     * @param rightID
     *            ID of the right necessary to execute this action.
     * @param defaultPermission
     *            whether this action should be permitted if no rights are
     *            configured for the action.
     * @see Action#Action(String, int)
     */
    public AbstractUserDependentAction(String text, int style, final String rightID,
            final boolean defaultPermission) {
        super(text,style);
        init(rightID,defaultPermission);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setEnabled(final boolean enabled) {
        _enable = enabled;
        updateState();
    }

    /**
     * Set the enabled state of the action regardless the authorization.
     * This function must be called whenever the enabled state of
     * the action regardless authorization changed.
     *
     * @param enabledWithoutAuthorization
     *                  The enabled state of the action regardless the authorization
     */
    protected void setEnabledWithoutAuthorization(
            boolean enabledWithoutAuthorization) {
        this.enabledWithoutAuthorization = enabledWithoutAuthorization;
    }

    /**
     * @return ID of the right necessary to execute this action.
     */
    protected final String getRightId() {
        return _rightId;
    }

    /**
     * Updates the enabled state depending on user permission.
     */
    protected void updateState() {
        boolean enableState = _enable && (enabledWithoutAuthorization || hasPermission());
        super.setEnabled(enableState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void run() {
        if (hasPermission()) {
            doWork();
        }else {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Failed",
                    "You are not authorized to perform this action!");
        }
    }

    /**
     * @return <code>true</code> if the user has the permission;
     *         <code>false</code> otherwise
     */
    public boolean hasPermission() {
        return SecurityFacade.getInstance().canExecute(getRightId(), _defaultPermission);
    }

    /**
     * This method holds the protected code. It's called by <code>run()</code>.
     */
    protected abstract void doWork();

    /**
     * @see org.csstudio.auth.internal.usermanagement.IUserManagementListener#handleUserManagementEvent(org.csstudio.auth.internal.usermanagement.UserManagementEvent)
     * @param event
     *            the UserManagementEvent to handle
     */
    @Override
    public final void handleUserManagementEvent(final UserManagementEvent event) {
        updateState();
    }

    /**
     * @see org.csstudio.auth.internal.rightsmanagement.IRightsManagementListener#handleRightsManagementEvent(org.csstudio.auth.internal.rightsmanagement.RightsManagementEvent)
     * @param event
     *            the RightsManagementEvent to handle
     */
    public final void handleRightsManagementEvent(
            final RightsManagementEvent event) {
        updateState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
        ActivationService.getInstance().unregisterObject(getRightId(), this);
        SecurityFacade.getInstance().removeUserManagementListener(this);
    }

}
