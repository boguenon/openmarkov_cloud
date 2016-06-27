package com.boguenon.service.modules.bayes.costeffectiveness;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.gui.dialog.inference.common.ScopeType;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.AxisVariation;
import org.openmarkov.core.model.network.modelUncertainty.DeterministicAxisVariationType;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.tasks.VariableElimination.VEResolution;
import org.openmarkov.inference.tasks.VariableElimination.VESensAnPlot;

public class PlotDialog {
	private VESensAnPlot veSensAnPlot;
    private UncertainParameter uncertainParameter;
    private double utilityReference;
    private AxisVariation axisVariation;
    private int iterations;
    private Variable decisionVariable;
    private java.util.List<Finding> selectedScenario;
    private ProbNet probNet;
    
	public PlotDialog(ProbNet probNet, EvidenceCase preResolutionEvidence, SensitivityAnalysisModel sensitivityAnalysisModel) 
	{
		this.probNet = probNet;
        uncertainParameter = (UncertainParameter)sensitivityAnalysisModel.getSelectedUncertainParametersXAxis().get(0);
        axisVariation = sensitivityAnalysisModel.getHorizontalAxisVariation();
        iterations = sensitivityAnalysisModel.getNumberOfIterationsSimulations();
        decisionVariable = sensitivityAnalysisModel.getDecisionVariable();
        selectedScenario = sensitivityAnalysisModel.getSelectedScenario();
        
        try
        {
            VEResolution veResolution = new VEResolution(probNet, preResolutionEvidence, null);
            utilityReference = veResolution.getUtility().getValues()[0];
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            veSensAnPlot = new VESensAnPlot(probNet, preResolutionEvidence, uncertainParameter, axisVariation, iterations, decisionVariable);
        }
        catch(NotEvaluableNetworkException e)
        {
            e.printStackTrace();
        }
	}

	public String getResult()
	{
		StringBuilder sb = new StringBuilder();
		
		getPlotChart(sb);
		
        return sb.toString();
	}
	
	private void getPlotChart(StringBuilder sb)
	{
        double minVariationValue;
        double maxVariationValue;
        if(axisVariation.getVariationType().equals(DeterministicAxisVariationType.UDIN))
        {
            minVariationValue = axisVariation.getVariationBounds()[0];
            maxVariationValue = axisVariation.getVariationBounds()[1];
        } else
        {
            minVariationValue = -axisVariation.getVariationValue() / 100D;
            maxVariationValue = axisVariation.getVariationValue() / 100D;
        }
        double variationInterval = (maxVariationValue - minVariationValue) / (double)iterations;
        double minRangeUtility = 1.7976931348623157E+308D;
        double maxRangeUtility = 4.9406564584124654E-324D;
        TablePotential uncertainParameterPotential = (TablePotential)veSensAnPlot.getUncertainParametersPotentials().get(uncertainParameter);
        int decisionVariableStates = 0;
        if(decisionVariable != null)
            decisionVariableStates += decisionVariable.getNumStates();
        else
            decisionVariableStates = 1;
        
        String scopeSubtitle = null;
        boolean legend = true;
        
        if(decisionVariable != null)
        {
            String decisionScopeSubtitle = "ScopeSelector.Title " + ScopeType.DECISION.toString().toLowerCase() + " - ";
            for(int i=0; i < selectedScenario.size(); i++)
            {
                Finding finding = selectedScenario.get(i);
                decisionScopeSubtitle = decisionScopeSubtitle + finding.getVariable() + ": " + finding.getState() + ", ";
            }

            decisionScopeSubtitle = decisionScopeSubtitle.substring(0, decisionScopeSubtitle.length() - 2);
            scopeSubtitle = decisionScopeSubtitle;
        } 
        else
        {
            scopeSubtitle = "ScopeSelector.Title " + ScopeType.GLOBAL.toString().toLowerCase();
            legend = false;
        }
        
        sb.append("<plot_chart legend='" + (legend ? "T" : "F") + "'>");
        
        if (scopeSubtitle != null && scopeSubtitle.length() > 0)
        {
        	sb.append("<subtitle><![CDATA[" + scopeSubtitle + "]]></subtitle>");
        }
        
        sb.append("<axis xaxis='" + axisVariation.getVariationType().toString() + "' yaxis='" + "ExpectedUtility" + "' min='" + minVariationValue + "' max='" + maxVariationValue + "'></axis>");
        
        sb.append("<reference_markers>");
        sb.append("<reference value='" + utilityReference + "'/>");
        sb.append("</reference_markers>");
        
        sb.append("<series>");
        
        for(int decisionStateIndex = 0; decisionStateIndex < decisionVariableStates; decisionStateIndex++)
        {
        	String seriesname = null;
        	if(decisionVariable != null)
        		seriesname = (new StringBuilder()).append(decisionVariable.getName()).append(" = ").append(decisionVariable.getStateName(decisionStateIndex)).toString();
            else
                seriesname = uncertainParameter.getName();
        	
            sb.append("<series name='" + seriesname + "'>");

            String x_axis = "";
            String y_axis = "";
            
            int horizontalIteration = 0;
            for(int valueIndex = 0; valueIndex < iterations; valueIndex++)
            {
                int globalValueIndex = valueIndex + decisionStateIndex * (iterations + 1);
                double value = uncertainParameterPotential.getValues()[globalValueIndex];
                if(value < minRangeUtility)
                    minRangeUtility = value;
                if(value > maxRangeUtility)
                    maxRangeUtility = value;
                double variationValue = minVariationValue + variationInterval * (double)horizontalIteration;
                
                x_axis = (valueIndex == 0) ? "" + variationValue : x_axis + "\t" + variationValue;
                y_axis = (valueIndex == 0) ? "" + value : y_axis + "\t" + value;
                
                horizontalIteration++;
            }
            
            sb.append("<xvalues><![CDATA[" + x_axis + "]]></xvalues>");
            sb.append("<yvalues><![CDATA[" + y_axis + "]]></yvalues>");

            sb.append("</series>");
        }
        
        sb.append("</series>");
        
        sb.append("</plot_chart>");
	}
}
