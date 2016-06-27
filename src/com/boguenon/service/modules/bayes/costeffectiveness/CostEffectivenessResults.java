package com.boguenon.service.modules.bayes.costeffectiveness;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CostEffectivenessResults {
	
	private Map<String, String> result_data;
	
	public CostEffectivenessResults() {
		this.result_data = new HashMap<String, String>();
	}
	
	public String getResults()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<smsg>");
		sb.append("<cost_effectiveness_results>");
		
		Iterator<String> itr = result_data.keySet().iterator();
		
		while (itr.hasNext())
		{
			String key = itr.next();
			sb.append("<" + key + ">");
			sb.append(result_data.get(key));
			sb.append("</" + key + ">");
		}

        sb.append("</cost_effectiveness_results>");
        sb.append("</smsg>");
        
        return sb.toString();
	}
	
	public void setResult(String results, String rname)
	{
		if (results != null && results.length() > 0)
		{
			this.result_data.put(rname, results);
		}
	}
}
