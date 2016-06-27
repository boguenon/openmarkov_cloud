package com.boguenon.service.modules.bayes.costeffectiveness;

import java.util.List;

import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.StringWithProperties;

import com.boguenon.service.modules.bayes.editor.EditorPanel;

public class CostEffectivenessDialog {
	private ProbNet probNet = null;
	private EditorPanel editorPanel = null;
	
	public CostEffectivenessDialog(ProbNet probNet, EditorPanel editorPanel) {
		this.probNet = probNet;
		this.editorPanel = editorPanel;
	}
	
	public String runCostEffectiveness(boolean sensitivityAnalysis)
    {
    	String r = null;
    	
    	EvidenceCase evidence = this.editorPanel.getPreResolutionEvidence();
    	
    	// CostEffectivenessAnalysis costEffectivenessAnalysis = null;
    	// CostEffectivenessResults ceaResults = null;
    	
    	List<Criterion> decisionCriteria = probNet.getDecisionCriteria();
    	
    	for (Node probNode : probNet.getNodes())
    	{
	        if (probNode.getNodeType() == NodeType.UTILITY && decisionCriteria != null)
	        {
	            probNode.getVariable().setDecisionCriterion(decisionCriteria.get (0));
	        }
    	}
    	
    	// CostEffectivenessAnalysisParams costEffectivenessAnalysisParams = new CostEffectivenessAnalysisParams(this.probNet, sensitivityAnalysis);
    	
//        if (sensitivityAnalysis) {
//            CostEffectivenessAnalysisRunner ceProgressBar = new CostEffectivenessAnalysisRunner(probNet, evidence, costEffectivenessAnalysisParams);
//            ceaResults = ceProgressBar.runAnalysis();
//        } else {
//            costEffectivenessAnalysis = new CostEffectivenessAnalysis(probNet,
//                    evidence,
//                    costEffectivenessAnalysisParams.getCostDiscount(),
//                    costEffectivenessAnalysisParams.getEffectivenessDiscount(),
//                    costEffectivenessAnalysisParams.getNumSlices(),
//                    costEffectivenessAnalysisParams.getInitialValues(),
//                    costEffectivenessAnalysisParams.getTransitionTime());
//            
//            
//            ceaResults = new CostEffectivenessResults(costEffectivenessAnalysis);
//        }
        
        // r = ceaResults.getResults();
        
        return r;
    }

}
