package com.boguenon.service.modules.bayes.costeffectiveness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.inference.tasks.VariableElimination.VECEPSA;

public class CEProbabilisticDialog {
	private int iterations;
    private Variable decisionVariable;
    private java.util.List selectedScenario;
    private ProbNet probNet;
    private VECEPSA vecepsa;
    private java.util.List<GTablePotential> psaResults;
    
    private double evaluationWTP;
    private double referenceWTP;
    
    private boolean selectedStates[];
    
    boolean moreThanOneInterval;
    private final int DEFAULT_LAMBDA = 30000;
    private SensitivityAnalysisModel sensitivityAnalysisModel;
	
	public CEProbabilisticDialog(ProbNet probNet, EvidenceCase preResolutionEvidence, SensitivityAnalysisModel sensitivityAnalysisModel) 
	{
		vecepsa = null;

        this.probNet = probNet;

        iterations = sensitivityAnalysisModel.getNumberOfIterationsSimulations();
        decisionVariable = sensitivityAnalysisModel.getDecisionVariable();
        selectedScenario = sensitivityAnalysisModel.getSelectedScenario();
        selectedStates = new boolean[decisionVariable.getNumStates()];
        this.sensitivityAnalysisModel = sensitivityAnalysisModel;
        
        try
        {
            try
            {
                vecepsa = new VECEPSA(probNet, preResolutionEvidence, sensitivityAnalysisModel.getDecisionVariable(), sensitivityAnalysisModel.getNumberOfIterationsSimulations(), true);
            }
            catch(UnexpectedInferenceException e)
            {
                e.printStackTrace();
            }
            evaluationWTP = 30000D;
            referenceWTP = 30000D;
            psaResults = new ArrayList();
            
            java.util.List thresholds = new ArrayList();
            
            GTablePotential potential;
            
            for(Iterator<GTablePotential> i$ = vecepsa.getCeaResults().iterator(); i$.hasNext(); psaResults.add(potential))
            {
                potential = (GTablePotential)i$.next();
                if(!moreThanOneInterval)
                {
                    int i = 0;
                    do
                    {
                        if(i >= potential.elementTable.size())
                            break;
                        CEP cep = (CEP)potential.elementTable.get(i);
                        if(cep.getNumIntervals() != 1)
                        {
                            moreThanOneInterval = true;
                            break;
                        }
                        i++;
                    } while(true);
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}

	public String getResult()
	{
		StringBuilder sb = new StringBuilder();
		
		getCEPlaneChart(sb);
		getCEACChart(sb);
		
        return sb.toString();
	}
	
	private void getCEPlaneChart(StringBuilder sb)
	{
		sb.append("<ceplane>");
		
		sb.append("<series>");
        for(int cepIndex = 0; cepIndex < decisionVariable.getNumStates(); cepIndex++)
        {
            if(!selectedStates[cepIndex])
                continue;
            
            sb.append("<series name='" + decisionVariable.getStateName(cepIndex) + "'>");
            
            double cost;
            double effectiveness;
            
            String x_axis = "";
            String y_axis = "";
            
            for(int i=0; i < psaResults.size(); i++)
            {
                GTablePotential gTablePotential = (GTablePotential) psaResults.get(i);
                List<CEP> cepsForDecision = (List<CEP>) gTablePotential.elementTable;
                
                double baseCost;
                double baseEffectiveness;
                
                baseCost = 0.0D;
                baseEffectiveness = 0.0D;
                
                cost = ((CEP)cepsForDecision.get(cepIndex)).getCost(evaluationWTP);
                cost -= baseCost;
                effectiveness = ((CEP)cepsForDecision.get(cepIndex)).getEffectiveness(evaluationWTP);
                effectiveness -= baseEffectiveness;
                
                x_axis = (i == 0) ? "" + effectiveness : x_axis + "\t" + effectiveness;
                y_axis = (i == 0) ? "" + cost : y_axis + "\t" + cost;
            }

            sb.append("<xvalues><![CDATA[" + x_axis + "]]></xvalues>");
            sb.append("<yvalues><![CDATA[" + y_axis + "]]></yvalues>");
            
            sb.append("</series>");
        }
        sb.append("</series>");
        
        sb.append("</ceplane>");
	}
	
	private void getCEACChart(StringBuilder sb)
	{
		sb.append("<ceac>");
		createCEACDataset(sb);
		
        sb.append("<reference_markers>");
        sb.append("<reference value='" + referenceWTP + "'/>");
        sb.append("</reference_markers>");
        
        sb.append("</ceac>");
	}
	
	private void createCEACDataset(StringBuilder sb)
    {
        boolean atLeastOneSerie = false;
        
        sb.append("<series>");
        
        List<String> series_names = new ArrayList<String>();
        String series_x[] = null;
        String series_y[] = null;
        
        for(int stateIndex = 0; stateIndex < decisionVariable.getNumStates(); stateIndex++)
        {
            if(selectedStates[stateIndex])
            {
                atLeastOneSerie = true;
                series_names.add(decisionVariable.getStateName(stateIndex));
            }
        }
        
        if(!atLeastOneSerie)
        {
        	
        }
        else
        {
	        double maxLambda = referenceWTP * 2D;
	        
	        series_x = new String[series_names.size()];
	        series_y = new String[series_names.size()];
	        
	        for(int i = 0; i < 1000; i++)
	        {
	            double lambda = (maxLambda * (double)i) / 1000D;
	            double winnersForEachSerie[] = new double[series_names.size()];
	            Iterator<GTablePotential> itr = vecepsa.getCeaResults().iterator();
	            
	            while (itr.hasNext())
	            {
	                GTablePotential gTablePotential = (GTablePotential) itr.next();
	                
	                int bestSeriesIndex = -1;
	                double maxNetMonetaryBenefit = (-1.0D / 0.0D);
	                int seriesIndex = 0;
	                for(int stateIndex = 0; stateIndex < decisionVariable.getNumStates(); stateIndex++)
	                {
	                    if(!selectedStates[stateIndex])
	                        continue;
	                    double netMonetaryBenefit = ((CEP)gTablePotential.elementTable.get(stateIndex)).getNetMonetaryBenefit(lambda);
	                    if(netMonetaryBenefit > maxNetMonetaryBenefit)
	                    {
	                        maxNetMonetaryBenefit = netMonetaryBenefit;
	                        bestSeriesIndex = seriesIndex;
	                    }
	                    seriesIndex++;
	                }
	
	                if(bestSeriesIndex != -1)
	                    winnersForEachSerie[bestSeriesIndex]++;
	            }
	            
	            for(int seriesIndex = 0; seriesIndex < series_names.size(); seriesIndex++)
	            {
	            	series_x[seriesIndex] = (i == 0) ? "" + lambda : series_x[seriesIndex] + "," + lambda;
	            	double y = winnersForEachSerie[seriesIndex] / (double)sensitivityAnalysisModel.getNumberOfIterationsSimulations();
	            	series_y[seriesIndex] = (i == 0) ? "" + y : series_y[seriesIndex] + "," + y;
	            }
	        }
	        
            for(int seriesIndex = 0; seriesIndex < series_names.size(); seriesIndex++)
            {
            	sb.append("<series name='" + series_names.get(seriesIndex) + "'>");
            	sb.append("<xdata><![CDATA[" + series_x[seriesIndex] + "]]></xdata>");
            	sb.append("<ydata><![CDATA[" + series_y[seriesIndex] + "]]></ydata>");
                sb.append("</series>");
            }

        }
        
        sb.append("</series>");
    }
	
	private void getAcceptabilityCurvePanel(StringBuilder sb)
	{
		
	}
}
