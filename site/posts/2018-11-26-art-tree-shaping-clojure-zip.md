---
title: "The Art of Tree Shaping with Clojure Zippers"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2018-11-26T15:48:06.000Z"
updated_at: "2026-05-16T13:42:04.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:23"]
---

This is a talk I did for the “Den of Clojure” meetup in Denver, Colorado. Enjoy!

Captions (subtitles) are available, and you can find the transcript below, as well as slides [over here](http://arnebrasseur.net/talks/2018-clojure-zip-denver/#).

For comments and discussion please refer to this post on [r/Clojure](https://www.reddit.com/r/Clojure/comments/a0k26p/the_art_of_tree_shaping_with_clojure_zippers/).

## Transcript

Arne:

My name is Arne. I’m from Europe but I flew in because I met Dan in Berlin recently and he told me about this awesome meetup, so I was like, yeah, I gotta check that out. I’ve been here just long enough to get over the light headedness. Yeah, I do Clojure consulting and training. I also make Screencasts about Clojure. I don’t know if anyone’s seen Lambda Island, lambdaisland.com. You can find videos about Clojure.

Dan:

I heard there’s even an article there that maybe someone around here wrote? Yeah?

Arne:

That is true. Yeah. Den of Clojure speaker, Joanne Cheng, wrote an article about D3 and ClojureScript, which is really neat.

Dan:

Awesome.

Arne:

Yeah.

Dan:

Cool. Well, thank you for joining us all the way from Berlin.

Arne:

Thank you very much for having me.

Dan:

Yeah. Pretty cool to come down here. You’re gonna talk to us about zippers.

Arne:

Zippers, yes. Functional zippers.

Dan:

Functional zippers. It’s a fun term, isn’t it?

Arne:

Who’s used zippers before?

Dan:

Take it away.

Arne:

A couple of people, okay. All right. I’ve got a lot of slides so I’m gonna keep the pace. I’m gonna put on a timer so I’ll have a rough idea. If I really go over time, I’ll cut it short, but we’ll see how it goes.

So, yeah. Welcome to my talk, The Art of Tree Shaping with Zippers. So you already know who I am now, I’m Arne Brasseur, I’m Plexus on the Internet. I’m from Belgium, originally, a tiny country in Europe between France, Germany and the Netherlands. Beer, chocolate, waffles, fries, mussels, all the good stuff. But I’m currently living in Berlin, in Germany.

I do, like I said, Clojure consulting and training for a living, as well as this thing, Lambda Island. There’s tons of cool Screencasts on there, been going for about two and a half years, there’s 40 something episodes there. It ranges from core stuff like transducers, or small stuff like the some function, but also Datomic, reagent, re-frame, tooling, ClojureScript compiler, Integrant, you name it, there’s 40 something episodes on there now. Some of them are free, some of them you can buy, and then you can also just sign up for an all-access pass. So, yeah. That’s me.

The Agenda for today is, first, we’re just gonna look at Clojure zip from a user’s perspective. Just walk through some examples, what the API feels like, what you can do with it. Then, we’ll talk a bit more about, okay, what exactly is this whole functional zipper idea, where does it come from and why does it matter, why is it relevant? Then, we’ll look at how Clojure implements zippers. Then, finally, very briefly, I’ll touch on a couple of libraries that build on zippers, stuff that you can go from there.

The clojure.zip API. Before I can really show you examples, you need some kind of mental model of what we’re talking about when we say “zipper.” So, a zipper, also called a “loc,” or “location,” is like a two element tuple where the first one is some kind of tree-shaped data structure. It can be all kinds of things, but anything that has nodes with children. Then the second one is kind of a pointer to a specific node, like a way of focusing on a specific node. Or you can also think of it as a path in the tree to say, “Okay, now we’re dealing with this particular one.” So, as a metaphor, you can think of it as having a map with a dot saying “you are here.” The map plus the dot, that’s your zipper. With the difference that when you have the zipper you can move that dot around and make changes to your map, but we’ll get to that in a second.

The zipper API allows you to navigate in your tree. You’re looking at a specific node, and then you can say you wanna go up, down, left, right, and then when you end up at a certain location you can start making changes. When I say “making changes,” it’s Clojure, so it’s immutable, like Clojure’s own data structures, so it really just means that you get a new copy or a new version, an updated version of your zipper. Same with the navigation, it’s also in a functional way. I mean, if you’re used to how Clojure works, this should be quite natural. I’m just pointing out that when I say “replaced,” or “removed,” it’s functional still.

clojure.zip comes with Clojure itself. You don’t need to install anything extra. If you have Clojure, you have clojure.zip. You just need to require it. It’s quite typical to require it and call it “z.” Then, there’s a couple of different functions to take a data structure and turn it into a zipper to wrap it into that two element tuple.

For instance, if you have nested vectors, you can think of nested vectors as being a tree structure. So, if you say then `vector-zip` and you pass it a vector, you get the zipper or the loc over that vector back. So, yeah, I’m using all these terms interchangeably. We’ll look more at where the term “loc” comes from when we talk about the origin of the zipper idea.

Like I said, a nested vector can be considered a tree, a nested sequence can be considered a tree, XML documents are quite naturally a tree. So, you have an element, an element has children, and then those elements can have their children. But anything that has nodes and child nodes can be a zipper. We’ll look a bit more about how that works when we look at the Clojure implementation.

I’ll walk you through a very simple example. Here, I have a vector and I’ve turned that into a zipper. On the right-hand side, you see a visualization of what we’re looking at now. We’re looking at the vector as a tree and we’re pointing at the root node. That’s why the top node is colored blue. That blue is your dot saying you are here.

Then, you can go down, now we’re just looking at that first subvector. You can go right. You can go down again. You can go to the right-most sibling on the current level. Then, when you end up at a certain point, you can do these operations. For instance, you can append an extra child node to the current node. So now, I have appended an extra X and you see that now this node, instead of just four, five, now it has four, five X.

Then, you can go up again. Now at some point … So, all of the time … You see I’m threading this, the result of each function call is a new zipper. Now at some point you want a plain value again. You want the value of the node you’re currently looking at. That’s what you use the node function for. Given the node that you’re currently looking at that gives you the value of that node.

It’s also very common that you just wanna go all the way back to the root, all the way back to the top. You say up, up, up until you can’t go up any further, and then get the value of the complete structure. The combination of that is encapsulated in the root function.

Yes?

Participant:Is it required that only the leaf nodes have values?

Arne:Well, the only requirements that the zipper API has is that for whatever structure you give it, you can define what is a branch node and what is a leaf node, where a branch node is anything that can have children. In this case, when you’re using vector zip, it’s gonna consider anything that isn’t a vector as a leaf. But you can define that however you want, but you do need sort of a dichotomy of it’s either a branch or it’s a leaf, it can’t be both. Does that answer your question?

Participant:[This is a bit hard to understand in the recording, the participant is asking about whether branch nodes can have values, i.e. contain extra information besides the list of children. In the vector example this isn’t clear, because a vector’s value is fully determined by its children.]

Arne:Yeah. You can have extra values on that node. Say, an XML document, an element has attributes, absolutely, yeah. You don’t see that in the vector example because a vector doesn’t contain much more information besides its children, but, yes, a node can have extra information besides its children. Absolutely, yeah.

We’ll get to that now. Here’s a second example, I came up with some hypothetical XML. This is the representation of a shopping cart, which has line items and a customer. In line items, there’s two product line items, and a discount. Clojure has a set of XML parsing functions built-in. So, I’m using `clojure.java.io` to read that file and then Clojure XML to parse it from XML to a Clojure data structure.

Then, the result is something like this. This is how `clojure.xml` represents XML documents, so it uses Clojure maps and vectors where each XML element turns into a map which has a tag attributes and content, which are its children.

When you get something like this, then you can pass that to the zipper `xml-zip` function and you get a zipper over that XML document. This is now, yeah, it’s getting a little small here, the visualization. That top one, for instance, that’s cart and then it’s two children have line items, customers. So this is a representation of that XML that we just saw.

I can do the exact same thing. I can go down, I can go down, right, right, left-most, down. Now I can replace the node. So before it was a string node that said “chocolate” and now I’ll replace it with “luxury chocolates.” Now I go up and then I use the edit function. The edit function is like update or swap or any of these functions that you take a function that you apply to a value to get a new value. In this case, I’m gonna take the value in this node and `assoc-in` the attributes map a new price, because now it’s “luxury chocolates” so the price has gone up. So, before it says `:price 2.5`, and now it says `:price 2.9`. Then I’m done and I go back to the root and I get the root value.

Now you don’t write actually code like this where you just say, down, down, left, right, replace, … . You’re gonna wrap this in a function and actually check where you are and what you’re doing. But conceptually, this is what you typically do. You walk around your tree, you make changes, and then in the end you go back to the root and get the root value. That’s how zippers work.

So, you’ve seen most of the API already. I’ll just quickly run through the full API, it’s not that big. There’s a couple different ways to create zippers. We’ve seen a few of those. There’s different functions to navigate. We’ve seen all of these. There’s a bunch of functions to update. We’ve only seen a few of these, but it’s basically insert a sibling on the left, insert a sibling on the right, insert a child on the left, insert a child on the right, “edit” to apply a function to the current node value, remove a node, and replace a node with a new node.

Then, just a helper functions, which takes an existing node and a new sequence of children, and creates a new node which has all the attributes of the old node but with a new set of children. We’ll get to that again later. You’ll come across them and I’ll make a note later when we talk about the implementation, why that is interesting.

Then, finally, you can look around. Right? You end up at a certain point and you can say, what are my left siblings? What are my right siblings? What are the children of this node? You can ask for the path. This gives you a sequence of node values from the root up to your current node that you’re currently looking at. Then, you can just ask, is this a branch node or not?

Then, finally, there’s these tree functions which come together, which give you a straightforward way to do a complete walk of all the nodes in your tree. This has a depth first walk, so you start … Yeah, I’m using `iterate`. If you’re not familiar with `iterate`, it says “take that zipper and then apply the next function to it over and over and over again”, and it returns a lazy sequence of all the results.

Here I have applied it zero times, so I’m still at the root. Then, one time, and you see it starts going down to the next one, to the next one. It’s depth first, so it goes to the bottom, and then the next one, it goes left and up, left and up, until you’ve seen every single node in the tree. Then, if you go next one more time, it goes back to the root and adds a special marker to that zipper that says “this is the end point.” So now, you can use that end predicate to say that, “Okay, am I done walking? Am I done checking every single node?”

So now you know the complete zipper API in my talk here, but I still have a lot of interesting things to talk about. First of all, I’m gonna give you a couple of caveats. Now you’re gonna probably forget these, but hopefully when you actually get into the thick of it and you start banging your head against the wall, because something isn’t working the way you think it should be, then maybe at some point you will be like, “Oh, actually this is what Arne mentioned, this is the one thing that I should watch out for.”

These are - there’s no boundary checks, so don’t fall off your zipper - There’s no way back from end - zippers are not cursor I’ll go over these one by one.

No boundary checks simply means that if you go down, down, down until you get to a leaf node and you go down once more, you get nil. Up to that point all is well, but then you do one more thing and you get a NullPointerException and you need to start looking like, okay, where did that nil come from? That is how the API is designed, you know, also left, left, you go left one more time, you get a nil. It means that you can do stuff like this, `(if-let [left (z/left ...)])` if I can still go left, and do something there, so it’s a fine API, but it is something to, you know, you do wanna have some checks and assertions in there to make sure that you don’t need to start chasing your nils after the fact.

The second thing I wanted to point out. So, I said, okay, when you do this complete depth first walk of your tree, you get this special end value. That end value is in a way a zipper, but not everything works on it the way that it works on regular zippers. For instance, if you try to go to the previous node, again it’s just gonna give you nil.

The main thing here, main advice here is, this end, either just treat it as a sentinel, just as a way to say, “Okay, now you’re done,” or to get the root value back. But that’s kind of it. Don’t try to do too much with that.

Then, finally, zippers are not cursors. What I mean by that is, a cursor in text points in between two characters and that means that it can be at the beginning of a line or at the end of the line or on an empty line. Whereas the zipper always points at a concrete node, which means that, for instance, you can’t have a zipper inside an empty collection and then say, “Okay, now I wanna insert something here.” Again, that’s how it works, there’s nothing wrong with that. But depending on the kind of problem you’re solving, sometimes the cursor model is much more intuitive and so you start thinking of your zipper as a cursor because the metaphor kind of matches until it doesn’t.

Here’s a little example Say, naively, I wanna take these nested vector, go two levels down. So, I’m inside the nested vector now. I wanna remove that one, remove that two and, in place of it, insert a three. What I’m hoping for is a nested vector with a three inside. Let’s see what happens. I go down, I go down, I remove this, and now I’m looking at that middle node. That might already be a little bit surprising. This is 100% by design. Again, there’s nothing wrong with that, but you do need to know how this stuff works. We’ll remove such that it removes the node at your current location, returning the loc that would have proceeded it in a depth first walk. That means, over here, if you would have called that previous, you would have ended up in that middle node, and that’s why it gives you that middle node.

If we continue our example, you remove it, you try to do an insert, and now you’re trying to insert a sibling of the root node. You can’t have a sibling if you don’t have a parent, and so it’s going to say “Exception: Insert at top.” That’s something to watch out for.

This is even more obvious when you have an example like this. I’m looking at that one, two, and then I delete it, and now I jump to that Y. Sometimes it’s a little counterintuitive.

All right, any questions so far? Yes.

Participant:Are you familiar with xpath and would zippers be useful compared to those ways of navigating the data structures?

Arne:I mean, Xpath, I’ve used Xpath a long time ago. I mean, conceptually, it’s too different from CSS selector, right? I think having these kind of selector APIs are very useful. I think they both have their uses. For instance, say, I’m doing stuff over XML, I will very often use Enlive, which gives me that selector API. It just depends what you’re doing. There are cases where the zipper model works where you really need more context or where it’s more loosely defined. Not everything can easily be captured in a path or in this kind of expression. I think for those cases, this is a really great tool in your toolbox.

Participant:You mentioned that the pointer has to point to some node. Does that mean when you create a zipper, you have to give it a root node, or do you create the tree first? Or how do you create a zipper? How do you do it?

Arne:Yeah. You need some kind of data structure for it to work on. You can give it an empty branch node of whatever kind of data structure you’re working with, so like an empty vector or an empty XML element. But you do need something like, say, you’re dealing with XML, the minimum you would have to give it is a single tag with nothing in it and then you can add stuff from there.

Participant:Okay. Create data structure first and then you create a zipper from that.

Arne:Yeah. Typically, you’ll have data structure and you use a zipper to do stuff with it, yeah. I mean, there’ll be more time for questions at the end. I just wanna make sure that this first part is more or less clear.

Okay. I’ve given you a rough mental model of the zipper, the two element tuple. Now it’s time to refine what a zipper really is. If you look at the doc string for Clojure zip, it’s a very Hickey doc string in that it makes absolute sense if you know exactly what he’s talking about. It says: “Functional hierarchical zipper, with navigation, editing, and enumeration. See Huet.”

Huet is a French guy, Gerard Huet, who published this article in 1997: “Functional Pearl, the Zipper”, where he says, “Almost every programmer has faced the problem of representing a tree together with a subtree that is the focus of attention, where that focus may move left, right, up, or down the tree. The Zipper is Huet’s nifty name for a nifty data structure, which fulfills this need. I wish I had known of it when I faced this task because the solution I came up with was not quite so efficient or elegant as the Zipper.” It’s a pretty cool article. It uses OCaml, that’s probably the biggest hurdle to reading it. But I remember reading it a couple of years ago, it’s a fun read. I recommend it.

Huet defines two types of data structures. The first one is the path. He says, “A path is like a zipper,” and in this case, he’s talking about an actual zipper, like off a hoodie or a jacket. “A path is like a zipper, allowing one to rip the tree structure down to a certain location. It contains its list L of left siblings, its father path P, and its list of R of right siblings. It’s a recursive definition, right? Because it contains a sequence of nodes, then a pointer to the previous parent, its own parents, and then a sequence of other nodes, the left and the right. There’s also a special value top, which means that we don’t have a parent path because we’re looking at the top of the structure, so that’s a special marker.

Then, “A location in the tree addresses a subtree, together with its path. This two element tuple, which is the current node with the path, which is the path we just defined. You just see where that terminology loc or location comes from. That’s from the original article.

Here I have a different visualization. In these visualization, there’s two types of nodes: there’s the diamond-shaped ones, which are the zippers or the paths; and the round ones are regular nodes. When I have my zipper over my XML document, it’s looking at the root node, so that’s the cart, and it has that marker that says, “okay, we’re at the top.”

Now, when I go down, now the thing I’m holding is the zipper, which is looking at line items with a parent, which is the previous zipper we had, and then left and right siblings. Then, I can go down again and now I’m looking at that product inside the line items, inside the cart. What you already see here is that the tree has been turned upside down, right? Before, we had the cart at the top and now we have the cart at the bottom. The thing that we’re holding onto is the place where we’re focusing on, the node where we ended up at.

The reason that that’s cool is that, say, I go right from here, so that means that I’m gonna shuffle left, right siblings and current node, those three pieces of information are gonna change, but the parent path and anything down from there remains unchanged. So I can start doing these local operations and not care about anything that’s above me in the tree. I hope that makes sense, these spatial metaphors.

We’ll look at Clojure’s implementation later and maybe then it’ll make more sense. So, yeah, it’s a generic way of prying a nested data structure open so that you can locally make changes and then afterwards you put it back together again.

I go down again. Now I’m gonna do the same stuff that I did before. I have this chocolate node and I’m going to replace it with the string luxury chocolate. Now the zipper has turned red, which means that it has a flag set on it, which says that it has changed. Because as we said before the parent and chain of parent paths doesn’t change when you do an operation like this, which means that that product node still has the old value.

Only when we start going up again does clojure.zip rebuild those nodes. So now product has changed because now it contains a new string. Now line item has changed because it contains a new product. Finally, we’re back at the cart. This is maybe the most confusing thing about the whole thing, but again it will probably become more clear when we get to the implementation. Does it make sense? Shall I walk through it again? Okay. I’ll keep going.

Here we are at the Clojure implementation. These vector-zip, xml-zip, seq-zip, in the end they all call the zipper function. This is the main way to create a zipper, to create a loc, and it takes four pieces of information. The first three are functions and the last one is your actual data structure, the root of your tree. Those first three functions are a way to tell Clojure how your data structure works, what kind of data structure you’re dealing with. You need to be able to answer two questions and do one operation. You need to be able to have a node an answer, is it the branch node or not? Can it have children or not? When you have a node you need to be able to say, “Give me a sequence of all the children of this node.” Then, finally, you need to be able to do that make node thing where you have a node and a new set of children and you get a new node which has all attributes of the old nodes but with the new children.

If you look at the implementation of zipper, this is it. You see that two element vector in there. Right? In returns a vector with that root and with the nil, which is what Clojure uses as its marker to say we’re at the top. Right? The path is nil, there’s no parent path because were at the top.

The other thing to notice here is that it then attaches a bunch of metadata to that vector. So these functions that you passed in there got stored on that two-element vector as metadata. This is pretty neat. It’s a neat trick and I haven’t seen it in many other places. So, yeah.

Now, if you look at vector-zip, vector is just calls zipper, in the case of a vector-zip, a root is a branch node if it’s a vector, otherwise we’ll consider it a child node. Seq just gives the stuff inside the of vector. Then, the make node function, mostly just calls back on the children. In this case, it also just make sure it preserves metadata.

So far it’s fairly straightforward. If you look at the implementation of the actual clojure.zip/branch function, clojure.zip/children, clojure.zip/make-node, they look up those specific functions on the metadata and call those. This is a pretty neat way of doing polymorphism. Clojure has protocols, Clojure has multimethods, which are all different ways of dispatching to different implementations based on the specific value that you’re looking at. But this is yet another way of doing it.

Vector-zip, on that zipper you’ll see that all it really does is just wrap it in a vector and then add a nil. If you look at the metadata, you’ll see those functions there. So, it’s kind of nothing under the covers. It’s fairly straightforward once you know what’s going on.

Participant:I just wanna butt in for a second and say, “Wow, that’s really cool.” Yeah.

Arne:

So, even if you never used zippers, maybe you end up using this trick. So now, we can actually start looking at the values return from these function calls. Now I’m going to walk through that first example again with the vector-zip. After turning it into a zipper, it’s just this two-element vector. And then you go down and so now you see that the node we’re looking is the node `[1 2]`, and the parent path, or the path at the moment has this left siblings, right siblings and a parent path, which is currently nil, which is, you know, because the parent of the node we’re looking at is the root.

Then, the Clojure also stores a sequence of parent nodes, which would use this to reconstitute, to rebuild the values as you go back up again. You can already see if I go left or right here, that left and right and the current node changes, but everything else stays the same. Now I go down one more level and you start seeing this linked structure. We’re looking at the tree now. It has left and right siblings and it has a parent path, which is the one we just had. Right?

Here we have left one, two, P nodes, P path nil, R nil, that’s inside there now, this is the parent path of the new path. Again, you know, we can go left and right and all it needs to do is change those siblings. Everything else stays the same way.

Now, append a child, it’s got an extra marker at the bottom. It’s changed now. But you see that X is now in the node that we’re looking at. But if you look at the parent node, that X isn’t there. Not yet, right? Clojure has been able to make this local change. That’s one of the benefits, the efficiency benefits of zippers that you can go deep into your structure, make a bunch of changes, and then only pay the price of rebuilding your path up to the root when you zip back up. We’ll do that now. Once you start going up, you can see that it starts building up those values again.

I’m actually doing better on time. Then I guess I really flew through this, slide 100. Okay. Two more libraries I wanna at least mention. One is fastzip. Fastzip is really just what it says on the tin, it’s faster zipper. The Clojure implementation with the vectors and the metadata, it’s super cool, it’s also nice that you can just inspect it, that it’s just the vector and you can look at those values. I do typically developed with Clojure.zip, but then when you wanna ship this in production and you wanna get those extra cycles out, you just swap out clojure.zip for fastzip. It’s absolutely the same API but it uses deftype under the hood so it really gives you this high-speed like native dispatch.

The only thing to watch out for is that you don’t rely on implementation details. Now you all know that a clojure.zip a two element vector, but if you start relying and, like for instance, destructure that vector, yeah, that’s not gonna work with fastzip. But as long as you stick to just the functions in Clojure.zip, it should work 100% the same Clojure. Clojure and ClojureScript, by the way, all of these are cross-platform.

Then, the final thing I wanna mention is rewrite-clj because this is one of the libraries that built on top zippers. In this case, in particular, it gives you a zipper over a Clojure syntax tree. So what rewrite-clj does is it parses Clojure source code, turns it into a syntax tree that preserves formatting and white space. You can process that, do stuff with it, make modifications without messing up people’s formatting. So, a lot of tools use this, you might’ve come across clj-format, zprint, lein-ancient, refactor-nrepl, mutant mutation testing, all of these use rewrite-clj under the hood.

Rewrite-clj has its own .zip name space, which largely as API corresponds with clojure.zip. For instance, in rewrite-clj, you go next or you go right, it’s going to skip over white space nodes, which is most of the time what you want. So you can deal with that AST and ignore the white space, but it is still there, when you build it up, you turn it back into text. The formatting is still there.

You also get some extra goodies, some extra functions that are particular to dealing with source code. So this is pretty nice library. Because, yeah, people keep asking me, “Okay, yeah. You know, I’ve looked at these zippers, but what do you use them for?” So this is, for instance, one real use case that I used them for not too long ago. A project that’s a year or two old, we started out with a certain namespace organizations and halfway through decided to have a bit more of a thought out namespace organization with proper, you know, reverse domain name and all of that stuff.

So, we had about 50% of our code using the old scheme and the new scheme. At a certain point we wanted to clean that all up. But at the same time it had to all happen as a big bang thing because you don’t want to block everyone who’s working on this. The solution was to write a script, which goes over to complete code base, updates or require statements, updates of namespaced keywords, all that kind of stuff so that we can find an opportune moment to merge as much as possible, run this once, get it over with.

This is one function in there, you see there’s a loop recur, which takes the input zipper and then just goes next, next, next. It goes over the complete source code until it’s either at the end, then it stops. Or if it’s looking at a list and the first element in that list is a :require keyword. In that case, it goes down into the list and passes the zipper to another function which will do the actual updating of the requires. This is one example of what zippers in the real world might look like.

This is my last slide. I didn’t go as much over time as I thought I would do. One more thing that I used zippers for recently, so I was working on a project which does a Jupiter notebook kind of interface. So, these are these environments in a web browser where you can evaluate code, and so we had streaming output from the server to the client. There’s two things that are tricky with that because you need to emulate a repl like a terminal. You need to deal with ANSI escape codes, but you also need to deal with carriage returns.

Like, if you have, for instance, a progress bar, you know, that would work in a terminal, then you need to emulate that in your HTML. If you’ve come across a carriage return, go back to the beginning of the line where the beginning of the line, in this case, is actually a bunch of spans that are encoding your color stuff. There I extensively used zippers to navigate back, “Okay, where is actually the beginning of the line, split this up.” So, that was another use case.

Yeah. That’s all I have. But I am here for questions. Questions. Yes.

Participant:

I kind of wanna ask about possible extensions to the zipper idea. I think it’s cool. What if you wanted a zipper except with two pointers? Or what if you wanted a zipper that instead of a tree data structures worked on directed acyclic graph or general graphs. Do you have any ideas?

Arne:

That’s an interesting research topic. I think the reason that zippers came to be is the idea of … actually I have one more slide here. I briefly mentioned this in passing, but it’s the idea of … Here are two different ways of doing the same thing, right? The bottom one, I’m just using Clojure’s assoc-in to replace a value deep inside a tree, whereas the top one, I’m doing the same thing with zippers. In this case, I mean, both do the exact same thing. But if you have a bunch of those assoc-in, so each time Clojure needs to go from the top to the bottom, make that change, and so you end up with a data structure on the right, which we use as a loc on the left, but anything from, you know, your position where you make the change up to the root, those will all be new nodes.

If you do a bunch of updates every single time, you’re rebuilding that path from your current position to the root. That’s really why zippers shine, because you can defer that cost as much as possible. So, in a more general graph structure, I think … I don’t know. I can’t answer off the top of my head if you can do the same. I mean, I guess, if your directed acyclic graph still has the concept of a root that you considered a root because you still wanna do it in kind of a functional way, then I guess you can do something similar.

Having to two pointers at the same time, I think this is going to start getting very, very tricky. You’re going to have to do a lot of bookkeeping and I don’t know if it still worth it … I guess, it also like to see if there’s a use case for it. It might be possible. Yeah.

Participant:Would you say that the benefits of using zippers scale with the number of changes that you need to make deep inside of some nested structure?

Arne:

I mean, that’s one of the benefits of zippers, is in the performance gain. So, yes, performance-wise you’re gonna get more bang for your buck, the more that you can cluster operations together, navigate to a certain position, do your changes there and then all these zip at once. But I think the other thing, people that use zippers, I don’t think performance is often the first reason, I think the main reason is that there’s certain problems where there’s navigation API is just very elegant. That’s probably the main reason that people choose zippers.

All right. Well, I’ll be here. I’m happy to chat later.

Dan:Thank you.

Arne:Thank you all very much.

Dan:That’s great. Just because I don’t think we were streaming then, Arne, how do I pronounce your name, I’m sorry?

Arne:Arne.

Dan:Arne, okay.

Arne:Arne.

Dan:You do freelance Clojure consulting. Are you looking for more clients?

Arne:Actually, not right now. I mean, I’m always happy to talk, there might be interesting stuff, especially when it comes to training with some people in Berlin.

Dan:You could still … maybe put on your wait list?

Arne:

Absolutely. But I actually just recently said goodbye to a consulting gig because I wanna spend some time on Lambda Island. I got some open source work that I’m committed to now with Clojurists Together. I’m also organizing a conference next year. It might be a little bit far from most of you. It’s gonna be in Belgium, Heart of Clojure, but it’s going to be an absolutely kick ass conference early August. I’m gonna be busy the next year, is what I’m saying.

Dan:

I’m going to be out in Belgium speaking in a conference in two weeks. Can you maybe schedule it for them? That’s great. I’ve never been to Belgium, so I’ll check it out. I’ll pre-scout it for next year’s Clojure conference.

Arne:

Yeah, yeah. That’d be awesome.

Dan:

That’s great. Cool. Well, thank you for speaking for us and I appreciate your help.
