package com.boguenon.service.modules.bayes.costeffectiveness;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

import com.boguenon.service.modules.bayes.common.CPTablePanel;
import com.boguenon.service.modules.bayes.common.JTable;

public class CostEffectivenessAnalysisPane {
	private TablePotential globalUtility = null;
	
	public CostEffectivenessAnalysisPane(TablePotential globalUtility) {
		this.globalUtility = globalUtility;
	}
	
	public String getResults()
	{
		// create a dummy ProbNet
        ProbNet dummyProbNet = new ProbNet ();
        // make sure first variable in globalUtility is decisionCriteria one
        List<Variable> correctOrder = new ArrayList<> (globalUtility.getVariables ());
        for (int i = 0; i < correctOrder.size (); i++)
        {
            if (correctOrder.get (i).getName ().equalsIgnoreCase ("Decision Criteria"))
            {
                Variable decisionCriteriaVariable = correctOrder.remove (i);
                correctOrder.add (0, decisionCriteriaVariable);
            }
        }
        globalUtility = DiscretePotentialOperations.reorder (globalUtility, correctOrder);
        Node dummyNode = new Node (dummyProbNet, globalUtility.getVariables ().get (0),
                                       NodeType.CHANCE);
        for (int i = 1; i < globalUtility.getVariables ().size (); i++)
        {
            Node newProbNode = new Node (dummyProbNet, globalUtility.getVariables ().get (i), NodeType.CHANCE);
            dummyProbNet.addLink (newProbNode, dummyNode, true);
        }
        List<Potential> potentials = new ArrayList<> ();
        TablePotential potentialCopy = new TablePotential (globalUtility.getVariables (),
                                                 PotentialRole.CONDITIONAL_PROBABILITY);
        potentialCopy.setValues (globalUtility.getValues ());
        potentials.add (potentialCopy);
        dummyNode.setPotentials (potentials);
        
        CPTablePanel cpTablePanel = new CPTablePanel(dummyNode);
        JTable table = cpTablePanel.getValuesTable();

        return table.getTableData();
	}
}
