package com.boguenon.service.modules.bayes.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.oopn.Instance.ParameterArity;

import com.boguenon.service.modules.bayes.CPGXML;

public class VisualNetwork {
	protected ProbNet probNet = null;
	protected boolean byTitle = false;
	public List<VisualNode> visualNodes = new ArrayList<VisualNode>();
	protected List<VisualLink> visualLinks = new ArrayList<VisualLink>();
	protected Set<VisualNode> selectedNodes = new HashSet<VisualNode>();
	protected Set<VisualLink> selectedLinks = new HashSet<VisualLink>();
	// protected VisualArrow newLink = null;
	protected VisualNode newLinkSource = null;
	
    protected boolean isPropagationActive = true;
    
    protected int workingMode = CPGXML.EDITION_WORKING_MODE;
    
	public VisualNetwork(ProbNet probNet) {
        
		this.probNet = probNet;
		
		//network.addNetworkChangeListener(this);
		//changed by mpalacios
		constructVisualInfo();
	}

	protected void constructVisualInfo() {

		List<Node> nodes = null;
		List<VisualNode> vNodesToDelete = new ArrayList<VisualNode>();
		List<VisualLink> vLinksToDelete = new ArrayList<VisualLink>();
		List<Link<Node>> links = null;
		Node nodeToCheck = null;
		Link linkToCheck = null;
		VisualNode vNode1 = null;
		VisualNode vNode2 = null;
		int i = -1;
		int visualNodesCount = -1;

		
		nodes = probNet.getNodes();
		
		for (VisualNode vNode : visualNodes) {
			nodeToCheck = vNode.getNode();
			int index = nodes.indexOf(nodeToCheck);
			vNodesToDelete.add(vNode);
		}
		
		visualNodes.removeAll(vNodesToDelete);

		for (Node node : nodes) 
		{
			vNode1 = createVisualNode(node);
			visualNodes.add(vNode1);
			vNode1.setByTitle(byTitle);
		}
		
		//links = probNet.backupProbNet.getLinks();
		links = probNet.getLinks();
		
		for (VisualLink vLink : visualLinks) 
		{
			linkToCheck = vLink.getLink();
			if (links.contains(linkToCheck) && !containsNodeToDelete(linkToCheck, vNodesToDelete)) 
			{
				links.remove(linkToCheck);
			} 
			else 
			{
				vLinksToDelete.add(vLink);
			}
		}
		visualLinks.removeAll(vLinksToDelete);
		visualNodesCount = visualNodes.size();
		for (Link link : links) {
			i = 0;
			vNode1 = null;
			vNode2 = null;
			while ((i < visualNodesCount) && ((vNode1 == null) || (vNode2 == null))) {
				if (vNode1 == null) {
					if (link.getNode1().equals(
						visualNodes.get(i).getNode())) {
						vNode1 = visualNodes.get(i);
					}
				}
				if (vNode2 == null) {
					if (link.getNode2().equals(
						visualNodes.get(i).getNode())) {
						vNode2 = visualNodes.get(i);
					}
				}
				i++;
			}
			if ((vNode1 != null) && (vNode2 != null)) {
				visualLinks.add(new VisualLink(link, vNode1, vNode2));
			}
		}
	}

	/**
	 * Returns whether the link contains nodes to delete
	 * @param linkToCheck
	 * @param vNodesToDelete
	 * @return
	 */
	protected boolean containsNodeToDelete(Link linkToCheck, List<VisualNode> vNodesToDelete) {
		
		for (VisualNode vNode: vNodesToDelete)
		if (linkToCheck.contains(vNode.getNode()))
			return true;
		
			return false;
	}

	/**
	 * Changes the presentation mode of the text of the nodes.
	 * 
	 * @param value
	 *            new value of the presentation mode of the text of the nodes.
	 */
	public void setByTitle(boolean value) {

		if (byTitle != value) {
			byTitle = value;
		}
		for (VisualNode node : visualNodes) {
			node.setByTitle(value);
		}

	}

	/**
	 * Returns the presentation mode of the text of the nodes.
	 * 
	 * @return true if the title of the nodes is the name or false if it is the
	 *         name.
	 */
	public boolean getByTitle() {

		return byTitle;

	}

	/**
	 * Creates a new list of visual nodes reordering them following
	 * this criteria: 
	 * - first criteria: selection state -> the selected nodes are in
	 *   the first places of the array.
	 * - second criteria: relevance -> the higher the relevance 
	 *   the nearer to the start of the array.
	 * 
	 * @return a new ordered array (first, selected nodes, and last, 
	 * 			non selected nodes; each group is ordered in 
	 * 			descending relevance criteria).
	 */
	private ArrayList<VisualNode> reorderVisualNodes() {
		int selPos = 0;
		ArrayList<VisualNode> newList = new ArrayList<VisualNode>();
		ArrayList<VisualNode> nodesSelected = new ArrayList<VisualNode>();
		ArrayList<VisualNode> nodesUnselected = new ArrayList<VisualNode>();

		for (VisualNode node : visualNodes) {
			nodesUnselected.add(node);
		}
		
		int selected = nodesSelected.size();
		int counter1 = 0;
		while (counter1 < selected) {
			VisualNode candidate = null;
			double highestRelevance = -1;
			for (int i=0; i<nodesSelected.size(); i++) {
				double relevance = nodesSelected.get(i).getNode().getRelevance();
				if (relevance > highestRelevance) {
					highestRelevance = relevance;
					candidate = nodesSelected.get(i);
				}
			}
			newList.add(selPos, candidate);
			selPos++;
			nodesSelected.remove(candidate);
			counter1++;
		}
		
		int unselected = nodesUnselected.size();
		int counter2 = 0;
		while (counter2 < unselected) {
			VisualNode candidate = null;
			double highestRelevance = -1;
			for (int i=0; i<nodesUnselected.size(); i++) {
				double relevance = nodesUnselected.get(i).getNode().getRelevance();
				if (relevance > highestRelevance) {
					highestRelevance = relevance;
					candidate = nodesUnselected.get(i);
				}
			}
			newList.add(selPos, candidate);
			selPos++;
			nodesUnselected.remove(candidate);
			counter2++;
		}
		
		return newList;
	}

