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
package org.csstudio.ui.util.internal.localization;

import org.eclipse.osgi.util.NLS;

/**
 * Access to the localization message ressources within this
 * plugin.
 *      
 * @author Alexander Will
 * @author Kay Kasemir
 */
//TODO: Copied from org.csstudio.platform.ui. Review is needed.
public final class Messages extends NLS {
	/**
	 * The bundle name of the localization messages ressources.
	 */
	private static final String BUNDLE_NAME = "org.csstudio.ui.util.internal.localization.messages"; //$NON-NLS-1$

    public static String AuthenticationPreferencePage_LOGIN_ON_STARTUP_OFFSITE;

	public static String CssWorkbenchAdvisor_LoggedInAs;
	public static String CssWorkbenchAdvisor_NotLoggedIn;

	public static String ExportPreferencesAction_DialogTitle;
	public static String ExportPreferencesAction_ErrorMessage;
	public static String ExportPreferencesAction_FileTypeDescription;
	public static String ExportPreferencesAction_IncludeDefaultsQuestion;

	public static String WorkbenchActionBuilder_CSS_ALARM_MENU;
	public static String WorkbenchActionBuilder_CSS_CONFIGURATION_MENU;
	public static String WorkbenchActionBuilder_CSS_DEBUGGING_MENU;
	public static String WorkbenchActionBuilder_CSS_DIAGNOSTICS_MENU;
	public static String WorkbenchActionBuilder_CSS_DISPLAY_MENU;
	public static String WorkbenchActionBuilder_CSS_EDITORS_MENU;
	public static String WorkbenchActionBuilder_CSS_MANAGEMENT_MENU;
	public static String WorkbenchActionBuilder_CSS_MENU;
	public static String WorkbenchActionBuilder_CSS_OTHER_MENU;
	public static String WorkbenchActionBuilder_CSS_TEST_MENU;
	public static String WorkbenchActionBuilder_CSS_TRENDS_MENU;
	public static String WorkbenchActionBuilder_CSS_UTILITIES_MENU;
    public static String WorkbenchActionBuilder_SHOW_VIEW;
    public static String WorkbenchActionBuilder_MENU_FILE;
    public static String WorkbenchActionBuilder_MENU_FILE_NEW;
    public static String WorkbenchActionBuilder_EXIT;
    public static String WorkbenchActionBuilder_MENU_HELP;
    public static String WorkbenchActionBuilder_OPEN_PERSPECTIVE;
    public static String WorkbenchActionBuilder_MENU_WINDOW;

	public static String Console_CONSOLE_TITLE;

    public static String JmsAppenderPreferencePage_PAGE_TITLE;
    public static String JmsAppenderPreferencePage_LOG_LEVEL;
    public static String JmsAppenderPreferencePage_PATTERN;
    public static String JmsAppenderPreferencePage_URL;
    public static String JmsAppenderPreferencePage_TOPIC;
    public static String JmsAppenderPreferencePage_USER;
    public static String JmsAppenderPreferencePage_PASSWORD;

    public static String FileAppenderPreferencePage_PAGE_TITLE;
    public static String FileAppenderPreferencePage_LOG_LEVEL;
    public static String FileAppenderPreferencePage_PATTERN;
    public static String FileAppenderPreferencePage_LOG_FILE;
    public static String FileAppenderPreferencePage_BACKUP_INDEX;

    public static String ConsoleAppenderPreferencePage_PAGE_TITLE;
    public static String ConsoleAppenderPreferencePage_LOG_LEVEL;
    public static String ConsoleAppenderPreferencePage_PATTERN;

    public static String LoggingPreferencePage_PAGE_TITLE;
    public static String LoggingPreferencePage_CONSOLE_APPENDER;
    public static String LoggingPreferencePage_FILE_APPENDER;
    public static String LoggingPreferencePage_JMS_APPENDER;

    public static String AuthenticationPreferencePage_PAGE_TITLE;
    public static String AuthenticationPreferencePage_LOGIN_ON_STARTUP;

	public static String AutoSizeColumnAction_Text;

    public static String CssWorkbenchAdvisor_WINDOW_TITLE;

    public static String LocalePreferencePage_PAGE_TITLE;
    public static String LocalePreferencePage_LOCALE;
    public static String LocalePreferencePage_CHANGE_MESSAGE;
    public static String LocalePreferencePage_DEFAULT;
    public static String LocalePreferencePage_DE;
    public static String LocalePreferencePage_EN_US;
    public static String LocalePreferencePage_EN_GB;

    public static String CSSPlatformPreferencePage_MESSAGE;
    public static String CSSApplicationsPreferencePage_MESSAGE;

