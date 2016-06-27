/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package com.boguenon.service.modules.bayes.mode;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.model.network.ProbNet;

import com.boguenon.service.modules.bayes.editor.EditorPanel;

@EditionState(name="Edit.Mode.Link", icon="link.gif", cursor="link.gif")
public class LinkEditionMode extends EditionMode
{

    public LinkEditionMode (EditorPanel editorPanel, ProbNet probNet)
    {
        super (editorPanel, probNet);
    }
}
