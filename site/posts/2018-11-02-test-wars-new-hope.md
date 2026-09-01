---
title: "Test Wars: A New Hope"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2018-11-02T17:51:58.000Z"
updated_at: "2026-05-16T13:42:05.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:23"]
---

Yesterday was the first day for me on a new job, thanks to [Clojurists Together](https://clojuriststogether.org/) I will be able to dedicate the coming three months to improving [Kaocha](https://github.com/lambdaisland), a next generation test runner for Clojure.

A number of projects applied for grants this quarter, some much more established than Kaocha. Clojurists Together has been asking people through their surveys if it would be cool to also fund “speculative” projects, and it seems people agreed.

I am extremely grateful for this opportunity. I hope to demonstrate in the coming months that Kaocha holds a lot of potential, and to deliver some of that potential in the form of a tool people love to use.

In this post I’d like to set the stage by providing a bit of context, and explaining the problems Kaocha tries to solve, as well as providing a rough plan for the coming months.

## Conventions and interfaces

When growing an ecosystem it’s important to have conventions and well defined interfaces in place. This allows parts of the ecosystem to develop separately, while still functioning as a whole.

Clojure has done a fairly good job of providing this shared foundation with regard to testing, and many tools and libraries have been built on top of it. At the same time some have chosen to go off and do their own thing, possibly because the affordances provided by Clojure’s testing conventions did not support their use cases.

When talking about a testing ecosystem we’re talking about a bunch of different things, which may or may not come in a single package.

  1. Test syntax: how to define tests in code
  2. Test semantics: how does the system define what is a test
  3. Assertion libraries: ways to write falsifiable test statements
  4. Mocking/stubbing libraries: utilities to help with test isolation
  5. Test fixtures and factories: utilities for building up test data and establishing application states
  6. Progress reporters: ways to get progress information while tests are running
  7. Result reporters: summarizing the results of a test run for human or machine consumption
  8. Test runners: comprehensively load, detect, and run test suites



Clojure 1.0 provided exactly one of these, number 2. When a var has `:test`metadata, then that is considered a test, and can be run with `clojure.core/test`. You could argue it also had a bit of 3 with `clojure.core/assert`, and a bit of 4 with `with-redefs`.

Clojure 1.1 added the `clojure.test` library which improves on number 3 (assertions) with an extensible macro: `(is)`, as well as providing syntax (1), and reporters (6 and 7).

More importantly it provides conventions so you can bring your own version of these things. `(is ...)` is powered by the `clojure.core/assert-expr`multimethod, so for instance [matcher-combinators](https://github.com/nubank/matcher-combinators) provides a custom `(is (match? ...))` assertion.

The reporter in `clojure.test` is also a multimethod. It receives “events” like `:begin-test-var`, `:pass`, or `:fail`, and can be extended to handle new types of event, like matcher-combinator’s `:mismatch`. It can also be swapped out with `with-redefs`, for instance when running tests through CIDER a custom reporter will be used to collect test results.

There is a chance of conflict here though, what if one library extends `report`, and the other replaces it? This illustrates a general problem with `clojure.test`, to build tooling on top of it you are forced to rely on details of the implementation, leading to brittle results that don’t compose well.

Clojure itself does not contain a test runner. There are some utilities in `clojure.test` to execute tests once they are defined, but no comprehensive way to say “load all test namespaces under this directory path, find all tests in them, and run them.” The role of a test runner has initially been performed by Leiningen, which includes `lein test`. It’s fairly basic but gets the job done. It will set an appropriate process exit code (important on CI), and provides some ways to filter or skip tests. It uses stock `clojure.test` output which is rather spartan.

Several libraries and Leiningen plugins came about to provide nicer output for failed assertions ([humane-test-output](https://github.com/pjstadig/humane-test-output), [ultra](https://github.com/venantius/ultra)), as well as things like a nice progress bar, output capturing and running tests in parallel ([eftest](https://github.com/weavejester/eftest)).

When Boot came around it also needed a test runner, and since `lein test` is tightly linked to Leiningen the boot folks implemented [boot-test](https://github.com/adzerk-oss/boot-test), which uses humane-test-output under the hood.

The fine folks from Metosin later created [bat-test](https://github.com/metosin/bat-test), which is perhaps the most feature complete of the ones covered so far. It works with boot and leiningen, supports eftest and cloverage, has global pre/post hooks and more.

And then `tools.deps` / Clojure CLI came around and we were back to square one, waiting for someone to port their existing test runner, or implement a new one. Cognitect came around with [cognitect-labs/test-runner](https://github.com/cognitect-labs/test-runner), which is probably the least feature complete of the bunch.

Finally there’s [circleci.test](https://github.com/circleci/circleci.test), which provides some really neat features like enforcing tests are pure and generating junit.xml output.

## Imperative vs functional

If we have this many test runners already, why create another one? All of the mentioned projects are available for a subset of Clojure build tools (lein, boot, Clojure CLI), and implement a subset of all possible test runner features, but they don’t compose. I can’t take some features of one and a few of the other.

Clojure’s philosophy is to provide pure functions over common data structures, so it’s easy to piece things together. The problem is that running tests is inherintly imperative. You load code, execute tests in order, and observe events and exceptions that occur. These get aggregated in some kind of mutuble structure, while progress is reported to the user.

The problem is that this kind of code doesn’t compose well, and so we end up with all these different projects with very little opportunity for synergy or reuse.

Kaocha addresses this by turning the imperative problem into a functional one. At the heart of Kaocha is the abstraction of a testable, a Clojure map with an `:id`, a `:type`, and any other information that characterizes this test. When you `(run testable)` you get a similar map back, but this time containing test results: the amount of assertions that ran, errors, failures, captured exceptions, captured output to stdout, and so forth.

`run` is by no means pure, since a test can cause (and verify) any number of side effects, but tooling authors can choose to ignore the side effects. All they care about is captured and returned as pure data.

Testables are built up hierarchically. You can `run` a test-suite, which will `run` any namespaces it contains, which in turn will `run` each test var. The top level testable is called the test-plan.
    
    
    (def test-plan
      {:tests [{:id :unit
                :type :clojure.test
                :tests [{:id :kaocha.result-test
                         :type :ns
                         :tests [{:id :kaocha.result-test/totals-test
                                  :type :var
                                  ,,,}]}]}]})
    
    (run test-plan)
    ;;=>
    {:tests [{:id :unit
              :type :clojure.test
              :tests [{:id :kaocha.result-test
                       :type :ns
                       :tests [{:id :kaocha.result-test/totals-test
                                :type :var
                                :count 1
                                :fail 0
                                :pass 1
                                :error 0
                                ,,,}]}]}]}
    

Kaocha is still built upon the foundations of `clojure.test`, and uses the same “reporter” abstraction for providing real time progress info. This isn’t always easy, as mentioned before `clojure.test` makes it easy to step on each other’s toes when extending or replacing the reporter. Kaocha tries to prevent this by providing its own conventions for extensions that are more readily composable. You can add additional reporters, instead of replacing the reporter wholesale. You can make Kaocha aware of custom event types through keyword hierarchies, and by implementing multimethods you can influence how custom events are rendered.

See the [docs about custom reporters](https://cljdoc.org/d/lambdaisland/kaocha/CURRENT/doc/8-extending#reporters)for more info.

## More than one type of tests

Long gone are the days that `clojure.test` was the only game in town. What if you prefer to write your tests with [midje](https://github.com/marick/Midje), [fudje](https://github.com/jimpil/fudje), [expectations](https://github.com/clojure-expectations/expectations), [selvage](https://github.com/nubank/selvage), or [cucumber](https://github.com/cucumber/cucumber-jvm-clojure)?

Some of these stick to the clojure.test conventions, and can be used with any of the above test runners, others do not. Midje in particular is interesting here, as it provides its own version of more or less everything we mentioned: syntax, test runner, mocking/stubbing and more, and it does it in a very idiosyncratic way, deliberately moving away from clojure.test.

So you can’t for instance use Midje with bat-test, or circleci.test, you can only use Midje with Midje. Conversely you can’t easily use features of Midje without adopting it wholesale.

What’s worse, imagine you want to use one testing library for your unit tests, and another for your integration tests, now you’re dealing with separate tools, each with their own configuration and interface.

Kaocha tries to solve this by providing a test type (or test suite) abstraction. To add support for a given test type to Kaocha you implement two multimethods, `load` and `run`. Getting these right isn’t trivial, as they need to do the double book keeping mentioned above, invoking the reporter as things are happening, and collecting and returning the results in a functional way. What they don’t need to worry about is all the rest of the test runner machinery. They don’t need to provide a CLI or REPL interface, they don’t need to think about functionality for filtering or skipping tests, for profiling or output capturing, all of that is provided by Kaocha.

Kaocha does not need to know about your test type up front, it just needs to be on the classpath and follow certain naming conventions. This means that anyone can implement a new test type and push it to clojars, encouraging experimentation and innovation.

Besides supporting command line use (`kaocha.runner`), or REPL interaction (`kaocha.repl`), Kaocha can also be invoked directly by tooling (`kaocha.api`). Perhaps in the future editor integrations based on Kaocha will gain support for all these different test types without any extra work.

## Moar features

People have many different approaches to writing and running tests, and this leads to different opinions about the features a test tool should support. One person’s essentials is an other person’s bloatware.

Kaocha tries to address this through plugins. Like test types these can be released by anyone, they are just Clojure libraries that follow certain naming conventions. Because of the functional, data-driven foundation that Kaocha provides plugin authors can ignore a lot of the details of running tests, and focus on the problem they are trying to solve.

For example the [kaocha-junit-xml](https://github.com/lambdaisland/kaocha-junit-xml) plugin provides support for writing out a `junit.xml` file with test results.

The guts of this plugin look like this. It implements three “hooks”, one to add an extra command line argument, one that transforms the test configuration, and that runs after all tests have finished.

The first two establish a common pattern, allowing configuration through `tests.edn` that can be overridden by supplying command line arguments.
    
    
    (defplugin kaocha.plugin/junit-xml
      "Write test results to junit.xml"
    
      (cli-options [opts]
        (conj opts [nil "--junit-xml-file FILENAME" "Save the test results to a Ant JUnit XML file."]))
    
      (config [config]
        (if-let [target (get-in config [:kaocha/cli-options :junit-xml-file])]
          (assoc config ::target-file target)
          config))
    
      (post-run [result]
        (when-let [filename (::target-file result)]
          (write-junit-xml filename result))
        result))
    

`write-junit-xml` takes the test result, a nested map with the details of every single test, and transforms it into XML. Quite a difference from [clojure.test.junit](https://github.com/clojure/clojure/blob/master/src/clj/clojure/test/junit.clj), which imperatively spits out the XML bit by bit as it goes along.
    
    
    (defn write-junit-xml [filename result]
      (with-open [f (io/writer (io/file filename))]
        (binding [*out* f]
          (clojure.xml/emit (test-result->xml result)))))
    

Note also that this plugin will work with any test type, not just `clojure.test`, because they all produce the same Kaocha test-result data structure.

Many of the features I have planned for Kaocha will be provided as plugins. Those that rely on extra third party dependencies will be deployed as separate jars. Maximum features, minimum bloat!

Some other plugin ideas

  * Cloverage integration
  * Detecting side effects (a la circleci.test)
  * Configurable global pre/post steps
  * Configuring dynamic bindings in tests.edn (e.g. `clojure.core/*print-length*`)
  * Configuration for test.check (report-trials, report-shrinking, default-test-count)



## Where are we now?

I’ve been working steadily on Kaocha since April, initially focusing on the base data structures, abstractions, and common functionality. I also put some effort into ergonomics, making sure the tool is pleasant to use. Most recently this led to [deep-diff](https://github.com/lambdaisland/deep-diff), which was split out into its own library.

The core functionality and extension points are there, including test types, reporters and plugins, as well as essential features like watching/autoloading, skipping and filtering, profiling, randomizing test order, and more.

`clojure.test` is the only test type that’s well supported so far, there’s a proof of concept of a Midje test type.

The release process has been streamlined so I can push out new versions quickly, you can expect new releases multiple times per week. Kaocha’s own tests run on CircleCI on a mix of Java and Clojure versions, and documentation is published on [cljdoc](https://cljdoc.org/d/lambdaisland/kaocha/CURRENT).

## What’s next?

I’ll be funded by Clojurists Together from November through January, and I do intend to make it count.

I have a long list of things I want to work on. I’ll start turning these into Github issues in the coming days so people can follow along.

Some of the things I mentioned in my Clojurists Together application have already been implemented, like [junit.xml output](https://github.com/lambdaisland/kaocha-junit-xml), and pretty diffing of test failures, which got pulled out into a standalone [deep-diff library](https://github.com/lambdaisland/deep-diff).

Boot support is high on the list, with that all three major build tools will be supported. Cloverage integration is also a priority, partly because I want to get a better view on Kaocha’s own coverage.

I’ll also start working on supporting more test types. The current test type abstraction hasn’t been fully put to the test yet, it will be interesting to see how well it can support other testing libraries, or whether its design needs to evolve.

The big one of these will be a ClojureScript test type. One of my dreams with Kaocha is to have a dead simple way to drop a `tests.edn` into your project and start testing both Clojure and ClojureScript code. This will be a major task as I [explained elsewhere](https://clojureverse.org/t/announcing-kaocha-a-new-and-improved-clojure-test-runner/2903), as it involves running the ClojureScript compiler, collecting metadata from the compiler, spinning up or connecting to a JavaScript runtime, invoking tests in a JS/CLJS harnass, and collecting the result.

Parallelization is one of the few features of Eftest and bat-test that Kaocha does not yet support. It hasn’t been high on the list because Kaocha by default randomizes the test order, providing a random seed that can be used to recreate a test run. This is a useful feature for catching unintended dependencies between tests, but it relies on test order being deterministic, which with parallelism is no longer the case. Still I understand why people would want to trade this determism in for higher throughput, and so I do intend to bring in parallelism, but it might require some reworking of Kaocha’s core.

There are also a bunch of smaller incremental improvements, like providing nicer feedback in case of misconfiguration. My current thinking is to get a lot of the smaller tasks out of the way in the first few weeks in order to get some momentum, and then start tackling ClojureScript support.

Towards the end of the three month program I intend to start working on documentation, and on making sure the project is easy to contribute to.

Meanwhile I’d love to hear your feedback. Did you try Kaocha yet? What did you think? Are there features missing that are blocking you from switching?

You can [contact me](https://lambdaisland.com/p/contact/) directly, or comment on this post [on r/clojure](https://www.reddit.com/r/Clojure/comments/9tlvr6/test_wars_a_new_hope/).
