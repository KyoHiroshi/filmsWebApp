//var page = 1
//var selectedDataType = "json"

var pager = {
		view:"pager",
		id:"pagerA",
		template: "{common.prev()}Page {common.page()} / #limit#{common.next()}",
		size:20,
		group:5,
//		/** !!! Switch everything back to UI controlled !!! **/
//		on: {
//		onItemClick: function(id, e, node) {
//		var limit = this.config.size
////		var page = this.config.page
//		var totalRecords = this.config.count
//		var totalPages = Math.ceil(totalRecords / limit)

//		if (id === "prev") {
//		console.log(id)
//		page--
//		if (page < 1) {
//		page = 1
//		}
//		} else if (id === "next") {
//		console.log(id)
//		page++
//		if (page > totalPages) {
//		page = totalPages
//		}
//		} else {
//		page = id
//		}

//		// Update the page number in the pager
////		$$("pagerA").config.page = page

//		console.log(page)
////		console.log(limit)
////		console.log(totalRecords)
////		console.log(totalPages)

//		// Reload the datatable with the new start and page parameters
//		$$("filmtable").clearAll()
//		$$("filmtable").load("FilmController?page=" + page + "&count=" + limit, "json")
//		$$("bottom_toolbar").refresh()

//		/** Figure out how to update the Page Number **/

//		}
//		}
}


var toolbar = {
		view:"toolbar",
		id:"bottom_toolbar",
		css:"myToolbar",
		elements:[
			{ view:"button", id:"btn_insert", minWidth:65, value:"Insert" },
			{ view:"button", id:"btn_update", minWidth:65, value:"Update", disabled: true },
			{ view:"button", id:"btn_del", minWidth:65, value:"Delete", disabled: true },
			{ gravity:3 },

			{
				view: "combo",
				id: "datatype_combo",
				label: "Data Type",
				editable: false,  // Prevents typing in the combo box
				options: [
					{ id: "json", value: "JSON" },
					{ id: "xml", value: "XML" },
					{ id: "text/plain", value: "Text/Plain" }
					],
					value: "json",
					width: 200,
					on: {
						onKeyPress:function(code, e){
							// prevent key press event
							return false;
						},
						onChange: function () {
							selectedDataType = this.getValue()

//							console.log(this.getValue())
							console.log(selectedDataType)

							var filmTable = $$("filmtable")
							filmTable.clearAll()

							if (selectedDataType === "text/plain") {
								webix.ajax().get("FilmController?type=" + selectedDataType, function (text, data) {
//									console.log(text)
									var films = []
									var filmStrings = text.split("\n")
									for (var i = 0; i < filmStrings.length; i++) {
										var filmString = filmStrings[i].trim()
										if (filmString.length > 0) {
											filmString = filmString.replace("Film [", "").replace("]", "")  // remove Film prefix and brackets
											var filmData = filmString.split(", stars=")  // split into fields before stars and stars
											var postStars = filmData[1].split(", review=")  // split stars from review
											var fields = filmData[0].split(", ")
											fields.push("stars=" + postStars[0])  // add stars back to fields
											fields.push("review=" + postStars[1])  // add review back to fields
											var film = {}
											for (var j = 0; j < fields.length; j++) {
												var field = fields[j].split("=")
												film[field[0]] = field[1]
											}
											films.push(film)
										}
									}
//									console.log(films)

									var filmArray = JSON.stringify(films)

//									console.log(filmArray)


									filmTable.define("datatype", "json")

									// Issue where defining the datatype is causing problems when switching to "text/plain" sometimes
									// This work around fixes the issue by JSON load request and that sets the datatype correctly
									filmTable.load("FilmController?type=json", "json")
									// Then we are clearing the table of the loaded json data, because we don't want to use that
									filmTable.clearAll()
									// And parsing in our string data, which we do want to display
									filmTable.parse(filmArray)
								})
							} else {
								filmTable.load("FilmController?type=" + selectedDataType, selectedDataType)
							}
						}
					}
			}

			]
}

