package com.boguenon.service.modules.bayes.editor;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNode;

public class VisualNode {
	/**
	 * Default width of a node when it is contracted. It is the width that it
	 * has if the length of its name is shorter enough; otherwise, its width is
	 * adjusted to fit the length of the name.
	 */
	protected static final double DEFAULT_NODE_CONTRACTED_WIDTH = 40;
	
	/**
	 * Width of a node when it is expanded.
	 */
	protected static final double NODE_EXPANDED_WIDTH = 205;
	
	/**
	 * Vertical margin of a node when it is expanded.
	 */
	protected static final double NODE_EXPANDED_HEIGHT_MARGIN = 5;
	
	/**
	 * Space from the left border of the node to the text.
	 */
	protected static final double HORIZONTAL_SPACE_TO_TEXT = 15;

	/**
	 * Space from the top border of the node to the text.
	 */
	protected static final double VERTICAL_SPACE_TO_TEXT = 4;

	/**
	 * Visual Network to which this visual node is associated.
	 */
	protected VisualNetwork visualNetwork;

	/**
	 * Object that has the node information.
	 */
	protected Node node;

	/**
	 * Object that manages the internal representation of the node when
	 * it is expanded.
	 */
	protected InnerBox innerBox;

	/**
	 * This variable determines if the node is going to be painted
	 * expanded (true) or contracted (false). 
	 */	
	protected boolean expanded;

	/**
	 * This variable indicates if the node has a pre-Resolution finding 
	 * established (true) or not (false). 
	 */	
	protected boolean preResolutionFinding;
	
	/**
	 * This variable indicates if the node has a post-Resolution finding 
	 * established (true) or not (false). 
	 */	
	protected boolean postResolutionFinding;
	//TODO Deber챠a ser un array de booleanos, con un valor por cada caso de evidencia
	//     (esto no pasa en el caso del preResol.)
	
	/**
	 * This variable influences the width of the node.
	 */
	protected boolean byTitle = false;

	/**
	 * Value of the X coordinate in temporal position of the node.
	 */
	protected int temporalCoordinateX;

	/**
	 * Value of the Y coordinate in temporal position of the node.
	 */
	protected int temporalCoordinateY;
	
	public VisualNode(Node node, VisualNetwork visualNetwork)
	{
	    this.node = node;
	    this.visualNetwork = visualNetwork;
	}

	/**
	 * Returns the string that must appear into the node. The variable 'byTitle'
	 * influences this string. If 'byTitle' is true and the node hasn't a title,
	 * then the name is used as title.
	 * 
	 * @return the string that must appear into the node.
	 */
	protected String getNodeString() {

		return node.getName();
	}


	/**
	 * Returns the node associated with the visual node.
	 * 
	 * @return information of the node.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Returns the InnerBox associated with the visual node.
	 * 
	 * @return innerBox associated with the visual node.
	 */
	public InnerBox getInnerBox() {
		return innerBox;
	}
	
	/**
	 * Sets the inner box associated to the node.
	 * 
	 * @param innerBox
	 *            new inner box associated to the node.
	 */
	public void setInnerBox(InnerBox innerBox) {
		this.innerBox = innerBox;
	}
	

	/**
	 * Returns true if the string of the node must be the title; otherwise,
	 * false.
	 * 
	 * @return true if the node will show the title; otherwise, false.
	 */
	public boolean getByTitle() {

		return byTitle;
	}

	/**
	 * Changes the type of the text (name or title) that appears inside the
	 * node.
	 * 
	 * @param newByTitle
	 *            true if the title of the node will be shown; false if the name
	 *            will be shown.
	 */
	public void setByTitle(boolean newByTitle) {

		byTitle = newByTitle;
	}

	/**
	 * Returns true if the node will be painted expanded; false if contracted.
	 * 
	 * @return true if the node will be painted expanded.
	 */	
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Establishes the way in which the node will be painted: expanded if true; 
	 * contracted if false.
	 * 
	 * @param expanded
	 *            true if the node has to be represented expanded; false if contracted.
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	/**
	 * Returns true if the node has a pre-Resolution finding established.
	 * 
	 * @return true if the node has a pre-Resolution finding established.
	 */	
	public boolean isPreResolutionFinding() {
		return this.preResolutionFinding;
	}

	/**
	 * Sets if the node has a pre-Resolution finding established or not.
	 * 
	 * @param findingInNode
	 *            true if the node has a pre-Resolution finding established.
	 */
	public void setPreResolutionFinding(boolean findingInNode) {
		this.preResolutionFinding = findingInNode;
	}
	
	/**
	 * Returns true if the node has a post-Resolution finding established.
	 * 
	 * @return true if the node has a post-Resolution finding established.
	 */	
	public boolean isPostResolutionFinding() {
		return this.postResolutionFinding;
	}

	/**
	 * Sets if the node has a post-Resolution finding established or not.
	 * 
	 * @param findingInNode
	 *            true if the node has a post-Resolution finding established.
	 */
	public void setPostResolutionFinding(boolean findingInNode) {
		this.postResolutionFinding = findingInNode;
	} 
	
	/**
	 * Returns true if the node has a finding established (pre or post-Resolution).
	 * 
	 * @return true if the node has a finding established (pre or post-Resolution).
	 */	
	public boolean hasAnyFinding() {
		return this.preResolutionFinding || this.postResolutionFinding;
	}
	
    /**
     * Returns the visualNetwork.
     * @return the visualNetwork.
     */
    public VisualNetwork getVisualNetwork ()
    {
        return visualNetwork;
    }
    
    public void paint()
    {
    	
    }
    
    public String refreshGraphicsInfo()
    {
    	String r = "";
    	
    	r = "<node name='" + node.getName() + "'>";
    	
    	if (this.innerBox != null)
    	{
    		r += this.innerBox.refreshGraphicsInfo();
    	}
    	
    	r += "</node>";
    	
    	return r;
    }
    
    @Override
    public String toString()
    {
    	StringBuilder sb = new StringBuilder();
    	sb.append(node.getName());
    	sb.append(" - ");
    	return sb.toString();
    }
}
