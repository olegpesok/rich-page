//functions for adding and removing classes
function hasClass(element, newClass) {
	return element.className
			.match(new RegExp('(\\s|^)' + newClass + '(\\s|$)'));
}

function addClass(element, newClass) {
	if (!hasClass(element, newClass))
		element.className += " " + newClass;
}

function removeClass(element, newClass) {
	if (hasClass(element, newClass)) {
		var r = new RegExp('(\\s|^)' + newClass + '(\\s|$)');
		element.className = element.className.replace(r, ' ');
	}
}

function getToggleListener(index, toggleIDList, contentIDList, $scroller) {

	var toggle = document.getElementById(toggleIDList[index]);
	var toggleList = []
	for ( var i = 0; i < toggleIDList.length; i++) {
		toggleList.push(document.getElementById(toggleIDList[i]))
	}
	;

	var content = document.getElementById(contentIDList[index]);
	var contentList = []
	for ( var i = 0; i < contentIDList.length; i++) {
		contentList.push(document.getElementById(contentIDList[i]))
	}
	;

	function closeAll() {
		for ( var i = 0; i < toggleList.length; i++) {
			removeClass(toggleList[i], "context-box-selected");
		}
		;

		for ( var i = 0; i < contentList.length; i++) {
			removeClass(contentList[i], "context-box-open");
		}
		;
	}

	return function(e) {
		closeAll();
		addClass(toggle, "context-box-selected");
		addClass(content, "context-box-open");
	}
}

function setUpToggles($scroller) {
	var toggleIDs = [ "context-box-info-toggle", "context-box-links-toggle" ];
	var contentIDs = [ "context-box-info", "context-box-links" ];

	for ( var i = 0; i < toggleIDs.length; i++) {
		var id = toggleIDs[i]
		var element = document.getElementById(id);
		element.addEventListener("click", getToggleListener(i, toggleIDs,
				contentIDs, $scroller))
	}
}

$(document).ready(function() {
	var $scroller = $('#context-box-content-container').slimScroll({
		alwaysVisible : false,
		railVisible : true,
		allowPageScroll:true,
		height : '260px',
		color : "rgb(40, 100, 237)"
	});
	setUpToggles($scroller);
});