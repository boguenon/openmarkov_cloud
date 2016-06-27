package com.boguenon.service.modules.bayes.costeffectiveness;

import java.text.DecimalFormat;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.AxisVariation;
import org.openmarkov.core.model.network.modelUncertainty.DeterministicAxisVariationType;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.tasks.VariableElimination.VESensAnMap;
import org.openmarkov.sensitivityanalysis.gui.model.ScopeType;

public class MapDialog {
	private VESensAnMap veSensAnMap;
    private UncertainParameter hUncertainParameter;
    private UncertainParameter vUncertainParameter;
    private AxisVariation hAxisVariation;
    private AxisVariation vAxisVariation;
    private int iterations;
    private Variable decisionVariable;
    private java.util.List<Finding> selectedScenario;
    private ProbNet probNet;
    
	public MapDialog(ProbNet probNet, EvidenceCase preResolutionEvidence, SensitivityAnalysisModel sensitivityAnalysisModel) 
	{
		this.probNet = probNet;
        hUncertainParameter = (UncertainParameter)sensitivityAnalysisModel.getSelectedUncertainParametersXAxis().get(0);
        vUncertainParameter = (UncertainParameter)sensitivityAnalysisModel.getSelectedUncertainParametersYAxis().get(0);
        hAxisVariation = sensitivityAnalysisModel.getHorizontalAxisVariation();
        vAxisVariation = sensitivityAnalysisModel.getVerticalAxisVariation();
        iterations = sensitivityAnalysisModel.getNumberOfIterationsSimulations();
        decisionVariable = sensitivityAnalysisModel.getDecisionVariable();
        selectedScenario = sensitivityAnalysisModel.getSelectedScenario();
        try
        {
            veSensAnMap = new VESensAnMap(probNet, preResolutionEvidence, hUncertainParameter, hAxisVariation, vUncertainParameter, vAxisVariation, iterations, decisionVariable);
        }
        catch(NotEvaluableNetworkException e)
        {
            e.printStackTrace();
        }
	}
	
	public String getResult()
	{
		StringBuilder sb = new StringBuilder();
		
		getMapChart(sb);
		
        return sb.toString();
	}
	