	/**
	 * This method returns a list containing all the nodes in the network.
	 * 
	 * @return a list containing all the nodes in the network.
	 */
	public List<VisualNode> getAllNodes() {
		return visualNodes;
	}

    /**
     * This method returns a list that contains all the links that leave of or
     * arrive in one node of the list of nodes passed as parameter.
     * @param nodes list of nodes whose links are returned.
     * @param onlyBothEnds returns only those links whose two ends are selected
     * @return a list of links related to the nodes.
     */
    public List<VisualLink> getLinksOfNodes (List<VisualNode> nodes, boolean onlyBothEnds)
    {
        ArrayList<VisualLink> links = new ArrayList<VisualLink> ();
        int i, l = nodes.size ();
        boolean found = false;
        boolean foundSource = false;
        boolean foundDestination = false;
        for (VisualLink visualLink : visualLinks)
        {
            found = false;
            foundSource = false;
            foundDestination = false;
            i = 0;
            while (!found && (i < l))
            {
                foundSource |= visualLink.getSourceNode ().equals (nodes.get (i));
                foundDestination |= visualLink.getDestinationNode ().equals (nodes.get (i));
                found = (onlyBothEnds)? foundSource && foundDestination : foundSource || foundDestination;  
                if (found)
                {
                    links.add (visualLink);
                }
                else
                {
                    i++;
                }
            }
        }
        return links;
    }
	
    /**
     * This method returns a list that contains all the links that leave of or
     * arrive in one node of the list of nodes passed as parameter.
     * @param nodes list of nodes whose links are returned.
     * @return a list of links related to the nodes.
     */
    public List<VisualLink> getLinksOfNodes (List<VisualNode> nodes)
    {
        return getLinksOfNodes (nodes, false);
    }

	/**
	 * This method returns a list containing the selected nodes.
	 * 
	 * @return a list containing the selected nodes.
	 */
	public List<VisualNode> getSelectedNodes() {

		return new ArrayList<VisualNode>(selectedNodes);

	}
	
	public VisualNode getNode(String nodename) {
		VisualNode vnode = null;
		
		for (int i=0; i < this.visualNodes.size(); i++)
		{
			if (this.visualNodes.get(i).getNodeString().equals(nodename))
			{
				vnode = this.visualNodes.get(i);
				break;
			}
		}
		
		return vnode;
	}

	/**
	 * This method returns a list containing the selected links.
	 * 
	 * @return a list containing the selected links.
	 */
	public List<VisualLink> getSelectedLinks() {

		return new ArrayList<VisualLink>(selectedLinks);

	}
	
	/**
	 * Returns the number of selected nodes.
	 * 
	 * @return number of selected nodes.
	 */
	public int getSelectedNodesNumber() {

		return selectedNodes.size();

	}

	/**
	 * Returns the number of selected links.
	 * 
	 * @return number of selected links.
	 */
	public int getSelectedLinksNumber() {

		return selectedLinks.size();

	}

	/**
	 * Returns the network that is painted by this object.
	 * 
	 * @return network which is painted.
	 */
	public ProbNet getNetwork() {

		return probNet;

	}

	protected VisualNode createVisualNode(Node node) {

		switch (node.getNodeType()) {
			case CHANCE: {
				return new VisualChanceNode(node, this);
			}
			case DECISION: {
			    return new VisualDecisionNode(node, this);
			}
			case UTILITY: {
				return new VisualUtilityNode(node, this);
			}
			default: {
				return null;
			}
		}

	}

	public void setProbNet(ProbNet probNet) {
		if(!this.probNet.equals(probNet))
		{
			this.probNet = probNet;
			clean();
			constructVisualInfo();
		}
	}

	protected void clean ()
    {
        visualNodes.clear ();
        visualLinks.clear ();
        selectedNodes.clear ();
        selectedLinks.clear ();
        newLinkSource = null;
    }
   
    /**
     * Returns the isPropagationActive.
     * @return the isPropagationActive.
     */
    public boolean isPropagationActive ()
    {
        return isPropagationActive;
    }

    /**
     * Sets the isPropagationActive.
     * @param isPropagationActive the isPropagationActive to set.
     */
    public void setPropagationActive (boolean isPropagationActive)
    {
        this.isPropagationActive = isPropagationActive;
    }

    public void setWorkingMode (int workingMode)
    {
        this.workingMode = workingMode;
    }  
    public int getWorkingMode ()
    {
        return workingMode;
    }

    public void editClass ()
    {
        // TODO Auto-generated method stub
    }
    
    public void paint()
    {
    	
    }

	public void setParameterArity(ParameterArity arity) {
		// TODO Auto-generated method stub
	}    
}
