<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    request.setCharacterEncoding("utf-8");
    String _d = request.getParameter("_d");
    String ukey = "?_d=" + _d;
    String lang = request.getParameter("lang");
    lang = (lang == null) ? "en_US" : lang;
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>DevHome</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="./css/ma1.css" />
	<link rel="stylesheet" type="text/css" href="./css/itemicon.css" />
	<link rel="stylesheet" type="text/css" href="./css/navi.css" />
	<link rel="stylesheet" type="text/css" href="./css/boguenon.css" />
	
<script type="text/javascript" src="../boguenon.js"></script>
<script type="text/javascript">

var useLocale = "<%=lang%>";
var m$_d = "";

function getLocale()
{
	var hash = window.location.hash.substring(1).split('&'),
		i, k, v, m;
	
	for (i=0; i < hash.length; i++)
	{
		m = hash[i].indexOf("=");
		if (m > 0)
		{
			k = hash[i].substring(0, m);
			v = hash[i].substring(m+1);
			
			if (k == "lang" && v)
			{
				useLocale = v;
				break;
			}
		}
	}
}

getLocale();

</script>

<script type="text/javascript" src="./js/ext-all-debug.js"></script>

<script type="text/javascript" src="./js/jquery-1.12.0.js"></script>
<script type="text/javascript" src="./common/browser.js"></script>
<script type="text/javascript" src="./js/jquery-ui-1.10.4.custom.js"></script>
<script type="text/javascript" src="./js/jquery.mousewheel.js"></script>
<script type="text/javascript" src="./js/jquery.fileupload.js"></script>

<script type="text/javascript" src="./js/highcharts.js"></script>

<script type="text/javascript" src="./js/highcharts-more.js"></script>

<script type="text/javascript" src="./common/base64.js"></script>
<script type="text/javascript" src="./common/boguenon.js"></script>

<script type="text/javascript" src="./common/boguenon_ext.js"></script>

<script type="text/javascript" src="./controls/resource.js"></script>
<script type="text/javascript" src="./controls/appmain.js"></script>

<script type="text/javascript">
Ext.Loader.setConfig({
    enabled: true
});

Ext.Loader.setPath('Ext.ux', './ux/');
Ext.Loader.setPath('Ext.ux.DataView', './ux/DataView/');
Ext.Loader.setPath("igc", './controls/');

Ext.require([
    'Ext.ux.statusbar.StatusBar'
]);
</script>

<script type="text/javascript" src="./navipanel/TabCloseMenu.js"></script>
<script type="text/javascript" src="./navipanel/navipanel.js"></script>
<script type="text/javascript" src="./navipanel/navimain.js"></script>

<script type="text/javascript" src="./js/jquery.jsPlumb-2.1.2.js"></script>
<script type="text/javascript" src="./ml/dlg_base.js"></script>
<script type="text/javascript" src="./ml/bflow.js"></script>
<script type="text/javascript" src="./ml/dialog/cost_effectiveness.js"></script>
<script type="text/javascript" src="./ml/dialog/db_generator.js"></script>
<script type="text/javascript" src="./ml/dialog/learning_dialog.js"></script>
<script type="text/javascript" src="./ml/dialog/network_properties_dialog.js"></script>
<script type="text/javascript" src="./ml/sensitivityanalysis/tornado_spider.js"></script>
<script type="text/javascript" src="./ml/bayes_obj.js"></script>
<script type="text/javascript" src="./ml/bayes_pgmx.js"></script>
<script type="text/javascript" src="./ml/bayes_node_properties.js"></script>
<script type="text/javascript" src="./ml/bayes.js"></script>

</head>
<body scroll="no">
	<div id="loading-mask" style=""></div>
	<!-- div id="loading">
  		<div class="loading-indicator"><img src="./images/extanim32.gif" width="32" height="32" style="margin-right:8px;" align="absmiddle"/>Loading...</div>
	</div -->
	
	<div id="loading">
		<div class="cmsg">
			<div class="msg">Loading BOGUENON</div>
			<div class="lpb">
				<div id="lpt" style="width: 10%;"></div>
			</div>
		</div>
	</div>

 	<div id="main"></div>
</body>
</html>
