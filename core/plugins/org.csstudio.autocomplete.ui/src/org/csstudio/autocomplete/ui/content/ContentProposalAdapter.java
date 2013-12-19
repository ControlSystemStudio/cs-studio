/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Hannes Erven <hannes@erven.at> - Bug 293841 - [FieldAssist] NumLock keyDown event should not close the proposal popup [with patch]
 *     ITER - Adapted to fit CSS auto-completion requirements
 *******************************************************************************/
package org.csstudio.autocomplete.ui.content;

import java.util.List;

import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.tooltips.TooltipData;
import org.csstudio.autocomplete.ui.IAutoCompleteProposalProvider;
import org.csstudio.autocomplete.ui.history.AutoCompleteHistory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.IControlContentAdapter2;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * ContentProposalAdapter can be used to attach content proposal behavior to a
 * control. This behavior includes obtaining proposals, opening a popup dialog,
 * managing the content of the control relative to the selections in the popup.
 */
public class ContentProposalAdapter {

	/**
	 * Flag that controls the printing of debug info.
	 */
	public static final boolean DEBUG = false;

	/*
	 * The object that provides content proposals.
	 */
	private IAutoCompleteProposalProvider proposalProvider;

	/*
	 * The control for which content proposals are provided.
	 */
	private Control control;

	/*
	 * The adapter used to extract the String contents from an arbitrary
	 * control.
	 */
	private IControlContentAdapter controlContentAdapter;

	/*
	 * The popup used to show proposals.
	 */
	private ContentProposalPopup popup;

	/*
	 * The keystroke that signifies content proposals should be shown.
	 */
	private KeyStroke triggerKeyStroke;

	/*
	 * The String containing characters that auto-activate the popup.
	 */
	private String autoActivateString;

	/*
	 * A boolean that indicates whether key events received while the proposal
	 * popup is open should also be propagated to the control. Default value is
	 * true.
	 */
	private boolean propagateKeys = true;

	/*
	 * The listener we install on the control.
	 */
	private Listener controlListener;

	/*
	 * The list of IContentProposalListener listeners.
	 */
	private ListenerList proposalListeners = new ListenerList();

	/*
	 * The list of IContentProposalListener2 listeners.
	 */
	private ListenerList proposalListeners2 = new ListenerList();

	/*
	 * Flag that indicates whether the adapter is enabled. In some cases,
	 * adapters may be installed but depend upon outside state.
	 */
	private boolean isEnabled = true;

	/*
	 * The delay in milliseconds used when autoactivating the popup.
	 */
	private int autoActivationDelay = 0;

	/*
	 * A boolean indicating whether a keystroke has been received. Used to see
	 * if an autoactivation delay was interrupted by a keystroke.
	 */
	private boolean receivedKeyDown;

	/*
	 * The remembered position of the insertion position. Not all controls will
	 * restore the insertion position if the proposal popup gets focus, so we
	 * need to remember it.
	 */
	private int cursorPos = -1;

	/*
	 * The remembered selection range. Not all controls will restore the
	 * selection position if the proposal popup gets focus, so we need to
	 * remember it.
	 */
	private Point selectionRange = new Point(-1, -1);
	private Point insertionRange = new Point(-1, -1);

	/*
	 * A flag that indicates that we are watching modify events.
	 */
	private boolean watchModify = false;

	/*
	 * A label provider used to display proposals in the popup, and to extract
	 * Strings from non-String proposals.
	 */
	private ILabelProvider labelProvider;

	/*
	 * History used to remenber validated control contents.
	 */
	private AutoCompleteHistory history;
	
	/*
	 * Control contents input helper.
	 */
	private ContentHelperPopup helper;

