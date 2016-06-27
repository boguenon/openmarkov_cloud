IG$/*mainapp*/._N12/*MainPanel*/ = Ext.extend(Ext.panel.Panel, {
    margin: '0 0 0 0',
    resizeTabs: true,
    minTabWidth: 100,
    // tabWidth: 135,
    plugins: new Ext.ux.TabCloseMenu(),
    enableTabScroll: true,
    activeTab: 0,
	layout: {
		type: "card",
		deferredRender: false
	},
	
	deferredRender: false,
	
	itemCls: Ext.baseCSSPrefix + 'tabpanel-child',
	tabPosition : 'top',
	removePanelHeader: true,

    plain: false,

    minTabWidth: undefined,
    maxTabWidth: undefined,
    
    customAddTab: function(uid, ext_panel) {
		var me = this,
			tab = me.getComponent(uid);
			
		if (tab)
		{
			IG$/*mainapp*/._I7e/*changeApp*/(2);
			me.setActiveTab(tab);
		}
		else
		{
			IG$/*mainapp*/._I7e/*changeApp*/(2);
			ext_panel.id = uid;
			tab = me.add(ext_panel);
			me.setActiveTab(tab);
		}
	},
	
    m1$7/*navigateApp*/: function(uid, itemtype, itemname, itemaddr, baddhistory, writable, option, toption) {
    	var key,
    		tab = this.getComponent(itemtype + "_" + uid);
    		
    	if (tab)
    	{
    		IG$/*mainapp*/._I7e/*changeApp*/(1);
    		
    		this.setActiveTab(tab);
    		
    		if (toption && tab.applyOption)
    		{
    			tab.applyOption.call(tab, toption);
    		}
    		else if (itemtype == "dashboard")
    		{
    			var teditor = this.getComponent("dashboardedit" + "_" + uid);
    			if (teditor)
    			{
    				if (tab.uid && tab.uid.length > 0)
		    		{
		   				tab.M1/*procRunDashboard*/.call(tab);
		    		}
    			}
    		}
    	}
    	else
    	{
    		var callback = new IG$/*mainapp*/._I3d/*callBackObj*/(this, function() {
					this.m1$7/*navigateApp*/(uid, itemtype, itemname, itemaddr, baddhistory, writable, option, toption);
				}),
				pitem = IG$/*mainapp*/._I61/*createAppPanel*/(uid, itemtype, itemname, itemaddr, writable, toption || null, callback);
    		
    		if (pitem)
    		{
    			pitem.address = itemaddr;
    			pitem.title = IG$/*mainapp*/._I28/*getTabTitle*/(itemname);
    			
    			if (option)
    			{
    				for (key in option)
    				{
    					pitem[key] = option[key];
    				}
    			}
    			
    			if (pitem.isWindow == true)
    			{
    				baddhistory = false;
    				pitem.title = IG$/*mainapp*/._I28/*getTabTitle*/(itemname) + " (" + IRm$/*resources*/.r1("D_" + itemtype.toUpperCase()) + ")";
    				
    				if (!IG$/*mainapp*/.ps[pitem.address])
    				{
    					IG$/*mainapp*/.ps[pitem.address] = pitem;
    					pitem.on("close", function() {
    						delete IG$/*mainapp*/.ps[pitem.address];
    					});
    					pitem.show();
    				}
    			}
    			else
    			{
    				IG$/*mainapp*/._I7e/*changeApp*/(1);
    				pitem.id = itemtype + "_" + uid;
    				var p = this.add(pitem);
    				this.setActiveTab(p);
    			}
    		}
    	}
    },
	
	i1/*initRecentVisit*/ : function(){
		var me = this,
			btn1 = $("#mbutton1"),
			idv_recitems = $("#idv_recitems"),
			welcome = $("#welcome"),
			items;
			
		me.mviewer = {
			mode: "recent",
			mrecenttext: btn1.text()
		};
		
		btn1.unbind("click");
		
		btn1.bind("click", function() {
			me.i1_1/*loadRecentVisit*/.call(me);
		});
		
		items = [
			{
				html: $("#bn_asia", welcome),
				handler: function() {
					IG$/*mainapp*/._n1/*navigateMenu*/("bn_asia");
				}
			},
			{
				html: $("#id_decide_test", welcome),
				handler: function() {
					IG$/*mainapp*/._n1/*navigateMenu*/("id_decide_test");
				}
			},
			{
				html: $("#dan_dating", welcome),
				handler: function() {
					IG$/*mainapp*/._n1/*navigateMenu*/("dan_dating");
				}
			},
			{
				html: $("#dan_reactor", welcome),
				handler: function() {
					IG$/*mainapp*/._n1/*navigateMenu*/("dan_reactor");
				}
			}
		];
		
		$.each(items, function(i, item) {
			if (item.html && item.html.length > 0 && item.handler)
			{
				item.html.bind("click", function() {
					item.handler();
				});
			}
		});
	},
	
	
	
	deferredRender : true,
	
	onAdd: function(item, index) {
		Ext.tab.Panel.prototype.onAdd.call(this, item, index);
	},
	
	//inherit docs
    initComponent: function() {
        var me = this,
            dockedItems = [].concat(me.dockedItems || []),
            activeTab = me.activeTab || (me.activeTab = 0),
            tabPosition = me.tabPosition,
            tbar;

        // Configure the layout with our deferredRender, and with our activeTeb
        me.layout = new Ext.layout.container.Card({
            owner: me,
            deferredRender: me.deferredRender,
            itemCls: me.itemCls,
            activeItem: activeTab
        });

        /**
         * @property {Ext.tab.Bar} tabBar Internal reference to the docked TabBar
         */
		IG$/*mainapp*/._n12t/*mainPanelTabBar*/ = new Ext.tab.Bar({
			ui: me.ui,
			dock: "top",
			id: "ig-tabbar",
			cls: "ig-main-tabbar",
			flex: 1,
			orientation: "horizontal",
			plain: true, //me.plain,
			cardLayout: me.layout,
			tabPanel: me,
			padding: 0,
			margin: 0,
			bodyPadding: 0,
			listeners: {
			}
		});
		
        me.tabBar = IG$/*mainapp*/._n12t/*mainPanelTabBar*/;

        me.callParent(arguments);
        
        me.setActiveTab = function(card) {
        	IG$/*mainapp*/._I7e/*changeApp*/(1);
        	Ext.tab.Panel.prototype.setActiveTab.call(me, card);
        };
	    me.getActiveTab = Ext.tab.Panel.prototype.getActiveTab;
	    me.getTabBar = Ext.tab.Panel.prototype.getTabBar;
	    me.onItemEnable = Ext.tab.Panel.prototype.onItemEnable;
	    me.onItemDisable = Ext.tab.Panel.prototype.onItemDisable;
	    me.onItemBeforeShow = Ext.tab.Panel.prototype.onItemBeforeShow;
	    me.onItemIconChange = Ext.tab.Panel.prototype.onItemIconChange;
	    me.onItemIconClsChange = Ext.tab.Panel.prototype.onItemIconClsChange;
	    me.onItemTitleChange = Ext.tab.Panel.prototype.onItemTitleChange;
	    me.doRemove = Ext.tab.Panel.prototype.doRemove;
	    me.onRemove = function(item, destroying) {
	    	Ext.tab.Panel.prototype.onRemove.call(me, item, destroying);
	    	
	    	var itemcnt = me.items.length;
        	
        	if (itemcnt == 0)
        	{
        		var explorer = IG$/*mainapp*/._IM3/*explorer*/;
        		IG$/*mainapp*/._I7e/*changeApp*/(0);
        	}
	    }
		
        // We have to convert the numeric index/string ID config into its component reference
        activeTab = me.activeTab = me.getComponent(activeTab);

        // Ensure that the active child's tab is rendered in the active UI state
        if (activeTab) {
            me.tabBar.setActiveTab(activeTab.tab, true);
        }
    },
    
	listeners: {
        beforeadd: function(tobj, comp, index, eopts) {
        	var me = this,
				tabbar = me.getTabBar(),
				maxtab = ig$/*appoption*/.maxtapcount || 0,
				atab;
				
			if (maxtab > 0 && me.items.length >= maxtab)
			{
				// tabbar.remove.call(tabbar, tabbar.items.items[0]);
				// me.setActiveTab(me.items.items[0]);
				atab = me.items.items[0];
				// me.setActiveTab(atab);
				me.remove(atab);
				//atab.close();
			}
        }
    }
});