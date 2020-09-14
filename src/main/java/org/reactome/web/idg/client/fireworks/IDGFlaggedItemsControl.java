package org.reactome.web.idg.client.fireworks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.reactome.web.fireworks.events.NodeFlaggedEvent;
import org.reactome.web.fireworks.legends.FlaggedItemsControl;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.shared.EventBus;

public class IDGFlaggedItemsControl extends FlaggedItemsControl{

	public IDGFlaggedItemsControl(EventBus eventBus) {
		super(eventBus);
		super.selector.removeFromParent();
		
		//Remove default style and make the msgLabel wider
		//Required because the default width is tagged with !important
		this.msgLabel.removeStyleName(msgLabel.getStyleName());
		this.msgLabel.getElement().getStyle().setWidth(250, Unit.PX);
		this.msgLabel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		this.msgLabel.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		this.msgLabel.getElement().getStyle().setTextOverflow(TextOverflow.ELLIPSIS);
	}

	@Override
	public void onNodeFlagged(NodeFlaggedEvent event) {
		super.flagTerm = event.getTerm();
		Set<String> descs = new HashSet<>();
		if(super.flagTerm.contains(",")) {
			descs = new HashSet<>(Arrays.asList(flagTerm.split(",")));
			flagTerm = flagTerm.substring(0, flagTerm.indexOf(","));
			descs.remove(flagTerm);
		}
		
		super.includeInteractors = event.getIncludeInteractors();
		
		String msg;
		//There is a case where the DiagramObjectsFlaggedEvent gets fired with a null value for getFlaggedItems();
		//Happens when DiagramViewerImpl runs flaggedElementsLoaded with falsey includeInteractors while the view is FIViewVisualizer
        msg = " - " + event.getFlagged().size() + (event.getIncludeInteractors() == true ? " interacting " : "") + (event.getFlagged().size() == 1 ? " pathway" : " pathways") + " flagged";
		
		super.msgLabel.setText(flagTerm + msg);
		
		if(descs.size() > 0) {
			msgLabel.setTitle(String.join("\n", descs));
		}
		
		super.loadingIcon.setVisible(false);
		super.interactorsLabel.setVisible(false);
		setVisible(true);
	}
	
	

}
