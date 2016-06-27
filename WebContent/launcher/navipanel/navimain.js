Ext.BLANK_IMAGE_URL = './images/s.gif';

IG$/*mainapp*/._n1/*navigateMenu*/ = function(pname)
{
	IG$/*mainapp*/._I7d/*mainPanel*/.m1$7/*navigateApp*/.call(IG$/*mainapp*/._I7d/*mainPanel*/, pname, "bayesnet", pname, "", true, true);
}

var hist,
	lastHist;

IG$/*mainapp*/._I7e/*changeApp*/ = function(mode, opt) {
	var appsel = IG$/*mainapp*/.appsel,
		l = appsel.getLayout(),
		menus = ig$/*appoption*/.mainmenu.mainmenu,
		i, m,
		c,
		nindex = mode;
		
	l.setActiveItem(nindex);
	
	c = l.getActiveItem();
	
	IG$/*mainapp*/.__atab = mode;
	
	c._IQ4/*initComp*/ && c._IQ4/*initComp*/.call(c);
	
	if (mode == 4 && opt) {
		for (i=0; i < menus.length; i++)
		{
			m = menus[i];
			m.tul && m.tul[m.acls == opt ? "show" : "hide"]();
		}
	}
};

IG$/*mainapp*/.$1/*loadApp*/ = function(tmpl) {
	var viewport = IG$/*mainapp*/.__1/*viewport*/,
		mainview,
		mview,
		appmenu,
		navitab,
		ntree,
		tabbaritems,
		appselitems,
		appselactive = 0,
		tappmenu = ig$/*appoption*/.mainmenu.mainmenu,
		i;
		
	if (IG$/*mainapp*/.__1m/*viewportpanel*/ || IG$/*mainapp*/.__ep == true)
	{
		setTimeout(function(){
			Ext.get('loading') && Ext.get('loading').fadeOut({remove:true});
			Ext.get('loading-mask') && Ext.get('loading-mask').fadeOut({remove:true});
		}, (IG$/*mainapp*/.__ep ? 0 : 100));
		
		return;
	}
	
	tabbaritems = [
		{
			xtype: "button",
			border: 0,
			margin: "0", //"0 5 0 0",
			cls: "ig-top-button",
			overCls: "ig-top-button-over",
			focusCls: "ig-top-button-click",
			text: IRm$/*resources*/.r1("S_HOME"),
			handler: function() {
				IG$/*mainapp*/._I7e/*changeApp*/(0);
			}
		}
	];
	
	tabbaritems.push(IG$/*mainapp*/._n12t/*mainPanelTabBar*/);
	
	appselitems = [
		{
			id:'welcome-panel',
			header: false,
			title: null, // 'Portal Main',
			autoLoad: {
				url: "./html/" + (ig$/*appoption*/.intropage || "navi_intro") + "_" + (window.useLocale || "en_US") + ".html", 
				callback: IG$/*mainapp*/._I7d/*mainPanel*/.i1/*initRecentVisit*/, 
				scope: IG$/*mainapp*/._I7d/*mainPanel*/
			},
			iconCls:'icon-welcome',
			autoScroll: true
		},
		IG$/*mainapp*/._I7d/*mainPanel*/
	];
	
	mainview = new Ext.panel.Panel({
        "layout":'border',
		padding: '0 0 0 0',
		
        items:[ 
	        {
	        	xtype: "container",
	        	region: "center",
	        	border: 0,
	        	layout: "border",
	        	items: [
	        		{
		        		xtype: "container",
		        		region: "north",
		        		height: 28,
		        		padding: "2 2 2 2",
		        		layout: {
		        			type: "hbox",
		        			align: "stretch"
		        		},
		        		items: 
		        			tabbaritems
		        		
		        	},
		        	{
			        	xtype: "container",
			        	layout: "card",
			        	name: "appsel",
			        	region: "center",
			        	minTabWidth: 40,
						deferredRender: false,
						activeItem: appselactive,
			        	items: appselitems
			        }
	        	]
	        }
		]
    });
    
    viewport.add(mainview);
    
    mainview.doLayout();
    
    IG$/*mainapp*/.__1m/*viewportpanel*/ = mainview;
    IG$/*mainapp*/._I8d/*appmenu*/ = mainview.down("[name=appmenu]");
    
    IG$/*mainapp*/.appsel = mainview.down("[name=appsel]");
    
	setTimeout(function(){
        Ext.get('loading') && Ext.get('loading').fadeOut({remove:true});
        Ext.get('loading-mask') && Ext.get('loading-mask').fadeOut({remove:true});
    }, 50);
}

Ext.onReady(function(){
    // Ext.QuickTips.init();
    Ext.tip.QuickTipManager.init()
    
    // alert("Load Initialize");
    
    $("#lpt", "#loading").css("width", "80%");
    
	IRm$/*resources*/.r2/*loadResources*/({
		func: function() {
			$("#lpt", "#loading").css("width", "100%");
			
			setTimeout(function() {
				var i,
					copt;
				
				$('#win-mask').css({display: 'none'});
				
	    		IG$/*mainapp*/.lE/*loadExtend*/.rcsloaded = true;
	    		
			    // Ext.Direct.addProvider(Ext.app.REMOTING_API);
			
			    IG$/*mainapp*/._I7d/*mainPanel*/ = new IG$/*mainapp*/._N12/*MainPanel*/({
			    	name: "_I7d"
			    });
			    		    
			    IG$/*mainapp*/._I7d/*mainPanel*/.on('tabchange', function(tp, tab){
			        // api.selectClass(tab.cclass); 
			    });
			    
			    IG$/*mainapp*/._IM3/*explorer*/ = null;
			    
			    $.each(IG$/*mainapp*/.lE/*loadExtend*/.items, function(i, obj) {
			    	IG$/*mainapp*/.extend(obj.name, obj.base, obj.option);
			    });
			    
			    $.each(IG$/*mainapp*/._IE1/*filteropcodes*/, function(i, obj) {
			    	var l = obj[4];
			    	obj[1] = IRm$/*resources*/.r1(l);
			    });
			    
				var viewport = new Ext.Viewport({
			        "layout": "fit",
			        id: 'mainview'
				});
				
				viewport.doLayout();
				
				IG$/*mainapp*/.__1/*viewport*/ = viewport;

				IG$/*mainapp*/.$1/*loadApp*/();
    		
			}, 10);
		}
	});
});

