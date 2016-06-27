package com.boguenon.service.modules.bayes.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.help.UnsupportedOperationException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceManager;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.oopn.Instance.ParameterArity;
import org.openmarkov.inference.tasks.VariableElimination.VEPropagation;

import com.boguenon.service.modules.bayes.CPGXML;
import com.boguenon.service.modules.bayes.mode.EditionMode;
import com.boguenon.service.modules.bayes.mode.EditionModeManager;

public class EditorPanel {

	protected ProbNet                        probNet;
    /**
     * Static field for serializable class.
     */
    private static final long                serialVersionUID                 = 2789011585460326400L;
    /**
     * Constant that indicates the value of the Expansion Threshold by default.
     */
    // This should be in a future a configuration option that should be read on
    // start
    private static final int                 DEFAULT_THRESHOLD_VALUE          = 5;
    /**
     * Current edition mode.
     */
    private EditionMode                      editionMode                      = null;
    /**
     * This variable indicates which is the expansion threshold of the network
     */
    private double                           currentExpansionThreshold        = DEFAULT_THRESHOLD_VALUE;
    /**
     * Network panel associated to this editor panel
     */
    private CPGXML                     networkPanel                     = null;
    /**
     * Pre resolution evidence
     */
    private EvidenceCase                     preResolutionEvidence;
    /**
     * Array of Evidence cases treated for this editor panel
     */
    private List<EvidenceCase>               postResolutionEvidence;
    /**
     * Each position of this array indicates if the corresponding evidence case
     * is currently compiled (if true) or not (if false)
     */
    private List<Boolean>                    evidenceCasesCompilationState;
    /**
     * Minimum value of the range of each utility node.
     */
    private HashMap<Variable, Double>        minUtilityRange;
    /**
     * Maximum value of the range of each utility node.
     */
    private HashMap<Variable, Double>        maxUtilityRange;
    /**
     * This variable indicates which is the evidence case that is currently
     * being treated
     */
    private int                              currentCase;
    /**
     * Inference manager
     */
    private InferenceManager                 inferenceManager                 = null;
    /**
     * Inference algorithm used to evaluate this network
     */
    private InferenceAlgorithm               inferenceAlgorithm               = null;
    /**
     * This variable indicates if the propagation mode is automatic or manual.
     */
    private boolean                          automaticPropagation;
    /**
     * This variable indicates if propagation should be done right now (if being
     * in Inference Mode).
     */
    private boolean                          propagationActive;
    /**
     * This variable indicates if it has been a change in the properties or in
     * the potential values in some node.
     */
    private boolean                          networkChanged                   = true;
    /**
     * Visual representation of the network
     */
    public VisualNetwork                  visualNetwork                    = null;

    private boolean                          approximateInferenceWarningGiven = false;
    private boolean                          canBeExpanded                    = false;
    
    private EditionModeManager               editionModeManager;

    /**
     * Constructor that creates the instance.
     * @param networkPanel network that will be edited.
     */
    public EditorPanel (CPGXML networkPanel, VisualNetwork visualNetwork)
    {
        this.networkPanel = networkPanel;
        this.probNet = networkPanel.getProbNet ();
        this.visualNetwork = visualNetwork;
        automaticPropagation = true;
        propagationActive = true;
        preResolutionEvidence = new EvidenceCase ();
        postResolutionEvidence = new ArrayList<EvidenceCase> (1);
        currentCase = 0;
        EvidenceCase evidenceCase = new EvidenceCase ();
        postResolutionEvidence.add (currentCase, evidenceCase);
        evidenceCasesCompilationState = new ArrayList<Boolean> (1);
        evidenceCasesCompilationState.add (currentCase, false);
        minUtilityRange = new HashMap<Variable, Double> ();
        maxUtilityRange = new HashMap<Variable, Double> ();
        initialize ();
        inferenceManager = new InferenceManager ();
        editionModeManager = new EditionModeManager (this, probNet);
        editionMode = editionModeManager.getDefaultEditionMode ();
    }

    /**
     * This method initializes this instance.
     */
    private void initialize ()
    {
    }

    /**
     * Changes the presentation mode of the text of the nodes.
     * @param value new value of the presentation mode of the text of the nodes.
     */
    public void setByTitle (boolean value)
    {
        visualNetwork.setByTitle (value);
    }

    /**
     * Returns the presentation mode of the text of the nodes.
     * @return true if the title of the nodes is the name or false if it is the
     *         name.
     */
    public boolean getByTitle ()
    {
        return visualNetwork.getByTitle ();
    }

    /**
     * Overwrite 'paint' method to avoid to call it explicitly.
     * @param g the graphics context in which to paint.
     */
    public void paint()
    {
        visualNetwork.paint();
    }

    /**
     * Returns the edition mode.
     * @return edition mode.
     */
    public EditionMode getEditionMode ()
    {
        return editionMode;
    }

    /**
     * Changes the state of the edition and carries out the necessary actions in
     * each case.
     * @param newState new edition state.
     */
    public void setEditionMode (String newEditionModeName)
    {
        EditionMode newEditionMode = editionModeManager.getEditionMode (newEditionModeName);
        if (!editionMode.equals (newEditionMode))
        {
            editionMode = newEditionMode;
        }
    }

    /**
     * Returns the number of selected nodes.
     * @return number of selected nodes.
     */
    public int getSelectedNodesNumber ()
    {
        return visualNetwork.getSelectedNodesNumber ();
    }

    /**
     * Returns the number of selected links.
     * @return number of selected links.
     */
    public int getSelectedLinksNumber ()
    {
        return visualNetwork.getSelectedLinksNumber ();
    }

    /**
     * Returns a list containing the selected nodes.
     * @return a list containing the selected nodes.
     */
    public List<VisualNode> getSelectedNodes ()
    {
        return visualNetwork.getSelectedNodes ();
    }

