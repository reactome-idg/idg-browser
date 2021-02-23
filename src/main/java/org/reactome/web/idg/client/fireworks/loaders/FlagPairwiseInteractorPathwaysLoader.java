package org.reactome.web.idg.client.fireworks.loaders;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import org.reactome.web.idg.client.fireworks.EnrichedPathwaysPostData;
import org.reactome.web.idg.client.fireworks.model.PathwayEnrichmentResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * 
 * @author brunsont
 *
 */
public class FlagPairwiseInteractorPathwaysLoader {

	public interface Handler {
		void onPathwaysToFlag(List<PathwayEnrichmentResult>stIds);
		void onPathwaysToFlagError();
	}
	
	public interface DataDescKeysHandler {
		void onDataDescsRecieved(List<String> dataDescs);
		void onDataDescsRecievedError();
	}
	
	private static final String BASE_URL = "/idgpairwise/";
	private static Request request;
	
	public static void findPathwaysToFlag(String term, List<Integer> dataDescs, Double prd, Handler handler) {
		if(request != null && request.isPending())
			request.cancel();
		
		String url = BASE_URL + "relationships/enrichedSecondaryPathwaysForTerm";
		
		EnrichedPathwaysPostData post = new EnrichedPathwaysPostData(term, dataDescs, prd);
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setHeader("content-type", "application/json");
		try {
			request = requestBuilder.sendRequest(post.toJSON(), new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() != Response.SC_OK) {
						handler.onPathwaysToFlagError();
						return;
					}
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
	
	public static void findDataDescsForKeys(List<Integer> dataDescKeys, DataDescKeysHandler dataDescKeysHandler) {
		String url = BASE_URL + "relationships/dataDescsForKeys";
		
		//build post data
		JSONArray keys = new JSONArray();
		for(Integer key : dataDescKeys) {
			keys.set(keys.size(), new JSONNumber(key));
		}
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("content-type", "application/json");
		try {
			requestBuilder.sendRequest(keys.toString(), new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					List<String> dataDescs = new ArrayList<>();
					JSONArray dataDescArray = JSONParser.parseStrict(response.getText()).isArray();
					for(int i=0; i<dataDescArray.size(); i++) {
						dataDescs.add(dataDescArray.get(i).isString().stringValue());
					}
					dataDescKeysHandler.onDataDescsRecieved(dataDescs);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					dataDescKeysHandler.onDataDescsRecievedError();
				}
			});
		} catch(RequestException ex) {
			dataDescKeysHandler.onDataDescsRecievedError();
		}
	}
	
	private static List<PathwayEnrichmentResult> parsePathwaysToFlag(String text) {
		List<PathwayEnrichmentResult> rtn = new ArrayList<>();
		
		JSONArray val = JSONParser.parseStrict(text).isArray();
		for(int i=0; i<val.size(); i++) {
			JSONObject pathway = val.get(i).isObject();
			rtn.add(new PathwayEnrichmentResult(pathway.get("stId").isString().stringValue(),
														   pathway.get("name").isString().stringValue(),
														   pathway.get("fdr").isNumber().doubleValue(),
														   pathway.get("pVal").isNumber().doubleValue()));
		}
		
		return rtn;
	}
	
}
