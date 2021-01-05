package org.reactome.web.idg.client.fireworks;

import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.manager.StateTokenHelper;
import org.reactome.web.fireworks.events.NodeFlaggedEvent;
import org.reactome.web.fireworks.legends.FlaggedItemsControl;
import org.reactome.web.idg.client.fireworks.events.SetFIFlagDataDescKeysEvent;
import org.reactome.web.idg.client.fireworks.handlers.SetFIFlagDataDescKeysHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author brunsont
 *
 */
public class IDGFlaggedItemsControl extends FlaggedItemsControl implements SetFIFlagDataDescKeysHandler{

	private List<Integer> dataDescKeys;
	private TextBox prdInput;
	
	public IDGFlaggedItemsControl(EventBus eventBus) {
		super(eventBus);
		
		super.selector.removeFromParent();
		
		//Remove default style and make the msgLabel wider
		//Required because the default width is tagged with !important
		this.msgLabel.removeStyleName(msgLabel.getStyleName());
		this.msgLabel.addStyleName(IDGRESOURCES.getCSS().idgFlaggedItemsLabel());
		
		prdInput = new TextBox();
		
		//setting attributes on TextBox to make it function better
		prdInput.getElement().setAttribute("type", "number");
		prdInput.getElement().setAttribute("min", "0");
		prdInput.getElement().setAttribute("max", "1");
		prdInput.getElement().setAttribute("step", "0.1");
		
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
		super.add(prdInput);
		prdInput.setVisible(false);
		
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
	
	@Override
	public void onNodeFlagged(NodeFlaggedEvent event) {
		super.flagTerm = event.getTerm();
		
		super.includeInteractors = event.getIncludeInteractors();
		
		String msg;
		//There is a case where the DiagramObjectsFlaggedEvent gets fired with a null value for getFlaggedItems();
		//Happens when DiagramViewerImpl runs flaggedElementsLoaded with falsey includeInteractors while the view is FIViewVisualizer
        msg = " - " + event.getFlagged().size() + (event.getIncludeInteractors() == true ? " interacting " : "") + (event.getFlagged().size() == 1 ? " pathway" : " pathways") + " flagged";
		
		super.msgLabel.setText(flagTerm + msg);
		
		super.loadingIcon.setVisible(false);
		super.interactorsLabel.setVisible(false);
		setVisible(true);
	}
	
	@Override
	public void onSetFIFlagDataDescKeys(SetFIFlagDataDescKeysEvent event) {
		this.dataDescKeys = event.getDataDescKeys();
		if(dataDescKeys.contains(0)) {
			prdInput.setVisible(true);
			StateTokenHelper helper = new StateTokenHelper();
			prdInput.setValue(helper.buildTokenMap(History.getToken()).get("SIGCUTOFF"));
		}
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
