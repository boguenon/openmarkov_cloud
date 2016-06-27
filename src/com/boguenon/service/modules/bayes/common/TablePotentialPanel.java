package com.boguenon.service.modules.bayes.common;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NullListPotentialsException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;

public class TablePotentialPanel extends ProbabilityTablePanel {

	protected ValuesTable valuesTable           = null;
    /**
     * Panel to scroll the table.
     */
    protected Node    node;
    protected boolean     hasLinkRestriction;

    /**
     * Constructor use by CPTablePanel
     * 
     * @param node
     */
    public TablePotentialPanel(Node node) {
        super();
        this.node = node;
        this.valuesTable = new ValuesTable(node);
        setData(node);
    }

    public void setData(Object[][] newData) {
        setData(newData, columns, 0, 0, NodeType.CHANCE);
    }

    /**
     * Sets a new table model with new data and new columns
     * 
     * @param newData
     *            new data for the table
     * @param newColumns
     *            new columns for the table
     */
    public void setData(Object[][] newData,
            String[] newColumns,
            int firstEditableRow,
            int lastEditableRow,
            NodeType nodeType) {
        data = newData.clone();
        columns = newColumns.clone();
        this.firstEditableRow = firstEditableRow;
        this.lastEditableRow = lastEditableRow;
        // valuesTable.setVariable(node.getPotentials().get( 0
        // ).getVariable( 0 ));
        valuesTable.setLastEditableRow(lastEditableRow);
        valuesTable.setShowingAllParameters(true);
        valuesTable.setNodeType(nodeType);
    }

    /**
     * Sets a new table model with new data and new columns based on three
     * items: <li>list of Potentials of the variable</li> <li>states of the
     * variable</li> <li>parents of the variable</li>
     * 
     * @param listPotentials
     *            - the list of potentials of the variable
     * @param variableName
     *            - name of the variable
     * @param variableStates
     *            - states of the variable
     * @param parents
     *            - parents of the variable
     */
    public void setData(Node node) {
        this.node = node;
        hasLinkRestriction = LinkRestrictionPotentialOperations.hasLinkRestriction(node);
        valuesTable.setData(node);
        Object[][] tableData = null;
        boolean[] uncertaintyInColumns = null;
        String[] newColumns = null;
        if (node.getPotentials() != null) {
            tableData = convertListPotentialsToTableFormat(node);
            newColumns = ValuesTable.getColumnsIdsSpreadSheetStyle(tableData[0].length);
            setFirstEditableRow(PotentialsTablePanelOperations.calculateFirstEditableRow(node.getPotentials(), node));
            setLastEditableRow(PotentialsTablePanelOperations.calculateLastEditableRow(node.getPotentials(), node));
            setData(tableData, newColumns, firstEditableRow, lastEditableRow, node.getNodeType());
            uncertaintyInColumns = getUncertaintyInColumns(node);
        } else {
            tableData = new Object[0][0];
            setFirstEditableRow(0);
            setData(tableData);
        }
    }

    private boolean[] getUncertaintyInColumns(Node node) {
        int size = this.columns.length; // valuesTable.getColumnCount();
        boolean[] uncertaintyInColumns = new boolean[size - 1];

        if (node.getPotentials().size() > 0) {

            TablePotential tablePotential = (TablePotential) node.getPotentials().get(0);
            for (int i = 1; i < size; i++) {
                boolean hasUncertainty = false;
                try {
                    EvidenceCase configuration = getConfiguration(tablePotential, i);
                    hasUncertainty = tablePotential.hasUncertainty(configuration);
                } catch (InvalidStateException | IncompatibleEvidenceException e) {
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }
                uncertaintyInColumns[i - 1] = hasUncertainty;
            }
        }
        return uncertaintyInColumns;
    }

    /**
     * calculate the number of rows of the table based on the type of the node,
     * the number of parents and the number of states of the variable
     * 
     * @param additionalProperties
     *            - node additionalProperties
     * @return the number of rows of this Potentials Table
     */
    protected int howManyRows(Node properties) {
        int numRows = 0;
        if (properties.getParents() != null) {
            numRows = properties.getParents().size();
        }
        if (properties.getNodeType() == NodeType.UTILITY) {
            numRows += 1;
        } else {
            if (properties.getVariable().getStates() != null) {
                numRows = numRows + properties.getVariable().getStates().length;
            }
        }
        return numRows;
    }