    public static String TimeStampWidget_Time_Now;
    public static String TimeStampWidget_Time_Now_TT;
    public static String TimeStampWidget_Time_SelectDate;
    public static String TimeStampWidget_Time_Time;
    public static String TimeStampWidget_Time_SelectHour;
    public static String TimeStampWidget_Time_Sep;
    public static String TimeStampWidget_Time_SelectMinute;
    public static String TimeStampWidget_Time_SelectSeconds;
    
    public static String SaveAsDialog_TITLE;
    public static String SaveAsDialog_MESSAGE;
    public static String SaveAsDialog_FILE_LABEL;
    public static String SaveAsDialog_FILE;
    public static String SaveAsDialog_OVERWRITE_QUESTION;
    public static String SaveAsDialog_QUESTION;

	public static String StringTableEditor_AddRowText;
	public static String StringTableEditor_DefaultColumnHeader;
	public static String StringTableEditor_DeleteToolTip;
	public static String StringTableEditor_EditToolTip;
    public static String StringTableEditor_MoveDownToolTip;
	public static String StringTableEditor_MoveUpToolTip;

	public static String ContainerSelectionGroup_TITLE;

    public static String ResourceAndContainerGroup_PROBLEM_EMPTY;
    public static String ResourceAndContainerGroup_PROBLEM_DOES_NOT_EXIST;
    public static String ResourceAndContainerGroup_PROBLEM_FILE_ALREADY_EXISTS_AT_LOCATION;
    public static String ResourceAndContainerGroup_PROBLEM_FILE_ALREADY_EXISTS;
    public static String ResourceAndContainerGroup_PROBLEM_EMPTY_NAME;
    public static String ResourceAndContainerGroup_PROBLEM_INVALID_FILE_NAME;

	public static String RowEditDialog_ShellTitle;

    public static String DeleteResourceAction_QUESTION_TITLE;
    public static String DeleteResourceAction_QUESTION_MESSAGE;

    public static String WorkspaceExplorerView_CANNOT_OPEN_EDITOR;
    public static String WorkspaceExplorerView_ERROR_MESSAGE;
    public static String WorkspaceExplorerView_ERROR_TITLE;

    public static String WizardNewFileCreationPage_LABEL_FILE;
    public static String WizardNewFileCreationPage_ERROR_TITLE;
    public static String WizardNewFileCreationPage_LABEL_FILE_NAME;

    public static String CreateFolderAction_ERROR_TITLE;
    public static String CreateFolderAction_ERROR_MESSAGE;
    public static String CreateFolderAction_DIALOG_TITLE;
    public static String CreateFolderAction_DIALOG_MESSAGE;

    public static String CreateProjectAction_ERROR_TITLE;
    public static String CreateProjectAction_DIALOG_MESSAGE;
    public static String CreateProjectAction_ERROR_MESSAGE;
    public static String CreateProjectAction_DIALOG_TITLE;

    public static String ChooseWorkspaceDialog_PRODUCT_NAME;
    public static String ChooseWorkspaceDialog_TITLE;
    public static String ChooseWorkspaceDialog_PROBLEM_MULTIPLE_PROJECTS;
    public static String ChooseWorkspaceDialog_WINDOW_TITLE;
    public static String ChooseWorkspaceDialog_CURRENT_WORKSPACE_LABEL;
    public static String ChooseWorkspaceDialog_WORKSPACE_LABEL;
    public static String ChooseWorkspaceDialog_BROWSE_LABEL;
    public static String ChooseWorkspaceDialog_BROWSER_TITLE;
    public static String ChooseWorkspaceDialog_BROWSER_MESSAGE;
    public static String ChooseWorkspaceDialog_USE_AS_DEFAULT;
    public static String ChooseWorkspaceDialog_Error;
    public static String ChooseWorkspaceDialog_NestedError;
    public static String ChooseWorkspaceDialog_NewWorkspaceTitle;
    public static String ChooseWorkspaceDialog_NewWorkspaceWarning;

    public static String OpenWorkspaceAction_TITLE;
    public static String OpenWorkspaceAction_MESSAGE;
    public static String OpenWorkspaceAction_PROBLEM_TITLE;
    public static String OpenWorkspaceAction_PROBLEM_MESSAGE;

    public static String WorkspacePreferencePage_REFRESH_ON_STARTUP;
    public static String WorkspacePreferencePage_PROMPT_FOR_WORKSPACE;
    public static String WorkspacePreferencePage_CONFIRM_EXIT;

    public static String EmptyEditorInput_NotSaved;
    public static String EmptyEditorInput_NotSaved_TT;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
