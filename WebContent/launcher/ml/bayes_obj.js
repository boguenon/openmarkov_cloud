IG$/*mainapp*/._IB8r/*BayesBranchObject*/ = function(tnode) {
	if (tnode)
	{
		this.p1/*parseNode*/(tnode);
	}
}

IG$/*mainapp*/._IB8r/*BayesBranchObject*/.prototype = {
	p1/*parseNode*/: function(tnode) {
		var me = this,
			i, vnode, vnodes;
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "thresholds");
		if (vnode)
		{
			me.thresholds = [];
			
			vnodes = IG$/*mainapp*/._I26/*getChildNodes*/(vnode);
			
			for (i=0; i < vnodes.length; i++)
			{
				me.thresholds.push({
					value: IG$/*mainapp*/._I1b/*XGetAttr*/(vnodes[i], "value"),
					belongs_to: IG$/*mainapp*/._I1b/*XGetAttr*/(vnodes[i], "belongs_to")
				});
			}
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "states");
		if (vnode)
		{
			me.states = [];
			
			vnodes = IG$/*mainapp*/._I26/*getChildNodes*/(vnode);
			
			for (i=0; i < vnodes.length; i++)
			{
				me.states.push({
					name: IG$/*mainapp*/._I1b/*XGetAttr*/(vnodes[i], "name")
				});
			}
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "label");
		
		if (vnode)
		{
			me.label = IG$/*mainapp*/._I24/*getTextContent*/(vnode);
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "reference");
		
		if (vnode)
		{
			me.reference = IG$/*mainapp*/._I24/*getTextContent*/(vnode);
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "potential");
		
		if (vnode)
		{
			me.potential = new IG$/*mainapp*/._IB8p/*BayesPotentialObject*/(vnode); 
		}
	},
	p2/*toXMLString*/: function() {
		var me = this,
			r = "<branch>",
			i;
		
		if (me.thresholds && me.thresholds.length)
		{
			r += "<thresholds>";
			
			for (i=0; i < me.thresholds.length; i++)
			{
				r += "<threshold value='" + me.thresholds[i].value + "' belongs_to='" + me.thresholds[i].belongs_to + "'></threshold>";
			}
			
			r += "</thresholds>";
		}
		
		if (me.states && me.states.length)
		{
			r += "<states>";
			
			for (i=0; i < me.states.length; i++)
			{
				r += "<state name='" + me.states[i].name + "'></state>";
			}
			
			r += "</states>";
		}
		
		if (me.label)
		{
			r += "<label>" + me.label + "</label>";
		}
		
		if (me.reference)
		{
			r += "<reference>" + me.reference + "</reference>";
		}
		
		if (me.potential)
		{
			r += me.potential.p2/*toXMLString*/();
		}
		
		r += "</branch>";
		
		return r;
	}
}

IG$/*mainapp*/._IB8p/*BayesPotentialObject*/ = function(tnode) {
	if (tnode)
	{
		this.p1/*parseNode*/(tnode);
	}
}

