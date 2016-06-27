package com.boguenon.service.modules.bayes.common;

import org.openmarkov.core.model.network.Node;

public abstract class PotentialPanel {

	public PotentialPanel() {
		// TODO Auto-generated constructor stub
	}

	/**
     * Fill the panel with the data from the node
     * @param probNode
     */
    public abstract void setData (Node node);
}
