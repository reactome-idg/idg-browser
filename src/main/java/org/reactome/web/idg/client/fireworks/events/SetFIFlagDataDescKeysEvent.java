package org.reactome.web.idg.client.fireworks.events;

import org.reactome.web.idg.client.fireworks.handlers.SetFIFlagDataDescKeysHandler;

import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public class SetFIFlagDataDescKeysEvent extends GwtEvent<SetFIFlagDataDescKeysHandler>{
	public static Type<SetFIFlagDataDescKeysHandler> TYPE = new Type<>();
	
	private List<Integer> dataDescKeys;	
	
	public SetFIFlagDataDescKeysEvent(List<Integer> dataDescKeys) {
		this.dataDescKeys = dataDescKeys;
	}
	
	@Override
	public Type<SetFIFlagDataDescKeysHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(SetFIFlagDataDescKeysHandler handler) {
		handler.onSetFIFlagDataDescKeys(this);
	}
	
	public List<Integer> getDataDescKeys() {
		return this.dataDescKeys;
	}
	
	@Override
	public String toString() {
		return "Set flag data description keys to " + dataDescKeys.toString();
	}
}
