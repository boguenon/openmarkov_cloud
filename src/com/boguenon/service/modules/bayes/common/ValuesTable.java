package com.boguenon.service.modules.bayes.common;

import java.util.LinkedList;
import java.util.List;

import org.openmarkov.core.exception.DeterministicValueNotAllowedException;
import org.openmarkov.core.exception.ProbabilisticValueNotAllowedException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;

public class ValuesTable extends JTable
{
	/**
	 * first editable Column
	 */
	public static final int                    FIRST_EDITABLE_COLUMN      = 1;
	/**
	 * boolean data model (to know if a value has been changed)
	 */
	protected boolean[][]                      dataModified               = null;
	/**
	 * type of node for this variable
	 */
	protected NodeType                         nodeType                   = null;
	/**
	 * number of decimals positions to be used for calculations and display
	 */
	protected static int                       decimalPositions           = 2;                                  // by
	                                                                                                             // default;
	/**
	 * last editable row. By default, it is zero until runtime initialization
	 */
	protected int                              lastEditableRow            = 0;
	/**
	 * define if the model is deterministic or probabilistic. By default, the
	 * model is probabilistic (false)
	 */
	protected boolean                          deterministic              = false;
	/**
	 * define if the table is using General or Canonical Potentials
	 * <ul>
	 * <li>if index = 0 then Using General Potential</li>
	 * <li>if index = 1,2,3 then Using Canonical Potential (family OR)</li>
	 * <li>if index = 4,5,6 then Using Canonical Potential (famili AND)</li>
	 */
	protected int                              indexPotential             = 0;                                  // General
	                                                                                                             // Potential
	                                                                                                             // by
	                                                                                                             // default
	/**
	 * define if the table shows all parameters or only independent parameters
	 */
	protected boolean                          showingAllParameters       = false;
	/**
	 * define if the table shows probabilities values or state name
	 */
	protected boolean                          showingProbabilitiesValues = false;
	/**
	 * define if the table shows TPC values or canonical values
	 */
	protected boolean                          showingTPCvalues           = false;
	/**
	 * define if the table shows Optimal Decision
	 */
	protected boolean                          showingOptimal             = false;
	protected Node                         	   node;
	protected ProbNet                          probNet;
	/**
	 * Define the last column of the table that was modified
	 */
	protected int                              lastCol                    = -1;
	/**
	 * Define the priority list when potential values are edited
	 */
	protected List<Integer>                    priorityList               = new LinkedList<Integer> ();
	private boolean                            isSelectAllForMouseEvent   = true;
	private boolean                            isSelectAllForActionEvent  = false;
	private boolean                            isSelectAllForKeyEvent     = false;
	
	/**
	 * default constructor with parameters
	 */
	public ValuesTable (Node node)
	{
	    this.node = node;
	    this.probNet = node.getProbNet ();
	}
	
	/**
	 * Constructor for ValuesTable
	 */
	public ValuesTable ()
	{
	}
	
	/**
	 * check the value to modify in the table and sets
	 */
	public void setValueAt (Object newValue, int row, int col)
	{
	    Object oldValue = getValueAt (row, col);
	    if (((Double) newValue) < 0 && node.getNodeType() != NodeType.UTILITY)
	    {
	        newValue = oldValue;
	        System.err.println("Introduced value cannot be negative");
	    }
	}
	
	/**
	 * Check if the value is valid on an Utility node.
	 * <p>
	 * @param oldValue - previous value in the cell
	 * @param newValue - new value to validate
	 * @param row - the row for the cell
	 * @param col - the column for the cell
	 */
	public void checkUtilityValue (Object oldValue, Object newValue, int row, int col)
	{
	    Double value = 0.0;
	    try
	    {
	        if (newValue instanceof String)
	        {
	            value = Double.parseDouble ((String) newValue);
	        }
	        else if (newValue instanceof Double)
	        {
	            value = (Double) newValue;
	        }
	        this.setValueAt (value, row, col);
	    }
	    catch (Exception ex)
	    {
	        showNodePotentialTableErrorMsg ("Double conversion error");
	        this.setValueAt (oldValue, row, col);
	    }
	}
	