    /**
     * Set a blank data table
     * 
     * @param additionalProperties
     *            - to obtain the required number of rows and columns
     * @return the blank data table
     */
    private Object[][] setBlankTable(Node properties) {
        Object[][] blankTable = null;
        int numRows = howManyRows(properties);
        int numColumns = ValuesTable.howManyColumns(properties);
        blankTable = new Object[numRows][numColumns];
        // TODO seria mas practico hacer un potential y luego ejecutar
        // el resto del metodo pero esto funciona
        for (int i = 0; i < properties.getVariable().getStates().length; i++) {
        }
        return blankTable;
    }

    /**
     * to retrieve the ListPotentials corresponding to the data in the table
     * 
     * @return
     */
    public ArrayList<Potential> getListPotentialsFromData() {
        ArrayList<Potential> result = null;
        result = convertTableFormatToListPotentials(valuesTable);
        // setListPotentials(result);
        return result;
    }

    private TablePotential getThisPotential(List<Potential> listPotentials) {
        TablePotential aPotential = null;
        try {
            aPotential = ((TablePotential) listPotentials.get(0));
        } catch (Exception ex) {
            // ExceptionsHandler.handleException(
            // ex, "no Potential.get(0) !!!", false );
        	System.err.println("no Potential.get(0) !!!");
        }
        return aPotential;
    }

    /**
     * Prepare the table data from the <code>Potential</code>s and States.
     * <p>
     * If the Potential is null, then the information is taken from the
     * <code>NodeProperties</code>
     * 
     * @param listPotentials
     *            - potentials of the table
     * @param states
     *            - states of the variable of this node
     * @param parents
     *            - <code>NodeWrapper</code> list of the parents
     * @return the table data to be set
     */
    protected Object[][] convertListPotentialsToTableFormat(Node node) {
        Object[][] values = null;
        try {
            // mpal
            PotentialsTablePanelOperations.checkIfNoPotential(node.getPotentials());
            values = setValuesTableSize(values, node);
            values = setParentsNameInUpperLeftCornerArea(values, node);
            values = setParentsStatesInTopArea(values, node);
            values = setNodeStatesInLeftArea(values, node);
            values = setPotentialDataInCentreArea(values, node);
            if (node.getNodeType() != NodeType.UTILITY) {
                values = setVariableNameInLowerLeftCornerArea(values, node);
                values = setVariableStatesInBottomArea(values, node);
            }
            setPosition(setNumberOfPostions(node.getPotentials()));
        } catch (NullListPotentialsException ex) {
            values = setBlankTable(node);
        }
        return values;
    }

