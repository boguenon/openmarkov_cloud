IG$/*mainapp*/._baN/*bflowNode*/ = function(tnode) {
	if (tnode)
	{
		this.p1/*parseXML*/(tnode);
	}
}

IG$/*mainapp*/._baN/*bflowNode*/.prototype = {
	p1/*parseXML*/: function(tnode) {
		var node = this,
			j, lnode,
			pnodes,
			lname, lvalue;
		
		IG$/*mainapp*/._I1f/*XGetInfo*/(node, tnode, "name;shape;sid;type;role", "s");
		IG$/*mainapp*/._I1f/*XGetInfo*/(node, tnode, "x;y;w;h", "i");
		lnode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "label");
		node.text = (lnode) ? IG$/*mainapp*/._I24/*getTextContent*/(lnode) : node.name;
		lnode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "property");
		if (lnode)
		{
			IG$/*mainapp*/._I1f/*XGetInfo*/(node, lnode, "dsname;dsignoreerror", "s");
			pnodes = IG$/*mainapp*/._I26/*getChildNodes*/(lnode);
			for (j=0; j < pnodes.length; j++)
			{
				node[IG$/*mainapp*/._I29/*XGetNodeName*/(pnodes[j])] = IG$/*mainapp*/._I24/*getTextContent*/(pnodes[j]);
			}
		}
		
		lnode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "comment");
		
		if (lnode)
		{
			node.comment = IG$/*mainapp*/._I24/*getTextContent*/(lnode);
		}
		
		lnode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "additional_properties");
		if (lnode)
		{
			node.additional_properties = [];
			pnodes = IG$/*mainapp*/._I26/*getChildNodes*/(lnode);
			
			for (j=0; j < pnodes.length; j++)
			{
				node.additional_properties.push({
					name: IG$/*mainapp*/._I1b/*XGetAttr*/(pnodes[j], "name"),
					value: IG$/*mainapp*/._I1b/*XGetAttr*/(pnodes[j], "value")
				});
			}
		}
		lnode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "states");
		if (lnode)
		{
			node.states = [];
			pnodes = IG$/*mainapp*/._I26/*getChildNodes*/(lnode);
			
			for (j=0; j < pnodes.length; j++)
			{
				node.states.push({
					name: IG$/*mainapp*/._I1b/*XGetAttr*/(pnodes[j], "name")
				});
			}
		}
		lnode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "precision");
		if (lnode)
		{
			node.precision = IG$/*mainapp*/._I24/*getTextContent*/(lnode);
		}
		lnode = IG$/*mainapp*/._I19/*getSubNode*/(tnode, "unit");
		if (lnode)
		{
			node.unit = {};
			pnodes = IG$/*mainapp*/._I26/*getChildNodes*/(lnode);
			
			for (j=0; j < pnodes.length; j++)
			{
				lname = IG$/*mainapp*/._I1b/*XGetAttr*/(pnodes[j], "name");
				lvalue = IG$/*mainapp*/._I24/*getTextContent*/(pnodes[j]);
				node.unit[lname] = lvalue;
			}
		}
	},
	
	c1/*clone*/: function(tobj) {
		var me = this,
			p = "name;shape;x;y;w;h;text;dsname;dsignoreerror;comment".split(";"),
			i, l;
		
		for (i=0; i < p.length; i++)
		{
			me[p[i]] = tobj[p[i]];
		}
		
		if (tobj._cs)
		{
			me._cs = [];
			for (i=0; i < tobj._cs.length; i++)
			{
				me._cs.push(tobj._cs[i]);
			}
		}
		
		if (tobj.additional_properties)
		{
			l = tobj.additional_properties;
			me.additional_properties = [];
			for (i=0; i < l.length; i++)
			{
				me.additional_properties.push({
					name: l[i].name,
					value: l[i].value
				});
			}
		}
		
		if (tobj.states)
		{
			l = tobj.states;
			
			me.states = [];
			
			for (i=0; i < l.length; i++)
			{
				me.states.push({
					name: l[i].name
				});
			}
		}
		
		me.precision = tobj.precision;
		
		if (tobj.unit)
		{
			me.unit = {};
			
			for (l in tobj.unit)
			{
				me.unit[l] = tobj.unit[l];
			}
		}
	},
	
	paint: function(g, emode) {
		var me = this,
			box = me.box,
			dg = $(".idv-flw-grp", box),
			dul,
			visualstates = g ? g.visualstates : null,
			mw = 102;
			
		dg.hide();
		dg.empty();
		
		if (visualstates && emode == 1)
		{
			box.width(150);
			dg.show();
			
			dul = $("<ul class='idv-visual-lst'></ul>").appendTo(dg);
			
			$.each(visualstates, function(n, states) {
				var values = states.values,
					i,
					statview,
					sviewarea,
					sname,
					sgraph;
				
				statview = $("<li class='idv-visual-states'></li>").appendTo(dul);
				sviewarea = $("<div class='idv-visual-area'></div>").appendTo(statview);
				sname = $("<div class='idv-visual-statname'><span id='statname'></span></div>").appendTo(sviewarea);
				
				$("#statname", sname).text(states.name);

				if (values && values.length)
				{
					$.each(values, function(i, value) {
						var sgraph = $("<div class='idv-visual-graph'><div class='idv-visual-graphval'></div></div>").appendTo(sviewarea);
						$(".idv-visual-graphval", sgraph).width(Number(value.barlength));
						
						sgraph.bind("click", function() {
							me.visualStateClick.call(me, g, {
								states: states,
								value: value
							});
						});
					});
				}
			});
		}
		else
		{
			box.width(100);
		}
	},
	
	visualStateClick: function(g, value) {
		var me = this;
		me._bf.container.trigger("visual_state_click", {
			graphic: g,
			state: value
		});
	}
}

