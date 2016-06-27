package com.boguenon.service.modules.bayes.costeffectiveness;

import java.util.Iterator;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.inference.tasks.VariableElimination.VECEADecision;

public class CEDecisionResults {
	private ProbNet probNet;
    private VECEADecision veceaDecision;
    private Variable decisionVariable;

    private double selectedMinThreshold;
    private double selectedMaxThreshold;
    private double meanThreshold;
    private CEP cepsForDecision[];
    private GTablePotential gtablePotentialResult;
    private java.util.List thresholdList;
    
    private java.util.List cePlaneShowHideCheckBoxes;
    private java.util.List frontierInterventionsShowHideCheckBoxes;
    
    private java.util.List analysisThresholdsRadioButtons;
    private java.util.List cePlanethresholdsRadioButtons;
    private java.util.List frontierInterventionsthresholdsRadioButtons;
    
    private final int COLUMN_STATE_NAME = 0;
    private final int COLUMN_COST = 1;
    private final int COLUMN_EFFECTIVENESS = 2;
    private final int COLUMN_INTERVENTION = 3;
    private final int COLUMN_ICER = 3;
    private final String CLICKABLE_COLUMN_COLOR = "#DDF5D8";
    private boolean hasInterventions;
    private final int DEFAULT_NUM_SIGNIFICANT_NUMBERS = 5;
    
	public CEDecisionResults(ProbNet probNet, EvidenceCase evidenceCase, Variable decisionVariable) 
		throws NotEvaluableNetworkException, IncompatibleEvidenceException, UnexpectedInferenceException
	{
		this.probNet = probNet;
        this.decisionVariable = decisionVariable;
        veceaDecision = new VECEADecision(probNet, evidenceCase, decisionVariable);
        gtablePotentialResult = veceaDecision.getCEPPotential();
        hasInterventions = false;
        
        Iterator itr = veceaDecision.getCEPPotential().elementTable.iterator();
        
        while(itr.hasNext())
        {
            Object cep = itr.next();
            org.openmarkov.core.model.network.potential.Intervention interventions[] = ((CEP) cep).getInterventions();
            if(interventions == null || interventions.length == 0 || interventions[0] == null)
                continue;
            
            hasInterventions = true;
        }
	}

	public String getResult()
	{
		StringBuilder sb = new StringBuilder();
		
		getAnalysisPanel(sb);
		getCEPlanePanel(sb);
		getFrontierInterventionsPanel(sb);
		
		return sb.toString();
	}
	
	private void getAnalysisPanel(StringBuilder sb)
	{
		
	}
	
	private void getCEPlanePanel(StringBuilder sb)
	{
		
	}
	
	private void getFrontierInterventionsPanel(StringBuilder sb)
	{
		
	}
}
