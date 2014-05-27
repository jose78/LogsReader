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



function executeGettingErrorNow() {
	console.log("starting executeSErvice");

	var ancho = document.getElementById('errorNowAncho').value;
	var minutos = document.getElementById('errorNowMinutos').value;

	var url = "/ErrorNowTbl?ancho=" + ancho + "&minutos=" + minutos;

	console.log("URL: " + url);
	callService(url, function(result) {
		clean('idTableErrorNow');
		printResultErrorNow(result);
	});
	// 
	 

	url = "/ErrorNowGr?ancho=" + ancho+"&application=BUZZ";
	console.log("URL: " + url);
	callService(url, function(result) {
		
		drawGraph(result,'BUZZ');
	});
	
	url = "/ErrorNowGr?ancho=" + ancho+"&application=NXT/WALLET";
	console.log("URL: " + url);
	callService(url, function(result) {
		
		drawGraph(result,'NXT/WALLET');
	});
}




function drawGraph(dataJson , application) {

	var json = JSON
			.parse(dataJson);
	console.log(json);
	
	try {

		var title = "Gráfica que recoge el Nº de errores por franja horaria para la aplicación "+application;

	
		console
				.log("eje y : "
						+ json.axisY);
		console
				.log("eje x : "
						+ json.axisX);

		var data = google.visualization
				.arrayToDataTable(json.listData);

		var options = {
			title : title,
			width : 800,
			height : 400,
			vAxis : {
				title : json.axisY
			},
			hAxis : {
				title : json.axisX
			},
			seriesType : "bars",
			
		};

		
		var chart = new google.visualization.ComboChart(
				document
						.getElementById('visualization'+application));
		// The select handler. Call the chart's getSelection() method
		function selectHandler() {
			var selectedItem = chart
					.getSelection()[0];
			console
					.log("selectedItem"
							+ selectedItem);
			if (selectedItem) {
				var value = data
						.getValue(
								selectedItem.row,
								selectedItem.column);
				alert('The user selected '
						+ value);
				console
						.log("row:"
								+ selectedItem.row
								+ " -> "
								+ json.listData[selectedItem.row + 1][0]);
				console
						.log("column:"
								+ selectedItem.column);
			}
		}

		// Listen for the 'select' event, and call my function selectHandler() when
		// the user selects something on the chart.
		google.visualization.events
				.addListener(
						chart,
						'select',
						selectHandler);

		chart.draw(data,
				options);

		console
				.log('Data create OK');

	} catch (err) {
		console
				.log('ERROR: '
						+ err);
	}

	console
			.log("printData FIN");

}

function printResultErrorNow(dataJSON) {

	var table = document.getElementById('idTableErrorNow');
	console.log("ID TABLE tableErrorNow" + table);
	var json = JSON.parse(dataJSON);
	console.log("JSON " + json);

	for (var i = 0; i < json.length; i++) {
		var data = json[i];

		var row = document.getElementById("row_error_now_" + data.service
				+ "_" + data.application);

		if (row == null) {
			row = addRow(table, data.service, data.aggregate);
		}

		row.insertCell(0).innerHTML = data.application;
		row.insertCell(1).innerHTML = data.label;
		row.insertCell(2).innerHTML = data.numberOfError;

		row.cells[0].align = "center";
		row.cells[1].align = "center";
		row.cells[2].align = "center";

	}
}