---
title: "Lambda Island Turns One, The Story of a Rocky Ride"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2017-05-13T13:14:34.000Z"
updated_at: "2026-05-16T13:42:10.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:20"]
---

One year ago to date I launched Lambda Island, a service that offers high quality video tutorials on web development with Clojure and ClojureScript. It’s been quite a ride. In this post I want to look back at the past year, provide some insight into how this experience has been for me, and give you a glimpse of what the future has in store.

This story really starts in December 2015. After three years of doing contract work for [Ticketsolve](http://ticketsolve.com) I decided it was time for a change. I have been self-employed for many years, but I knew that sooner or later I wanted to try my hand at selling a product, rather than selling my time.

In January and February I took some time for soul-searching, and recharging. I went to speak at RubyConf Australia, and got to hang out with some old friends around Australia and New Zealand. Once back in Berlin I got busy creating Lambda Island.

I decided to build the site from scratch. There are plenty of reasons why this isn’t the best idea, many would argue I’d be better off using an existing platform, but so far I haven’t regretted the choice. I felt it was important for my street cred to be running a Clojure app in production.

I did choose to make use of a handful of well chosen SaaS providers to do the heavy lifting. [Chargebee](https://www.chargebee.com/) does all the subscription management and invoicing, and I’m very happy with their service. EU VAT regulation is complex and a pain to implement, but they handle that swimmingly.

Payment is handled through Chargebee by [Stripe](https://stripe.com/), video hosting is done by [Vimeo](https://vimeo.com/), emails are sent with [Mailgun](https://www.mailgun.com/), drip campaigns go out with [Sendwithus](https://www.sendwithus.com/), and the whole thing runs on [Upcloud’s](https://www.upcloud.com/) virtual servers. Upcloud generously sponsored the last ClojureCup, and the credit we won there kept the servers up for almost a year.

I also had to figure out how to create screencasts. I had made some videos before, but there’s a big difference between just recording a live coding session, and actually creating a high quality screencast. With trial and error I managed to come up with a recording and editing workflow that worked for me, and I’ve been improving upon that ever since. I’ll leave the details of that for another post.

After figuring out some legal aspects and creating the first few videos I was finally ready to launch, and on 13 May 2016 I welcomed my first customers.

> I have customers now. The world will never be the same again.   
>   
> — 𐌐𐌋𐌄𐌗𐌖𐌔 (@plexus) [May 13, 2016](https://twitter.com/plexus/status/731089209684074496)

I would need about 700 subscribers to make what I was making before with contract work, but I was prepared to take a big pay cut. I figured with 200 customers I would make enough to live, or 300 to live modest but comfortably. If I could get about 30 new subscribers each month, then by the time my savings would run out I would be self-sufficient, all by the end of 2016.

Spoiler: that’s not quite what happened. In the past month I finally broke through the first 100, and it’s been a big struggle to stay afloat.

Here you see the number of activations (initial signups or people who cancelled earlier and the re-activated their subscription), and number of cancellations (a.k.a. “The Churn”), for each month.

![](__GHOST_URL__/img/blog/20170513-activations.png)![](__GHOST_URL__/img/blog/20170513-churn.png)

It’s been quite a rollercoaster ride. It all started off well. Twenty-five people signed up in the first two weeks. Many of those are still happy customers today. June and July were slow, but in August things were starting to pick up, and all through fall I had ten to fifteen new members each month.

At that point I had been working long days and weekends for close to half a year. I started getting more migraines and tension headaches, I had been getting sick more and more often, and mentally I was not in a good place. At the same time my financial reserve was getting dangerously low.

Around September I knew I needed a break, but I also urgently needed to make some money. I started looking for freelance Clojure work, but that wasn’t that easy to come by, especially not remote or part-time. I ended up taking on a project for [Mein Grundeinkommen](https://www.mein-grundeinkommen.de/), a non-profit working to supply people with basic income. The break would have to wait.

This brought some financial relief, but it did keep me busy for a while, so in October and November only two new videos came out. After that I found a gig teaching programming a few hours a week. Together with what was slowly starting to come in from Lambda Island I could already cover a good part of my costs.

At the same time I started making some life style changes. I made an effort to block the weekends and evenings for things unrelated to work. I cut my alcohol consumption way back. I wasn’t a big drinker before, but I found I was getting very sensitive to it, and just a few drinks would have me feeling sick and depressed for days. I started doing more sports, and made sure to get enough sleep. Since January I’ve been doing an hour of [Alexander Technique](https://en.wikipedia.org/wiki/Alexander_technique) once a week with a teacher, and for over a month now I’ve been meditating once a day with [Headspace](https://www.headspace.com/).

I still haven’t gotten the break that I needed, and I still get sick too easily, but I’m in a much better place than I was six months ago. I’m feeling productive again and optimistic for the future. I’ve learned some hard lessons from it all, and in that sense it’s been an important experience. I’ll also be taking two weeks of actual holiday this summer, one week with my family, and one week at the [European Juggling Convention](http://ejc2017.org/), which I’m sure will do me good.

So that’s where we are now. One year in. One hundred subscribers. Why hasn’t it taken off the way that I had hoped?

I am convinced the product itself is not the problem. People seem to genuinely appreciate the content I make. I did a survey to see why people cancelled, and the most common reason is they simple no longer use Clojure. The only complaint about the product itself is that there’s not enough new content coming out. This is understandable, but it’s also not something I can do much about at this point. And at 12 USD per month I think it’s fair to say people are getting their money’s worth.

In fact I’ve been meaning to raise the price for some time now, but since February sign-ups are barely keeping pace with cancellations, which has made me hesitant. At the same time I’m afraid that people think the low price reflects the quality of the content. So before long prices will have to go up, to make it clear that this is a luxury product, not fast food.

So if the product is good, then it follows that the problem is lack of marketing. Saying I haven’t done enough marketing is stating the obvious. So far all I’ve really made use of is social media and content marketing. Twitter and r/Clojure are two good sources of traffic.

There’s a rule of thumb that half of your time and effort should be spent on marketing, and the other half on developing the product. Most of my time goes into creating content, and hustling freelance gigs. I have a long list of things to try out though, but acting on them will take time.

So for the time being I need to accept that Lambda Island will remain more of a side gig then a primary source of income. Approaching it from that angle will give me a lot more peace of mind, and will give me time to gradually grow it into something that can stand on its own two feet.

So my plan is to stick to the current two-to-three week publishing schedule. I have a two freelance gigs that I’m wrapping up at the moment, and after that I’m looking to do ClojureScript contract work for two days a week. That should give me enough financial stability to worry less, and spend those three other days working productively on Lambda Island.

Thanks for reading this far. I felt it was important to tell this story, so people in the community know what’s up. I strongly believe in the power of open and honest communication, and with posts like these I want to take you all along on this journey.

Some people have asked what they can do to help. Here are a few things you can do,

  * Sign up for a subscription
  * Get your company to sign up for a team membership
  * Get your company to sponsor a video
  * Try the free trial and give me feedback
  * Tell your friends about Lambda Island, tell people what you like about it
  * Share links to the blog posts, freebies, the REPL guide, on Twitter/Facebook/Reddit/Slack



Thanks for listening, here’s to the next year of Lambda Island!

🡅 [Comment on /r/Clojure](https://www.reddit.com/r/Clojure/comments/6axfdy/lambda_island_turns_one_the_story_of_a_rocky_ride/)
