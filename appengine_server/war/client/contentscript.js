
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

RICH_SERVER = 'http://rich-page.appspot.com/';

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
  var selectedText = getSelectedText();
  if (selectedText) {
    jQuery.get(RICH_SERVER + 'Snippet?q='+selectedText+'', function(data) {
      debugger;
      if (data.resultOK) {
        // Remove previous popup.
        jQuery('#myModal').remove();
        
        // Create and show the popup.
        jQuery("body").append('<iframe id="myModal" frameborder="0" src="'+ RICH_SERVER +'Snippet?q='+selectedText+'&m=fr&v"></iframe>');
        jQuery('#myModal').css({'top':mouseY +10, 'left':mouseX, position:'absolute'}).hide().fadeIn('slow');

        // When users click close the popup.
        jQuery('body').click(function() {
          jQuery('#myModal').fadeOut('slow');
        });
      }
    });
  }
}

document.onmouseup = doSomethingWithSelectedText;
document.onkeyup = doSomethingWithSelectedText;
