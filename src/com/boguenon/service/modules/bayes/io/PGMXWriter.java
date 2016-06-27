package com.boguenon.service.modules.bayes.io;

import java.util.List;
import java.util.Map;

import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionType;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.ConditionalGaussianPotential;
import org.openmarkov.core.model.network.potential.DeltaPotential;
import org.openmarkov.core.model.network.potential.ExponentialHazardPotential;
import org.openmarkov.core.model.network.potential.ExponentialPotential;
import org.openmarkov.core.model.network.potential.GLMPotential;
import org.openmarkov.core.model.network.potential.LinearCombinationPotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.WeibullHazardPotential;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.plugin.PotentialType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.core.model.network.type.plugin.NetworkTypeManager;
import org.openmarkov.io.probmodel.strings.XMLValues;

import com.boguenon.service.modules.bayes.CPGXML;

public class PGMXWriter {

	private Map<Node, String> _probsid = null;
	private ProbNet probNet = null;
	private String instanceid = null;
	
	public PGMXWriter(ProbNet probNet, Map<Node, String> _probsid, String instanceid) {
		this.probNet = probNet;
		this._probsid = _probsid;
		this.instanceid = instanceid;
	}
	
	public void transform(StringBuilder sb)
	{
		sb.append("<smsg><item instanceid='" + this.instanceid + "'>");
		if (this.probNet != null)
		{
			String nettype = this.probNet.getNetworkType().toString();
			
			if (CPGXML.networkTypeManager == null)
			{
				CPGXML.networkTypeManager = new NetworkTypeManager();
			}
			
			String nettypename = CPGXML.networkTypeManager.getName(this.probNet.getNetworkType());
			
			sb.append("<probnet>");
			
			String comment = probNet.getComment();
			
			if (comment != null && comment.length() > 0)
			{
				sb.append("<comment><![CDATA[" + comment + "]]></comment>");
			}
			
			this.getAdditionalConstraints(sb, probNet);
			
			this.getAdditionalProperties(sb, probNet);
			
			this.getDecisionCriteria(sb, probNet);
			
			sb.append("</probnet>");
			
			sb.append("<nodes type='" + nettype + "' typename='" + nettypename + "'>");
			
			getVariables(sb, probNet);
			
			sb.append("</nodes>");
			
			sb.append("<links>");
			
			this.getLinks(sb, this.probNet);
			
			sb.append("</links>");
			
			sb.append("<potentials>");
			
			List<Potential> potentials = this.probNet.getPotentials();
			
			if (potentials != null)
			{
				for (Potential potential : potentials)
				{
					Variable potentialVariable = null;
					
					if (potential.getVariables().isEmpty()
		                    || potential.getPotentialRole() == PotentialRole.UTILITY) {
		                potentialVariable = potential.getUtilityVariable();
		            } else {
		                potentialVariable = potential.getVariable(0);
		            }
					
					if ((probNet.getNode(potentialVariable).getNodeType() != NodeType.DECISION) && (potential.getPotentialRole() != PotentialRole.POLICY))
					{
						appendPotential(sb, potential);
					}
				}
			}
			
			sb.append("</potentials>");
		}
		sb.append("</item></smsg>");
	}
	
	
	protected void getAdditionalProperties(StringBuilder sb, ProbNet probNet) 
	{
		sb.append("<additional_properties>");
		
        for (String propertyName : probNet.additionalProperties.keySet()) {
            if (probNet.additionalProperties.get(propertyName) != null) {
            	
                sb.append("<property name='" + propertyName + "' value='" + probNet.additionalProperties.get(propertyName).toString() + "'></property>");
            }
        }
        
        sb.append("</additional_properties>");
    }
	
