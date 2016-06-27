/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package com.boguenon.service.modules.bayes.editor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openmarkov.core.model.network.VariableType;

/**
 * This class implements the graphic representation of each state that a node
 * has.
 * @author asaez
 * @version 1.0
 */
public class VisualState
{
    /**
     * Number of decimals
     */
    public static final int   NUMBER_OF_DECIMALS = 5;
    
    /**
     * The VisualNode this State is associated to.
     */
    private VisualNode          visualNode;
    /**
     * The order number assigned to this State. Determines in which position
     * will be painted this state.
     */
    private int                 stateNumber;
    /**
     * The name assigned to this State.
     */
    private String              stateName;
    /**
     * Array of values assigned to the state. There is one value for each
     * evidence case in memory.
     */
    private ArrayList<Double>   stateValues;
    /**
     * This variable indicates which is the position of the arrayList currently
     * selected (corresponding with the current evidence case).
     */
    private int                 currentStateValue;
    /**
     * Array of booleans that determine whether the state has evidence or not
     */
    private List<Boolean>       evidence              = new ArrayList<> ();

    /**
     * Formatting string for values shown in the visual state
     */
    private String formattingString = "0.";

    /**
     * Creates a new State.
     * @param visualNode visualNode to which this State is associated.
     * @param number order number to be assigned to this State inside the inner
     *            box.
     * @param name name of this state.
     * @param numValues Number of values that has to have each visual state.
     */
    public VisualState (VisualNode visualNode, int number, String name, int numValues)
    {
        this.visualNode = visualNode;
        this.stateNumber = number;
        this.stateName = name;
        stateValues = new ArrayList<Double> (numValues);
        for (int i = 0; i < numValues; i++)
        {
            stateValues.add (0.0);
        }
        evidence = new ArrayList<> ();
        evidence.add (false);
        currentStateValue = 0;
        StringBuilder sb = new StringBuilder(formattingString);
        for(int i=0; i < NUMBER_OF_DECIMALS;++i)
        {
            sb.append("0");
        }
        formattingString = sb.toString();
    }
    
    /**
     * Creates a new State.
     * @param visualNode visualNode to which this State is associated.
     * @param number order number to be assigned to this State inside the inner
     *            box.
     * @param name name of this state.
     */
    public VisualState (VisualNode visualNode, int number, String name)
    {
        this(visualNode, number, name, 1);
    }    

    /**
     * Returns the visualNode to which this sate is associated.
     * @return visualNode to which this sate is associated.
     */
    public VisualNode getVisualNode ()
    {
        return visualNode;
    }

    /**
     * Sets the visualNode to which this sate is associated.
     * @param visualNode the visualNode to which this sate is associated.
     */
    public void setVisualNode (VisualNode visualNode)
    {
        this.visualNode = visualNode;
    }

    /**
     * Returns the order number assigned to this state.
     * @return order number assigned to this state.
     */
    public int getStateNumber ()
    {
        return stateNumber;
    }

    /**
     * Sets the order number of this state.
     * @param stateNumber the order number of this state.
     */
    public void setStateNumber (int stateNumber)
    {
        this.stateNumber = stateNumber;
    }

    /**
     * Returns the name assigned to this state.
     * @return name assigned to this state.
     */
    public String getStateName ()
    {
        return stateName;
    }

    /**
     * Sets the name of this state.
     * @param stateName the name of this state.
     */
    public void setStateName (String stateName)
    {
        this.stateName = stateName;
    }

    /**
     * Sets which is the position of the array of values that is selected.
     * @param currentStateValue the position of the array of values to be set.
     */
    public void setCurrentStateValue (int currentStateValue)
    {
        this.currentStateValue = currentStateValue;
    }

    /**
     * Creates a new position in the array of values of the visual state It is
     * initially assigned 0.0 to this new position
     */
    public void createNewStateValue ()
    {
        stateValues.add (0.0);
        evidence.add (false);
    }

    /**
     * Clears all the positions in the array of values of the visual state and
     * creates again the initial position assigning 0.0 to it
     */
    public void clearAllStateValues ()
    {
        stateValues.clear ();
        stateValues.add (0, 0.0);
        evidence.clear ();
        evidence.add (false);
    }

