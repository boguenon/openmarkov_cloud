package com.boguenon.service.modules.bayes.costeffectiveness;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.modelUncertainty.AxisVariation;
import org.openmarkov.core.model.network.modelUncertainty.DeterministicAxisVariationType;
import org.openmarkov.core.model.network.modelUncertainty.UncertainParameter;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.tasks.VariableElimination.VEResolution;
import org.openmarkov.inference.tasks.VariableElimination.VESensAnTornadoSpider;
import org.openmarkov.sensitivityanalysis.gui.model.TornadoBar;

public class TornadoSpiderDialog {
	private VESensAnTornadoSpider veSensAnTornadoSpider;
    private java.util.List<UncertainParameter> uncertainParameters;
    private double utilityReference;
    private AxisVariation axisVariation;
    private int iterations;
    private ProbNet probNet;
    
	public TornadoSpiderDialog(ProbNet probNet, EvidenceCase preResolutionEvidence, SensitivityAnalysisModel sensitivityAnalysisModel) {
		veSensAnTornadoSpider = null;
        this.probNet = probNet;
        uncertainParameters = sensitivityAnalysisModel.getSelectedUncertainParametersXAxis();
        axisVariation = sensitivityAnalysisModel.getHorizontalAxisVariation();
        iterations = sensitivityAnalysisModel.getNumberOfIterationsSimulations();
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
            veSensAnTornadoSpider = new VESensAnTornadoSpider(probNet, preResolutionEvidence, uncertainParameters, axisVariation, iterations);
        }
        catch(NotEvaluableNetworkException e)
        {
            e.printStackTrace();
        }
	}
	
	public String getResult()
	{
		StringBuilder sb = new StringBuilder();
		
		getTornadoPanel(sb);
        getSpiderPanel(sb);
        
        return sb.toString();
	}
	
	private void getTornadoPanel(StringBuilder sb)
    {
        java.util.List<TornadoBar> tornadoBars = getTornadoBars();
        getTornadoChart(tornadoBars, sb);
    }
	
	private java.util.List<TornadoBar> getTornadoBars()
    {
        java.util.List<TornadoBar> tornadoBars = new ArrayList<TornadoBar>();
        TornadoBar tornadoBar;
        
        for(int n = 0; n < uncertainParameters.size(); n++)
        {
            UncertainParameter uncertainParameter = (UncertainParameter) uncertainParameters.get(n);
            Map<UncertainParameter, TablePotential> vspider = veSensAnTornadoSpider.getUncertainParametersPotentials(); 
            TablePotential potential = (TablePotential) vspider.get(uncertainParameter);
            double minValue = 1.7976931348623157E+308D;
            double maxValue = 4.9406564584124654E-324D;
            
            double arr$[] = potential.getValues();
            int len = arr$.length;
            
            for(int i = 0; i < len; i++)
            {
                double value = arr$[i];
                if(value < minValue)
                    minValue = value;
                if(value > maxValue)
                    maxValue = value;
            }

            tornadoBar = new TornadoBar(uncertainParameter, minValue, maxValue);
            tornadoBars.add(tornadoBar);
        }

        Collections.sort(tornadoBars);
        return tornadoBars;
    }
	
	private void getTornadoChart(java.util.List<TornadoBar> tornadoBars, StringBuilder sb)
    {
        int bars = uncertainParameters.size();
        String seriesKeys[] = {
            ""
        };
        
        String categoryKeys[] = new String[bars];
        Number starts[][] = new Number[1][bars];
        Number ends[][] = new Number[1][bars];
        int series = 0;
        double min = 1.7976931348623157E+308D;
        double max = 0.0D;
        double reductionCoef = Math.pow(10D, -6D);
        
        for(int i=0; i < tornadoBars.size(); i++)
        {
            TornadoBar tornadoBar = (TornadoBar) tornadoBars.get(i);
            categoryKeys[series] = tornadoBar.getUncertainParameter().getName();
            
            double minValue = tornadoBar.getMinValue();
            double maxValue = tornadoBar.getMaxValue();
            
            if(minValue == maxValue)
                if(minValue == 0.0D)
                {
                    minValue -= reductionCoef;
                    maxValue += reductionCoef;
                } else
                {
                    minValue -= minValue * reductionCoef;
                    maxValue += maxValue * reductionCoef;
                }
            min = minValue >= min ? min : minValue;
            max = maxValue <= max ? max : maxValue;
            starts[0][series] = Double.valueOf(minValue);
            ends[0][series] = Double.valueOf(maxValue);
            series++;
        }

        sb.append("<tornado>");
        sb.append("<series_key><![CDATA[" + arrayToString(seriesKeys) + "]]></series_key>");
        sb.append("<category_key><![CDATA[" + arrayToString(categoryKeys) + "]]></category_key>");
        
        sb.append("<value_axis min='" + (min - (max - min) * 0.050000000000000003D) + "' max='" + (max + (max - min) * 0.050000000000000003D) + "'>");
        sb.append("</value_axis>");
        sb.append("<reference_markers>");
        sb.append("<reference value='" + utilityReference + "'/>");
        sb.append("</reference_markers>");
        sb.append("<bars>");
        
        for (int i=0; i < bars; i++)
        {
        	sb.append("<bar starts='" + starts[0][i] + "' ends='" + ends[0][i] + "'></bar>");
        }
        
        sb.append("</bars>");
        
        sb.append("</tornado>");
    }
	
	private void getSpiderPanel(StringBuilder sb)
    {
        getSpiderChart(sb);
    }
	
	private void getSpiderChart(StringBuilder sb)
    {
		DecimalFormat decimalFormat = null;
		
        if(axisVariation.getVariationType().equals(DeterministicAxisVariationType.PORV) || axisVariation.getVariationType().equals(DeterministicAxisVariationType.POPP))
            decimalFormat = new DecimalFormat("+##.##%;-##.##%");
        else
        if(axisVariation.getVariationType().equals(DeterministicAxisVariationType.RORV) || axisVariation.getVariationType().equals(DeterministicAxisVariationType.UDIN))
            decimalFormat = new DecimalFormat("0.000;-0.000");
        
        double minVariationValue;
        double maxVariationValue;
        
        if(axisVariation.getVariationType().equals(DeterministicAxisVariationType.UDIN))
        {
            minVariationValue = axisVariation.getVariationBounds()[0];
            maxVariationValue = axisVariation.getVariationBounds()[1];
        } 
        else
        {
            minVariationValue = -axisVariation.getVariationValue() / 100D;
            maxVariationValue = axisVariation.getVariationValue() / 100D;
        }
        
        double variationInterval = (maxVariationValue - minVariationValue) / (double)iterations;
        double minRangeUtility = 1.7976931348623157E+308D;
        double maxRangeUtility = 4.9406564584124654E-324D;
        
        sb.append("<spider_chart>");
        
        sb.append("<axis xaxis='" + axisVariation.getVariationType().toString() + "' yaxis='" + "ExpectedUtility" + "' min='" + minVariationValue + "' max='" + maxVariationValue + "'></axis>");
        
        sb.append("<reference_markers>");
        sb.append("<reference value='" + utilityReference + "'/>");
        sb.append("</reference_markers>");
        
        sb.append("<series>");
        
        for (int n=0; n < uncertainParameters.size(); n++)
        {
            UncertainParameter uncertainParameter = (UncertainParameter) uncertainParameters.get(n);
            TablePotential uncertainParameterPotential = (TablePotential)veSensAnTornadoSpider.getUncertainParametersPotentials().get(uncertainParameter);
            // XYSeries series = new XYSeries();
            sb.append("<series name='" + uncertainParameter.getName() + "'>");

            int horizontalIteration = 0;
            double arr$[] = uncertainParameterPotential.getValues();
            int len$ = arr$.length;
            
            String x_axis = "";
            String y_axis = "";
            
            for(int i = 0; i < len$; i++)
            {
                double value = arr$[i];
                if(value < minRangeUtility)
                    minRangeUtility = value;
                if(value > maxRangeUtility)
                    maxRangeUtility = value;
                double variationValue = minVariationValue + variationInterval * (double)horizontalIteration;
                
                x_axis = (i == 0) ? "" + variationValue : x_axis + "\t" + variationValue;
                y_axis = (i == 0) ? "" + value : y_axis + "\t" + value;
                // series.add(variationValue, value);
                horizontalIteration++;
            }
            
            sb.append("<xvalues><![CDATA[" + x_axis + "]]></xvalues>");
            sb.append("<yvalues><![CDATA[" + y_axis + "]]></yvalues>");

            sb.append("</series>");
        }
        
        sb.append("</series>");
        
        sb.append("</spider_chart>");
    }
	
	private String arrayToString(String[] values)
	{
		String r = "";
		
		for (int i=0; i < values.length; i++)
		{
			String c = values[i];
			c = c == null ? "" : c;
			
			r = i == 0 ? c : r + "\t" + c;
		}
		
		return r;
	}
}
