package org.reactome.web.idg.client.fireworks;

import java.util.List;

import org.reactome.web.fi.legends.FlaggedInteractorSetLegend;
import org.reactome.web.fireworks.events.NodeFlaggedResetEvent;
import org.reactome.web.fireworks.handlers.NodeFlaggedResetHandler;
import org.reactome.web.idg.client.fireworks.events.SetFIFlagDataDescKeysEvent;
import org.reactome.web.idg.client.fireworks.handlers.SetFIFlagDataDescKeysHandler;
import org.reactome.web.idg.client.fireworks.loaders.FlagPairwiseInteractorPathwaysLoader;
import org.reactome.web.idg.client.fireworks.loaders.FlagPairwiseInteractorPathwaysLoader.DataDescKeysHandler;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Label;

public class FireworksFlaggedInteractorSetLegend extends FlaggedInteractorSetLegend implements NodeFlaggedResetHandler, SetFIFlagDataDescKeysHandler{

	private List<String> dataDescriptions;
	
	public FireworksFlaggedInteractorSetLegend(EventBus eventBus) {
		super(eventBus);
		
		eventBus.addHandler(NodeFlaggedResetEvent.TYPE, this);
		eventBus.addHandler(SetFIFlagDataDescKeysEvent.TYPE, this);
	}

	@Override
	public void onNodeFlaggedReset() {
		super.dataDescContainer.clear();
		super.setVisible(false);
	}

	@Override
	public void onSetFIFlagDataDescKeys(SetFIFlagDataDescKeysEvent event) {
		FlagPairwiseInteractorPathwaysLoader.findDataDescsForKeys(event.getDataDescKeys(), new DataDescKeysHandler() {

			@Override
			public void onDataDescsRecieved(List<String> dataDescs) {
				dataDescriptions = dataDescs;
				updateWidget();
			}

			@Override
			public void onDataDescsRecievedError() {
				onNodeFlaggedReset();
			}
		});
	}
	
	private void updateWidget() {
		dataDescContainer.clear();
		dataDescriptions.forEach(e -> {
			Label lbl = new Label(e);
			lbl.setStyleName(RESOURCES.getCSS().descLabel());
			dataDescContainer.add(new Label(e));
		});
		this.setVisible(true);
	}

}
