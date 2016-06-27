
IG$/*mainapp*/._I51/*ShowErrorMessage*/ = function(doc, parent, params) {
	var root = IG$/*mainapp*/._I18/*XGetNode*/(doc, "/smsg"),
		errcode = IG$/*mainapp*/._I1b/*XGetAttr*/(root, "errorcode"),
		errdesc = IG$/*mainapp*/._I1b/*XGetAttr*/(root, "errormsg"),
		dnode = IG$/*mainapp*/._I18/*XGetNode*/(root, "detail"),
		errmsg = dnode ? IG$/*mainapp*/._I24/*getTextContent*/(dnode) : null,
		snode = IG$/*mainapp*/._I18/*XGetNode*/(root, "stacktrace"),
		errstack = snode ? IG$/*mainapp*/._I24/*getTextContent*/(snode) : null,
		pop,
		merror = false;
		
	switch (errcode)
	{
	case "0x7500":
	case "0x6d00":
		merror = true;
		break;
	}
	
	if (merror == false && (errmsg || errstack || params))
	{
//		pop = new IG$/*mainapp*/.E5a/*ErrorDialog*/({
//			a1/*messagecontent*/: {
//				errdesc: errdesc,
//				errmsg: errmsg,
//				errstack: errstack,
//				params: params
//			}
//		});
//		pop.show();

		IG$/*mainapp*/._I54/*alertmsg*/(ig$/*appoption*/.appname, errdesc + "<br/>\n\n" + (errmsg || ""), null, parent, 1, "error", null, {
			errdesc: errdesc,
			errmsg: errmsg,
			errstack: errstack,
			params: params
		});
	}
	else
	{
		IG$/*mainapp*/._I54/*alertmsg*/(ig$/*appoption*/.appname, errdesc + "\n\n" + (errmsg || ""), null, parent, 1, "error");
	}
}

IG$/*mainapp*/._I52/*ShowError*/ = function(errdesc, parent) {
	IG$/*mainapp*/._I54/*alertmsg*/(ig$/*appoption*/.appname, errdesc, null, parent, 1, "error");
}

IG$/*mainapp*/._I53/*ShowConnectionError*/ = function(panel) {
	IG$/*mainapp*/._I54/*alertmsg*/(ig$/*appoption*/.appname, IRm$/*resources*/.r1('M_ERR_CONNECT'), null, panel, 1, "error");
	//history.back(-1);
}

if (IG$/*mainapp*/.pb)
{
	IG$/*mainapp*/.E5a/*ErrorDialog*/ = IG$/*mainapp*/.x_c/*extend*/(IG$/*mainapp*/.pb, {
		xtype: "panel",
		modal: true,
		region:'center',
		
		"layout": 'fit',
		
		closable: false,
		resizable:false,
		constrain: true,
		constrainHeader: true,
		
		callback: null,
		ignoreHeaderBorderManagement: true,
		frame: false,
		width: 300,
		height: 350,
		
		_ic/*initComponent*/ : function() {
			var me = this,
				msg = me.a1/*messagecontent*/,
				param = "",
				k;
			
			if (msg.params)
			{
				for (k in msg.params)
				{
					if (param)
					{
						param += "\n";
					}
					
					param += k + ": " + Base64.encode(msg.params[k]);
				}
				
				param += "\n\n";
				
				if (msg.errstack)
				{
					param += "stacktrace :" + msg.errstack + "\n";
				}
			}
			else
			{
				param = "stacktrace : " + msg.errstack + "\n";
			}
			
			msg.errstack = param;
			
			IG$/*mainapp*/.apply(this, {
				title: "Server Message",
				items: [
					{
						xtype: "container",
						name: "m_msg",
						border: 0,
						layout: "fit",
						listeners: {
							afterrender: function(tobj) {
								var m_msg = me.down("[name=m_msg]"),
									m_msg_body = m_msg.body.dom,
									mbody;
									
								m_msg_body.empty();
								
								mbody = $("<div class='ing-error-dlg'></div>").appendTo(m_msg_body)
								
								mbody.append("<div class='ing-error-desc'>" + msg.errdesc + "</div>");
								
								if (msg.errmsg)
								{
									mbody.append("<div class='ing-error-msg'>" + msg.errmsg + "</div>");
								}
								
								if (msg.errstack)
								{
									mbody.append("<div class='ing-error-stack'><span>" + param + "</span></div>");
								}
							}
						}
					}
				],
				
				buttons: [
					{
						text: IRm$/*resources*/.r1("B_CLOSE"),
						handler: function() {
							this.close();
						},
						scope: this
					}
				]
			});
			IG$/*mainapp*/.E5a/*ErrorDialog*/.superclass._ic/*initComponent*/.apply(this, arguments);
		}
	});
}

