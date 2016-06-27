var IRm$/*resources*/ = {};

IRm$/*resources*/.locale = [];

IRm$/*resources*/.r1 = function(rcsid, rvalue) {
	if (!IRm$/*resources*/.currentLocale)
	{
		var i;
		
		IRm$/*resources*/.currentLocale = IRm$/*resources*/.locale[0];
		
		for (i=0; i < IRm$/*resources*/.locale.length; i++)
		{
			if (IRm$/*resources*/.locale[i].locale == window.useLocale)
			{
				IRm$/*resources*/.currentLocale = IRm$/*resources*/.locale[i];
				break;
			}
		}
	}
	
	var r = rcsid; // ? (IRm$/*resources*/.currentLocale.data[rcsid] || '!RESOURCE_EMPTY!') : null;
	
	if (IRm$/*resources*/.currentLocale && IRm$/*resources*/.currentLocale.data[rcsid])
	{
		r = ig$/*appoption*/.vmode == 1 ? rcsid + "*" : IRm$/*resources*/.currentLocale.data[rcsid];
	}
	else
	{
		r = (ig$/*appoption*/.vmode == 1 ? "x " : "") + rcsid;
	}
	
	if (r && rvalue)
	{
		if (typeof(rvalue) == "string")
		{
			rvalue = [rvalue];
		}
		
		if (rvalue.length == 1)
		{
			r = r.replace('@', rvalue[0]);
		}
		else
		{
			for (i=0; i < rvalue.length; i++)
			{
				r = r.replace("@" + (i+1), rvalue[i]);
			}
		}
	}
	return r;
}

IRm$/*resources*/.r2_d/*loadResources*/ = function(bproc) {
	if (bproc && bproc.func)
	{
		bproc.func.call(bproc.scope);
	}
}

IRm$/*resources*/.r2/*loadResources*/ = function(bproc) {
	$.ajax({
		type: "GET",
		url: "./html/resources.xml", 
		dataType: "text",
		timeout: 10000,
		success: function(data) {
			var xdoc = IG$/*mainapp*/._I13/*loadXML*/(data),
				t = IG$/*mainapp*/._I18/*XGetNode*/(xdoc, "/smsg/item"),
	    		tnodes,
	    		i, j, snodes,
	    		loc, sname, svalue,
	    		sloc;
	    		
	    	if (t)
	    	{
	    		tnodes = IG$/*mainapp*/._I26/*getChildNodes*/(t);
	    		
	    		sloc = {
					locale: "en_US",
					data: {}
				};
	        			
	    		for (i=0; i < tnodes.length; i++)
	    		{
					sname = IG$/*mainapp*/._I1b/*XGetAttr*/(tnodes[i], "name");
					svalue = IG$/*mainapp*/._I24/*getTextContent*/(tnodes[i]);
					sloc.data[sname] = svalue;
	    		}
	    		
	    		IRm$/*resources*/.locale.push(sloc);
	    	}
	    	
	    	if (bproc && bproc.func)
	    	{
	    		bproc.func.call(bproc.scope);
	    	}
		},
		error: function(e, status, thrown) {
		}
	});
}