	private void getMapChart(StringBuilder sb)
	{
		DecimalFormat hDecimalFormat = null;
        if(hAxisVariation.getVariationType().equals(DeterministicAxisVariationType.PORV) || hAxisVariation.getVariationType().equals(DeterministicAxisVariationType.POPP))
            hDecimalFormat = new DecimalFormat("+##.##%;-##.##%");
        else
        if(hAxisVariation.getVariationType().equals(DeterministicAxisVariationType.RORV) || hAxisVariation.getVariationType().equals(DeterministicAxisVariationType.UDIN))
            hDecimalFormat = new DecimalFormat("0.000;-0.000");
        
        double hMinVariationValue;
        double hMaxVariationValue;
        if(hAxisVariation.getVariationType().equals(DeterministicAxisVariationType.UDIN))
        {
            hMinVariationValue = hAxisVariation.getVariationBounds()[0];
            hMaxVariationValue = hAxisVariation.getVariationBounds()[1];
        } 
        else
        {
            hMinVariationValue = -hAxisVariation.getVariationValue() / 100D;
            hMaxVariationValue = hAxisVariation.getVariationValue() / 100D;
        }
        
        double hVariationInterval = (hMaxVariationValue - hMinVariationValue) / (double)iterations;
        
        DecimalFormat vDecimalFormat = null;
        if(vAxisVariation.getVariationType().equals(DeterministicAxisVariationType.PORV) || vAxisVariation.getVariationType().equals(DeterministicAxisVariationType.POPP))
            vDecimalFormat = new DecimalFormat("+##.##%;-##.##%");
        else
        if(vAxisVariation.getVariationType().equals(DeterministicAxisVariationType.RORV) || vAxisVariation.getVariationType().equals(DeterministicAxisVariationType.UDIN))
            vDecimalFormat = new DecimalFormat("0.000;-0.000");
        
        double vMinVariationValue;
        double vMaxVariationValue;
        
        if(hAxisVariation.getVariationType().equals(DeterministicAxisVariationType.UDIN))
        {
            vMinVariationValue = hAxisVariation.getVariationBounds()[0];
            vMaxVariationValue = hAxisVariation.getVariationBounds()[1];
        } else
        {
            vMinVariationValue = -hAxisVariation.getVariationValue() / 100D;
            vMaxVariationValue = hAxisVariation.getVariationValue() / 100D;
        }
        
        double vVariationInterval = (vMaxVariationValue - vMinVariationValue) / (double)iterations;
        double minRangeUtility = 1.7976931348623157E+308D;
        double maxRangeUtility = 4.9406564584124654E-324D;
        
        TablePotential uncertainParameterPotential = (TablePotential)veSensAnMap.getUncertainParametersPotentials().get(hUncertainParameter);
        int decisionVariableStates = 0;

        if(decisionVariable != null)
            decisionVariableStates += decisionVariable.getNumStates();
        else
            decisionVariableStates = 1;
        
        int totalIterations = iterations + 1;
        
        double xvalues[] = new double[totalIterations * totalIterations];
        double yvalues[] = new double[totalIterations * totalIterations];
        double zvalues[] = new double[totalIterations * totalIterations];
        
        for(int row = 0; row < totalIterations; row++)
        {
            for(int column = 0; column < totalIterations; column++)
            {
                int dataPosition = row * totalIterations + column;
                double zAxisValue = uncertainParameterPotential.getValues()[dataPosition];
                if(decisionVariable != null)
                {
                    int greatUtilityStateIndex = 0;
                    for(int decisionVariableStateIndex = 1; decisionVariableStateIndex < decisionVariableStates; decisionVariableStateIndex++)
                    {
                        int dataPositionForThatDecision = dataPosition + decisionVariableStateIndex * totalIterations * totalIterations;
                        double zAxisValueForThatDecision = uncertainParameterPotential.getValues()[dataPositionForThatDecision];
                        if(zAxisValueForThatDecision > zAxisValue)
                        {
                            greatUtilityStateIndex = decisionVariableStateIndex;
                            continue;
                        }
                        if(zAxisValueForThatDecision == zAxisValue)
                            greatUtilityStateIndex = -1;
                    }

                    zAxisValue = greatUtilityStateIndex;
                }
                if(zAxisValue < minRangeUtility)
                    minRangeUtility = zAxisValue;
                if(zAxisValue > maxRangeUtility)
                    maxRangeUtility = zAxisValue;
                double xAxisValue = hMinVariationValue + hVariationInterval * (double)column;
                double yAxisValue = vMinVariationValue + vVariationInterval * (double)row;
                xvalues[dataPosition] = xAxisValue;
                yvalues[dataPosition] = yAxisValue;
                zvalues[dataPosition] = zAxisValue;
            }

        }
        
        String s_xvalues = "";
        String s_yvalues = "";
        String s_zvalues = "";
        
        for (int i=0; i < xvalues.length; i++)
        {
        	s_xvalues = (i == 0) ? "" + xvalues[i] : s_xvalues + "," + xvalues[i];
        	s_yvalues = (i == 0) ? "" + yvalues[i] : s_yvalues + "," + yvalues[i];
        	s_zvalues = (i == 0) ? "" + zvalues[i] : s_zvalues + "," + zvalues[i];
        }
        
        String scopeSubtitle;
        boolean legend = true;
        
        if(decisionVariable != null)
        {
            String decisionScopeSubtitle = "ScopeSelector.Title " + ScopeType.DECISION.toString().toLowerCase() + " - ";
            for(int i = 0; i < selectedScenario.size(); i++)
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
        
        sb.append("<map_chart legend='" + (legend ? "T" : "F") + "'>");
        
        if (scopeSubtitle != null && scopeSubtitle.length() > 0)
        {
        	sb.append("<subtitle><![CDATA[" + scopeSubtitle + "]]></subtitle>");
        }
        
        sb.append("<xdata><![CDATA[" + s_xvalues + "]]></xdata>");
        sb.append("<ydata><![CDATA[" + s_yvalues + "]]></ydata>");
        sb.append("<zdata><![CDATA[" + s_zvalues + "]]></zdata>");
        sb.append("</map_chart>");

        if(decisionVariable != null)
        {
        	sb.append("<legends>");
            
            for(int stateIndex = 0; stateIndex < decisionVariableStates; stateIndex++)
            {
                String l = decisionVariable.getName() + " = " + decisionVariable.getStateName(stateIndex);
                sb.append("<legend name='" + l + "'></legend>");
            }

            sb.append("</legends>");
        } 
        else
        {
            sb.append("<range min='" + minRangeUtility + "' max='" + maxRangeUtility + "'></range>");
        }
	}
}
