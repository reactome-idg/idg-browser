package org.reactome.web.idg.client.fireworks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.reactome.web.fireworks.client.FireworksViewerImpl;
import org.reactome.web.fireworks.legends.BottomContainerPanel;
import org.reactome.web.fireworks.legends.FlaggedItemsControl;
import org.reactome.web.idg.client.fireworks.loaders.FlagPairwiseInteractorPathwaysLoader;
import org.reactome.web.idg.client.fireworks.loaders.FlagPairwiseInteractorPathwaysLoader.Handler;

/**
 * 
 * @author brunsont
 *
 */
public class IDGFireworksViewerImpl extends FireworksViewerImpl {

	public IDGFireworksViewerImpl(String json) {
		super(json);
		BottomContainerPanel panel = super.canvases.getBottomContainerPanel();
		for(int i=0; i<panel.getWidgetCount(); i++) {
			if(panel.getWidget(i) instanceof FlaggedItemsControl)
				panel.remove(i);
		}
		panel.add(new FireworksFlaggedInteractorSetLegend(eventBus));
		panel.add(new IDGFlaggedItemsControl(eventBus));
	}

	@Override
	protected void findPathwaysToFlag(String identifier, Boolean includeInteractors) {
		if(!includeInteractors) {
			super.findPathwaysToFlag(identifier, includeInteractors);
			return;
		}
		//first token is gene term and the rest are data descriptions to narrow interactors
		identifier.replaceAll("%7C", "|");
		String[] tokens = identifier.split(",");
		String term = tokens[0];
		List<String> dataDescs = Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
		
		
		//Make server call
		FlagPairwiseInteractorPathwaysLoader.findPathwaysToFlag(term, dataDescs, new Handler() {
			@Override
			public void onPathwaysToFlag(List<String> stIds) {
				if(stIds.size() == 0) return;
				flagPathways(stIds, new ArrayList<>());
			}
			@Override
			public void onPathwaysToFlagError() {
				resetFlaggedItems();
			}
		});
	}
}
