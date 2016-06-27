/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package com.boguenon.service.modules.bayes.mode;

import java.util.HashSet;
import java.util.List;


import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;

import com.boguenon.service.modules.bayes.editor.EditorPanel;

public abstract class NodeEditionMode extends EditionMode
{
    private NodeType nodeType;

    public NodeEditionMode (EditorPanel editorPanel, ProbNet probNet, NodeType nodeType)
    {
        super (editorPanel, probNet);
        this.nodeType = nodeType;
    }
}
