IG$/*mainapp*/.bA_5/*tornado_spider*/ = Ext.extend(Ext.Window, {
	modal: true,
	isWindow: true,
	"layout": "fit",
	
	closable: false,
	resizable:true,
	width: 600,
	height: 550,
	
	_r: {},
	
	_IFe/*initF*/: function() {
		var me = this,
			result_xml = me.result_xml,
			tnode = result_xml ? IG$/*mainapp*/._I18/*XGetNode*/(result_xml, "/smsg/cost_effectiveness_results") : null,
			tnodes;
		
		if (tnode)
		{
			tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(tnode);
			
			$.each(tnodes, function(i, node) {
				me.d1/*drawChart*/.call(me, node);
			});
		}
	},
	
	d1/*drawChart*/: function(rnode) {
		var me = this,
			cname = IG$/*mainapp*/._I29/*XGetNodeName*/(rnode),
			cnode;
		
		switch(cname)
		{
		case "tornado_spider":
			cnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "tornado");
			cnode && me.d2/*drawTornado*/(cnode);

			cnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "spider_chart");
			
			if (me._r["p_spider"] == 0)
			{
				cnode && me.d3/*drawSpider*/(cnode, "p_spider");
			}
			else
			{
				me._r["p_spider"] = cnode;
			}
			break;
		case "plot":
			cnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "plot_chart");
			if (cnode)
			{
				if (me._r["p_plot"] == 0)
				{
					cnode && me.d3/*drawSpider*/(cnode, "p_plot");
				}
				else
				{
					me._r["p_plot"] = cnode;
				}
			}
			break;
		}
	},
	
	d2/*drawTornado*/: function(rnode) {
		var me = this,
			p_tornado = me.down("[name=p_tornado]"),
			p_tornado_doc = $(p_tornado.el.dom),
			tchart,
			categories = [],
			mc, chart = {
				chart: {
					type: "bar"
				},
				title: {
					text: "Tornado"
				},
				xAxis: [
					{
						reversed: false
					},
					{
						opposite: true,
						reversed: false,
						linkedTo: 0,
						labels: {
							step: 1
						}
					}
				],
				yAxis: {
					labels: {
						formatter: function() {
							return Math.abs(this.value);
						}
					}
				},
				plotOptions: {
					series: {
						stacking: "normal"
					}
				},
				series: []
			},
			bnode,
			bnodes,
			i, minvalues = [], maxvalues = [],
			s, e,
			rval;
		
		p_tornado_doc.empty();
		
		tchart = $("<div></div>").appendTo(p_tornado_doc);
		tchart.css({
			width: p_tornado_doc.width(),
			height: p_tornado_doc.height()
		});
		
		categories = IG$/*mainapp*/._I1a/*getSubNodeText*/(rnode, "category_key");
		
		categories = categories ? categories.split("\t") : [];
		
		chart.xAxis[0].categories = categories;
		chart.xAxis[1].categories = categories;
		
		chart.chart.renderTo = tchart[0];
		
		bnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "reference_markers/reference");
		rval = IG$/*mainapp*/._I1b/*XGetAttr*/(bnode, "value");
		rval = Number(rval);
		
		bnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "bars");
		bnodes = IG$/*mainapp*/._I26/*getChildNodes*/(bnode);
		
		for (i=0; i < bnodes.length; i++)
		{
			s = IG$/*mainapp*/._I1b/*XGetAttr*/(bnodes[i], "starts");
			e = IG$/*mainapp*/._I1b/*XGetAttr*/(bnodes[i], "ends");
			
			minvalues.push(Number(s) - rval);
			maxvalues.push(Number(e) - rval);
		}
		
		chart.series.push({
			name: "Starts",
			data: minvalues
		});
		
		chart.series.push({
			name: "Ends",
			data: maxvalues
		});
		
		mc = new Highcharts.Chart(chart);
	},
	
	d3/*drawSpider*/: function(rnode, pname) {
		var me = this,
			p_spider = me.down("[name=" + pname + "]"),
			p_spider_doc = $(p_spider.el.dom),
			tchart,
			categories = [],
			mc, chart = {
				chart: {
					type: "line"
				},
				title: {
					text: "Spider"
				},
				xAxis: {
				},
				yAxis: {
					labels: {
						formatter: function() {
							return Math.abs(this.value);
						}
					}
				},
				legend: {
					layout: "vertical",
					align: "right",
					verticalAlign: "middle",
					borderWidth: 0
				},
				series: []
			},
			bnode,
			bnodes,
			i,
			s, x, y;
		
		p_spider_doc.empty();
		
		tchart = $("<div></div>").appendTo(p_spider_doc);
		tchart.css({
			width: p_spider_doc.width(),
			height: p_spider_doc.height()
		});
		
		bnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "series");
		
		if (bnode)
		{
			bnodes = IG$/*mainapp*/._I26/*getChildNodes*/(bnode);
			
			for (i=0; i < bnodes.length; i++)
			{
				s = {
					name: IG$/*mainapp*/._I1b/*XGetAttr*/(bnodes[i], "name"),
					data: []
				};
				
				if (i == 0)
				{
					x = IG$/*mainapp*/._I1a/*getSubNodeText*/(bnodes[i], "xvalues");
					categories = x.split("\t");
				}
				
				y = IG$/*mainapp*/._I1a/*getSubNodeText*/(bnodes[i], "yvalues");
				s.data = y.split("\t");
				
				for (j=0; j < s.data.length; j++)
				{
					s.data[j] = Number(s.data[j]);
				}
				
				chart.series.push(s);
			}
		}
		
		chart.xAxis.categories = categories;
		chart.chart.renderTo = tchart[0];
		
		bnode = IG$/*mainapp*/._I18/*XGetNode*/(rnode, "reference_markers/reference");
		if (bnode)
		{
			rval = IG$/*mainapp*/._I1b/*XGetAttr*/(bnode, "value");
			rval = Number(rval);
		}
		
		mc = new Highcharts.Chart(chart);
	},
	
	initComponent : function() {
		var panel = this;
		
		panel.title = IRm$/*resources*/.r1("T_TORSP_RS");
		
		Ext.apply(this, {
			items: [
				{
					xtype: "panel",
					bodyPadding: 10,
					layout: "fit",
					items: [
					    {
					    	xtype: "tabpanel",
					    	defaults: {
					    		deferredRender: false
					    	},
					    	flex: 1,
					    	items: [
						    	{
						    		xtype: "panel",
						    		title: "Tornado",
						    		layout: "fit",
						    		name: "p_tornado"
						    	},
						    	{
						    		xtype: "panel",
						    		title: "Spider",
						    		layout: "fit",
						    		name: "p_spider",
						    		listeners: {
						    			afterrender: function(tobj) {
						    				if (panel._r["p_spider"])
						    				{
												panel.d3/*drawSpider*/.call(panel, panel._r["p_spider"], "p_spider");
						    				}
						    				panel._r["p_spider"] = 0;
						    			}
						    		}
						    	},
						    	{
						    		xtype: "panel",
						    		title: "Plot",
						    		layout: "fit",
						    		name: "p_plot",
						    		listeners: {
						    			afterrender: function(tobj) {
						    				if (panel._r["p_plot"])
						    				{
												panel.d3/*drawSpider*/.call(panel, panel._r["p_plot"], "p_plot");
						    				}
						    				panel._r["p_plot"] = 0;
						    			}
						    		}
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
		
		IG$/*mainapp*/.bA_5/*tornado_spider*/.superclass.initComponent.apply(this, arguments);
	}
});