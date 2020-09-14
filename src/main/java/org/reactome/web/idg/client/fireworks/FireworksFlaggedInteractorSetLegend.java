package org.reactome.web.idg.client.fireworks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.reactome.web.fi.legends.FlaggedInteractorSetLegend;
import org.reactome.web.fireworks.events.NodeFlaggedEvent;
import org.reactome.web.fireworks.events.NodeFlaggedResetEvent;
import org.reactome.web.fireworks.handlers.NodeFlaggedHandler;
import org.reactome.web.fireworks.handlers.NodeFlaggedResetHandler;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Label;

public class FireworksFlaggedInteractorSetLegend extends FlaggedInteractorSetLegend implements NodeFlaggedHandler, NodeFlaggedResetHandler{

	public FireworksFlaggedInteractorSetLegend(EventBus eventBus) {
		super(eventBus);
		
		eventBus.addHandler(NodeFlaggedEvent.TYPE, this);
		eventBus.addHandler(NodeFlaggedResetEvent.TYPE, this);
	}

	@Override
	public void onNodeFlaggedReset() {
		super.dataDescContainer.clear();
		super.setVisible(false);
	}

	@Override
	public void onNodeFlagged(NodeFlaggedEvent event) {
		String flagTerm = event.getTerm();
		if(!event.getIncludeInteractors()) return;
		
		Set<String> descs = new HashSet<>();
		if(flagTerm.contains(",")) {
			descs = new HashSet<>(Arrays.asList(flagTerm.split(",")));
			descs.remove(flagTerm.substring(0, flagTerm.indexOf(",")));
		}
		if(descs.size() == 0) return;
		
		
		dataDescContainer.clear();
		descs.forEach(e -> {
			Label lbl = new Label(e);
			lbl.setStyleName(RESOURCES.getCSS().descLabel());
			dataDescContainer.add(new Label(e));
		});
		this.setVisible(true);
	}

}