IG$/*mainapp*/._IB8p/*BayesPotentialObject*/.prototype = {
	p1/*parseNode*/: function(tnode) {
		var me = this,
			i, vnode, vnodes,
			uval;
		
		me.type = IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "type");
		me.role = IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "role");
		
		me.variables = [];
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "variables");
		if (vnode)
		{
			vnodes = IG$/*mainapp*/._I26/*getChildNodes*/(vnode);
			
			for (i=0; i < vnodes.length; i++)
			{
				me.variables.push({
					name: IG$/*mainapp*/._I1b/*XGetAttr*/(vnodes[i], "name")
				});
			}
		}
		me.values = IG$/*mainapp*/._I1a/*getSubNodeText*/(tnode, "values");
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "uncertain_values");
		
		if (vnode)
		{
			me.uncertain_values = [];
			
			vnodes = IG$/*mainapp*/._I26/*getChildNodes*/(vnode);
			
			for (i=0; i < vnodes.length; i++)
			{
				uval = IG$/*mainapp*/._I1c/*XGetAttrProp*/(vnodes[i]);
				uval.value = IG$/*mainapp*/._I24/*getTextContent*/(vnodes[i]);
				
				me.uncertain_values.push(uval);
			}
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "utility_variable");
		
		if (vnode)
		{
			me.utility_variable = {
				name: IG$/*mainapp*/._I1b/*XGetAttr*/(vnode, "name")
			};
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "top_variable");
		
		if (vnode)
		{
			me.top_variable = {
				name: IG$/*mainapp*/._I1b/*XGetAttr*/(vnode, "name"),
				timeslice: IG$/*mainapp*/._I1b/*XGetAttr*/(vnode, "timeslice")
			};
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "branches");
		
		if (vnode)
		{
			me.branches = [];
			
			vnodes = IG$/*mainapp*/._I26/*getChildNodes*/(vnode);
			
			for (i=0; i < vnodes.length; i++)
			{
				me.branches.push(new IG$/*mainapp*/._IB8r/*BayesBranchObject*/(vnodes[i]));
			}
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "model");
		
		if (vnode)
		{
			me.model = IG$/*mainapp*/._I24/*getTextContent*/(vnode);
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "subpotentials");
		
		if (vnode)
		{
			me.subpotentials = [];
			
			vnodes = IG$/*mainapp*/._I26/*getChildNodes*/(vnode);
			
			for (i=0; i < vnodes.length; i++)
			{
				me.subpotentials.push(new IG$/*mainapp*/._IB8p/*BayesPotentialObject*/(vnodes[i]));
			}
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "time_variable");
		
		if (vnode)
		{
			me.time_variable = {
				name: IG$/*mainapp*/._I1b/*XGetAttr*/(vnode, "name"),
				timeslice: IG$/*mainapp*/._I1b/*XGetAttr*/(vnode, "timeslice")
			};
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "coefficients");
		
		if (vnode)
		{
			me.coefficients = IG$/*mainapp*/._I24/*getTextContent*/(vnode);
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "covariates");
		
		if (vnode)
		{
			me.covariates = [];
			
			vnodes = IG$/*mainapp*/._I26/*getChildNodes*/(vnode);
			
			for (i=0; i < vnodes.length; i++)
			{
				me.covariates.push(IG$/*mainapp*/._I24/*getTextContent*/(vnodes[i]));
			}
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "covariance_matrix");
		
		if (vnode)
		{
			me.covariance_matrix = IG$/*mainapp*/._I24/*getTextContent*/(vnode);
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "cholesky_decomposition");
		
		if (vnode)
		{
			me.cholesky_decomposition = IG$/*mainapp*/._I24/*getTextContent*/(vnode);
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "numeric_value");
		
		if (vnode)
		{
			me.numeric_value = IG$/*mainapp*/._I24/*getTextContent*/(vnode);
		}
		
		vnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "state");
		
		if (vnode)
		{
			me.state = IG$/*mainapp*/._I24/*getTextContent*/(vnode);
		}
	},
	
	p2/*toXMLString*/: function() {
		var me = this,
			r = "<potential type='" + (me.type || "") + "' role='" + (me.role || "") + "'>",
			i, 
			variables = me.variables,
			uncertain_values = me.uncertain_values;
		
		if (variables)
		{
			r += "<variables>";
			for (i=0; i < variables.length; i++)
			{
				r += "<variable name='" + variables[i].name + "'/>";
			}
			r += "</variables>";
		}
		r += "<values>" + (me.values || "") + "</values>";
		
		if (uncertain_values && uncertain_values.length)
		{
			r += "<uncertain_values>";
			
			for (i=0; i < uncertain_values.length; i++)
			{
				r += "<value " 
				  + (uncertain_values[i].distribution ? " distribution='" + uncertain_values[i].distribution + "'" : "")
				  + (uncertain_values[i].name ? " name='" + uncertain_values[i].name + "'" : "")
				  + ">" + (uncertain_values[i].value || "") + "</value>";
			}
			
			r += "</uncertain_values>";
		}
		
		if (me.utility_variable && me.utility_variable.name)
		{
			r += "<utility_variable name='" + me.utility_variable.name + "'></utility_variable>";
		}
		
		if (me.top_variable && me.top_variable.name)
		{
			r += "<top_variable name='" + me.top_variable.name + "'" + (me.top_variable.timeslice ? " timeslice='" + me.top_variable.timeslice + "'" : "") + "></top_variable>";
		}
		
		if (me.time_variable && me.time_variable.name)
		{
			r += "<time_variable name='" + me.time_variable.name + "'" + (me.time_variable.timeslice ? " timeslice='" + me.time_variable.timeslice + "'" : "") + "></time_variable>";
		}
		
		if (me.branches && me.branches.length)
		{
			r += "<branches>";
			
			for (i=0; i < me.branches.length; i++)
			{
				r += me.branches[i].p2/*toXMLString*/();
			}
			
			r += "</branches>";
		}
		
		if (me.subpotentials && me.subpotentials.length)
		{
			r += "<subpotentials>";
			
			for (i=0; i < me.subpotentials.length; i++)
			{
				r += me.subpotentials[i].p2/*toXMLString*/();
			}
			
			r += "</subpotentials>";
		}
		
		if (me.model)
		{
			r += "<model>" + me.model + "</model>";
		}
		
		if (me.coefficients)
		{
			r += "<coefficients>" + me.coefficients + "</coefficients>";
		}
		
		if (me.covariates && me.covariates.length)
		{
			r += "<covariates>";
			
			for (i=0; i < me.covariates.length; i++)
			{
				r += "<covariate>" + me.covariates[i] + "</covariates>";
			}
			
			r += "</covariates>";
		}
		
		if (me.covariance_matrix)
		{
			r += "<covariance_matrix>" + me.covariance_matrix + "</covariance_matrix>";
		}
		
		if (me.cholesky_decomposition)
		{
			r += "<cholesky_decomposition>" + me.cholesky_decomposition + "</cholesky_decomposition>";
		}
		
		if (me.numeric_value)
		{
			r += "<numeric_value>" + me.numeric_value + "</numeric_value>";
		}
		
		if (me.state)
		{
			r += "<state>" + me.state + "</state>";
		}
		
		r += "</potential>";
		return r;
	}
};

