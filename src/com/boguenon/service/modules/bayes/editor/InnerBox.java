/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package com.boguenon.service.modules.bayes.editor;

/**
 * This abstract class specifies the methods that all inner boxes of the 
 * visual nodes have to implement.
 * 
 * @author asaez 
 * @version 1.0
 */
public abstract class InnerBox {
	protected double height;
	
	protected static final double BAR_FULL_LENGTH = 100;
	
	/**
	 * The VisualNode this InnerBox is associated to.
	 */
	protected VisualNode visualNode;
	
	/**
	 * Returns the visualNode associated with the innerBox.
	 * 
	 * @return visualNode associated with the innerBox.
	 */
	public VisualNode getVisualNode() {
		return visualNode;
	}
	
	/**
	 * Returns the number of visual states of this inner box.
	 * 
	 * @return the number of visual states of this inner box.
	 */
	public abstract int getNumStates();
	
	public abstract void update(int i);
	
	public void paint() {
	}

	public String refreshGraphicsInfo()
	{
		String r = "<innerbox>";
		
		r += "</innerbox>";
		
		return r;
	}
}