	private boolean hasSelectedTopProposal = false;
	private boolean preventTopProposalSelection = false;
	private Proposal selectedTopProposal = null;

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for the field.
	 * 
	 * @param control the control for which the adapter is providing content
	 *            assist. May not be <code>null</code>.
	 * @param controlContentAdapter the <code>IControlContentAdapter</code> used
	 *            to obtain and update the control's contents as proposals are
	 *            accepted. May not be <code>null</code>.
	 * @param proposalProvider the <code>IAutoCompleteProposalProvider</code>
	 *            used to obtain content proposals for this control, or
	 *            <code>null</code> if no content proposal is available.
	 * @param keyStroke the keystroke that will invoke the content proposal
	 *            popup. If this value is <code>null</code>, then proposals will
	 *            be activated automatically when any of the auto activation
	 *            characters are typed.
	 * @param autoActivationCharacters An array of characters that trigger
	 *            auto-activation of content proposal. If specified, these
	 *            characters will trigger auto-activation of the proposal popup,
	 *            regardless of whether an explicit invocation keyStroke was
	 *            specified. If this parameter is <code>null</code>, then only a
	 *            specified keyStroke will invoke content proposal. If this
	 *            parameter is <code>null</code> and the keyStroke parameter is
	 *            <code>null</code>, then all alphanumeric characters will
	 *            auto-activate content proposal.
	 */
	public ContentProposalAdapter(Control control,
			IControlContentAdapter controlContentAdapter,
			IAutoCompleteProposalProvider proposalProvider,
			KeyStroke keyStroke, char[] autoActivationCharacters) {
		super();
		// We always assume the control and content adapter are valid.
		Assert.isNotNull(control);
		Assert.isNotNull(controlContentAdapter);
		this.control = control;
		this.controlContentAdapter = controlContentAdapter;

		// The rest of these may be null
		this.proposalProvider = proposalProvider;
		this.triggerKeyStroke = keyStroke;
		if (autoActivationCharacters != null) {
			this.autoActivateString = new String(autoActivationCharacters);
		}
		addControlListener(control);

		history = new AutoCompleteHistory(control, controlContentAdapter);
		helper = new ContentHelperPopup(this);
	}