	protected void getAdditionalConstraints(StringBuilder sb, ProbNet probNet) {
        List<PNConstraint> constraints = probNet.getAdditionalConstraints();
        NetworkType networkType = probNet.getNetworkType();
        if (constraints.size() > 1) {
        	sb.append("<additional_constraints>");
            for (int i = 1; i < constraints.size(); i++) {
                // TODO To implement the getArguments method in constraints
                /*
                 * if (constraints.get(i).getArguments() != null){ close =
                 * false; }
                 */
                PNConstraint constraint = constraints.get(i);
                if (!networkType.isApplicableConstraint(constraint)) {
                	sb.append("<constraint name='" + constraint.toString() + "'></constraint>");
                }
                // To be extended here when the arguments of the restrictions
                // are available
                // TODO Eliminar XMLBasicConstraints y XMLCompoundConstraints
                // TODO revisar que el toString de cada constraint sea correcto
            }
            sb.append("</additional_constraints>");
        }
    }

	protected void getAlwaysObservedAttribute(StringBuilder sb, Node probNode) {
        if (probNode.isAlwaysObserved()) {
        	sb.append("<always_observed></always_observed>");
        }
    }
	
	protected void getAdditionalProperties(StringBuilder sb, Node node) 
	{
        if (!node.getPurpose().isEmpty()) 
        {
            sb.append("<property name='purpose' value='" + node.getPurpose() + "'></property>");
        }
        
        if (node.getRelevance() != ProbNode.defaultRelevance) {
        	sb.append("<property name='relevance' value='" + String.valueOf(node.getRelevance()) + "'></property>");
        }
        for (String propertyKey : node.additionalProperties.keySet()) {
            String propertyValue = node.additionalProperties.get(propertyKey);
            sb.append("<property name='" + propertyKey + "' value='" + propertyValue + "'></property>");
        }
    }
	
	protected void getDecisionCriteria(StringBuilder sb, ProbNet probNet) {
        List<Criterion> decisionCritera = probNet.getDecisionCriteria();
        if (decisionCritera != null && !decisionCritera.isEmpty()) {
        	sb.append("<decision_criteria>");
            // for (String criterionName : decisionCritera.getNames()) {
            for (int i = 0; i < decisionCritera.size(); i++) {
                // getCriteria(decisionCriteriaElement, new
                // Element(XMLTags.CRITERION.toString()),
                // criterionName, decisionCritera.getProperties(criterionName));
            	
                getCriteria(sb, decisionCritera.get(i));

            }
            sb.append("</decision_criteria>");
        }
    }
	
	protected void getCriteria(StringBuilder sb, Criterion criterion) {
		
		sb.append("<criterion name='" + criterion.getCriterionName() + "' unit='" + criterion.getCriterionUnit() + "'>");        
        sb.append("</criterion>");
    }
	
	private void getVariables(StringBuilder sb, ProbNet probNet)
	{
		if (probNet.getNumNodes() > 0) {
            for (Node probNode : probNet.getNodes()) 
            {
                getVariable(sb, probNode);
            }
        }
	}
	
	private void getVariable(StringBuilder sb, Node probNode)
	{
		String pnodename = probNode.getName();
		
		sb.append("<node");
		// writeVariableName(sb, probNode.getVariable());
		
        String variableType = probNode.getVariable().getVariableType().toString();
        sb.append(" type='" + variableType + "'");
        String nodeType = probNode.getNodeType().toString();
        sb.append(" role='" + nodeType + "'");
        sb.append(" shape='" + CPGXML.getShape(probNode.getNodeType()) + "'");
        
        double m_x = probNode.getCoordinateX();
		double m_y = probNode.getCoordinateY();
		
		String nodetypename = probNode.getNodeType().name().toLowerCase();
		
		sb.append(" name='" + nodetypename + "'");
		
		sb.append(" x='" + Double.toString(m_x) + "' y='" + Double.toString(m_y) + "'");
		sb.append(" w='200' h='60'");
		sb.append(" sid='" + _probsid.get(probNode) + "'");
        
        // OOPN start
        sb.append(" is_input='" + (probNode.isInput() ? "T" : "F") + "'>");
        
        sb.append("<label><![CDATA[" + pnodename + "]]></label>");
        
        // OOPN end
        getVariableChildren(sb, probNode);
        
        sb.append("</node>");
	}
	
