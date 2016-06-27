package com.boguenon.service.modules.bayes.learning;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseReader;
import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.learning.algorithm.hillclimbing.HillClimbingAlgorithm;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.Metric;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.annotation.MetricManager;
import org.openmarkov.learning.core.LearningManager;
import org.openmarkov.learning.core.algorithm.LearningAlgorithm;
import org.openmarkov.learning.core.exception.UnobservedVariablesException;
import org.openmarkov.learning.core.preprocess.Discretization;
import org.openmarkov.learning.core.preprocess.FilterDatabase;
import org.openmarkov.learning.core.preprocess.MissingValues;
import org.openmarkov.learning.core.util.ModelNetUse;

import com.boguenon.service.modules.bayes.CPGXML;

public class LearnNetwork {
	private CPGXML pgxml = null;
	private ProbNet modelNet = null;
	
	private CaseDatabase database = null;
	
	private String databasePath;
	private String algorithm;
	
	private boolean[] isNumeric;
	
	private boolean useNodePositions = false;
	private boolean startFromModelNet = false;
	private boolean addLinkModelNet = false;
	private boolean deleteLinksModelNet = false;
	private boolean invertLinksModelNet = false;
	
	private boolean automaticLearning = true;
	
	private List<CVariable> all_variables = null;
	
	public LearnNetwork(CPGXML pgxml, String databasePath) {
		this.pgxml = pgxml;
		this.databasePath = databasePath;
		this.algorithm = "Hill climbing";
	}

	public String learnNetwork()
    {
		String r = null;
    	ModelNetUse modelNetUse = null;
    	
        if ((databasePath == null) || (databasePath.equals ("")))
        {
            System.err.println("Learning.MustLoadACaseDatabase");
            return null;
        }
        
        loadCaseFile(this.databasePath);
        
        List<Variable> selectedVariables = getSelectedVariables();
        
        if (selectedVariables.isEmpty ())
        {
            System.err.println("Learning.MustChooseVariables");
            return null;
        }
        
        CaseDatabase preprocessedDatabase = null;
        CaseDatabase discretizedDB = null;
        
        try
        {
            preprocessedDatabase = FilterDatabase.filter(database, selectedVariables);
            preprocessedDatabase = MissingValues.process (preprocessedDatabase, getSelectedMissingValuesOptions ());
            discretizedDB = Discretization.process (preprocessedDatabase, getSelectedDiscretizeOptions (),
                                                                 getSelectedNumIntervals(), modelNet);
        }
        catch (Exception e)
        {
            System.err.println("Learning.ErrorPreprocessing");
            e.printStackTrace ();
            discretizedDB = null;
        }
        if (discretizedDB != null)
        {
            if (modelNet != null)
            {
                modelNetUse = new ModelNetUse (true, useNodePositions, startFromModelNet, addLinkModelNet,
                                               deleteLinksModelNet, invertLinksModelNet);
            }
            try
            {
                // Initialize learningManager
                LearningManager learningManager = new LearningManager (discretizedDB, algorithm, modelNet, modelNetUse);
                LearningAlgorithm learningAlgorithm = null;
                
                if (algorithm.equals("Hill climbing"))
                {
                	String metric = "K2";
                	String alphaParameter = "0.5";
                	learningAlgorithm = this.getHillClimbingAlgorithmInstance(learningManager.getLearnedNet(), discretizedDB, metric, alphaParameter);
                }
                else
                {
                    learningAlgorithm = learningManager.getAlgorithmInstance(algorithm);
                }
                
                if(learningAlgorithm == null)
                {
                    throw new InvalidParameterException("Unable to instance learning algorithm " + algorithm);
                }
                
                /* Get current time */
                long start = System.currentTimeMillis ();
                
                learningManager.init (learningAlgorithm);
                
                if (automaticLearning == true)
                {
                    learningManager.learn();
                    /* Get elapsed time in milliseconds */
                    long elapsedTimeMillis = System.currentTimeMillis() - start;
                    System.out.print("Learning.LearningFinished" + calculateTime(elapsedTimeMillis) + "\n");
                    if (modelNetUse == null || !modelNetUse.isUseModelNet()) 
                    {
                        // Place nodes in a sensible way
                        placeNodesInLearnedNet(learningManager.getLearnedNet());
                    }
                }
                else
                {
                    // INTERACTIVE LEARNING
                    // InteractiveLearningDialog interactiveLearningGUI = new InteractiveLearningDialog (false, learningManager);
                    // if( (modelNetUse == null) || (!modelNetUse.isUseNodePositions()) )
                    // {
                    //     placeNodesInCircle (learningManager.getLearnedNet ());
                    // }
                    // interactiveLearningGUI.setVisible (true);
                }
                
                ProbNet probNet = learningManager.getLearnedNet ();
                setProperName (probNet);
                
                this.pgxml.setProbNet(probNet);
                
                StringBuilder sb = new StringBuilder();
                this.pgxml.transform(sb);
                
                r = sb.toString();
                // NetworkPanel networkPanel = MainPanel.getUniqueInstance ().getMainPanelListenerAssistant ().createNewFrame (probNet);
            }
            catch (UnobservedVariablesException e1)
            {
            	System.err.println("Learning.Error.LatentVariables" + " :" + e1.getUnobservedVariables());
            }
            catch (Exception e)
            {
            	System.err.println("Learning.Error" + ":" + e.getMessage ());
                e.printStackTrace ();
            }catch (OutOfMemoryError e1) {
            	System.err.println("Learning.Error.OutOfMemory");
            }
        }
        
        return r;
    }
	
