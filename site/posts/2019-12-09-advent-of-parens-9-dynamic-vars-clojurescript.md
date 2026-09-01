---
title: "Advent 2019 part 9, Dynamic Vars in ClojureScript"
slug: "advent-of-parens-9-dynamic-vars-clojurescript"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2019-12-09T15:42:00.000Z"
updated_at: "2026-05-16T13:41:56.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:10"]
---

_This post is part of[Advent of Parens 2019](https://lambdaisland.com/blog/2019-11-25-advent-of-parens), my attempt to publish one blog post a day during the 24 days of the advent._

Clojure has this great feature called Dynamic Vars, it lets you create variables which can be dynamically bound, rather than lexically. Lexical (from Ancient Greek λέξις (léxis) _word_) in this case means “according to how it is written”. `let` bindings for instance are lexical.
    
    
    (defn hello [x]
      (str "hello " x))
    
    (defn greetings []
      (str "greetings" foo)) ;; *error*
    
    (let [foo 123]
      (hello foo)
      (greetings))
    

The `let` introduces a _lexical_ binding for `foo`, it can be accessed in `(hello foo)` because lexically (textually) it’s within the `let`. `greetings`is outside of the let so it can’t “see” `foo`.

_Dynamic_ bindings are introduced with `binding`, and are visible within the same call stack. Any function that is called on the same thread can “see” the binding. For this to work you need to add metadata marking the var as `:dynamic`, and you need to give the name earmuffs (that’s what those asterisks are called. Cute, right?)
    
    
    (def ^:dynamic *foo*)
    
    (defn greetings []
      (str "greetings" *foo*) 
    
    (binding [*foo* 123]
      (greetings))
    

Ok so this is great and handy (although don’t overuse it, but that’s a topic for another time). But how does this work in ClojureScript. For one ClojureScript does not have vars, they compile to plain JavaScript globals. JavaScript (and thus ClojureScript) also doesn’t have threads, so the mechanisms provided by vars like root binding and thread bindings are not relevant. But dynamic vars do exist in ClojureScript… curious! I’ve been wondering for some time now how they work, and today I finally investigated.

Here’s how `binding` is implemented. Curious indeed! It calls the analyzer to check if the vars are actually defined as dynamic, and if that doesn’t throw a compile-time error it will just call `with-redefs`.
    
    
    (core/defmacro binding
      "binding => var-symbol init-expr
      Creates new bindings for the (already-existing) vars, with the
      supplied initial values, executes the exprs in an implicit do, then
      re-establishes the bindings that existed before.  The new bindings
      are made in parallel (unlike let); all init-exprs are evaluated
      before the vars are bound to their new values."
      [bindings & body]
      (core/let [names (take-nth 2 bindings)]
        (cljs.analyzer/confirm-bindings &env names)
        `(with-redefs ~bindings ~@body)))
    

`with-redefs` is even more curious.
    
    
    (core/defmacro with-redefs
      "binding => var-symbol temp-value-expr
      Temporarily redefines vars while executing the body.  The
      temp-value-exprs will be evaluated and each resulting value will
      replace in parallel the root value of its var.  After the body is
      executed, the root values of all the vars will be set back to their
      old values. Useful for mocking out functions during testing."
      [bindings & body]
      (core/let [names (take-nth 2 bindings)
                 vals (take-nth 2 (drop 1 bindings))
                 orig-val-syms (map (comp gensym #(core/str % "-orig-val__") name) names)
                 temp-val-syms (map (comp gensym #(core/str % "-temp-val__") name) names)
                 binds (map core/vector names temp-val-syms)
                 resets (reverse (map core/vector names orig-val-syms))
                 bind-value (core/fn [[k v]] (core/list 'set! k v))]
        `(let [~@(interleave orig-val-syms names)
               ~@(interleave temp-val-syms vals)]
           ~@(map bind-value binds)
           (try
             ~@body
             (finally
               ~@(map bind-value resets))))))
    

This one takes a bit more effor to read, but the clue is in this line: `bind-value (core/fn [[k v]] (core/list 'set! k v))`. Basically what the example from above translates to is:
    
    
    (set! *foo* 123)
    (try
      (greetings)
      (finally
        (set! *foo* nil)))
    

That’s it, that’s all there is. Somewhat anti-climactic I must admit. It just sets the value, calls your code, and sets it back afterwards.

([with-redefs and binding in context](https://github.com/clojure/clojurescript/blob/23cedecbf4f704f9fee672e395bbfa1e3fe3ee1a/src/main/clojure/cljs/core.cljc#L2232-L2267))

So do you need `:dynamic` in ClojureScript? Or can you just use `with-redefs`, or even just `set!`? It looks like it, although there are of course good reasons (readability, showing intent) to still use them if you want to rebind something later on.

So case closed? Not really. I had this nagging suspicion that there might still be more going on, so I went through the compiler and analyzer to see what they do with things marked as `:dynamic`, and sure enough in `cljs.compiler` we find this:
    
    
    (defmethod emit* :invoke
      [{f :fn :keys [args env] :as expr}]
      (let [info (:info f)
            fn? (and ana/*cljs-static-fns*
                     (not (:dynamic info))
                     (:fn-var info))
            ,,,] ,,,))
    

So what are we looking at? This is the code that emits (prints out JavaScript) function invocations. Usually this just emits things like `foo.call(123)`, but there’s an optimization you can enable that makes calling multi-arity functions a bit faster. When you have a function like
    
    
    (defn foo
      ([arg1] ,,,)
      ([arg1 arg2] ,,,))
    

Then ClojureScript will emit three functions: one that takes one argument, one that takes two, and one that dispatches to the right one based on `arguments.length`.

When you enable the `:static-fns` compiler option then ClojureScript will try to avoid calling the dispatch function, calling the specific arity versions directly. But… this would break when you rebind the dispatch function, so if `:static-fns` is enabled, and the function you’re calling is dynamic, then emit a regular `foo.call(...)`.

(Note that there are some caveats with `:static-fns`. At the ClojureScript Internals workshop that David Nolen gave in Berlin earlier this year he mentioned it may actually perform worse than when you leave it off. I don’t remember the exact details, it might have been because it prevents some Google Closure Compiler optimizations.)

**[Comment on ClojureVerse](https://clojureverse.org/t/advent-of-parens-2019/5159)**
