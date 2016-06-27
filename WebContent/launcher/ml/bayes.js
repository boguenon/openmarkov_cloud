IG$/*mainapp*/._IB9b/*ml_bayes*/ = Ext.extend(Ext.panel.Panel, {
	closable: true,
	
	layout: "fit",
	bodyPadding: 0,
	sgap: 1000,
	
	_IFd/*init_f*/: function() {
		var me = this,
			topmenu = me.topmenu,
			mainarea = me.mainarea,
			fmenu,
			farea,
			bf,
			dt = [],
			bccall,
			req;
			
		dt = [
			{
				shape: "circle",
				name: "chance",
				_cs: ["ba_base", "ba_circle"],
				text: IRm$/*resources*/.r1("L_ML_C1")
			},
			{
				shape: "rect",
				name: "decision",
				_cs: ["ba_base", "ba_rect"],
				text: IRm$/*resources*/.r1("L_ML_C2")
			},
			{
				shape: "rhombus",
				name: "utility",
				_cs: ["ba_base", "ba_rhombus"],
				text: IRm$/*resources*/.r1("L_ML_C3")
			}
		];
		
		farea = me.farea = $("<div class='idv-flw-reg'></div>").appendTo(mainarea);
		
		bf = me.bf = new IG$/*mainapp*/._I9a/*flowDiagram*/(farea);
		bf.init.call(bf);
			
		topmenu.empty();
		fmenu = $("<div class='idv-flw-mnu'></div>").appendTo(topmenu);
		
		me.l1/*initFlowMenu*/(fmenu, dt);
		
		bccall = new IG$/*mainapp*/._I3d/*callBackObj*/(me, me.l4/*onBoxUpdate*/);
		
		bf.container.bind("boxClicked", function(ev, data) {
			var dlg,
				moption = me.down("[name=moption]");
				
			// moption.removeAll();
			
			dlg = me.__dlg;
			
			if (!dlg)
			{
				me.__dlg = dlg = new IG$/*mainapp*/.BY_1/*ByayesChance*/({
					job: data,
					_sustain: 1,
					_vmode: data.name,
					_p/*editor*/: me,
					callback: bccall
				});
				
				moption.add(dlg);
				
				dlg.on("close_dlg", function(m) {
					// moption.remove(dlg);
					moption.collapse();
				});
			}
			else
			{
				dlg.job = data;
				dlg._vmode = data.name,
				dlg._IFe/*initF*/.call(dlg);
			}
			
			if (dlg)
			{
				moption.expand();
				// IG$/*mainapp*/._I_5/*checkLogin*/(this, dlg);
			}
		});
		
		bf.container.bind("flow_changed", function(ev, data) {
			switch(data.source)
			{
			case "box_add":
			case "link_node":
			case "move_link":
			case "link_detach":
				me.updateNetwork.call(me, data.source, data);
				break;
			}
		});
		
		bf.container.bind("visual_state_click", function(ev, data) {
			me.setNewFinding.call(me, data);
		});
		
		if (!ig$/*appoption*/.b$Am)
		{
			req = new IG$/*mainapp*/._I3e/*requestServer*/();
			req.init(me, 
				{
		            ack: "77",
		            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({action: "get_var"}, "action"),
		            mbody: IG$/*mainapp*/._I2e/*getItemOption*/({})
		        }, me, function(xdoc) {
		        	var me = this,
		        		data = {},
		        		tnode,
		        		tnodes,
		        		snodes,
		        		tname,
		        		tdata,
		        		titem,
		        		i, j;
		        		
		        	tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg");
		        	
		        	if (tnode)
		        	{
		        		tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnode);
		        		
		        		for (i=0; i < tnodes.length; i++)
		        		{
		        			tname = IG$/*mainapp*/._I29/*XGetNodeName*/(tnodes[i]);
		        			tdata = data[tname] = [];
		        			
		        			snodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnodes[i]);
		        			for (j=0; j < snodes.length; j++)
		        			{
		        				titem = {
		        					value: IG$/*mainapp*/._I1b/*XGetAttr*/(snodes[j], "value")
		        				};
		        				titem.name = titem.name || titem.value;
		        				tdata.push(titem);
		        			}
		        		}
		        		
		        		ig$/*appoption*/.b$Am = data;
		        	}
		        	
		        	me._a1/*loadInit*/();
		        }, false);
			req._l/*request*/();
		}
		else
		{
			me._a1/*loadInit*/();
		}
	},
	
	_a1/*loadInit*/: function() {
		var me = this;
		
		if (me.uid)
		{
			me.M1/*loadContent*/();
		}
		else
		{
			me.rs_M1/*rs_loadContent*/(null);
		}
	},
	
	l4/*onBoxUpdate*/: function(job) {
		var me = this,
			bf = me.bf;
		
		job.text = job.dsname;
		
		bf.s4/*setTitle*/(job, job.dsname);
	},
	
	M1/*loadContent*/: function() {
		var me = this,
			req = new IG$/*mainapp*/._I3e/*requestServer*/();
		
		if (me.uid)
		{
			me.setLoading(true);
			req.init(me, 
				{
		            ack: "5",
		            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid}),
		            mbody: IG$/*mainapp*/._I2e/*getItemOption*/({option: 'diagnostics'})
		        }, me, me.rs_M1/*rs_loadContent*/, false);
			req._l/*request*/();
		}
		else
		{
			me.rs_M1/*rs_loadContent*/(null);
		}
	},
	
	rs_M1/*rs_loadContent*/: function(xdoc) {
		var me = this,
			bf = me.bf;
		
		if (!me.itemobj)
		{
			me.itemobj = new IG$/*mainapp*/._IB8b/*BayesObject*/(xdoc, bf);
		}
		else
		{
			me.itemobj.L1/*parseContent*/(xdoc);
		}
		
		me.A1/*refreshUI*/();
	},
	
	A1/*refreshUI*/: function() {
		var me = this,
			bf = me.bf,
			itemobj = me.itemobj,
			mtype = itemobj.mtype;
			
		bf.L1/*loadOption*/.call(bf, itemobj);
		
		if (!mtype)
		{
			me.np/*network_properties*/();
		}
		else
		{
			if (!me.bfirst)
			{
				var itemobj = me.itemobj,
					instanceid = itemobj.instanceid,
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
			        		
			        	me.bfirst = 1;
			        	
			        	if (tnode)
			        	{
			        		itemobj.instanceid = IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "instanceid");
			        	}
			        }, false);
				req._l/*request*/();
			}
		}
		
		switch (mtype)
		{
		case "":
			break;
		}
	},
	
	m2/*getParents*/: function(mobj, pname) {
		var me = this,
			bf = me.bf,
			a_boxes = bf.a_boxes,
			i,
			cobj,
			cons = bf.plumb.getConnections(),
			src, tgt,
			connections = [],
			prev = [];
			
		for (i=0; i < a_boxes.length; i++)
		{
			if (a_boxes[i].sid == mobj.job.sid)
			{
				cobj = a_boxes[i];
				break;
			}
		}
		
		if (cons && cons.length)
		{
			for (i=0; i < cons.length; i++)
			{
				src = cons[i].source;
	            tgt = cons[i].target;
	            
	            src = bf.getBoxObject.call(bf, src);
	            tgt = bf.getBoxObject.call(bf, tgt);
	            
	            connections.push({
	            	src: src,
	            	tgt: tgt
	            });
	            
	            if (tgt && tgt.sid == cobj.sid)
	            {
	            	if (src && (pname && src.name == pname || !pname))
	            	{
	            		prev.push(src);
	            	}
	            }
	        }
		}
		
		return prev;
	},
	
	l1/*initFlowMenu*/: function(fmenu, dt) {
		var me = this,
			i,
			mul,
			gap = 10,
			sx = gap,
			bf = me.bf;
			
		fmenu.empty();
		
		$.each(dt, function(n, item) {
			var dv,
				bg,
				i;
			
			if (!item.hidden)
			{
				dv = $("<div class='idv-flw-shp-a'><div>" + item.text + "</div></div>").appendTo(fmenu);
				dv.css({left: sx});
				
				if (item._cs && item._cs.length)
				{
					for (i=0; i < item._cs.length; i++)
					{
						dv.addClass(item._cs[i]);
					}
				}
				
				sx += gap + IG$/*mainapp*/.x_10/*jqueryExtension*/._w(dv);
				dv.draggable({
					opacity: 0.7, 
					helper: "clone",
					stop: function(event, ui) {
						var p = ui.position,
							farea = me.farea,
							mp = $(farea.parent()).offset(),
							mposition = farea.offset(),
							mw = IG$/*mainapp*/.x_10/*jqueryExtension*/._w(farea),
							mh = IG$/*mainapp*/.x_10/*jqueryExtension*/._h(farea);
						
						var nitem = new IG$/*mainapp*/._baN/*bflowNode*/();
						nitem.c1/*clone*/.call(nitem, item);
						nitem.w = 100;
						nitem.h = 28;
						
						mposition.top -= mp.top;
						mposition.left -= mp.left;
						
						p.w = nitem.w;
						p.h = nitem.h;
						
						if (mposition.left < p.left && p.left < mposition.left + mw 
							&& mposition.top < p.top && p.top < mposition.top + mh)
						{
							bf.addBox.call(bf, nitem, p, true);
						}
					}
				});
			}
		});
	},
	
	m1/*loadPGMX*/: function(c) {
		var me = this,
			req = new IG$/*mainapp*/._I3e/*requestServer*/();
		
		me.lv/*changeviewmode*/(0);
		
		req.init(me, 
			{
	            ack: "77",
	            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid, action: "load_pgmx"}, "uid;action"),
	            mbody: c
	        }, me, function(xdoc) {
	        	this.rs_M1/*rs_loadContent*/(xdoc);
	        }, false);
		req._l/*request*/();
	},
	
	np/*network_properties*/: function() {
		var me = this,
			dlg = new IG$/*mainapp*/.bA_1/*network_properties_dialog*/({
				itemobj: me.itemobj,
				callback: new IG$/*mainapp*/._I3d/*callBackObj*/(me, function(c) {
					me.updateNetwork("net_prop");
				})
			});
		
		dlg.show();
	},
	
	updateNetwork: function(ptype, data) {
		var me = this,
			itemobj = me.itemobj,
			instanceid = itemobj.instanceid,
			uid = itemobj.uid,
			req = new IG$/*mainapp*/._I3e/*requestServer*/(),
			payload,
			p2 = "uid;action;instanceid;actiontype",
			p1 = {
				uid: uid, 
				instanceid: instanceid, 
				action: "update_network", 
				actiontype: ptype
			};
			
		switch (ptype)
		{
		case "box_add":
			p2 += ";sid";
			p1.sid = data.item.sid;
			break;
		case "link_node":
			p2 += ";src;tgt";
			p1.src = data.item.src;
			p1.tgt = data.item.tgt;
			break;
		case "move_link":
			p2 += ";source_0;source_1;target_0;target_1";
			p1.source_0 = data.item.source_0;
			p1.source_1 = data.item.source_1;
			p1.target_0 = data.item.target_0;
			p1.target_1 = data.item.target_1;
			break;
		case "link_detach":
			p2 += ";src;tgt";
			p1.src = data.item.src;
			p1.tgt = data.item.tgt;
			break;
		}
			
		payload = IG$/*mainapp*/._I2d/*getItemAddress*/(p1, p2);
		
		req.init(me, 
			{
	            ack: "77",
	            payload: payload,
	            mbody: itemobj.L2/*getContent*/()
	        }, me, function(xdoc) {
	        	if (!instanceid)
        		{
	        		var tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/data"),
	        			new_inst = (tnode) ? IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "instanceid") : null;
	        			
	        		if (new_inst)
	        		{
	        			itemobj.instanceid = new_inst;
	        		}
        		}
	        }, false);
		req._l/*request*/();
	},
	
	setNewFinding: function(item) {
		var me = this,
			itemobj = me.itemobj,
			req = new IG$/*mainapp*/._I3e/*requestServer*/();
		
		req.init(me, 
			{
	            ack: "77",
	            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({
	            	uid: me.uid, 
	            	action: "set_new_finding", 
	            	instanceid: itemobj.instanceid
	            }, "uid;action;instanceid"),
	            mbody: IG$/*mainapp*/._I2e/*getItemOption*/({
	            	target: item.graphic.name,
	            	state: item.state.states.name
	            }) 
	        }, me, function(xdoc) {
	        	var me = this,
	        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/graphics");
	        		
	        	if (tnode)
	        	{
	        		me.paint(tnode, me.emode);
	        	}
	        }, false);
		req._l/*request*/();
	},
	
	_t$/*toolbarHandler*/: function(cmd) {
		var me = this,
			itemobj = me.itemobj,
			dlg;
		
		switch (cmd)
		{
		case "cmd_save":
			if (!me.uid)
			{
				me.fV7/*saveAsMetaContent*/(false);
			}
			else
			{
				me.fV6/*saveMetaContent*/(false);
			}
			break;
		case "cmd_saveas":
			me.fV7/*saveAsMetaContent*/(false);
			break;
		case "cmd_load_pgmx":
			dlg = new IG$/*mainapp*/._IB9c/*ml_bayes_pgmx*/({
				itemobj: me.itemobj,
				callback: new IG$/*mainapp*/._I3d/*callBackObj*/(me, function(c) {
					me.m1/*loadPGMX*/(c);
				})
			});
			dlg.show();
			break;
		case "cmd_net_prop":
			me.np/*network_properties*/();
			break;
		case "cmd_favorites":
			if (this.uid)
    		{
    			var req = new IG$/*mainapp*/._I3e/*requestServer*/();
    			req.init(me, 
					{
			            ack: "11",
			        	payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid}),
			            mbody: IG$/*mainapp*/._I2e/*getItemOption*/({option: 'addfavorites'})
			        }, me, me.rs_i1_2/*regFavorites*/, null, null);
				req._l/*request*/();
    		}
    		else
			{
				IG$/*mainapp*/._I54/*alertmsg*/(ig$/*appoption*/.appname, IRm$/*resources*/.r1('M_ERR_NSAVE'), null, me, 1, "error");
			}
			break;
		case "cmd_schedule":
			if (me.uid)
			{
				var mreq = [
					{
						ack: "3",
						payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid, option: "execute"}, "uid;option"),
						mbody: me.itemobj.L2/*getContent*/.call(me.itemobj),
						jobkey: "0"
					}
				]; 
				
				IG$/*mainapp*/._I50/*showScheduler*/(me, me.uid, me.itemtype, mreq, null);
			}
			break;
		case "cmd_net_mode":
			me.setNewWorkingMode();
			break;
		case "cmd_decision_tree":
			me.toggleDecisionTree();
			break;
		case "cmd_learn":
			var dlg = new IG$/*mainapp*/.bA_2/*learning_dialog*/({
				callback: new IG$/*mainapp*/._I3d/*callBackObj*/(me, function() {
					var mreq = new IG$/*mainapp*/._I3e/*requestServer*/();
	    			mreq.init(me, 
						{
							ack: "77",
							payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid, instanceid: itemobj.instanceid, action: "learn_network"}, "uid;action;instanceid"),
							mbody: IG$/*mainapp*/._I2e/*getItemOption*/() 
						}, me, function(xdoc) {
							
				        }
					); 
					mreq._l/*request*/();
				})
			});
			dlg.show();
			break;
		case "cmd_db_generator":
			var dlg = new IG$/*mainapp*/.bA_3/*db_generator*/({
				callback: new IG$/*mainapp*/._I3d/*callBackObj*/(me, function(param) {
					var mreq = new IG$/*mainapp*/._I3e/*requestServer*/();
	    			mreq.init(me, 
						{
							ack: "77",
							payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid, instanceid: itemobj.instanceid, action: "db_gen"}, "uid;action;instanceid"),
							mbody: IG$/*mainapp*/._I2e/*getItemOption*/(param) 
						}, me, function(xdoc) {
							var tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/item"),
								fpath, filename;
							
							if (tnode)
							{
								fpath = IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "luid");
								filename = IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "filename");
								
								if (fpath && filename)
								{
									$.download(ig$/*appoption*/.servlet, [
										{name: "ack", value: "35"},
										{name: "_mts_", value: IG$/*mainapp*/._g$a/*global_mts*/ || ""},
					    				{name: "payload", value: fpath},
					    				{name: "mbody", value: filename}
					    			], 'POST');
								}
							}
				        }
					); 
					mreq._l/*request*/();
				})
			});
			dlg.show();
			break;
		case "cmd_cost_effct_sen":
		case "cmd_cost_effct_det":
			var atype = (cmd == "cmd_cost_effct_sen" ? 0 : 1), 
				dlg = new IG$/*mainapp*/.bA_4/*cost_effectiveness*/({
				atype: atype, 
				uid: me.uid,
				itemobj: itemobj,
				width: atype == 0 ? 600 : 320,
				height: atype == 0 ? 550 : 300,
				callback: new IG$/*mainapp*/._I3d/*callBackObj*/(me, function(param) {
					
				})
			});
			dlg.show();
			break;
		}
	},
	
	toggleDecisionTree: function() {
		// INFERENCE_WORKING_MODE, EDITION_WORKING_MODE
		var me = this,
			req = new IG$/*mainapp*/._I3e/*requestServer*/(),
			itemobj = me.itemobj;
		
		if (itemobj && itemobj.instanceid)
		{
			me.lv/*changeviewmode*/(1);
			req.init(me, 
				{
		            ack: "77",
		            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid, action: "set_decision_tree", instanceid: itemobj.instanceid}, "uid;action;instanceid"),
		            mbody: IG$/*mainapp*/._I2e/*getItemOption*/() 
		        }, me, function(xdoc) {
		        	var me = this,
		        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/decision_tree");
		        		
		        	if (tnode)
		        	{
		        		me.paint_decision_tree(tnode);
		        	}
		        }, false);
			req._l/*request*/();
		}
	},
	
	paint_decision_tree: function(tnode) {
		var me = this,
			m_decision_tree = me.down("[name=m_decision_tree]"),
			eldom,
			mdom,
			dnd_viewer = me.dnd_viewer,
			tdata = {};
		
		if (!dnd_viewer)
		{
			eldom = $(m_decision_tree.el.dom);
			mdom = $("<div class='ba-decision-viewer'></div>").appendTo(eldom);
			mdom.width(eldom.width()).height(eldom.height());
			dnd_viewer = new node_tree_viewer(mdom);
			me.dnd_viewer = dnd_viewer;
		}
		
		tdata = me.build_decision_data(tnode);
		
		if (tdata && tdata.length > 0)
		{
			dnd_viewer.loadTreeData(tdata[0]);
		}
	},
	
	build_decision_data: function(tnode) {
		var me = this,
			tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnode),
			snode, snodes,
			i, j, p,
			ndname,
			tdata = [],
			tobj = {};
			
		for (i=0; i < tnodes.length; i++)
		{
			ndname = IG$/*mainapp*/._I29/*XGetNodeName*/(tnodes[i]);
			
			switch (ndname)
			{
			case "decision_branch":
			case "decision_treenode":
				tobj = {};
				snode = IG$/*mainapp*/._I19/*getSubNode*/(tnodes[i], "property");
				if (snode)
				{
					snodes = IG$/*mainapp*/._I26/*getChildNodes*/(snode);
					for (j=0; j < snodes.length; j++)
					{
						p = IG$/*mainapp*/._I1b/*XGetAttr*/(snodes[j], "name");
						tobj[p] = IG$/*mainapp*/._I24/*getTextContent*/(snodes[j]);
					}
					
					if (ndname == "decision_branch")
					{
						tobj.name = tobj.branch_state || "top";
					}
					else
					{
						tobj.name = tobj.nodename;
					}
				}
				snode = IG$/*mainapp*/._I19/*getSubNode*/(tnodes[i], "children");
				if (snode)
				{
					tobj.children = me.build_decision_data(snode);
				}
				tdata.push(tobj);
				break;
			}
		}
		
		return tdata;
	},
	
	setNewWorkingMode: function() {
		// INFERENCE_WORKING_MODE, EDITION_WORKING_MODE
		var me = this,
			req = new IG$/*mainapp*/._I3e/*requestServer*/(),
			itemobj = me.itemobj,
			emode = me.emode ? 0 : 1;
			
		me.emode = emode;
		
		if (itemobj && itemobj.instanceid)
		{
			me.lv/*changeviewmode*/(0);
			
			if (me.emode)
			{
				req.init(me, 
					{
			            ack: "77",
			            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: me.uid, action: "set_working_mode", instanceid: itemobj.instanceid}, "uid;action;instanceid"),
			            mbody: IG$/*mainapp*/._I2e/*getItemOption*/() 
			        }, me, function(xdoc) {
			        	var me = this,
			        		tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/graphics");
			        		
			        	if (tnode)
			        	{
			        		me.paint(tnode, me.emode);
			        	}
			        }, false);
				req._l/*request*/();
			}
			else
			{
				me.paint(null, me.emode);
			}
		}
	},
	
	paint: function(gnode, emode) {
		var me = this,
			tnode = gnode ? IG$/*mainapp*/._I18/*XGetNode*/(gnode, "visualnodes") : null,
			tnodes,
			p, pn, vn, nd,
			g = {},
			i, j, k, vns, vstat, v1, v2, v3, v4,
			itemobj = me.itemobj;
			
		if (tnode)
		{
			tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnode);
			
			for (i=0; i < tnodes.length; i++)
			{
				pn = tnodes[i];
				p = {
					name: IG$/*mainapp*/._I1b/*XGetAttr*/(pn, "name"),
					visualstates: []
				};
				
				vn = IG$/*mainapp*/._I18/*XGetNode*/(pn, "innerbox/visualstates");
				
				if (vn)
				{
					vns = IG$/*mainapp*/._I26/*getChildNodes*/(vn);
					
					for (j=0; j < vns.length; j++)
					{
						v1 = vns[j];
						vstat = IG$/*mainapp*/._I1c/*XGetAttrProp*/(v1);
						vstat.position = parseInt(vstat.position);
						vstat.ispropagationactive = vstat.ispropagationactive == "T";
						vstat.values = [];
						
						v2 = IG$/*mainapp*/._I18/*XGetNode*/(v1, "values");
						
						if (v2)
						{
							v3 = IG$/*mainapp*/._I26/*getChildNodes*/(v2);
							
							for (k=0; k < v3.length; k++)
							{
								v4 = v3[k];
								
								vstat.values.push({
									barlength: IG$/*mainapp*/._I1a/*getSubNodeText*/(v4, "barlength"),
									formatvalue: IG$/*mainapp*/._I1a/*getSubNodeText*/(v4, "formatvalue")
								});
							}
						}
						
						p.visualstates.push(vstat);
					}
				}
				
				g[p.name] = p;
			}
		}
		
		$.each(itemobj.nodes, function(i, nd) {
			nd.paint.call(nd, g[nd.text], emode);
		});
		
		me.bf.plumb.repaintEverything();
	},
	
	rs_i1_2/*regFavorites*/: function(xdoc) {
    	IG$/*mainapp*/._I54/*alertmsg*/(ig$/*appoption*/.appname, IRm$/*resources*/.r1("M_SAVED_FAV"), null, null, 0, "success");
    },
    
    ll4/*getStatus*/: function() {
    	var me = this;
    	
    	if (me.stimer)
		{
			clearTimeout(me.stimer);
		}
		
		me.stimer = setTimeout(function() {
			me.ll3/*getRunStatus*/.call(me);
		}, me.sgap);
    },
    
    rs_lllE/*runBusinessError*/: function(xdoc) {
    	var panel = this,
    		r = true,
    		errcode = IG$/*mainapp*/._I27/*getErrorCode*/(xdoc);
    	
    	if(errcode == "0x28a0")
    	{
    		r = false;
    		
    		var tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg"),
    			sid = IG$/*mainapp*/._I24/*getTextContent*/(tnode);
    			
    		if (sid)
    		{
    			IG$/*mainapp*/._I55/*confirmMessages*/(ig$/*appoption*/.appname, "Already workflow is running on server. Would you like to stop service?", function(e) {
	    			if (e == "yes")
	    			{
	    				var req = new IG$/*mainapp*/._I3e/*requestServer*/();
	    				req.init(panel, 
							{
				                ack: "3",
					            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: panel.uid, sid: sid, option: "cancelWorkflow"}, "uid;sid;option"),
					            mbody: IG$/*mainapp*/._I2e/*getItemOption*/()
				            }, panel, panel.rs_ll2/*cancelWorkflow*/, null);
					    req._l/*request*/();
	    			}
	    		}, panel, panel);
    		}
    	}
    	
    	return r;
    },
    
    rs_ll2/*cancelWorkflow*/: function(xdoc) {
    	var me = this;
    	me.lll/*runBusiness*/();
    },
	
	fV6/*saveMetaContent*/: function(afterclose) {
		var panel = this,
			contentxml = panel.itemobj.L2/*getContent*/.call(panel.itemobj);
    	var req = new IG$/*mainapp*/._I3e/*requestServer*/();
    	req.init(panel, 
			{
	            ack: "31",
	            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: panel.uid}),
	            mbody: contentxml
	        }, panel, panel.rs_fV6/*saveMetaContent*/, null, [afterclose]);
		req._l/*request*/();
	},
	
	rs_fV6/*saveMetaContent*/: function(xdoc, opt) {
		var me = this,
			afterclose = (opt ? opt[0] : false);
    	if (afterclose == true)
    	{
    		me.close();
    	}
    	else
    	{
    		me._ILb_/*contentchanged*/ = false;
    		IG$/*mainapp*/._I54/*alertmsg*/(ig$/*appoption*/.appname, IRm$/*resources*/.r1("M_SAVED"), null, null, 0, "success");
    	}
	},
	
	fV7/*saveAsMetaContent*/: function(afterclose) {
		var me = this,
			dlgitemsel = new IG$/*mainapp*/._I96/*metaSelectDlg*/({
	    		mode: "newitem",
	    		initpath: me.nodepath,
	    		callback: new IG$/*mainapp*/._I3d/*callBackObj*/(me, me.fV8/*saveNewMetaContent*/, afterclose)
	    	});
		IG$/*mainapp*/._I_5/*checkLogin*/(me, dlgitemsel);
	},
	
	fV8/*saveNewMetaContent*/: function(item, afterclose) {
		var panel = this,
    		contentxml = panel.itemobj.L2/*getContent*/.call(panel.itemobj),
    		req = new IG$/*mainapp*/._I3e/*requestServer*/();
    	
		req.init(panel, 
			{
                ack: "31",
	            payload: "<smsg><item address='" + item.nodepath + "/" + item.name + "' name='" + item.name + "' type='" + (this.itemtype) + "' pid='" + item.uid + "' description=''/></smsg>",
	            mbody: contentxml
            }, panel, panel._IO5/*rs_processMakeMetaItem*/, panel._IO6/*rs_processMakeMetaItem*/, [item.name, afterclose, item.nodepath, item.uid, contentxml]);
       	req.showerror = false;
	    req._l/*request*/();
	},
	
	_IO5/*rs_processMakeMetaItem*/: function(xdoc, opt) {
		var me = this,
			itemobj = me.itemobj,
			pnode;
			
		pnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/item");
		
		if (pnode)
		{
			itemobj.item = IG$/*mainapp*/._I1c/*XGetAttrProp*/(pnode);
			me.setTitle(itemobj.item.name);
			me.uid = itemobj.item.uid;
		}
	},
	
	_IO6/*rs_processMakeMetaItem*/: function(xdoc, opt) {
    	var panel = this,
    		itemname = opt[0],
    		afterclose = opt[1],
    		nodepath = opt[2],
    		pitemuid = opt[3],
    		contentxml = opt[4],
    		errcode = IG$/*mainapp*/._I27/*getErrorCode*/(xdoc);
    	
    	if (errcode == "0x12e0")
    	{
    		IG$/*mainapp*/._I55/*confirmMessages*/(ig$/*appoption*/.appname, itemname + " already exist on the server. Would you overwrite existing item with this copy?", function(e) {
    			if (e == "yes")
    			{
    				var req = new IG$/*mainapp*/._I3e/*requestServer*/();
    				req.init(panel, 
						{
			                ack: "31",
				            payload: "<smsg><item address='" + nodepath + "/" + itemname + "' name='" + itemname + "' type='" + (this.itemtype ? this.itemtype : 'Report') + "' pid='" + pitemuid + "' description='' overwrite='T'/></smsg>",
				            mbody: contentxml
			            }, panel, panel._IO5/*rs_processMakeMetaItem*/, null, [itemname, afterclose, nodepath]);
				    req._l/*request*/();
    			}
    		}, panel, panel);
    	}
    	else
    	{
    		IG$/*mainapp*/._I51/*ShowErrorMessage*/(xdoc, panel);
    	}
    },
    
    lv/*changeviewmode*/: function(mode) {
    	var me = this,
    		m_main_card = me.m_main_card;
    		
    	m_main_card.getLayout().setActiveItem(mode);
    },
	
	initComponent: function() {
		var me = this;
		
		Ext.apply(this, {
			items: [
				{
					xtype: "panel",
					layout: "card",
					name: "m_main_card",
					items: [
						{
							xtype: "panel",
							layout: {
								type: "vbox",
								align: "stretch"
							},
							items: [
								{
									xtype: "container",
									name: "topmenu",
									height: 40,
									border: 1,
									style: {
									    borderColor: '#efefef',
									    borderStyle: 'solid'
									}
								},
								{
									xtype: "container",
									flex: 1,
									layout: "border",
									items: [
										{
											xtype: "container",
											name: "mainarea",
											region: "center",
											flex: 1
										},
										{
											xtype: "panel",
											name: "moption",
											title: "Properties",
											split: true,
											collapsible: true,
											collapsed: true,
											collapseMode: "mini",
											region: "east",
											width: 420,
											layout: "fit",
											items: [
												{
													xtype: "displayfield",
													value: "Click Item to set properties"
												}
											]
										}
									]
								}
							]
						},
						{
							xtype: "container",
							name: "m_decision_tree",
							listeners: {
								resize: function(tobj, w, h) {
									var me = this,
										dnd_viewer = me.dnd_viewer;
										
									if (dnd_viewer)
									{
										dnd_viewer.setSize.call(dnd_viewer, w, h);
									}
								},
								scope: this
							}
						}
					]
				}
			],
			tbar: [
				{
			    	iconCls: 'icon-toolbar-save',
			    	name: "t_save",
	            	tooltip: IRm$/*resources*/.r1('L_SAVE_CONTENT'),
	            	handler: function() {
			    		this._t$/*toolbarHandler*/('cmd_save'); 
			    	},
	            	scope: this
			    },
			    {
		        	iconCls: 'icon-toolbar-saveas',
		        	name: "t_save_as",
		        	tooltip: IRm$/*resources*/.r1('L_SAVE_CONTENTAS'),
		        	handler: function() {
			    		this._t$/*toolbarHandler*/('cmd_saveas'); 
			    	},
		        	scope: this
		        },
		        {
		        	text: "NetProp",
		        	handler: function() {
		        		this._t$/*toolbarHandler*/("cmd_net_prop");
		        	},
		        	scope: this
		        },
		        {
		        	text: IRm$/*resources*/.r1("L_ML_C7"),
		        	handler: function() {
		        		this._t$/*toolbarHandler*/('cmd_load_pgmx'); 
		        	},
		        	scope: this
		        },
		        "-",
		        {
		        	text: IRm$/*resources*/.r1("L_ML_C4a"),
		        	handler: function() {
		        		this._t$/*toolbarHandler*/('cmd_net_mode'); 
		        	},
		        	scope: this
		        },
		        {
		        	text: IRm$/*resources*/.r1("L_ML_C5"),
		        	handler: function() {
		        		this._t$/*toolbarHandler*/('cmd_decision_tree'); 
		        	},
		        	scope: this
		        },
		        "-",
		        {
		        	text: IRm$/*resources*/.r1("L_ML_C6"),
		        	handler: function() {
		        		this._t$/*toolbarHandler*/('cmd_cases'); 
		        	},
		        	scope: this
		        },
		        {
		        	text: IRm$/*resources*/.r1("L_ML_C6a"),
		        	handler: function() {
		        		this._t$/*toolbarHandler*/('cmd_cases_new'); 
		        	},
		        	scope: this
		        },
		        {
		        	text: IRm$/*resources*/.r1("L_ML_C6b"),
		        	handler: function() {
		        		this._t$/*toolbarHandler*/('cmd_cases_rm'); 
		        	},
		        	scope: this
		        },
		        "-",
		        {
		        	text: IRm$/*resources*/.r1("L_ML_LEARN"),
		        	handler: function() {
		        		this._t$/*toolbarHandler*/('cmd_learn'); 
		        	},
		        	scope: this
		        },
		        {
		        	text: "Tools",
		        	menu: new Ext.menu.Menu({
						items: [
							{
								text: "Database generator",
								handler: function() {
									this._t$/*toolbarHandler*/('cmd_db_generator'); 
								},
								scope: this
							},
							{
					        	text: IRm$/*resources*/.r1("L_ML_CEFF_S"),
					        	handler: function() {
					        		this._t$/*toolbarHandler*/('cmd_cost_effct_sen'); 
					        	},
					        	scope: this
					        },
					        {
					        	text: IRm$/*resources*/.r1("L_ML_CEFF_D"),
					        	handler: function() {
					        		this._t$/*toolbarHandler*/('cmd_cost_effct_det'); 
					        	},
					        	scope: this
					        }
						]
					})
		        },
		        "->",
		        {
					iconCls: "icon-toolbar-schedule",
					cls: "ig_r_sch",
					tooltip: IRm$/*resources*/.r1('B_SCHEDULE'),
					hidden: ig$/*appoption*/.features && !ig$/*appoption*/.features.enable_scheduler ? true : false,
					handler: function() {
						this._t$/*toolbarHandler*/.call(this, 'cmd_schedule');
					},
					scope: this
				},
		        {
		        	iconCls: "icon-toolbar-favorites",
		        	tooltip: IRm$/*resources*/.r1('B_FAVORITES'),
		        	handler: function() {
		        		this._t$/*toolbarHandler*/.call(this, 'cmd_favorites');
		        	},
		        	scope: this
		        }
			]
	    });
	          
		IG$/*mainapp*/._IB9b/*ml_bayes*/.superclass.initComponent.call(this);
	},
	
	listeners: {
		afterrender: function(tobj) {
			var me = this;
			
			me.m_main_card = me.down("[name=m_main_card]");
			
			me.topmenu = $(me.down("[name=topmenu]").el.dom);
			me.mainarea = $(me.down("[name=mainarea]").el.dom);
			
			me._IFd/*init_f*/();
		},
		beforeclose: function(panel, opts) {
			if (panel.itemobj && panel.itemobj.instanceid)
			{
				var lreq = new IG$/*mainapp*/._I3e/*requestServer*/();
				lreq.init(panel,
					{
						ack: "77",
						payload: IG$/*mainapp*/._I2d/*getItemAddress*/({uid: panel.uid, action: "ref_down", instanceid: panel.itemobj.instanceid}, "uid;action;instanceid"),
						mbody: IG$/*mainapp*/._I2e/*getItemOption*/()
					}, panel, function(xdoc) {
	
					}, false);
	
				lreq._l/*request*/();
			}
		}
	}
});