IG$/*mainapp*/._IB8b/*BayesObject*/ = function(xdoc, bf) {
	var me = this;
	me.bf = bf;
	
	me.nodes = [];
	me.links = [];
	me.potentials = [];
	
	xdoc && me.L1/*parseContent*/(xdoc);
}

IG$/*mainapp*/._IB8b/*BayesObject*/.prototype = {
	L1/*parseContent*/: function(xdoc) {
		var me = this,
			pnode,
			tnode, tnodes, i, j, pnodes, node, lnode, lnk,
			uid, snode, potential, mnode,
			instanceid,
			creterion;
		
		me.nodes = [];
		me.links = [];
		me.potentials = [];
		me.decision_criteria = [];
		
		pnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/item");
		
		if (pnode)
		{
			uid = IG$/*mainapp*/._I1b/*XGetAttr*/(pnode, "uid");
			
			if (uid)
			{
				me.item = IG$/*mainapp*/._I1c/*XGetAttrProp*/(pnode);
			}
			
			instanceid = IG$/*mainapp*/._I1b/*XGetAttr*/(pnode, "instanceid");
			
			if (instanceid)
			{
				me.instanceid = instanceid;
			}
			
			tnode = IG$/*mainapp*/._I19/*getSubNode*/(pnode, "probnet");
			me.additional_properties = [];
			
			if (tnode)
			{
				snode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "comment");
				
				if (snode)
				{
					me.comment = IG$/*mainapp*/._I24/*getTextContent*/(snode);
				}
				
				snode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "additional_properties");
				
				if (snode)
				{
					tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(snode);
					
					for (i=0; i < tnodes.length; i++)
					{
						me.additional_properties.push({
							name: IG$/*mainapp*/._I1b/*XGetAttr*/(tnodes[i], "name"),
							value: IG$/*mainapp*/._I1b/*XGetAttr*/(tnodes[i], "value")
						});
					}
				}
			}
			
			tnode = IG$/*mainapp*/._I19/*getSubNode*/(pnode, "nodes");
			if (tnode)
			{
				me.mtype = IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "type");
				me.mtypename = IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "typename");
				
				tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnode);
				
				for (i=0; i < tnodes.length; i++)
				{
					node = new IG$/*mainapp*/._baN/*bflowNode*/(tnodes[i]);
					node.w = 100;
					node.h = 28;
					node._cs = ["ba_base", "ba_" + node.shape];
					me.nodes.push(node);
				}
			}
			
			tnode = IG$/*mainapp*/._I19/*getSubNode*/(pnode, "links");
			if (tnode)
			{
				tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnode);
				for (i=0; i < tnodes.length; i++)
				{
					lnk = {};
					IG$/*mainapp*/._I1f/*XGetInfo*/(lnk, tnodes[i], "from;to;dashstyle;directed", "s");
					
					snode = IG$/*mainapp*/._I19/*getSubNode*/(tnodes[i], "potential");
					
					if (snode)
					{
						lnk.potential = new IG$/*mainapp*/._IB8p/*BayesPotentialObject*/(snode);
					}
					
					snode = IG$/*mainapp*/._I19/*getSubNode*/(tnodes[i], "revealing_condition");
					
					if (snode)
					{
						lnk.revealing_condition = {};
						
						mnode = IG$/*mainapp*/._I19/*getSubNode*/(snode, "states");
						
						if (mnode)
						{
							lnk.revealing_condition.states = [];
							
							snodes = IG$/*mainapp*/._I26/*getChildNodes*/(mnode);
							for (j=0; j < snodes.length; j++)
							{
								lnk.revealing_condition.states.push({
									name: IG$/*mainapp*/._I1b/*XGetAttr*/(snodes[j], "name")
								});
							}
						}
					}
					
					me.links.push(lnk);
				}
			}
			
			tnode = IG$/*mainapp*/._I19/*getSubNode*/(pnode, "potentials");
			if (tnode)
			{
				tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnode);
				for (i=0; i < tnodes.length; i++)
				{
					potential = new IG$/*mainapp*/._IB8p/*BayesPotentialObject*/(tnodes[i]);
					me.potentials.push(potential);
				}
			}
			
			tnode = IG$/*mainapp*/._I19/*getSubNode*/(pnode, "decision_criteria");
			if (tnode)
			{
				tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnode);
				for (i=0; i < tnodes.length; i++)
				{
					criterion = IG$/*mainapp*/._I1c/*XGetAttrProp*/(tnodes[i]);
					
					snode = IG$/*mainapp*/._I19/*getSubNode*/(tnodes[i], "additional_properties");
					
					if (snode)
					{
						snodes = IG$/*mainapp*/._I26/*getChildNodes*/(snode);
						for (j=0; j < snodes.length; j++)
						{
							criterion[IG$/*mainapp*/._I1b/*XGetAttr*/(snodes[j], "name")] = IG$/*mainapp*/._I1b/*XGetAttr*/(snodes[j], "value");
						}
					}
					
					me.decision_criteria.push(criterion);
				}
			}
		}
	},
	
	L2/*getContent*/: function() {
		var me = this,
			r = [],
			l,
			potentials = me.potentials,
			decision_criteria = me.decision_criteria;
		
		r.push("<smsg><item");
		
		me.item && r.push(IG$/*mainapp*/._I20/*XUpdateInfo*/(me.item, "uid;nodepath;name", "s"));
		r.push(">");
		
		r.push("<probnet>");
		
		if (me.comment)
		{
			r.push("<comment><![CDATA[" + me.comment + "]]></comment>");
		}
	
		l = me.additional_properties;
		
		if (l && l.length)
		{
			r.push("<additional_properties>");
			
			for (i=0; i < l.length; i++)
			{
				r.push("<property name='" + l[i].name + "' value='" + l[i].value + "'/>");
			}
			
			r.push("</additional_properties>");
		}
		
		r.push("</probnet>");
		
		r.push(me.bf.l2/*exportContent*/.call(me.bf, me.mtype, me.mtypename));
		
		if (potentials && potentials.length)
		{
			r.push("<potentials>");
			for (i=0; i < potentials.length; i++)
			{
				r.push(potentials[i].p2/*getXMLString*/());
			}
			r.push("</potentials>");
		}
		
		if (decision_criteria && decision_criteria.length)
		{
			r.push("<decision_criteria>");
			
			for (i=0; i < decision_criteria.length; i++)
			{
				r.push("<criterion name='" + decision_criteria[i].name + "'>");
				r.push("<additional_properties>");
				$.each(decision_criteria[i], function(m, val) {
					if (m && m != "name")
					{
						r.push("<property name='" + m + "' value='" + val + "'></property>");
					}
				});
				r.push("</additional_properties>");
				r.push("</criterion>");
			}
			
			r.push("</decision_criteria>");
		}
		
		r.push("</item></smsg>");
		
		return r.join("");
	}
}