package com.boguenon.service.modules.bayes.costeffectiveness;

import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.potential.Intervention;

public class CEPDialog {
	private CEP cep;
    private ProbNet probNet;

	public CEPDialog(CEP cep, ProbNet probNet) {
		this.cep = cep;
        this.probNet = probNet.copy();
	}

	private String getLambdaLeftEndPoint(CEP cep, int intervalIndex)
    {
        Double threshold;
        if(intervalIndex == 0)
            threshold = Double.valueOf(cep.getMinThreshold());
        else
            threshold = Double.valueOf(cep.getThreshold(intervalIndex - 1));
        return (new Double(Util.roundWithSignificantFigures(threshold.doubleValue(), 6))).toString();
    }
	
	private String getLambdaRightEndPoint(CEP cep, int intervalIndex, int numIntervals)
    {
        Double threshold;
        if(intervalIndex == numIntervals - 1)
            threshold = Double.valueOf(cep.getMaxThreshold());
        else
            threshold = Double.valueOf(cep.getThreshold(intervalIndex));
        String lambdaRight;
        if(threshold.doubleValue() == (1.0D / 0.0D))
            lambdaRight = "+\u221E";
        else
            lambdaRight = (new Double(Util.roundWithSignificantFigures(threshold.doubleValue(), 6))).toString();
        return lambdaRight;
    }
	
	private String getFirstLine(String string)
    {
        int indexEOL = string.indexOf("\n");
        return indexEOL != -1 ? string.substring(0, indexEOL) : string;
    }
	
	public String getResult()
	{
		StringBuilder sb = new StringBuilder();
        
        sb.append("<cep>");
        
        sb.append("<table>");
        
        String headers = "LAMBDA_INF,LAMBDA_SUP,COST,EFFECTIVENESS,INTERVENTION";
        sb.append("<headers><![CDATA[" + headers + "]]></headers>");
        
        double costs[] = cep.getCosts();
        double effectiveness[] = cep.getEffectivities();
        int numRows = costs.length;
        Intervention interventions[] = cep.getInterventions();
        
        sb.append("<data>");
        
        for(int i = 0; i < numRows; i++)
        {
        	sb.append("<row>" + getLambdaLeftEndPoint(cep, i) + ";");
        	sb.append(getLambdaRightEndPoint(cep, i, numRows) + ";");
        	sb.append(Double.valueOf(costs[i]) + ";");
        	sb.append(Double.valueOf(effectiveness[i]) + ";");
        	sb.append(interventions[i] != null ? ((Object) (getFirstLine(interventions[i].toString()))) : "null");
        }
        
        sb.append("</data>");
        
        sb.append("</table>");
        
        sb.append("</cep>");
        
        return sb.toString();
	}
}
