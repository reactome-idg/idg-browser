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
        DiagramFactory.WATERMARK_BASE_URL = "https://idg.reactome.org/PathwayBrowser/";
		FireworksFactory.setFireworksViewerCreator(json -> new IDGFireworksViewerImpl(json));
        DiagramFactory.setDiagramViewerCreator(() -> new IdgDiagramViewerImpl());
        DiagramFactory.INTERACTORS_INITIAL_RESOURCE = "null";
        DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME = "null";
        GA_TOKEN = "UA-42985898-5";
        GA_DOMAIN = "idg.reactome.org";
    }
	
	
}