IG$/*mainapp*/._I54/*alertmsg*/ = function(title, msg, fn, parent, ismodal, mtype, stack, a1/*messagecontent*/, btn) {
	if (ismodal == 2)
	{
		btn = 1;
	}

	var body = $("body"),
		msgbox = $("<div class='igc-alert-cnt'>" + (ismodal ? "<div class='igc-alert-bg'></div>" : "") + "<div class='igc-alert-body'></div></div>").appendTo(body),
		mbody = $(".igc-alert-body", msgbox),
		bbox,
		bclose, bdetail,
		h = IRm$/*resources*/.r1("M_UNKNOWN"),
		pd = "", k,
		msgpop,
		binfo = "", bk,
		browser = window.bowser;
		
	mtype = mtype || "warning";
	
	switch (mtype)
	{
	case "error":
		h = IRm$/*resources*/.r1("M_ERROR");
		break;
	case "warning":
		h = IRm$/*resources*/.r1("M_WARNING");
		break;
	case "info":
		h = IRm$/*resources*/.r1("M_INFO");
		break;
	case "success":
		h = IRm$/*resources*/.r1("M_SUCCESS");
		break;
	}
	
	if (a1/*messagecontent*/)
	{
		if (a1/*messagecontent*/.params)
		{
			for (k in a1/*messagecontent*/.params)
			{
				if (pd)
				{
					pd += "\n";
				}
				
				pd += k + ": " + Base64.encode(a1/*messagecontent*/.params[k]);
			}
			
			pd += "\n\n";
			
			if (a1/*messagecontent*/.errstack)
			{
				pd += "stacktrace :" + a1/*messagecontent*/.errstack + "\n";
			}
		}
		else
		{
			pd = "stacktrace : " + a1/*messagecontent*/.errstack + "\n";
		}
		
		if (pd && browser)
		{
			for (bk in browser)
			{
				if (typeof(browser[k]) == "function")
					continue;
					
				binfo += k + ": " + browser[k] + "\n";
			}
			
			pd = binfo + "\n\n" + pd;
		}
	}
		
	bbox = $("<div class='igc-alert'><a class='close'>&#215;</a><div class='detail' style='display: none'>" + IRm$/*resources*/.r1("M_DETAIL") + "</div><h4 class='alert-heading'>" + h + "</h4><span>" + msg + "</span></div>").appendTo(mbody);
	bclose = $(".close", bbox)[btn ? "hide" : "show"]();
	bdetail = $(".detail", bbox);
	
	pd && bdetail.show();
	
	bbox.addClass("igc-alert-" + mtype);
	
	bdetail.bind("click", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
		
		msgpop = new IG$/*mainapp*/.E5a/*ErrorDialog*/({
			renderTo: msgbox,
			a1/*messagecontent*/: a1/*messagecontent*/
		});
		msgpop.setPosition((msgbox.width() - msgpop.width) / 2, (msgbox.height() - msgpop.height) / 2);
		msgpop.setSize(msgpop.width, msgpop.height);
		msgpop.show();
	});
	
	bclose.bind("click", function(e) {
		e.preventDefault();
		e.stopImmediatePropagation();
		msgpop && msgpop.close();
		msgbox.fadeOut(function() {
			if (fn)
			{
				fn.call(parent);
			}
			msgbox.remove();
		});
	});
	
	if (!btn)
	{
		bbox.bind("click", function(e) {
			e.preventDefault();
			e.stopImmediatePropagation();
			msgpop && msgpop.close();
			msgbox.fadeOut(function() {
				if (fn)
				{
					fn.call(parent);
				}
				msgbox.remove();
			});
		});
	}
	else
	{
		var dbtn,
			dbtnsul,
			btns = [];
		
		if (btn == 6 || btn == 14)
		{
			btns.push({
				text: IRm$/*resources*/.r1("B_YES"),
				bseq: 2,
				dlg: "yes"
			});
			
			btns.push({
				text: IRm$/*resources*/.r1("B_NO"),
				bseq: 4,
				dlg: "no"
			});
			
			if (btn == 14)
			{
				btns.push({
					text: IRm$/*resources*/.r1("B_CANCEL"),
					bseq: 8,
					dlg: "cancel"
				});
			}
		}
		else if (btn == 1 || btn == 9)
		{
			btns.push({
				text: IRm$/*resources*/.r1("B_OK"),
				bseq: 1,
				dlg: "ok"
			});
			
			if (btn == 9)
			{
				btns.push({
					text: IRm$/*resources*/.r1("B_CANCEL"),
					bseq: 8,
					dlg: "cancel"
				});
			}
		}
		
		dbtn = $("<div class='igc-alert-buttons'><ul></ul></div>").appendTo(bbox);
		dbtnsul = $("ul", dbtn);
		
		$.each(btns, function(i, m) {
			var dp = $("<li></li>").appendTo(dbtnsul);
				d = $("<div class='igc-button'>" + m.text + "</div>").appendTo(dp);
				
			d.bind("click", function(e) {
				e.preventDefault();
				e.stopImmediatePropagation();
				msgpop && msgpop.close();
				msgbox.fadeOut(function() {
					if (fn)
					{
						fn.call(parent, m.dlg);
					}
					msgbox.remove();
				});
			});
		});
	}
	
	if (!ismodal)
	{
		msgbox.addClass("igc-no-modal");
		
		setTimeout(function() {
			msgpop && msgpop.close();
			
			msgbox.fadeOut(function() {
				if (fn)
				{
					fn.call(parent);
				}
				msgbox.remove();
			});
		}, 1500);
	}
}