	protected void getVariableChildren(StringBuilder sb, Node probNode) {
        // getCommment(sb, probNode);
        // TODO verificar que las coordenadas sean validas no null
        // getCoordinates(variableElement, probNode);
		
		String comment = probNode.getComment();
		
		if (comment != null && comment.length() > 0)
		{
			sb.append("<comment><![CDATA[" + comment + "]]></comment>");
		}
		
		sb.append("<additional_properties>");
        getAdditionalProperties(sb, probNode);
        sb.append("</additional_properties>");
        String unit = probNode.getVariable().getUnit().getString();
        if (unit != null)
        {
        	sb.append("<unit>");
            sb.append(String.valueOf(unit));
            sb.append("</unit>");
        }
        
        getAlwaysObservedAttribute(sb, probNode);
        
        double precision = probNode.getVariable().getPrecision();
        
        if (Double.isNaN(precision))
        {
        	sb.append("<precision>");
        	sb.append(String.valueOf(precision));
            sb.append("</precision>");
        }
        
        NodeType nodeType = probNode.getNodeType();
        
        if (nodeType == NodeType.UTILITY) {
            getDecisionCriteria(sb, probNode, "decision_criteria");
        }
        // states reading
        switch (probNode.getVariable().getVariableType()) 
        {
        case NUMERIC:
        case DISCRETIZED:
            if (probNode.getNodeType() != NodeType.UTILITY)
            {
            	sb.append("<thresholds>");
                getThresholds(sb, probNode.getVariable().getPartitionedInterval());
                sb.append("</thresholds>");
            }
            break;
        case FINITE_STATES:
        	sb.append("<states>");
            getStates(sb, probNode);
            sb.append("</states>");
            break;
        }
    }
	
	protected void getLinks(StringBuilder sb, ProbNet probNet) {
        if (probNet.getLinks().size() > 0) 
        {
            for (Link<Node> link : probNet.getLinks()) {
                sb.append("<link");
                
                Node p1 = (Node) link.getNode1();
    			Node p2 = (Node) link.getNode2();
                
                sb.append(" from='" + _probsid.get(p1) + "'");
        		sb.append(" to='" + _probsid.get(p2) + "'");
                sb.append(" directed='" + (link.isDirected() ? "T" : "F") + "'");
                sb.append(" dashstyle='F'");
                
                /*
                 * linkElement.setAttribute( XMLAttributes.VAR1.toString(), ( (
                 * ProbNode )link.getNode1().getObject() ).getName() );
                 * linkElement.setAttribute( XMLAttributes.VAR2.toString(), ( (
                 * ProbNode )link.getNode2().getObject() ).getName() );
                 */
                
                sb.append(">");
                if (link.hasRestrictions()) {
                	appendLinkRestriction(sb, link);
                }
                if (link.hasRevealingConditions()) {
                	appendRevealingConditions(sb, link);
                }
                // linkElement.addContent(varaibleElement)
                // TODO Write comment
                // TODO Write label
                // TODO Write additional additionalProperties
                
                sb.append("</link>");
            }
        }
    }
	
	protected void getDecisionCriteria(StringBuilder sb, Node probNode, String nodename) {
		if (probNode.getVariable().getDecisionCriterion() != null) {
			sb.append("<" + nodename + " name='" + probNode.getVariable().getDecisionCriterion().getCriterionName() + "'>");
            sb.append("</" + nodename + ">");
        }
    }
	