	/**
	 * Check if the value is valid on a deterministic model.
	 * <p>
	 * In this model, the summa of the values of the column is 1 and only one of
	 * the values is 1 and all others are zeros.
	 * @param oldValue - previous value in the cell
	 * @param newValue - new value to validate
	 * @param row - the row for the cell
	 * @param col - the column for the cell
	 */
	public void checkDeterministic (Object oldValue, Object newValue, int row, int col)
	{
	    Double value = 0.0;
	    try
	    {
	        if (newValue instanceof String)
	        {
	            value = Double.parseDouble ((String) newValue);
	        }
	        else if (newValue instanceof Double)
	        {
	            value = (Double) newValue;
	        }
	        checkZeroOrOneValues (value);
	        assignNewDeterministicValuesToColumn (oldValue, value, row, col);
	    }
	    catch (DeterministicValueNotAllowedException ex)
	    {
	        showNodePotentialTableErrorMsg ("NodePotentialTable.Msg.DeterministicValueNotAllowed");
	        this.setValueAt (oldValue, row, col);
	    }
	}
	
	/**
	 * Check if the value is valid on a probabilistic model
	 * <p>
	 * In this model, the sum of the values of the column is 1 but there are no
	 * restrictions to the individual values
	 * @param oldValue - previous value in the cell
	 * @param newValue - new value to validate
	 * @param row - the row for the cell
	 * @param col - the column for the cell
	 */
	public void checkProbabilistic (Object oldValue, Object newValue, int row, int col)
	{
	    Double value = 0.0;
	    try
	    {
	        if (newValue instanceof String)
	        {
	            value = Double.parseDouble ((String) newValue);
	        }
	        else if (newValue instanceof Double)
	        {
	            value = (Double) newValue;
	        }
	        checkValueBetweenZeroAndOneValues (value);
	    }
	    catch (ProbabilisticValueNotAllowedException ex)
	    {
	        showNodePotentialTableErrorMsg ("NodePotentialTable.Msg.ProbabilisticValueNotAllowed");
	        this.setValueAt (oldValue, row, col);
	    }
	}
	
	/**
	 * show a error window message to the user with a specific msg
	 * @param msg - the error message to show to user
	 */
	private void showNodePotentialTableErrorMsg (String msg)
	{
	    System.err.println(msg + ".Text");
	}
	
	/**
	 * Check if a value is equal to 0 or 1 values
	 * @param value - the value to check
	 * @return true if value is compliance with the condition
	 */
	private boolean checkZeroOrOneValues (double value)
	    throws DeterministicValueNotAllowedException
	{
	    boolean result = false;
	    if ((value == 1.0) || (value == 0.0))
	    {
	        result = true;
	    }
	    else
	    {
	        throw new DeterministicValueNotAllowedException ("");
	    }
	    return result;
	}
	
	/**
	 * Check if a value is between 0 and 1 values
	 * @param value - the value to check
	 * @return true if value is compliance with the condition
	 */
	private boolean checkValueBetweenZeroAndOneValues (double value)
	    throws ProbabilisticValueNotAllowedException
	{
	    boolean result = false;
	    if ((value >= 0.0) & (value <= 1.0))
	    {
	        result = true;
	    }
	    else
	    {
	        throw new ProbabilisticValueNotAllowedException ("");
	    }
	    return result;
	}
	
