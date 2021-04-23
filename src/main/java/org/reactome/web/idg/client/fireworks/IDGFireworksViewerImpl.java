package org.reactome.web.idg.client.fireworks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reactome.web.fi.data.manager.StateTokenHelper;
import org.reactome.web.fireworks.client.FireworksViewerImpl;
import org.reactome.web.fireworks.events.NodeFlaggedEvent;
import org.reactome.web.fireworks.events.NodeFlaggedResetEvent;
import org.reactome.web.fireworks.events.ShowReacfoamButtonEvent;
import org.reactome.web.fireworks.legends.BottomContainerPanel;
import org.reactome.web.fireworks.legends.FlaggedItemsControl;
import org.reactome.web.fireworks.model.Edge;
import org.reactome.web.fireworks.model.Node;
import org.reactome.web.idg.client.fireworks.events.SetFIFlagDataDescKeysEvent;
import org.reactome.web.idg.client.fireworks.loaders.FlagPairwiseInteractorPathwaysLoader;
import org.reactome.web.idg.client.fireworks.loaders.FlagPairwiseInteractorPathwaysLoader.Handler;
import org.reactome.web.idg.client.fireworks.model.PathwayEnrichmentResult;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

/**
 * 
 * @author brunsont
 *
 */
public class IDGFireworksViewerImpl extends FireworksViewerImpl implements ValueChangeHandler<String>{

	private StateTokenHelper stHelper;
	
	private String flagTerm;
	private List<Integer> dataDescKeys;
	private List<PathwayEnrichmentResult> flaggedPathways;
	private Double prd;
	private Double fdr;
	
	public IDGFireworksViewerImpl(String json) {
		super(json);
		stHelper = new StateTokenHelper();
		BottomContainerPanel panel = super.canvases.getBottomContainerPanel();
		for(int i=0; i<panel.getWidgetCount(); i++) {
			if(panel.getWidget(i) instanceof FlaggedItemsControl)
				panel.remove(i);
		}
		panel.add(new IDGPathwayFlaggingPanel(eventBus));
		History.addValueChangeHandler(this);
		eventBus.fireEventFromSource(new ShowReacfoamButtonEvent(true), this);
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		Map<String,String> tokenMap = stHelper.buildTokenMap(event.getValue());
		if(tokenMap.containsKey("FLG"))
			findPathwaysToFlag(tokenMap.get("FLG"), tokenMap.containsKey("FLGINT") ? true:false);
		
	}

	@Override
	public void onNodeFlaggedReset() {
		super.onNodeFlaggedReset();
		eventBus.fireEventFromSource(new ShowReacfoamButtonEvent(true), this);
		Map<String, String> tokenMap = stHelper.buildTokenMap(History.getToken());
		tokenMap.remove("DSKEYS");
		tokenMap.remove("SIGCUTOFF");
		tokenMap.remove("FLGFDR");
		History.newItem(stHelper.buildToken(tokenMap));
	}

	@Override
	protected void findPathwaysToFlag(String identifier, Boolean includeInteractors) {
		//if not include interactors, super methods can handle everything
		if(!includeInteractors) {
			eventBus.fireEventFromSource(new ShowReacfoamButtonEvent(true), this);
			super.findPathwaysToFlag(identifier, includeInteractors);
			return;
		}
		eventBus.fireEventFromSource(new ShowReacfoamButtonEvent(false), this);

		
		Map<String, String> tokenMap = stHelper.buildTokenMap(History.getToken());
		if(!doFlag(tokenMap)) { 
			updateFdr(tokenMap);
			return;
		}
		
		this.flagTerm = identifier;
		
		//get dataDescription keys from "DSKEYS"
		dataDescKeys = Arrays.stream(tokenMap.get("DSKEYS").split(",")).map(num -> Integer.parseInt(num)).collect(Collectors.toList());
		Collections.sort(dataDescKeys);
		
		//if SIGCUTOFF is not on map, add to prd, otherwise will be null
		prd = tokenMap.get("SIGCUTOFF") != null ? Double.parseDouble(tokenMap.get("SIGCUTOFF")):null;
		
		//if FLGFDR is not set, default to 0.05.
		fdr = tokenMap.get("FLGFDR") != null ? Double.parseDouble(tokenMap.get("FLGFDR")): 0.05d;
		
		//Make server call
		FlagPairwiseInteractorPathwaysLoader.findPathwaysToFlag(identifier, dataDescKeys, prd, new Handler() {
			@Override
			public void onPathwaysToFlag(List<PathwayEnrichmentResult> pathways) {
				if(pathways.size() == 0) return;
				flaggedPathways = pathways;
				eventBus.fireEventFromSource(new SetFIFlagDataDescKeysEvent(dataDescKeys), this);
				//filter flaggedPathways by FDR set by user
				flagPathways(flaggedPathways.stream().filter(pw -> pw.getFdr() <= fdr)
													 .map(pw -> pw.getStId())
													 .collect(Collectors.toList()), new ArrayList<>());
			}
			@Override
			public void onPathwaysToFlagError() {
				resetFlaggedItems();
			}
		});
	}
	
	private void updateFdr(Map<String, String> tokenMap) {
		if(tokenMap.containsKey("FLGFDR") && fdr != Double.parseDouble(tokenMap.get("FLGFDR"))) {
			this.fdr = Double.parseDouble(tokenMap.get("FLGFDR"));
			flagPathways(flaggedPathways.stream().filter(pw -> pw.getFdr() <= fdr)
												 .map(pw -> pw.getStId())
												 .collect(Collectors.toList()), new ArrayList<>());
		}
	}
	
	

	@Override
	protected void setFlaggedElements(String term, Boolean includeInteractors, Set<Node> nodesToFlag,
			Set<Edge> edgesToFlag) {
		//fix so NodeFlaggedEvent isnt fired on reset flagging
		if(term == null) {
			super.nodesToFlag = new HashSet<>();
			super.edgesToFlag = new HashSet<>();
			eventBus.fireEventFromSource(new NodeFlaggedResetEvent(), this);
			super.forceFireworksDraw = true;
			return;
		}
		if(nodesToFlag == null || nodesToFlag.isEmpty()) {
			super.nodesToFlag = new HashSet<>();
			super.edgesToFlag = new HashSet<>();
			super.forceFireworksDraw = true;
            this.eventBus.fireEventFromSource(new NodeFlaggedEvent(term, includeInteractors, nodesToFlag), this);
            return;
		}
		super.setFlaggedElements(term, includeInteractors, nodesToFlag, edgesToFlag);
	}

	private boolean doFlag(Map<String,String> tokenMap) {
		List<Integer> newDataDescKeys = Arrays.stream(tokenMap.get("DSKEYS").split(","))
				 .map(num -> Integer.parseInt(num))
				 .collect(Collectors.toList());
		Collections.sort(newDataDescKeys);
		
		if(!tokenMap.get("FLG").equals(this.flagTerm)
				|| (tokenMap.containsKey("SIGCUTOFF") && prd != Double.parseDouble(tokenMap.get("SIGCUTOFF"))) 
				|| !this.dataDescKeys.equals(newDataDescKeys)){
			return true;
		}
		return false;
	}
}