	protected void getTreeADDBranch(StringBuilder sb, TreeADDBranch branch, Variable topVariable, ProbNet probNet) 
	{
        sb.append("<branch>");
        // Read the branch element

        if (topVariable.getVariableType() == VariableType.NUMERIC) 
        {
            sb.append("<thresholds>");
            // Export the left and right values of the interval. Closed/Open
            // attribute must be taken into account
            sb.append("<threshold value='" + String.valueOf(branch.getLowerBound().getLimit()) + "' belongs_to='" + (branch.getLowerBound().belongsToLeft() ? XMLValues.LEFT.toString() : XMLValues.RIGHT.toString()) + "'></threshold>");
            sb.append("<threshold value='" + String.valueOf(branch.getUpperBound().getLimit()) + "' belongs_to='" + (branch.getUpperBound().belongsToLeft() ? XMLValues.LEFT.toString() : XMLValues.RIGHT.toString()) + "'></threshold>");
            
            sb.append("</thresholds>");

            // Append the information to the element to be exported
        } 
        else if (topVariable.getVariableType() == VariableType.FINITE_STATES || topVariable.getVariableType() == VariableType.DISCRETIZED) 
        {
            // TODO: test cardinality of variable states and InnerNode
            List<State> branchStates = branch.getBranchStates();

            sb.append("<states>");
            for (State state : branchStates) {
                String varStateName = state.getName();
                sb.append("<state name='" + varStateName + "'></state>");
            }
            sb.append("</states>");
        }

        // label
        if(branch.getLabel() != null)
        {
        	sb.append("<label>" + branch.getLabel() + "</label>");
        }
        
        if(branch.getReference() != null)
        {
        	sb.append("<reference>" + branch.getReference() + "</reference>");
        }
        else
        {
            Potential potential = branch.getPotential();
            getPotential(sb, probNet, potential);
        }
        
        sb.append("</branch>");
    }
	
	protected void getStates(StringBuilder sb, Node probNode) {
        // TODO revisar el caso para variables numéricas
        for (State singleState : probNode.getVariable().getStates()) 
        {
        	sb.append("<state name='" + singleState.getName() + "'></state>");
        }
    }
	
	protected void getThresholds(StringBuilder sb, PartitionedInterval partitionedInterval) {
        if (partitionedInterval.getLimits().length > 0) {
            int i = 0;
            for (double limit : partitionedInterval.getLimits()) {
                sb.append("<threshold value='" + String.valueOf(limit) + "' belongs_to='" + partitionedInterval.getBelongsTo(i) + "'>");
                sb.append("</threshold>");
                i++;
            }
        }
    }
	
