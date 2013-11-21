package org.csstudio.shift;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShiftBuilder {

	private Object id;
	private String owner;
	private Date startDate;
	private Date endDate;
	private String type;
	private String description;
	private String leadOperator;
	private String closeShiftUser;
	private String onShiftPersonal;
	private String report;
    private String status;
	
	private Map<String, ShiftBuilder> shifts = new HashMap<String, ShiftBuilder>();

	
	private ShiftBuilder(final String type) {
		this.type = type;
	}
	
	private ShiftBuilder() {
	    
	}
	
	public static ShiftBuilder withType(final String type) {
		return new ShiftBuilder(type);
	}
	
	public ShiftBuilder setOwner(final String owner) {
		this.owner = owner;
		return this;
	}
	
	public ShiftBuilder setStartDate(final Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public ShiftBuilder setEndDate(final Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public ShiftBuilder setType(final String type) {
		this.type = type;
		return this;
	}
	
	public ShiftBuilder setDescription(final String description) {
		this.description = description;
		return this;
	}
	
	public ShiftBuilder setLeadOperator(final String leadOperator) {
		this.leadOperator = leadOperator;
		return this;
	}
	
	public ShiftBuilder setCloseShiftUser(final String closeShiftUser) {
		this.closeShiftUser = closeShiftUser;
		return this;
	}
	
	public ShiftBuilder setOnShiftPersonal(final String onShiftPersonal) {
		this.onShiftPersonal = onShiftPersonal;
		return this;
	}
	
	public ShiftBuilder addDescription(final String description) {		
		this.description = this.description == null ? description : this.description.concat(description);
		return this;
	}
	
	public ShiftBuilder setReport(final String report) {
		this.report = report;
		return this;
	}
	
	public ShiftBuilder setStatus(final String status) {
		this.status = status;
		return this;
	}
	
	public static ShiftBuilder shift(final Shift shift) {
		final ShiftBuilder shiftBuilder = new ShiftBuilder();
		shiftBuilder.id = shift.getId();
		shiftBuilder.owner = shift.getOwner();
		shiftBuilder.startDate = shift.getStartDate();
		shiftBuilder.endDate = shift.getEndDate();
		shiftBuilder.type = shift.getType();
		shiftBuilder.description = shift.getDescription();
		shiftBuilder.onShiftPersonal = shift.getOnShiftPersonal();
		shiftBuilder.leadOperator = shift.getLeadOperator();
		shiftBuilder.closeShiftUser = shift.getCloseShiftUser();
		shiftBuilder.report = shift.getReport();
		shiftBuilder.status = shift.getStatus();
		return shiftBuilder;
	}
	
	public Shift build() throws IOException {
		return new ShiftImpl(id, owner, startDate, endDate, type, report, description, leadOperator, closeShiftUser, onShiftPersonal, status);
	}
	
	private class ShiftImpl implements Shift {
		private Object id;
		private String owner;
		private Date startDate;
		private Date endDate;
		private String type;
		private String description;
		private String leadOperator;
		private String closeShiftUser;
		private String onShiftPersonal;
		private String report;
		private String status;
		
		public ShiftImpl(final Object id, final String owner, final Date startDate, final Date endDate, final String type, final String report,
                         final String description, final String leadOperator, final String closeShiftUser, final String onShiftPersonal, final String status) {
			super();
			this.id = id;
			this.owner = owner;
			this.startDate = startDate;
			this.endDate = endDate;
			this.type = type;
			this.description = description;
			this.leadOperator = leadOperator;
			this.closeShiftUser = closeShiftUser;
			this.onShiftPersonal = onShiftPersonal;
			this.report = report;
			this.status = status;
		}
		
		@Override
		public Object getId() {
			return id;
		}
		
		@Override
		public String getOwner() {
			return owner;
		}
		
		@Override
		public Date getStartDate() {
			return startDate;
		}
		
		@Override
		public Date getEndDate() {
			return endDate;
		}
		
		@Override
		public String getType() { 
			return type;
		}
		
		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getCloseShiftUser() {
			return closeShiftUser;
		}

		@Override
		public String getOnShiftPersonal() {
			return onShiftPersonal;
		}

		@Override
		public String getLeadOperator() {
			return leadOperator;
		}
		
		
		@Override
		public String getReport() {
			return report;
		}
		
		@Override
		public String getStatus() {
			return status;
		}	
				
	}
	
    /**
     * Set the list of shifts to shifts
     * 
     * @param shifts
     * @return
     * @throws IOException 
     */
    public ShiftBuilder setShifts(Collection<ShiftBuilder> shifts) throws IOException {
		this.shifts = new HashMap<String, ShiftBuilder>(shifts.size());
		for (ShiftBuilder shiftBuilder : shifts) {
		    this.shifts.put(shiftBuilder.build().getType(), shiftBuilder);
		}
		return this;
    }

}
