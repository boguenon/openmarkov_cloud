IG$/*mainapp*/._I61/*createAppPanel*/ = function(uid, itemtype, name, address, writable, opt, callback) {
	var p = null,
		popt = {uid: uid, writable: writable}, key, val,
		modname,
		modtype = 0,
		scripts;
	
	if (opt)
	{
		for (key in opt)
		{
			popt[key] = opt[key];
		}
	}
	
	popt.iconCls = "icon-bigdata";
	modtype = 3;
	popt.itemtype = "BayesNet";
	modname = "_IB9b";

	if (modname)
	{
		if (IG$/*mainapp*/[modname])
		{
			p = new IG$/*mainapp*/[modname](popt);
		}
		else
		{
			// load module
			scripts = ig$/*appoption*/.scmap[modtype == 1 ? "igcm" : (modtype == 2 ? "igc7" : (modtype == 3 ? "igc9" : "igcn"))];
			
			IG$/*mainapp*/.x03/*getScriptCache*/(
				scripts, 
				new IG$/*mainapp*/._I3d/*callBackObj*/(this, function() {
					if (IG$/*mainapp*/[modname])
					{
						callback && callback.execute();
					}
					else
					{
						IG$/*mainapp*/._I52/*ShowError*/(IRm$/*resources*/.r1("L_ERR_L_MOD"));
					}
				})
			);
		}
	}
	
	return p;
};


IG$/*mainapp*/._I64/*showHideAllWindow*/ = function(b_show) {
	var visible = (b_show == true) ? "visible" : "hidden",
		mtop = (b_show == true) ? 0 : 10000,
		i;
	
	if (IG$/*mainapp*/._I7d/*mainPanel*/)
	{
		for (i=IG$/*mainapp*/._I7d/*mainPanel*/.items.length - 1; i>=0; i--)
		{
			IG$/*mainapp*/._I7d/*mainPanel*/.items.items[i].close();
		}
	}	
};