	private void appendPotential(StringBuilder sb, Potential potential)
	{
		String potentialType = potential.getClass().getAnnotation(PotentialType.class).name();
		
		if (potential instanceof ICIPotential)
		{
			potentialType = "ICIModel";
		}
		
		String prole = potential.getPotentialRole().toString();
		
		sb.append("<potential type='" + potentialType + "'");
		sb.append(" role='" + prole + "'");
		sb.append(">");
		
		String comment = potential.getComment();
		
		if (comment != null && comment.length() > 0)
		{
			sb.append("<comment><![CDATA[" + comment + "]]></comment>");
		}
		
		if (potential.getPotentialRole() == PotentialRole.UTILITY)
		{
			Variable utilityVariable = potential.getUtilityVariable();
            // it could be null in Branches potentials
            if (utilityVariable != null) {
                sb.append("<utility_variable name='" + utilityVariable.getName() + "'>");
                sb.append("</utility_variable>");
            }
		}
		
		List<Variable> potentialVariables = potential.getVariables();
        if (!potentialVariables.isEmpty()) {
            writePotentialVariables(sb, potentialVariables);
        }
		
        if (potential instanceof TablePotential) {
		    sb.append("<values>");
		    TablePotential tb = (TablePotential) potential;
		    sb.append(getString(tb.values));
		    sb.append("</values>");
		    // Write table values to the XML file
		    
		    if (tb.getUncertaintyTable() != null) 
		    {
		        getUncertainValuesElement(sb, tb);
		    }
        }
        else if (potential instanceof TreeADDPotential) 
        {
		    TreeADDPotential treePotential = (TreeADDPotential) potential;
		    // Get the root node of the TreeADD
		    Variable topVariable = treePotential.getRootVariable();
		    // union of intervals must cover the whole range of the
		    // numeric variable
		    // Write the variable of the root node as the top variable of
		    // the potential
		    sb.append("<top_variable");
		    writeVariableName(sb, topVariable);
		    sb.append(">");
		    // Branches of the topVariable
		    sb.append("<branches>");
		    for (TreeADDBranch branch : treePotential.getBranches()) {
		    	// Recursive writing of every branch in the treeADD structure
		    	getTreeADDBranch(sb, branch, topVariable, probNet);
		    }
		    sb.append("<branches>");
		    // Write var names of the table potential to the XML file
		    sb.append("</top_variable>");
        }
        else if (potential instanceof ICIPotential) {
		    ICIPotential iciPotential = (ICIPotential) potential;
		    // Model Element
		    sb.append("<model>");
			sb.append(iciPotential.getClass().getAnnotation(PotentialType.class).name());
			sb.append("</model>");
			// Subpotentials element
			sb.append("<subpotentials>");
			Variable conditionedVariable = iciPotential.getVariables().get(0);
			// Noisy parameters
			for (int i = 1; i < iciPotential.getNumVariables(); ++i) {
				Variable parentVariable = iciPotential.getVariables().get(i);
				sb.append("<potentail type='Table'>");
		    
				sb.append("<variables>");
				sb.append("<variable name='" + conditionedVariable.getName() + "'></variable>");
				sb.append("<variable name='" + parentVariable.getName() + "'></variable>");
				
				
				sb.append("</variables>");
				sb.append("<values>");
				sb.append(getString(iciPotential.getNoisyParameters(parentVariable)));
			    sb.append("</values>");
			    sb.append("</potential>");
			}
			sb.append("</subpotentials>");
			// Leaky parameters
			sb.append("<potential type='Table'>");
		
			sb.append("<variables>");
			sb.append("<variable name='" + conditionedVariable.getName() + "'></variable>");
			sb.append("</variables>");
		    sb.append("<values>");
		    sb.append(getString(iciPotential.getLeakyParameters()));
		    sb.append("</values>");
		    sb.append("</potential>");
        }
        else if (potential instanceof WeibullHazardPotential) {
		    WeibullHazardPotential weibullPotential = (WeibullHazardPotential) potential;
		    Variable timeVariable = weibullPotential.getTimeVariable();
		    if(timeVariable != null)
		    {
		    	sb.append("<time_variable name='" +  timeVariable.getBaseName() + "' timeslice='" + timeVariable.getTimeSlice() + "'>");
		        sb.append("</time_variable>");
		    }
		    getRegressionPotential(sb, weibullPotential);
        }
        else if (potential instanceof ExponentialHazardPotential) {
		    ExponentialHazardPotential exponentialHazardPotential = (ExponentialHazardPotential) potential;
		    getRegressionPotential(sb, exponentialHazardPotential);
        }
        else if (potential instanceof LinearCombinationPotential) {
        	LinearCombinationPotential linearRegressionPotential = (LinearCombinationPotential) potential;
		    getRegressionPotential(sb, linearRegressionPotential);
        } else if (potential instanceof ExponentialPotential) {
		    ExponentialPotential exponentialPotential = (ExponentialPotential) potential;
		    getRegressionPotential(sb, exponentialPotential);
        }
		else if (potential instanceof DeltaPotential) {
		    DeltaPotential deltaPotential = (DeltaPotential) potential;
		    if(deltaPotential.getConditionedVariable().getVariableType() == VariableType.NUMERIC)
		    {
		        sb.append("<numeric_value>");
		        sb.append( String.valueOf(deltaPotential.getNumericValue()));
		        sb.append("</numeric_value>");
		    }else
		    {
		        sb.append("<state>");
		        sb.append(deltaPotential.getState().getName());
		        sb.append("</state>");
		    }
		}
		else if (potential instanceof ConditionalGaussianPotential)
		{
			ConditionalGaussianPotential gaussianPotential = (ConditionalGaussianPotential)potential;
			sb.append("<subpotential>");
        	getPotential(sb, probNet, gaussianPotential.getMean());
        	getPotential(sb, probNet, gaussianPotential.getVariance());
        	sb.append("</subpotential>");
		}
		
		sb.append("</potential>");
	}
	
