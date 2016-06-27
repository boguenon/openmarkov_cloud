package com.boguenon.service.modules.bayes.common;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;

public abstract class ProbabilityTablePanel extends PotentialPanel  {
    protected String[]              columns                               = null;

    /**
     * Data of the cells.
     */
    protected Object[][]            data                                  = null;
    /**
     * number of positions in this table
     */
    protected int                   position                              = -1;
    /**
     * list of variables that are shown in this table
     */
    protected List<Variable>        variables                             = null;

    /**
     * list of potentials for the variable
     */
    protected List<Potential>       listPotentials                        = null;

    /**
     * first editable row (only for temporal storage)
     */
    protected int                   firstEditableRow                      = -1;
    /**
     * last editable row (only for temporal storage)
     */
    protected int                   lastEditableRow                       = -1;

    /**
     * base index for coordinates in the table
     */
    private int                     baseIndexForCoordinates               = -1;

    /**
     * Properties for options to display in the table
     */
    protected boolean               showAllParameters                     = true;
    protected boolean               showProbabilitiesValues               = true;
    protected boolean               showTPCvalues                         = true;
    protected boolean               showNetValues                         = true;


    protected EvidenceCase          evidenceCase;
    /**
     * index of the column selected in valuesTable
     */
    protected int                   selectedColumn                        = -1;

    /**
     * this is a default constructor with no construction parameters
     * 
     * @wbp.parser.constructor
     */
    public ProbabilityTablePanel() {
        this(new String[] { "id", "states", "values" }, new Object[][] { new Object[] { 0, null, 0 } }); // default init
    }

    /**
     * This is the default constructor
     * 
     * @param newColumns
     *            array of texts that appear in the header of the columns.
     * @param newData
     *            content of the cells.
     */
    public ProbabilityTablePanel(String[] newColumns, Object[][] newData) {
        columns = newColumns.clone();
        data = newData.clone();
    }

    /**
     * @return the showAllParameters
     */
    public boolean isShowAllParameters() {

        return showAllParameters;
    }

    /**
     * @return the showProbabilitiesValues
     */
    public boolean isShowProbabilitiesValues() {

        return showProbabilitiesValues;
    }

    /**
     * @return the showTPCvalues
     */
    public boolean isShowTPCvalues() {

        return showTPCvalues;
    }

    /**
     * @return the showNetValues
     */
    public boolean isShowNetValues() {

        return showNetValues;
    }

    /**
     * @param showNetValues
     *            the showNetValues to set
     */
    public void setShowNetValues(boolean showNetValues) {

        this.showNetValues = showNetValues;
        if (isShowNetValues()) {
            // show Net values
        } else {
            // show compound values
        }
    }

    /**
     * sets the first row for edition
     * 
     * @param firstRow
     *            - the first row that is available for edition
     */
    protected void setFirstEditableRow(int firstEditableRow) {

        this.firstEditableRow = firstEditableRow;

    }

    /**
     * gets the first row on edition
     * 
     * @return first row for edition
     */
    protected int getFirstEditableRow() {

        return this.firstEditableRow;
    }

    /**
     * @return the lastEditableRow
     */
    protected int getLastEditableRow() {

        return lastEditableRow;
    }

    /**
     * @param lastEditableRow
     *            the lastEditableRow to set
     */
    protected void setLastEditableRow(int lastEditableRow) {

        this.lastEditableRow = lastEditableRow;
    }

    /**
     * @return the position
     */
    protected int getPosition() {

        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    protected void setPosition(int position) {

        this.position = position;
    }

    /**
     * @return the variables
     */
    protected List<Variable> getVariables() {

        return variables;
    }

    /**
     * @param variables
     *            the variables to set
     */

    /**
     * @param listPotentials
     *            the listPotentials to set
     */
    public void setListPotentials(ArrayList<Potential> listPotentials) {

        this.listPotentials = listPotentials;
    }

    /**
     * Set the Base index for the coordinates in the table related to the
     * Potential of the variable of this node
     * 
     * @param value
     *            - the new base index for coordinates in the table
     */
    protected void setBaseIndexForCoordinates(int value) {
        this.baseIndexForCoordinates = value;
    }

    public void addParent(Variable parent) {

    }

    public void deleteParent(Variable parent) {

    }

    public void addState(String state) {

    }

    public void deleteState(String state) {

    }

    public Object[][] getData() {
        return this.data;
    }
    
    public String getXMLData() {
    	StringBuilder sb = new StringBuilder();
    	
    	if (this.data != null)
    	{
	    	sb.append("<table_data rows='" + this.data.length + "' cols='" + (this.data.length > 0 ? this.data[0].length : 0) + "' hc='" + firstEditableRow + "'>");
	    	
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
