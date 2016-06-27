IG$/*mainapp*/.BC$base/*hadoop_dlg_base*/ = Ext.extend(Ext.panel.Panel, {
	
	region:"center",
	"layout": "fit",
			
	callback: null,
	
	_IFe/*initF*/: function() {
		var me = this,
			job = me.job;
			
		if (job)
		{
			me.down("[name=dsname]").setValue(job.dsname);
			me.down("[name=dsdesc]").setValue(job.dsdesc);
			me.down("[name=dsignoreerror]").setValue(job.dsignoreerror == "T" ? true : false);
		}
	},
	
	
	_IFf/*confirmDialog*/: function() {
		var me = this,
			job = me.job;
			
		if (job)
		{
			job.dsname = me.down("[name=dsname]").getValue();
			job.dsdesc = me.down("[name=dsdesc]").getValue();
			job.dsignoreerror = me.down("[name=dsignoreerror]").getValue() == true ? "T" : "F";
		}
		
		me._IG0/*closeDlgProc*/();
	},
	
	_IG0/*closeDlgProc*/: function() {
		var me = this;
		
		me.callback && me.callback.execute(me.job);
		
		me.fireEvent("close_dlg", me);
	},
	
	initComponent : function() {
		var panel = this,
			bsitem = [],
			i;
		
		panel.title = panel.title || IRm$/*resources*/.r1("T_BG_DS");
		panel.addEvents("close_dlg");
		
		bsitem.push({
			xtype: "fieldset",
			title: "Basic Option",
			layout: "anchor",
			defaults: {
				anchor: "100%",
				labelAlign: "top"
			},
			items: [
				{
					xtype: "textfield",
					name: "dsname",
					fieldLabel: "Name"
				},
				{
					xtype: "textarea",
					name: "dsdesc",
					fieldLabel: "Description"
				},
				{
					xtype: "checkbox",
					name: "dsignoreerror",
					hidden: panel.hide_ierr ? true : false,
					fieldLabel: "Ignore Error",
					labelAlign: "left",
					boxLabel: "Ignore and continue"
				}
			]
		});
		
		if (panel.moption)
		{
			for (i=0; i < panel.moption.length; i++)
			{
				bsitem.push(panel.moption[i]);
			}
		}
		
		Ext.apply(this, {
			defaults:{bodyStyle:"padding:3px"},
			
			items: [
				{
					xtype: "tabpanel",
					layout: "fit",
					items: [
						{
							xtype: "form",
							layout: "anchor",
							title: "Configuration",
							autoScroll: true,
							defaults: {
								anchor: "100%"
							},
							items: bsitem
						},
						{
							xtype: "form",
							title: "History",
							hidden: panel.s_hist == 0,
							layout: {
								type: "vbox",
								align: "stretch"
							},
							items: [
								{
									xtype: "fieldset",
									title: "Execute Log",
									layout: "fit",
									flex: 1,
									items: [
										{
											xtype: "gridpanel",
											name: "grdloghist",
											store: {
												xtype: "store",
												fields: [
													"sid", "lid", "jobtype", "pstatus", "created", "updated",
													"pstatus_desc", "created_desc", "updated_desc", "duration"
												]
											},
											columns: [
												{
													xtype: "gridcolumn",
													text: "Start time",
													flex: 1,
													dataIndex: "created_desc"
												},
												{
													xtype: "gridcolumn",
													text: "End time",
													flex: 1,
													dataIndex: "updated_desc"
												},
												{
													xtype: "gridcolumn",
													text: "Duration",
													dataIndex: "duration",
													flex: 1
												},
												{
													xtype: "gridcolumn",
													text: "Status",
													width: 120,
													dataIndex: "pstatus_desc"
												}
											],
											tbar: [
												{
													xtype: "button",
													text: "Refresh",
													handler: function() {
														var me = this;
														me.rL/*refreshLog*/();
													},
													scope: this
												}
											],
											listeners: {
												itemclick: function(view, record, item, index, e) {
													var lid = record.get("lid");
													
													if (lid)
													{
														this.g1/*getLogDetail*/(lid);
													}
												},
												scope: this
											}
										}
									]
								},
								{
									xtype: "fieldset",
									title: "Log Details",
									layout: "fit",
									flex: 2,
									items: [
										{
											xtype: "textarea",
											name: "logdetail"
										}
									]
								}
							]
						},
						{
							xtype: "panel",
							hidden: (panel.s_hist != 0),
							title: "Probability",
							layout: {
								type: "vbox",
								align: "stretch"
							},
							items: [
							    {
							    	xtype: "fieldcontainer",
							    	fieldLabel: "Relation Type",
							    	layout: "hbox",
							    	items: [
										{
											xtype: "combobox",
											name: "potential_type",
											labelWidth: 80,
											width: 200,
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
											xtype: "button",
											text: "Reorder Potential"
										}
							    	]
							    },
							    {
							    	xtype: "fieldcontainer",
							    	layout: "hbox",
							    	items: [
								    	{
								    		xtype: "button",
								    		text: "Edit mean potentail"
								    	},
								    	{
								    		xtype: "button",
								    		text: "Edit variance potential"
								    	}
							    	]
							    },
								{
							    	xtype: "gridpanel",
									hideHeaders: false,
									columnLines: true,
									name: "g_p1",
									viewConfig : {
										enableTextSelection : true,
										ForceFit : true,
										loadMask : true
									},
									flex: 1,
									columns: [
										
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
						// this.close();
						var me = this;
						me.fireEvent("close_dlg", me);
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
		
		IG$/*mainapp*/.BC$base/*hadoop_dlg_base*/.superclass.initComponent.apply(this, arguments);
	}
});