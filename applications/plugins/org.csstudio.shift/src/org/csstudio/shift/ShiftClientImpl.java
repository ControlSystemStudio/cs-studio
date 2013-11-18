package org.csstudio.shift;


import gov.bnl.shiftClient.ShiftApiClient;
import gov.bnl.shiftClient.ShiftFinderException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.csstudio.shift.util.ShiftSearchUtil;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.TimeParser;

public class ShiftClientImpl implements ShiftClient {
	
	private final ShiftApiClient reader;
    

    public ShiftClientImpl(ShiftApiClient shiftApiClient) {
	this.reader = shiftApiClient;
    }

    @Override
	public Collection<Shift> listShifts() throws ShiftFinderException {
    	Collection<Shift> shifts = new ArrayList<Shift>();
		Collection<gov.bnl.shiftClient.Shift> returnedShifts = reader.listShifts();
		for (gov.bnl.shiftClient.Shift shift : returnedShifts) {
		    shifts.add(new ShiftToShift(shift));
		}
		return shifts;
	}

	@Override
	public Shift getShift(final Integer shiftId, final String type) throws ShiftFinderException {
		return new ShiftToShift(reader.getShift(shiftId, type));
	}
	
	@Override
	public Shift start(final Shift shift) throws ShiftFinderException {
		return new ShiftToShift(reader.start(shiftBuilder(shift)));	
	}

	@Override
	public Shift end(final Shift shift) throws ShiftFinderException {
		return new ShiftToShift(reader.end(shiftBuilder(shift)));	

	}

	@Override
	public Shift close(final Shift shift) throws ShiftFinderException {
		return new ShiftToShift(reader.close(shiftBuilder(shift)));	
	}

	@Override
	public Collection<Shift> findShiftsBySearch(final String pattern) throws ShiftFinderException {
		final Map<String, String> searchParameters = ShiftSearchUtil.parseSearchString(pattern);
	    if(searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_START) || searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_END)) {
		    TimeInterval timeInterval = null;
			if (searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_START) && searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_END)) {
				timeInterval = TimeParser.getTimeInterval(searchParameters.get(ShiftSearchUtil.SEARCH_KEYWORD_START),
						searchParameters.get(ShiftSearchUtil.SEARCH_KEYWORD_END));
			    searchParameters.put("from", String.valueOf(timeInterval.getStart().getSec()));
			    searchParameters.put("to", String.valueOf(timeInterval.getEnd().getSec()));
			} else if (searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_START)) {
				timeInterval = TimeParser.getTimeInterval(searchParameters.get(ShiftSearchUtil.SEARCH_KEYWORD_START), "now");
			    searchParameters.put("from", String.valueOf(timeInterval.getStart().getSec()));
			    searchParameters.put("to", String.valueOf(timeInterval.getEnd().getSec()));
			} else if (searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_END)) {
				timeInterval = TimeParser.getTimeInterval("now", searchParameters.get(ShiftSearchUtil.SEARCH_KEYWORD_END));
			    searchParameters.put("to", String.valueOf(timeInterval.getEnd().getSec()));

			}
		    searchParameters.remove(ShiftSearchUtil.SEARCH_KEYWORD_START);
		}
	    final Collection<Shift> shifts = new ArrayList<Shift>();
	    final Collection<gov.bnl.shiftClient.Shift> returnedShifts = reader.findShifts( searchParameters);
		for (gov.bnl.shiftClient.Shift shift : returnedShifts) {
		    shifts.add(new ShiftToShift(shift));
		}
		return shifts;
	}

	@Override
	public Collection<Shift> findShifts(final Map<String, String> map)  throws ShiftFinderException {
		final Collection<Shift> shifts = new ArrayList<Shift>();
		final Collection<gov.bnl.shiftClient.Shift> returnedShifts = reader.findShifts(map);
		for (gov.bnl.shiftClient.Shift shift : returnedShifts) {
		    shifts.add(new ShiftToShift(shift));
		}
		return shifts;
	}
	
	@Override
	public Collection<Shift> findShifts(final MultivaluedMap<String, String> map) throws ShiftFinderException {
		final Collection<Shift> shifts = new ArrayList<Shift>();
		final Collection<gov.bnl.shiftClient.Shift> returnedShifts = reader.findShifts(map);
		for (gov.bnl.shiftClient.Shift shift : returnedShifts) {
		    shifts.add(new ShiftToShift(shift));
		}
		return shifts;
	}
	
	@Override
	public Collection<String> listTypes() throws ShiftFinderException {
		Collection<gov.bnl.shiftClient.Type> types = reader.listTypes();
		Collection<String> typeNames = new HashSet<String>();
		for(gov.bnl.shiftClient.Type type : types) {
			typeNames.add(type.getName());
		}
		return typeNames;
	}
	
	private class ShiftToShift implements Shift {
		
		private final gov.bnl.shiftClient.Shift shift;
		
		public ShiftToShift(final gov.bnl.shiftClient.Shift shift) {
			this.shift = shift;
		}

		@Override
		public Object getId() {
			return shift.getId();
		}

		@Override
		public String getOwner() {
			return shift.getOwner();
		}

		@Override
		public Date getStartDate() {
			return shift.getStartDate();
		}

		@Override
		public Date getEndDate() {
			return shift.getEndDate();
		}

		@Override
		public String getType() {
			return shift.getType().getName();
		}

		@Override
		public String getDescription() {
			return shift.getDescription();
		}

		@Override
		public String getCloseShiftUser() {
			return shift.getCloseShiftUser();
		}

		@Override
		public String getOnShiftPersonal() {
			return shift.getOnShiftPersonal();
		}

		@Override
		public String getLeadOperator() {
			return shift.getLeadOperator();
		}
		
		@Override
		public String getReport() {
			return shift.getReport();
		}
	}
	private gov.bnl.shiftClient.Shift shiftBuilder(final Shift shift) {
		gov.bnl.shiftClient.Shift newShift = new gov.bnl.shiftClient.Shift();
		newShift.setId((Integer) shift.getId());
		newShift.setType(new gov.bnl.shiftClient.Type(shift.getType()));
		newShift.setStartDate(shift.getStartDate());
		newShift.setOwner(shift.getOwner());
		newShift.setEndDate(shift.getEndDate());
		newShift.setDescription(shift.getDescription());
		newShift.setCloseShiftUser(shift.getCloseShiftUser());
		newShift.setOnShiftPersonal(shift.getOnShiftPersonal());
		newShift.setLeadOperator(shift.getLeadOperator());
		newShift.setReport(shift.getReport());
		return newShift;
    }


}
