package org.reactome.web.idg.client;

import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.fi.client.IdgDiagramViewerImpl;
import org.reactome.web.pwp.client.Browser;

public class IDGBrowser extends Browser {
	
	static {
        DiagramFactory.setDiagramViewerCreator(() -> new IdgDiagramViewerImpl());
        DiagramFactory.INTERACTORS_INITIAL_RESOURCE = "null";
        DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME = "null";
    }
	
	
}