IG$/*mainapp*/._I55/*confirmMessages*/ = function(title, msg, fn, parent, owner, btn) {
	// title, msg, fn, parent, ismodal, mtype, stack, a1/*messagecontent*/
	IG$/*mainapp*/._I54/*alertmsg*/(
		title || IRm$/*resources*/.r1("L_SAVE_CHANGES"),
		msg || IRm$/*resources*/.r1("L_SAVE_C_MSG"),
		fn,
		owner,
		1,
		"info",
		null, null, btn || 6);
}

if (window.Ext)
{
	IG$/*mainapp*/._I57/*IngPanel*/ = Ext.extend(Ext.panel.Panel, {
		frameHeader: false,
		
		initComponent: function() {
			this.on("destroy", function(tobj, eopts) {
				tobj.__dx = true;
			});
			
			IG$/*mainapp*/._I57/*IngPanel*/.superclass.initComponent.call(this);
		},
		
		destroy: function() {
			var me = this;
			me.__dx = true;
			setTimeout(function() {
				IG$/*mainapp*/._I57/*IngPanel*/.superclass.destroy.call(me);
			}, 10);
		},
		
		setLoading: function(load, targetEl) {
			var me = this,
	            config,
	            mask, moff, toff,
	            mdom;
	            
	        if (me.rendered) 
	        {
	           	Ext.destroy(me.loadMask);
	            me.loadMask = null;
	            
	            if (me.rendermask)
	        	{
	        		me.rendermask.empty();
	        		me.rendermask.hide();
	        	}
	
	            if (load !== false && !me.collapsed) 
	            {
	                if (Ext.isObject(load)) 
	                {
	                    config = Ext.apply({}, load);
	                } 
	                else if (Ext.isString(load)) 
	                {
	                    config = {msg: load};
	                } 
	                else 
	                {
	                    config = {};
	                }
	                
	                if (me.rendermask)
	                {
	                	mdom = $(me.el.dom);
		                me.rendermask.show();
		                me.rendermask.empty();
		                moff = $(mdom.parent()).offset();
		                toff = mdom.offset();
		                me.rendermask.css({top: toff.top - moff.top});
		                
	                }
	                
	                if (targetEl) 
	               	{
	               		
	                    Ext.applyIf(config, {
	                        useTargetEl: true
	                    });
	                }
	                me.loadMask = new Ext.LoadMask((me.rendermask ? me.rendermask[0] : null) || me, config);
	                me.loadMask.show(me.ownerCt);
	            }
	        }
			return me.loadMask;
		}
	});
}


if (window.Ext)
{
	// fix hide submenu (in chrome 43)
	Ext.override(Ext.menu.Menu, {
	    onMouseLeave: function(e) {
	    var me = this;
	
	
	    // BEGIN FIX
	    var visibleSubmenu = false;
	    me.items.each(function(item) { 
	        if(item.menu && item.menu.isVisible()) { 
	            visibleSubmenu = true;
	        }
	    })
	    if(visibleSubmenu) {
	        //console.log('apply fix hide submenu');
	        return;
	    }
	    // END FIX
	
	
	    me.deactivateActiveItem();
	
	
	    if (me.disabled) {
	        return;
	    }
	
	
	    me.fireEvent('mouseleave', me, e);
	    }
	});
}

IG$/*mainapp*/.m2ER = function() {
	var fields = [];
	var config = {
		type: 'xml',
		root: 'smsg',
		record: 'item',
		success: '@success'
	}
	IG$/*mainapp*/.m2ER.superclass.constructor.call(this, config, fields);
};

window.Ext && Ext.extend(IG$/*mainapp*/.m2ER, Ext.data.reader.Xml, {
	readRecords: function (doc) {
		this.xmlData = doc;
		var node = IG$/*mainapp*/._I18/*XGetNode*/(doc, "/smsg/item");
		
		if (node)
		{
			records = this.extractData(node);
			recordCount = records.length;
		}
		else
		{
			recordCount = 0;
			records = [];
		}
		
		var ret = Ext.create('Ext.data.ResultSet', {
			total: recordCount,
			count: recordCount,
			records: records,
			success: true,
			message: null
		});
		
		return ret;
	},
	
	read: function(response) {
		var doc = response.responseXML;
		if(!doc) {
			throw {message: "XmlReader.read: XML Document not available"};
		}
		this.xmlData = doc;
		return this.readRecords(doc);
	}
});
