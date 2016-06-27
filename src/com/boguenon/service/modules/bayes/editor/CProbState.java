package com.boguenon.service.modules.bayes.editor;

import org.w3c.dom.Node;

import com.boguenon.utility.XMLTransform;

public class CProbState {
	public String name;
	
	public CProbState(Node pnode) {
		this.name = XMLTransform.GetElementValue(pnode, "name");
	}

}
