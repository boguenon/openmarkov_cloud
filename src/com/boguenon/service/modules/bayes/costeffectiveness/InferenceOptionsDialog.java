package com.boguenon.service.modules.bayes.costeffectiveness;

import java.util.ArrayList;
import java.util.Iterator;

import org.openmarkov.core.inference.MulticriteriaOptions;
import org.openmarkov.core.inference.TemporalOptions;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.ProbNet;

public class InferenceOptionsDialog {
	private java.util.List decisionCriteria;
	
	private ProbNet probNet;
	
	private boolean isTemporal;
    private boolean isMulticriteria;
    private MulticriteriaOptions multicriteriaOptions;
    private TemporalOptions temporalOptions;
    public static final int CRITERION_COLUMN = 0;
    public static final int UNICRITERIA_SCALE_COLUMN = 1;
    public static final int UNICRITERIA_DISCOUNT_COLUMN = 2;
    public static final int UNICRITERIA_DISCOUNT_UNIT_COLUMN = 3;
    public static final int CE_USE_COLUMN = 1;
    public static final int CE_SCALE_COLUMN = 2;
    public static final int CE_DISCOUNT_COLUMN = 3;
    public static final int CE_DISCOUNT_UNIT_COLUMN = 4;
    private static final long serialVersionUID = 1L;
	
    private Integer numSimulations;
    
	public InferenceOptionsDialog(ProbNet probNet, org.openmarkov.core.inference.MulticriteriaOptions.Type onlyShowThisType) {
		this.probNet = probNet;
		
		if(!probNet.hasConstraint(org.openmarkov.core.model.network.constraint.OnlyAtemporalVariables.class))
            isTemporal = true;
        else
            isTemporal = false;
        if(probNet.getDecisionCriteria() != null && probNet.getDecisionCriteria().size() > 1)
            isMulticriteria = true;
        
        decisionCriteria = new ArrayList();
        Criterion criterion;
        for(Iterator itr = probNet.getDecisionCriteria().iterator(); itr.hasNext(); decisionCriteria.add(criterion.clone()))
        {
            criterion = (Criterion)itr.next();
        }
        
        multicriteriaOptions = probNet.getInferenceOptions().getMultiCriteriaOptions().clone();
        temporalOptions = probNet.getInferenceOptions().getTemporalOptions().clone();
        
        boolean requiredInfereceOptions = false;
        
        if(isTemporal)
        {
            requiredInfereceOptions = true;
        }
        
        if(isMulticriteria)
        {
            requiredInfereceOptions = true;
        }

        if(onlyShowThisType != null)
        {
            if(onlyShowThisType.equals(org.openmarkov.core.inference.MulticriteriaOptions.Type.UNICRITERION))
            {
                probNet.getInferenceOptions().getMultiCriteriaOptions().setUnicriterionOptionsShowed(true);
            } 
            else if(onlyShowThisType.equals(org.openmarkov.core.inference.MulticriteriaOptions.Type.COST_EFFECTIVENESS))
            {
                probNet.getInferenceOptions().getMultiCriteriaOptions().setCeOptionsShowed(true);
            }
        } 
        else if(probNet.getInferenceOptions().getMultiCriteriaOptions().getMulticriteriaType().equals(org.openmarkov.core.inference.MulticriteriaOptions.Type.UNICRITERION))
        {
            probNet.getInferenceOptions().getMultiCriteriaOptions().setUnicriterionOptionsShowed(true);
        }
        else if(probNet.getInferenceOptions().getMultiCriteriaOptions().getMulticriteriaType().equals(org.openmarkov.core.inference.MulticriteriaOptions.Type.COST_EFFECTIVENESS))
        {
            probNet.getInferenceOptions().getMultiCriteriaOptions().setCeOptionsShowed(true);
        }
	}

	public MulticriteriaOptions getMulticriteriaOptions()
    {
        return multicriteriaOptions;
    }
}
