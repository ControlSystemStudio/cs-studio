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
import org.csstudio.platform.util.StringUtil;

/**
 * Visitor implementation that determines logical and semantic errors in a DCT
 * project.
 * 
 * @author Sven Wende
 * 
 */
public final class ProblemVisitor implements IVisitor {
	private Map<String, Set<UUID>> finalEpicsNames;
	private Set<MarkableError> errors;

	/**
	 * Constructor.
	 */
	public ProblemVisitor() {
		errors = new HashSet<MarkableError>();
		finalEpicsNames = new HashMap<String, Set<UUID>>();
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(Project project) {
		if (!StringUtil.hasLength(project.getDbdPath())) {
			errors.add(new MarkableError(project.getId(), "No DBD file specified for project."));
		}
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IFolder folder) {
		// FIXME: Fehlerermittlung ausprogrammieren
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IPrototype prototype) {
		// FIXME: Fehlerermittlung ausprogrammieren
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IInstance instance) {
		// FIXME: Fehlerermittlung ausprogrammieren
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IRecord record) {
		// .. check name resolution
		Set<String> missing = determineMissingAliases(AliasResolutionUtil.getNameFromHierarchy(record), record);
		if (!missing.isEmpty()) {
			errors.add(new MarkableError(record.getId(), "Parameters [" + StringUtil.toSeparatedString(missing, ",")
					+ "] in record name cannot be resolved."));
		} else {
			// .. check that final EPICS names for concrete records are unique
			// all over the project
			if (!record.isAbstract()) {
				try {
					String finalEpicsName = ResolutionUtil.resolve(record.getEpicsNameFromHierarchy(), record);

					if (finalEpicsNames.containsKey(finalEpicsName)) {
						finalEpicsNames.get(finalEpicsName).add(record.getId());

						for (UUID id : finalEpicsNames.get(finalEpicsName)) {
							errors.add(new MarkableError(id, "Record name \"" + finalEpicsName + "\" is not unique."));
						}
					} else {
						finalEpicsNames.put(finalEpicsName, new HashSet<UUID>());
						finalEpicsNames.get(finalEpicsName).add(record.getId());
					}

				} catch (Exception e) {
					assert false : "Should not happen";
				}
			}
		}

		// .. check field resolution
		Map<String, Object> fields = record.getFields();

		for (String key : fields.keySet()) {
			try {
				ResolutionUtil.resolve((String) fields.get(key), record);
			} catch (AliasResolutionException e) {
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
	 * Determines missing aliases.
	 * 
	 * @param value
	 *            the value that has to be resolved
	 * @param record
	 *            the base element
	 * @return all missing aliases
	 */
	private Set<String> determineMissingAliases(String value, IRecord record) {
		Set<String> result = new HashSet<String>();
		Map<String, String> aliases = AliasResolutionUtil.getFinalAliases(record.getContainer());

		if (StringUtil.hasLength(value)) {
			Set<String> required = DctActivator.getDefault().getFieldFunctionService().findRequiredVariables(value);

			if (!required.isEmpty()) {
				for (String r : required) {
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
		private UUID id;
		private String message;

		/**
		 * Constructor.
		 * 
		 * @param id
		 *            the id of the model element, the error is related to
		 * @param message
		 *            the error message
		 */
		private MarkableError(UUID id, String message) {
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
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			return result;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			boolean result = false;
			
			if(obj instanceof MarkableError) {
				MarkableError me = (MarkableError) obj;
				
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
