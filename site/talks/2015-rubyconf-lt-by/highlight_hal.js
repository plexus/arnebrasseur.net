setTimeout(function() {
  var attrs = document.getElementsByClassName('hljs-attribute');
  for (var i=0 ; i < attrs.length ; i++) {
    var node = attrs[i];
    var text = node.textContent;
    if (text == "_links" || text == "_embedded" || text == "_controls") {
      var strong = document.createElement('strong');
      node.parentNode.insertBefore(strong, node);
      node.parentNode.removeChild(node);
      strong.appendChild(node);
    }
  }
}, 1000);
