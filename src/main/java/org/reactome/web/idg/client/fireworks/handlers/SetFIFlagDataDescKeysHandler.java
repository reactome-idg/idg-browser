package org.reactome.web.idg.client.fireworks.handlers;

import org.reactome.web.idg.client.fireworks.events.SetFIFlagDataDescKeysEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface SetFIFlagDataDescKeysHandler extends EventHandler{
	void onSetFIFlagDataDescKeys(SetFIFlagDataDescKeysEvent event);
}
