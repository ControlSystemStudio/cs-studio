package de.desy.language.editor.core.parser;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import de.desy.language.editor.core.measurement.KeyValuePair;
import de.desy.language.libraries.utils.contract.Contract;

/**
 * The abstract definition of a parser used to parse the source code receive a
 * parse tree.
 */
public abstract class AbstractLanguageParser {

    /**
     * The sequence this parser is working on.
     */
    private CharSequence _sequenceToWorkOn;

    /**
     * Performs the parsing of given input and returns an abstract parse tree as
     * nodes and child nodes.
     *
     * @param input
     *            The not-null, non-empty sequence to parse (this may also be a
     *            sub-sequence of the whole source code).
     * @param sourceResource
     *            The source IResource to parse, may be null.
     * @return The root node of the current parsed sequence, which used to hold
     *         the sub-nodes.
     */
    public final Node parse(final CharSequence input, IResource sourceResource,
            final IProgressMonitor progressMonitor) {
        Contract.requireNotNull("input", input); //$NON-NLS-1$
        this._sequenceToWorkOn = input;
        return doParse(input, sourceResource, progressMonitor);
    }

    /**
     * <p>
     * The sequence this parser is working on. This sequence will never be
     * changed.
     * </p>
     *
     * <p>
     * Programmer's note: This method should only be called by clients and not
     * by this parser itself.
     * </p>
     *
     * @return The sequence this parser is working on, not null.
     */
    public final CharSequence getSequenceWorkingOn() {
        return this._sequenceToWorkOn;
    }

    /**
     * Implement this method to realize parsing.
     *
     * @param input
     *            The input to work on.
     * @param sourceResource
     *            The source IResource to parse, may be null.
     * @return The first node (root) of the parse tree.
     */
    protected abstract Node doParse(final CharSequence input,
            IResource sourceResource, final IProgressMonitor progressMonitor);

    public abstract KeyValuePair[] getMeasurementData();
}
