package org.reactome.web.idg.client.fireworks;

import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.manager.StateTokenHelper;
import org.reactome.web.fireworks.events.NodeFlaggedEvent;
import org.reactome.web.fireworks.legends.FlaggedItemsControl;
import org.reactome.web.idg.client.fireworks.events.SetFIFlagDataDescKeysEvent;
import org.reactome.web.idg.client.fireworks.handlers.SetFIFlagDataDescKeysHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author brunsont
 *
 */
public class IDGFlaggedItemsControl extends FlaggedItemsControl implements SetFIFlagDataDescKeysHandler{

	private List<Integer> dataDescKeys;
	private FlowPanel prdPanel;
	private TextBox prdInput;
	private FlowPanel fdrPanel;
	private TextBox fdrInput;
	private FlowPanel controlPanel;
	
	public IDGFlaggedItemsControl(EventBus eventBus) {
		super(eventBus);
		
		super.selector.removeFromParent();
		this.getElement().getStyle().setHeight(56, Unit.PX);
		
		//Remove default style and make the msgLabel wider
		//Required because the default width is tagged with !important
		this.msgLabel.removeStyleName(msgLabel.getStyleName());
		
		controlPanel = new FlowPanel();
		controlPanel.getElement().getStyle().setFloat(Float.LEFT);
		controlPanel.getElement().getStyle().setWidth(100, Unit.PCT);
		
		prdInput = new TextBox();
		
		//setting attributes on TextBox to make it function better
		prdInput.getElement().setAttribute("type", "number");
		prdInput.getElement().setAttribute("min", "0");
		prdInput.getElement().setAttribute("max", "1");
		
		prdInput.setStyleName(IDGRESOURCES.getCSS().prdInput());
		prdInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					updatePRD();
				}
			}
		});
		
		prdInput.setValue("0.9"); //set default PRD value to 0.9
		InlineLabel prdLbl = new InlineLabel("FI Score ≥");
		prdLbl.getElement().getStyle().setFloat(Float.NONE);
		prdPanel = new FlowPanel();
		prdPanel.getElement().getStyle().setFloat(Float.LEFT);
		prdPanel.add(prdLbl);
		prdPanel.add(prdInput);
		controlPanel.add(prdPanel);
		prdInput.setVisible(true);
		
		//make fdr input box
		fdrInput = new TextBox();
		
		//seting attributes on TexBox to make it function better
		fdrInput.getElement().setAttribute("type", "number");
		fdrInput.getElement().setAttribute("min", "0");
		fdrInput.getElement().setAttribute("max", "1");
		fdrInput.setStyleName(IDGRESOURCES.getCSS().prdInput());
		fdrInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					updateFDR();
				}
			}
		});
		
		fdrInput.setValue("0.05");
		InlineLabel fdrLbl = new InlineLabel("fdr ≤");
		fdrLbl.getElement().getStyle().setFloat(Float.NONE);
		fdrPanel = new FlowPanel();
		fdrPanel.getElement().getStyle().setFloat(Float.LEFT);
		fdrPanel.add(fdrLbl);
		fdrPanel.add(fdrInput);
		controlPanel.add(fdrPanel);
		fdrPanel.setVisible(true);
		
		
		controlPanel.setVisible(false);
		this.getElement().getStyle().setHeight(28, Unit.PX);
		super.add(controlPanel);
		
		eventBus.addHandler(SetFIFlagDataDescKeysEvent.TYPE, this);
	}

	/**
	 * update SIGCUTOFF token to new value and set new history token to cause update
	 * 
	 */
	private void updatePRD() {
		StateTokenHelper helper = new StateTokenHelper();
		Map<String, String> tokenMap = helper.buildTokenMap(History.getToken());
		tokenMap.put("SIGCUTOFF", prdInput.getValue()+"");
		History.newItem(helper.buildToken(tokenMap));
	}
	
	/**
	 * Update FLGFDR token and set new history token
	 */
	private void updateFDR() {
		StateTokenHelper helper = new StateTokenHelper();
		Map<String, String> tokenMap = helper.buildTokenMap(History.getToken());
		tokenMap.put("FLGFDR", fdrInput.getValue()+"");
		History.newItem(helper.buildToken(tokenMap));
	}
	
	@Override
	public void onNodeFlagged(NodeFlaggedEvent event) {
		super.flagTerm = event.getTerm();
		
		super.includeInteractors = event.getIncludeInteractors();
		
		//controlPanel shouldnt be visible when there are no interactors
		if(includeInteractors) {
			this.getElement().getStyle().setHeight(56, Unit.PX);
			controlPanel.setVisible(true);
		}
		else {
			this.getElement().getStyle().setHeight(28, Unit.PX);
			controlPanel.setVisible(false);
		}
		
		String msg;
		//There is a case where the DiagramObjectsFlaggedEvent gets fired with a null value for getFlaggedItems();
		//Happens when DiagramViewerImpl runs flaggedElementsLoaded with falsey includeInteractors while the view is FIViewVisualizer
        msg = " - " + event.getFlagged().size() + (event.getIncludeInteractors() == true ? " interacting " : "") + (event.getFlagged().size() == 1 ? " pathway" : " pathways") + " flagged";
		
		super.msgLabel.setText(flagTerm + msg);
		
		super.loadingIcon.setVisible(false);
		super.interactorsLabel.setVisible(false);
		
		StateTokenHelper helper = new StateTokenHelper();
		fdrInput.setValue(helper.buildTokenMap(History.getToken()).get("FLGFDR"));
		
		setVisible(true);
	}
	
	@Override
	public void onSetFIFlagDataDescKeys(SetFIFlagDataDescKeysEvent event) {
		this.dataDescKeys = event.getDataDescKeys();
		if(dataDescKeys.contains(0)) {
			prdPanel.setVisible(true);
			StateTokenHelper helper = new StateTokenHelper();
			prdInput.setValue(helper.buildTokenMap(History.getToken()).get("SIGCUTOFF"));
		}
		else prdPanel.setVisible(false);
	}
	
	public static IDGResources IDGRESOURCES;
	static {
		IDGRESOURCES = GWT.create(IDGResources.class);
		IDGRESOURCES.getCSS().ensureInjected();
	}
	
	public interface IDGResources extends ClientBundle {
		@Source (IDGResourceCSS.CSS)
		IDGResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgFireworks-FlaggedInteractorControl")
	public interface IDGResourceCSS extends CssResource {
		String CSS = "org/reactome/web/idg/client/fireworks/IDGFlaggedItemsControl.css";
		
		String idgFlaggedItemsLabel();
		
		String prdInput();
	}
}