	/*
	 * Add our listener to the control. Debug information to be left in until
	 * this support is stable on all platforms.
	 */
	private void addControlListener(Control control) {
		if (DEBUG) {
			System.out
					.println("ContentProposalListener#installControlListener()"); //$NON-NLS-1$
		}
		if (controlListener != null) {
			return;
		}
		controlListener = new Listener() {
			public void handleEvent(Event e) {
				if (!isEnabled) {
					return;
				}

				switch (e.type) {
				case SWT.Traverse:
				case SWT.KeyDown:
					if (DEBUG) {
						StringBuffer sb;
						if (e.type == SWT.Traverse) {
							sb = new StringBuffer("Traverse"); //$NON-NLS-1$
						} else {
							sb = new StringBuffer("KeyDown"); //$NON-NLS-1$
						}
						sb.append(" received by adapter"); //$NON-NLS-1$
						dump(sb.toString(), e);
					}
					// If the popup is open, it gets first shot at the
					// keystroke and should set the doit flags appropriately.
					if (popup != null) {
						popup.getTargetControlListener().handleEvent(e);
						if (DEBUG) {
							StringBuffer sb;
							if (e.type == SWT.Traverse) {
								sb = new StringBuffer("Traverse"); //$NON-NLS-1$
							} else {
								sb = new StringBuffer("KeyDown"); //$NON-NLS-1$
							}
							sb.append(" after being handled by popup"); //$NON-NLS-1$
							dump(sb.toString(), e);
						}
						// If the popup is open and this is a valid character,
						// we want to watch for the modified text.
						if (propagateKeys && e.character != 0)
							watchModify = true;

						return;
					}

					// We were only listening to traverse events for the popup
					if (e.type == SWT.Traverse) {
						return;
					}
					
					if (e.keyCode == SWT.ARROW_RIGHT) {
						int pos = getControlContentAdapter().
								getCursorPosition(getControl());
						String contents = getControlContentAdapter()
								.getControlContents(getControl());
						if (pos == contents.length()) {
							e.doit = false;
							openProposalPopup(false);
							return;
						}
					}

					// The popup is not open. We are looking at keydown events
					// for a trigger to open the popup.
					if (triggerKeyStroke != null) {
						// Either there are no modifiers for the trigger and we
						// check the character field...
						if ((triggerKeyStroke.getModifierKeys() == KeyStroke.NO_KEY && triggerKeyStroke
								.getNaturalKey() == e.character) ||
						// ...or there are modifiers, in which case the keycode
						// and state must match
								(triggerKeyStroke.getNaturalKey() == e.keyCode && ((triggerKeyStroke
										.getModifierKeys() & e.stateMask) == triggerKeyStroke
										.getModifierKeys()))) {
							// We never propagate the keystroke for an explicit
							// keystroke invocation of the popup
							e.doit = false;
							openProposalPopup(false);
							return;
						}
					}
					/*
					 * The triggering keystroke was not invoked. If a character
					 * was typed, compare it to the autoactivation characters.
					 */
					if (e.character != 0) {
						if (autoActivateString != null) {
							if (autoActivateString.indexOf(e.character) >= 0) {
								// Prevent weird behavior with top proposals
								if (e.character == '\b')
									setPreventTopProposalSelection(true);
								autoActivate();
							} else {
								// No autoactivation occurred, so record the key
								// down as a means to interrupt any
								// autoactivation that is pending due to
								// autoactivation delay.
								receivedKeyDown = true;
								// watch the modify so we can close the popup in
								// cases where there is no longer a trigger
								// character in the content
								watchModify = true;
							}
						} else {
							// The autoactivate string is null. If the trigger
							// is also null, we want to act on any modification
							// to the content. Set a flag so we'll catch this
							// in the modify event.
							if (triggerKeyStroke == null) {
								watchModify = true;
							}
						}
					} else {
						// A non-character key has been pressed. Interrupt any
						// autoactivation that is pending due to autoactivation
						// delay.
						receivedKeyDown = true;
					}
					break;

				// There are times when we want to monitor content changes
				// rather than individual keystrokes to determine whether
				// the popup should be closed or opened based on the entire
				// content of the control.
				// The watchModify flag ensures that we don't autoactivate if
				// the content change was caused by something other than typing.
				case SWT.Modify:
					if (allowsAutoActivate() && watchModify) {
						if (DEBUG) {
							dump("Modify event triggers popup open or close", e); //$NON-NLS-1$
						}
						watchModify = false;
						// We are in autoactivation mode, either for specific
						// characters or for all characters. In either case,
						// we should close the proposal popup when there is no
						// content in the control.
						if (isControlContentEmpty()) {
							closeProposalPopup();
						} else {
							// Given that we will close the popup when there are
							// no valid proposals, we must consider reopening it
							// on any content change when there are no
							// particular autoActivation characters
							if (autoActivateString == null) {
								autoActivate();
							} else {
								// Autoactivation characters are defined, but
								// this modify event does not involve one of
								// them. See if any of the autoactivation
								// characters are left in the content
								// and close the popup if none remain.
								if (!shouldPopupRemainOpen())
									closeProposalPopup();
							}
						}
					}
					break;
				default:
					break;
				}
			}

			/**
			 * Dump the given events to "standard" output.
			 */
			private void dump(String who, Event e) {
				StringBuffer sb = new StringBuffer(
						"--- [ContentProposalAdapter]\n"); //$NON-NLS-1$
				sb.append(who);
				sb.append(" - e: keyCode=" + e.keyCode + hex(e.keyCode)); //$NON-NLS-1$
				sb.append("; character=" + e.character + hex(e.character)); //$NON-NLS-1$
				sb.append("; stateMask=" + e.stateMask + hex(e.stateMask)); //$NON-NLS-1$
				sb.append("; doit=" + e.doit); //$NON-NLS-1$
				sb.append("; detail=" + e.detail + hex(e.detail)); //$NON-NLS-1$
				sb.append("; widget=" + e.widget); //$NON-NLS-1$
				System.out.println(sb);
			}

			private String hex(int i) {
				return "[0x" + Integer.toHexString(i) + ']'; //$NON-NLS-1$
			}
		};
		control.addListener(SWT.KeyDown, controlListener);
		control.addListener(SWT.Traverse, controlListener);
		control.addListener(SWT.Modify, controlListener);

		if (DEBUG) {
			System.out
					.println("ContentProposalAdapter#installControlListener() - installed"); //$NON-NLS-1$
		}
	}

