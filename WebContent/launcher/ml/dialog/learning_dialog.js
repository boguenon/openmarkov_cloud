IG$/*mainapp*/.bA_2/*learning_dialog*/ = Ext.extend(Ext.Window, {
	modal: true,
	isWindow: true,
	region:"center",
	"layout": {
		type: "fit",
		align: "stretch"
	},
	
	closable: false,
	resizable:false,
	width: 500,
	height: 450,
	
	callback: null,
	
	_IFe/*initF*/: function() {
	},
	
	_IFf/*confirmDialog*/: function() {
		var me = this;
		me.callback && me.callback.execute();
		me.close();
	},
			
	initComponent : function() {
		var panel = this;
		
		panel.title = IRm$/*resources*/.r1("T_MEASURE");
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
							xtype: "tabpanel",
							items: [
								{
									xtype: "panel",
									title: "General",
									layout: "anchor",
									autoScroll: true,
									items: [
										{
											xtype: "fieldset",
											title: "Database",
											items: [
												{
													xtype: "textfield",
													fieldLabel: "Database"
												}
											]
										},
										{
											xtype: "fieldset",
											title: "Algorithm",
											layout: "anchor",
											items: [
												{
													xtype: "combobox",
													fieldLabel: "Algorithm",
													queryMode: 'local',
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
															{name: "Hill climbing"},
															{name: "PC"}
														]
													},
													listeners: {
														change: function(tobj, nvalue, ovalue, eopt) {
															var me = this,
																m_p1 = me.down("[name=m_p1]"),
																c = nvalue == "PC";
																
															m_p1.setTitle(nvalue + " algorithm: Options");
															m_p1.show();
															
															m_pc1.setVisible(c);
															m_pc2.setVisible(!c);
															m_pc3.setVisible(c);
														},
														scope: this
													}
												},
												{
													xtype: "textarea",
													anchor: "100%",
													height: 60
												},
												{
													xtype: "fieldset",
													name: "m_p1",
													title: "Algorithm: Options",
													layout: "anchor",
													items: [
														{
															xtype: "combobox",
															fieldLabel: "Independence test",
															name: "m_pc1",
															hidden: true,
															queryMode: 'local',
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
																	{name: "Cross entropy", value: ""}
																]
															}
														},
														{
															xtype: "combobox",
															fieldLabel: "Metric",
															name: "m_pc2",
															hidden: true,
															queryMode: 'local',
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
																	{name: "K2", value: ""},
																	{name: "Bayesian", value: ""},
																	{name: "BD", value: ""},
																	{name: "MDLM", value: ""},
																	{name: "Entropy", value: ""},
																	{name: "ALC", value: ""}
																]
															}
														},
														{
															xtype: "numberfield",
															name: "m_pc3",
															hidden: true,
															fieldLabel: "Significance level"
														},
														{
															xtype: "numberfield",
															fieldLabel: "Alpha parameter"
														},
														{
															xtype: "displayfield",
															value: "(Laplace-like correction)"
														}
													]
												}
											]
										},
										{
											xtype: "fieldset",
											title: "Learning type",
											items: [
											    {
											    	xtype: "radiogroup",
											    	columns: 1,
											    	vertical: true,
											    	items: [
										    	        {
										    	        	boxLabel: "Interactive learning"
										    	        },
										    	        {
										    	        	boxLabel: "Automatic Learning"
										    	        }
											    	]
											    }
											]
										}
									]
								},
								{
									xtype: "panel",
									title: "Model network",
									layout: "anchor",
									items: [
										{
											xtype: "fieldset",
											title: "Choose Model Net",
											items: [
											    {
											    	xtype: "radiogroup",
											    	columns: 1,
											    	vertical: true,
											    	items: [
														{
															boxLabel: "Do not use any model network"
														},
														{
															boxLabel: "Use the already open newtork"
														},
														{
															boxLabel: "Load model network from file"
														}
											    	]
											    }
											]
										},
										{
											xtype: "fieldset",
											title: "Model network use",
											items: [
												{
													xtype: "checkbox",
													boxLabel: "Use the information of the nodes"
												},
												{
													xtype: "checkbox",
													boxLabel: "Start learning from model network"
												},
												{
													xtype: "fieldcontainer",
													padding: "0 0 0 20",
													items: [
														{
															xtype: "checkbox",
															boxLabel: "Allow link addition"
														},
														{
															xtype: "checkbox",
															boxLabel: "Allow link removal"
														},
														{
															xtype: "checkbox",
															boxLabel: "Allow link inversion"
														}
													]
												}
											]
										}
									]
								},
								{
									xtype: "panel",
									title: "Preprocessing",
									layout: "anchor",
									items: [
										{
											xtype: "combobox",
											fieldLabel: "Discretize",
											queryMode: 'local',
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
													{name: "Specify for each variable"},
													{name: "Do not discretize"},
													{name: "Equal frequency intervals"},
													{name: "Equal with intervals"}
												]
											}
										},
										{
											xtype: "combobox",
											fieldLabel: "Treat missing values",
											queryMode: 'local',
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
													{name: "Specify for each variable"},
													{name: "Keep records with missing values"},
													{name: "Erase records with missing values"}
												]
											}
										},
										{
											xtype: "checkbox",
											fieldLabel: "Same number of intervals"
										},
										{
											xtype: "numberfield",
											labelWidth: 120,
											width: 160,
											fieldLabel: "Number of intervals"
										},
										{
											xtype: "radiogroup",
											vertical: true,
											columns: 1,
											items: [
												{
													boxLabel: "Use all variables"
												},
												{
													boxLabel: "Use only the variables in the model network"
												},
												{
													boxLabel: "Use selected variables"
												}
											]
										},
										{
											xtype: "checkbox",
											boxLabel: "Select / unselect all variables"
										},
										{
											xtype: "gridpanel",
											flex: 1,
											columns: [
												{
													text: "Preprocessing",
													flex: 1
												},
												{
													text: "Missing values",
													flex: 1
												},
												{
													text: "Discretization",
													flex: 1
												},
												{
													text: "Number of intervals",
													width: 40
												}
											]
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
		
		IG$/*mainapp*/.bA_2/*learning_dialog*/.superclass.initComponent.apply(this, arguments);
	}
});