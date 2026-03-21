https://www.theregister.com/2026/03/17/ai_businesses_faking_it_reckoning_coming_codestrap/
https://nolanlawson.com/2026/02/07/we-mourn-our-craft/

https://blog.lmorchard.com/2026/03/11/grief-and-the-ai-split/
https://www.anildash.com/2026/03/13/coders-after-ai/
https://www.baldurbjarnason.com/2026/the-two-worlds-of-programming/

In the past week, three different blog posts crossed my feed, all talking about
how AI-assisted coding is dividing programming into two camps. It's clear that a
cultural split is happening. The rise of LLMs has dragged programmers kicking
and screaming into the culture wars.

Anil and Les both talk about people who lament what's happening because "they
care about their craft".

From Anil:

> ... It's the same reason so many coders like to bake, or knit, or do
> woodworking — they're all hobbies where precisely doing the right thing is
> rewarded with a delightful result.

and Les:

> If you're mourning the loss of the craft itself—the texture of writing code,
> the satisfaction of an elegant solution—that's real, and no amount of "just
> adapt" addresses it.

Sure, that _is_ real. Some people just like coding, it's a fun hobby they want
to hold on to. But to say that that is the main reason many smart people are
refusing to go along with the narrative that AI coding is inevitable is
patronising and simplistic.

Baldur's take is a bit more sophisticated:

> Two groups within the field of software development look at the same dynamic,
> the same behaviours, and the same features, but come to diametrically opposite
> conclusions.
>
> This might seem perplexing but it makes sense once you realise that software development was already divided:
>
> 1. Those who believe software development and the software industry has been
>    going from strength to strength – “software is eating the world” – and this
>    is just making that happen faster. 
> 2. Those who believe we’ve been in an incrementally escalating software crisis
>    since at least 2007.
> 
> Our current software crisis – we’ve had a few – has been ramping up since the
> US gave up on regulation after the 2007 crash. Instead of reforming and
> regulating finance, the US decided to let the finance industry take over all
> of its industries, which hasn’t been great overall, but for software it’s
> meant that “quality” stopped mattering.

Baldur posits that the schism goes back to 2007, and how people look at what has
happened in the software industry since. He points to the financial crisis,
which does have its roots in 2007, with the rise of mortgage-backed securities
backed by subprime US housing loans, which would lead to the collapse of Bear
Sterns in March 2008, Lehman Brothers in September, and from there the entire
house of cards came tumbling down, eventually spilling over into the Eurozone
crisis.

But 2007 is also the year of the iPhone. It is the year Ruby on Rails exploded
in popularity. It's the time of Shopify, Zendesk, Github, Dropbox, Heroku,
Mailchimp. In other words, it's the start of SaaS, and of Mobile. Two
technological trends that would define the startup ecosystem for well over a
decade. 

-----

I am going to offer my own take on the divide that's emerging among professional
programmers, and while any such simply characterisation of a big heterogenous
group of people falling into two camps is out of necessity hopelessly reductive,
I hope I can still use it as a way to give you, the reader, a more insightful
way to look at what's happening around you.

For Anil the two groups or _coder-as-a-career_ vs _coding-as-an-avocation_, Les
calls them _the craft-lovers_ and _the make-it-go people_. My two groups are
these:

1. People who want to believe that technology is apolitical, and that
   technological advancements invariably lead to progress. People who have
   enjoyed their cushy engineering salaries without thinking too hard about
   things that might make them uncomfortable.
2. People who understand that technology is always political, who see the
   pattern of technological "progress" leading to greater wealth concentration,
   and the erosion of democracy, the destruction of the environment,
   exploitation of the working class (including most programmers).
   
Yes, there is a mourning there. A mourning of what technology could have done
for humanity. A possible future that hypercapitalism has moved us away from,
gradually at first, until it got accelerated to hyperspeed with LLMs.



-----------------------

Two articles crossed my feed recently. They both talk about how the rise of
AI-assisted coding tools is leading to a split in the developer community. Or
rather, how it made a split visible that had always been there.

The first is this article by Les Orchard titled [Grief and the AI
Split](https://blog.lmorchard.com/2026/03/11/grief-and-the-ai-split/), where he
divides developers in two camps, the "craft lovers" and the "make-it-go people".

I did not enjoy this article, I think the dichotomy it presents is simplistic
and wrong. It frames the people who care about the "how" of programming to be
somehow less practical. Just make it go brrrrr, bro. Stop caring how the sausage
gets made!

It also bothers me because I am both. I care deeply about the craft of software,
I also care deeply about shipping working software, on time and within budget.
That to me _is_ the craft. The real question then is whether you care about
_what_ you ship, or if you simply care _that_ you ship. If it's the former, if
you ever hope to rise above the mediocre, then there's no escaping the how, the
craft.

Baldur Bjarnason had a somewhat different take. In [The two worlds of
programming: why developers who make the same observations about LLMs come to
opposite
conclusions](https://www.baldurbjarnason.com/2026/the-two-worlds-of-programming/)
he also talks about a hidden divide, that AI-dev tooling has laid bare:

> - Those who believe software development and the software industry has been going from strength to strength – “software is eating the world” – and this is just making that happen faster.
> - Those who believe we’ve been in an incrementally escalating software crisis since at least 2007.


https://www.anildash.com/2026/03/13/coders-after-ai/

We don't mourn the craft, we mourn a world we could have had, where software was actually good, where this staggering growth in computing power had actually gotten us something.

It's that economics has forced us to accept that we can only get more shittier software. Software can't get better anymore, it can only get _more_.

# Discernment

- seeing through the layers
- inward towards bare metal
- outward, towards the user and the environment

Why does Linus get angry when people submit half-assed patches? because the details matter. It's hard to uphold quality in a code base. Linux is the foundation for a huge portion of computing in the world. It is that because it is, on the whole, rock solid.

# Infinte bugs

Glenford Myers: In The Art of Software Testing, he posits that it is impossible to test a program completely. Since you cannot test every possible input combination in a non-trivial system, you can never prove the absence of bugs—only their presence.

Edsger W. Dijkstra: Famously said, "Program testing can be used to show the presence of bugs, but never to show their absence!"


Lubarsky’s Law of Cybernetic Entomology—"There is always one more bug"

- Fixing a bug often creates a new one: (The "Regression" effect).
- Bugs hide other bugs: A major defect might prevent a certain code path from ever being reached; once you fix it, you "unlock" the hidden bugs in that new path.
- Complexity is infinite: In a non-trivial system, the number of possible states is so large that exhaustive testing is mathematically impossible.

# Code is a monolith

Part of the problem is that AI coding discourse tries to make general statements about "coding", but there's a huge variety of activities that all considered "coding".

- writing a database engine
- doing web development (backend, frontend)
- piecing APIs together
- data science and statistical analysis
- linux kernel development
- embedded systems
- device drivers
- dev tooling
- automate-for-yourself spreadsheet stuff
- (dev)ops / platform engineering
- desktop applications
- home automation systems


 you cannot simultaneously expand the public commons and work towards it's enclosure; moreover, if the public commons do not stand for the public good, then it's not really a commons any more.
 
 https://wandering.shop/@xgranade/116259862366462379
