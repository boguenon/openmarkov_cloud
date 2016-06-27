/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package com.boguenon.service.modules.bayes.editor;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;

/**
 * This class is the visual representation of a decision node.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo - fix public and static methods, fix Double comparison
 * @version 1.2 asaez - add expanded representation
 */
public class VisualDecisionNode extends VisualNode {
    /**
     * This attribute indicates if the node has an imposed policy
     */
    private boolean            hasPolicy                     = false;

    /**
     * Creates a new visual node from a node.
     * 
     * @param node
     *            object that has the information of the node.
     * @param visualNetwork
     *            editor panel to which this visual node is associated.
     */
    public VisualDecisionNode(Node node, VisualNetwork visualNetwork) {
        super(node, visualNetwork);
        expanded = false;
        if (node.getPolicyType() != PolicyType.OPTIMAL) {
            hasPolicy = true;
        }
        preResolutionFinding = false;
        postResolutionFinding = false;
        innerBox = new FSVariableBox(this);
        setHasPolicy(node.getPotentials().size() != 0);
    }

    /**
     * Returns a boolean indicating if the node has an imposed policy.
     * 
     * @return true if the node has imposed policy; false otherwise.
     */
    public boolean isHasPolicy() {
        return hasPolicy;
    }

    /**
     * Sets if the node has an imposed policy or not.
     * 
     * @param hasPolicy
     *            new value for the hasPolicy attribute.
     */
    public void setHasPolicy(boolean hasPolicy) {
        this.hasPolicy = hasPolicy;
    }

    /**
     * Paints the visual node into the graphics object as a rectangle.
     * 
     * @param g
     *            graphics object where paint the node.
     */
    public void paint() {

        String text = getNodeString();
//        double textHeight = getHeight(text, g);
//        double textWidth = getWidth(text, g);
        
//        if (preResolutionFinding) {
//            g.setPaint(BACKGROUND_PRE_FINDING_COLOR);
//        } else if (postResolutionFinding
//                && (visualNetwork.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
//            g.setPaint(BACKGROUND_POST_FINDING_COLOR);
//        } else {
//            if (hasPolicy) {
//                g.setPaint(BACKGROUND_POLICY_SET_COLOR);
//            } else {
//                g.setPaint(BACKGROUND_COLOR);
//            }
//        }
//        g.fill(shape);
//        g.setPaint(FOREGROUND_COLOR);
//        g.setStroke(getContourStroke());
//        g.draw(shape);
//        g.setFont(FONT_HELVETICA);
//        g.setPaint(TEXT_FOREGROUND_COLOR);
//
//        if (isExpanded()) {
//            double rectangleWitdh = points[1].getX() - points[0].getX();
//            text = adjustText(text, rectangleWitdh, 3, FONT_HELVETICA, g);
//            textWidth = getWidth(text, g);
//        }
//        double textPosX = getTemporalPosition().getX() - (textWidth / 2);
//        double textPosY = points[0].getY() + textHeight;
//
//        g.drawString(text, (float) textPosX, (float) textPosY);
//
//        if (isExpanded()) {
//            innerBox.paint();
//        }

    }
}
