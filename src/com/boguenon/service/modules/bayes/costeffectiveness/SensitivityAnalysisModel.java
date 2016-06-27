package com.boguenon.service.modules.bayes.costeffectiveness;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.gui.dialog.inference.common.ScopeType;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.AxisVariation;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.sensitivityanalysis.gui.model.AnalysisType;
import org.openmarkov.sensitivityanalysis.gui.model.SensitivityAnalysisConfiguration;

public class SensitivityAnalysisModel {
	private AnalysisType analysisType;
    private List<UncertainParameter> selectedUncertainParametersXAxis;
    private List<UncertainParameter> selectedUncertainParametersYAxis;
    private AxisVariation horizontalAxisVariation;
    private AxisVariation verticalAxisVariation;
    private SensitivityAnalysisConfiguration configuration;
    private ScopeType scopeType;
    private Variable decisionVariable;
    private List<Finding> selectedScenario;
    private boolean throwErrorMessageIfProbAboveOne;
    private int numberOfIterationsSimulations;
    
	public SensitivityAnalysisModel()
    {
        analysisType = AnalysisType.TORNADO_SPIDER;
        selectedUncertainParametersXAxis = new ArrayList<UncertainParameter>();
        selectedUncertainParametersYAxis = new ArrayList<UncertainParameter>();
        horizontalAxisVariation = new AxisVariation();
        verticalAxisVariation = new AxisVariation();
        scopeType = ScopeType.GLOBAL;
        selectedScenario = new ArrayList<Finding>();
        throwErrorMessageIfProbAboveOne = false;
    }

    public AnalysisType getAnalysisType()
    {
        return analysisType;
    }

    public void setAnalysisType(AnalysisType analysisType)
    {
        this.analysisType = analysisType;
    }

    public List<UncertainParameter> getSelectedUncertainParametersXAxis()
    {
        return selectedUncertainParametersXAxis;
    }

    public void setSelectedUncertainParametersXAxis(List<UncertainParameter> selectedUncertainParametersXAxis)
    {
        this.selectedUncertainParametersXAxis = selectedUncertainParametersXAxis;
    }

    public List<UncertainParameter> getSelectedUncertainParametersYAxis()
    {
        return selectedUncertainParametersYAxis;
    }

    public void setSelectedUncertainParametersYAxis(List<UncertainParameter> selectedUncertainParametersYAxis)
    {
        this.selectedUncertainParametersYAxis = selectedUncertainParametersYAxis;
    }

    public AxisVariation getHorizontalAxisVariation()
    {
        return horizontalAxisVariation;
    }

    public void setHorizontalAxisVariation(AxisVariation horizontalAxisVariation)
    {
        this.horizontalAxisVariation = horizontalAxisVariation;
    }

    public AxisVariation getVerticalAxisVariation()
    {
        return verticalAxisVariation;
    }

    public void setVerticalAxisVariation(AxisVariation verticalAxisVariation)
    {
        this.verticalAxisVariation = verticalAxisVariation;
    }

    public SensitivityAnalysisConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(SensitivityAnalysisConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public ScopeType getScopeType()
    {
        return scopeType;
    }

    public void setScopeType(ScopeType scopeType)
    {
        this.scopeType = scopeType;
    }

    public Variable getDecisionVariable()
    {
        return decisionVariable;
    }

    public void setDecisionVariable(Variable decisionVariable)
    {
        this.decisionVariable = decisionVariable;
    }

    public List<Finding> getSelectedScenario()
    {
        return selectedScenario;
    }

    public void setSelectedScenario(List<Finding> selectedScenario)
    {
        this.selectedScenario = selectedScenario;
    }

    public boolean isThrowErrorMessageIfProbAboveOne()
    {
        return throwErrorMessageIfProbAboveOne;
    }

    public void setThrowErrorMessageIfProbAboveOne(boolean throwErrorMessageIfProbAboveOne)
    {
        this.throwErrorMessageIfProbAboveOne = throwErrorMessageIfProbAboveOne;
    }

    public int getNumberOfIterationsSimulations()
    {
        return numberOfIterationsSimulations;
    }

    public void setNumberOfIterationsSimulations(int numberOfIterationsSimulations)
    {
        this.numberOfIterationsSimulations = numberOfIterationsSimulations;
    }
    
    public void setChanged()
    {
    	
    }
    
}