	protected void getPotential(StringBuilder sb, ProbNet probNet, Potential potential) 
	{
		String potentialType = potential.getClass().getAnnotation(PotentialType.class).name();
        sb.append("<potential type='" + potentialType + "'");
        
        sb.append(">");
        // TODO add function attribute
        // add comment child
        if(potential.getComment() != null && !potential.getComment().isEmpty())
        {
        	sb.append("<comment>" + potential.getComment() + "</comment>");
        }
        
        // TODO add aditionalProperties child
        if (potential.getPotentialRole() == PotentialRole.UTILITY) {
            Variable utilityVariable = potential.getUtilityVariable();
            // it could be null in Branches potentials
            if (utilityVariable != null) {
            	sb.append("<utiltity_variable");
                writeVariableName(sb, utilityVariable);
                sb.append("></utility_variable>");
            }
        }
        
        List<Variable> potentialVariables = potential.getVariables();
        if (!potentialVariables.isEmpty()) {
            writePotentialVariables(sb, potentialVariables);
        }
        

        if (potential instanceof TablePotential) {
            sb.append("<values>");
            sb.append(getString(((TablePotential) potential).values));
            sb.append("</values>");
            // Write table values to the XML file
            if (((TablePotential) potential).getUncertaintyTable() != null) 
            {
                getUncertainValuesElement(sb, potential);
            }
        } 
        else if (potential instanceof TreeADDPotential) 
        {
            TreeADDPotential treePotential = (TreeADDPotential) potential;
            // Get the root node of the TreeADD
            Variable topVariable = treePotential.getRootVariable();
            // union of intervals must cover the whole range of the
            // numeric variable
            // Write the variable of the root node as the top variable of
            // the potential
            sb.append("<top_variable");
            writeVariableName(sb, topVariable);
            sb.append("></top_variable>");
            // Branches of the topVariable
            sb.append("<branches>");
            for (TreeADDBranch branch : treePotential.getBranches()) {
                // Recursive writing of every branch in the treeADD structure
                getTreeADDBranch(sb, branch, topVariable, probNet);
            }
            // Write var names of the table potential to the XML file
            sb.append("</branches>");
        }
        else if (potential instanceof ICIPotential) {
            ICIPotential iciPotential = (ICIPotential) potential;
            // Model Element
            sb.append("<model>");
            sb.append(iciPotential.getClass().getAnnotation(RelationPotentialType.class).name());
            sb.append("</model>");
            // Variables element
            sb.append("<variables>");
            for (Variable variable : iciPotential.getVariables()) {
                sb.append("<variable name='" + variable.getName() + "'></variable>");
            }
            sb.append("</variables>");
            
            sb.append("<subpotentials>");
            
            Variable conditionedVariable = iciPotential.getVariables().get(0);
            // Noisy parameters
            for (int i = 1; i < iciPotential.getNumVariables(); ++i) {
                Variable parentVariable = iciPotential.getVariables().get(i);
                sb.append("<potential");
                sb.append(" type='Table'>");
                sb.append("<variables>");
                sb.append("<variable name='" + conditionedVariable.getName() + "'></variable>");
                sb.append("<variable name='" + parentVariable.getName() + "'></variable>");
                sb.append("</variables>");
                sb.append("<values>");
                sb.append(getString(iciPotential.getNoisyParameters(parentVariable)));
                sb.append("</values>");
                sb.append("</potential>");
            }
            
            // Leaky parameters
            sb.append("<potential type='Table'>");
            sb.append("<variables>");
            sb.append("<variable name='" + conditionedVariable.getName() + "'></variable>");
            sb.append("<variables>");
            sb.append("<values>");
            sb.append(getString(iciPotential.getLeakyParameters()));
            sb.append("</values>");
            sb.append("</potential>");
            sb.append("</subpotentials>");
        } 
        else if (potential instanceof WeibullHazardPotential) 
        {
            WeibullHazardPotential weibullPotential = (WeibullHazardPotential) potential;
            Variable timeVariable = weibullPotential.getTimeVariable();
            if(timeVariable != null)
            {
            	sb.append("<time_variable");
                sb.append(" name='" + timeVariable.getBaseName() + "'");
                sb.append(" timeslice='"  + timeVariable.getTimeSlice() + "'");
                sb.append("></time_variable>");
            }
            getRegressionPotential(sb, weibullPotential);
        }
        else if (potential instanceof ExponentialHazardPotential)
        {
            ExponentialHazardPotential exponentialHazardPotential = (ExponentialHazardPotential) potential;
            getRegressionPotential(sb, exponentialHazardPotential);
        }
        else if (potential instanceof LinearCombinationPotential) {
        	LinearCombinationPotential linearRegressionPotential = (LinearCombinationPotential) potential;
            getRegressionPotential(sb, linearRegressionPotential);
        }
        else if (potential instanceof ExponentialPotential) {
            ExponentialPotential exponentialPotential = (ExponentialPotential) potential;
            getRegressionPotential(sb, exponentialPotential);
        }
        else if (potential instanceof DeltaPotential) {
            DeltaPotential deltaPotential = (DeltaPotential)potential;
            if(deltaPotential.getConditionedVariable().getVariableType() == VariableType.NUMERIC)
            {
            	sb.append("<numeric_value>");
                sb.append( String.valueOf(deltaPotential.getNumericValue()));
                sb.append("</numeric_value>");
            }else
            {
                sb.append("<state>");
                sb.append(deltaPotential.getState().getName());
                sb.append("</state>");
            }
        }
        else if (potential instanceof ConditionalGaussianPotential)
        {
        	ConditionalGaussianPotential gaussianPotential = (ConditionalGaussianPotential)potential;
        	sb.append("<subpotential>");
        	getPotential(sb, probNet, gaussianPotential.getMean());
        	getPotential(sb, probNet, gaussianPotential.getVariance());
        	sb.append("</subpotential>");
        }
    }
	
