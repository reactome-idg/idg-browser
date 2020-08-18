package org.reactome.web.idg.client.fireworks.loaders;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.reactome.web.idg.client.fireworks.EnrichedPathwaysPostData;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * 
 * @author brunsont
 *
 */
public class FlagPairwiseInteractorPathwaysLoader {

	public interface Handler {
		void onPathwaysToFlag(List<String>stIds);
		void onPathwaysToFlagError();
	}
	
	private static final String BASE_URL = "/idgpairwise/";
	private static Request request;
	
	public static void findPathwaysToFlag(String term, List<String> dataDescs, Handler handler) {
		if(request != null && request.isPending())
			request.cancel();
		
		String url = BASE_URL + "/relationships/enrichedSecondaryPathwaysForGene";
		EnrichedPathwaysPostData post = new EnrichedPathwaysPostData(term, dataDescs);
		String postString = post.toJSON();
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setHeader("content-type", "application/json");
		try {
			request = requestBuilder.sendRequest(postString, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() != Response.SC_OK)
						handler.onPathwaysToFlagError();
					handler.onPathwaysToFlag(parsePathwaysToFlag(response.getText()));
				}
				@Override
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch(RequestException ex) {
			handler.onPathwaysToFlagError();
		}
	}
	
	private static List<String> parsePathwaysToFlag(String text) {
		Set<String> rtn = new HashSet<>();
		
		JSONArray val = JSONParser.parseStrict(text).isArray();
		for(int i=0; i<val.size(); i++) {
			JSONObject pathway = val.get(i).isObject();
			if(pathway.get("fdr").isNumber().doubleValue() > new Double(0.05))
				continue;
			rtn.add(pathway.get("stId").isString().stringValue());
		}
		
		return new ArrayList<>(rtn);
	}
	
}
