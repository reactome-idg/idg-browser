package org.reactome.web.idg.client.fireworks;


import java.util.ArrayList;
import java.util.List;

import org.reactome.web.fireworks.events.NodeFlagRequestedEvent;
import org.reactome.web.fireworks.events.NodeFlaggedEvent;
import org.reactome.web.fireworks.events.NodeFlaggedResetEvent;
import org.reactome.web.fireworks.handlers.NodeFlagRequestedHandler;
import org.reactome.web.fireworks.handlers.NodeFlaggedHandler;
import org.reactome.web.fireworks.handlers.NodeFlaggedResetHandler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class IDGPathwayFlaggingPanel extends AbsolutePanel implements HasMouseOverHandlers, 
HasMouseOutHandlers, NodeFlaggedHandler, NodeFlaggedResetHandler, NodeFlagRequestedHandler{

	private FireworksFlaggedInteractorSetLegend legend;
	private IDGFlaggedItemsControl control;
	
	List<AbsolutePanel> panels;
	private boolean interactorsIncluded;
	
	public IDGPathwayFlaggingPanel(EventBus eventBus) {
		
		this.setStyleName(RESOURCES.getCSS().flaggingPanel());
		
		panels = new ArrayList<>();
		initPanels(eventBus);
		
		this.setVisible(false);
		
		eventBus.addHandler(NodeFlaggedEvent.TYPE, this);
		eventBus.addHandler(NodeFlaggedResetEvent.TYPE, this);
		eventBus.addHandler(NodeFlagRequestedEvent.TYPE, this);
		
	}
	
	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
	
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}
	
	private void initPanels(EventBus eventBus) {
		legend = new FireworksFlaggedInteractorSetLegend(eventBus);
		control = new IDGFlaggedItemsControl(eventBus);
		
		panels.add(legend);
		panels.add(control);
		
		for(AbsolutePanel panel : panels) this.add(panel);
		
		this.addMouseOverHandler(e -> {
			if(interactorsIncluded) legend.setVisible(true);
			control.setVisible(true);
		});
		
		this.addMouseOutHandler(e -> {
			panels.forEach(p -> p.setVisible(false));
		});
	}
	
	@Override
	public void onNodeFlagRequested(NodeFlagRequestedEvent event) {
		this.setVisible(true);
		interactorsIncluded = event.getIncludeInteractors();
		
		//set right height for panel
		if(!interactorsIncluded) this.getElement().getStyle().setHeight(40, Unit.PX);
		else this.getElement().getStyle().setHeight(110, Unit.PX);
	}

	@Override
	public void onNodeFlaggedReset() {
		this.setVisible(false);
	}

	@Override
	public void onNodeFlagged(NodeFlaggedEvent event) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				panels.forEach(p -> p.setVisible(false));
			}
		};
		timer.schedule(3000);
		
	}

	/*
	 * Everything below her eis styling
	 * */
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idg-fireworks-idgPathwayFlaggingPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/idg/client/fireworks/IDGPathwayFlaggingPanel.css";
		
		String flaggingPanel();
	}
}
