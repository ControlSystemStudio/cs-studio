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

import java.util.ArrayList;
import java.util.List;

import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.tooltips.TooltipData;
import org.csstudio.autocomplete.ui.AutoCompleteUIPlugin;
import org.csstudio.autocomplete.ui.util.SSTextLayout;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * The lightweight popup used to show content proposals for a text field. If
 * additional information exists for a proposal, then selecting that
 * proposal will result in the information being displayed in a secondary
 * popup.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class ContentProposalPopup extends PopupDialog {

	/*
	 * Set to <code>true</code> to use a Table with SWT.VIRTUAL. This is a
	 * workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=98585#c40
	 * The corresponding SWT bug is
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=90321
	 */
	private static final boolean USE_VIRTUAL = !Util.isMotif();

	/*
	 * Empty string.
	 */
	private static final String EMPTY = ""; //$NON-NLS-1$

	/*
	 * The delay before showing a secondary popup.
	 */
	private static final int POPUP_DELAY = 500;

	/*
	 * The minimum pixel width for the popup. May be overridden by using
	 * setInitialPopupSize.
	 */
	private static final int POPUP_MINIMUM_WIDTH = 200;
	private static final int FOOTER_MINIMUM_HEIGHT = 10;

	/*
	 * The pixel offset of the popup from the bottom corner of the control.
	 */
	private static final int POPUP_OFFSET = 3;

	/*
	 * The listener we install on the popup and related controls to determine
	 * when to close the popup. Some events (move, resize, close, deactivate)
	 * trigger closure as soon as they are received, simply because one of the
	 * registered listeners received them. Other events depend on additional
	 * circumstances.
	 */
	private final class PopupCloserListener implements Listener {
		private boolean scrollbarClicked = false;

		public void handleEvent(final Event e) {

			// If focus is leaving an important widget or the field's
			// shell is deactivating
			if (e.type == SWT.FocusOut) {
				scrollbarClicked = false;
				/*
				 * Ignore this event if it's only happening because focus is
				 * moving between the popup shells, their controls, or a
				 * scrollbar. Do this in an async since the focus is not
				 * actually switched when this event is received.
				 */
				e.display.asyncExec(new Runnable() {
					public void run() {
						if (isValid()) {
							if (scrollbarClicked || hasFocus()) {
								return;
							}
							// Workaround a problem on X and Mac, whereby at
							// this point, the focus control is not known.
							// This can happen, for example, when resizing
							// the popup shell on the Mac.
							// Check the active shell.
							Shell activeShell = e.display.getActiveShell();
							if (activeShell == getShell()
									|| (infoPopup != null && infoPopup
											.getShell() == activeShell)) {
								return;
							}
							/*
							 * System.out.println(e);
							 * System.out.println(e.display.getFocusControl());
							 * System.out.println(e.display.getActiveShell());
							 */
							close();
						}
					}
				});
				return;
			}

			// Scroll bar has been clicked. Remember this for focus event
			// processing.
			if (e.type == SWT.Selection) {
				scrollbarClicked = true;
				return;
			}
			
			if (e.type == SWT.Resize) {
				// Do not close popup on resize for web version.
				// RAP raise too many resize event
				return;
			}
			// For all other events, merely getting them dictates closure.
			close();
		}

		// Install the listeners for events that need to be monitored for
		// popup closure.
		void installListeners() {
			// Listeners on this popup's table and scroll bar
			proposalTable.addListener(SWT.FocusOut, this);
			ScrollBar scrollbar = proposalTable.getVerticalBar();
			if (scrollbar != null) {
				scrollbar.addListener(SWT.Selection, this);
			}

			// Listeners on this popup's shell
			getShell().addListener(SWT.Deactivate, this);
			getShell().addListener(SWT.Close, this);

			// Listeners on the target control
			control.addListener(SWT.MouseDoubleClick, this);
			control.addListener(SWT.MouseDown, this);
			control.addListener(SWT.Dispose, this);
			control.addListener(SWT.FocusOut, this);
			// Listeners on the target control's shell
			Shell controlShell = control.getShell();
			controlShell.addListener(SWT.Move, this);
			controlShell.addListener(SWT.Resize, this);
		}

		// Remove installed listeners
		void removeListeners() {
			if (isValid()) {
				proposalTable.removeListener(SWT.FocusOut, this);
				ScrollBar scrollbar = proposalTable.getVerticalBar();
				if (scrollbar != null) {
					scrollbar.removeListener(SWT.Selection, this);
				}

				getShell().removeListener(SWT.Deactivate, this);
				getShell().removeListener(SWT.Close, this);
			}

			if (control != null && !control.isDisposed()) {

				control.removeListener(SWT.MouseDoubleClick, this);
				control.removeListener(SWT.MouseDown, this);
				control.removeListener(SWT.Dispose, this);
				control.removeListener(SWT.FocusOut, this);

				Shell controlShell = control.getShell();
				controlShell.removeListener(SWT.Move, this);
				controlShell.removeListener(SWT.Resize, this);
			}
		}
	}

	/*
	 * The listener we will install on the target control.
	 */
	private final class TargetControlListener implements Listener {
		// Key events from the control
		public void handleEvent(Event e) {
			if (!isValid()) {
				return;
			}

			char key = e.character;

			// Traverse events are handled depending on whether the
			// event has a character.
			if (e.type == SWT.Traverse) {
				// If the traverse event contains a legitimate character,
				// then we must set doit false so that the widget will
				// receive the key event. We return immediately so that
				// the character is handled only in the key event.
				// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=132101
				if (key != 0) {
					e.doit = false;
					return;
				}
				// Traversal does not contain a character. Set doit true
				// to indicate TRAVERSE_NONE will occur and that no key
				// event will be triggered. We will check for navigation
				// keys below.
				e.detail = SWT.TRAVERSE_NONE;
				e.doit = true;
			} else {
				// Default is to only propagate when configured that way.
				// Some keys will always set doit to false anyway.
				e.doit = adapter.getPropagateKeys();
				String delete = new String(new char[] { 8 });
				String currentKey = new String(new char[] { e.character });
				if (!delete.equals(currentKey))
					handleTopProposals = true;
			}

			// No character. Check for navigation keys.
			if (key == 0) {
				int newSelection = proposalTable.getSelectionIndex();
				int visibleRows = (proposalTable.getSize().y / proposalTable
						.getItemHeight()) - 1;
				switch (e.keyCode) {
				case SWT.ARROW_UP:
					newSelection -= 1;
					if (newSelection < 0)
						newSelection = proposalTable.getItemCount() - 1;
					while (nonSelectableItems.contains(newSelection))
						if (--newSelection < 0)
							newSelection = proposalTable.getItemCount() - 1;
					// Not typical - usually we get this as a Traverse and
					// therefore it never propagates. Added for consistency.
					if (e.type == SWT.KeyDown) {
						// don't propagate to control
						e.doit = false;
					}
					break;

				case SWT.ARROW_DOWN:
					newSelection += 1;
					if (newSelection > proposalTable.getItemCount() - 1)
						newSelection = 0;
					while (nonSelectableItems.contains(newSelection))
						if (++newSelection > proposalTable.getItemCount() - 1)
							newSelection = 0;
					// Not typical - usually we get this as a Traverse and
					// therefore it never propagates. Added for consistency.
					if (e.type == SWT.KeyDown) {
						// don't propagate to control
						e.doit = false;
					}
					break;

				case SWT.PAGE_DOWN:
					newSelection += visibleRows;
					if (newSelection >= proposalTable.getItemCount())
						newSelection = proposalTable.getItemCount() - 1;
					while (nonSelectableItems.contains(newSelection))
						if (--newSelection < 0)
							newSelection = proposalTable.getItemCount() - 1;
					if (e.type == SWT.KeyDown) {
						// don't propagate to control
						e.doit = false;
					}
					break;

				case SWT.PAGE_UP:
					newSelection -= visibleRows;
					if (newSelection < 0)
						newSelection = 0;
					while (nonSelectableItems.contains(newSelection))
						if (++newSelection > proposalTable.getItemCount() - 1)
							newSelection = 0;
					if (e.type == SWT.KeyDown) {
						// don't propagate to control
						e.doit = false;
					}
					break;

				// Need to be propagated to control.
				case SWT.HOME:
				case SWT.ARROW_LEFT:
					if (e.type == SWT.Traverse) {
						e.doit = false;
					} else {
						e.doit = true;
					}
					return; // do nothing more

				case SWT.END:
					if (e.type == SWT.Traverse) {
						e.doit = false;
					} else {
						e.doit = true;
						// If the contents was completed (i.e. the end of the
						// content is behind the selection) the ARROW_RIGHT
						// shoud trigger the recompute of proposals otherwise we
						// do nothing.
						if (adapter.hasSelectedTopProposal()) {
							handleTopProposals = true;
							asyncRecomputeProposals();
						}
					}
					return;
				case SWT.ARROW_RIGHT:
					if (e.type == SWT.Traverse) {
						e.doit = false;
						// If the contents was completed (i.e. the end of the
						// content is behind the selection) the ARROW_RIGHT
						// shoud trigger the recompute of proposals otherwise we
						// do nothing.
						int pos = adapter.getControlContentAdapter()
								.getCursorPosition(control);
						String contents = adapter.getControlContentAdapter()
								.getControlContents(control);
						if (adapter.hasSelectedTopProposal()
								|| pos == contents.length()) {
							handleTopProposals = true;
							asyncRecomputeProposals();
						}
					} else {
						e.doit = true;
					}
					return;

				// Any unknown keycodes will cause the popup to close.
				// Modifier keys are explicitly checked and ignored because
				// they are not complete yet (no character).
				default:
					if (e.keyCode != SWT.CAPS_LOCK && e.keyCode != SWT.NUM_LOCK
							&& e.keyCode != SWT.MOD1 && e.keyCode != SWT.MOD2
							&& e.keyCode != SWT.MOD3 && e.keyCode != SWT.MOD4) {
						close();
					}
					return;
				}

				// If any of these navigation events caused a new selection,
				// then handle that now and return.
				if (newSelection >= 0) {
					selectProposal(newSelection);
					Proposal proposal = getSelectedProposal();
					if (proposal != null)
						adapter.proposalSelected(proposal);
				}
				return;
			}

			// key != 0
			// Check for special keys involved in cancelling, accepting, or
			// filtering the proposals.
			switch (key) {
			case SWT.ESC:
				e.doit = false;
				close();
				break;

			case SWT.LF:
			case SWT.CR:
				e.doit = true;
				Object p = getSelectedProposal();
				if (p != null) {
					acceptCurrentProposal(false);
				} else {
					close();
				}
				break;

			case SWT.TAB:
				e.doit = false;
				getShell().setFocus();
				return;

			case SWT.BS:
				// Recompute the proposals if the cursor position
				// will change (is not at 0).
				int pos = adapter.getControlContentAdapter().getCursorPosition(
						control);
				// We rely on the fact that the contents and pos do not yet
				// reflect the result of the BS. If the contents were
				// already empty, then BS should not cause
				// a recompute.
				if (pos > 0) {
					asyncRecomputeProposals();
				}
				break;

			default:
				// If the key is a defined unicode character, and not one of
				// the special cases processed above, update the filter text
				// and filter the proposals.
				if (Character.isDefined(key)) {
					// Recompute proposals after processing this event.
					asyncRecomputeProposals();
				}
				break;
			}
		}
	}

	/*
	 * Internal class used to implement the secondary popup.
	 */
	private class InfoPopupDialog extends PopupDialog {

		/*
		 * The text control that displays the text.
		 */
		private Text text;

		/*
		 * The String shown in the popup.
		 */
		private String contents = EMPTY;
		private static final int CONTENT_MAX_LENGTH = 50;

		/*
		 * Construct an info-popup with the specified parent.
		 */
		InfoPopupDialog(Shell parent) {
			super(parent, PopupDialog.HOVER_SHELLSTYLE, false, false, false,
					false, false, null, null);
		}

		/*
		 * Create a text control for showing the info about a proposal.
		 */
		protected Control createDialogArea(Composite parent) {
			text = new Text(parent, SWT.READ_ONLY | SWT.NO_FOCUS);

			// Use the compact margins employed by PopupDialog.
			GridData gd = new GridData(GridData.BEGINNING | GridData.FILL_BOTH);
			gd.horizontalIndent = 5;
			gd.verticalAlignment = SWT.CENTER;
			text.setLayoutData(gd);
			text.setText(contents);

			// since SWT.NO_FOCUS is only a hint...
			text.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent event) {
					ContentProposalPopup.this.close();
				}
			});
			return text;
		}

		/*
		 * Adjust the bounds so that we appear adjacent to our parent shell
		 */
		protected void adjustBounds() {
			Rectangle parentBounds = getParentShell().getBounds();
			Point textSize = text.getSize();
			Rectangle itemBounds = proposalTable.getItem(
					proposalTable.getSelectionIndex()).getBounds();
			int controlY = parentBounds.y + proposalTable.getBounds().y
					+ itemBounds.y + POPUP_VERTICALSPACING + 1;
			int controlWidht = textSize.x + 20;
			int controlHeight = itemBounds.height;
			Rectangle proposedBounds = null;

			// Try placing the info popup to the right
			Rectangle rightProposedBounds = new Rectangle(parentBounds.x
					+ parentBounds.width + PopupDialog.POPUP_HORIZONTALSPACING,
					controlY, controlWidht, controlHeight);
			rightProposedBounds = getConstrainedShellBounds(rightProposedBounds);

			// If it won't fit on the right, try the left
			if (rightProposedBounds.intersects(parentBounds)) {
				Rectangle leftProposedBounds = new Rectangle(parentBounds.x
						- controlWidht - POPUP_HORIZONTALSPACING - 1, controlY,
						controlWidht, controlHeight);
				leftProposedBounds = getConstrainedShellBounds(leftProposedBounds);

				// If it won't fit on the left, display on top of the item
				if (leftProposedBounds.intersects(parentBounds)) {
					rightProposedBounds.x = parentBounds.x + itemBounds.x;
					rightProposedBounds.y = controlY - itemBounds.height + 1;
					proposedBounds = rightProposedBounds;
				} else {
					// Use the proposed bounds on the left
					proposedBounds = leftProposedBounds;
				}
			} else {
				// Use the proposed bounds on the right
				proposedBounds = rightProposedBounds;
			}
			getShell().setBounds(proposedBounds);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog#getForeground()
		 */
		protected Color getForeground() {
			return control.getDisplay().getSystemColor(
					SWT.COLOR_INFO_FOREGROUND);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog#getBackground()
		 */
		protected Color getBackground() {
			return control.getDisplay().getSystemColor(
					SWT.COLOR_INFO_BACKGROUND);
		}

		/*
		 * Set the text contents of the popup.
		 */
		void setContents(String newContents) {
			if (newContents == null) {
				newContents = EMPTY;
			}
			if (newContents.length() > CONTENT_MAX_LENGTH)
				newContents = newContents.substring(0, CONTENT_MAX_LENGTH)
						+ "...";
			int crIndex = newContents.indexOf("\n");
			if (crIndex > 0)
				newContents = newContents.substring(0, crIndex);
			this.contents = newContents;
			if (text != null && !text.isDisposed()) {
				text.setText(contents);
				text.pack();
				this.adjustBounds();
			}
		}

		/*
		 * Return whether the popup has focus.
		 */
		boolean hasFocus() {
			if (text == null || text.isDisposed()) {
				return false;
			}
			return text.getShell().isFocusControl() || text.isFocusControl();
		}
	}

	/*
	 * The listener installed on the target control.
	 */
	private Listener targetControlListener;

	/*
	 * The listener installed in order to close the popup.
	 */
	private PopupCloserListener popupCloser;

	/*
	 * The table used to show the list of proposals.
	 */
	private Table proposalTable;

	/*
	 * The text used to display info under the table
	 */
	private Text footer;

	/*
	 * The proposals to be shown (cached to avoid repeated requests).
	 */
	private ContentProposalList proposalList;

	/*
	 * Secondary popup used to show detailed information about the selected
	 * proposal.
	 */
	private InfoPopupDialog infoPopup;

	/*
	 * Flag indicating whether there is a pending secondary popup update.
	 */
	private boolean pendingDescriptionUpdate = false;

	/*
	 * The desired size in pixels of the proposal popup.
	 */
	private Point popupSize;

	/*
	 * The control for which content proposals are provided.
	 */
	private Control control;

	/*
	 * A label provider used to display proposals in the popup, and to extract
	 * Strings from non-String proposals.
	 */
	// private ILabelProvider labelProvider;

	/*
	 * A flag indicating whether or not we should handle top proposals.
	 */
	private boolean handleTopProposals = false;

	private ContentProposalAdapter adapter;
	
	private Image partialContentImage;
	private Image partialContentImageSelected;
	private Image functionContentImage;
	private Image functionContentImageSelected;
	private SSTextLayout[] textLayouts;

	private final int SWTMeasureItem = 41;
	private final int SWTPaintItem = 42;
	private int maxItemWidth = 0;

	private List<Integer> nonSelectableItems;
	private Long uniqueId = Long.MIN_VALUE;

	/**
	 * Constructs a new instance of this popup, specifying the control for which
	 * this popup is showing content, and how the proposals should be obtained
	 * and displayed.
	 * 
	 * @param infoText Text to be shown in a lower info area, or
	 *        <code>null</code> if there is no info area.
	 */
	ContentProposalPopup(ContentProposalAdapter adapter, String infoText,
			ContentProposalList proposalList) {
		// IMPORTANT: Use of SWT.ON_TOP is critical here for ensuring
		// that the target control retains focus on Mac and Linux. Without
		// it, the focus will disappear, keystrokes will not go to the
		// popup, and the popup closer will wrongly close the popup.
		// On platforms where SWT.ON_TOP overrides SWT.RESIZE,
		// we will live with this.
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=126138
		super(adapter.getControl().getShell(), SWT.RESIZE | SWT.ON_TOP | SWT.NO_FOCUS, false,
				false, false, false, false, null, infoText);
		this.adapter = adapter;
		this.control = adapter.getControl();
		
		// this.labelProvider = adapter.getLabelProvider();
		this.partialContentImage = AutoCompleteUIPlugin.getDefault()
				.getImageFromPlugin(AutoCompleteUIPlugin.PLUGIN_ID,
						"icons/mglass-16.png");
		this.partialContentImageSelected = AutoCompleteUIPlugin.getDefault()
				.getImageFromPlugin(AutoCompleteUIPlugin.PLUGIN_ID,
						"icons/mglass-16-white.png");
		this.functionContentImage = AutoCompleteUIPlugin.getDefault()
				.getImageFromPlugin(AutoCompleteUIPlugin.PLUGIN_ID,
						"icons/function-16.png");
		this.functionContentImageSelected = AutoCompleteUIPlugin.getDefault()
				.getImageFromPlugin(AutoCompleteUIPlugin.PLUGIN_ID,
						"icons/function-16-white.png");
		this.nonSelectableItems = new ArrayList<Integer>();

		this.proposalList = proposalList;
		// When the popup is opened & the content is not already completed, we
		// want to handle this behaviour
		if (!adapter.isPreventTopProposalSelection())
			handleTopProposals = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.PopupDialog#getForeground()
	 */
	protected Color getForeground() {
		return JFaceResources.getColorRegistry().get(
				JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.PopupDialog#getBackground()
	 */
	protected Color getBackground() {
		return JFaceResources.getColorRegistry().get(
				JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR);
	}

	/*
	 * Creates the content area for the proposal popup. This creates a table and
	 * places it inside the composite. The table will contain a list of all the
	 * proposals.
	 * 
	 * @param parent The parent composite to contain the dialog area; must not
	 * be <code>null</code>.
	 */
	protected final Control createDialogArea(final Composite parent) {
		Composite wrapper = (Composite) super.createDialogArea(parent);
		wrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		wrapper.setLayout(new GridLayout());

		// Use virtual where appropriate (see flag definition).
		if (USE_VIRTUAL) {
			proposalTable = new Table(wrapper, SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.VIRTUAL | SWT.NO_FOCUS);
			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					handleSetData(event);
				}
			};
			proposalTable.addListener(SWT.SetData, listener);
			
			/*
			 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
			 * Therefore, it is critical for performance that these methods be
			 * as efficient as possible. NOT implemented for RAP.
			 */
			if (!AutoCompleteUIPlugin.isRAP()) {
				proposalTable.addListener(SWTPaintItem, new Listener() {
					public void handleEvent(Event event) {
						TableItem item = (TableItem) event.item;
						int index = proposalTable.indexOf(item);
						if (textLayouts != null && index < textLayouts.length
								&& textLayouts[index] != null) {
							textLayouts[index].handlePaintItemEvent(event, 20, 2);
						}
						Proposal p = (Proposal) item.getData();
						Image image = getImage(p,
								index == proposalTable.getSelectionIndex());
						if (image != null)
							event.gc.drawImage(image, event.x, event.y + 2);
					}
				});
				proposalTable.addListener(SWTMeasureItem, new Listener() {
					public void handleEvent(Event event) {
						TableItem item = (TableItem) event.item;
						int index = proposalTable.indexOf(item);
						if (textLayouts != null && index < textLayouts.length
								&& textLayouts[index] != null) {
							textLayouts[index].handleMeasureItemEvent(event);
						}
					}
				});
			}
		} else {
			proposalTable = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.NO_FOCUS);
		}

		footer = new Text(wrapper, SWT.READ_ONLY | SWT.WRAP | SWT.NO_FOCUS);
		GridData textGridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		textGridData.heightHint = FOOTER_MINIMUM_HEIGHT;
		textGridData.widthHint = 100;
		footer.setLayoutData(textGridData);

		// set the proposals to force population of the table.
		setProposals(proposalList);

		proposalTable.setHeaderVisible(false);
		proposalTable.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event e) {
				getTargetControlListener().handleEvent(e);
			}
		});
		proposalTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// If a proposal has been selected, show it in the secondary
				// popup. Otherwise close the popup.
				if (e.item == null) {
					if (infoPopup != null)
						infoPopup.close();
				} else {
					Proposal proposal = (Proposal) e.item.getData();
					if (proposal != null) {
						showProposalDescription();
						adapter.proposalSelected(proposal);
					} else {
						if (infoPopup != null)
							infoPopup.close();
						proposalTable.deselectAll();
					}
				}
			}

			// Default selection was made. Accept the current proposal.
			public void widgetDefaultSelected(SelectionEvent e) {
				Proposal proposal = (Proposal) e.item.getData();
				if (proposal != null) {
					acceptCurrentProposal(true);
				} else {
					proposalTable.deselectAll();
				}
			}
		});

		// Added to solve a item resize bug on windows:
		new TableColumn(proposalTable, SWT.NONE | SWT.NO_FOCUS);
		proposalTable.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent event) {
				if (proposalTable.getColumnCount() > 0) {
					if (proposalTable.getClientArea().width > maxItemWidth) {
						proposalTable.getColumn(0).setWidth(
								proposalTable.getClientArea().width);
					} else {
						proposalTable.getColumn(0).setWidth(maxItemWidth);
					}
				}
			}
		});

		return proposalTable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.PopupDialog.adjustBounds()
	 */
	protected void adjustBounds() {
		adjustTableBounds();
		// Now set up a listener to monitor any changes in size.
		if (AutoCompleteUIPlugin.isRAP())
			return;
		getShell().addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				popupSize = getShell().getSize();
				if (infoPopup != null) {
					infoPopup.close();
					Proposal p = getSelectedProposal();
					if (p != null) {
						showProposalDescription();
					}
				}
			}
		});
	}

	private void adjustTableBounds() {
		// Get our control's location in display coordinates.
		Point location = control.getDisplay().map(control.getParent(), null,
				control.getLocation());
		int initialX = location.x + POPUP_OFFSET;
		int initialY = location.y + control.getSize().y + POPUP_OFFSET;

		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = proposalTable.getItemHeight() * getTableLength() + 30;
		data.widthHint = Math.max(maxItemWidth,
				Math.max(control.getSize().x, POPUP_MINIMUM_WIDTH));
		proposalTable.setLayoutData(data);
		
		getShell().pack();
		popupSize = getShell().getSize();
		int scrollBarWitdh = proposalTable.getVerticalBar().getSize().x;
		Rectangle shellBounds = new Rectangle(initialX, initialY, popupSize.x + scrollBarWitdh + 30, popupSize.y);

		// Constrain to the display
		Rectangle constrainedBounds = getConstrainedShellBounds(shellBounds);
		shellBounds.x = constrainedBounds.x;
		// If there has been an adjustment causing the popup to overlap
		// with the control, then reduce the popup size.
		if (constrainedBounds.y < initialY) {
			shellBounds.height -= initialY - constrainedBounds.y;
		}

		getShell().setBounds(shellBounds);
	}

	private void initializeTextLayouts() {
		if (AutoCompleteUIPlugin.isRAP())
			return;
		Display display = Display.getCurrent();

		FontData defaultFontData = display.getSystemFont().getFontData()[0];
		String fontName = defaultFontData.getName();
		int fontHeight = defaultFontData.getHeight();

		Font headerFont = new Font(display, new FontData(fontName, fontHeight, SWT.ITALIC | SWT.BOLD));
		Font noFont = new Font(display, new FontData(fontName, fontHeight, SWT.NORMAL));
		Color black = display.getSystemColor(SWT.COLOR_BLACK);

		int index = 0;
		textLayouts = new SSTextLayout[getTableLength()];
		for (Proposal proposal : proposalList.getTopProposalList()) {
			textLayouts[index] = AutoCompleteUIPlugin.getUIHelper()
					.newTextLayout();

			String text = getString(proposal);
			textLayouts[index].init(display, text);
			textLayouts[index].addStyle(noFont, black, 0, text.length());
			if (proposal.getStyles() != null && !proposal.getStyles().isEmpty()) {
				for (ProposalStyle style : proposal.getStyles()) {
					FontData newFontData = new FontData(fontName, fontHeight,
							style.fontStyle);
					Font font = new Font(display, newFontData);
					Color color = display.getSystemColor(style.fontColor);
					textLayouts[index].addStyle(font, color, style.from,
							style.to);
				}
			}
			index++;
		}
		for (String provider : proposalList.getProviderList()) {
			textLayouts[index] = AutoCompleteUIPlugin.getUIHelper()
					.newTextLayout();

			int count = proposalList.getCount(provider);
			String headerText = provider + " (" + count + " matching items)";
			textLayouts[index].init(display, headerText);
			textLayouts[index].addStyle(headerFont, black, 0,
					headerText.length());
			index++;

			for (Proposal proposal : proposalList.getProposals(provider)) {
				textLayouts[index] = AutoCompleteUIPlugin.getUIHelper()
						.newTextLayout();

				String text = getString(proposal);
				textLayouts[index].init(display, text);
				textLayouts[index].addStyle(noFont, black, 0, text.length());
				if (proposal.getStyles() != null
						&& !proposal.getStyles().isEmpty()) {
					for (ProposalStyle style : proposal.getStyles()) {
						FontData newFontData = new FontData(fontName,
								fontHeight, style.fontStyle);
						Font font = new Font(display, newFontData);
						Color color = display.getSystemColor(style.fontColor);
						textLayouts[index].addStyle(font, color, style.from,
								style.to);
					}
				}
				index++;
			}
		}
		for (SSTextLayout sstl : textLayouts)
			if (sstl != null && sstl.getBounds() != null
					&& sstl.getBounds().width > maxItemWidth)
				maxItemWidth = sstl.getBounds().width;
		adjustTableBounds();
	}

	/*
	 * Handle the set data event. Set the item data of the requested item to the
	 * corresponding proposal in the proposal cache.
	 */
	private void handleSetData(Event event) {
		TableItem item = (TableItem) event.item;
		int index = proposalTable.indexOf(item);
		Display display = Display.getCurrent();

		int proposalIndex = 0;
		for (Proposal proposal : proposalList.getTopProposalList()) {
			if (index == proposalIndex) {
				if (AutoCompleteUIPlugin.isRAP()) {
					item.setText("  " + getString(proposal));
					item.setImage(getImage(proposal, false));
					if (maxItemWidth < item.getBounds().width)
						maxItemWidth = item.getBounds().width;
				}
				item.setData(proposal);
				return;
			}
			proposalIndex++;
		}
		for (String provider : proposalList.getProviderList()) {
			if (index == proposalIndex) {
				// Data == null => not selectable
				item.setData(null);
				item.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
				if (AutoCompleteUIPlugin.isRAP()) {
					int count = proposalList.getCount(provider);
					String text = provider + " (" + count + " matching items)";
					item.setText(text);

					FontData fontData = display.getSystemFont().getFontData()[0];
					FontData newFontData = new FontData(fontData.getName(), fontData.getHeight(), SWT.ITALIC | SWT.BOLD);
					Font font = new Font(display, newFontData);
					item.setFont(font);

					if (maxItemWidth < item.getBounds().width)
						maxItemWidth = item.getBounds().width;
				}
				return;
			}
			proposalIndex++;
			for (Proposal proposal : proposalList.getProposals(provider)) {
				if (index == proposalIndex) {
					if (AutoCompleteUIPlugin.isRAP()) {
						item.setText("  " + getString(proposal));
						item.setImage(getImage(proposal, false));
						if (maxItemWidth < item.getBounds().width)
							maxItemWidth = item.getBounds().width;
					}
					item.setData(proposal);
					return;
				}
				proposalIndex++;
			}
		}
	}

	/*
	 * Caches the specified proposals and repopulates the table if it has been
	 * created.
	 */
	private void setProposals(ContentProposalList newProposalList) {
		if (newProposalList == null) {
			newProposalList = getEmptyProposalArray();
		}
		this.proposalList = newProposalList;
		if (!isValid())
			return;

		// Reset item width
		maxItemWidth = 0;
		nonSelectableItems.clear();
		int proposalIndex = proposalList.getTopProposalList().size();
		for (String provider : proposalList.getProviderList()) {
			nonSelectableItems.add(proposalIndex);
			proposalIndex += proposalList.getProposals(provider).length + 1;
		}

		// If there is a table
		if (isValid()) {
			if (USE_VIRTUAL) {
				// Set and clear the virtual table. Data will be
				// provided in the SWT.SetData event handler.
				proposalTable.setItemCount(getTableLength());
				proposalTable.clearAll();
				initializeTextLayouts();
			} else {
				// Populate the table manually
				proposalTable.setRedraw(false);
				proposalTable.setItemCount(getTableLength());
				TableItem[] items = proposalTable.getItems();

				int index = 0;
				for (Proposal proposal : newProposalList.getTopProposalList()) {
					TableItem item = items[index];
					item.setText("  " + getString(proposal));
					item.setImage(getImage(proposal, false));
					item.setData(proposal);
					index++;
				}
				for (String provider : newProposalList.getProviderList()) {
					TableItem item = items[index];
					int count = newProposalList.getCount(provider);
					String text = provider + " (" + count + " matching items)";
					item.setText(text);
					// Data == null => not selectable
					item.setData(null);

					Display display = Display.getCurrent();
					Color color = display.getSystemColor(SWT.COLOR_GRAY);
					FontData fontData = item.getFont().getFontData()[0];
					Font font = new Font(display, new FontData(
							fontData.getName(), fontData.getHeight(),
							SWT.ITALIC | SWT.BOLD));
					item.setBackground(color);
					item.setFont(font);

					index++;
					for (Proposal proposal : newProposalList
							.getProposals(provider)) {
						item.setText("  " + getString(proposal));
						item.setImage(getImage(proposal, false));
						item.setData(proposal);
						index++;
					}
				}
				proposalTable.setRedraw(true);
			}
			if (infoPopup != null) {
				infoPopup.close();
			}
		}
		footer.setText("");
		
		if (handleTopProposals) {
			adapter.handleTopProposals(newProposalList);
			// First to respond win otherwise if all reponded,
			// we stop handle
			if (adapter.hasSelectedTopProposal()
					|| newProposalList.allResponded()) {
				handleTopProposals = false;
			}
		}
		// Select the top proposal that was displayed, if any
		proposalTable.deselectAll();
		if (adapter.hasSelectedTopProposal()) {
			int index = proposalList.getTopProposalList().indexOf(
					adapter.getSelectedTopProposal());
			if (index >= 0)
				selectProposal(index);
		}
		if (AutoCompleteUIPlugin.isRAP())
			adjustTableBounds();
	}

	/*
	 * Return the proposal table length including header & top proposals.
	 */
	private int getTableLength() {
		if (proposalList == null)
			return 0;
		return proposalList.length() + proposalList.getProviderList().size()
				+ proposalList.getTopProposalList().size();
	}

	/*
	 * Get the string for the specified proposal. Always return a String of some
	 * kind.
	 */
	private String getString(Proposal proposal) {
		if (proposal == null) {
			return EMPTY;
		}
		// if (labelProvider == null) {
		// return proposal.getLabel() == null ? proposal.getContent()
		// : proposal.getLabel();
		// }
		// return labelProvider.getText(proposal);
		return proposal.getValue();
	}

	/*
	 * Get the image for the specified proposal. If there is no image available,
	 * return null.
	 */
	private Image getImage(Proposal proposal, boolean selected) {
		if (proposal == null)
			return null;
		// return labelProvider.getImage(proposal);
		if (proposal.isPartial() && partialContentImage != null)
			return selected ? partialContentImageSelected : partialContentImage;
		if (proposal.isFunction() && functionContentImage != null)
			return selected ? functionContentImageSelected : functionContentImage;
		return null;
	}

	/*
	 * Return an empty array. Used so that something always shows in the
	 * proposal popup, even if no proposal provider was specified.
	 */
	private ContentProposalList getEmptyProposalArray() {
		return new ContentProposalList();
	}

	/*
	 * Answer true if the popup is valid, which means the table has been created
	 * and not disposed.
	 */
	private boolean isValid() {
		return proposalTable != null && !proposalTable.isDisposed();
	}

	/*
	 * Return whether the receiver has focus. This includes a check for whether
	 * the info popup has focus.
	 */
	public boolean hasFocus() {
		if (!isValid()) {
			return false;
		}
		if (getShell().isFocusControl() || proposalTable.isFocusControl()) {
			return true;
		}
		if (infoPopup != null && infoPopup.hasFocus()) {
			return true;
		}
		return false;
	}
	
	/*
	 * Return the current selected proposal.
	 */
	private Proposal getSelectedProposal() {
		if (isValid()) {
			int index = proposalTable.getSelectionIndex();
			if (proposalList == null || index < 0 || index >= getTableLength()) {
				return null;
			}
			int proposalIndex = 0;
			for (Proposal proposal : proposalList.getTopProposalList()) {
				if (index == proposalIndex) {
					return proposal;
				}
				proposalIndex++;
			}
			for (String provider : proposalList.getProviderList()) {
				if (index == proposalIndex) {
					return null;
				}
				proposalIndex++;
				for (Proposal proposal : proposalList.getProposals(provider)) {
					if (index == proposalIndex) {
						return proposal;
					}
					proposalIndex++;
				}
			}
		}
		return null;
	}
	
	/*
	 * Select the proposal at the given index.
	 */
	private void selectProposal(int index) {
		Assert.isTrue(index >= 0, "Proposal index should never be negative"); //$NON-NLS-1$
		if (!isValid() || proposalList == null || index >= getTableLength()) {
			return;
		}
		proposalTable.setSelection(index);
		proposalTable.showSelection();

		showProposalDescription();
	}

	/**
	 * Opens this ContentProposalPopup. This method is extended in order to add
	 * the control listener when the popup is opened and to invoke the secondary
	 * popup if applicable.
	 * 
	 * @return the return code
	 * 
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open() {
		int value = super.open();
		if (popupCloser == null) {
			popupCloser = new PopupCloserListener();
		}
		popupCloser.installListeners();
		Proposal p = getSelectedProposal();
		if (p != null) {
			showProposalDescription();
		}
		return value;
	}

	/**
	 * Closes this popup. This method is extended to remove the control
	 * listener.
	 * 
	 * @return <code>true</code> if the window is (or was already) closed, and
	 *         <code>false</code> if it is still open
	 */
	public boolean close() {
		popupCloser.removeListeners();
		if (infoPopup != null) {
			infoPopup.close();
		}
		boolean ret = super.close();
		adapter.notifyPopupClosed();
		return ret;
	}

	/*
	 * Show the currently selected proposal's description in a secondary popup.
	 */
	private void showProposalDescription() {
		// If we do not already have a pending update, then
		// create a thread now that will show the proposal description
		if (!pendingDescriptionUpdate) {
			// Create a thread that will sleep for the specified delay
			// before creating the popup. We do not use Jobs since this
			// code must be able to run independently of the Eclipse
			// runtime.
			Runnable runnable = new Runnable() {
				public void run() {
					pendingDescriptionUpdate = true;
					try {
						Thread.sleep(POPUP_DELAY);
					} catch (InterruptedException e) {
					}
					if (!isValid()) {
						return;
					}
					getShell().getDisplay().syncExec(new Runnable() {
						public void run() {
							// Query the current selection since we have
							// been delayed
							Proposal p = getSelectedProposal();
							if (p != null) {
								String description = p.getDescription();
								if (description != null) {
									if (infoPopup == null) {
										infoPopup = new InfoPopupDialog(getShell());
										infoPopup.open();
										infoPopup.getShell()
												.addDisposeListener(
														new DisposeListener() {
															public void widgetDisposed(
																	DisposeEvent event) {
																infoPopup = null;
															}
														});
									}
									infoPopup.setContents(p.getDescription());
								} else if (infoPopup != null) {
									infoPopup.close();
								}
								pendingDescriptionUpdate = false;
							}
						}
					});
				}
			};
			Thread t = new Thread(runnable);
			t.start();
		}
	}

	/*
	 * Accept the current proposal.
	 */
	private void acceptCurrentProposal(boolean addToHistory) {
		// Close before accepting the proposal. This is important
		// so that the cursor position can be properly restored at
		// acceptance, which does not work without focus on some controls.
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=127108
		Proposal proposal = getSelectedProposal();
		if (proposal != null) {
			adapter.proposalAccepted(proposal, addToHistory);
			close();
		}
	}

	/*
	 * Get the proposals from the proposal provider, and recompute any caches.
	 * Repopulate the popup if it is open.
	 */
	private void recomputeProposals(ContentProposalList newProposalList) {
		if (newProposalList == null)
			newProposalList = getEmptyProposalArray();
		if (newProposalList.fullLength() == 0 && newProposalList.allResponded()) {
			this.proposalList = newProposalList;
			close();
		} else {
			setProposals(newProposalList);
		}
	}

	/*
	 * In an async block, request the proposals. This is used when clients are
	 * in the middle of processing an event that affects the widget content. By
	 * using an async, we ensure that the widget content is up to date with the
	 * event.
	 */
	private void asyncRecomputeProposals() {
		footer.setText("Searching...");
		if (isValid()) {
			synchronized (uniqueId) {
				if (uniqueId == Long.MAX_VALUE)
					uniqueId = Long.MIN_VALUE;
				uniqueId++;
			}
			final Long currentId = new Long(uniqueId);
			control.getDisplay().asyncExec(new Runnable() {
				public void run() {
					adapter.getProposals(new IContentProposalSearchHandler() {

						@Override
						public void handleResult(
								final ContentProposalList proposalList) {
							if (control != null && !control.isDisposed()) {
								control.getDisplay().asyncExec(new Runnable() {
									public void run() {
										if (currentId.equals(uniqueId))
											recomputeProposals(proposalList);
									}
								});
							}
						}

						@Override
						public void handleTooltips(List<TooltipData> tooltips) {
							adapter.handleTooltipData(tooltips);
						}
					});
				}
			});
		}
	}

	Listener getTargetControlListener() {
		if (targetControlListener == null) {
			targetControlListener = new TargetControlListener();
		}
		return targetControlListener;
	}

	public Point getPopupSize() {
		return popupSize;
	}

	public void setPopupSize(Point size) {
		popupSize = size;
	}

	public void refreshProposals(ContentProposalList newProposalList) {
		if (!adapter.isPreventTopProposalSelection())
			handleTopProposals = true;
		recomputeProposals(newProposalList);
	}
}
