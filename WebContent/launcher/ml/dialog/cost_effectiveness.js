IG$/*mainapp*/.bA_4/*cost_effectiveness*/ = Ext.extend(Ext.Window, {
	modal: true,
	isWindow: true,
	region:"center",
	"layout": {
		type: "fit",
		align: "stretch"
	},
	
	closable: false,
	resizable:false,
	width: 600,
	height: 550,
	
	callback: null,
	atype: 0, // 0: sensitivity analysis, 1: determistic analysis
	
	_IFe/*initF*/: function() {
		var me = this,
			params = ig$/*appoption*/.b$Am,
			deterministic_axis_variation_type = params.deterministic_axis_variation_type,
			p3 = me.down("[name=p3]");
		
		p3.store.loadData(params.analysis_types);
		me.down("[name=p4]").store.loadData(deterministic_axis_variation_type);
		me.down("[name=p5]").store.loadData(deterministic_axis_variation_type);
		me.down("[name=p6]").store.loadData(params.scope_types);
		
		p3.setValue("TORNADO_SPIDER");
	},
	
	_IFf/*confirmDialog*/: function() {
		var me = this,
			itemobj = me.itemobj,
			atype = me.atype,
			mreq = new IG$/*mainapp*/._I3e/*requestServer*/(),
			horizontalAxisParam = me.down("[name=p4]"),
			mbody = ["<smsg><info"];
		
		mbody.push(" analysis_type='" + me.down("[name=p3]").getValue() + "'");
		if (atype == 1)
		{
			mbody.push(" scope_type='" + me.down("[name=pb]").getValue() + "'");
			mbody.push(" decision='" + me.down("[name=pc]").getValue() + "'");
		}
		else
		{
			mbody.push(" scope_type='" + me.down("[name=p6]").getValue() + "'");
			mbody.push(" decision='" + me.down("[name=p7]").getValue() + "'");
		}
		
		mbody.push(">");
		
		mbody.push("<axis>");
		mbody.push("<horizontal_axis_parameter axitype='" + horizontalAxisParam.getValue() + "'>");
		mbody.push("</horizontal_axis_parameter>");
		mbody.push("</axis>");
		
		mbody.push("</info></smsg>");
		
		mreq.init(me, 
			{
				ack: "77",
				payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid, instanceid: itemobj.instanceid, action: "cost_effectiveness", method: (atype == 0 ? "sensitivity" : "determistic")}, "uid;action;instanceid;method"),
				mbody: mbody.join("") 
			}, me, function(xdoc) {
				var dlg = new IG$/*mainapp*/.bA_5/*tornado_spider*/({
					result_xml: xdoc
				});
				dlg.show();
	        }
		); 
		mreq._l/*request*/();
	},
	
	s1/*setAnalysisType*/: function(t) {
		var me = this,
			p1 = me.down("[name=p1]"),
			p6 = me.down("[name=p6]"),
			horizontalAxisParam = me.down("[name=p4]"),
			haxis = "POPP";
			decision = me.down("[name=p7]"),
			p1v = false,
			decisionV = false;
		
		switch (t)
		{
		case "TORNADO_SPIDER":
			p6.setValue("GLOBAL");
			break;
		case "MAP":
			p1v = true;
			decisionV = true;
			break;
		case "PLOT":
			decisionV = true;
			break;
		}
		p1.setVisible(p1v);
		decision.setVisible(decisionV);
		horizontalAxisParam.setValue(haxis);
	},
			
	initComponent : function() {
		var panel = this;
		
		panel.title = IRm$/*resources*/.r1("T_COST_EFF");
		
		// this.datagrid = Ext.create("Ext.grid.Panel", );
		
		// this._IH1/*mainpanel*/ = Ext.create("Ext.form.Panel", );
				 
		Ext.apply(this, {
			defaults:{bodyStyle:"padding:0px"},
			
			items: [
				{
					xtype: "panel",
					"layout": "fit",
					border: 0,
					defaults: {
						bodyPadding: 5
					},
					items: [
						{
							xtype: "panel",
							layout: "anchor",
							autoScroll: true,
							hidden: panel.atype != 0,
							items: [
								{
									xtype: "fieldcontainer",
									layout: "anchor",
									items: [
										{
											xtype: "combobox",
											fieldLabel: "Analysis type",
											queryMode: 'local',
											name: "p3",
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
													var tval = tobj.getValue();
													
													this.s1/*setAnalysisType*/(tval);
												},
												scope: this
											}
										},
										{
											xtype: "fieldcontainer",
											fieldLabel: "Points per parameter",
											name: "p2",
											items: [
												{
													xtype: "numberfield",
													width: 40
												}
											]
										}
									]
								},
								{
									xtype: "panel",
									layout: {
										type: "vbox",
										align: "stretch"
									},
									items: [
									    {
									    	xtype: "fieldset",
									    	title: "Parameters",
									    	height: 200,
									    	layout: {
									    		type: "hbox",
									    		align: "stretch"
									    	},
									    	items: [
												{
													xtype: "gridpanel",
													name: "p0",
													flex: 1,
													columns: [
													    {
													    	text: "Name",
													    	flex: 1
													    }
													]
												},
												{
													xtype: "gridpanel",
													name: "p1",
													hidden: true,
													flex: 1,
													columns: [
													    {
													    	text: "Name",
													    	flex: 1
													    }
													]
												}
									    	]
									    },
										{
											xtype: "fieldset",
											title: "Options",
											layout: "anchor",
											flex: 1,
											bodyPadding: 5,
											items: [
												{
													xtype: "fieldcontainer",
													fieldLabel: "Horizontal axis parameter",
													layout: "hbox",
													items: [
														{
															xtype: "combobox",
															queryMode: 'local',
															name: "p4",
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
															width: 60,
															name: "p4a",
															labelWidth: 20,
															fieldLabel: "%"
														}
													]
												},
												{
													xtype: "fieldcontainer",
													fieldLabel: "Vertical axis parameter",
													layout: "hbox",
													items: [
														{
															xtype: "combobox",
															queryMode: 'local',
															name: "p5",
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
															width: 60,
															name: "p5a",
															labelWidth: 20,
															fieldLabel: "%"
														}
													]
												},
												{
													xtype: "combobox",
													queryMode: 'local',
													fieldLabel: "Scope",
													name: "p6",
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
													xtype: "combobox",
													queryMode: 'local',
													fieldLabel: "Decision",
													name: "p7",
													hidden: true,
													displayField: 'name',
													valueField: 'value',
													editable: false,
													autoSelect: true,
													store: {
														xtype: 'store',
														fields: [
															"name", "value"
														],
														data: [
															{name: "Percentage of the 2nd order probability"},
															{name: "Percentage over reference value"},
															{name: "Ratio over reference value"},
															{name: "User defined interval"}
														]
													}
												},
												{
													xtype: "combobox",
													queryMode: 'local',
													name: "p8",
													fieldLabel: "When a probability parameter is above 1",
													labelWidth: 220,
													displayField: 'name',
													valueField: 'value',
													editable: false,
													autoSelect: true,
													store: {
														xtype: 'store',
														fields: [
															"name", "value"
														],
														data: [
															{name: "Ignore case"},
															{name: "Throw error message", value: "dbc"}
														]
													}
												}
											]
										},
										{
											xtype: "fieldset",
											title: "Scenario",
											layout: "anchor",
											items: [
												{
													xtype: "combobox",
													queryMode: 'local',
													fieldLabel: "Do test?",
													name: "p9",
													displayField: 'name',
													valueField: 'value',
													editable: false,
													autoSelect: true,
													store: {
														xtype: 'store',
														fields: [
															"name", "value"
														],
														data: [
															{name: "Yes"},
															{name: "No", value: "dbc"}
														]
													}
												},
												{
													xtype: "combobox",
													queryMode: 'local',
													name: "pa",
													fieldLabel: "Result of test",
													displayField: 'name',
													valueField: 'value',
													editable: false,
													autoSelect: true,
													store: {
														xtype: 'store',
														fields: [
															"name", "value"
														],
														data: [
															{name: "not done"},
															{name: "done"}
														]
													}
												}
										    ]
										}
									]
								}
							]
						},
						{
							xtype: "panel",
							layout: "anchor",
							hidden: panel.atype != 1,
							items: [
								{
									xtype: "combobox",
									queryMode: 'local',
									name: "pb",
									fieldLabel: "Scope",
									displayField: 'name',
									valueField: 'value',
									editable: false,
									autoSelect: true,
									store: {
										xtype: 'store',
										fields: [
											"name", "value"
										],
										data: [
											{name: "Global"},
											{name: "One decision"}
										]
									}
								},
								{
									xtype: "combobox",
									queryMode: 'local',
									name: "pc",
									fieldLabel: "Decision",
									displayField: 'name',
									valueField: 'value',
									editable: false,
									autoSelect: true,
									store: {
										xtype: 'store',
										fields: [
											"name", "value"
										],
										data: [
											{name: "Therapy"},
											{name: "Percentage over reference value"},
											{name: "Ratio over reference value"},
											{name: "User defined interval"}
										]
									}
								},
								{
									xtype: "fieldset",
									layout: "anchor",
									title: "Scenario",
									items: [
										{
											xtype: "combobox",
											queryMode: 'local',
											name: "pd",
											fieldLabel: "Do test?",
											displayField: 'name',
											valueField: 'value',
											editable: false,
											autoSelect: true,
											store: {
												xtype: 'store',
												fields: [
													"name", "value"
												],
												data: [
													{name: "yes"},
													{name: "no"}
												]
											}
										},
										{
											xtype: "combobox",
											queryMode: 'local',
											name: "pe",
											fieldLabel: "Result of test",
											displayField: 'name',
											valueField: 'value',
											editable: false,
											autoSelect: true,
											store: {
												xtype: 'store',
												fields: [
													"name", "value"
												],
												data: [
													{name: "not done"},
													{name: "no"}
												]
											}
										}
									]
								}
							]
						}
					]
				}
			],
			buttons:[
				"->",
				{
					text: IRm$/*resources*/.r1("B_CONFIRM"),
					handler: function() {
						this._IFf/*confirmDialog*/();
					},
					scope: this
				}, {
					text: IRm$/*resources*/.r1("B_CANCEL"),
					handler:function() {
						this.close();
					},
					scope: this
				}
			],
			listeners: {
				afterrender: function(ui) {
					var panel = this;
					panel._IFe/*initF*/();
				}
			}
		});
		
		IG$/*mainapp*/.bA_4/*cost_effectiveness*/.superclass.initComponent.apply(this, arguments);
	}
});