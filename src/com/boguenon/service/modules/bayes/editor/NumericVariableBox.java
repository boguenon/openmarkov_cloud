package com.boguenon.service.modules.bayes.editor;

import org.openmarkov.core.model.network.PartitionedInterval;

public class NumericVariableBox extends InnerBox {

	private static final double SCALE_VERTICAL_SEPARATION = 12D;
    private static final int SCALE_RANGE_HORIZONTAL_OFFSET = 8;
    private static final int SCALE_RANGE_VERTICAL_OFFSET = 4;
    private double minValue;
    private double maxValue;
    private VisualState visualState;
    
	public NumericVariableBox(VisualNode vNode) {
		this(vNode, "");
	}
	
	public NumericVariableBox(VisualNode vNode, String stateName)
	{
        minValue = (-1.0D / 0.0D);
        maxValue = (1.0D / 0.0D);
        visualState = null;
        visualNode = vNode;
        visualState = new VisualState(visualNode, 0, stateName);
        PartitionedInterval domain = vNode.getNode().getVariable().getPartitionedInterval();
        setMinValue(domain.getMin());
        setMaxValue(domain.getMax());
    }
	
	public double getMinValue()
    {
        return minValue;
    }

    public void setMinValue(double minValue)
    {
        this.minValue = Math.rint(minValue * 100D) / 100D;
    }

    public double getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(double maxValue)
    {
        this.maxValue = Math.rint(maxValue * 100D) / 100D;
    }
	
	public void update(int numCases)
    {
        PartitionedInterval domain = visualNode.getNode().getVariable().getPartitionedInterval();
        setMinValue(domain.getMin());
        setMaxValue(domain.getMax());
        String stateName = visualState == null ? "" : visualState.getStateName();
        visualState = new VisualState(visualNode, 0, stateName, numCases);
    }
	
	
	
	public VisualState getVisualState()
    {
        return visualState;
    }

    public void setVisualState(VisualState visualState)
    {
        this.visualState = visualState;
    }

    public int getNumStates()
    {
        return 1;
    }
    
    public String refreshGraphicsInfo()
	{
		String r = "<innerbox isnumeric='T' minvalue='" + minValue + "' maxvalue='" + maxValue + "'>";
		
		r += "<visualstates>";
        r += visualState.refreshGraphicsInfo();
        r += "</visualstates>";
        
		r += "</innerbox>";
		
		return r;
	}
}
