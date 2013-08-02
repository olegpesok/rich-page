
if (window.console){
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
});

RICH_SERVER = 'http://localhost:8888/';

//// Get selected text:
function getSelectedText() {
  var text = "";
  if (typeof window.getSelection != "undefined") {
      text = window.getSelection().toString();
  } else if (typeof document.selection != "undefined" && document.selection.type == "Text") {
      text = document.selection.createRange().text;
  }
  return text;
}


function doSomethingWithSelectedText() {
	markSelection();
	/*
  var selectedText = getSelectedText();
  if (selectedText) {
    jQuery.get(RICH_SERVER + 'Snippet?q='+selectedText+'', function(data) {
      debugger;
      if (data && data.resultOK) {
        // Remove previous popup.
        jQuery('#myModal').remove();
        
        // Create and show the popup.
        jQuery("body").append('<iframe id="myModal" frameborder="0" src="'+ RICH_SERVER +'Snippet?q='+selectedText+'&v"></iframe>');
        jQuery('#myModal').css({'top':mouseY +10, 'left':mouseX, position:'absolute'}).hide().fadeIn('slow');

        // When users click close the popup.
        jQuery('body').click(function() {
          jQuery('#myModal').fadeOut('slow');
        });
      }
    });
  }*/
}

document.onmouseup = doSomethingWithSelectedText;
document.onkeyup = doSomethingWithSelectedText;



var markSelection = (function() {
	var markerTextChar = "\ufeff";
	var markerTextCharEntity = "&#xfeff;";

	var markerEl, markerId = "sel_" + new Date().getTime() + "_"
			+ Math.random().toString().substr(2);

	var selectionEl;

	return function() {
		var sel, range;

		var selectedText = getSelectedText();
		if(selectedText)
		{
			if (document.selection && document.selection.createRange) {
				// Clone the TextRange and collapse
				range = document.selection.createRange().duplicate();
				range.collapse(false);

				// Create the marker eleme	nt containing a single invisible character
				// by creating literal HTML and insert it
				range.pasteHTML('<span id="' + markerId + '" style="position: relative;">' + markerTextCharEntity + '</span>');
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

				// Create the marker element containing a single invisible character
				// using DOM methods and insert it
				markerEl = document.createElement("span");
				markerEl.id = markerId;
				markerEl.appendChild(document.createTextNode(markerTextChar));
				range.insertNode(markerEl);
			}

			if (markerEl) {

			// Find markerEl position http://www.quirksmode.org/js/findpos.html
				var obj = markerEl;
				var left = 0, top = 0;
				do {
					left += obj.offsetLeft;
					top += obj.offsetTop;
				} while (obj = obj.offsetParent);
				top+= markerEl.offsetHeight;
				left-= 13;
				top -= 13;
				markerEl.parentNode.removeChild(markerEl);
			
					jQuery.get(RICH_SERVER + 'Snippet?q='+selectedText+'', function(data) {
							if (data && data.resultOK) {
								// Remove previous popup.
				        jQuery('#myModal').remove();
				        
				        // Create and show the popup.
				        jQuery("body").append('<iframe id="myModal" frameborder="0" src="'+ RICH_SERVER +'Snippet?q='+selectedText+'&v"></iframe>');
				        jQuery('#myModal').css({'width':335,'height':410,'top':top-13, 'left':left-13, position:'absolute'}).hide().fadeIn('slow');

				        // When users click close the popup.
				        jQuery('body').click(function() {
				          jQuery('#myModal').fadeOut('slow');
				        });
				      }
				    });
		}}
	};
})();
