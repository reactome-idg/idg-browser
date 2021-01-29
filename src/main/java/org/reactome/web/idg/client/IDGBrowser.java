package org.reactome.web.idg.client;

import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.fireworks.client.FireworksFactory;
import org.reactome.web.idg.client.fireworks.IDGFireworksViewerImpl;
import org.reactome.web.fi.client.IdgDiagramViewerImpl;
import org.reactome.web.pwp.client.Browser;

/**
 * 
 * @author brunsont
 *
 */
public class IDGBrowser extends Browser {
	
	static {
		FireworksFactory.setFireworksViewerCreator(json -> new IDGFireworksViewerImpl(json));
        DiagramFactory.setDiagramViewerCreator(() -> new IdgDiagramViewerImpl());
        DiagramFactory.INTERACTORS_INITIAL_RESOURCE = "null";
        DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME = "null";
    }

	@Override
	protected void initConfig() {
		super.initConfig();
		FireworksFactory.SHOW_FOAM_BTN = false;
	}
}