	private void appendLinkRestriction(StringBuilder sb, Link<Node> link)
	{
		double[] table = ((TablePotential) link.getRestrictionsPotential()).values;

        boolean hasRestriction = false;
        for (int i = 0; i < table.length; i++) {
            if (table[i] == 0.0) {
                hasRestriction = true;
            }
        }
        if (hasRestriction) 
        {
            Potential potential = link.getRestrictionsPotential();
            this.appendPotential(sb, potential);
        }
	}
	
	private void appendRevealingConditions(StringBuilder sb, Link<Node> link)
	{
		Node node = (Node) link.getNode1();
        VariableType varType = node.getVariable().getVariableType();
        
        sb.append("<revealing_condition>");
        if (varType == VariableType.NUMERIC) {
            List<PartitionedInterval> intervals = link.getRevealingIntervals();
            sb.append("<threasholds>");
            for (PartitionedInterval partitionedInterval : intervals) {
                if (partitionedInterval.getLimits().length > 0) {
                    int i = 0;
                    for (double limit : partitionedInterval.getLimits()) {
                    	sb.append("<threashold value='" + String.valueOf(limit) + "' belongs_to='" + partitionedInterval.getBelongsTo(i) + "'>");
                    	sb.append("<threashold>");
                        i++;
                    }
                }
            }
            sb.append("</threasholds>");
        } else {

            List<State> states = link.getRevealingStates();
            sb.append("<states>");
            for (State state : states) {
                sb.append("<state name='" + state.getName() + "'></state>");
            }
            sb.append("</states>");
        }
        sb.append("</revealing_condition>");
	}
	
