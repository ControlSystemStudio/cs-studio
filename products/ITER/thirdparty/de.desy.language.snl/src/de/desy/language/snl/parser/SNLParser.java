/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
package de.desy.language.snl.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import de.desy.language.editor.core.measurement.KeyValuePair;
import de.desy.language.editor.core.parser.AbstractLanguageParser;
import de.desy.language.editor.core.parser.Node;
import de.desy.language.snl.parser.nodes.AllDefineStatementsNode;
import de.desy.language.snl.parser.nodes.AllVariablesNode;
import de.desy.language.snl.parser.nodes.AssignStatementNode;
import de.desy.language.snl.parser.nodes.DefineStatementNode;
import de.desy.language.snl.parser.nodes.EntryNode;
import de.desy.language.snl.parser.nodes.EventFlagNode;
import de.desy.language.snl.parser.nodes.ExitNode;
import de.desy.language.snl.parser.nodes.MonitorStatementNode;
import de.desy.language.snl.parser.nodes.OptionStatementNode;
import de.desy.language.snl.parser.nodes.PlaceholderNode;
import de.desy.language.snl.parser.nodes.ProgramNode;
import de.desy.language.snl.parser.nodes.StateNode;
import de.desy.language.snl.parser.nodes.StateSetNode;
import de.desy.language.snl.parser.nodes.SyncStatementNode;
import de.desy.language.snl.parser.nodes.VariableNode;
import de.desy.language.snl.parser.nodes.WhenNode;
import de.desy.language.snl.parser.parser.AssignStatementParser;
import de.desy.language.snl.parser.parser.DefineConstantStatementParser;
import de.desy.language.snl.parser.parser.DefineFunctionStatementParser;
import de.desy.language.snl.parser.parser.EntryParser;
import de.desy.language.snl.parser.parser.EventFlagParser;
import de.desy.language.snl.parser.parser.ExitParser;
import de.desy.language.snl.parser.parser.MonitorStatementParser;
import de.desy.language.snl.parser.parser.MultiLineCommentParser;
import de.desy.language.snl.parser.parser.MultiLineEmbeddedCParser;
import de.desy.language.snl.parser.parser.OptionStatementParser;
import de.desy.language.snl.parser.parser.ProgramParser;
import de.desy.language.snl.parser.parser.SingleLineCommentParser;
import de.desy.language.snl.parser.parser.SingleLineEmbeddedCParser;
import de.desy.language.snl.parser.parser.StateParser;
import de.desy.language.snl.parser.parser.StateSetParser;
import de.desy.language.snl.parser.parser.SyncStatemantParser;
import de.desy.language.snl.parser.parser.VariableParser;
import de.desy.language.snl.parser.parser.WhenParser;

/**
 * Outline parser of the SNL language.
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.2
 */
public class SNLParser extends AbstractLanguageParser {

	private static String replaceWithWhitespace(final String input,
			final String toReplace) {
		final int length = toReplace.length();
		final char[] replacement = new char[length];
		Arrays.fill(replacement, ' ');
		return input.replace(toReplace, new String(replacement));
	}

	private String _input;
	private List<KeyValuePair> _measurementData;
	private int _entryCount = 0;
	private int _exitCount = 0;
	private int _whenCount = 0;
	private int _statesCount = 0;
	private int _entryDuration = 0;
	private int _exitDuration = 0;
	private int _whenDuration = 0;
	private int _statesDuration = 0;

	public SNLParser() {
		_measurementData = new LinkedList<KeyValuePair>();
	}

