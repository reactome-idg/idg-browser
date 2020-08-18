package org.reactome.web.idg.client.fireworks;

import java.util.List;

import com.google.gwt.dev.json.JsonObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class EnrichedPathwaysPostData {

	String gene;
	private List<String> dataDescs;
	
	public EnrichedPathwaysPostData() {
		
	}
	
	public EnrichedPathwaysPostData(String gene, List<String> dataDescs) {
		this.gene = gene;
		this.dataDescs = dataDescs;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public List<String> getDataDescs() {
		return dataDescs;
	}

	public void setDataDescs(List<String> dataDescs) {
		this.dataDescs = dataDescs;
	}
	
	public String toJSON() {
		JSONObject rtn = new JSONObject();
		rtn.put("gene", new JSONString(this.gene));
		JSONArray descJSON = new JSONArray();
		dataDescs.forEach(d -> {
			descJSON.set(descJSON.size(), new JSONString(d));
		});
		rtn.put("dataDescs", descJSON);
		
		return rtn.toString();
	}
}