webix.ready(function(){
	var selectedDataType = "json";
	webix.ui({
		rows:[
			{
				view: "scrollview",
				scroll: "y",
				body: {
					view:"datatable",
					id:"filmtable",
					autoConfig:true,
					height: 200,
					container:"filmsContainer",
					resizeColumn:true,
					resizeRow:true,
					fixedRowHeight:false,
					rowLineHeight:20,
					rowHeight:50,
					autoheight: true,
					columns:[
						{ id:"id", header:["ID", {content:"numberFilter", placeholder:"Enter ID with =, <, >"}], adjust:"data"},
						{ id:"title", header:["Title", {content:"textFilter", placeholder:"Enter text to filter"}], adjust:"data"},
						{ id:"year", header:["Year", {content:"selectFilter"}], adjust:"data"},
						{ id:"director", header:["Director", {content:"textFilter", placeholder:"Enter text to filter"}], fillspace:2},
						{ id:"stars", header:["Stars", {content:"textFilter", placeholder:"Enter text to filter"}], fillspace:2},
						{ id:"review", header:["Review", {content:"textFilter", placeholder:"Enter text to filter"}], fillspace:4, css:"webix_cell_ellipsis"}
						],
						url: "./FilmController?type=" + selectedDataType,
						on: {
							onBeforeLoad:function(){
								this.showOverlay("Loading...");
							},
							onAfterLoad:function(){
								if (!this.count())
									this.showOverlay("Sorry, there is no data");
								else
									this.hideOverlay();
							}
						},
						select:"row",
						scheme:{
							$change:function(item){
								if (item.id%2 == 0) {
									item.$css = "webix_row_even"
								} else {
									item.$css = "webix_row_odd"
								}
							}
						},
						hover:"my_hover",
						datatype:selectedDataType,
						pager:"pagerA"
				}
			},
			{
				cols:[
					pager,
					toolbar
					]
			}
			]
	})


	// Insert
	var insertButton = $$("btn_insert")

	insertButton.attachEvent("onItemClick", function () {
		// Create a form window dynamically
		var formWindow = webix.ui({
			view: "window",
			id: "insertFormWindow",
			position: "center",
			modal: true,
			head: "Insert Form",
			width: 400,
			body: {
				view: "form",
				id: "insertForm",
				elements: [
					{ view: "text", name: "title", label: "Title" },
					{ view: "text", name: "year", label: "Year" },
					{ view: "text", name: "director", label: "Director" },
					{ view: "text", name: "stars", label: "Stars" },
					{ view: "textarea", name: "review", label: "Review" },
					{
						margin: 10,
						cols: [
							{
								view: "button",
								value: "Insert",
								css: "webix_primary",
								click: function () {
									var form = $$("insertForm")
									if (form.validate()) {
										webix.ajax().post("./InsertServlet", form.getValues(), function (text, data) {
											var response = data.json()
											console.log(response)
											if (response && response.status === "success") {
												webix.message("Film inserted successfully")
												$$("insertFormWindow").close()
												$$("filmtable").clearAll()
												$$("filmtable").load("FilmController", selectedDatatype)
											} else {
												webix.message("Failed to insert film")
											}
										}).catch(function (error) {
											webix.message("Error: " + error)
										})
									}
								}
							},
							{
								view: "button",
								value: "Cancel",
								click: function () {
									formWindow.close()
								}
							}
							]
					}
					],
					rules: {
						title: webix.rules.isNotEmpty,
						year: webix.rules.isNumber,
						director: webix.rules.isNotEmpty
					}
			}
		})

		// Show the form window
		formWindow.show()
	})
	// Insert


	// Update
	var datatable = $$("filmtable")
	var updateButton = $$("btn_update")

	datatable.attachEvent("onAfterSelect", function (id) {
		updateButton.enable()
	})

	datatable.attachEvent("onAfterUnSelect", function (id) {
		updateButton.disable()
	})

	updateButton.attachEvent("onItemClick", function () {
		var selectedId = datatable.getSelectedId()
		var selectedItem = datatable.getItem(selectedId)

		// Create a form window dynamically
		var formWindow = webix.ui({
			view: "window",
			id: "updateFormWindow",
			position: "center",
			modal: true,
			head: "Update Form",
			width: 400,
			body: {
				view: "form",
				id: "updateForm",
				elements: [
					{ view: "text", name: "id", label: "ID", readonly: true },
					{ view: "text", name: "title", label: "Title" },
					{ view: "text", name: "year", label: "Year" },
					{ view: "text", name: "director", label: "Director" },
					{ view: "text", name: "stars", label: "Stars" },
					{ view: "textarea", name: "review", label: "Review" },
					{
						margin: 10,
						cols: [
							{
								view: "button",
								value: "Update",
								css: "webix_primary",
								click: function () {
									var form = $$("updateForm")
									if (form.validate()) {
										webix.ajax().post("./UpdateServlet", form.getValues(), function (text, data) {
											var response = data.json()
											console.log(response)
											if (response && response.status === "success") {
												webix.message("Film updated successfully")
												$$("updateFormWindow").close()
												$$("filmtable").load("FilmController", selectedDatatype)
											} else {
												webix.message("Failed to update film")
											}
										}).catch(function (error) {
											webix.message("Error: " + error)
										})
									}
								}
							},
							{
								view: "button",
								value: "Cancel",
								click: function () {
									formWindow.close()
								}
							}
							]
					}
					],
					rules: {
						title: webix.rules.isNotEmpty,
						year: webix.rules.isNumber,
						director: webix.rules.isNotEmpty
					}
			}
		})

		// Populate the form fields with selected row data
		$$("updateForm").setValues(selectedItem)

		// Show the form window
		formWindow.show()
	})
	// Update


	// Delete
	var deleteButton = $$("btn_del")

	datatable.attachEvent("onAfterSelect", function (id) {
		deleteButton.enable()
	})

	datatable.attachEvent("onAfterUnSelect", function (id) {
		deleteButton.disable()
	})

	deleteButton.attachEvent("onItemClick", function () {
		var selectedId = datatable.getSelectedId()
		var selectedItem = datatable.getItem(selectedId)

		webix.confirm({
			title: "Delete",
			ok: "Yes",
			cancel: "No",
			text: "Are you sure you want to delete this film?",
			callback: function (result) {
				if (result) {
					webix.ajax().post("./DeleteServlet", { id: selectedItem.id }, function (text, data) {
						var response = data.json()
						console.log(response)
						if (response && response.status === "success") {
							webix.message("Film deleted successfully")
							$$("filmtable").clearAll()
							$$("filmtable").load("FilmController", selectedDatatype)
						} else {
							webix.message("Failed to delete film")
						}
					}).catch(function (error) {
						webix.message("Error: " + error)
					})
				}
			}
		})
	})
	// Delete
})












