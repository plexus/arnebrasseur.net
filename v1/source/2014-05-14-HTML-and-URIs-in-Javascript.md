---
title: HTML and URI's in Javascript
date: 2014-05-14
---

I have [written](http://devblog.arnebrasseur.net/2013-04-plain-text) and [spoken](http://devblog.arnebrasseur.net/speaking.html#rulu2013) a [few](http://devblog.arnebrasseur.net/speaking.html#eurucamp2013) [times](http://devblog.arnebrasseur.net/speaking.html#rubyconfar2014) about the perils of string arithmetic on formal data. Those talks were focused on theory and fundamentals, in this post you'll get very practical tips.

When programming for the web there are two types of formal data you'll come across *All The Time*, HTML and URIs. These formats have well specified structure and semantics, so that machines can unambiguously generate and consume them. Don't try to do what the machine does better, or you'll shoot yourself in the foot.

## URIs

This one is easy: use [URI.js](https://github.com/medialize/URI.js). It is unfortunate that browsers don't have built-in APIs to deal with URIs in a sane way, but URI.js really gives you all you need.

Some simple examples

```js
// bad
window.location.origin + '/foo/bar'
//good
URI('/foo/bar').absoluteTo(window.location.origin).toString()

// bad
uri = 'http://example.com/posts/' + escapeURI(postId) + '/comments/' + escapeURI(commentId)
// good
uri = URI.expand('http://example.com/posts/{pid}/comments/{cid}', {pid: postId, cid: commentId))

// complete example from the README
URI("http://example.org/foo.html?hello=world")
  .username("rodneyrehm")
    // -> http://rodneyrehm@example.org/foo.html?hello=world
  .username("")
    // -> http://example.org/foo.html?hello=world
  .directory("bar")
    // -> http://example.org/bar/foo.html?hello=world
  .suffix("xml")
    // -> http://example.org/bar/foo.xml?hello=world
  .query("")
    // -> http://example.org/bar/foo.xml
  .tld("com")
    // -> http://example.com/bar/foo.xml
  .query({ foo: "bar", hello: ["world", "mars"] });
```

There are tons of edge cases that this covers that your naive let's-mash-some-strings-together code does not, including proper escaping.

**Update**

I should have mentioned this earlier, [URI templates](http://tools.ietf.org/html/rfc6570) are actually a RFC standardized mechanism for building and recognizing URIs. This is what `URI.expand` above is based on. It's a very useful and underused mechanism.

## HTML

In contrast to URIs, browsers do come with a sane API for building HTML, it's called the DOM (Document Object Model) API.

```js
var divNode  = document.createElement("div");
var textNode = document.createTextNode("We all live in happy HTML! &<>");
divNode.appendChild(textNode);
document.body.appendChild(divNode);
```

So that's great, except that no one wants to actually write code like that, so people end up committing atrocities like setting `innerHTML` with the tagsoup of the day. Notice though how this version has already eliminated the need of manually calling escape functions.

The highly informative [MDN](https://developer.mozilla.org) article [DOM Building and HTML Insertion](https://developer.mozilla.org/en-US/Add-ons/Overlay_Extensions/XUL_School/DOM_Building_and_HTML_Insertion). has some great tips, for instance a handy jsonToDOM function.

The implementation there is already quite clever, allowing one to set event handlers in one go. Since this article is meant for people building browser extensions, it also has some XUL stuff that's not relevant when programming for the web.

```js
document.body.appendChild(jsonToDOM(
  ["div", {},
    ["a", { href: href, onclick: function() { } }, text])));
```

Great idea, and with some tweaking very useful in a browser context. But chances are you're already using jQuery, in which case I have good news for you: jQuery has everything covered!

```js
var divNode = $('<div>', {class: 'my-div'}).append($('<a>', {href: '..'}));
```

The `$('<tag>', {attributes})` syntax provides an easy way to build DOM objects. The result is a [jQuery object](http://learn.jquery.com/using-jquery-core/jquery-object/). You'll have to unwrap it to get to actual DOM element.

```js
var domNode = divNode[0];
```

You might want to convert this to an HTML string now. In that case it's highly likely you're doing it wrong, but there are some cases where this is actually legit, e.g. Ember.js Handlebars helpers don't allow returning DOM nodes. I assume this will change with HTMLBars.

In this case keep in mind that calling `html()` on the jQuery object will only return the *inner* HTML. You can get the full thing from the DOM node though.

```js
var nodeHTML = divNode[0].outerHTML;
```

For example in Ember.js:

```js
Ember.Handlebars.registerBoundHelper('linkToPost', function(postId) {
  var uri  = URI.expand('/posts/{id}', {id:  postId});
  var html = $('<a>', {href: uri, text: "goto post"})[0].outerHTML;
  return new Handlebars.SafeString(html);
});
```

## Putting the two together

Take this simple function

```js
function linkToPost(postId) {
  var uri = '/posts/' + encodeURI(postId);
  return '<a href="' + uri + '">goto post</a>';
}
```

The problem here is that there are two levels of interpretation going on. While the URI is correctly escaped in itself, when placing it in the context of HTML, in particular as an attribute value, there's extra escaping that needs to happen, so the value can't break out of the attribute (by including ' or ") or out of the HTML tag (by including > or <).

Escaping always depends on context, and if there are multiple levels of context the manual approach will always fail, without fault. In short if you find yourself:

* writing HTML fragments inside strings ('<a href=...')
* calling escape functions (e.g. for URI or HTML) manually

think if you can let some other component that knows the details of the language you're generating better than you do, to do the work for you. Here's a corrected version of the above.

```js
function linkToPost(postId) {
  var uri = URI.expand('/posts/{id}', {id:  postId});
  return $('<a>', {href: uri, text: "goto post"});
}
```

## Finally

Browsers don't come with a function for manually escaping HTML. That is because *you don't need it*. Having it there might encourage bad practices and hence do more bad than good.

But as with everything there are exceptions. If you really need to escape HTML, and you're sure your use case is legit, there are a few options.

Let the browser do it for you:

```js
var divNode  = document.createElement("div");
var textNode = document.createTextNode("We all live in happy HTML! &<>");
divNode.innerHTML // "We all live in happy HTML!We all live in happy HTML! &amp;&lt;&gt;"
```

Use [Underscore.js](http://underscorejs.org/)

```js
_.escape("We all live in happy HTML! &<>");
// "We all live in happy HTML!We all live in happy HTML! &amp;&lt;&gt;"
```

or copy any of the functions you find on the web. Make sure it escapes &lt; &gt; ' " &amp;.