    /**
     * set values table size for the potential
     * 
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setValuesTableSize(Object[][] oldValues, Node node) {
        Object[][] values = oldValues;
        int numRows = 0;
        int numColumns = 1; // at least, there is one column for the node names
        int row = PotentialsTablePanelOperations.calculateFirstEditableRow(node.getPotentials(),
        		node);
        setBaseIndexForCoordinates(row);
        setFirstEditableRow(row);
        TablePotential tablePotential = getThisPotential(node.getPotentials());
        List<Variable> variablesBeforeReorder = tablePotential.getVariables();
        setVariables(variablesBeforeReorder);
        if (node.getNodeType() == NodeType.UTILITY) {
            setBaseIndexForCoordinates(row - 1);
            numRows = getVariables().size();
            setLastEditableRow(numRows - 1);
            // numRows++;
            if (tablePotential.getTableSize() == 0)
                numColumns++;
            else
                numColumns += tablePotential.getTableSize();
        } else {
            // number of states of the conditioned variable
            int numDimensions = tablePotential.getDimensions()[0];
            // parents + variableStates
            numRows = getVariables().size() - 1 + numDimensions;
            setLastEditableRow(numRows - 1);
            numRows = numRows + 1; // + 1 for variableValues (when used in show
                                   // as Values
            if (numDimensions == 0) {
                // do nothing??
            } else { // all table div by variable states
                numColumns = numColumns + (tablePotential.getTableSize() / numDimensions);
            }
        }
        // create the array of arrays
        values = new Object[numRows][numColumns];
        return values;
    }

    private void setVariables(List<Variable> variables) {
        // TODO update this statement, when constructor of this class with
        // potential as parameter is implemented
        if (node != null && node.getNodeType() == NodeType.UTILITY) {
            this.variables = new ArrayList<Variable>();
            this.variables.add(node.getVariable());
            for (Variable variable : variables)
                this.variables.add(variable);
        } else
            this.variables = variables;
    }

    /**
     * This methods fills the Upper Left corner of the table with the name of
     * the parents of the node
     * 
     * @param values
     *            - the table that is being modified
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setParentsNameInUpperLeftCornerArea(Object[][] oldValues, Node node) {
        Object[][] values = oldValues;
        List<Variable> parents = new ArrayList<Variable>();
        for (Variable variable : getVariables()) {
            if (!variable.getName().equals(node.getName())) {
                parents.add(variable);
            }
        }
        if ((parents != null) && (parents.size() > 0)) {
            for (int i = 0; i < parents.size(); i++) {
                values[i][0] = parents.get(parents.size()-i-1);
            }
        }
        return values;
    }

    /**
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setParentsStatesInTopArea(Object[][] oldValues, Node node) {
        Object[][] values = oldValues;
        int numColumns = (values.length == 0 ? 0 : values[0].length);
        TablePotential tablePotential = getThisPotential(node.getPotentials());
        List<Variable> variables = tablePotential.getVariables();
        int[] offsets = tablePotential.getOffsets();
        int numStates = node.getVariable().getNumStates();
        int numVariables = tablePotential.getNumVariables();
        int numParentVariables = (tablePotential.getUtilityVariable() != null) ? tablePotential.getNumVariables()
                : tablePotential.getNumVariables() - 1;
        for (int row = 0; row < numParentVariables; row++) {
            int variableIndex = numVariables - row - 1;
            int numRepetitions = offsets[variableIndex] / numStates;
            State[] states = variables.get(variableIndex).getStates();
            int column = 1;
            while (column < numColumns) {
                for (State state : states) {
                    for (int i = 0; i < numRepetitions; i++) {
                        values[row][column] = state.getName();
                        column++;
                    }
                }
            }
        }
        return values;
    }

    /**
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private int setNumberOfPostions(List<Potential> listPotentials) {
        int numPositions = 1;
        try {
            for (Variable variable : listPotentials.get(0).getVariables()) {
                numPositions = numPositions * variable.getNumStates();
            }
        } catch (NullPointerException exception) {
            numPositions = 0;
            System.err.println("not enough memory");
        }
        setPosition(numPositions);
        return numPositions;
    }

    /**
     * this method sets the first row with the values of the states of the node
     * (if it is a node chance) or the name of the variable of the node (if it
     * is a utility node)
     * 
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setNodeStatesInLeftArea(Object[][] oldValues, Node properties) {
        Object[][] values = oldValues;
        TablePotential tablePotential = (TablePotential) getThisPotential(properties.getPotentials());
        int row = getFirstEditableRow();
        if (properties.getNodeType() == NodeType.UTILITY) {
            values[row][0] = properties.getName();
        } else
        /* if (properties.getNodeType() == NodeType.CHANCE) */{
            // set first column values with the state names
            if (0 < tablePotential.getDimensions()[0]) {
                // int numOfTheState =
                // tablePotential.getVariable( 0 ).getNumStates() - 1;
                int length = values.length - 2;
                for (State state : tablePotential.getVariable(0).getStates()) {
                    values[length--][0] = state.getName();
                    // row++;
                    // numOfTheState--;
                }
            }
        }
        return values;
    }

    /**
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setPotentialDataInCentreArea(Object[][] oldValues, Node properties) {
        Object[][] values = oldValues;
        int position = 0;
        int numColumns = (values.length == 0 ? 0 : values[0].length);
        TablePotential tablePotential = (TablePotential) getThisPotential(properties.getPotentials());
        // rounding initial values
        double[] initialValues = tablePotential.getValues();
        double[] roundedValues = new double[initialValues.length];
        int maxDecimals = 10;
        double epsilon;
        epsilon = Math.pow(10, -(maxDecimals + 2));
        for (int i = 0; i < initialValues.length; i++) {
            roundedValues[i] = Util.roundAndReduce(initialValues[i], epsilon, maxDecimals);
        }
        tablePotential.setValues(roundedValues);
        int cont = getLastEditableRow();
        for (int j = 1; j <= numColumns - 1; j++) {
            for (int i = cont; i >= getFirstEditableRow(); i--, position++) {
                double value = tablePotential.getValues()[position];
                values[i][j] = value;
            }
        }
        return values;
    }

    /****
     * Calculates the position on the dataTable for a state combination
     * 
     * @param stateIndices
     *            - indexes of the states
     * @return an array containing the row at the first position and the column
     *         at the second position.
     */
    private int[] getRowAndColumnForStateCombination(int[] stateIndices, TablePotential potential) {
        int numStates = node.getVariable().getNumStates();
        int position = potential.getPosition(stateIndices);
        int column = (position / numStates) + 1;
        int row = getLastEditableRow() - (position % numStates);
        return new int[] { row, column };
    }

    /****
     * Calculates the positions of the table which are not editable due to a
     * link restriction. If the position is not editable it contains the value
     * 1, otherwise it contains a null value.
     * @param node 
     * 
     * @return a two dimensional array with the size of the table containing the
     *         information about the editable positions.
     */
    private Object[][] getNotEditablePositions(Node node) {
        Object[][] notEditablePositions = null;
        notEditablePositions = setValuesTableSize(notEditablePositions, node);
        if (node.getNodeType() == NodeType.CHANCE && hasLinkRestriction) {
            List<int[]> statesWithRestriction = LinkRestrictionPotentialOperations.getStateCombinationsWithLinkRestriction(node);
            TablePotential potential = (TablePotential) node.getPotentials().get(0);
            for (int[] state : statesWithRestriction) {
                // reorder the variables
                int[] reorderedState = new int[state.length];
                reorderedState[0] = state[0];
                for (int i = 1; i < state.length; i++) {
                    reorderedState[state.length - i] = state[i];
                }
                int[] position = getRowAndColumnForStateCombination(reorderedState, potential);
                int row = position[0];
                int column = position[1];
                notEditablePositions[row][column] = 1;
            }
        }
        
        boolean[] uncertaintyInColumns = getUncertaintyInColumns(node);
        for(int row=firstEditableRow; row< notEditablePositions.length; ++row)
        {
            for (int column = 1; column < notEditablePositions[0].length; ++column) {
                if(uncertaintyInColumns[column-1])
                {
                    notEditablePositions[row][column] = 1;
                }
            }
        }
        return notEditablePositions;
    }

    /**
     * In the lower left corner area, the last row is reserved in the model for
     * displaying the name of the variable
     * 
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setVariableNameInLowerLeftCornerArea(Object[][] oldValues,
            Node properties) {
        Object[][] values = oldValues;
        values[getLastEditableRow() + 1][0] = properties.getName();
        return values;
    }

    /**
     * In a discretize table model that shows only values (not probabilities),
     * this area will store the name of the state that is required to display
     * 
     * @param values
     *            - the table that is being modified
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setVariableStatesInBottomArea(Object[][] oldValues, Node properties) {
        Object[][] values = oldValues;
        int numColumns = (values.length == 0 ? 0 : values[0].length);
        TablePotential tablePotential = (TablePotential) getThisPotential(properties.getPotentials());
        State[] states = tablePotential.getVariable(0).getStates();
        double max;
        for (int j = numColumns - 1; j >= 1; j--) {
            max = (Double) values[getFirstEditableRow()][j];
            values[getLastEditableRow() + 1][j] = states[0].getName();
            for (int i = getFirstEditableRow() + 1; i <= getLastEditableRow(); i++) {
                if (((Double) values[i][j]) > max) {
                    max = (Double) values[i][j];
                    values[getLastEditableRow() + 1][j] = states[i - getFirstEditableRow()].getName();
                }
            }
        }
        return values;
    }

    /**
     * Convert the table with the data in a List of Potentials to be saved
     * 
     * @param valuesTable
     *            - the table with the data
     * @return a list of Potentials
     */
    private ArrayList<Potential> convertTableFormatToListPotentials(ValuesTable valuesTable) {
        ArrayList<Potential> listPotentials = new ArrayList<Potential>();
        if (getPosition() >= 0) { // it is not a Decision node
            double[] table = new double[getPosition()];
            TablePotential tablePotential = null;
            int position = 0;
            for (int j = valuesTable.getColumnCount() - 1; j > 0; j--) {
                for (int i = valuesTable.getLastEditableRow() - 1; i >= getFirstEditableRow(); i--, position++) {
                    table[position] = (Double) valuesTable.getValueAt(i, j);
                }
            }
            tablePotential = new TablePotential(getVariables(),
                    PotentialRole.CONDITIONAL_PROBABILITY,
                    table);
            listPotentials.add(tablePotential);
        }
        return listPotentials;
    }

    /**
     * This method generates the evidenceCase based on the column selected on
     * the <code>valuesTable</code> object.
     * 
     * @param tablePotential
     *            The TablePotential object edited
     * @param col
     *            The column selected. Never is 0 , because the column 0 is the
     *            states column
     * @return An evidence case object
     * @throws InvalidStateException
     * @throws IncompatibleEvidenceException
     */
    private EvidenceCase getConfiguration(TablePotential tablePotential, int col)
            throws InvalidStateException, IncompatibleEvidenceException {
        Variable variable = null;
        EvidenceCase evidence = new EvidenceCase();
        // configuration of all variables
        if (tablePotential.getPotentialRole() == PotentialRole.UTILITY 
                && tablePotential.getUtilityVariable() != null) {
            variable = tablePotential.getUtilityVariable();
            variables = tablePotential.getVariables();
        } else { 
            variable = tablePotential.getVariable(0);
            variables = tablePotential.getVariables();
            variables.remove(0);
        }
        int[] parentsConfiguration = new int[variables.size()];
        // Gets the start position of a reordered potential
        int startPosition = Util.toPositionOnPotentialReordered(variable.getNumStates()
                + variables.size()
                - 1,
                col,
                variable.getNumStates(),
                variables.size());
        // gets the configuration selected
        int[] configuration = tablePotential.getConfiguration(startPosition);
        // back to the original order of variables configuration
        // first value of configuration matches the value of the first variable
        // in inverse order because the potential visualization is in inverse
        // order
        int j = 0;
        int end = 0;
        if (variable == tablePotential.getUtilityVariable()) {
            end = -1;
        }
        for (int i = configuration.length - 1; i > end; i--) {
            parentsConfiguration[j++] = configuration[i];
        }
        // Gets the evidence
        j = 0;
        Finding finding;
        for (Variable var : variables) {
            finding = new Finding(var, parentsConfiguration[j]);
            evidence.addFinding(finding);
            j++;
        }
        return evidence;
    }

    public EvidenceCase getEvidenceCaseFromSelectedColumn() {
        EvidenceCase evi = null;
        try {
            evi = getConfiguration((TablePotential) node.getPotentials().get(0), selectedColumn);
        } catch (InvalidStateException | IncompatibleEvidenceException e) {
            e.printStackTrace();
        }
        return evi;
    }

    /**
     * This method initializes valuesTable and defines that first two columns
     * are not selectable
     * 
     * @return a new values table.
     */
    public ValuesTable getValuesTable() {
        if (valuesTable == null) {
            valuesTable = new ValuesTable(node);
            valuesTable.setName("PotentialsTablePanel.valuesTable");
        }
        return valuesTable;
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * deterministic
     */
    public void setDeterministicModel() {
        valuesTable.setDeterministic(true);
        setShowAllParameters(true);
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * probabilistic
     */
    public void setProbabilisticModel() {
        valuesTable.setDeterministic(false);
        setShowAllParameters(true);
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * optimal (decision node)
     */
    public void setOptimalModel() {
        valuesTable.setShowingOptimal(true);
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * general (TablePotential)
     */
    public void setGeneralModel(int familyIndex) {
        valuesTable.setUsingGeneralPotential(familyIndex);
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * canonical (ICIPotential)
     */
    public void setCanonicalModel(int familyIndex) {
        valuesTable.setUsingGeneralPotential(familyIndex);
    }

    /**
     * @param showAllParameters
     *            the showAllParameters to set
     */
    public void setShowAllParameters(boolean showAllParameters) {
        this.showAllParameters = showAllParameters;
        valuesTable.setShowingAllParameters(showAllParameters);
    }

    /**
     * @param showProbabilitiesValues
     *            the showProbabilitiesValues to set
     */
    public void setShowProbabilitiesValues(boolean showProbabilitiesValues) {
        this.showProbabilitiesValues = showProbabilitiesValues;
        valuesTable.setShowingProbabilitiesValues(showProbabilitiesValues);
    }

    /**
     * @param showTPCvalues
     *            the showTPCvalues to set
     */
    public void setShowTPCvalues(boolean showTPCvalues) {
        this.showTPCvalues = showTPCvalues;
        valuesTable.setShowingTPCvalues(showTPCvalues);
    }
    
    public String getXMLData() {
    	StringBuilder sb = new StringBuilder();
    	
    	if (this.data != null)
    	{
    		String ucols = "";
    		
    		boolean[] uncertaintyInColumns = getUncertaintyInColumns(node);
    		
    		for (int i=0; i < uncertaintyInColumns.length; i++)
    		{
    			if (uncertaintyInColumns[i] == true)
    			{
    				ucols = ucols.length() == 0 ? "" + i : ucols + "," + i;
    			}
    		}
    		
	    	sb.append("<table_data rows='" + this.data.length + "' cols='" + (this.data.length > 0 ? this.data[0].length : 0) + "' hc='" + firstEditableRow + "' uncertain_columns='" + ucols + "'>");
	    	
	    	for (int i=0; i < this.data.length; i++)
	    	{
	    		String row = "<row seq='" + i + "'><![CDATA[";
	    		
	    		for (int j=0; j < this.data[i].length; j++)
	    		{
	    			row += (j == 0) ? this.data[i][j] : "\t" + this.data[i][j];
	    		}
	    		
	    		row += "]]></row>";
	    		
	    		sb.append(row);
	    	}
	    	
	    	sb.append("</table_data>");
    	}
    	
    	return sb.toString();
    }
}