	public LearningAlgorithm getHillClimbingAlgorithmInstance(ProbNet probNet, CaseDatabase database, String metric, String alphaParameter)
    {
		MetricManager metricManager = new MetricManager();
		
        Metric metricInstance = null;
        try
        {
            Constructor<?>[] constructors = metricManager.getMetricByName (metric).getConstructors ();
            for (Constructor<?> constructor : constructors)
            {
                Class<?>[] parameterTypes = constructor.getParameterTypes ();
                if (parameterTypes.length == 1 && parameterTypes[0] == double.class) metricInstance = (Metric) constructor.newInstance (Double.parseDouble (alphaParameter));
                else if (parameterTypes.length == 0) metricInstance = (Metric) constructor.newInstance ();
            }
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e)
        {
            e.printStackTrace ();
        }        
        return new HillClimbingAlgorithm (probNet, database, Double.parseDouble (alphaParameter), metricInstance);
    }
    
    private void placeNodesInLearnedNet (ProbNet learnedNet)
    {
        double top =  0.0;
        double bottom =  600.0;
        double left =  100.0;
        double right =  800.0;
        
        ProbNet graph = learnedNet.copy ();
        List<List<Node>> nodesInLevels = new ArrayList<List<Node>>(); 
        while (!graph.getNodes ().isEmpty ())
        {
            // Look for the leaves
            List<Node> leaves = new ArrayList<> ();
            for(Node node : graph.getNodes())
            {
                if(node.getChildren ().isEmpty ())
                {
                    leaves.add (node);
                }
            }
            for(Node leave : leaves)
            {
                graph.removeNode (leave);
            }            
            nodesInLevels.add (leaves);
        }
        
        double verticalStep  = (bottom - top) / nodesInLevels.size ();
        double currentY = bottom;
        for(List<Node> nodes : nodesInLevels)
        {
            double currentX = left;
            double horizontalStep = (right - left) / nodes.size ();
            for(Node node : nodes)
            {
                Node realNode = null;
                try
                {
                    realNode = learnedNet.getNode(node.getName());
                    realNode.setCoordinateX (currentX);
                    realNode.setCoordinateY (currentY);
                }
                catch (NodeNotFoundException e)
                {
                }
                currentX += horizontalStep;
            }
            currentY -= verticalStep;
        }
        
    }   
        
    private List<Variable> getSelectedVariables() {
        List<Variable> variables = new ArrayList<Variable>();
        
        for (int i=0; i < all_variables.size(); i++) {
        	if (all_variables.get(i).isSelected() == true)
        	{
        		variables.add (database.getVariable(all_variables.get(i).getName()));
        	}
        }
        return variables;
    }
    
    private Map<String, MissingValues.Option> getSelectedMissingValuesOptions()
    {
        Map<String, MissingValues.Option> selectedPreprocessOptions = new HashMap<>();
        
        for (int i= 0; i < all_variables.size(); ++i) {
        	if (all_variables.get(i).isSelected() == true && all_variables.get(i).isMissingValue() == true)
        	{
	            String variableName = all_variables.get(i).getName();
	            MissingValues.Option  missingValuesOption = MissingValues.Option.valueOf(variableName);             
	            selectedPreprocessOptions.put(variableName, missingValuesOption);
        	}
        }        
        return selectedPreprocessOptions;
    }
    