    /**
     * Returns a list containing the selected links.
     * @return a list containing the selected links.
     */
    public List<VisualLink> getSelectedLinks ()
    {
        return visualNetwork.getSelectedLinks ();
    }


    // private boolean requestCostEffectiveness(Window owner,
    // String suffixTypeAnalysis, boolean isProbabilistic) {
    // costEffectivenessDialog = new CostEffectivenessDialog(owner);
    // costEffectivenessDialog.showSimulationsNumberElements(isProbabilistic);
    // return (costEffectivenessDialog.requestData(probNet.getName(),
    // suffixTypeAnalysis) == CostEffectivenessDialog.OK_BUTTON);
    // }
    /**
     * This method shows a dialog box with the additionalProperties of a link.
     * If some property has changed, insert a new undo point into the network
     * undo manager.
     * @param link
     */
    public void changeLinkProperties (VisualLink link)
    {
        /*
         * This method must be implemented to activate the possibility of
         * editing the additionalProperties of a link in future versions.
         */
    }

    /**
     * This method imposes a policy in a decision node.
     */
    public void imposePolicyInNode ()
    {
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            if (node.getNode ().getNodeType () == NodeType.DECISION)
            {
                Node probNode = node.getNode ();
                // TODO manage other kind of policy types from the interface
                probNode.setPolicyType (PolicyType.OPTIMAL);
                List<Variable> variables = new ArrayList<Variable> ();
                // it is added first conditioned variable
                variables.add (node.getNode ().getVariable ());
                List<Node> nodes = probNode.getProbNet ().getNodes();
                for (Node possibleParent : nodes)
                {
                    if (probNode.isParent(possibleParent))
                    {
                        variables.add (possibleParent.getVariable ());
                    }
                }
                UniformPotential policy = new UniformPotential (
                                                                variables,
                                                                PotentialRole.POLICY);
                List<Potential> policies = new ArrayList<Potential> ();
                policies.add (policy);
                probNode.setPotentials (policies);
//                imposePolicyDialog.setTitle ("ImposePolicydialog.Title.Label");
//                if (imposePolicyDialog.requestValues () == NodePropertiesDialog.OK_BUTTON)
//                {
//                    // change its color
//                    ((VisualDecisionNode) node).setHasPolicy (true);
//                    networkChanged = true;
//                }
//                else
//                { // if user cancels policy imposition then no potential is
//                  // restored to the probnode
//                    List<Potential> noPolicy = new ArrayList<Potential> ();
//                    probNode.setPotentials (noPolicy);
//                }
            }
        }
    }

    /**
     * This method edits an imposed policy of a decision node.
     */
    public void editNodePolicy ()
    {
        System.out.println ("Pulsada la opción 'Editar Política'"); // ...Borrar
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            if (node.getNode ().getNodeType () == NodeType.DECISION)
            {
                Node probNode = node.getNode ();
                // TODO manage other kind of policy types from the interface
                // probNode.setPolicyType(PolicyType.OPTIMAL);
                // Potential imposedPolicy = probNode.getPotentials ().get (0);
//                PotentialEditDialog imposePolicyDialog = new PotentialEditDialog (
//                                                                                  Utilities.getOwner (this),
//                                                                                  probNode, false);
//                if (imposePolicyDialog.requestValues () == NodePropertiesDialog.OK_BUTTON)
//                {
//                    // change it colour
//                    ((VisualDecisionNode) node).setHasPolicy (true);
//                    networkChanged = true;
//                }
            }
        }
    }

    /**
     * This method removes an imposed policy from a decision node.
     */
    public void removePolicyFromNode ()
    {
        System.out.println ("Pulsada la opción 'Eliminar Política'"); // ...Borrar
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            if (node.getNode ().getNodeType () == NodeType.DECISION)
            {
                Node probNode = node.getNode ();
                ArrayList<Potential> noPolicy = new ArrayList<> ();
                probNode.setPotentials (noPolicy);
                ((VisualDecisionNode) node).setHasPolicy (false);
            }
        }
        networkChanged = true;
    }

    /**
     * This method shows the expected utility of a decision node.
     */
    public void showExpectedUtilityOfNode ()
    {
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            Node probNode = node.getNode ();
            try
            {
                // Potential expectedUtility = null;// =
                // inferenceAlgorithm.getExpectedtedUtility(node.getProbNode().getVariable());
                Potential expectedUtility;
                expectedUtility = inferenceAlgorithm.getExpectedUtilities (probNode.getVariable ());
                ProbNode dummyNode = new ProbNode (new ProbNet (), probNode.getVariable (),
                                                   probNode.getNodeType ());
                dummyNode.setPotential (expectedUtility);
//                PotentialEditDialog expectedUtilityDialog = new PotentialEditDialog (
//                                                                                     Utilities.getOwner (this),
//                                                                                     dummyNode,
//                                                                                     false, true);
//                expectedUtilityDialog.setTitle ("ExpectedUtilityDialog.Title.Label");
//                expectedUtilityDialog.requestValues ();
            }
            catch (IncompatibleEvidenceException | UnexpectedInferenceException e)
            {
            	System.err.println("ExceptionGeneric : " + e.getMessage());
                e.printStackTrace ();
            }
        }
        networkChanged = false;
    }

    /**
     * This method shows the optimal policy for a decision node.
     */
    public void showOptimalPolicyOfNode ()
    {
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            ProbNet dummyProbNet = new ProbNet ();
            Node dummy = null;
            try
            {
                // Potential optimalPolicy =
                // inferenceAlgorithm.getOptimizedPolicies().get(node.getProbNode().getVariable());
                Potential optimalPolicy = inferenceAlgorithm.getOptimizedPolicy (node.getNode ().getVariable ());
                dummyProbNet.addPotential (optimalPolicy);
                Variable conditionedVariable = optimalPolicy.getVariable (0);
                dummy = dummyProbNet.getNode (conditionedVariable);
                dummy.setNodeType (NodeType.DECISION);
                dummy.setPolicyType (PolicyType.OPTIMAL);
                for (Variable variable : optimalPolicy.getVariables ())
                {
                    if (variable.equals (conditionedVariable))
                    {
                        continue;
                    }
                    try
                    {
                        dummyProbNet.addLink (variable, conditionedVariable, true);
                    }
                    catch (NodeNotFoundException e)
                    {
                        throw new RuntimeException ("Node not found: " + e.getMessage ());
                    }
                }
//                PotentialEditDialog optimalPolicyDialog = new PotentialEditDialog (
//                                                                                   Utilities.getOwner (this),
//                                                                                   dummy, false,
//                                                                                   true);
//                optimalPolicyDialog.setTitle ("OptimalPolicyDialog.Title.Label");
//                optimalPolicyDialog.requestValues ();
            }
            catch (IncompatibleEvidenceException | UnexpectedInferenceException e)
            {
            	System.err.println("ExceptionGeneric : " + e.getMessage());
                e.printStackTrace ();
            }
        }
        networkChanged = false;
    }


    /**
     * This method returns the current Evidence Case.
     * @return the current Evidence Case.
     */
    public EvidenceCase getCurrentEvidenceCase ()
    {
        return postResolutionEvidence.get (currentCase);
    }

    /**
     * This method returns the Evidence Case.
     * @param caseNumber the number of the case to be returned.
     * @return the selected Evidence Case.
     */
    public EvidenceCase getEvidenceCase (int caseNumber)
    {
        return postResolutionEvidence.get (caseNumber);
    }

    /**
     * This method returns list of evidence cases
     * @return the list of Evidence Cases.
     */
    public ArrayList<EvidenceCase> getEvidence ()
    {
        ArrayList<EvidenceCase> evidence = new ArrayList<EvidenceCase> ();
        for (EvidenceCase postResolutionEvidenceCase : postResolutionEvidence)
        {
            if (!postResolutionEvidenceCase.isEmpty ())
            {
                evidence.add (postResolutionEvidenceCase);
            }
        }
        if (!evidence.isEmpty () || !preResolutionEvidence.isEmpty ())
        {
            evidence.add (0, preResolutionEvidence);
        }
        return evidence;
    }

    /**
     * This method returns the number of the Evidence Case that is currently
     * selected
     * @return the number of the current Evidence Case.
     */
    public int getCurrentCase ()
    {
        return currentCase;
    }

    public EvidenceCase getPreResolutionEvidence ()
    {
        return preResolutionEvidence;
    }

    /**
     * This method sets which is the current evidence case.
     * @param currentCase new value for the current evidence case.
     */
    public void setCurrentCase (int currentCase)
    {
        this.currentCase = currentCase;
    }

    /**
     * This method returns the number of Evidence Cases that the ArrayList is
     * currently holding .
     * @return the number of Evidence Cases in the ArrayList.
     */
    public int getNumberOfCases ()
    {
        return postResolutionEvidence.size ();
    }

    /**
     * This method returns a boolean indicating if the case number passed as
     * parameter is currently compiled.
     * @param caseNumber number of the evidence case.
     * @return the compilation state of the case.
     */
    public boolean getEvidenceCasesCompilationState (int caseNumber)
    {
        return evidenceCasesCompilationState.get (caseNumber);
    }

    /**
     * This method sets which is the compilation state of the case.
     * @param caseNumber number of the evidence case to be set.
     * @param value true if compiled; false otherwise.
     */
    public void setEvidenceCasesCompilationState (int caseNumber, boolean value)
    {
        this.evidenceCasesCompilationState.set (caseNumber, value);
    }

    /**
     * This method sets the list of evidence cases
     * @param owner window that owns the dialog box.
     */
    public void setEvidence (EvidenceCase preResolutionEvidence,
                             List<EvidenceCase> postResolutionInference)
    {
        this.postResolutionEvidence = (postResolutionInference == null) ? new ArrayList<EvidenceCase> ()
                                                                       : postResolutionInference;
        this.preResolutionEvidence = (preResolutionEvidence == null) ? new EvidenceCase ()
                                                                    : preResolutionEvidence;
        if (postResolutionEvidence.isEmpty ())
        {
            this.postResolutionEvidence.add (new EvidenceCase ());
        }
        currentCase = this.postResolutionEvidence.size () - 1;
        // Update visual info on evidence
        for (VisualNode node : visualNetwork.getAllNodes ())
        {
            node.setPostResolutionFinding (false);
        }
        for (EvidenceCase evidenceCase : postResolutionEvidence)
        {
            for (Finding finding : evidenceCase.getFindings ())
            {
                for (VisualNode node : visualNetwork.getAllNodes ())
                {
                    if (node.getNode ().getVariable ().equals (finding.getVariable ()))
                    {
                        node.setPostResolutionFinding (true);
                    }
                }
            }
        }
        for (VisualNode node : visualNetwork.getAllNodes ())
        {
            node.setPreResolutionFinding (false);
        }
        for (Finding finding : preResolutionEvidence.getFindings ())
        {
            for (VisualNode node : visualNetwork.getAllNodes ())
            {
                if (node.getNode ().getVariable ().equals (finding.getVariable ()))
                {
                    node.setPreResolutionFinding (true);
                }
            }
        }
        // Update evidenceCasesCompilationState
        evidenceCasesCompilationState.clear ();
        for (int i = 0; i < postResolutionEvidence.size (); ++i)
        {
            evidenceCasesCompilationState.add (false);
        }
    }

    /**
     * This method returns true if propagation type currently set is automatic;
     * false if manual.
     * @return true if the current propagation type is automatic.
     */
    public boolean isAutomaticPropagation ()
    {
        return automaticPropagation;
    }

    /**
     * This method sets the current propagation type.
     * @param automaticPropagation new value of the propagation type.
     */
    public void setAutomaticPropagation (boolean automaticPropagation)
    {
        this.automaticPropagation = automaticPropagation;
    }

    /**
     * This method returns the propagation status: true if propagation should be
     * done right now; false otherwise.
     * @return true if propagation should be done right now.
     */
    public boolean isPropagationActive ()
    {
        return propagationActive;
    }

    /**
     * This method sets the propagation status.
     * @param propagationActive new value of the propagation status.
     */
    public void setPropagationActive (boolean propagationActive)
    {
        this.propagationActive = propagationActive;
        this.visualNetwork.setPropagationActive (propagationActive);
    }

    /**
     * This method returns the associated network panel.
     * @return the associated network panel.
     */
    public CPGXML getNetworkPanel ()
    {
        return networkPanel;
    }

    /**
     * This method changes the current expansion threshold.
     * @param expansionThreshold new value of the expansion threshold.
     */
    public void setExpansionThreshold (double expansionThreshold)
    {
        this.currentExpansionThreshold = expansionThreshold;
    }

    /**
     * This method returns the current expansion threshold.
     * @return the value of the current expansion threshold.
     */
    public double getExpansionThreshold ()
    {
        return currentExpansionThreshold;
    }

    /**
     * This method updates the expansion state (expanded/contracted) of the
     * nodes. It is used in transitions from edition to inference mode and vice
     * versa, and also when the user modifies the current expansion threshold in
     * the Inference tool bar
     * @param newWorkingMode new value of the working mode.
     */
    public void updateNodesExpansionState (int newWorkingMode)
    {
        if (newWorkingMode == CPGXML.EDITION_WORKING_MODE)
        {
            VisualNode visualNode = null;
            List<VisualNode> allNodes = visualNetwork.getAllNodes ();
            if (allNodes.size () > 0)
            {
                for (int i = 0; i < allNodes.size (); i++)
                {
                    visualNode = allNodes.get (i);
                    if (visualNode.isExpanded ())
                    {
                        visualNode.setExpanded (false);
                    }
                }
            }
        }
        else if (newWorkingMode == CPGXML.INFERENCE_WORKING_MODE)
        {
            VisualNode visualNode = null;
            List<VisualNode> allNodes = visualNetwork.getAllNodes ();
            if (allNodes.size () > 0)
            {
                for (int i = 0; i < allNodes.size (); i++)
                {
                    visualNode = allNodes.get (i);
                    if (visualNode.getNode ().getRelevance () >= getExpansionThreshold ())
                    {
                        visualNode.setExpanded (true);
                    }
                    else
                    {
                        visualNode.setExpanded (false);
                    }
                }
            }
        }
    }

    /**
     * This method updates the value of each state for each node in the network
     * with the current individual probabilities.
     */
    public void updateIndividualProbabilities ()
    {
        // if some visualNode has a number of values different from the
        // number of evidence cases in memory, we need to recreate its
        // visual states and consider that the network has been changed.
        for (VisualNode visualNode : visualNetwork.getAllNodes ())
        {
            InnerBox innerBox = visualNode.getInnerBox ();
            VisualState visualState = null;
            if (innerBox instanceof FSVariableBox)
            {
                visualState = ((FSVariableBox) innerBox).getVisualState (0);
                updateVisualStateAndEvidence(innerBox, visualState);
            }
            else if (innerBox instanceof NumericVariableBox)
            {
                visualState = ((NumericVariableBox) innerBox).getVisualState ();
                updateVisualStateAndEvidence(innerBox, visualState);
            }
        }
        
        if ((propagationActive)
            && (networkPanel.getWorkingMode () == CPGXML.INFERENCE_WORKING_MODE))
        {
            // if the network has been changed, propagation must be done in
            // each evidence case in memory. Otherwise, only propagation in
            // current case is needed.
            if (networkChanged)
            {
                for (int i = 0; i < postResolutionEvidence.size (); i++)
                {
                    doPropagation (getEvidenceCase (i), i);
                }
                updateNodesFindingState (postResolutionEvidence.get (currentCase));
                networkChanged = false;
            }
            else
            {
                if (evidenceCasesCompilationState.get (currentCase) == false)
                {
                    if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
                }
            }
        }
        else if (evidenceCasesCompilationState.get (currentCase) == false)
        {
            // Even if propagation mode is manual, a propagation should be
            // done the first time that inference mode is selected
            doPropagation (postResolutionEvidence.get (currentCase), currentCase);
        }
        updateAllVisualStates ("", currentCase);
    }
    
    private void updateVisualStateAndEvidence(InnerBox innerBox, VisualState visualState)
    {
    	if (visualState.getNumberOfValues () != postResolutionEvidence.size ())
		{
    		innerBox.update(postResolutionEvidence.size());
		    
		    networkChanged = true;
		    
		    for (int i = 0; i < postResolutionEvidence.size (); i++)
		    {
		        evidenceCasesCompilationState.set (i, false);
		    }
		}
    }

    /**
     * This method removes all the findings established in the current evidence
     * case.
     */
    public void removeAllFindings ()
    {
        setPropagationActive (isAutomaticPropagation ());
        List<VisualNode> visualNodes = visualNetwork.getAllNodes ();
        for (int i = 0; i < visualNodes.size (); i++)
        {
            visualNodes.get (i).setPostResolutionFinding (false);
        }
        List<Finding> findings = postResolutionEvidence.get (currentCase).getFindings ();
        for (int i = 0; i < findings.size (); i++)
        {
            try
            {
                postResolutionEvidence.get (currentCase).removeFinding (findings.get (i).getVariable ());
            }
            catch (NoFindingException exc)
            {
            	System.err.println("ExceptionNoFinding : " + exc.getMessage());
            }
        }
        if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) 
        	setPropagationActive (false);
        // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        // networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsFindingsDependent (networkPanel);
    }

    /**
     * This method removes the findings that a node could have in all the
     * evidence cases in memory. It is invoked when a change takes place in
     * properties or probabilities of a the node
     * @param node the node in which to remove the findings.
     */
    public void removeNodeEvidenceInAllCases (Node node)
    {
        for (int i = 0; i < postResolutionEvidence.size (); i++)
        {
            List<Finding> findings = postResolutionEvidence.get (i).getFindings ();
            for (int j = 0; j < findings.size (); j++)
            {
                try
                {
                    if (node.getVariable () == (findings.get (j).getVariable ()))
                    {
                        postResolutionEvidence.get (i).removeFinding (findings.get (j).getVariable ());
                        if (isAutomaticPropagation () && (inferenceAlgorithm != null))
                        {
                            if (!doPropagation (postResolutionEvidence.get (i), i)) setPropagationActive (false);
                        }
                        if (i == currentCase)
                        {
                            List<VisualNode> visualNodes = visualNetwork.getAllNodes ();
                            for (int k = 0; k < visualNodes.size (); k++)
                            {
                                if (visualNodes.get (k).getNode () == node)
                                {
                                    visualNodes.get (k).setPostResolutionFinding (false);// ...asaez....PENDIENTE........
                                }
                            }
                        }
                    }
                }
                catch (NoFindingException exc)
                {
                	System.err.println("ExceptionNoFinding : " + exc.getMessage());
                }
            }
        }
        // networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsFindingsDependent (networkPanel);
    }

    /**
     * This method returns true if there are any finding in the current evidence
     * case.
     * @return true if the current evidence case has at least one finding.
     */
    public boolean areThereFindingsInCase ()
    {
        boolean areFindings = false;
        List<Finding> findings = postResolutionEvidence.get (currentCase).getFindings ();
        if (findings != null)
        {
            if (findings.size () > 0)
            {
                areFindings = true;
            }
        }
        return areFindings;
    }

    /**
     * This method returns the number of the Evidence Case that is currently
     * selected
     * @param visualState the visual state in which the finding is going to be
     *            set.
     */
    public void setNewFinding (VisualNode visualNode, VisualState visualState)
    {
        boolean isInferenceMode = networkPanel.getWorkingMode () == CPGXML.INFERENCE_WORKING_MODE;
        EvidenceCase evidenceCase = (isInferenceMode) ? postResolutionEvidence.get (currentCase)
                                                     : preResolutionEvidence;
        setPropagationActive (isAutomaticPropagation ());
        Variable variable = visualNode.getNode ().getVariable ();
        boolean nodeAlreadyHasFinding = evidenceCase.getFinding (variable) != null;
        int oldState = -1;
        if (nodeAlreadyHasFinding)
        {
            // There is already a finding in the node
            oldState = evidenceCase.getState (variable);
            if (oldState == visualState.getStateNumber ())
            {
                // The finding is in the same state, therefore, remove evidence
                try
                {
                    evidenceCase.removeFinding (variable);
                    if (isInferenceMode)
                    {
                        visualNode.setPostResolutionFinding (false);
                    }
                }
                catch (NoFindingException exc)
                {
                	System.err.println("ExceptionNoFinding : " + exc.getMessage());
                }
            }
            else
            {
                // There is a finding in another state. Remove old, add new
                try
                {
                    evidenceCase.removeFinding (variable);
                    Finding finding = new Finding (variable, visualState.getStateNumber ());
                    evidenceCase.addFinding (finding);
                    if (isInferenceMode)
                    {
                        visualNode.setPostResolutionFinding (true);
                    }
                    else
                    {
                        visualNode.setPreResolutionFinding (true);
                    }
                }
                catch (NoFindingException exc)
                {
                	System.err.println("ExceptionNoFinding : " + exc.getMessage());
                }
                catch (InvalidStateException exc)
                {
                	System.err.println("ExceptionInvalidState : " + exc.getMessage());
                }
                catch (IncompatibleEvidenceException exc)
                {
                	System.err.println("ExceptionIncompatibleEvidence : " + exc.getMessage());
                }
                catch (Exception exc)
                {
                	System.err.println("ExceptionGeneric : " + exc.getMessage());
                }
            }
        }
        else
        { // No finding previously in node, add
            Finding finding = new Finding (variable, visualState.getStateNumber ());
            try
            {
                evidenceCase.addFinding (finding);
                if (isInferenceMode)
                {
                    visualNode.setPostResolutionFinding (true);
                }
                else
                {
                    visualNode.setPreResolutionFinding (true);
                }
            }
            catch (InvalidStateException exc)
            {
            	System.err.println("ExceptionInvalidState : " + exc.getMessage());
            }
            catch (IncompatibleEvidenceException exc)
            {
            	System.err.println("ExceptionIncompatibleEvidence : " + exc.getMessage());
            }
            catch (Exception exc)
            {
            	System.err.println("ExceptionGeneric : " + exc.getMessage());
            }
        }
        if (isInferenceMode)
        {
            evidenceCasesCompilationState.set (currentCase, false);
        }
        else
        {
            for (int i = 0; i < evidenceCasesCompilationState.size (); ++i)
            {
                evidenceCasesCompilationState.set (i, false);
            }
        }
        // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
            && (isInferenceMode))
        {
            if (!doPropagation (evidenceCase, currentCase))
            // if propagation does not succeed, restore previous state
            {
                if (nodeAlreadyHasFinding)
                {
                    try
                    {
                        evidenceCase.removeFinding (variable);
                    }
                    catch (NoFindingException e)
                    {/* Not possible */
                    }
                    Finding finding = new Finding (variable, oldState);
                    try
                    {
                        evidenceCase.addFinding (finding);
                    }
                    catch (InvalidStateException e)
                    {/* Not possible */
                    }
                    catch (IncompatibleEvidenceException e)
                    {/* Not possible */
                    }
                }
                else
                {
                    try
                    {
                        evidenceCase.removeFinding (variable);
                    }
                    catch (NoFindingException e)
                    { /* Not possible */
                    }
                }
                if (isInferenceMode)
                {
                    visualNode.setPostResolutionFinding (nodeAlreadyHasFinding);
                }
                else
                {
                    visualNode.setPreResolutionFinding (nodeAlreadyHasFinding);
                }
            }
        }
        // networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsFindingsDependent (networkPanel);
        // networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsPropagationTypeDependent (networkPanel);// ..
    }

    /**
     * Returns the inference algorithm assigned to the panel.
     * @return the inference algorithm assigned to the panel.
     */
    public InferenceAlgorithm getInferenceAlgorithm ()
    {
        return inferenceAlgorithm;
    }

    /**
     * Sets the inference algorithm assigned to the panel.
     * @param inferenceAlgorithm the inference Algorithm to be assigned to the
     *            panel.
     */
    public void setInferenceAlgorithm (InferenceAlgorithm inferenceAlgorithm)
    {
        this.inferenceAlgorithm = inferenceAlgorithm;
    }

    /**
     * This method does the propagation of the evidence in the network
     * @param evidenceCase the evidence case with which the propagation must be
     *            done.
     * @param caseNumber number of this evidence case.
     */
    public boolean doPropagation (EvidenceCase evidenceCase, int caseNumber)
    {
        Map<Variable, TablePotential> individualProbabilities = null;
        boolean propagationSucceded = false;
        try
        {
            long start = System.currentTimeMillis ();
            try
            {
                calculateMinAndMaxUtilityRanges ();
                VEPropagation vePosteriorValues = new VEPropagation(probNet, probNet.getVariables(), preResolutionEvidence, evidenceCase, null);
                individualProbabilities = vePosteriorValues.getPosteriorValues();
            }
            catch (OutOfMemoryError e)
            {
                if (!approximateInferenceWarningGiven)
                {
                	System.err.println("NotEnoughMemoryForExactInference : " + e.getMessage());
                    approximateInferenceWarningGiven = true;
                }
                inferenceAlgorithm = inferenceManager.getDefaultApproximateAlgorithm (probNet);
                inferenceAlgorithm.setPostResolutionEvidence (evidenceCase);
                individualProbabilities = inferenceAlgorithm.getProbsAndUtilities ();
            }
            catch (NotEvaluableNetworkException e)
            { 
                e.printStackTrace();
                return false;
            }
            long elapsedTimeMillis = System.currentTimeMillis () - start;
            System.out.println ("Inference took " + elapsedTimeMillis + " milliseconds.");
            updateNodesFindingState (evidenceCase);
            paintInferenceResults (caseNumber, individualProbabilities);
            propagationSucceded = true;
        }
        catch (IncompatibleEvidenceException e)
        {
        	System.err.println("ExceptionIncompatibleEvidence : " + e.getMessage());
            e.printStackTrace ();
        }
        catch (UnsupportedOperationException e)
        {
        	System.err.println("NoPropagationCanBeDone : " + e.getMessage());
        }
        catch (Exception e)
        {
        	System.err.println("ExceptionGeneric during inference : " + e.getMessage());
            e.printStackTrace ();
        }
        evidenceCasesCompilationState.set (caseNumber, propagationSucceded);
        
        return propagationSucceded;
    }

    // This commented method computes the exact ranges of the utility functions.
    // However, we are using an approximation in the method currently offered by
    // this class.
    /**
     * Calculates minUtilityRange and maxUtilityRange fields.
     */
    /*
     * private void () { TablePotential auxF; ArrayList<Variable>
     * utilityVariables = probNet .getVariables(NodeType.UTILITY); for (Variable
     * utility : utilityVariables) { auxF = probNet.getUtilityFunction(utility);
     * minUtilityRange.put(utility, Tools.min(auxF.values));
     * maxUtilityRange.put(utility, Tools.max(auxF.values)); } }
     */
    /**
     * Calculates minUtilityRange and maxUtilityRange fields. It is an
     * approximate implementation. The correct computation is given by a method
     * with the same name, but commented above.
     * @throws NonProjectablePotentialException
     */
    private void calculateMinAndMaxUtilityRanges ()
        throws NonProjectablePotentialException
    {
        List<Variable> utilityVariables = probNet.getVariables (NodeType.UTILITY);
        for (Variable utility : utilityVariables)
        {
            Node probNode = probNet.getNode (utility);
            minUtilityRange.put (utility, probNode.getApproximateMinimumUtilityFunction ());
            maxUtilityRange.put (utility, probNode.getApproximateMaximumUtilityFunction ());
        }
    }

    /**
     * This method fills the visualStates with the proper values to be
     * represented after the evaluation of the evidence case
     * @param caseNumber number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each
     *            variable.
     */
    private void paintInferenceResults (int caseNumber,
                                        Map<Variable, TablePotential> individualProbabilities)
    {
        for (VisualNode visualNode : visualNetwork.getAllNodes ())
        {
            Node probNode = visualNode.getNode ();
            Variable variable = probNode.getVariable ();
            switch (probNode.getNodeType ())
            {
            case CHANCE :
            case DECISION :
                paintInferenceResultsChanceOrDecisionNode(caseNumber, individualProbabilities, variable, visualNode);
                break;
            case UTILITY :
                paintInferenceResultsUtilityNode(caseNumber, individualProbabilities, variable, visualNode);
                break;
            }
        }
    }

    /**
     * This method fills the visualStates of a utility node with the proper
     * values to be represented after the evaluation of the evidence case
     * @param caseNumber number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each
     *            variable.
     * @param variable
     * @param visualNode
     */
    private void paintInferenceResultsUtilityNode (int caseNumber, Map<Variable, TablePotential> individualProbabilities, Variable variable, VisualNode visualNode)
    {
    	NumericVariableBox innerBox = (NumericVariableBox) visualNode.getInnerBox ();
        VisualState visualState = innerBox.getVisualState ();
        visualState.setStateValue (caseNumber, individualProbabilities.get (variable).values[0]);
        
        innerBox.setMinValue(((Double)minUtilityRange.get(variable)).doubleValue());
        innerBox.setMaxValue(((Double)maxUtilityRange.get(variable)).doubleValue());
    }

    /**
     * This method fills the visualStates of a chance or decision node with the
     * proper values to be represented after the evaluation of the evidence case
     * @param caseNumber number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each
     *            variable.
     * @param variable
     * @param visualNode
     */
    private void paintInferenceResultsChanceOrDecisionNode (int caseNumber, Map<Variable, TablePotential> individualProbabilities, Variable variable, VisualNode visualNode)
    {
        Potential potential = individualProbabilities.get (variable);
        if (potential instanceof TablePotential)
        {
            TablePotential tablePotential = (TablePotential) potential;
            if (tablePotential.getNumVariables () == 1)
            {
                double[] values = tablePotential.getValues ();
                if ((visualNode.getInnerBox ()) instanceof FSVariableBox)
                {
                    FSVariableBox innerBox = (FSVariableBox) visualNode.getInnerBox ();
                    for (int i = 0; i < innerBox.getNumStates (); i++)
                    {
                        VisualState visualState = innerBox.getVisualState (i);
                        visualState.setStateValue (caseNumber, values[i]);
                    }
                }
                // PROVISIONAL2: Currently the propagation
                // algorithm is returning a TablePotential
                // with 0 variables when the node has a Uniform
                // relation
            }
            else if (tablePotential.getNumVariables () == 0)
            {
                if ((visualNode.getInnerBox ()) instanceof FSVariableBox)
                {
                    FSVariableBox innerBox = (FSVariableBox) visualNode.getInnerBox ();
                    for (int i = 0; i < innerBox.getNumStates (); i++)
                    {
                        VisualState visualState = innerBox.getVisualState (i);
                        visualState.setStateValue (caseNumber, (1.0 / innerBox.getNumStates ()));
                    }
                }
                visualNode.setPostResolutionFinding (false);
                // END OF
                // PROVISIONAL2.............asaez...Comprobar si es innecesario
                // este Provisional2............
            }
            else
            {
            	System.err.println("Table Potential of " + variable.getName() + " has " + tablePotential.getNumVariables() + " variables. It cannot be treated by now.");
            }
        }
    }

    /**
     * This method updates the "finding state" of each node
     * @param evidenceCase the evidence case with which the update must be done.
     */
    public void updateNodesFindingState (EvidenceCase evidenceCase)
    {
        for (VisualNode visualNode : visualNetwork.getAllNodes ())
        {
            visualNode.setPreResolutionFinding (false);
            visualNode.setPostResolutionFinding (false);
        }
        for (Finding finding : evidenceCase.getFindings ())
        {
            Variable variable = finding.getVariable ();
            for (VisualNode visualNode : visualNetwork.getAllNodes ())
            {
                if (variable.getName ().equals (visualNode.getNode ().getName ()))
                {
                    visualNode.setPostResolutionFinding (true);
                }
            }
        }
        for (Finding finding : preResolutionEvidence.getFindings ())
        {
            Variable variable = finding.getVariable ();
            for (VisualNode visualNode : visualNetwork.getAllNodes ())
            {
                if (variable.getName ().equals (visualNode.getNode ().getName ()))
                {
                    visualNode.setPreResolutionFinding (true);
                }
            }
        }
    }

    public void temporalEvolution ()
    {
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            // new TraceTemporalEvolutionDialog (Utilities.getOwner (this), node.getProbNode (), preResolutionEvidence);
        }
    }

    /**
     * This method creates a new evidence case
     */
    public void createNewEvidenceCase ()
    {
        try
        {
            EvidenceCase newEvidenceCase = new EvidenceCase ();
            EvidenceCase currentEvidenceCase = getCurrentEvidenceCase ();
            List<Finding> currentFindings = currentEvidenceCase.getFindings ();
            for (int i = 0; i < currentFindings.size (); i++)
            {
                newEvidenceCase.addFinding (currentFindings.get (i));
            }
            addNewEvidenceCase (newEvidenceCase);
        }
        catch (InvalidStateException exc)
        {
        	System.err.println("ExceptionInvalidState : " + exc.getMessage());
        }
        catch (IncompatibleEvidenceException exc)
        {
        	System.err.println("ExceptionIncompatibleEvidence : " + exc.getMessage());
        }
        catch (Exception exc)
        {
        	System.err.println("ExceptionGeneric : " + exc.getMessage());
        }
    }

    /**
     * This method adds a new evidence case
     */
    public void addNewEvidenceCase (EvidenceCase newEvidenceCase)
    {
        setPropagationActive(isAutomaticPropagation ());
        postResolutionEvidence.add (newEvidenceCase);
        currentCase = (postResolutionEvidence.size () - 1);
        evidenceCasesCompilationState.add (currentCase, false);
        updateAllVisualStates("new", currentCase);
        // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        
        if (isPropagationActive () && networkPanel.getWorkingMode () == CPGXML.INFERENCE_WORKING_MODE)
        {
            if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) 
            	setPropagationActive (false);
        }
    }

    /**
     * This method makes the first evidence case to be the current
     */
    public void goToFirstEvidenceCase ()
    {
        currentCase = 0;
        updateAllVisualStates ("", currentCase);
        // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        
        if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
            && (networkPanel.getWorkingMode () == CPGXML.INFERENCE_WORKING_MODE))
        {
            if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
        }
        else
        {
            updateNodesFindingState (postResolutionEvidence.get (currentCase));
        }
    }

    /**
     * This method makes the previous evidence case to be the current
     */
    public void goToPreviousEvidenceCase ()
    {
        if (currentCase > 0)
        {
            currentCase--;
            updateAllVisualStates ("", currentCase);
            // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
            
            if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
                && (networkPanel.getWorkingMode () == CPGXML.INFERENCE_WORKING_MODE))
            {
                if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
            }
            else
            {
                updateNodesFindingState (postResolutionEvidence.get (currentCase));
            }
        }
        else
        {
        	System.err.println("NoPreviousEvidenceCase");
        }
    }

    /**
     * This method makes the next evidence case to be the current
     */
    public void goToNextEvidenceCase ()
    {
        if (currentCase < (postResolutionEvidence.size () - 1))
        {
            currentCase++;
            updateAllVisualStates ("", currentCase);
            // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
            
            if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
                && (networkPanel.getWorkingMode () == CPGXML.INFERENCE_WORKING_MODE))
            {
                if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
            }
            else
            {
                updateNodesFindingState (postResolutionEvidence.get (currentCase));
            }
        }
        else
        {
        	System.err.println("NoNextEvidenceCaseMessage : ");
        }
    }

    /**
     * This method makes the last evidence case to be the current
     */
    public void goToLastEvidenceCase ()
    {
        currentCase = (postResolutionEvidence.size () - 1);
        updateAllVisualStates ("", currentCase);
        // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        
        if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
            && (networkPanel.getWorkingMode () == CPGXML.INFERENCE_WORKING_MODE))
        {
            if (doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
        }
        else
        {
            updateNodesFindingState (postResolutionEvidence.get (currentCase));
        }
    }

    /**
     * This method clears out all the evidence cases. It returns to an 'initial
     * state' in which there is only an initial evidence case with no findings
     * (corresponding to prior probabilities)
     */
    public void clearOutAllEvidenceCases ()
    {
        setPropagationActive (isAutomaticPropagation ());
        postResolutionEvidence.clear ();
        evidenceCasesCompilationState.clear ();
        EvidenceCase newEvidenceCase = new EvidenceCase ();
        postResolutionEvidence.add (newEvidenceCase);
        currentCase = 0;
        evidenceCasesCompilationState.add (currentCase, false);
        updateAllVisualStates ("clear", currentCase);
        // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        
        if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
    }

    /**
     * This method updates all visual states of all visual nodes when it is
     * needed for a navigation operation among the existing evidence cases, a
     * creation of a new case or when all cases are cleared out.
     * @param option the specific operation to be done over the visual states.
     */
    public void updateAllVisualStates (String option, int caseNumber)
    {
        List<VisualNode> allVisualNodes = visualNetwork.getAllNodes ();
        for (VisualNode visualNode : allVisualNodes)
        {
            InnerBox innerBox = visualNode.getInnerBox ();
            VisualState visualState = null;
            for (int i = 0; i < innerBox.getNumStates (); i++)
            {
                if (innerBox instanceof FSVariableBox)
                {
                    visualState = ((FSVariableBox) innerBox).getVisualState (i);
                }
                else if (innerBox instanceof NumericVariableBox)
                {
                    visualState = ((NumericVariableBox) innerBox).getVisualState ();
                }
                if (option.equals ("new"))
                {
                    visualState.createNewStateValue ();
                }
                else if (option.equals ("clear"))
                {
                    visualState.clearAllStateValues ();
                }
                visualState.setCurrentStateValue (caseNumber);
            }
        }
    }

    /**
     * This method does the propagation of the evidence for all the evidence
     * cases in memory.
     * @param mainPanelMenuAssistant the menu assistant associated to the main
     *            panel.
     */
    public void propagateEvidence ()
    {
        setPropagationActive (true);
        if (networkPanel.getWorkingMode () == CPGXML.INFERENCE_WORKING_MODE)
        {
            for (int i = 0; i < getNumberOfCases (); i++)
            {
                if (evidenceCasesCompilationState.get (i) == false)
                {
                    if (doPropagation (getEvidenceCase (i), i)) setPropagationActive (false);
                }
            }
            updateAllVisualStates ("", currentCase);
            // networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
            updateNodesFindingState (postResolutionEvidence.get (currentCase));
        }
        // mainPanelMenuAssistant.updateOptionsEvidenceCasesNavigation (networkPanel);
        // mainPanelMenuAssistant.updateOptionsPropagationTypeDependent (networkPanel);
        // mainPanelMenuAssistant.updateOptionsFindingsDependent (networkPanel);
    }

    /***
     * Initializes the link restriction potential of a link
     */
    public void enableLinkRestriction ()
    {
        List<VisualLink> links = visualNetwork.getSelectedLinks ();
        if (!links.isEmpty ())
        {
            Link link = links.get (0).getLink ();
            if (!link.hasRestrictions ())
            {
                link.initializesRestrictionsPotential ();
            }
            link.resetRestrictionsPotential ();
        }
    }

    /***
     * Resets the link restriction potential of a link
     */
    public void disableLinkRestriction ()
    {
        List<VisualLink> links = visualNetwork.getSelectedLinks ();
        if (!links.isEmpty ())
        {
            Link link = links.get (0).getLink ();
            link.setRestrictionsPotential (null);
        }
    }

    /***
     * Initializes the revelation arc properties of a link
     */
    public void enableRevelationArc ()
    {
        List<VisualLink> links = visualNetwork.getSelectedLinks ();
        if (!links.isEmpty ())
        {
            Link link = links.get (0).getLink ();
        }
    }

    /**
     * Sets a new visualNetwork.
     * @param visualNetwork
     */
    public void setVisualNetwork (VisualNetwork visualNetwork)
    {
        this.visualNetwork = visualNetwork;
    }

    /**
     * Returns the visualNetwork.
     * @return the visualNetwork.
     */
    public VisualNetwork getVisualNetwork ()
    {
        return visualNetwork;
    }

    public void setProbNet (ProbNet probNet)
    {
        networkChanged = true;
        this.probNet = probNet;
        visualNetwork.setProbNet (probNet);
    }

    /**
     * Sets workingMode
     * @param newWorkingMode
     */
    public void setWorkingMode (int newWorkingMode)
    {
        visualNetwork.setWorkingMode (newWorkingMode);
        if (newWorkingMode == CPGXML.INFERENCE_WORKING_MODE)
        {
            editionMode = editionModeManager.getDefaultEditionMode ();
        }
    }

    public void editClass ()
    {
        visualNetwork.editClass ();
    }

    public void setParameterArity (ParameterArity arity)
    {
        visualNetwork.setParameterArity (arity);
    }
}