	private void getRegressionPotential(StringBuilder sb, GLMPotential potential)
    {
        getCoefficientsElement(sb, potential.getCoefficients());
        getCovariatesElement(sb, potential.getCovariates());
        
        if(potential.getCovarianceMatrix() != null)
        {
            getCovarianceMatrixElement(sb, potential.getCovarianceMatrix());
        }
        else if(potential.getCholeskyDecomposition() != null)
        {
            getCholeskyDecompositionElement(sb, potential.getCholeskyDecomposition());
        }
    }
	
	private void getCoefficientsElement(StringBuilder sb, double[] coefficients) {
        sb.append("<coefficients>");
        sb.append(getString(coefficients));
        sb.append("</coefficients>");
    }
    
    private void getCovariatesElement(StringBuilder sb, String[] covariates) {
        sb.append("<covariates>");
        for (String covariate : covariates) {
            sb.append("<covariate>" + covariate + "</covariates>");
        }
        sb.append("</covariates>");
    }

    private void getCovarianceMatrixElement(StringBuilder sb, double[] covarianceMatrix) {
    	sb.append("<covariance_matrix>");
        sb.append(getString(covarianceMatrix));
        sb.append("</covariance_matrix>");
    }

    private void getCholeskyDecompositionElement(StringBuilder sb, double[] choleskyDecomposition) {
        sb.append("<cholesky_decomposition>");
        sb.append(getString(choleskyDecomposition));
        sb.append("</cholesky_decomposition>");
    }
	
	protected void writePotentialVariables(StringBuilder sb, List<Variable> potentialVariables) {
        if (potentialVariables.size() > 0) {
            sb.append("<variables>");
            for (Variable variable : potentialVariables) {
                sb.append("<variable");
                writeVariableName(sb, variable);
                sb.append("></variable>");
            }
            // Write var names of the table potential to the XML file
            sb.append("</variables>");
        }
    }
	
	private void writeVariableName(StringBuilder sb, Variable variable) {
        sb.append(" name='" + variable.getBaseName() + "'");
		
        if (variable.getTimeSlice() >= 0) {
            sb.append(" timeslice='" + String.valueOf(variable.getTimeSlice()) + "'");
        }
    }
	
	private void getUncertainValuesElement(StringBuilder sb, Potential potential) {
		sb.append("<uncertain_values>");
        
        UncertainValue[] table = ((TablePotential) potential).getUncertaintyTable();
        int size = table.length;
        for (int i = 0; i < size; i++) {
            UncertainValue auxValue = table[i];
            getUncertainValueElement(sb, auxValue);
        }
        sb.append("</uncertain_values>");
    }
	
	private void getUncertainValueElement(StringBuilder sb, UncertainValue uncertainValue) {
        sb.append("<values");
        String nameParam = null;
        String functionName = null;
        String paramValue = null;
        if (uncertainValue != null) {
            ProbDensFunction function = uncertainValue.getProbDensFunction();
            functionName = function.getClass().getAnnotation(ProbDensFunctionType.class).name();
            nameParam = uncertainValue.getName();
            paramValue = getString(uncertainValue.getProbDensFunction().getParameters());
        }
        
        if (functionName != null)
        {
        	sb.append(" distribution='" + functionName + "'");
        }
        
        if (nameParam != null)
        {
        	sb.append(" name='" + nameParam + "'");
        }
        
        sb.append(">");
        
        if (paramValue != null)
        {
        	sb.append(paramValue);
        }
        
        sb.append("</values>");
    }
	
	protected String getString(double[] table) {
        StringBuffer stringBuffer = new StringBuffer();
        for (double value : table) {
            stringBuffer.append(String.valueOf(value) + " ");
        }
        return stringBuffer.toString();
    }
}
