---
title: Exploring Ruby Macros
date: 2015-10-19
---

Macros are one of those language features that divide programmer communities. Some swear by their stupendous power, others reject them with a ferocity as if they are the breed of Satan.

When asked Matz has always made his position clear: there is no place in Ruby for macros. I think he's right, the Ruby language has very little to gain by adding them. It would only clutter up a language with an already massive syntax, and encourage people to make a huge cryptic mess.

But that doesn't mean we can't try it out anyway :D, so during the Euruko conference I coded up a proof of concept of the ["macros" gem.](https://github.com/plexus/macros)

Macros are syntax tree transformations that are applied before code is evaluated. Ruby doesn't expose its parse tree directly, but we can parse the code ourselves with the [Parser](https://github.com/whitequark/parser) gem, apply macros, then turn the result into Ruby code again with [Unparser](https://github.com/mbj/unparser), which is exactly what the [Macros](https://github.com/plexus/macros) gem does.

Macros are most common in homoiconic languages (i.e. LISPs), where the source code directly corresponds with the parsed syntax tree. If you know how to program in such a language, you also know how to write or manipulate syntax trees. This is not the case in Ruby. Knowing how code constructs map to AST nodes is a skill in itself.

For example:

``` ruby
Macros.parse('collection.inject({}) {|acc, el| acc.merge(el.name => el) }')
# =>
s(:block,
  s(:send,
    s(:send, nil, :collection), :inject,
    s(:hash)),
  s(:args,
    s(:arg, :acc),
    s(:arg, :el)),
  s(:send,
    s(:lvar, :acc), :merge,
    s(:hash,
      s(:pair,
        s(:send,
          s(:lvar, :el), :name),
        s(:lvar, :el)))))
```

## Why macros

Macros have several use cases. Let's see how these could apply to Ruby.

### Introducing custom syntax

This is one of the main reasons people reach for macros, they allow you to extend a language in a way that blends in seamlessly with the existing language constructs.

For example, most lisps have three conditional constructs, `if`, `when`, and `cond`. Often `if` and `when` are macros that rely on `cond`, but it's impossible to tell. They all look like regular language features.

``` lisp
(if x
  a
  b)

(when x
  a)

(cond
  x a
  y b)
```

Because lisp has macros, it is possible to add languages features in 3rd party code, features that would have to be provided by the language implementor otherise. There are lisp libraries that add object orientation, CSP channels, or logic programming, introducing new forms like defclass, defmethod, or go loops.

We can do similar things with Ruby macros, but because we are bound by the existing parser we can't introduce new forms. So our macro calls will resemble method calls, rather than built-in keywords. The issue here is that the parser is aware of keywords like class or def, but not of the ones our macros introduce.

As an illustration, originally I thought of having macros look like this:

``` ruby
defmacro foo(x)
  #...
end
```

This is not syntactically valid because the parser doesn't know that defmacro starts a block, so the trailing end causes it to blow up. This would have worked

``` ruby
defmacro foo(x) do
  # ...
end
```

But the `do` makes this look inconsistent, and exposes it as the hack it is, since the parser will consider this two function calls.

This all felt a bit too hodgepodgey to me, so I settled on using regular `def`, but inside a `Macros do ; end` block.

``` ruby
Macros do
  def foo(ast)

  end
end
```

### Performance

Macros are expanded at load time, the running system is no longer aware of them. This makes it possible to apply optimizations. One example would be stripping out debug calls.

Ruby has elaborate support for introspection and meta programming, but it introduces an extra layer of interpretation that takes up CPU cycles. Because of this some existing, popular Ruby projects prefer mashing strings together and eval'ing the result, instead of using existing tools like define_method, thus using a half assed, informally specified, bug ridden implementation of macros. In this case they probably should just use actual macros.

### Meta programming

There are certain problems that can perfectly be solved without macros, but somehow the level of "programming programs" makes their solution so much more elegant and concise.

This is probably the most contentious case to discuss. Your macro based solution may seem pure genius, but will you still be able to follow it in a few weeks or months? How about your colleagues?

It wouldn't be the first lisp programmer to throw away their tangled mess of macros and start over with good old reliable functions. With great power comes a great ability to screw up.

## Conclusion

Bringing macros to Ruby has been a great thought exercise, and has really made it clear to me why the two aren't a good match. Then again, maybe there are genuinely defendable use cases. Clojure's `core.async` hinges on the `go` macro, which rewrites code using async channels into a state machine. Could we port that to ruby? How about logic programming?

So maybe they will become just another tool in my toolbox. One I should never, ever need. Except when I do.