    /**
     * Sets the value of this state for the given position of the array (this
     * position matches the evidence case number). The value is truncated so it
     * only has NUMBER_OF_DECIMALS decimals
     * @param caseNumber the position in the array to be established
     * @param value the value to be set
     */
    public void setStateValue (int caseNumber, double value)
    {
        try
        {
            // Value is currently formatted fixely with 4 decimals
            double truncatedValue = (Math.rint(value * Math.pow(10, NUMBER_OF_DECIMALS)))
                    / Math.pow(10, NUMBER_OF_DECIMALS);
            stateValues.set (caseNumber, truncatedValue);
        }
        catch (Exception exc)
        {
            //"ERROR" + "\n\n" + exc.getMessage (),
              // StringDatabase.getUniqueInstance ().getString ("ExceptionGeneric.Title.Label"),
        }
    }

    /**
     * Returns the number of positions in the array. This number is the same
     * that the number of evidence cases in memory and the same that the number
     * of bars that should be painted
     * @return the number of bars to be painted for that state.
     */
    public int getNumberOfValues ()
    {
        return stateValues.size ();
    }

    /**
     * Calculates the position that this state occupies inside the inner box.
     * This position is reserve
     * @return the position that this state occupies inside the inner box
     */
    private int getStatePosition ()
    {
        InnerBox innerBox = (InnerBox) visualNode.getInnerBox ();
        if (innerBox instanceof FSVariableBox)
        {
            return (((FSVariableBox) innerBox).getNumStates () - stateNumber);
        }
        else
        {
            return 1;
        }
    }

    /**
     * Paint the representation of the state when it is its not compiled form
     * @param x x coordinate reference for painting
     * @param y y coordinate reference for painting
     * @param g graphics object where paint the node.
     */
    private void paintNotCompiled (Double x, Double y, StringBuilder sb)
    {
    	sb.append("<value notcompiled='T'>");
    	sb.append("<barlength>0</barlength>");
    	sb.append("</value>");
    }
    
    public String refreshGraphicsInfo()
    {
    	StringBuilder sb = new StringBuilder();
    	int stateposition = getStatePosition();
    	
    	boolean ispropagationactive = getVisualNode ().getVisualNetwork ().isPropagationActive();
    	sb.append("<visualstate name='" + stateName + "' position='" + stateposition + "' ispropagationactive='" + (ispropagationactive ? "T" : "F") + "'>");
    	
        Double xName = 0.0d;
        Double xBar = 0.0d;
        Double xValue = 0.0d;
        Double yText = 0.0d;
        Double yFirstBar = 0.0d;
        
        boolean isNumeric = visualNode.getNode().getVariable().getVariableType() == VariableType.NUMERIC;
        
        if (isNumeric)
        {
            xBar = xName;
            xValue = xName;
        }
        else
        {
            xBar = xName;
            xValue = xName;
        }
        
        if (visualNode.getVisualNetwork().isPropagationActive())
        {
        }
        else
        {
        }
        
        sb.append("<values>");
        
        if (ispropagationactive)
        {
            for (int i = 0; i < stateValues.size (); i++)
            {
                double barLength = 0.0;
                if (isNumeric)
                {
                    InnerBox innerBox = visualNode.getInnerBox ();
                    Double minRange = ((NumericVariableBox) innerBox).getMinValue ();
                    Double maxRange = ((NumericVariableBox) innerBox).getMaxValue ();
                    Double range = maxRange - minRange;
                    Double value = stateValues.get(i) - minRange;
                    barLength = (value * 100) / range;
                }
                else
                {
                    barLength = (stateValues.get (i) * 10000D) / InnerBox.BAR_FULL_LENGTH;
                }
                
                sb.append("<value>");
                sb.append("<barlength>" + barLength + "</barlength>");
                
                // Value is currently formatted fixely with 4 decimals
                DecimalFormat decimalFormat = new DecimalFormat (formattingString,new DecimalFormatSymbols (Locale.US));
                String formattedValue = String.valueOf (decimalFormat.format (stateValues.get (currentStateValue)));
                sb.append("<formatvalue>" + formattedValue + "</formatvalue>");
                sb.append("</value>");
            }
        }
        else
        {
            if (getVisualNode ().hasAnyFinding ())
            {
                if (evidence.get(currentStateValue))
                {
                    // setColorCaseDependent (currentStateValue);
                	sb.append("<value>");
                	sb.append("<barlength>100</barlength>");
                	sb.append("</value>");
                }
                else
                {
                    paintNotCompiled (xBar, yFirstBar, sb);
                }
            }
            else
            {
                paintNotCompiled (xBar, yFirstBar, sb);
            }
        }
        
        sb.append("</values>");
    	
    	sb.append("</visualstate>");
    	
    	return sb.toString();
    }

    public void removeFinding ()
    {
        evidence.set (currentStateValue, false);
    }

    public void addFinding ()
    {
        evidence.set (currentStateValue, true);
    }
}
