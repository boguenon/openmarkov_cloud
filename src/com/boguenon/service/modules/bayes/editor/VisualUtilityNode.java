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
import org.openmarkov.core.model.network.ProbNode;

/**
 * This class is the visual representation of a utility node.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - fix public and static methods, fix Double comparison
 * @version 1.2 asaez - add expanded representation
 */
public class VisualUtilityNode extends VisualNode {
    /**
     * Creates a new visual node from a node.
     * 
     * @param node
     *            object that has the information of the node.
     * @param panel
     *            editor panel to which this visual node is associated.
     */
    public VisualUtilityNode(Node node, VisualNetwork visualNetwork) {
        super(node, visualNetwork);
        expanded = false;
        preResolutionFinding = false;
        postResolutionFinding = false;
        innerBox = new NumericVariableBox(this, " EU");
    }

    /**
     * Paints the visual node into the graphics object as a hexagon.
     * 
     * @param g
     *            graphics object where paint the node.
     */
    @Override
    public void paint() {
    	String text = getNodeString();

        if (isExpanded()) {
            innerBox.paint();
        }
    }
}