	/**
	 * assign new deterministic values to column with the following condition:
	 * <p>
	 * <ul>
	 * <li>if the new value is 1.0, then previous 1.0 cell is set to 0.0</li>
	 * <li>if the new value is 0.0, then previous cell is trying to be set to
	 * 1.0 except when it is in first editable row. In this case, next cell is
	 * set to 1.0</li>
	 * </ul>
	 * @param oldValue - previous value of the cell that is being edited
	 * @param value - new value of the cell that is being edited
	 * @param row - the row of the cell that is being edited
	 * @param col - the column where the values are stored for the edited cell
	 */
	private void assignNewDeterministicValuesToColumn (Object oldValue,
	                                                   Object value,
	                                                   int row,
	                                                   int col)
	{
	    int initialRow = 0;
	    if (((Double) value) == 1.0)
	    {
	        for (int i = initialRow; i < lastEditableRow; i++)
	        {
	            if (((Double) super.getValueAt (i, col)) == 1.0)
	            {
	                super.setValueAt (0.0, i, col);
	                super.setValueAt (value, row, col);
	                String stateValue = (String) super.getValueAt (row, 0);
	                super.setValueAt (stateValue, lastEditableRow, col);
	                break;
	            }
	        }
	    }
	    else if (((Double) value) == 0.0)
	    {
	        String stateValue = "";
	        if (row == initialRow)
	        {
	            if (row == lastEditableRow)
	            {
	                super.setValueAt (1.0, row, col);
	                stateValue = (String) super.getValueAt (row, 0);
	            }
	            else
	            {
	                super.setValueAt (1.0, row + 1, col);
	                stateValue = (String) super.getValueAt (row + 1, 0);
	            }
	        }
	        else
	        {
	            super.setValueAt (1.0, row - 1, col);
	            stateValue = (String) super.getValueAt (row - 1, 0);
	        }
	        super.setValueAt (stateValue, lastEditableRow + 1, col);
	        super.setValueAt (0.0, row, col);
	    }
	}
	
	/**
	 * AssignNewProbabilisticValuesToColumn(...) assign new probabilistic values
	 * to column by splitting the difference between the old and new value of
	 * the edited cell between the other not modified cells of the column, and
	 * ensuring that addition of all values in the column is equals to 1.
	 * <p>
	 * And setting the value of the edited cell to the new value.
	 * @param oldValue - previous value of the cell that is being edited
	 * @param value - new value of the cell that is being edited
	 * @param row - the row of the cell that is being edited
	 * @param col - the column where the values are stored for the edited cell
	 */
	private void assignNewProbabilisticValuesToColumn (Object oldValue,
	                                                   Object value,
	                                                   int row,
	                                                   int col,
	                                                   int numRowsToModify)
	{
	    int initialRow = 0;
	    double delta = 0.0;
	    double summa = 0.0;
	    double newSumma = 0.0;
	    double auxValue = 0.0;
	    // option 1. Delta equal distribution but only in not modified nodes
	    for (int i = initialRow; i < lastEditableRow; i++)
	    {
	        if (dataModified[i][col])
	        { // summarize all previous edited values
	            summa = roundingDouble (summa + (Double) super.getValueAt (i, col));
	        }
	    }
	    if (dataModified[row][col])
	    {
	        summa = roundingDouble (summa - (Double) oldValue);
	        numRowsToModify++;
	    }
	    newSumma = roundingDouble (summa + (Double) value);
	    if (newSumma > 1.0)
	    {
	        showNodePotentialTableErrorMsg ("NodePotentialTable.Msg.SummaOfProbabilitiesHigherThanOne");
	        super.setValueAt (oldValue, row, col);
	    }
	    else
	    { // probabilistic condition is valid
	        super.setValueAt (value, row, col);
	        summa = newSumma;
	        dataModified[row][col] = true;
	        numRowsToModify--;
	        if (numRowsToModify != 0)
	        {
	            delta = ((((Double) value).doubleValue () - ((Double) oldValue).doubleValue ()) / numRowsToModify);
	            delta = roundingDouble (delta); // only 2 decimals positions
	            for (int i = initialRow; i < lastEditableRow; i++)
	            {
	                if (i != row && !dataModified[i][col])
	                {
	                    // change and summarize
	                    auxValue = (Double) super.getValueAt (i, col);
	                    auxValue = roundingDouble (auxValue - delta);
	                    newSumma = roundingDouble (summa + auxValue);
	                    if (newSumma > 1.0)
	                    {
	                        // ensure summa is not greater than 1
	                        auxValue = roundingDouble (auxValue - (newSumma - 1.0));
	                    }
	                    summa = roundingDouble (summa + auxValue);
	                    super.setValueAt (auxValue, i, col);
	                }
	            }
	        }
	    }
	}
	
	/**
	 * @return the variable
	 */
	public Variable getVariable ()
	{
	    return node.getVariable();
	}
	
	/**
	 * @return the nodeType
	 */
	public NodeType getNodeType ()
	{
	    return nodeType;
	}
	
	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType (NodeType nodeType)
	{
	    this.nodeType = nodeType;
	}
	