    private Map<String, Discretization.Option> getSelectedDiscretizeOptions ()
    {
        Map<String, Discretization.Option> selectedDiscretizeOptions = new HashMap<>();
        
        for (int i= 0; i < this.all_variables.size(); ++i) {
        	if (this.all_variables.get(i).isSelected() == true && this.all_variables.get(i).isDiscretize() != null)
        	{
	            String variableName = this.all_variables.get(i).getName();
	            String discretize_option = this.all_variables.get(i).isDiscretize();
	            Discretization.Option discretizationOption = Discretization.Option.valueOf(discretize_option);
	            selectedDiscretizeOptions.put(variableName, discretizationOption);
        	}
        }        
        return selectedDiscretizeOptions; 
    }
    
    private Map<String, Integer> getSelectedNumIntervals ()
    {
        Map<String, Integer> selectedNumIntervals = new HashMap<>();
        
        for (int i= 0; i < this.all_variables.size(); ++i) {
        	if (this.all_variables.get(i).isSelected() == true)
        	{
        		int numIntervals = this.all_variables.get(i).getNumOfInterval();
        		
        		if (numIntervals > 0)
        		{
		            String variableName = this.all_variables.get(i).getName();
		            selectedNumIntervals.put(variableName, numIntervals);
        		}
        	}
        }        
        return selectedNumIntervals; 
    }

    private void loadCaseFile(String path)
    {
        if ((path != null) && (!path.equals ("")))
        {
        	CaseDatabaseManager caseDbManager = new CaseDatabaseManager();
        	
            CaseDatabaseReader reader = caseDbManager.getReader (FilenameUtils.getExtension (path));
            if (reader == null)
            {
                System.err.println("Learning.IncorrectCaseDatabaseFileFormat");
            }
            else
            {
                try
                {
                    // Load the database
                    if (databasePath != null)
                    {
                    	database = reader.load (databasePath);
                    	this.updateVariableSelectionPanel();
                    }
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private void updateVariableSelectionPanel(){
    	if (this.database != null)
    	{
    		isNumeric = new boolean[database.getVariables().size ()];
    		
    		int i = 0;
    		
    		this.all_variables = new ArrayList<CVariable>();
    		
    		for(Variable var : database.getVariables()){
    			boolean select = true;
                // if(modelNetVariablesRadioButton.isSelected ())
                {
                    // select = modelNet.containsVariable (var.getName());
                }
                
                String vname = var.getName();
                CVariable variable = new CVariable(vname, select);
                
                this.all_variables.add(variable);
                
                isNumeric[i] = Discretization.isNumeric (var);
                
                // boolean discretizeOptions = isNumeric[i] && discretize
                
                if (modelNet != null) {
                	try {
    					VariableType variableTypeInModel = modelNet.getNode(var.getName()).getVariable().getVariableType();
    					if (variableTypeInModel == VariableType.DISCRETIZED) {
    						// discretizeOptions.setSelectedItem(stringDatabase.getString("Learning.Discretize.ModelNet"));
    					}
    				} catch (NodeNotFoundException e) {
    				}
                }
                
                i++;
    		}
    	}
    }
    
    private static String calculateTime(long elapsedTimeMillis){
        
        StringBuffer timeString = new StringBuffer();
        int minutes, seconds;
        
        minutes = (int) (elapsedTimeMillis / 60000);
        elapsedTimeMillis -= minutes * 60000;
        seconds = (int) (elapsedTimeMillis / 1000);
        elapsedTimeMillis -= seconds * 1000;
        
        timeString.append(minutes + "' " + seconds + "\" " + elapsedTimeMillis + " ms."); 
        
        return timeString.toString();
    }
    
    private void setProperName(ProbNet probNet){
    	String name = databasePath.substring(databasePath.lastIndexOf('\\') + 1, databasePath.lastIndexOf('.')) + "Learning.NetSuffix"; 
    	probNet.setName(name);
    }
    
    private class CVariable
    {
    	private String name = null;
    	
    	private boolean selected = true;
    	private boolean is_missing_value = false;
    	private String discretize = Discretization.Option.NONE.toString();
    	
    	private int num_of_interval = 2;
    	
    	public CVariable(String name, boolean selected)
    	{
    		this.name = name;
    		this.selected = selected;
    	}
    	
    	public String getName()
    	{
    		return name;
    	}
    	
    	public boolean isSelected()
    	{
    		return selected;
    	}
    	
    	public boolean isMissingValue()
    	{
    		return is_missing_value;
    	}
    	
    	public String isDiscretize()
    	{
    		return discretize;
    	}
    	
    	public int getNumOfInterval()
    	{
    		return num_of_interval;
    	}
    }
}
