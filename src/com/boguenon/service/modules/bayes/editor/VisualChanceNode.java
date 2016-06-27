package com.boguenon.service.modules.bayes.editor;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNode;

public class VisualChanceNode extends VisualNode {
	/**
	 * Width of a the arc of the rounded rectangle.
	 */
	private static final double ARC_WIDTH = 20;

	/**
	 * Height of a the arc of the rounded rectangle.
	 */
	private static final double ARC_HEIGHT = 20;

	/**
	 * Creates a new visual node from a node.
	 * 
	 * @param node
	 *            object that has the information of the node.
	 * @param visualNetwork
	 *            editor panel to which this visual node is associated.
	 */
	public VisualChanceNode(Node node, VisualNetwork visualNetwork) {
		super(node, visualNetwork);
		expanded = false;
		preResolutionFinding = false;
		postResolutionFinding = false;
		innerBox = new FSVariableBox(this);
	}

	/**
	 * Paints the visual node into the graphics object as a rounded rectangle.
	 * 
	 * @param g
	 *            graphics object where paint the node.
	 */
	public void paint() {
		String text = getNodeString();

//		if (probNode.isAlwaysObserved()) {
//			g.setPaint(ALWAYS_OBSERVED_COLOR);
//			g.setStroke((isSelected())? OBSERVED_WIDE_STROKE : OBSERVED_NORMAL_STROKE);
//		} else if (probNode.isInput ()) {
//		    g.setStroke((isSelected())? WIDE_DASHED_STROKE : NORMAL_DASHED_STROKE);
//		} else {
//            g.setStroke((isSelected())? WIDE_STROKE : NORMAL_STROKE);
//		}

		if (isExpanded()) {
			innerBox.paint();
		}

	}
}
