package org.csstudio.utility.toolbox.framework.proposal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class DateProposalProvider implements IContentProposalProvider {

	private final SimpleDateFormat sd;

	public DateProposalProvider(SimpleDateFormat sd) {
		this.sd = sd;
	}

	
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		String value = "";
		if (StringUtils.isEmpty(contents)) {
			value = sd.format(new Date());
		} else {
			value = guessDateFromContents(contents);
		}
		return new IContentProposal[] { new ProposalData(value) };
	}

	private String guessDateFromContents(String contents) {
		
		String dateContents = contents.replace(".", "#");
		dateContents = dateContents.replace("-", "#");
		String[] parts = dateContents.split("#");
		
		int firstValue = -1;
		int secondValue = -1;
		int thirdValue = -1;
		
		try {
			if (parts.length >= 1) {
				firstValue = Integer.valueOf(parts[0]);
			}
			if (parts.length >= 2) {
				secondValue = Integer.valueOf(parts[1]);
			}
			if (parts.length == 3) {
				thirdValue = Integer.valueOf(parts[2]);
			}

			Calendar cal = GregorianCalendar.getInstance();
			
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
				
			if (firstValue != -1) {
				day = firstValue;
			}
			
			if (secondValue != -1) {
				month = secondValue - 1;
			}

			if (thirdValue != -1) {
				year = thirdValue;
				if (year < 99) {
					year += 2000;
				}
			}

			cal.set(year, month, day);
			
			return sd.format(cal.getTime());

		} catch (Exception e) {
			
			return "";
			
		}
				
	}

}
