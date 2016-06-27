IG$/*mainapp*/.bA_3/*db_generator*/ = Ext.extend(Ext.Window, {
	modal: true,
	isWindow: true,
	region:"center",
	"layout": {
		type: "fit",
		align: "stretch"
	},
	
	closable: false,
	resizable:false,
	width: 400,
	autoHeight: true,
	
	callback: null,
	
	_IFe/*initF*/: function() {
		var me = this;
		me.down("[name=filetype]").setValue("csv");
	},
	
	_IFf/*confirmDialog*/: function() {
		var me = this,
			num_cases = me.down("[name=num_cases]"),
			filetype = me.down("[name=filetype]");
			
		me.callback && me.callback.execute({
			num_cases: num_cases.getValue(),
			filetype: filetype.getValue()
		});
		
		me.close();
	},
			
	initComponent : function() {
		var panel = this;
		
		panel.title = IRm$/*resources*/.r1("T_DB_GEN");
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
						bodyPadding: 10
					},
					items: [
						{
							xtype: "panel",
							layout: "anchor",
							items: [
								{
									xtype: "combobox",
									fieldLabel: "File Type",
									queryMode: 'local',
									name: "filetype",
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
											{name: "CSV File", value: "csv"},
											{name: "Elvira DB File", value: "dbc"},
											{name: "Excel File", value: "xls"},
											{name: "Weka DB File", value: "arff"}
										]
									}
								},
								{
									xtype: "numberfield",
									name: "num_cases",
									fieldLabel: "Number of cases",
									minValue: 100,
									maxValue: 100000,
									value: 5000,
									step: 10
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
		
		IG$/*mainapp*/.bA_3/*db_generator*/.superclass.initComponent.apply(this, arguments);
	}
});