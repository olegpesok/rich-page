$(document).ready
	(function() {
		bodyElem = document.getElementsByTagName('body')[0];
		page={$page};
		start = Math.max(page - 5, 1);

		dummy = document.getElementById('dummy');
		for ( var i = start; i < start + 10; i++) {

			$("body").append('<a href="./AdminPage?act=viewAll&page=' + i + '">' + i + '</a> | ');
		}
	});