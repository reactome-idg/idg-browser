package org.reactome.web.idg.client.fireworks;

import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * 
 * @author brunsont
 *
 */
public class EnrichedPathwaysPostData {

	String term;
	private List<String> dataDescs;
	
	public EnrichedPathwaysPostData() {
		
	}
	
	public EnrichedPathwaysPostData(String term, List<String> dataDescs) {
		this.term = term;
		this.dataDescs = dataDescs;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public List<String> getDataDescs() {
		return dataDescs;
	}

	public void setDataDescs(List<String> dataDescs) {
		this.dataDescs = dataDescs;
	}
	
	public String toJSON() {
		JSONObject rtn = new JSONObject();
		rtn.put("term", new JSONString(this.term));
		JSONArray descJSON = new JSONArray();
		dataDescs.forEach(d -> {
			descJSON.set(descJSON.size(), new JSONString(d));
		});
		rtn.put("dataDescs", descJSON);
		
		return rtn.toString();
	}
}
