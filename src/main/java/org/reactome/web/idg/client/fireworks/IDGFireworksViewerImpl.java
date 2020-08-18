package org.reactome.web.idg.client.fireworks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.reactome.web.fireworks.client.FireworksViewerImpl;
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
	}

	@Override
	protected void findPathwaysToFlag(String identifier, Boolean includeInteractors) {
		if(!includeInteractors) {
			super.findPathwaysToFlag(identifier, includeInteractors);
			return;
		}
		//first token is gene term and the rest are data descriptions to narrow interactors
		String[] tokens = identifier.split(",");
		String term = tokens[0];
		List<String> dataDescs = Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
		
		
		//Make server call
		FlagPairwiseInteractorPathwaysLoader.findPathwaysToFlag(term, dataDescs, new Handler() {
			@Override
			public void onPathwaysToFlag(List<String> stIds) {
				flagPathways(stIds, new ArrayList<>());
			}
			@Override
			public void onPathwaysToFlagError() {
				resetFlaggedItems();
			}
		});
	}
}
