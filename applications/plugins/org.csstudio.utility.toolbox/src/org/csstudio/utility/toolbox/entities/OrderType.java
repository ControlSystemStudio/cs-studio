package org.csstudio.utility.toolbox.entities;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.utility.toolbox.framework.binding.TextValue;

public class OrderType implements TextValue {

	private int id;
	private String text;
	
	public OrderType(int id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public static List<OrderType> getTypeList() {
		List<OrderType> statusList = new ArrayList<OrderType>();
		OrderType statusBa = new OrderType(1, "Bestellanforderung");
		OrderType statusWa = new OrderType(2, "Werkstattauftrag");
		OrderType statusLa = new OrderType(3, "Lagerabruf");
		statusList.add(statusBa);
		statusList.add(statusWa);
		statusList.add(statusLa);
		return statusList;
	}
	
	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	@Override
	public String getValue() {
		return text;
	}
	
}
