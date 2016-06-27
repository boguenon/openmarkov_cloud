/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package com.boguenon.service.modules.bayes.mode;

import org.openmarkov.core.model.network.ProbNet;

import com.boguenon.service.modules.bayes.editor.EditorPanel;
import com.boguenon.service.modules.bayes.editor.VisualNetwork;

/**
 * This class the defines the behaviour of the editor panel in a certain edition
 * state such as selection, node creation, link creation, etc.
 * @author ibermejo
 */
public abstract class EditionMode
{

    protected EditorPanel editorPanel;
    protected VisualNetwork visualNetwork;
    protected ProbNet probNet;
    
    
    public EditionMode(EditorPanel editorPanel, ProbNet probNet)
    {
        this.editorPanel = editorPanel;
        this.visualNetwork = editorPanel.getVisualNetwork ();
        this.probNet = probNet;
    }
}
