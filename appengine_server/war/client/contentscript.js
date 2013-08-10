if (window.console) {
	console.log('rich-page is running')
}

// Get mouse location:
var mouseX;
var mouseY;
jQuery(document).ready(function(jQuery) {
	jQuery(document).mousemove(function(e) {
		mouseX = e.pageX;
		mouseY = e.pageY;
	});

	jQuery('body').click(function() {
		closeOwowContextBox2();
	});
});

DEBUG = "http://localhost:8888/";
NOTDEBUG = 'http://rich-page.appspot.com/'
RICH_SERVER = DEBUG;

var CLOSEITNEXTCLICK = false;

// Setting trim function
if (!String.prototype.trim) {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, '');
	};
}

// // Get selected text:
function getSelectedText() {
	var text = "";
	if (typeof window.getSelection != "undefined") {
		text = window.getSelection().toString();
	} else if (typeof document.selection != "undefined"
			&& document.selection.type == "Text") {
		text = document.selection.createRange().text;
	}
	return text.trim();
}

function doSomethingWithSelectedText() {
	markSelection();
	/*
	 * var selectedText = getSelectedText(); if (selectedText) {
	 * jQuery.get(RICH_SERVER + 'Snippet?q='+selectedText+'', function(data) {
	 * debugger; if (data && data.resultOK) { // Remove previous popup.
	 * jQuery('#myModal').remove(); // Create and show the popup.
	 * jQuery("body").append('<iframe id="myModal" frameborder="0" src="'+
	 * RICH_SERVER +'Snippet?q='+selectedText+'&v"></iframe>');
	 * jQuery('#myModal').css({'top':mouseY +10, 'left':mouseX,
	 * position:'absolute'}).hide().fadeIn('slow'); // When users click close
	 * the popup. jQuery('body').click(function() {
	 * jQuery('#myModal').fadeOut('slow'); }); } }); }
	 */
}

document.onmouseup = doSomethingWithSelectedText;
document.onkeyup = doSomethingWithSelectedText;

function closeOwowContextBox() {
	jQuery('#myModal').hide();
	// jQuery('#owow_rich_div').hide();
}

function closeOwowContextBox2() {
	if (CLOSEITNEXTCLICK) {
		if ($('#myModal').is(':visible')) {
			jQuery('#myModal').hide();
			CLOSEITNEXTCLICK = false;
		}
	}
	// jQuery('#owow_rich_div').hide();
}

var markSelection = (function() {
	var markerTextChar = "\ufeff";
	var markerTextCharEntity = "&#xfeff;";

	var markerEl, markerId = "sel_" + new Date().getTime() + "_"
			+ Math.random().toString().substr(2);

	var selectionEl;
	var jOwowRichDiv;

	var lastText;
	var hold = false;
	var lastPos = {
		top : 0,
		left : 0
	};

	return function() {
		var sel, range;
		var selectedText = getSelectedText();

		if (selectedText != lastText)
			jQuery("#owow_rich_div").remove();
		var dataReady;

		if (selectedText) {
			if (document.selection && document.selection.createRange) {
				// Clone the TextRange and collapse
				range = document.selection.createRange().duplicate();
				range.collapse(false);

				// Create the marker eleme nt containing a single invisible
				// character
				// by creating literal HTML and insert it
				range.pasteHTML('<span id="' + markerId
						+ '" style="position: relative;">'
						+ markerTextCharEntity + '</span>');
				markerEl = document.getElementById(markerId);
			} else if (window.getSelection) {
				sel = window.getSelection();

				if (sel.getRangeAt) {
					range = sel.getRangeAt(0).cloneRange();
				} else {
					// Older WebKit doesn't have getRangeAt
					range.setStart(sel.anchorNode, sel.anchorOffset);
					range.setEnd(sel.focusNode, sel.focusOffset);

					// Handle the case when the selection was selected backwards
					// (from the end to the start in the
					// document)
					if (range.collapsed !== sel.isCollapsed) {
						range.setStart(sel.focusNode, sel.focusOffset);
						range.setEnd(sel.anchorNode, sel.anchorOffset);
					}
				}

				range.collapse(false);

				// Create the marker element containing a single invisible
				// character
				// using DOM methods and insert it
				markerEl = document.createElement("span");
				markerEl.id = markerId;
				markerEl.appendChild(document.createTextNode(markerTextChar));
				range.insertNode(markerEl);
			}

			if (markerEl) {

				// Find markerEl position
				// http://www.quirksmode.org/js/findpos.html
				var obj = markerEl;
				var left = 0, top = 0;
				do {
					left += obj.offsetLeft;
					top += obj.offsetTop;
				} while (obj = obj.offsetParent);

				top += markerEl.offsetHeight;
				obj = markerEl.parentNode;
				var text = obj.innerText;
				for ( var i = 0; i <= 5 && (text.split(' ', 14)).length < 13; i++) {
					obj = obj.parentNode;
					text = obj.innerText;
				}
				markerEl.parentNode.removeChild(markerEl);

				if (lastText == selectedText && lastPos.top == top
						&& lastPos.left == left && hold)
					return;
				hold = true;
				lastText = selectedText;
				lastPos = {
					top : top,
					left : left
				}
				jQuery("body")
						.append(
								'<div id="owow_rich_div"><img id="owow_rich_img" style="width:100%;height:100%" src="'
										+ RICH_SERVER
										+ 'icons/question.png" /></div>');

				var toShow = true;
				var toHide = true;
				dataReady = false;

				jQuery("#owow_rich_div").css({
					'width' : 20,
					'height' : 20,
					"background-color" : "#fff",
					'top' : top,
					'left' : left,
					// 'z-index' : 20000,
					position : 'absolute'
				}).show().click(
						function() {
							if (dataReady) {
								jQuery('#myModal').show().css({
									'display' : 'block'
								});
								console.log('clicked');
								CLOSEITNEXTCLICK = false;
								setTimeout(function() {
									CLOSEITNEXTCLICK = true;
								}, 1000);

							} else {
								jQuery(this).children().attr("src",
										RICH_SERVER + "img/throbber.gif");
								toHide = false;
							}
						});
				// alert(text);
				var page_url = window.location.href;
				jQuery.get(RICH_SERVER + 'Snippet?q=' + selectedText + '&url=' + page_url, function(data) {
					if (data && data.resultOK && toShow) {

						dataReady = true;
						// Remove previous popup.
						jQuery('#myModal').remove();
						// Create and show the popup.
						var page_url = window.location.href;
						jQuery("body").append(
								'<iframe id="myModal" frameborder="0" src="'
										+ RICH_SERVER + 'Snippet?q='
										+ selectedText + '&v&url='
										+ page_url + '"></iframe>');
						jQuery('#myModal').css({
							'width' : 335,
							'height' : 410,
							'top' : top - 26,
							'left' : left - 26,
							'z-index' : 10000,
							position : 'absolute'
						}).show().bind('click', function(event) {
							CLOSEITNEXTCLICK = true;
						});
						if (toHide) {
							jQuery('#myModal').hide();
						}
						CLOSEITNEXTCLICK = true;
					} else {
						jQuery("#owow_rich_div").hide();
						console.log("no results for " + selectedText)
					}
				}).fail(function() { 
					jQuery("#owow_rich_div").hide();
					console.log("error for " + selectedText)
				});
			}
		} else {
			hold = false;
		}
	};
})();
