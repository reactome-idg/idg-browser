package org.reactome.web.idg.client.fireworks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reactome.web.fi.data.manager.StateTokenHelper;
import org.reactome.web.fireworks.client.FireworksViewerImpl;
import org.reactome.web.fireworks.legends.BottomContainerPanel;
import org.reactome.web.fireworks.legends.FlaggedItemsControl;
import org.reactome.web.idg.client.fireworks.loaders.FlagPairwiseInteractorPathwaysLoader;
import org.reactome.web.idg.client.fireworks.loaders.FlagPairwiseInteractorPathwaysLoader.Handler;

import com.google.gwt.user.client.History;

/**
 * 
 * @author brunsont
 *
 */
public class IDGFireworksViewerImpl extends FireworksViewerImpl {

	private StateTokenHelper stHelper;
	
	public IDGFireworksViewerImpl(String json) {
		super(json);
		stHelper = new StateTokenHelper();
		BottomContainerPanel panel = super.canvases.getBottomContainerPanel();
		for(int i=0; i<panel.getWidgetCount(); i++) {
			if(panel.getWidget(i) instanceof FlaggedItemsControl)
				panel.remove(i);
		}
		panel.add(new FireworksFlaggedInteractorSetLegend(eventBus));
		panel.add(new IDGFlaggedItemsControl(eventBus));
	}
	
	

	@Override
	public void onNodeFlaggedReset() {
		super.onNodeFlaggedReset();
		Map<String, String> tokenMap = stHelper.buildTokenMap(History.getToken());
		tokenMap.remove("DSKEYS");
		tokenMap.remove("SIGCUTOFF");
		History.newItem(stHelper.buildToken(tokenMap));
	}



	@Override
	protected void findPathwaysToFlag(String identifier, Boolean includeInteractors) {
		if(!includeInteractors) {
			super.findPathwaysToFlag(identifier, includeInteractors);
			return;
		}
		
		Map<String, String> tokenMap = stHelper.buildTokenMap(History.getToken());
		
		//get dataDescription keys from "DSKEYS"
		List<Integer> dataDescKeys = Arrays.stream(tokenMap.get("DSKEYS").split(",")).map(num -> Integer.parseInt(num)).collect(Collectors.toList());
		
		//if SIGCUTOFF is not on map, add to prd, otherwise will be null
		Double prd = tokenMap.get("SIGCUTOFF") != null ? Double.parseDouble(tokenMap.get("SIGCUTOFF")):null;
		
		//Make server call
		FlagPairwiseInteractorPathwaysLoader.findPathwaysToFlag(identifier, dataDescKeys, prd, new Handler() {
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