	/**
	 * @return the lastEditableRow
	 */
	public int getLastEditableRow ()
	{
	    return lastEditableRow;
	}
	
	/**
	 * @param lastEditableRow the lastEditableRow to set
	 */
	public void setLastEditableRow (int lastEditableRow)
	{
	    this.lastEditableRow = lastEditableRow;
	}
	
	/**
	 * @return the usingGeneralPotential
	 */
	public boolean isUsingGeneralPotential ()
	{
	    return (indexPotential == 0 ? true : false);
	}
	
	/**
	 * @param usingGeneralPotential the usingGeneralPotential to set
	 */
	public void setUsingGeneralPotential (int indexPotential)
	{
	    this.indexPotential = indexPotential;
	    if (isUsingGeneralPotential ())
	    {// if indexPotential == 0
	        if ("leak".equals (getValueAt (0, getColumnCount () - 1)))
	        {
	            // previous model=Optimal
	            // remove the leakColumn
	            int index = getColumnCount () - 1;
	            // TableColumn column = getColumnModel ().getColumn (index);
	            // getColumnModel ().removeColumn (column);
	        }
	        else
	        {
	            // do nothing
	        }
	    }
	    else
	    { // canonical models
	        if ("leak".equals (getValueAt (0, getColumnCount () - 1)))
	        {
	            // previous model=Optimal
	            // do nothing
	        }
	        else
	        {
	            int rowCount = this.getRowCount ();
	            int firstRow = this.getFirstEditableRow ();
	            Object[] values = new Object[rowCount];
	            values[0] = "leak";
	            for (int i = 1; i < firstRow; i++)
	            {
	                values[i] = "-";
	            }
	            for (int i = firstRow; i < rowCount; i++)
	            {
	                values[i] = 0.0;
	            }
	            if (1 <= indexPotential & indexPotential <= 3)
	            {
	                // OR family
	                values[rowCount - 1] = 1.0;
	            }
	            else if (4 <= indexPotential & indexPotential <= 6)
	            {
	                // AND family
	                values[firstRow] = 1.0;
	            }
	            else
	            {
	                // error ????????
	            }
	            betterAddColumn ("leak", values);
	        }
	    }
	}
	
	/**
	 * @return the deterministic
	 */
	public boolean isDeterministic ()
	{
	    return deterministic;
	}
	
	/**
	 * Define the deterministic behaviour of the table
	 * @param deterministic the table behaviour as deterministic(true) or not
	 */
	public void setDeterministic (boolean deterministic)
	{
	    this.deterministic = deterministic;
	    if (isDeterministic ())
	    {
	        for (int k = FIRST_EDITABLE_COLUMN; k < this.getColumnCount (); k++)
	        {
	            double maxValue = -1;
	            int rowPosition = -1;
	            for (int i = this.getFirstEditableRow (); i < getLastEditableRow (); i++)
	            {
	                if (((Double) this.getValueAt (i, k)) > maxValue)
	                {
	                    maxValue = (Double) this.getValueAt (i, k);
	                    rowPosition = i;
	                }
	            }
	            for (int i = this.getFirstEditableRow (); i < getLastEditableRow (); i++)
	            {
	                if (i == rowPosition)
	                {
	                    this.setValueAt (1.0, i, k);
	                }
	                else
	                {
	                    this.setValueAt (0.0, i, k);
	                }
	            }
	        }
	        // and now, by default, show in the table, the name of the state
	        // but not the values of the cells
	        setShowingProbabilitiesValues (false);
	    }
	    else
	    {
	        setShowingProbabilitiesValues (true);
	    }
	}
	
	/**
	 * @return the showingAllParameters
	 */
	public boolean isShowingAllParameters ()
	{
	    return showingAllParameters;
	}
	
