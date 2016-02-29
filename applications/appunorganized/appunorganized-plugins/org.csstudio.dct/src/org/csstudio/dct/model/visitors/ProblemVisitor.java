package org.csstudio.dct.model.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.domain.common.strings.StringUtil;

/**
 * Visitor implementation that determines logical and semantic errors in a DCT
 * project.
 *
 * @author Sven Wende
 *
 */
public final class ProblemVisitor implements IVisitor {
    private final Map<String, Set<UUID>> finalEpicsNames;
    private final Set<MarkableError> errors;
    private final Set<MarkableError> _warnings;

    /**
     * Constructor.
     */
    public ProblemVisitor() {
        errors = new HashSet<MarkableError>();
        _warnings = new HashSet<MarkableError>();
        finalEpicsNames = new HashMap<String, Set<UUID>>();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(final Project project) {
        if (!StringUtil.hasLength(project.getDbdPath())) {
            errors.add(new MarkableError(project.getId(), "No DBD file specified for project."));
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(final IFolder folder) {
        // FIXME: Fehlerermittlung ausprogrammieren
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(final IPrototype prototype) {
        // FIXME: Fehlerermittlung ausprogrammieren
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(final IInstance instance) {
        // FIXME: Fehlerermittlung ausprogrammieren
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(final IRecord record) {
        // .. check name resolution
        final Set<String> missing = determineMissingAliases(AliasResolutionUtil.getNameFromHierarchy(record), record);
        if (!missing.isEmpty()) {
            errors.add(new MarkableError(record.getId(), "Parameters [" + StringUtil.toSeparatedString(missing, ",")
                    + "] in record name cannot be resolved."));
        } else {
            // .. check that final EPICS names for concrete records are unique
            // all over the project

            final Boolean disabled = AliasResolutionUtil.getPropertyViaHierarchy(record, "disabled");
            final boolean real = !record.isAbstract();
            if (real && !disabled) {
                try {
                    final String finalEpicsName = ResolutionUtil.resolve(AliasResolutionUtil.getEpicsNameFromHierarchy(record), record);
                    final Map<String, String> fields = record.getFinalFields();
                    if("bi".equals(record.getType())) {
//                        final String onam = ResolutionUtil.resolve(fields.get("ONAM"), record);
                        final String onam = fields.get("ONAM");
                        if(onam == null || onam.isEmpty()) {
                            _warnings.add(new MarkableError(record.getId(), "From Record "+finalEpicsName+" the Field ONAM not set"));
                        }
                        final String znam = fields.get("ZNAM");
                        if(znam == null || znam.isEmpty()) {
                            _warnings.add(new MarkableError(record.getId(), "From Record "+finalEpicsName+" the Field ZNAM not set"));
                        }
                    }
                    if (finalEpicsNames.containsKey(finalEpicsName)) {
                        finalEpicsNames.get(finalEpicsName).add(record.getId());

                        for (final UUID id : finalEpicsNames.get(finalEpicsName)) {
                            errors.add(new MarkableError(id, "Record name \"" + finalEpicsName + "\" is not unique."));
                        }
                    } else {
                        finalEpicsNames.put(finalEpicsName, new HashSet<UUID>());
                        finalEpicsNames.get(finalEpicsName).add(record.getId());
                    }

                } catch (final Exception e) {
                    errors.add(new MarkableError(record.getId(), e.getMessage()));
                }
            }
        }

        // .. check field resolution
        final Map<String, String> fields = record.getFields();
        for (final String key : fields.keySet()) {
            try {
                ResolutionUtil.resolve(fields.get(key), record);
            } catch (final AliasResolutionException e) {
                errors.add(new MarkableError(record.getId(), "Resolution error:" + e.getMessage()));
            }
        }
    }

    /**
     * Returns the errors that have been discovered.
     *
     * @return all discovered errors
     */
    public Set<MarkableError> getErrors() {
        return errors;
    }

    /**
     * Returns the warnings that have been discovered.
     *
     * @return all discovered warnings
     */
    public Set<MarkableError> getWarnnings() {
        return _warnings;
    }

    /**
     * Determines missing aliases.
     *
     * @param value
     *            the value that has to be resolved
     * @param record
     *            the base element
     * @return all missing aliases
     */
    private Set<String> determineMissingAliases(final String value, final IRecord record) {
        final Set<String> result = new HashSet<String>();
        final Map<String, String> aliases = AliasResolutionUtil.getFinalAliases(record.getContainer());

        if (StringUtil.hasLength(value)) {
            final Set<String> required = DctActivator.getDefault().getFieldFunctionService().findRequiredVariables(value);

            if (!required.isEmpty()) {
                for (final String r : required) {
                    if (!aliases.containsKey(r)) {
                        result.add(r);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Represents an error.
     *
     * @author Sven Wende
     *
     */
    public static final class MarkableError {
        private final UUID id;
        private final String message;

        /**
         * Constructor.
         *
         * @param id
         *            the id of the model element, the error is related to
         * @param message
         *            the error message
         */
        private MarkableError(final UUID id, final String message) {
            assert id != null;
            assert message != null;
            this.id = id;
            this.message = message;
        }

        /**
         * Returns the id of the model element, the error is related to.
         *
         * @return id of the model element, the error is related to
         */
        public UUID getId() {
            return id;
        }

        /**
         * Returns the error message.
         *
         * @return the error message
         */
        public String getErrorMessage() {
            return message;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (id == null ? 0 : id.hashCode());
            result = prime * result + (message == null ? 0 : message.hashCode());
            return result;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            boolean result = false;

            if(obj instanceof MarkableError) {
                final MarkableError me = (MarkableError) obj;

                if(id.equals(me.id)) {
                    if(message.equals(me.message)) {
                        result = true;
                    }
                }
            }

            return result;
        }

    }

}
