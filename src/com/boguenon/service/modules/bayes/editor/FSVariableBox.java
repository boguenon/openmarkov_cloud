/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package com.boguenon.service.modules.bayes.editor;


import java.util.HashMap;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;

/**
 * This class is the visual representation of the inner box associated
 * to a VisualNode that represents a Finite States variable
 * 
 * @author asaez 
 * @version 1.0
 */
public class FSVariableBox extends InnerBox {

	/**
	 * This variable contains a list of all the visual states that are part
	 * of this inner box.
	 */
	public HashMap<Integer, VisualState> visualStates = null;
	
	/**
	 * Creates a new Finite States Variable innerBox.
	 * 
	 * @param vNode
	 *            visualNode to which this Finite States Variable innerBox is associated.
	 */
	public FSVariableBox(VisualNode vNode) {
		visualNode = vNode;
		visualStates = new HashMap<Integer, VisualState>();
		createVisualStates();
	}
	
	/**
	 * This method creates a visual state for each state of the variable.
	 * Each visual state will have only a value
	 */
	protected void createVisualStates() {
		Node probNode = visualNode.getNode();
		Variable variable = probNode.getVariable();
		State[] states = variable.getStates();
		for (int i=0; i<states.length; i++) {
			VisualState visualState = new VisualState(visualNode, i, states[i].getName());
			this.visualStates.put(i, visualState);
		}
	}
	
	/**
	 * This method creates a visual state for each state of the variable.
	 * 
	 * @param numValues
	 *            Number of values that has to be each visual state.
	 */
	private void createVisualStates(int numValues) {
		Node probNode = visualNode.getNode();
		Variable variable = probNode.getVariable();
		State[] states = variable.getStates();
		for (int i=0; i<states.length; i++) {
			VisualState visualState =
					new VisualState(visualNode, i, states[i].getName(), numValues);
			this.visualStates.put(i, visualState);
		}
	}
	
	public void update(int numCases)
    {
        visualStates.clear();
        createVisualStates(numCases);
    }
	
	/**
	 * This method recreates the visual states of the inner box.
	 *  
	 * @param numCases
	 *            Number of evidence cases in memory.
	 */
	public void recreateVisualStates(int numCases) {
		visualStates.clear();
		createVisualStates(numCases);
	}
		
	/**
	 * Returns the visual state that occupies the given position.
	 * 
	 * @param numPosition
	 *            The position of the state to be returned.
	 * 
	 * @return the visual state that occupies the given position.
	 */
	public VisualState getVisualState(Integer numPosition) {
		return visualStates.get(numPosition);
	}
	
	/**
	 * Returns the visual state with the given name.
	 * 
	 * @param name
	 *            The name of the state to be returned.
	 * 
	 * @return the visual state with the given name.
	 */
	public VisualState getVisualState(String name) {
		VisualState visualState = null;
		for (int i=0; i<visualStates.size(); i++) {
			if (visualStates.get(i).getStateName().equals(name)) {
				visualState = visualStates.get(i);
			}
		}
		return visualState;
	}
	
	/**
	 * Returns the number of visual states of this inner box.
	 * 
	 * @return the number of visual states of this inner box.
	 */
	public int getNumStates() {
		return visualStates.size();
	}
			
	public String refreshGraphicsInfo()
	{
		String r = "<innerbox>";
		
		r += "<visualstates>";
		
		for (int i= 0; i < visualStates.size(); i++) 
		{
			r += visualStates.get(i).refreshGraphicsInfo();
		}
		
		r += "</visualstates>";
		
		r += "</innerbox>";
		
		return r;
	}
}