	@Override
	protected Node doParse(final CharSequence input, IResource sourceResource,
			final IProgressMonitor progressMonitor) {
		long overallStart = System.currentTimeMillis();
		if (input.length() == 0) {
			final PlaceholderNode placeholder = new PlaceholderNode(
					"No content to outline avail.");
			return placeholder;
		}
		progressMonitor.beginTask("Parsing outline...",
				IProgressMonitor.UNKNOWN);
		progressMonitor.worked(1);
		this._input = this.removeAllComments(input);
		progressMonitor.worked(2);
		this._input = this.removeAllEmbeddedC(this._input);
		progressMonitor.worked(3);

		Node root = null;

		final ProgramParser programParser = new ProgramParser();
		programParser.findNext(this._input);
		if (programParser.hasFoundElement()) {
			progressMonitor.worked(4);
			final ProgramNode programNode = programParser.getLastFoundAsNode();
			root = programNode;

			// configure the program node:
			findAndAddAllStateSets(programNode, _input);
			progressMonitor.worked(5);
			findAndAddAllVariables(programNode, _input);
			progressMonitor.worked(6);
			findAndAddAllDefineStatements(programNode, _input);
			progressMonitor.worked(7);
			findAndAddAllEventFlags(programNode, _input);
			progressMonitor.worked(8);
			findAndAddAllOptionStatements(programNode, _input);
			progressMonitor.worked(9);
		} else {
			final PlaceholderNode placeholder = new PlaceholderNode(
					"Missing: program statement");
			if (sourceResource != null) {
				try {
					IMarker errorMarker = sourceResource
							.createMarker(IMarker.PROBLEM);
					errorMarker.setAttribute(IMarker.SEVERITY,
							IMarker.SEVERITY_ERROR);
					errorMarker
							.setAttribute(
									IMarker.MESSAGE,
									"Missing introducing program statement, place a statement like \"program MyProgramName\"");
				} catch (CoreException e) {
					// No need to handle!
					// TODO Decide for a better exception handling, maybe extend
					// interface to transport exception!
					e.printStackTrace();
				}
			}
			root = placeholder;
		}
		progressMonitor.done();
		long overallEnd = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("Overall duration (ms)", (int)(overallEnd-overallStart)));
		_measurementData.add(new KeyValuePair("Warnings", root.getAllWarningNodes().size()));
		_measurementData.add(new KeyValuePair("Errors", root.getAllErrorNodes().size()));
		return root;
	}

	private void findAndAddAllEntrys(final Node node, final String input) {
		long parseTimeStart = System.currentTimeMillis();
		final EntryParser entryParser = new EntryParser();
		entryParser.findNext(input);
		while (entryParser.hasFoundElement()) {
			_entryCount++;
			final EntryNode entryNode = entryParser.getLastFoundAsNode();
			final int startOffset = entryNode.getStatementStartOffset()
					+ node.getStatementStartOffset();
			final int endOffset = entryNode.getStatementEndOffset()
					+ node.getStatementStartOffset() + 1;
			entryNode.setOffsets(startOffset, endOffset);
			node.addChild(entryNode);
			final int lastFound = entryParser.getEndOffsetLastFound();
			entryParser.findNext(input, lastFound);
		}

		long parseTimeEnd = System.currentTimeMillis();
		_entryDuration = _entryDuration + (int)(parseTimeEnd - parseTimeStart);
	}

	private void findAndAddAllWhens(final Node node, final String input) {
		long parseTimeStart = System.currentTimeMillis();
		final WhenParser whenParser = new WhenParser();
		whenParser.findNext(input);
		while (whenParser.hasFoundElement()) {
			_whenCount++;
			final WhenNode whenNode = whenParser.getLastFoundAsNode();
			final int startOffset = whenNode.getStatementStartOffset()
					+ node.getStatementStartOffset();
			final int endOffset = whenNode.getStatementEndOffset()
					+ node.getStatementStartOffset() + 1;
			whenNode.setOffsets(startOffset, endOffset);
			node.addChild(whenNode);
			final int lastFound = whenParser.getEndOffsetLastFound();
			whenParser.findNext(input, lastFound);
		}
		long parseTimeEnd = System.currentTimeMillis();
		_whenDuration = _whenDuration + (int)(parseTimeEnd - parseTimeStart);
	}

	private void findAndAddAllExits(final Node node, final String input) {
		long parseTimeStart = System.currentTimeMillis();
		final ExitParser exitParser = new ExitParser();
		exitParser.findNext(input);
		while (exitParser.hasFoundElement()) {
			_exitCount++;
			final ExitNode exitNode = exitParser.getLastFoundAsNode();
			final int startOffset = exitNode.getStatementStartOffset()
					+ node.getStatementStartOffset();
			final int endOffset = exitNode.getStatementEndOffset()
					+ node.getStatementStartOffset() + 1;
			exitNode.setOffsets(startOffset, endOffset);
			node.addChild(exitNode);
			final int lastFound = exitParser.getEndOffsetLastFound();
			exitParser.findNext(input, lastFound);
		}
		long parseTimeEnd = System.currentTimeMillis();
		_exitDuration = _exitDuration + (int)(parseTimeEnd - parseTimeStart);
	}

	private void findAndAddAllStates(final Node node, final CharSequence input) {
		long parseTimeStart = System.currentTimeMillis();
		final StateParser stateParser = new StateParser();
		stateParser.findNext(input);
		while (stateParser.hasFoundElement()) {
			_statesCount++;
			final StateNode stateNode = stateParser.getLastFoundAsNode();
			final int startOffset = stateNode.getStatementStartOffset()
					+ node.getStatementStartOffset();
			final int endOffset = stateNode.getStatementEndOffset()
					+ node.getStatementStartOffset() + 1;
			stateNode.setStartOffsets(startOffset, endOffset);
			node.addChild(stateNode);
			final String lastFoundStatement = stateParser
					.getLastFoundStatement();
			this.findAndAddAllEntrys(stateNode, lastFoundStatement);
			this.findAndAddAllWhens(stateNode, lastFoundStatement);
			this.findAndAddAllExits(stateNode, lastFoundStatement);
			final int lastFound = stateParser.getEndOffsetLastFound();
			stateParser.findNext(input, lastFound);
		}
		long parseTimeEnd = System.currentTimeMillis();
		_statesDuration = _statesDuration + (int)(parseTimeEnd-parseTimeStart);
	}

	private void findAndAddAllStateSets(final Node node,
			final String input) {
		long parseTimeStart = System.currentTimeMillis();
		final String result = input;
		final StateSetParser stateSetParser = new StateSetParser();
		stateSetParser.findNext(result);
		int count = 0;
		while (stateSetParser.hasFoundElement()) {
			count++;
			final StateSetNode stateSetNode = stateSetParser
					.getLastFoundAsNode();
			node.addChild(stateSetNode);
			final String lastFoundStatement = stateSetParser
					.getLastFoundStatement();

			this.findAndAddAllStates(stateSetNode, lastFoundStatement);
			checkStatesOfWhens(stateSetNode);
			
			final int lastFound = stateSetParser.getEndOffsetLastFound();
			stateSetParser.findNext(result, lastFound);
		}
		
		long parseTimeEnd = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("StateSet Node parse duration (ms)", (int)(parseTimeEnd-parseTimeStart)));
		_measurementData.add(new KeyValuePair("StateSet Nodes", count));
		_measurementData.add(new KeyValuePair("State Nodes", _statesCount));
		_measurementData.add(new KeyValuePair("State Node parse duration (ms)", _statesDuration));
		_measurementData.add(new KeyValuePair("Entry Nodes", _entryCount));
		_measurementData.add(new KeyValuePair("Entry Node parse duration (ms)", _entryDuration));
		_measurementData.add(new KeyValuePair("When Nodes", _whenCount));
		_measurementData.add(new KeyValuePair("When Node parse duration (ms)", _whenDuration));
		_measurementData.add(new KeyValuePair("Exit Nodes", _exitCount));
		_measurementData.add(new KeyValuePair("Exit Node parse duration (ms)", _exitDuration));
	}

	private void checkStatesOfWhens(StateSetNode stateSetNode) {
		List<String> states = new LinkedList<String>();
		if (stateSetNode.hasChildren()) {
			for (Node child : stateSetNode.getChildrenNodes()) {
				if (child instanceof StateNode) {
					states.add(((StateNode) child).getSourceIdentifier());
				}
			}
		}
					
		if (stateSetNode.hasChildren()) {
			for (Node child : stateSetNode.getChildrenNodes()) {
				if (child instanceof StateNode) {
					StateNode state = (StateNode) child;
					if (state.hasChildren()) {
						for (Node current : state.getChildrenNodes()) {
							if (current instanceof WhenNode) {
								String followingState = ((WhenNode) current).getFollowingState();
								if (!states.contains(followingState)) {
									current.addWarning("Referenced state '" + followingState + "' not found in StateSet '" + stateSetNode.getSourceIdentifier() + "'");
								}
							}
						}
					}
				}
			}
		}
	}

	private void findAndAddAllVariables(final Node node, final String input) {
		long parseTimeStart = System.currentTimeMillis();
		final Map<String, VariableNode> variableMap = new HashMap<String, VariableNode>();
		final Map<String, AssignStatementNode> assignMap = new HashMap<String, AssignStatementNode>();

		AllVariablesNode variableParentNode = new AllVariablesNode();
		
		Interval[] exclusions = findIllegalPositions(node);

		final VariableParser variableParser = new VariableParser(exclusions);

		variableParser.findNext(input);

		while (variableParser.hasFoundElement()) {
			final VariableNode varNode = variableParser.getLastFoundAsNode();
			variableParentNode.addChild(varNode);

			if (variableMap.containsKey(varNode.getSourceIdentifier())) {
				varNode.addWarning("Duplicated variable declaration");
			}
			variableMap.put(varNode.getSourceIdentifier(), varNode);

			final int lastEndPosition = variableParser.getEndOffsetLastFound();

			// search next one
			variableParser.findNext(input, lastEndPosition);
		}
		
		if (!variableMap.isEmpty()) {
			node.addChild(variableParentNode);
		}

		long parseTimeIntermediate1 = System.currentTimeMillis();
		
		final AssignStatementParser assignParser = new AssignStatementParser(exclusions);
		assignParser.findNext(input);

		while (assignParser.hasFoundElement()) {
			final AssignStatementNode assignNode = assignParser
					.getLastFoundAsNode();
			assignMap.put(assignNode.getSourceIdentifier(), assignNode);
			final VariableNode varNode = variableMap.get(assignNode
					.getSourceIdentifier());
			if (varNode == null) {
				assignNode.addWarning("No variable definition found for '"
						+ assignNode.getSourceIdentifier() + "'");
				variableParentNode.addChild(assignNode);
			} else {
				varNode.setAssignedChannel(assignNode);
				if (varNode.isArray() && !assignNode.isArray()) {
					assignNode.addWarning("Variable "+varNode.getSourceIdentifier()+" is declared as array");
				} else if (!varNode.isArray() && assignNode.isArray()) {
					assignNode.addWarning("Variable "+varNode.getSourceIdentifier()+" is not declared as array");
				}
			}

			final int lastEndPosition = assignParser.getEndOffsetLastFound();

			// search next one
			assignParser.findNext(input, lastEndPosition);
		}

		long parseTimeIntermediate2 = System.currentTimeMillis();
		final MonitorStatementParser monitorParser = new MonitorStatementParser(exclusions);
		monitorParser.findNext(input);

		int count = 0;
		while (monitorParser.hasFoundElement()) {
			count++;
			final MonitorStatementNode monitorNode = monitorParser
					.getLastFoundAsNode();
			final VariableNode varNode = variableMap.get(monitorNode
					.getSourceIdentifier());
			final AssignStatementNode assignNode = assignMap.get(monitorNode
					.getSourceIdentifier());
			if (varNode == null) {
				monitorNode.addWarning("No variable definition found for '"
						+ monitorNode.getSourceIdentifier() + "'");
				variableParentNode.addChild(monitorNode);
			} else {
				varNode.setMonitored(monitorNode);
			}
			if (assignNode == null) {
				monitorNode.addWarning("No assign statement found for '"
						+ monitorNode.getSourceIdentifier() + "'");
			}

			final int lastEndPosition = monitorParser.getEndOffsetLastFound();

			// search next one
			monitorParser.findNext(input, lastEndPosition);
		}
		long parseTimeEnd = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("Variable parse duration (ms)", (int)(parseTimeIntermediate1-parseTimeStart)));
		_measurementData.add(new KeyValuePair("Assignment parse duration (ms)", (int)(parseTimeIntermediate2-parseTimeIntermediate1)));
		_measurementData.add(new KeyValuePair("Monitor parse duration (ms)", (int)(parseTimeEnd-parseTimeIntermediate2)));
		_measurementData.add(new KeyValuePair("Variable Nodes", variableMap.size()));
		_measurementData.add(new KeyValuePair("Assignment Nodes", assignMap.size()));
		_measurementData.add(new KeyValuePair("Monitor Nodes", count));
	}
	
	private void findAndAddAllDefineStatements(final Node node, final String input) {
		long parseTimeStart = System.currentTimeMillis();
		
		AllDefineStatementsNode defineParentNode = new AllDefineStatementsNode();
		
		List<String> defineNames = new LinkedList<String>();
		Interval[] exclusions = findIllegalPositions(node);
		
		DefineConstantStatementParser constantParser = new DefineConstantStatementParser(exclusions);
		constantParser.findNext(input);

		while (constantParser.hasFoundElement()) {
			final DefineStatementNode defineNode = constantParser.getLastFoundAsNode();
			defineParentNode.addChild(defineNode);

			if (defineNames.contains(defineNode.getSourceIdentifier())) {
				defineNode.addWarning("Duplicated define declaration");
			}
			defineNames.add(defineNode.getSourceIdentifier());

			final int lastEndPosition = constantParser.getEndOffsetLastFound();

			// search next one
			constantParser.findNext(input, lastEndPosition);
		}
		
		DefineFunctionStatementParser functionParser = new DefineFunctionStatementParser(exclusions);
		functionParser.findNext(input);

		while (functionParser.hasFoundElement()) {
			final DefineStatementNode defineNode = functionParser.getLastFoundAsNode();
			defineParentNode.addChild(defineNode);

			if (defineNames.contains(defineNode.getSourceIdentifier())) {
				defineNode.addWarning("Duplicated define declaration");
			}
			defineNames.add(defineNode.getSourceIdentifier());

			final int lastEndPosition = functionParser.getEndOffsetLastFound();

			// search next one
			functionParser.findNext(input, lastEndPosition);
		}
		
		if (!defineNames.isEmpty()) {
			node.addChild(defineParentNode);
		}
		
		long parseTimeEnd = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("Define parse duration (ms)", (int)(parseTimeEnd-parseTimeStart)));
		_measurementData.add(new KeyValuePair("Define Statements", defineNames.size()));
	}
	
	private Interval[] findIllegalPositions(Node root) {
		List<Interval> result = new LinkedList<Interval>(); 
		if (root.hasChildren()) {
			for (Node current : root.getChildrenNodes()) {
				if (current instanceof StateSetNode) {
					Interval interval = new Interval(current.getStatementStartOffset(), current.getStatementEndOffset());
					result.add(interval);
				}
			}
		}
		return result.toArray(new Interval[result.size()]);
	}

	private void findAndAddAllEventFlags(final Node node, final String input) {
		long parseTimeStart = System.currentTimeMillis();
		final Map<String, EventFlagNode> eventFlags = new HashMap<String, EventFlagNode>();
	
		Interval[] exclusions = findIllegalPositions(node);
		final EventFlagParser eventFlagParser = new EventFlagParser(exclusions);
	
		eventFlagParser.findNext(input);
		while (eventFlagParser.hasFoundElement()) {
			final EventFlagNode varNode = eventFlagParser.getLastFoundAsNode();
			node.addChild(varNode);
	
			eventFlags.put(varNode.getSourceIdentifier(), varNode);
	
			final int lastEndPosition = eventFlagParser.getEndOffsetLastFound();
	
			// search next one
			eventFlagParser.findNext(input, lastEndPosition);
		}
	
		long parseTimeMiddle = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("EventFlag Node parse duration (ms)", (int)(parseTimeMiddle - parseTimeStart)));
		
		final SyncStatemantParser syncParser = new SyncStatemantParser(exclusions);
		syncParser.findNext(input);
	
		int syncCount = 0;
		while (syncParser.hasFoundElement()) {
			syncCount++;
			final SyncStatementNode syncNode = syncParser.getLastFoundAsNode();
			final EventFlagNode eventNode = eventFlags.get(syncNode
					.getSourceIdentifier());
			if (eventNode == null) {
				syncNode.addWarning("No event flag definition found for '"
						+ syncNode.getSourceIdentifier() + "'");
				node.addChild(syncNode);
			} else {
				eventNode.setSynchronized(syncNode);
			}
	
			final int lastEndPosition = syncParser.getEndOffsetLastFound();
	
			// search next one
			syncParser.findNext(input, lastEndPosition);
		}
		_measurementData.add(new KeyValuePair("EventFlag Nodes", eventFlags.size()));
		_measurementData.add(new KeyValuePair("Sync Nodes", syncCount));
		long parseTimeEnd = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("Sync Node parse duration (ms)", (int)(parseTimeEnd - parseTimeMiddle)));
	}

	private void findAndAddAllOptionStatements(Node rootNode, String input) {
		long parseTimeStart = System.currentTimeMillis();
		final OptionStatementParser optionParser = new OptionStatementParser();
		optionParser.findNext(input);
		int count = 0;
		while (optionParser.hasFoundElement()) {
			count++;
			final OptionStatementNode optionStatementNode = optionParser
						.getLastFoundAsNode();
			Node parentNode = findSurroundingNode(rootNode, optionStatementNode);
			parentNode.addChild(optionStatementNode);
			final int lastFound = optionParser.getEndOffsetLastFound();
			optionParser.findNext(input, lastFound);
		}
		_measurementData.add(new KeyValuePair("Option Nodes", count));
		long parseTimeEnd = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("Option parse duration (ms)", (int)(parseTimeEnd-parseTimeStart)));
	}
	
	private Node findSurroundingNode(Node rootNode, Node childNode) {
		if (rootNode.hasChildren()) {
			for (Node node : rootNode.getChildrenNodes()) {
				if (node.hasOffsets()
						&& node.getStatementStartOffset() <= childNode
								.getStatementStartOffset()
						&& childNode.getStatementEndOffset() <= node
								.getStatementEndOffset()) {
					return findSurroundingNode(node, childNode);
				}
			}
		}
		return rootNode;
	}

	public String getClearedInput() {
		return _input;
	}

	private String removeAllComments(final CharSequence input) {
		long parseTimeStart = System.currentTimeMillis();
		String result = input.toString();

		final MultiLineCommentParser multiCommentParser = new MultiLineCommentParser();
		multiCommentParser.findNext(result);
		int multiLineCount = 0;
		while (multiCommentParser.hasFoundElement()) {
			multiLineCount++;
			final String comment = multiCommentParser.getLastFoundStatement();
			if (!result.contains(comment)) {
				break;
			}
			result = SNLParser.replaceWithWhitespace(result, comment);
			multiCommentParser.findNext(result);
		}

		final SingleLineCommentParser singleLineCommentParser = new SingleLineCommentParser();
		singleLineCommentParser.findNext(result);
		int singleLineCount = 0;
		while (singleLineCommentParser.hasFoundElement()) {
			singleLineCount++;
			final String singleCommentLine = singleLineCommentParser
					.getLastFoundStatement();
			if (!result.contains(singleCommentLine)) {
				break;
			}
			result = SNLParser.replaceWithWhitespace(result, singleCommentLine);

			singleLineCommentParser.findNext(result);
		}
		_measurementData.add(new KeyValuePair("Single Line Comments", singleLineCount));
		_measurementData.add(new KeyValuePair("Multi Line Comments", multiLineCount));
		long parseTimeEnd = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("Comment parse duration (ms)", (int)(parseTimeEnd-parseTimeStart)));
		return result;
	}

	private String removeAllEmbeddedC(final String input) {
		long parseTimeStart = System.currentTimeMillis();
		String result = input;

		final MultiLineEmbeddedCParser multiLineEmbeddedCParser = new MultiLineEmbeddedCParser();
		multiLineEmbeddedCParser.findNext(result);
		int multiLineCount = 0;
		while (multiLineEmbeddedCParser.hasFoundElement()) {
			multiLineCount++;
			final String embeddedCline = multiLineEmbeddedCParser
					.getLastFoundStatement();
			if (!result.contains(embeddedCline)) {
				break;
			}
			result = SNLParser.replaceWithWhitespace(result, embeddedCline);

			multiLineEmbeddedCParser.findNext(result);
		}

		final SingleLineEmbeddedCParser singleLineEmbeddedCParser = new SingleLineEmbeddedCParser();
		singleLineEmbeddedCParser.findNext(result);
		int singleLineCount = 0;
		while (singleLineEmbeddedCParser.hasFoundElement()) {
			singleLineCount++;
			final String embeddedCline = singleLineEmbeddedCParser
					.getLastFoundStatement();
			if (!result.contains(embeddedCline)) {
				break;
			}
			result = SNLParser.replaceWithWhitespace(result, embeddedCline);

			singleLineEmbeddedCParser.findNext(result);
		}
		
		_measurementData.add(new KeyValuePair("Single Line Embedded-C", singleLineCount));
		_measurementData.add(new KeyValuePair("Multi Line Embedded-C", multiLineCount));
		long parseTimeEnd = System.currentTimeMillis();
		_measurementData.add(new KeyValuePair("Embedded-C parse duration (ms)", (int)(parseTimeEnd-parseTimeStart)));		
		return result;
	}

	@Override
	public KeyValuePair[] getMeasurementData() {
		return _measurementData.toArray(new KeyValuePair[_measurementData.size()]);
	}

}
