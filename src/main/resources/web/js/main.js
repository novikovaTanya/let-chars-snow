function appendToCloud (event) {
  var p = $('<p style="left:' + (Math.floor(Math.random() * 100) - 20) + 'px">' + event.data + "</p>");
  $(".cloud" + this.n).append(p);
  p.on('webkitAnimationEnd', function(e) { this.remove(); })
};

for (var i = 1; i <= 3; i++) {
  var cloud = new WebSocket("ws://localhost:8080/snow");
  cloud.n = i;
  cloud.onmessage = appendToCloud;
};

$('.controls').on('click', function(e) {
  $(this).find('img').toggle();
  $('audio').prop('muted',!$('audio').prop('muted'));
});