	/**
	 * Method to show/hide rows based on the showingAllParameters attribute
	 * using a RowFilter mechanism.
	 * <ul>
	 * <li>If true, the table is shown completely with probabilities values
	 * which means that there is no active row filter</li>
	 * <li>If not, the row filter is set to show all rows except the one that
	 * has the state name equals to the last state name.</li>
	 * </ul>
	 * @param showingAllParameters if true, show all; if false, show only
	 *            independent parameters
	 */
	public void setShowingAllParameters (boolean showingAllParameters)
	{
	    this.showingAllParameters = showingAllParameters;

	    if (isShowingAllParameters ())
	    {
	        if ((getVariable () != null) && (getVariable ().getName () != null)
	            && node.getNodeType () != NodeType.UTILITY)
	        {
	            String name = getVariable ().getName ();
	            if (getVariable ().getTimeSlice () != Integer.MIN_VALUE)
	            {
	                name = getRegExp (name);
	            }
	            if (name.contains ("(") || name.contains (")"))
	            {
	                name = getRegExpParenthesis (name);
	            }
	            if (name.contains ("+"))
	            {
	                name = name.replace ("+", "\\+");
	            }
	            if (name.contains ("?"))
	            {
	                name = name.replace ("?", "\\?");
	            }
	        }
	    }
	    else
	    {
	        int lastRow = this.getRowCount () - 1 - 1;
	        lastRow = (lastRow < 0 ? 0 : lastRow);
	    }
	}
	
	/**
	 * Gets the regular expression for the temporal node
	 * @param name the name of the node
	 * @return the regular expression of the name of node
	 */
	private String getRegExp (String name)
	{
	    int cont1 = name.indexOf ("[");
	    String s1 = name.substring (0, cont1);
	    int cont2 = name.indexOf ("]");
	    String s2 = name.substring (cont1, cont2);
	    String s3 = name.substring (cont2, name.length ());
	    return s1 + "\\" + s2 + "\\" + s3;
	}
	
	/**
	 * Gets the regular expression for node names with parenthesis
	 * @param name the name of the node
	 * @return the regular expression of the name of node
	 */
	private String getRegExpParenthesis (String name)
	{
	    if (name.contains ("("))
	    {
	        name = name.replace ("(", "\\(");
	    }
	    if (name.contains (")"))
	    {
	        name = name.replace (")", "\\)");
	    }
	    return name;
	}
	
	/**
	 * @return the showingProbabilitiesValues
	 */
	protected boolean isShowingProbabilitiesValues ()
	{
	    return showingProbabilitiesValues;
	}
	
	/**
	 * Method to show/hide rows based upon the showingProbabilitiesValues
	 * parameter. If showingProbabilitiesValues is true, table shows numerical
	 * values for all the configurations but if showingProbabilitiesValues is
	 * false, table shows the name of the state of the node corresponding to the
	 * maximum value in a deterministic model.
	 * <ul>
	 * <li>true = show probabilities</li>
	 * <li>false = show values</li>
	 * </ul>
	 * @param showingProbabilitiesValues the showingProbabilitiesValues to set
	 */
	public void setShowingProbabilitiesValues (boolean showingProbabilitiesValues)
	{
	    this.showingProbabilitiesValues = showingProbabilitiesValues;
	    
	    if (isShowingProbabilitiesValues ())
	    {
	        if ((getVariable () != null) && (getVariable ().getName () != null))
	        {
	            String name = getVariable ().getName ();
	        }
	        else
	        {
	        }
	    }
	    else
	    {
	    }
	}
	
	/**
	 * @return the showingTPCvalues
	 */
	protected boolean isShowingTPCvalues ()
	{
	    return showingTPCvalues;
	}
	
	/**
	 * Method to show/hide rows based upon th showingProbabilitiesValues
	 * attribute If showingProbabilities, table shows numerical values for all
	 * the configurations but if showingValues, table shows the name of the
	 * state of the node corresponding to the maximum value in a deterministic
	 * model
	 * @param showingProbabilitiesValues the showingProbabilitiesValues to set
	 */
	public void setShowingTPCvalues (boolean showingTPCvalues)
	{
	    this.showingTPCvalues = showingTPCvalues;
	    if (isShowingTPCvalues ())
	    {
	        System.out.println ("NodePotentialTable. Showing TPC values coming soon...");
	    }
	    else
	    {
	        System.out.println ("NodePotentialTable. Showing Canonical values coming soon...");
	    }
	}
	
	/**
	 * @return the showingOptimal
	 */
	public boolean isShowingOptimal ()
	{
	    return showingOptimal;
	}
	
