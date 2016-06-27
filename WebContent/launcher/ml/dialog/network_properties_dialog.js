IG$/*mainapp*/.bA_1/*network_properties_dialog*/ = Ext.extend(Ext.Window, {
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
		var me = this,
			tdata = ig$/*appoption*/.b$Am,
			network_types = tdata.network_types,
			variable_types = tdata.variable_types,
			net_type = me.down("[name=net_type]"),
			var_type = me.down("[name=var_type]");
			
		if (network_types)
		{
			net_type.store.loadData(network_types);
		}
		
		if (variable_types)
		{
			var_type.store.loadData(variable_types);
		}
		
		if (me.itemobj)
		{
			net_type.setValue(me.itemobj.mtype || "");
			var_type.setValue(me.itemobj.variable_types || "");
		}
	},
	
	_IFf/*confirmDialog*/: function() {
		var me = this,
			net_type = me.down("[name=net_type]"),
			itemobj = me.itemobj;
		
		net_type.clearInvalid();
		
		if (!net_type.getValue())
		{
			net_type.markInvalid("Empty");
			return;
		}
		
		me.itemobj.mtype = net_type.getValue();
		me.itemobj.mtypename = net_type.getValue();
		
		me.callback && me.callback.execute();
		
		me.close();
	},
	
	_IG0/*closeDlgProc*/: function(param) {
		var me = this;
		
		me.close();
	},
		
	initComponent : function() {
		var panel = this;
		
		panel.title = IRm$/*resources*/.r1("T_NET_PROP");
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
									title: "Definition",
									layout: "anchor",
									items: [
										{
											xtype: "combobox",
											fieldLabel: "Network Type",
											name: "net_type",
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
											xtype: "checkbox",
											disabled: true,
											fieldLabel: "Is Object Oriented"
										}
									]
								},
								{
									xtype: "panel",
									title: "Variables",
									layout: "anchor",
									items: [
										{
											xtype: "combobox",
											fieldLabel: "Variable Type",
											name: "var_type",
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
											xtype: "combobox",
											fieldLabel: "Default States",
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
													{name: "Absent - Present"},
													{name: "No - Yes"},
													{name: "Negative - Positive"},
													{name: "Absent - Mild - Moderate - Severe"},
													{name: "Low - Medium - High"}
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
		
		IG$/*mainapp*/.bA_1/*network_properties_dialog*/.superclass.initComponent.apply(this, arguments);
	}
});