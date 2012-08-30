package org.csstudio.utility.toolbox.view.forms.listener;

import java.util.List;

import org.csstudio.utility.toolbox.entities.Gebaeude;
import org.csstudio.utility.toolbox.entities.Raum;
import org.csstudio.utility.toolbox.framework.builder.AbstractControlWithLabelBuilder;
import org.csstudio.utility.toolbox.framework.proposal.TextValueProposalProvider;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.services.GebaeudeService;
import org.csstudio.utility.toolbox.services.RaumService;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class RoomFocusListener implements FocusListener {

	private Text building;
	private Text room;

	@Inject
	private RaumService raumService;

	@Inject
	private GebaeudeService gebaeudeService;

	public void init(Text building, Text room) {
		this.building = building;
		this.room = room;
	}

	@Override
	public void focusGained(FocusEvent event) {
		Option<Gebaeude> gebaeude = gebaeudeService.findByName(building.getText());
		if (gebaeude.hasValue()) {
			List<Raum> rooms = raumService.findAll(gebaeude.get().getGebaeudeId());
			TextValueProposalProvider proposalProvider = (TextValueProposalProvider) room
						.getData(AbstractControlWithLabelBuilder.CONTENT_PROPOSAL_PROVIDER);
			if (proposalProvider == null) {
				throw new IllegalStateException("proposalProvider must not be null");
			}
			proposalProvider.setData(rooms);
		}
	}

	@Override
	public void focusLost(FocusEvent event) {
		// do nothing
	}

}
