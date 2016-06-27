IG$/*mainapp*/._IB9c/*ml_bayes_pgmx*/ = Ext.extend(Ext.Window, {
	modal: true,
	"layout": "fit",
	closable: false,
	resizable:false,
	width: 500,
	height: 500,
	
	callback: null,
	
	_IFd/*init_f*/: function() {
		var me = this,
			itemobj = me.itemobj;
		
		if (itemobj)
		{
			var instanceid = itemobj.instanceid,
				uid = itemobj.uid,
				req = new IG$/*mainapp*/._I3e/*requestServer*/();
				
			req.init(me, 
				{
		            ack: "77",
		            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: uid, instanceid: instanceid, action: "restore_pgmx"}, "uid;action;instanceid"),
		            mbody: itemobj.L2/*getContent*/()
		        }, me, function(xdoc) {
		        	var txtpgmx = me.down("[name=txtpgmx]"),
		        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/data"),
		        		tval;
		        	
		        	if (tnode)
		        	{
		        		tval = IG$/*mainapp*/._I24/*getTextContent*/(tnode);
		        		txtpgmx.setValue(Base64.decode(tval));
		        	}
		        }, false);
			req._l/*request*/();
		}
	},
	
	_l1/*loadURL*/: function(fuid) {
		var me = this,
			m1 = me.down("[name=m1]"),
			m1val = m1.getValue(),
			itemobj = me.itemobj;
		
		m1.clearInvalid();
		
		if (!fuid && !m1val)
		{
			m1.markInvalid("Empty");
			return;
		}
		
		var instanceid = itemobj.instanceid,
			uid = itemobj.uid,
			req = new IG$/*mainapp*/._I3e/*requestServer*/();
			
		req.init(me, 
			{
	            ack: "77",
	            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: uid, instanceid: instanceid, action: "load_url"}, "uid;action;instanceid"),
	            mbody: "<smsg><info" + (fuid ? " fuid='" + fuid + "'>" : "><url><![CDATA[" + m1val + "]]></url>") + "</info></smsg>"
	        }, me, function(xdoc) {
	        	var txtpgmx = me.down("[name=txtpgmx]"),
	        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/data"),
	        		tval;
	        	
	        	if (tnode)
	        	{
	        		tval = IG$/*mainapp*/._I24/*getTextContent*/(tnode);
	        		txtpgmx.setValue(Base64.decode(tval));
	        	}
	        }, false);
		req._l/*request*/();
	},
	
	km1/*uploadFile*/: function() {
		var me = this,
			mform = me.down("[name=mform]"),
			txtfilename = me.down("[name=txtfilename]");
		
		if (txtfilename.getValue())
		{
			mform.getForm().submit({
				url: ig$/*appoption*/.servlet,
				waitMsg: 'Uploading your data file',
				success: function(fp, o) {
					IG$/*mainapp*/._I54/*alertmsg*/('Success', 'Processed file on the server', null, me, 0, "success");
					var node = IG$/*mainapp*/._I18/*XGetNode*/(fp.errorReader.xmlData, "/smsg/result"),
						uid = IG$/*mainapp*/._I1b/*XGetAttr*/(node, "uid");
					
					me._l1/*loadURL*/.call(me, uid);
				}
			})
		}
	},
	
	initComponent: function() {
		var me = this;
		
		me.title = IRm$/*resources*/.r1('L_ML_C7');
		
		Ext.apply(this, {
			defaults:{bodyStyle:"padding:10px"},
			items: [
				{
					xtype: "panel",
					layout: "border",
					autoHeight: true,
					defaults: {
						anchor: "100%"
					},
					items: [
						{
							xtype: "fieldset",
							title: "Open External Resources",
							region: "north",
							layout: {
								type: "vbox",
								align: "stretch"
							},
							items: [
								{
									xtype: "fieldcontainer",
									layout: "hbox",
									fieldLabel: "Open from URL",
									items: [
										{
											xtype: "textfield",
											name: "m1"
										},
										{
											xtype: "button",
											text: "Load",
											handler: function() {
												this._l1/*loadURL*/();
											},
											scope: this
										}
									]
								},
								{
									xtype: "form",
									name: "mform",
									border: 0,
									layout: "fit",
									items: [
										{
										  	xtype: "fieldcontainer",
										  	fieldLabel: "File Upload",
										  	layout: {
										  		type: "hbox",
										  		align: "stretch"
										  	},
										  	items: [
												{
													xtype: "fileuploadfield",
													name: "fileupload",
													flex: 1,
													buttonText: IRm$/*resources*/.r1("L_SELECT_FILE"),
													hideLabel: true,
													listeners: {
														change: function(tobj, value, eopt) {
															var fname = "",
																dval = "\\",
																txtfilename = me.down("[name=txtfilename]");
															if (value)
															{
																if (value.indexOf("/") > -1)
																{
																	dval = "/";
																}
																value = value.split(dval);
																if (value.length > 0)
																{
																	fname = value[value.length - 1];
																}
															}
															txtfilename.setValue(fname);
														}
													}
												},
												{
													xtype: "hiddenfield",
													name: "txtfilename"
												},
												{
													xtype: "hiddenfield",
													name: "targetfolder",
													value: "upload"
												},
												{
													xtype: "hiddenfield",
													name: "filenamemode",
													value: "blank"
												},
												{
													xtype: "button",
													text: IRm$/*resources*/.r1("L_UPLOAD"),
													handler: function() {
														me.km1/*uploadFile*/.call(me);
													},
													scope: this
												}
										  	]
										}
									]
								}
							]
						},
						{
							xtype: "textarea",
							flex: 1,
							labelAlign: "top",
							fieldLabel: "PGMX Content",
							region: "center",
							name: "txtpgmx"
						}
					]
				}
			],
			buttons:[
				{
					text: IRm$/*resources*/.r1('B_CONFIRM'),
					handler: function() {
						var me = this,
							callback = me.callback,
							txtpgmx = me.down("[name=txtpgmx]");
						
						callback && callback.execute(txtpgmx.getValue());
					},
					scope: this
				}, 
				{
					text: IRm$/*resources*/.r1('B_CANCEL'),
					handler:function() {
						this.close();
					},
					scope: this
				}
			]
		});
		
		IG$/*mainapp*/._IB9c/*ml_bayes_pgmx*/.superclass.initComponent.call(this);
	},
	
	listeners: {
		afterrender: function(tobj) {
			var me = this,
				mform = me.down("[name=mform]");
				
			mform.getForm().errorReader = new IG$/*mainapp*/.m2ER();
			me._IFd/*init_f*/();
		}
	}
	
});	