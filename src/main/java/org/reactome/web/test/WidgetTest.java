package org.reactome.web.test;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class WidgetTest implements EntryPoint{

	@Override
	public void onModuleLoad() {
		Label lbl = new Label("hello world");
		
		RootPanel.get().add(lbl);
	}

}