IG$/*mainapp*/._I99/*bflowoption*/ = {
	hoverPaintStyle: { strokeStyle:"#7ec3d9" }
};

IG$/*mainapp*/._I9a/*flowDiagram*/ = function(container) {
	this.container = container;
}

IG$/*mainapp*/._I9a/*flowDiagram*/.prototype = {
	init: function() {
		var me = this,
			container = me.container,
			doc = $(document),
			mainarea;
		
		me.mwidth = 0;
		me.mheight = 0;
		
		container.empty();
		
		me.sid = 0;
		me.a_boxes = [];
		me.m_boxes = {};
		
		me.mainarea = mainarea = $("<div class='idv-flw-mreg'></div>").appendTo(container);
		
		me.render = $("<div id='render'></div>").appendTo(mainarea);
		
		me.plumb = jsPlumb.getInstance({
			Endpoint: ["Dot", {radius: 5}],
			HoverPaintStyle: {strokeStyle: "#42a62c", lineWidth: 2},
			ConnectionOverlays: [
				["Arrow", {
					location: 1,
					id: "arrow",
					length: 14,
					foldback: 0.8
				}],
				["Label", {label: "", id: "label"}]
			]
		});
		
		me.plumb.bind("connection", function(info, e) {
			var lnk = info.connection,
				src = lnk.source,
				tgt = lnk.target;
            
            src = me.getBoxObject.call(me, src);
            tgt = me.getBoxObject.call(me, tgt);
            
            if (src && tgt && e) 
            {
            	me.container.trigger("flow_changed", {
    				item: {
    					src: src.sid,
    					tgt: tgt.sid
    				},
    				source: "link_node"
    			});
            }
		});
		
		me.plumb.bind("connectionDetached", function(info, e) {
			var lnk = info.connection,
				src = lnk.source,
				tgt = lnk.target;
	        
	        src = me.getBoxObject.call(me, src);
	        tgt = me.getBoxObject.call(me, tgt);
	        
	        if (src && tgt && e) 
	        {
	        	me.container.trigger("flow_changed", {
					item: {
						src: src.sid,
						tgt: tgt.sid
					},
					source: "link_detach"
				});
	        }
		});
		
		me.plumb.bind("connectionMoved", function(info, e) {
			var s1 = info.originalSourceId,
				s2 = info.newSourceId,
				t1 = info.originalTargetId,
				t2 = info.newTargetId;
			
			s1 = me.getBoxObject.call(me, s1);
			s2 = me.getBoxObject.call(me, s2);
			t1 = me.getBoxObject.call(me, t1);
			t2 = me.getBoxObject.call(me, t2);
			
			if (s1 && s2 && t1 && t2 && e)
			{
				me.container.trigger("flow_changed", {
					item: {
						source_0: s1.sid,
						source_1: s2.sid,
						target_0: t1.sid,
						target_1: t2.sid
					},
					source: "move_link"
				});
			}
		});
				
		var gpt;
		
		var gf_mousemove = function(e) {
			if (gpt)
			{
				var mpt = {
					x: e.pageX,
					y: e.pageY
				}, tx = 0, ty = 0,
				tw = IG$/*mainapp*/.x_10/*jqueryExtension*/._w(me.container),
				th = IG$/*mainapp*/.x_10/*jqueryExtension*/._h(me.container),
				mw = IG$/*mainapp*/.x_10/*jqueryExtension*/._w(me.mainarea),
				mh = IG$/*mainapp*/.x_10/*jqueryExtension*/._h(me.mainarea),
				om = gpt.om,
				ot = me.container.offset(),
				gx = om.left - ot.left,
				gy = om.top - ot.top,
				sx, sy;
				
				if (mw > tw || mh > th)
				{
					tx = mpt.x - gpt.x;
					ty = mpt.y - gpt.y;
					
					sy = gy + ty;
					sx = gx + tx;
										
					if (sy < (th - mh))
					{
						sy = th - mh;
					}
					else if (sy > 0)
					{
						sy = 0;
					}
					
					if (sx < (tw - mw))
					{
						sx = tw - mw;
					}
					else if (sx > 0)
					{
						sx = 0;
					}
					
					me.mainarea.css({top: sy, left: sx});
				}
			}
		};
		
		var gf_mouseup = function(e) {
			doc.unbind("mousemove", gf_mousemove);
			doc.unbind("mouseup", gf_mouseup);
		};
		
		mainarea.bind({
			mousedown: function(e) {
				if (e.srcElement && e.srcElement == mainarea[0])
				{
					gpt = {
						x: e.pageX,
						y: e.pageY,
						om: me.mainarea.offset()
					};
					doc.bind("mousemove", gf_mousemove);
					doc.bind("mouseup", gf_mouseup);
				}
			},
			mouseup: function(e) {
				doc.unbind("mousemove", gf_mousemove);
				doc.unbind("mouseup", gf_mouseup);
			}
		});
	},
	
	getID: function(sid) {
		var me = this;
		
		if (!sid)
		{
			sid = "uid_" + me.sid++;
		}
		
		while (me.m_boxes[sid]) {
			sid = "uid_" + me.sid++;
		}
		
		return sid;
	},
	
	s1/*setStatus*/: function(stat) {
		var me = this,
			item = me.m_boxes[stat.lid];
			
		if (item && item.statdv)
		{
			item.statdv.removeClass("idv-flw-shp mec-stat-saved");
			item.statdv.removeClass("idv-flw-shp mec-stat-on");
			item.statdv.removeClass("idv-flw-shp mec-stat-err");
			
			switch (stat.status)
			{
			case 1:
				item.statdv.addClass("idv-flw-shp mec-stat-on");
				break;
			case 10:
				item.statdv.addClass("idv-flw-shp mec-stat-saved");
				break;
			default:
				item.statdv.addClass("idv-flw-shp mec-stat-err");
				break;
			}
		}
	},
	
	s2/*clearStatus*/: function() {
		var me = this,
			a_boxes = me.a_boxes,
			i;
			
		for (i=0; i < a_boxes.length; i++)
		{
			if (a_boxes[i].statdv)
			{
				a_boxes[i].statdv.removeClass("idv-flw-shp mec-stat-saved");
				a_boxes[i].statdv.removeClass("idv-flw-shp mec-stat-on");
				a_boxes[i].statdv.removeClass("idv-flw-shp mec-stat-err");
			}
		}
	},
	
	addBox: function(item, p, isnew) {
		var me = this;
		me.a_boxes.push(item);
		
		if (IG$/*mainapp*/._I07/*checkUID*/(item.sid) == false)
		{
			var me = this,
				lreq = new IG$/*mainapp*/._I3e/*requestServer*/();
			lreq.init(me, 
				{
		            ack: "11",
		            payload: IG$/*mainapp*/._I2d/*getItemAddress*/({}),
		            mbody: IG$/*mainapp*/._I2e/*getItemOption*/({option: "newid"})
				}, me, function(xdoc) {
					var tnode = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/item"),
						suid = (tnode ? IG$/*mainapp*/._I1b/*XGetAttr*/(tnode, "uid") : null);
					
					if (suid)
					{
						item.sid = suid;
						me.rs_addBox.call(me, item, p, isnew);
					}
				}, false);
				
			lreq._l/*request*/();
		}
		else
		{
			this.rs_addBox(item, p, isnew);
		}
	},
	
	rs_addBox: function(item, p, isnew) {
		var me = this,
			dv, dvc, ep, title, dp, i,
			dg;
		
		dv = $("<div class='idv-flw-shp'></div>").appendTo(me.mainarea);
		dv.css({left: p.left, top: p.top});
		if (p.w && p.h)
		{
			dv.width(p.w);
			// dv.height(p.h);
		}
		
		if (item._cs && item._cs.length)
		{
			for (i=0; i < item._cs.length; i++)
			{
				dv.addClass(item._cs[i]);
			}
		}
		
		bg = $("#bgimg", dv);
		IG$/*mainapp*/.x_10/*jqueryExtension*/._w(bg, IG$/*mainapp*/.x_10/*jqueryExtension*/._w(dv));
		IG$/*mainapp*/.x_10/*jqueryExtension*/._h(bg, IG$/*mainapp*/.x_10/*jqueryExtension*/._h(dv));
		item.box = dv;
		item._bf = me;
		item.dv_dsname = $("<div class='idv-bd-dsnm'>" + (item.dsname || "") + "</div>").appendTo(dv);
		
		dvc = $("<div class='ba_m_title_c'></div>").appendTo(dv);
		title = $("<span class='ba_m_title' title='" + item.text + "'>" + item.text + "</span>").appendTo(dvc);
		$("<div class='mec-ep'></div>").appendTo(dv);
		dp = $("<div class='mec-rp'></div>").appendTo(dv);
		
		item.statdv = $("<div class='mec-stat'></div>").appendTo(dv);
		item.sid = item.sid || me.getID();
		
		item.__tL/*titleArea*/ = title;
		
		title.bind("click", function() {
			me.container.trigger("boxClicked", item);
		});
		
		dg = $("<div class='idv-flw-grp'></div>").appendTo(dv).hide();
		
		me.plumb.draggable(dv);
		
		me.plumb.makeSource(dv, {
			filter: ".mec-ep",
			anchor: "Continuous",
			connector: ["StateMachine", {curviness: 20}],
			connectorStyle: {strokeStyle: "#efefef", lineWidth: 2},
			maxConnections: 30,
			onMaxConnections: function(info, e) {
			}
		});
		
		me.plumb.makeTarget(dv, {
			dropOptions: {hoverClass: "dragHover"},
			anchor: "Continuous",
			allowLoopback: false
		});
		
		me.m_boxes[item.sid] = item;
		
		dp.bind("click", function() {
			me.removeBox.call(me, item, true);
		});
		
		if (isnew)
		{
			me.container.trigger("flow_changed", {
				item: item,
				source: "box_add"
			});
		}
		
		me.u1/*updateSize*/();
	},
	
	s4/*setTitle*/: function(item, txt) {
		var me = this,
			titlearea = item.__tL/*titleArea*/;
		
		titlearea.text(txt);
		titlearea.attr("title", txt);
	},
	
	removeBox: function(item, bconfirm) {
		var me = this;
			
		if (bconfirm == true)
		{
			IG$/*mainapp*/._I55/*confirmMessages*/(ig$/*appoption*/.appname, "Confirm to delete node!", function(e) {
				if (e == "yes")
				{
					me.rs_removeBox.call(me, item);
				}
			});
		}
		else
		{
			me.rs_removeBox.call(me, item);
		}
	},
	
	rs_removeBox: function(item) {
		var me = this,
			i;
			
		me.plumb.detachAllConnections(item.box);
		item.box.remove();
		
		for (i=0; i < me.a_boxes.length; i++)
		{
			if (me.a_boxes[i].sid == item.sid)
			{
				me.a_boxes.splice(i, 1);
				break;
			}
		}
		delete me.m_boxes[item.sid];
		
		me.u1/*updateSize*/();
	},
	
	u1/*updateSize*/: function() {
		var me = this, 
			mwidth = 0,
			mheight = 0,
			twidth = IG$/*mainapp*/.x_10/*jqueryExtension*/._w(me.container),
			theight = IG$/*mainapp*/.x_10/*jqueryExtension*/._h(me.container),
			i, boxes = me.a_boxes;
			
		for (i=0; i < boxes.length; i++)
		{
			mwidth = Math.max(mwidth, boxes[i].x + boxes[i].w);
			mheight = Math.max(mheight, boxes[i].y + boxes[i].h);
		}
		
		mwidth = Math.max(mwidth+20, twidth);
		mheight = Math.max(mheight+20, theight);
		
		IG$/*mainapp*/.x_10/*jqueryExtension*/._w(me.mainarea, mwidth);
		IG$/*mainapp*/.x_10/*jqueryExtension*/._h(me.mainarea, mheight);
	},
	
	addLink: function(lnk) {
		var me = this,
            did1 = me.m_boxes[lnk.from] ? me.m_boxes[lnk.from].box : null,
            did2 = me.m_boxes[lnk.to] ? me.m_boxes[lnk.to].box : null,
            stroke = "rgb(189,11,11)";
            
        if (!did1 || !did2)
        	return;
            
        var mlink = me.plumb.connect(
            {
                source: did1, 
                target: did2, 
                paintStyle:{ 
                    lineWidth:8,
                    strokeStyle: stroke,
                    outlineColor:"#666",
                    outlineWidth:1,
                    dashstyle: null
                },
                detachable: true,
                hoverPaintStyle: IG$/*mainapp*/._I99/*bflowoption*/.hoverPaintStyle, 
                anchors:[ [ 0.3 , 1, 0, 1 ], "TopCenter" ], 
                overlays: [
                    ["DoubleLine", { fillStyle: "#09098e", width: 15, length: 15 } ]
//                     [
//                     	"Custom", {
//                     		create: function(component) {
//                     			return $("<div><div class='ba-single-line'></div></div>");
//                     		},
//                     		location: 0.5
//                     	}
//                    ]
                ],
                endpoint: [
                	"Dot",
	                {
	                	radius: 5
	                }
                ],
                endpointStyles: {fillStyle: "#456"}
            }
        );
        mlink.ptr_link = lnk;
	},
	
	L1/*loadOption*/: function(itemobj) {
		var me = this,
			node, lnk,
			i;
			
		me.itemobj = itemobj;
		
		me.init();
		
		$.each(itemobj.nodes, function(i, node) {
			me.addBox(node, {
				left: node.x,
				top: node.y,
				w: node.w,
				h: node.h
			});
		});
		
		for (i=0; i < itemobj.links.length; i++)
		{
			lnk = itemobj.links[i];
			me.addLink(lnk);
		}
	},
	
	m1/*appProperty*/: function(r, box, params) {
		var i,
			pnames = params.split(";");
		
		for (i=0; i < pnames.length; i++)
		{
			if (box[pnames[i]])
			{
				r.push("<" + pnames[i] + "><![CDATA[" + (box[pnames[i]] || "") + "]]></" + pnames[i] + ">");
			}
		}
	},
	
	l2/*exportContent*/: function(mtype, mtypename) {
		var me = this,
			r = [],
			i, j, l, o, box, ab, lnk, ptr_link,
			toffset = me.mainarea.offset();
		
		r.push("<nodes");
		
		if (mtype && mtypename)
		{
			r.push(" type='" + mtype + "' typename='" + mtypename + "'");
		}
		
		r.push(">");
		
		for (i=0; i < me.a_boxes.length; i++)
		{
			ab = me.a_boxes[i];
			o = ab.box.offset();
            box = {
                name: ab.name,
                shape: ab.shape || "",
                x: o.left - toffset.left,
                y: o.top - toffset.top,
                w: IG$/*mainapp*/.x_10/*jqueryExtension*/._w(ab.box),
                h: IG$/*mainapp*/.x_10/*jqueryExtension*/._h(ab.box),
                sid: ab.sid,
                text: ab.text,
                dsname: ab.dsname,
                dsdesc: ab.dsdesc,
                dstype: ab.dstype,
                dsignoreerror: ab.dsignoreerror,
                dsmapper: ab.dsmapper,
                dsreducer: ab.dsreducer,
                dstname: ab.dstname,
                dsscript: ab.dsscript,
                dsfname: ab.dsfname,
                dsskipexist: ab.dsskipexist,
                doctmpl: ab.doctmpl,
                type: ab.type,
                role: ab.role,
                additional_properties: ab.additional_properties,
                states: ab.states,
                precision: ab.precision,
                unit: ab.unit,
                comment: ab.comment
            };
            
            r.push("<node" + IG$/*mainapp*/._I20/*XUpdateInfo*/(box, "name;shape;sid;x;y;w;h;type;role", "s") + ">");
            r.push("<label><![CDATA[" + box.text + "]]></label>");
            
            if (box.comment)
            {
            	r.push("<comment><![CDATA[" + box.comment + "]]></comment>");
            }
            
            r.push("<property");
            r.push(IG$/*mainapp*/._I20/*XUpdateInfo*/(ab, "dsname;dsignoreerror", "s"));
            r.push(">");
            
            switch (box.name)
            {
            case "datasource":
            	me.m1/*appProperty*/(r, ab, "dsdesc;dstype;dsmapper;dsreducer;dsfname;dstname;dsskipexist");
            	break;
            case "shell":
            	me.m1/*appProperty*/(r, ab, "dsdesc;dsscript");
            	break;
            case "hive":
            	me.m1/*appProperty*/(r, ab, "dsdesc;dsscript");
            	break;
            case "mongoreduce":
            	me.m1/*appProperty*/(r, ab, "dsdesc;rowregex;rowsample;mrtype;m_delim;cfields");
            	break;
            case "mongodoc":
            	me.m1/*appProperty*/(r, ab, "dsdesc;doctmpl;mrnode;mrdb;mrcol;mrdocname;mrsmode");
            	break;
            case "mapreduce":
            	me.m1/*appProperty*/(r, ab, "dsdesc;dsmapper;dsreducer;confparams");
            	break;
            case "output":
            	me.m1/*appProperty*/(r, ab, "dsdesc;dsfname;dscnames;dsruntype;dsdelim;dsdelimval;dstcube;dstcubeuid;dsmeasuredelim;dsmeasuredelimval;dsmname;dscsdatause;dscsdatafile");
            	break;
            default:
            	me.m1/*appProperty*/(r, ab, "dsdesc");
            	break;
            }
            r.push("</property>");
            
            l = box.additional_properties;
		
			if (l && l.length)
			{
				r.push("<additional_properties>");
				
				for (j=0; j < l.length; j++)
				{
					r.push("<property name='" + l[j].name + "' value='" + l[j].value + "'/>");
				}
				
				r.push("</additional_properties>");
			}
			
			l = box.states;
			
			if (l && l.length)
			{
				r.push("<states>");
				
				for (j=0; j < l.length; j++)
				{
					r.push("<state name='" + l[j].name + "'/>");
				}
				
				r.push("</states>");
			}
			
			if (box.precision)
			{
				r.push("<precision>" + box.precision + "</precision>");
			}
			
			if (box.unit)
			{
				r.push("<unit>");
				
				for (j in box.unit)
				{
					r.push("<property name='" + j + "'><![CDATA[" + box.unit[j] + "]]></property>");
				}
				
				r.push("</unit>");
			}
            
            r.push("</node>");
		}
		r.push("</nodes>");
		
		var cons = me.plumb.getConnections(),
			src, tgt;
		r.push("<links>");
		if (cons && cons.length)
		{
			for (i=0; i < cons.length; i++)
			{
				lnk = cons[i];
				ptr_link = lnk.ptr_link;
				src = lnk.source;
                tgt = lnk.target;
                
                src = me.getBoxObject(src);
                tgt = me.getBoxObject(tgt);
                
                if (src && tgt) {
                    var p = lnk.getPaintStyle();
                    r.push("<link");
                    
                    r.push(IG$/*mainapp*/._I20/*XUpdateInfo*/({
                        from: src.sid, 
                        to: tgt.sid,
                        dashstyle: (p.dashstyle) ? true : false,
                        directed: ptr_link ? ptr_link.directed : "T"
                    }, "from;to;dashstyle;directed", "s"));
                    r.push(">");
                    
                    if (ptr_link && ptr_link.potential)
                    {
                    	r.push(ptr_link.potential.p2/*toXMLString*/());
                    }
                    
                    if (ptr_link && ptr_link.revealing_condition)
                    {
                    	r.push("<revealing_condition>");
                    	
                    	if (ptr_link.revealing_condition.states)
                    	{
                    		r.push("<states>");
                    		for (j=0; j < ptr_link.revealing_condition.states.length; j++)
                    		{
                    			r.push("<state name='" + ptr_link.revealing_condition.states[j].name + "'></state>");
                    		}
                    		r.push("</states>");
                    	}
                    	
                    	r.push("</revealing_condition>");
                    }
                    
                    r.push("</link>");
                }
			}
		}
		r.push("</links>");
		
		return r.join("");
	},
	
	getBoxObject: function(elem) {
		var me = this,
            i, r,
            boxes = me.a_boxes,
            t = typeof(elem) == "string" ? 0 : 1,
            elem_id = t ? $(elem).attr("id") : null,
            box_id;
		
        for (i=0; i < boxes.length; i++) 
        {
            if (!t && boxes[i].sid == elem) 
            {
                r = boxes[i];
                break;
            }
            else if (t) 
            {
            	box_id = boxes[i].box.attr("id");
            	if (box_id == elem_id)
            	{
	                r = boxes[i];
	                break;
            	}
            }
        }
        
        return r;
	}
}