package com.boguenon.service.modules.bayes.costeffectiveness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.modelUncertainty.SystematicSampling;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.sensitivityanalysis.gui.model.AnalysisType;
import org.openmarkov.sensitivityanalysis.gui.model.SensitivityAnalysisConfiguration;

public class SensitivityAnalysisController {
	private SensitivityAnalysisModel sensitivityAnalysisModel;
	private SensitivityAnalysisConfiguration configuration;
	
	private ProbNet probNet;
    private EvidenceCase preResolutionEvidence;
    private HashMap<String, UncertainParameter> uncertainParameters;
    private java.util.List<String> orderedUncertainParametersKeys;
    private java.util.List sensitivityAnalysisPlots;
    
    private EvidenceCase evidence;
	
	public SensitivityAnalysisController(ProbNet probNet, EvidenceCase evidence) {
		sensitivityAnalysisModel = new SensitivityAnalysisModel();
        configuration = new SensitivityAnalysisConfiguration();
        uncertainParameters = new HashMap<String, UncertainParameter>();
        sensitivityAnalysisPlots = new ArrayList();
        
        this.probNet = probNet;
        this.evidence = evidence;
        
        preResolutionEvidence = new EvidenceCase(evidence);

        if(probNet.getInferenceOptions().getMultiCriteriaOptions().getMulticriteriaType().equals(org.openmarkov.core.inference.MulticriteriaOptions.Type.UNICRITERION))
        {
            configuration.setIsUnicriterion(true);
        }
        else
        {
            configuration.setIsUnicriterion(false);
        }
        java.util.List<UncertainParameter> listUncertainParameters = SystematicSampling.getUncertainParameters(probNet);
        orderedUncertainParametersKeys = new ArrayList<String>();
        int unnamedParameter = 1;
        Iterator itr = listUncertainParameters.iterator();
        
        while (itr.hasNext())
        {
            UncertainParameter uncertainParameter = (UncertainParameter)itr.next();
            String parameterName = uncertainParameter.getName();
            if(parameterName != null && !parameterName.isEmpty())
            {
                uncertainParameters.put(parameterName, uncertainParameter);
                orderedUncertainParametersKeys.add(parameterName);
            }
        }
	}
	
	public SensitivityAnalysisModel getSensitivityAnalysisModel()
    {
        return sensitivityAnalysisModel;
    }

    public SensitivityAnalysisConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(SensitivityAnalysisConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public ProbNet getProbNet()
    {
        return probNet;
    }

    public void setProbNet(ProbNet probNet)
    {
        this.probNet = probNet;
    }

    public HashMap<String, UncertainParameter> getUncertainParameters()
    {
        return uncertainParameters;
    }

    public CostEffectivenessResults runAnalysis()
    {
    	CostEffectivenessResults r = new CostEffectivenessResults();
    	
        AnalysisType sensitivityAnalysisType = sensitivityAnalysisModel.getAnalysisType();

        if(sensitivityAnalysisType.equals(AnalysisType.TORNADO_SPIDER)) 
        {
        	TornadoSpiderDialog dlg = new TornadoSpiderDialog(probNet, preResolutionEvidence, sensitivityAnalysisModel);
        	r.setResult(dlg.getResult(), "tornado_spider");
        }
        else if(sensitivityAnalysisType.equals(AnalysisType.PLOT))
        {
        	PlotDialog dlg = new PlotDialog(probNet, preResolutionEvidence, sensitivityAnalysisModel);
        	r.setResult(dlg.getResult(), "plot");
        }
        else if(sensitivityAnalysisType.equals(AnalysisType.MAP))
        {
        	MapDialog dlg = new MapDialog(probNet, preResolutionEvidence, sensitivityAnalysisModel);
        	r.setResult(dlg.getResult(), "map");
        }
        else if(sensitivityAnalysisType.equals(AnalysisType.CEPLANE))
        {
        	// CEProbabilisticDialog dlg = new CEProbabilisticDialog(probNet, preResolutionEvidence, sensitivityAnalysisModel);
        	// r.setResult(dlg.getResult(), "ceplane");
        }
        
        preResolutionEvidence = new EvidenceCase(evidence);
        
        return r;
    }

    public void setPreResolutionEvidence(EvidenceCase preResolutionEvidence)
    {
        this.preResolutionEvidence = preResolutionEvidence;
    }

    public EvidenceCase getPreResolutionEvidence()
    {
        return preResolutionEvidence;
    }
    
    public java.util.List<String> getOrderedUncertainParametersKeys()
    {
        return orderedUncertainParametersKeys;
    }
}