	/**
	 * @param showingOptimal the showingOptimal to set
	 */
	public void setShowingOptimal (boolean showingOptimal)
	{
	    this.showingOptimal = showingOptimal;
	}
	
	/**
	 * Internal method to add a column without affecting the other columns in
	 * the table. If using directly addColumn() to the JTable will cause all
	 * columns will lost previous visual formats
	 * @param columnHeaderName - name of the column to be used
	 * @param values - values to be set in the column
	 */
	public void betterAddColumn (Object columnHeaderName, Object[] values)
	{
	    // col.setHeaderValue (columnHeaderName);
	    // model.addColumn (columnHeaderName.toString (), values);
	}
	
	/**
	 * set the number of columns in the table adding one more for the variable's
	 * states and adding one more for the id column (hidden)
	 * @param parents - parents of the variable
	 * @return the number of columns in the table
	 */
	public static int howManyColumns (Node properties)
	{
	    int numColumns = 0;
	    if (properties.getParents () != null)
	    {
	        int aux = 1;
	        for (Node parent : properties.getParents ())
	        {
	            State[] parentStates = parent.getVariable ().getStates ();
	            aux = aux * parentStates.length;
	        }
	        numColumns = aux;
	    }
	    else
	    {
	        numColumns = 1;
	    }
	    numColumns = FIRST_EDITABLE_COLUMN + numColumns;
	    return numColumns;
	}
	
	/**
	 * set a default id for the columns (Excel format)
	 */
	public static String[] getColumnsIdsSpreadSheetStyle (int howManyColumns)
	{
	    String[] columnsId = new String[howManyColumns];
	    String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    for (int columnPosition = 0; columnPosition < howManyColumns; columnPosition++)
	    {
	        String columnId = "";
	        int firstLetterPosition = columnPosition % 26;
	        int secondLetterPosition = columnPosition / 26 - 1;
	        if (columnPosition >= (26 * 27))
	        {
	        }
	        else if (columnPosition >= 26)
	        {
	            columnId = columnId
	                       + ALPHABET.substring (secondLetterPosition, secondLetterPosition + 1)
	                       + ALPHABET.substring (firstLetterPosition, firstLetterPosition + 1);
	        }
	        else
	        {
	            columnId = columnId
	                       + ALPHABET.substring (firstLetterPosition, firstLetterPosition + 1);
	        }
	        columnsId[columnPosition] = columnId;
	    }
	    return columnsId;
	}
	
	/**
	 * print the NodePotentialTable
	 */
	public void printTable ()
	{
	    System.out.println ("NodePotentialTable: ");
	    if (getVariable () != null)
	    {
	        System.out.println ("    variable = " + getVariable ().getName ());
	    }
	    else
	    {
	        System.out.println ("    variable = not defined yet");
	    }
	    System.out.println ("    lastEditableRow = " + lastEditableRow);
	    System.out.println ("    usingGeneralPotencial = " + isUsingGeneralPotential ());
	    System.out.println ("    deterministic = " + isDeterministic ());
	    System.out.println ("    showingAllParameters = " + isShowingAllParameters ());
	    System.out.println ("    showingProbabilitiesValues = " + isShowingProbabilitiesValues ());
	    System.out.println ("    showingTPCvalues = " + isShowingTPCvalues ());
	}
	
	/**
	 * @return the decimalPositions
	 */
	protected static int getDecimalPositions ()
	{
	    return decimalPositions;
	}
	
	/**
	 * @param decimalPositions the decimalPositions to set
	 */
	protected static void setDecimalPositions (int newDecimalPositions)
	{
	    decimalPositions = newDecimalPositions;
	}
	
	/**
	 * roundDouble takes a double number and returns a new double with a certain
	 * number of decimals positions, using rounding mechanism
	 * @param number - double number to be rounded
	 * @return double with only n-decimals positions
	 */
	private static final double roundingDouble (double number)
	{
	    double positions = Math.pow (10, (double) decimalPositions);
	    return Math.round (number * positions) / positions;
	}
	
	/**
	 * Sets node
	 * @param node
	 */
	public void setData (Node node)
	{
	    this.probNet = node.getProbNet();
	}    
}
