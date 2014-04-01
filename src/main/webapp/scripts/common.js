$( document ).ready(function() {
  $('span.spieler_name').each(function(index, element) {
	  var _element = $(element);
	  var url = encodeURI("spielerDetails.jsp?SpielerName="+ _element.text());
	  var link = "<a href='" + url + "'></a>";
	  _element.wrapInner(link);
  });
});