	/*
	 * Return whether this adapter is configured for autoactivation, by specific
	 * characters or by any characters.
	 */
	private boolean allowsAutoActivate() {
		// there are specific autoactivation chars supplied
		// OR we autoactivate on everything
		return (autoActivateString != null && autoActivateString.length() > 0)
				|| (autoActivateString == null && triggerKeyStroke == null);
	}

	/*
	 * Autoactivation has been triggered. Open the popup using any specified
	 * delay.
	 */
	private void autoActivate() {
		if (autoActivationDelay > 0) {
			Runnable runnable = new Runnable() {
				public void run() {
					receivedKeyDown = false;
					try {
						Thread.sleep(autoActivationDelay);
					} catch (InterruptedException e) {
					}
					if (!isValid() || receivedKeyDown) {
						return;
					}
					getControl().getDisplay().syncExec(new Runnable() {
						public void run() {
							openProposalPopup(true);
						}
					});
				}
			};
			Thread t = new Thread(runnable);
			t.start();
		} else {
			// Since we do not sleep, we must open the popup
			// in an async exec. This is necessary because
			// this method may be called in the middle of handling
			// some event that will cause the cursor position or
			// other important info to change as a result of this
			// event occurring.
			getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (isValid()) {
						openProposalPopup(true);
					}
				}
			});
		}
	}

	protected void refreshHelper() {
		if (!helper.isOpened())
			helper.open();
		else helper.refresh();
	}

	public void handleTooltipData(List<TooltipData> tooltips) {
		helper.updateData(tooltips);
	}

	/**
	 * Open the proposal popup and display the proposals provided by the
	 * proposal provider. This method returns immediately. That is, it does not
	 * wait for a proposal to be selected. This method is used by subclasses to
	 * explicitly invoke the opening of the popup. If there are no proposals to
	 * show, the popup will not open and a beep will be sounded.
	 */
	protected void openProposalPopup() {
		openProposalPopup(false);
	}

	/**
	 * Close the proposal popup without accepting a proposal. This method
	 * returns immediately, and has no effect if the proposal popup was not
	 * open. This method is used by subclasses to explicitly close the popup
	 * based on additional logic.
	 */
	protected void closeProposalPopup() {
		if (popup != null) {
			popup.close();
		}
	}
	
	/**
	 * Open the proposal popup and display the proposals provided by the
	 * proposal provider. If there are no proposals to be shown, do not show the
	 * popup. This method returns immediately. That is, it does not wait for the
	 * popup to open or a proposal to be selected.
	 * 
	 * @param autoActivated a boolean indicating whether the popup was
	 *            autoactivated. If false, a beep will sound when no proposals
	 *            can be shown.
	 */
	private void openProposalPopup(final boolean autoActivated) {
		if (isValid()) {
			if (popup == null) {
				// Check whether there are any proposals to be shown.
				getProposals(new IContentProposalSearchHandler() {
					
					@Override
					public void handleResult(
							final ContentProposalList proposalList) {
						if (isValid()) {
							getControl().getDisplay().syncExec(new Runnable() {
								public void run() {
									if (popup == null) {
										initPopup(proposalList, autoActivated);
									} else {
										popup.refreshProposals(proposalList);
									}
									if (proposalList.allResponded())
										setPreventTopProposalSelection(false);
								}
							});
						}
					}

					@Override
					public void handleTooltips(List<TooltipData> tooltips) {
						handleTooltipData(tooltips);
					}
				});
			}
		}
	}

	/*
	 * Init & open the popup on asynchronous call.
	 */
	private void initPopup(ContentProposalList proposalList,
			boolean autoActivated) {
		if (proposalList.fullLength() > 0) {
			if (DEBUG) {
				System.out.println("POPUP OPENED BY PRECEDING EVENT"); //$NON-NLS-1$
			}
			popup = new ContentProposalPopup(this, null, proposalList);
			popup.open();
			popup.getShell().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					popup = null;
				}
			});
			internalPopupOpened();
			notifyPopupOpened();
		} else if (!autoActivated) {
			getControl().getDisplay().beep();
		}
	}

	/*
	 * Record the control's cursor position.
	 */
	private void recordCursorPosition() {
		if (isValid()) {
			IControlContentAdapter adapter = getControlContentAdapter();
			cursorPos = adapter.getCursorPosition(control);
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=139063
			if (adapter instanceof IControlContentAdapter2) {
				selectionRange = ((IControlContentAdapter2) adapter)
						.getSelection(control);
			}

		}
	}

	/**
	 * Get the proposals from the proposal provider.
	 */
	public void getProposals(final IContentProposalSearchHandler listener) {
		if (proposalProvider == null || !isValid())
			return;
		if (DEBUG) {
			System.out.println(">>> obtaining proposals from provider"); //$NON-NLS-1$
		}
		recordCursorPosition();
		final String contents = getControlContentAdapter().getControlContents(getControl());
		final String contentToParse = contents.substring(0, cursorPos);
		this.insertionRange = new Point(cursorPos, cursorPos);
		hasSelectedTopProposal = false;

		// clear the helper before requesting proposals to providers
		helper.clearData();

		// Interrupt current search & start new
		proposalProvider.cancel();
		proposalProvider.getProposals(contentToParse, listener);
	}

	/**
	 * Handle hightlight of the appended control contents.
	 * 
	 * @param proposalList the list of proposals.
	 */
	public void handleTopProposals(final ContentProposalList proposalList) {
		List<Proposal> topProposals = proposalList.getTopProposalList();
		if (topProposals.size() == 2) {
			selectedTopProposal = topProposals.get(0);
			hasSelectedTopProposal = true;
			setControlContent(selectedTopProposal, false);
		}
	}

	/**
	 * Check if the control content is completed by a top proposal.
	 * 
	 * @return <code>true</code> if the control content is completed by a top
	 *         proposal.
	 */
	public boolean hasSelectedTopProposal() {
		return hasSelectedTopProposal;
	}

	public Proposal getSelectedTopProposal() {
		return selectedTopProposal;
	}

	public boolean isPreventTopProposalSelection() {
		return preventTopProposalSelection;
	}

	public void setPreventTopProposalSelection(boolean preventTopProposalSelection) {
		this.preventTopProposalSelection = preventTopProposalSelection;
	}

	/**
	 * A content proposal has been accepted. Update the control contents
	 * accordingly and notify any listeners.
	 * 
	 * @param proposal the accepted proposal
	 */
	public void proposalAccepted(final Proposal proposal,
			final boolean addToHistory) {
		hasSelectedTopProposal = false;
		setControlContent(proposal, true);

		// Add entry to history
		if (addToHistory && !(proposal.isFunction() || proposal.isPartial()))
			history.addEntry(proposal.getValue());

		// Update helper
		helper.clearData();
		helper.updateData(proposal.getTooltips());

		// TODO: remove useless stuff
		// In all cases, notify listeners of an accepted proposal.
		IContentProposal contentProposal = new IContentProposal() {
			@Override
			public String getLabel() {
				return proposal.getValue();
			}

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public int getCursorPosition() {
				return proposal.getValue().length();
			}

			@Override
			public String getContent() {
				return proposal.getValue();
			}
		};
		notifyProposalAccepted(contentProposal);
	}

	/**
	 * A content proposal has been selected. Update the control contents
	 * accordingly without notify listeners.
	 * 
	 * @param proposal the selected proposal
	 */
	public void proposalSelected(final Proposal proposal) {
		hasSelectedTopProposal = false;
		setControlContent(proposal, false);
	}

	/*
	 * Set the text content of the control to the specified text, setting the
	 * cursorPosition at the desired location within the new contents.
	 */
	private void setControlContent(final Proposal proposal, final boolean accepted) {
		if (isValid()) {
			// Should already be false, but just in case.
			watchModify = false;
			String content = getControlContentAdapter().getControlContents(getControl());
			int insertionPos = proposal.getInsertionPos();
			String before = proposal.getOriginalValue();
			if (insertionPos < before.length())
				before = before.substring(0, insertionPos);
			String value = proposal.getValue();
			String after = "";
			if (insertionRange.y < content.length())
				after = content.substring(insertionRange.y);
			int insertionLength = value.length();
			this.insertionRange.y = insertionPos + insertionLength;
			
			if ((!after.isEmpty() && !accepted) || hasSelectedTopProposal)
				this.selectionRange = new Point(this.insertionRange.x,
						this.insertionRange.y);
			else this.selectionRange = new Point(-1, -1);
			
			String text = before + value + after;
			controlContentAdapter.setControlContents(control, text, insertionRange.y);
			if (controlContentAdapter instanceof IControlContentAdapter2
					&& selectionRange.x != -1) {
				((IControlContentAdapter2) controlContentAdapter).setSelection(
						control, selectionRange);
			}
			control.forceFocus();
		}
	}

	/*
	 * A proposal has been accepted. Notify interested listeners.
	 */
	private void notifyProposalAccepted(IContentProposal proposal) {
		if (DEBUG) {
			System.out.println("Notify listeners - proposal accepted."); //$NON-NLS-1$
		}
		final Object[] listenerArray = proposalListeners.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			((IContentProposalListener) listenerArray[i])
					.proposalAccepted(proposal);
		}
	}

	/*
	 * The proposal popup has opened. Notify interested listeners.
	 */
	private void notifyPopupOpened() {
		if (DEBUG) {
			System.out.println("Notify listeners - popup opened."); //$NON-NLS-1$
		}
		final Object[] listenerArray = proposalListeners2.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			((IContentProposalListener2) listenerArray[i])
					.proposalPopupOpened(this);
		}
	}

	/*
	 * The proposal popup has closed. Notify interested listeners.
	 */
	public void notifyPopupClosed() {
		if (DEBUG) {
			System.out.println("Notify listeners - popup closed."); //$NON-NLS-1$
		}
		final Object[] listenerArray = proposalListeners2.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			((IContentProposalListener2) listenerArray[i])
					.proposalPopupClosed(this);
		}
	}

	/*
	 * Check that the control and content adapter are valid.
	 */
	private boolean isValid() {
		return control != null && !control.isDisposed()
				&& controlContentAdapter != null;
	}

	/*
	 * Return whether the control content is empty.
	 */
	private boolean isControlContentEmpty() {
		return getControlContentAdapter().getControlContents(getControl())
				.length() == 0;
	}

	/*
	 * The popup has just opened, but listeners have not yet been notified.
	 * Perform any cleanup that is needed.
	 */
	private void internalPopupOpened() {
		if (control instanceof Combo) {
			((Combo) control).setListVisible(false);
		}
	}

	/*
	 * Return whether a proposal popup should remain open. If it was
	 * autoactivated by specific characters, and none of those characters
	 * remain, then it should not remain open. This method should not be used to
	 * determine whether autoactivation has occurred or should occur, only
	 * whether the circumstances would dictate that a popup remain open.
	 */
	private boolean shouldPopupRemainOpen() {
		// If we always autoactivate or never autoactivate,
		// it should remain open
		if (autoActivateString == null || autoActivateString.length() == 0)
			return true;
		String content = getControlContentAdapter().getControlContents(
				getControl());
		for (int i = 0; i < autoActivateString.length(); i++) {
			if (content.indexOf(autoActivateString.charAt(i)) >= 0)
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the content proposal popup has the focus. This includes
	 * both the primary popup and any secondary info popup that may have focus.
	 * 
	 * @return <code>true</code> if the proposal popup or its secondary info
	 *         popup has the focus
	 */
	public boolean hasProposalPopupFocus() {
		return popup != null && popup.hasFocus();
	}

	/**
	 * Sets focus to the proposal popup. If the proposal popup is not opened,
	 * this method is ignored. If the secondary popup has focus, focus is
	 * returned to the main proposal popup.
	 */
	public void setProposalPopupFocus() {
		if (isValid() && popup != null)
			popup.getShell().setFocus();
	}

	/**
	 * Answers a boolean indicating whether the main proposal popup is open.
	 * 
	 * @return <code>true</code> if the proposal popup is open, and
	 *         <code>false</code> if it is not.
	 */
	public boolean isProposalPopupOpen() {
		if (isValid() && popup != null)
			return true;
		return false;
	}

	/**
	 * Return a boolean indicating whether the receiver is enabled.
	 * 
	 * @return <code>true</code> if the adapter is enabled, and
	 *         <code>false</code> if it is not.
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * Set the boolean flag that determines whether the adapter is enabled.
	 * 
	 * @param enabled <code>true</code> if the adapter is enabled and responding
	 *            to user input, <code>false</code> if it is ignoring user
	 *            input.
	 */
	public void setEnabled(boolean enabled) {
		// If we are disabling it while it's proposing content, close the
		// content proposal popup.
		if (isEnabled && !enabled) {
			if (popup != null) {
				popup.close();
			}
		}
		isEnabled = enabled;
	}

	/**
	 * Get the control on which the content proposal adapter is installed.
	 * 
	 * @return the control on which the proposal adapter is installed.
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Get the label provider that is used to show proposals.
	 * 
	 * @return the {@link ILabelProvider} used to show proposals, or
	 *         <code>null</code> if one has not been installed.
	 */
	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * Set the label provider that is used to show proposals. The lifecycle of
	 * the specified label provider is not managed by this adapter. Clients must
	 * dispose the label provider when it is no longer needed.
	 * 
	 * @param labelProvider the (@link ILabelProvider} used to show proposals.
	 */
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	/**
	 * Return the array of characters on which the popup is autoactivated.
	 * 
	 * @return An array of characters that trigger auto-activation of content
	 *         proposal. If specified, these characters will trigger
	 *         auto-activation of the proposal popup, regardless of whether an
	 *         explicit invocation keyStroke was specified. If this parameter is
	 *         <code>null</code>, then only a specified keyStroke will invoke
	 *         content proposal. If this value is <code>null</code> and the
	 *         keyStroke value is <code>null</code>, then all alphanumeric
	 *         characters will auto-activate content proposal.
	 */
	public char[] getAutoActivationCharacters() {
		if (autoActivateString == null) {
			return null;
		}
		return autoActivateString.toCharArray();
	}

	/**
	 * Set the array of characters that will trigger autoactivation of the
	 * popup.
	 * 
	 * @param autoActivationCharacters An array of characters that trigger
	 *            auto-activation of content proposal. If specified, these
	 *            characters will trigger auto-activation of the proposal popup,
	 *            regardless of whether an explicit invocation keyStroke was
	 *            specified. If this parameter is <code>null</code>, then only a
	 *            specified keyStroke will invoke content proposal. If this
	 *            parameter is <code>null</code> and the keyStroke value is
	 *            <code>null</code> , then all alphanumeric characters will
	 *            auto-activate content proposal.
	 * 
	 */
	public void setAutoActivationCharacters(char[] autoActivationCharacters) {
		if (autoActivationCharacters == null) {
			this.autoActivateString = null;
		} else {
			this.autoActivateString = new String(autoActivationCharacters);
		}
	}

	/**
	 * Set the delay, in milliseconds, used before any autoactivation is
	 * triggered.
	 * 
	 * @return the time in milliseconds that will pass before a popup is
	 *         automatically opened
	 */
	public int getAutoActivationDelay() {
		return autoActivationDelay;

	}

	/**
	 * Set the delay, in milliseconds, used before autoactivation is triggered.
	 * 
	 * @param delay the time in milliseconds that will pass before a popup is
	 *            automatically opened
	 */
	public void setAutoActivationDelay(int delay) {
		autoActivationDelay = delay;

	}

	/**
	 * Get the boolean that indicates whether key events (including
	 * auto-activation characters) received by the content proposal popup should
	 * also be propagated to the adapted control when the proposal popup is
	 * open.
	 * 
	 * @return a boolean that indicates whether key events (including
	 *         auto-activation characters) should be propagated to the adapted
	 *         control when the proposal popup is open. Default value is
	 *         <code>true</code>.
	 */
	public boolean getPropagateKeys() {
		return propagateKeys;
	}

	/**
	 * Set the boolean that indicates whether key events (including
	 * auto-activation characters) received by the content proposal popup should
	 * also be propagated to the adapted control when the proposal popup is
	 * open.
	 * 
	 * @param propagateKeys a boolean that indicates whether key events
	 *            (including auto-activation characters) should be propagated to
	 *            the adapted control when the proposal popup is open.
	 */
	public void setPropagateKeys(boolean propagateKeys) {
		this.propagateKeys = propagateKeys;
	}
	
	/**
	 * Return the size, in pixels, of the content proposal popup.
	 * 
	 * @return a Point specifying the last width and height, in pixels, of the
	 *         content proposal popup.
	 */
	public Point getPopupSize() {
		return popup.getPopupSize();
	}

	/**
	 * Set the size, in pixels, of the content proposal popup. This size will be
	 * used the next time the content proposal popup is opened.
	 * 
	 * @param size a Point specifying the desired width and height, in pixels,
	 *            of the content proposal popup.
	 */
	public void setPopupSize(Point size) {
		popup.setPopupSize(size);
	}

	/**
	 * Return the content adapter that can get or retrieve the text contents
	 * from the adapter's control. This method is used when a client, such as a
	 * content proposal listener, needs to update the control's contents
	 * manually.
	 * 
	 * @return the {@link IControlContentAdapter} which can update the control
	 *         text.
	 */
	public IControlContentAdapter getControlContentAdapter() {
		return controlContentAdapter;
	}

	/**
	 * Add the specified listener to the list of content proposal listeners that
	 * are notified when content proposals are chosen. </p>
	 * 
	 * @param listener the IContentProposalListener to be added as a listener.
	 *            Must not be <code>null</code>. If an attempt is made to
	 *            register an instance which is already registered with this
	 *            instance, this method has no effect.
	 * 
	 * @see org.eclipse.jface.fieldassist.IContentProposalListener
	 */
	public void addContentProposalListener(IContentProposalListener listener) {
		proposalListeners.add(listener);
	}

	/**
	 * Removes the specified listener from the list of content proposal
	 * listeners that are notified when content proposals are chosen. </p>
	 * 
	 * @param listener the IContentProposalListener to be removed as a listener.
	 *            Must not be <code>null</code>. If the listener has not already
	 *            been registered, this method has no effect.
	 * 
	 * @since 3.3
	 * @see org.eclipse.jface.fieldassist.IContentProposalListener
	 */
	public void removeContentProposalListener(IContentProposalListener listener) {
		proposalListeners.remove(listener);
	}

	/**
	 * Add the specified listener to the list of content proposal listeners that
	 * are notified when a content proposal popup is opened or closed. </p>
	 * 
	 * @param listener the IContentProposalListener2 to be added as a listener.
	 *            Must not be <code>null</code>. If an attempt is made to
	 *            register an instance which is already registered with this
	 *            instance, this method has no effect.
	 * 
	 * @since 3.3
	 * @see org.eclipse.jface.fieldassist.IContentProposalListener2
	 */
	public void addContentProposalListener(IContentProposalListener2 listener) {
		proposalListeners2.add(listener);
	}

	/**
	 * Remove the specified listener from the list of content proposal listeners
	 * that are notified when a content proposal popup is opened or closed. </p>
	 * 
	 * @param listener the IContentProposalListener2 to be removed as a
	 *            listener. Must not be <code>null</code>. If the listener has
	 *            not already been registered, this method has no effect.
	 * 
	 * @since 3.3
	 * @see org.eclipse.jface.fieldassist.IContentProposalListener2
	 */
	public void removeContentProposalListener(IContentProposalListener2 listener) {
		proposalListeners2.remove(listener);
	}

	public AutoCompleteHistory getHistory() {
		return history;
	}

	public ContentHelperPopup getHelper() {
		return helper;
	}

}
