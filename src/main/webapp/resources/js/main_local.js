function callService(url, functionToPrint) {

	console.log("Prepare to call");
	function handler() {

		if (client.readyState == 4 && client.status == 200) {
			console.log("Calling");
			functionToPrint(client.responseText);
			console.log("End call");
		}
	}

	var client = new XMLHttpRequest();
	client.onreadystatechange = handler;
	client.open("GET", url);
	client.send();
}

/*
 * despliega el panel el panel con el date picker al seleccionar diario.
 */

function handleClick(object, idDiv) {
	var id = object.id;
	var value = document.getElementById(idDiv).getAttribute("style");
	console.log("value:"+value);
	if (value.indexOf("visible") > -1) {
		document.getElementById(idDiv).setAttribute("style",
				"visibility:collapse;");
	} else {
		document.getElementById(idDiv).setAttribute("style",
				"visibility:visible;");
	}
}
function clean(idTbl) {
	console.log("clear table " + idTbl);
	var table = document.getElementById(idTbl);
	if (table == null)
		return;
	var rows = table.rows;
	if (rows == null)
		return;
	var i = rows.length;
	while (--i) {
		rows[i].parentNode.removeChild(rows[i]);
		// or
		// table.deleteRow(i);
	}
}