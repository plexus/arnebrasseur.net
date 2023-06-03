---
title: The Devil in Plain Text
date: 2013-04-15
---

When developing for the web, one inevitably deals with lots of strings. When a browser talks to your killer web app they converse in plain text. String manipulation seems to be a web developer's core business.

A language like Ruby is a natural fit for this kind of job, since it inherits the exquisite text manipulation features from Perl. Here's a partial list of the languages you might be dealing with in a modern day web project :

* HTML, CSS, Javascript
* HAML, SASS/SCSS, Coffeescript
* JSON, XML, YAML
* SQL, Ruby, Regex
* URL, HTTP request/response, Mbox/MIME

## Precious Plain Text

These are all formal languages, with specific rules of what constitutes a well formed string, and with specific semantics. Yet a lot of the time we deal with them in our programs as mere strings of characters, generating them on the fly with string interpolation and templating systems, parsing them ad-hoc with regexp matching.

This is in itself an amazing accomplishment, a consequence of the Unix history of writing simple but composable tools with plain text interfaces. Plain text is The Universal Data Type, the One to Rule Them All. There is something profoundly pragmatic about reducing all problems to text manipulation. But like the One Ring, one should consider carefully when to wield its power.

## What we (still haven't) learned from SQL injection attacks

No self respecting web dev would dare to commit this code :

````ruby
User.where("age > #{params[:min_age]}")
````

Several decades of SQL injection attacks, and little [Bobby Tables](http://xkcd.com/327/), have taught us that escaping values in queries is not optional, so instead we write :

````ruby
User.where("age > ?", params[:min_age])
````

Now the database driver will 'escape' the value before inserting it into the SQL statement, making sure that in its target context it is still just a single value.

Sadly SQL seems to be the only case where this mechanism has become standardized, automated, and commonly used. We still manually `CGI.escape`, `Regexp.escape`, `json_escape`, `Shellwords.escape`, and just as often, we forget.

## Semantics, semantics, semantics!

The obvious problem is that we are dealing with "dumb" strings containing "smart" data. You, the programmer, know what is in them, but your program has no clue.

The case above is common : we move a primitive value like an integer or a string into a new context. Its meaning is supposed to stay the same (e.g integer:4, string:'foo'), but because it ends up in a context with different laws, it needs to be encoded in a particular way. Here is the literal string `"Foo&Bar, just 4U!"` in a few different contexts:

````html
<p>Foo&amp;Bar, just 4U!</p>
````

````
http://example.com/Foo%26Bar%2C+just+4U%21
````

````bash
echo Foo\\&Bar,\\ just\\ 4U\\!
````

If only Ruby Strings were a little bit smarter! But wait, they have already smartened up. Ruby 1.9 strings contain characters rather than bytes. They are aware of their own encoding, adding a level of interpretation on top of the underlying array of bytes. It would be an interesting exercise to make strings content-type aware. Here's how it could work.

````ruby
p1, p2 = String.html('<p>'), String.html('</p>')
foo = 'foo&<bar>'
p1.type
# => 'html'
foo.type
# => 'raw'
html = p1 + foo + p2
# => '<p>foo&amp;&gt;bar&lt;</p>'
````

This is a step in the right direction. It is a trivial example however, and I don't want to dwell on it too long in this post. My main point is that we could use a unified API for constructing and composing 'strings with meaning'. But it would be no more than a compromise, an iterative step up from where we are.

**Update: [Coping by James Coglan](https://github.com/jcoglan/coping) is an implementation of this idea.**

## The Universal Data Type, Revisited

Properly encoding strings matters, it is something we should always keep in mind, but there is an iceberg of other potential issues lurking underneath the water when we treat structured data as merely textual strings. We are playing doctor Frankenstein, tinkering with characters to create monstrosities of ill-formed strings with dubious semantics.

The reason we do this seems to be that our tools are so well suited for textual manipulation. We are wielding Maslow's Regexp and treating every problem as a textual nail. Surely we can do better.

Ruby has more than one parent, and while it has the powerful string processing of Perl, it is also inspired by the elegant list processing of LISP.

Long before the plain text hegemony of Unix, there was already the world of LISP in which everything is a list. Strings are lists, as are nil, true, functions, lambdas, and (surprise) lists. LISP pioneered the idea of having a unifying data type, and providing powerful tools to manipulate it. And half a century later we are still dealing with data in a representation that's several levels of abstraction removed from that.

We could be dealing with lists of tokens, or abstract syntax trees, and yet we aren't. We are concatenating strings because we need to "get shit done".

Here's an exercise : go back to the list of languages at the top and for each of them ask yourself :

* do you know a parser library for that language?
* do you know how to use it?
* can you manipulate the parsed data structure, adding, removing and changing nodes?
* can you turn the result back into its textual representation?

There is a gap in our tooling waiting to be filled. We need an elegant API contract that all parser/generator libraries can implement. Learn once, use everywhere. My hope is that you will look at all this string wizardry with different eyes. You might find it hard to unsee a pattern, it may even start to itch. And when it does, scratch.
