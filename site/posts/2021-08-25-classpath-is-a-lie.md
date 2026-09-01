---
title: "The Classpath is a Lie"
slug: "classpath-is-a-lie"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2021-08-25T16:20:18.000Z"
updated_at: "2026-05-16T13:41:40.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-15 08:16"]
---

by Arne Brasseur

A key concept when working with Clojure is “the classpath”, a concept which we inherit from Clojure’s host language Java. It’s a sequence of paths that Clojure (or Java) checks when looking for a Clojure source file (`.clj`), a Java Class file (`.class`), or other resources. So it’s a lookup path, conceptually similar to the `PATH` in your shell, or the “library path” in other dynamic languages.

The classpath gets set when starting the JVM by using the `-cp` (or `-classpath`) command line flag.
    
    
    java -cp src:/home/arne/.m2/repository/org/clojure/clojure/1.10.3/clojure-1.10.3.jar clojure.main
    

Entries on this “classpath” are either directories (like `src`), or JAR files (like `clojure-1.10.3.jar`), which are really just zip files in disguise.
    
    
    unzip -l ~/.m2/repository/org/clojure/clojure/1.10.3/clojure-1.10.3.jar
    

When you `require` a namespace, Clojure will look for a corresponding `.clj`, `.cljc`, or `.class` file “on the classpath”. You can do the same by using `clojure.java.io/resource`.
    
    
    (require '[clojure.java.io :as io])
    (io/resource "clojure/main.class")
    ;;=> #object[java.net.URL 0x3237dfe5 "jar:file:/home/arne/.m2/repository/org/clojure/clojure/1.10.3/clojure-1.10.3.jar!/clojure/main.class"]
    

Intuitively we think of this performing something like the following pseudocode:
    
    
    (some #(find-file-in-directory-or-jar % "clojure/main.class") the-classpath)
    

It’s a useful mental model. It is also wrong. No my sweet summer child, in the world of ClassLoaders and URLClassPaths nothing is ever that straightforward.

The trouble starts when you want to do anything more than find a named resource on the classpath. Perhaps you want to inspect the classpath, iterate over all the files on the classpath, add or remove entries to or from the classpath. What you find out is that in Java

  * everything happens somewhere else
  * all the good bits are hidden from you



## Everything Happens Somewhere Else

This is supposedly a quote from Adele Goldberg, one of the pioneers working at Xerox PARC:

> In Smalltalk, everything happens somewhere else.

(I can’t find a good source to support this though, I’d be grateful if anyone is able to trace this to a primary source.)

Java is no different. You don’t just _do_ stuff. You ask an object to do it, which asks another object, which delegates to its parent implementation, and so forth. When you need to look up stuff on “the classpath”, you ask a `java.lang.ClassLoader`.
    
    
    (.getResource ^ClassLoader loader "clojure/main.class")
    

`ClassLoader` is an abstract class with many descendants, including
    
    
    jdk.internal.loader.ClassLoaders$AppClassLoader
    jdk.internal.loader.ClassLoaders$PlatformClassLoader
    jdk.internal.loader.BootClassLoader
    jdk.internal.loader.BuiltinClassLoader
    jdk.internal.loader.SecureClassLoader
    java.net.URLClassLoader
    clojure.lang.DynamicClassLoader
    

Each class retains a reference to the classloader it was loaded with:
    
    
    (.getClassLoader (class (fn [])))
    ;; => clojure.lang.DynamicClassLoader@4413660d
    
    (.getClassLoader clojure.main)
    ;; => jdk.internal.loader.ClassLoaders$AppClassLoader@443b7951
    
    (.getClassLoader java.sql.Time)
    ;; => jdk.internal.loader.ClassLoaders$PlatformClassLoader@4d131e92
    
    (.getClassLoader String) ; more on this special case below
    ;; => nil
    

So we’ve established we need a classloader before we can do anything classpath-y. Where do we get one? If you need access to a ClassLoader in Java for some reason you typically just get the one that the class of `this` was loaded with, and use that.
    
    
    this.getClass().getClassLoader()
    

But we don’t have `this` in Clojure. Let’s maybe see which classloader Clojure itself uses when it needs to require a namespace:
    
    
    package clojure.lang;
    
    public class RT {
    
    static public ClassLoader baseLoader(){
    	if(Compiler.LOADER.isBound())
    		return (ClassLoader) Compiler.LOADER.deref();
    	else if(booleanCast(USE_CONTEXT_CLASSLOADER.deref()))
    		return Thread.currentThread().getContextClassLoader();
    	return Compiler.class.getClassLoader();
    }
    
    }
    

It first checks the `clojure.lang.Compiler/LOADER` dynamic var. From scouring the code it seems this is used to set the loader internally during a specific scope, but what this ultimately is used for I have no idea. It does give you a way to override the classloader that Clojure uses, by giving that var a root binding. This is something we do in Kaocha to allow us to add test directories to the classpath at runtime, although I’m not sure this is recommended, and I may reconsider how we do that after having leveled up considerably recently when it comes to classpath shenanigans.

Next it uses the “context class loader”, if `USE_CONTEXT_CLASSLOADER` is true, which by default it is. This one is interesting, it’s a thread-local, mutable ClassLoader field, so you can `setContextClassLoader` as well as `getContextClassLoader`.

What’s this for? According to [this StackOverflow post](https://stackoverflow.com/questions/1771679/difference-between-threads-context-class-loader-and-normal-classloader)which has lots of juicy details “[it] exists only because whoever designed the ObjectInputStream API forgot to accept the ClassLoader as a parameter, and this mistake has haunted the Java community to this day”. Perhaps a tad dramatic. Fact is that this is the ClassLoader Clojure looks at (under typical circumstances). And since it’s mutable that gives us some options for doing… interesting stuff.

## The Classloader Chain

ClassLoaders don’t come alone, they bring all their ancestors with them. Each classloader has a reference to a parent.
    
    
    (defn classloader-chain [cl]
       (take-while identity (iterate #(.getParent %) cl)))
    

When evaluating this in Clojure you should see at least three entries, one provided by Clojure, and two by Java. There is actually one more ClassLoader, the BootLoader, but it’s built-in to the virtual machine, you don’t get to see it. It’s represented by `nil`.
    
    
    (classloader-chain (clojure.lang.RT/baseLoader))
    ;;=>
    [clojure.lang.DynamicClassLoader@107842e8
     jdk.internal.loader.ClassLoaders$AppClassLoader@443b7951
     jdk.internal.loader.ClassLoaders$PlatformClassLoader@4d131e92]
    

(To keep the spacial metaphors straight, I’m going to refer to this list interchangeably as the “stack” or “chain” of classloaders. The ones higher in the list will be “up” the stack, the ones lower in this last are “down” the stack.)

If you evaluate this using nREPL you may instead have gotten a much longer list, with a whole bunch of `clojure.lang.DynamicClassLoader` instances at the top, followed by the same `AppClassLoader` and `PlatformClassLoader`. This is something I [had noticed before](https://clojureverse.org/t/dynamically-adding-to-the-classpath-in-a-post-java-9-world-help/2520/6), and never had gotten a satisfying answer about, until [Daniel Szmulewicz recently blogged](https://danielsz.github.io/blog/2021-05-12T13_24.html) about it. I highly recommend reading his post, it makes a good complement for this one, and will help deepen your understanding.

So what happens when you call `getResource` or `getResources` on Clojure’s class loader? When you dig in you’ll see that neither `DynamicClassLoader` nor its parent `URLClassLoader` implement `getResource`, so they inherit the base implementation in `java.lang.ClassLoader`.
    
    
    package java.lang;
    
    public abstract class ClassLoader {
        public URL getResource(String name) {
            URL url;
            if (parent != null) {
                url = parent.getResource(name);
            } else {
                url = BootLoader.findResource(name);
            }
            if (url == null) {
                url = findResource(name);
            }
            return url;
        }
    }
    

This checks in order:

  * the parent classloader
  * the BootLoader
  * `findResource` on _this_ classloader



`findResource` is where classes like `URLClassLoader` implement their own logic for finding resources, `getResource` wraps `findResource`, but with added logic for traversing down the chain of parent classloaders.

What’s important to notice here is that _the parent is checked first_. This is a key insight, it means that if any of the classloaders down the chain return a resource, then the current classloader is simply bypassed.

And what’s that `BootLoader` stuff in the middle? It allows adding classpath entries with [special `-Xbootclasspath`flags](https://www.eclipse.org/openj9/docs/xbootclasspath/). These are always checked first, so this lets you replace Java’s own classes with patched versions.

Looking again at this `classloader-chain`, we now know that when looking for a resource, these are checked bottom-to-top.
    
    
    [clojure.lang.DynamicClassLoader@107842e8
     jdk.internal.loader.ClassLoaders$AppClassLoader@443b7951
     jdk.internal.loader.ClassLoaders$PlatformClassLoader@4d131e92
     nil]
    

First the built-in “boot classloader” is checked, providing some core classes that are baked into the JVM, like `String`, and fundemenatal things like `java.base` or `java.logging`.

Then the `PlatformClassLoader` is checked. Here you find classes provided by Java SE. This class loader was introduced with the module system introduced in Java 9, and doesn’t use a classpath at all, but it knows how to load classes from specific core modules like `java.sql` or `java.xml.dom`.

Then we get the `AppClassLoader`, also called the “system class loader”. This is where “the classpath” (the one we passed to java with `-classpath`) gets checked.

If none of those find the resource, then Clojure’s `DynamicClassLoader` gets a turn. In this case it doesn’t do anything of its own, it simply inherits the implementation from `URLClassLoader`, and by default it does not have any URLs in the list that it checks.

So what’s it for? It implements methods like `findClass` and `loadClass`, to return classes which are only defined in memory. This allows the compiler to generate bytecode on the fly when you evaluate forms, without having to create `.class` files on disk. So far we’ve mostly talked about locating resources via classloaders, but as the name suggests their first use is to find “classes”, i.e. to load byte code from disk and turn it into a class definition that the JVM can work with.
    
    
    (defn xxx [])
      
    (.loadClass (clojure.lang.RT/baseLoader) "user$xxx")
    ;; user$xxx
    
    (.loadClass (ClassLoader/getPlatformClassLoader) "user$xxx")
    ;; => java.lang.ClassNotFoundException
    

Here you see that when you define a function, it really creates a class (and an instance thereof). Clojure’s classloader knows about this class, it can find it in memory. Java’s classloader has no idea.

## So what do you do with this?

Ok, that was already a lot of theory and nitty gritty details… why am I doing this to myself? (and to you, dear reader)

One thing I like to be able to do is add dependencies to a project without having to restart the REPL process every time. For a brief period in time this worked wonderfully for me using [Pomegranate](https://github.com/clj-commons/pomegranate), but since Java 9 this stopped working, and my [pleas for help](https://clojureverse.org/t/dynamically-adding-to-the-classpath-in-a-post-java-9-world-help/2520)largely fell on deaf ears.

Since then I found the `Compiler/LOADER` workaround which we use in Kaocha, and there’s an experimental [tools.deps add-lib3 branch](https://github.com/clojure/tools.deps.alpha/tree/add-lib3) that adds this functionality directly to tools.deps.alpha. Cool beans!

Now let’s add a little twist. We have over a dozen Lambda Island open source libraries at this point, and many depend on each other. It happens regularly that you are working on one library, and halfway through you figure out you need some related changes or additions in another library.

The typical thing to do is to change `deps.edn` to use a `:local/root`reference, restart your REPL, and continue from there. When your REPL is quick to restart and you don’t have much state to build up again then maybe that’s not a big deal, but it can get pretty annoying.

We started running into the same problem with Nextjournal. We’re in the process of extracting and releasing some of the modules that go into making Nextjournal, the way we’ve already open-sourced [clojure-mode](https://github.com/nextjournal/clojure-mode). (Keep an eye out for this, it’s good stuff!)

But that means going from monorepo bliss to a situation where there’s a lot more overhead in maintaining and coordinating these things. On top of that for a big application like Nextjournal restarting your REPL can take a little while, it’s something we really like to avoid.

So we tried adding it with `add-lib`.
    
    
    (require '[clojure.tools.deps.alpha.repl :as deps-repl]
             '[clojure.java.classpath :as cp]
             '[clojure.java.io :as io])
    
    (deps-repl/add-lib {nextjournal.clojure-mode {:local/root "../clojure-mode"}})
    
    (filter #(re-find #"clojure-mode" (str %)) (cp/classpath))
    ;; => ("/home/arne/Nextjournal/clojure-mode")
    
    (io/resource "nextjournal/clojure_mode.cljs")
    ;;=> "/home/arne/.gitlibs/libs/nextjournal/clojure-mode/a83c87cd2bd2049b70613f360336a096d15c5518/src/nextjournal/clojure_mode.cljs"
    

Ok, what’s going on here? We’ve tried adding the `:local/root` version of clojure-mode to the classpath. We can see it’s on there, and yet when we look for something specific we see it’s still getting looked up in the gitlib version. Frustrating!

Let’s first get some better insight into what’s happening, by looking at the stack of classloaders again, and for each checking which locations it checks.
    
    
    (defn classpath-chain
      "Return a list of classloader names, and the URLs they have on their classpath"
      []
      (for [cl (classloader-chain)]
        [(symbol
          (or (.getName cl)
              (str cl)))
         (map str (cond
                    (instance? URLClassLoader cl)
                    (.getURLs cl)
                    (= "app" (.getName cl))
                    (map #(File. ^String %)
                     (.split (System/getProperty "java.class.path")
                             (System/getProperty "path.separator")))))]))
    

Here we loop over the `(classloader-chain)` we had earlier. Some of them will be instances of `URLClassLoader`, and for these we can simply ask them what URLs they check.

For the app/system classloader the situation is a little different. Up to Java 8 this was also a `URLClassLoader`, but since Java 9 that’s no longer the case, and there’s no good way to inspect its actual classpath. It contains a `URLClassPath` instance, but it’s private. Remember, all the good bits are always out of reach.

But we can look at how it’s being initialized:
    
    
    package jdk.internal.loaders
    
    public class ClassLoaders {
        private static final BootClassLoader BOOT_LOADER;
        private static final PlatformClassLoader PLATFORM_LOADER;
        private static final AppClassLoader APP_LOADER;
        
        static {
            // ...
            String cp = System.getProperty("java.class.path");
            URLClassPath ucp = new URLClassPath(cp, false);
            APP_LOADER = new AppClassLoader(PLATFORM_LOADER, ucp);
        }
    }
    

It takes the `java.class.path` system property as its path, so assuming no one has changed the property since then, this gives us a way to find out what classpath it’s looking at.
    
    
    (classpath-chain)
    ;;=>
    ([clojure.lang.DynamicClassLoader@711fe6bb ("/home/arne/Nextjournal/clojure-mode")]
     [app
      ("dev"
       "test"
       "src"
       "resources"
       "/home/arne/.m2/repository/org/clojure/clojure/1.10.3/clojure-1.10.3.jar"
       "/home/arne/.m2/repository/org/clojure/core.specs.alpha/0.2.56/core.specs.alpha-0.2.56.jar"
       "/home/arne/.m2/repository/org/clojure/spec.alpha/0.2.194/spec.alpha-0.2.194.jar"
       "/home/arne/.gitlibs/libs/nextjournal/clojure-mode/a83c87cd2bd2049b70613f360336a096d15c5518/src/nextjournal/clojure_mode.cljs")]
     [platform ()])
    

As you can see `add-lib` added the new directory to Clojure’s DynamicClassLoader, but the gitlib version is still part of the application class loader. And since we know that loaders lower down the stack are checked first, files in the gitlib will shadow files in the `:local/root`.

Why wasn’t `(cp/classpath)` telling us this? Turns out the current implementation is flawed, as soon as the `DynamicClassLoader` contains a URL (as is the case after calling `add-lib`, it completely ignores the system classpath, even though in reality it is still checked (and even gets priority!).

Sadly we can’t change the system classloader. Its classpath may as well be set in stone. But we can define our own classloader, one which plays by our own rules.

The plan is as follows:

  * Define our own subclass of `URLClassLoader`
  * Install it directly above the bottom-most `DynamicClassLoader` (one `DynamicClassLoader` is enough, the fact that there may be many is an unfortunate side-effect of how Clojure and nREPL interact).
  * Have it first check its own paths, then those of its parent, and only then delegate further to Java’s classloaders



Note that the old version (the gitlib) will still be there, but we only look for files there if they don’t exist in the `:local/root` version. This works fine if you are only changing or adding files, but if you delete a file you may start seeing it pick up the old version.
    
    
    (defn priority-classloader
      [cl urls]
      (let [cp-files (map io/as-file urls)
            find-resources (fn [^String name]
                             (mapcat (fn [^File cp-entry]
                                       (cond
                                         (and (cp/jar-file? cp-entry)
                                              (some #{name} (cp/filenames-in-jar (JarFile. cp-entry))))
                                         [(URL. (str "jar:file:" cp-entry "!/" name))]
                                         (.exists (io/file cp-entry name))
                                         [(URL. (str "file:" (io/file cp-entry name)))]))
                                     cp-files))]
        (proxy [URLClassLoader] [(str `priority-classloader) (into-array URL urls) cl]
          (getResource [name]
            (or (first (find-resources name))
                (.findResource (.getParent this) name)
                (.getResource (.getParent this) name)))
          (getResources [name]
            (java.util.Collections/enumeration
             (distinct
              (concat
               (find-resources name)
               (mapcat
                enumeration-seq
                [(.findResources (.getParent this) name)
                 (.getResources (.getParent (.getParent this)) name)]))))))))
    

Ok this is getting a little hairy. I was hoping I could just call `(.findResource this ...)` to have it search the list of paths, but that’s not working… I followed the trail from `URLClassLoader` to `URLClassPath` to `Loader` (remember how in Java everything always happens somewhere else?), and eventually gave up and implemented my own `find-resources` logic.

The important bits are here:
    
    
    (or (first (find-resources name))          ; Search our own paths
        (.findResource (.getParent this) name) ; Search DynamicClassLoader
        (.getResource (.getParent this) name)) ; Search App/Platform/Boot
    

Now let’s try installing it (`root-loader` is helper to find the `DynamicClassLoader` that sits immediately above the application classloader, like I said, one is enough):
    
    
    (.setContextClassLoader 
     (Thread/currentThread) 
     (priority-loader (root-loader) ["/home/arne/Nextjournal/clojure-mode"]))
    

That should do the trick, but it doesn’t, at least not when you evaluate this from nREPL. The problem there is that nREPL captures the current context classloader at the beginning of each `eval` operation, and restores it afterwards. So we need to somehow set it “outside” of the current `eval`.
    
    
    (let [thread (Thread/currentThread)]
      (future
        (Thread/sleep 100)
        (.setContextClassLoader 
         thread
         (priority-loader (root-loader (context-classloader thread) ["/home/arne/Nextjournal/clojure-mode"])))))
    

We use a future which closes over the current thread, and then update the thread’s context classloader from the future, which runs on a different thread.

And… it works! Except that when you try to navigate with something like `cider-find-var`, presumably because that uses a separate nREPL session, which runs on a different thread, which has its own classloader. Here too there are solutions. The first thing I tried was simply forcing Orchard (the library backing CIDER) to use a specific classloader, and that works. Now I’m leaning towards looping over all threads, and installing this priority-loader on every thread that has a `DynamicClassLoader`.

## Conclusion

So what did we learn?

  * There’s really no such thing as “the classpath”, but there’s a hierarchy of class loaders, and you can kindly ask them to give you stuff.
  * Clojure has two places where it looks for a classloader, `Compiler/LOADER` and the thread’s context classloader. Setting the first is easy, setting the second requires some more care.
  * Adding something to “the classpath” is easy once you have a `DynamicClassLoader`/`URLClassLoader`, but removing something that is part of the classpath the application started with is impossible, and replacing something only kind of works by using some heavy trickery to make the new entry take precedence over the old.
  * Use the source! Clojure and Java are both open source, which is fantastic. I would not have been able to get this far without having these sources available.



This is not the last word on these topics, these experiments are ongoing, we’ve set up a repo under [lambdaisland/classpath](https://github.com/lambdaisland/classpath) where you can find the current state of things. In particular it contains this cool helper:
    
    
    (update-classpath!
     '{:aliases [:dev :test :licp]
       :extra {:deps {com.lambdaisland/webstuff {:local/root "/home/arne/github/lambdaisland/webstuff"}}}})
    

This will read `deps.edn`, use it to construct a “basis” with the given options, and then add any new entries to the classpath. It’s as close to a `deps.edn`reload as you’ll get, and you can add local overrides, as I’ve shown here.

We’re also working on other quality-of-life helpers in there, like `git-pull-lib` to update the `:git/sha` of a library to the latest commit in a branch.

If you want to discuss this post just head on over to [ClojureVerse](https://clojureverse.org/t/the-classpath-is-a-lie/8093)!
