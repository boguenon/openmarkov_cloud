IG$/*mainapp*/.BY_1/*ByayesChance*/ = Ext.extend(IG$/*mainapp*/.BC$base/*hadoop_dlg_base*/, {
	
	region:"center",
	"layout": "fit",
	
	autoHeight: true,
		
	callback: null,
	
	hide_ierr: 1,
	s_hist: 0,
	
	_IFe/*initF*/: function() {
		var me = this,
			job = me.job,
			tdata = ig$/*appoption*/.b$Am,
			variable_types = tdata.variable_types,
			purpose_types = tdata.purpose_types,
			potential_types = tdata.potential_types,
			_p/*editor*/ = me._p/*editor*/,
			itemobj = _p/*editor*/.itemobj,
			instanceid = itemobj.instanceid,
			req;
		
		// declare control to variable
		
		me.down("[name=variable_type]").store.loadData(variable_types || []);
		me.down("[name=purpose]").store.loadData(purpose_types || []);
		me.down("[name=potential_type]").store.loadData(potential_types || []);
		
		me.callParent(arguments);
		
		me._instanceid = instanceid;
			
		if (job && instanceid)
		{
			req = new IG$/*mainapp*/._I3e/*requestServer*/();
			
			me.setLoading(true);
			
			req.init(me, 
				{
		            ack: "77",
		            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({instanceid: instanceid, action: "get_property"}, "action;instanceid"),
		        	mbody: IG$/*mainapp*/._I2e/*getItemOption*/({option: "node", name: job.text}, "option;name")
		        }, me, function(xdoc) {
		        	var me = this,
		        		rnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/item"),
		        		tnode,
		        		p, c, dnode, dnodes, data, header,
		        		potential_type,
		        		p_data,
		        		i, row, crow, cols, rows,
		        		j, store, columns, fields, h, column,
		        		g_p1 = me.down("[name=g_p1]"),
		        		uncertain_columns, ucols;
		        	
		        	if (rnode)
		        	{
		        		p = IG$/*mainapp*/._I1c/*XGetAttrProp*/(rnode);
		        		me.down("[name=dsname]").setValue(p.name);
		        		
		        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "node_definition");
		        		
		        		if (tnode)
	        			{
		        			p = IG$/*mainapp*/._I1c/*XGetAttrProp*/(tnode);
		        			me.down("[name=relavance]").setValue(p.relavance);
		        			me.down("[name=purpose]").setValue(p.purpose);
		        			
		        			c = IG$/*mainapp*/._I1a/*getSubNodeText*/(tnode, "comment");
		        			me.down("[name=dsdesc]").setValue(c);
		        			
		        			c = IG$/*mainapp*/._I1a/*getSubNodeText*/(tnode, "decision");
		        			me.down("[name=decision]").setValue(c);
	        			}
		        		
		        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "domain_values_table");
		        		
		        		data = [];
		        		
		        		if (tnode)
		        		{
		        			// precision, unit, variable_type
		        			p = IG$/*mainapp*/._I1c/*XGetAttrProp*/(tnode);
		        			me.down("[name=variable_type]").setValue(p.variable_type);
		        			
		        			dnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "states");
		        			
		        			data = me.g1/*getTableData*/(dnode, "name".split(";"))
		        		}
		        		
		        		me.down("[name=domain_table]").store.loadData(data);
		        		
		        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "node_parents");
		        		
	        			data = me.g1/*getTableData*/(tnode, "key;name".split(";"));
		        		me.down("[name=node_parent]").store.loadData(data);
		        		
		        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "probability");
		        		
		        		if (tnode)
		        		{
		        			potential_type = IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "potential_type");
		        			me.down("[name=potential_type]").setValue(potential_type);
		        			
		        			switch(potential_type)
		        			{
		        			case "TABLE":
		        				fields = [];
		        				p_data = [];
		        				columns = [];
		        				
		        				dnode = IG$/*mainapp*/._I18/*XGetNode*/(tnode, "table_data");
		        				
		        				if (dnode)
	        					{
		        					cols = Number(IG$/*mainapp*/._I1b/*XGetAttr*/(dnode, "cols"));
		        					rows = Number(IG$/*mainapp*/._I1b/*XGetAttr*/(dnode, "rows"));
		        					h = Number(IG$/*mainapp*/._I1b/*XGetAttr*/(dnode, "hc"));
		        					uncertain_columns = IG$/*mainapp*/._I1b/*XGetAttr*/(dnode, "uncertain_columns");
		        					
		        					uncertain_columns = uncertain_columns ? uncertain_columns.split(",") : [];
		        					
		        					ucols = {};
		        					
		        					for (i=0; i < uncertain_columns.length; i++)
	        						{
		        						ucols[Number(uncertain_columns[i])] = 1;
	        						}
		        					
		        					dnodes = IG$/*mainapp*/._I26/*getChildNodes*/(dnode);
		        					
		        					for (i=0; i < dnodes.length; i++)
		        					{
		        						row = IG$/*mainapp*/._I24/*getTextContent*/(dnodes[i]).split("\t");
		        						
		        						if (i < h)
		        						{
	        								for (j=0; j < row.length; j++)
	        								{
	        									if (i == 0)
	        									{
	        										column = {
	        											xtype: "gridcolumn",
	        											width: 150,
	        											header: row[j],
	        											dataIndex: "c" + j,
	        											menuDisabled: true
	        										};
	        										
	        										column._lastcolumn = column;
	        										
	        										columns.push(column);
	        										fields.push("c" + j);
	        									}
	        									else
	        									{
	        										delete columns[j]._lastcolumn["dataIndex"];
	        										delete columns[j]._lastcolumn["width"];
	        										columns[j]._lastcolumn.columns = columns[j]._lastcolumn.columns || [];
	        										
	        										column = {
	        											header: row[j],
	        											dataIndex: "c" + j,
	        											menuDisabled: true
	        										};
	        										
	        										columns[j]._lastcolumn.columns.push(column);
	        										columns[j]._lastcolumn = column;
	        									}
	        								}
		        						}
		        						else
		        						{
		        							crow = {};
			        						for (j=0; j < row.length; j++)
			        						{
			        							crow["c" + j] = row[j];
			        						}
			        						
		        							p_data.push(crow);
		        						}
		        					}
		        					
		        					if (columns.length)
			        				{
		        						store = Ext.create("Ext.data.ArrayStore", {
		        							fields: fields
		        						});
		        						g_p1.reconfigure(store, columns);
				        				g_p1.store.loadData(p_data);
			        				}
	        					}
		        				break;
		        			}
		        		}
		        	}
		        }, false);
			req._l/*request*/();
		}
	},
	
	g1/*getTableData*/: function(dnode, dnames) {
		var i, j,
			data = [],
			dnodes, r, rd;
		
		if (dnode)
		{
			dnodes = IG$/*mainapp*/._I26/*getChildNodes*/(dnode);
			
			for (i=0; i < dnodes.length; i++)
			{
				r = IG$/*mainapp*/._I24/*getTextContent*/(dnodes[i]).split("\t");
				rd = {};
				for (j=0; j < dnames.length; j++)
				{
					rd[dnames[j]] = r[j];
				}
				data.push(rd);
			}
		}
		
		return data;
	},
	
	_IFf/*confirmDialog*/: function() {
		var me = this,
			job = me.job;
		
		if (job)
		{
			
		}
		
		me.callParent(arguments);
	},
	
	_IG0/*closeDlgProc*/: function() {
		var me = this,
			job = me.job,
			req,
			mbody = "<smsg><item name='" + job.text + "' cname='" + job.dsname + "'>",
			variable_type = me.down("[name=variable_type]").getValue();
			
		mbody += "<node_definition relavance='" + me.down("[name=relavance]").getValue() + "' purpose='" + me.down("[name=purpose]").getValue() + "'>"
		mbody += "<comment><![CDATA[" + (job.dsdesc || "") + "]]></comment>";
		mbody += "<decision><![CDATA[" + (me.down("[name=decision]").getValue() || "") + "]]></decision>";
		mbody += "</node_definition>";
		
		mbody += "<domain_values_table precision='" + me.down("[name=precision]").getValue() + "' unit='" + me.down("[name=unit]").getValue() + "' variable_type='" + variable_type + "'>";
		switch (variable_type)
		{
		case "finitStates":
			break;
		case "numeric":
		case "descritized":
			break;
		}
		mbody += "</domain_values_table>";
		
		mbody += "</item></smsg>";
		
		req = new IG$/*mainapp*/._I3e/*requestServer*/();
		
		req.init(me, 
			{
	            ack: "77",
	            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({instanceid: me._instanceid, action: "set_property", target: "node"}, "action;instanceid;target"),
	        	mbody: mbody
	        }, me, function(xdoc) {
	        	me.callback && me.callback.execute(me.job);
	        	me.fireEvent("close_dlg", me);
	        }
	    );
		
		req._l/*request*/();
	},
	
	_v1/*variable_type_changed*/: function(tval) {
		var me = this,
			domain_table = me.down("[name=domain_table]"),
			state_num = me.down("[name=state_num]");
		
		domain_table.hide();
		state_num.hide();
		
		switch (tval)
		{
		case "finitStates":
			domain_table.show();
			break;
		case "descritized":
		case "numeric":
			state_num.show();
			break;
		}
	},
	
	initComponent : function() {
		var panel = this,
			lw = 120,
			__m/*mode*/ = panel.__m/*mode*/;
			
		panel._job_name = IRm$/*resources*/.r1("T_BA_1");
		
		panel.moption = [
			{
				xtype: "fieldset",
				title: "Definition",
				layout: "anchor",
				defaults: {
					anchor: "100%"
				},
				items: [
					{
						xtype: "fieldcontainer",
						layout: "hbox",
						items: [
							{
								xtype: "combobox",
								fieldLabel: "Purpose",
								name: "purpose",
								labelWidth: 60,
								width: 180,
								queryMode: 'local',
								displayField: 'name',
								valueField: 'value',
								editable: false,
								autoSelect: true,
								store: {
									xtype: 'store',
									fields: [
										"name", "value"
									]
								}
							},
							{
								xtype: "numberfield",
								labelWidth: 50,
								width: 160,
								name: "relavance",
								fieldLabel: "Relevance"
							}
						]
					},
					{
						xtype: "combobox",
						fieldLabel: "Decision criterion",
						name: "decision",
						queryMode: 'local',
						displayField: 'name',
						valueField: 'value',
						editable: false,
						autoSelect: true,
						store: {
							xtype: 'store',
							fields: [
								"name", "value"
							]
						}
					}
				]
			},
			{
				xtype: "fieldset",
				title: "Domain",
				layout: "anchor",
				items: [
					{
						xtype: "combobox",
						fieldLabel: "Variable type",
						name: "variable_type",
						queryMode: 'local',
						displayField: 'name',
						valueField: 'value',
						editable: false,
						autoSelect: true,
						store: {
							xtype: 'store',
							fields: [
								"name", "value"
							]
						},
						listeners: {
							change: function(tobj) {
								this._v1/*variable_type_changed*/(tobj.getValue());
							},
							scope: this
						}
					},
					{
						xtype: "gridpanel",
						hideHeaders: true,
						name: "domain_table",
						hidden: true,
						height: 150,
						tbar: [
							{
								text: "Stanard domains"
							},
							{
								text: "Add"
							},
							{
								text: "Delete"
							},
							{
								text: "Up"
							},
							{
								text: "Down"
							}
						],
						store: {
							fields: ["name"]
						},
						columns: [
							{
								text: "Name",
								flex: 1,
								dataIndex: "name"
							}
						]
					},
					{
						xtype: "container",
						layout: "anchor",
						hidden: true,
						name: "state_num",
						items: [
							{
								xtype: "fieldcontainer",
								fieldLabel: "Precision",
								layout: "hbox",
								items: [
									{
										xtype: "numberfield",
										name: "precision",
										width: 60
									},
									{
										xtype: "textfield",
										fieldLabel: "Unit",
										name: "unit",
										labelAlign: "right",
										labelWidth: 60,
										width: 120
									}
								]
							},
							{
								xtype: "gridpanel",
								hideHeaders: true,
								height: 140,
								tbar: [
									{
										text: "Add"
									},
									{
										text: "Delete"
									}
								],
								columns: [
									{
										text: "Name",
										flex: 1
									},
									{
										text: "Lower",
										width: 25
									},
									{
										text: "Value",
										width: 50
									},
									{
										text: "Higher",
										width: 25
									},
									{
										text: "Value",
										width: 50
									}
								]
							}
						]
					}
				]
			},
			{
				xtype: "fieldset",
				title: "Parents",
				layout: "anchor",
				items: [
					{
						xtype: "gridpanel",
						hideHeaders: true,
						name: "node_parent",
						height: 100,
						tbar: [
							{
								text: "Add"
							},
							{
								text: "Delete"
							}
						],
						store: {
							fields: ["name"]
						},
						columns: [
							{
								text: "Name",
								dataIndex: "name",
								flex: 1
							}
						]
					}
				]
			},
			{
				xtype: "fieldset",
				layout: "anchor",
				title: "Other properties",
				tbar: [
					{
						text: "Add"
					},
					{
						text: "Delete"
					},
					{
						text: "Up"
					},
					{
						text: "Down"
					}
				]
			}
		];
		
		IG$/*mainapp*/.BY_1/*ByayesChance*/.superclass.initComponent.apply(this, arguments);
	}
});