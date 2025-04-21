$(document).ready(function(){
	var page = new Page();
	page.init();
});

function Page(){
	
}

Page.prototype.init = function(){
	var that = this;
	$("#appointment-dt").datepicker({autoclose:true});
	$("#services").multiselect();
	this.bindButtons();
	$.get(root + "/appointments/all", function(data){
		that.displayTable(data);
	});
};

Page.prototype.bindButtons = function(){
	this.bindSaveButton();
};

Page.prototype.bindSaveButton = function(){
	var that = this;
	
	$("#appointment-form").submit(function(e){
		e.preventDefault();
		var data = $(this).serialize();
		$.post(root + "/appointments/save", data, function(data){
			that.displayTable(data);
			$(".multiselect").multiselect("refresh");	
			$("#appointment-form")[0].reset();
			$("#add-appointment-form").modal('hide');
		}, "json");
		
	});
};

Page.prototype.displayTable = function(rows){
	var tmpRows = [];
	var $tableBody = $("#appointment-table tbody");

	for(var i = 0; i < rows.length; i++){
		tmpRows.push(this.createRowHtml(rows[i]));
	}

	$tableBody.empty();
	$tableBody.append(tmpRows);
};

Page.prototype.createRowHtml = function(row){
	var date = row.appointmentDt.monthValue + "/" + row.appointmentDt.dayOfMonth + "/" + row.appointmentDt.year;
	var $row = $("<tr/>");
	var $userCell = $("<td/>");
	var userLink = $("<a/>").attr("href", root+"/appointments/"+ row.appointmentId).text(row.user.firstName + " " + row.user.lastName);
	var $dateCell = $("<td/>").text(date);
	$userCell.append(userLink);
	var $makeCell = $("<td/>").text(row.automobile.make);
	var $modelCell = $("<td/>").text(row.automobile.model);
	var $yearCell = $("<td/>").text(row.automobile.year);
	var $servicesCell = $("<td/>").text(row.services.join(", "));
	var $statusCell = $("<td/>").text(row.status);
	$row.append($userCell, $dateCell, $makeCell, $modelCell, $yearCell, $servicesCell, $statusCell);
	return $row;
};