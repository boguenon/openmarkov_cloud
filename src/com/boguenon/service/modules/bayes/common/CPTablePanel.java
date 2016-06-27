package com.boguenon.service.modules.bayes.common;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNode;

public class CPTablePanel extends TablePotentialPanel 
{
    public CPTablePanel (Node node)
    {
        super (node);
    }

    /**
     * This method initializes valuesTable and defines that first two columns
     * are not selectable
     * @return a new values table.
     */
    @Override
    public ValuesTable getValuesTable ()
    {
        if (valuesTable == null)
        {
            valuesTable = new ValuesTable(node);
            // valuesTable.setName("PotentialsTablePanel.valuesTable");
        }
        return valuesTable;
    }
}
