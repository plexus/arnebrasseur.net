---
title: "Two Years of Lambda Island, A Healthy Pace and Things to Come"
type: "post"
status: "published"
visibility: "public"
featured: false
date: "2018-06-15T14:15:02.000Z"
updated_at: "2026-05-16T13:42:05.000Z"
authors: "Arne Brasseur"
tags: ["Lambda Island", "#Import 2025-09-09 12:23"]
---

It’s been just over two years since Lambda Island first launched, and just [like last year](https://lambdaisland.com/blog/2017-05-13-lambda-island-turns-one) I’d like to give you all an update about what’s been happening, where we are, and where things are going.

To recap: the first year was _rough_. I’d been self-employed for nearly a decade, but I’d always done stable contracting work, which provided a steady stream of income, and made it easy for me to unplug at the end of the day.

Lambda Island was, as the Dutch expression goes, “a different pair of sleeves”. I really underestimated what switching to a one-man product business in a niche market would mean, and within months I was struggling with symptoms of burnout, so most of year one was characterised by trying to keep things going and stay afloat financially, while looking after myself and trying to get back to a good place, physically and mentally.

Luckily that all worked out, and during this second year I’ve managed to find a steady, sustainable pace. I’ve been recharging, I even managed to take some holidays this time, and I’m slowly rebuilding my previously depleted savings.

I’m still not able to live off Lambda Island completely, but it provides a good bit of income. One lesson I’ve learned first hand is that the greatest product is worth nothing without good marketing, and of all the hats I wear the one that says “Marketer” is perhaps the one that suits me least.

Still, I have gained some recognition for my efforts in Clojure’s community and in Open Source development, as well as for creating content of exceptional quality, and so there continues to be a steady trickle of new signups. This year has also seen more teams signing up for company plans, which I think is a great development.

It makes a lot of sense, Lambda Island has always focused on the kind of things you’d need to know and use in an actual job, rather than on what’s the latest hotness, and so teams are finding it a great way to quickly [introduce people to Datomic](https://lambdaisland.com/episodes/datomic-quickstart-part-1), [teach people foundational Clojure concepts](https://lambdaisland.com/collections/clojure-foundations), or improve their [approach to testing](https://lambdaisland.com/collections/clojure-testing).

## Coaching and Training

Hiring and onboarding Clojure developers isn’t always easy. There is only a limited amount of senior Clojurists in any given locality, and so companies have to train and mentor more junior profiles, as well as experienced devs coming from other languages. Lambda Island can be an excellent resource for this.

I’ve been helping one company with this process directly. [Nextjournal](https://nextjournal.com/) is building an ambitious product using Clojure and ClojureScript, but most of the devs are coming from Erlang and Elixir.

Over the past year I’ve helped them figure out issues with their tooling to make sure everyone can work comfortably. Through one-on-one coaching sessions and code reviews I’ve helped people to grasp Clojure’s guiding principles, adopt a REPL-based workflow, get better at writing idiomatic code, and generally get over the uncertainty of “are we doing this right?”.

From talking to people at conferences and meetups it seems there are more companies that could benefit from this kind of personal coaching, and so I’ve been talking with some talented Clojure people to see if we could start offering this as a general service. I’m very excited about this possibility! If you think your company could benefit from any kind of training or coaching by experienced Clojurists, or if you want to be the first to know when this service becomes available, then please drop me a line (arne at lambdaisland dot com). We’re still figuring out the specific, and any concrete input at this point would be extremely valuable.

## Lambda Island

Lambda Island will continue to regularly publish new content, the next episode will be a follow-up episode to [Episode 38. Transducers](https://lambdaisland.com/episodes/transducers). While that one taught you how to use the built-in transducers, the new episode will dig deeper into how transducers work, and look at some powerful transducer librarier like [xforms](https://github.com/cgrand/xforms) and [kixi.stats](https://github.com/MastodonC/kixi.stats).

Year two has seen fewer new episodes than year one, only about a dozen versus the 30 episodes I crancked out in those first twelve months. This has turned out to be a more sustainable pace, and while I would like to increase the frequency a bit again it will remain closer to once a month than to once a week.

You may have noticed that the title screens have started looking a lot nicer though, that’s because this work is now done by the talented [Lubov Soltan](https://twitter.com/moolver_sin), who also created the branding for the [Dutch Clojure Days](http://clojuredays.org/). This has been a big step for me as it’s the first time another person has been involved in producing episodes.

[![](https://i.vimeocdn.com/video/702033997_1280x720.jpg)](https://lambdaisland.com/episodes/react-components-reagent)[![](https://i.vimeocdn.com/video/692032990_1280x720.jpg)](https://lambdaisland.com/episodes/ultimate-dev-setup)[![](https://i.vimeocdn.com/video/688845330_1280x720.jpg)](https://lambdaisland.com/episodes/integrant)[![](https://i.vimeocdn.com/video/678258031_1280x720.jpg)](https://lambdaisland.com/episodes/transducers)

## Buying episodes

Something else I’ve been working on is making it possible to buy individual episodes. So far the only way to access premium content has been through a subscription. This made sense at the beginning, as there wasn’t much content yet, and new videos were coming out regularly. Subscriptions were offered cheap, and what you were really buying was the promise of future updates.

Now that there’s a substantial catalog new subscribers get instant access to 40+ episodes, about 9 hours of content. Subscription prices have gone up a bit, but not nearly as much as they should considering the value you’re getting. On the other hand consumers have become more price conscious about recurring charges for online services.

So the plan is to sell individual episodes instead, they’ll likely be priced around the ten dollar mark. There will still be subscriptions, but they will be marketed more as premium “all access passes” aimed at companies.

This will also make it more rewarding for me to create new content, and to do better marketing, as I can more directly correlate my efforts to sales. I’ll also be able to see more clearly which topics work well and which don’t. In the end there’s no better way to figure out what people want than by letting them vote with their wallet.

Of course all existing customers will be “grandfathered in”, you can keep your existing plan as long as you like, even those that signed up for ridiculously cheap yearly plans all the way in the beginning. You’ve supported me from the start, this is the least I can do to say “thank you”!

## Privacy

Finally I’ve been shipping various improvements and fixes for long standing bugs to the site. Part of this has been to make the site GDPR compliant. Email notifications are now strictly opt-in, and there’s a [Privacy Policy](https://lambdaisland.com/p/privacy-policy) that complements the existing [Cookie Policy](https://lambdaisland.com/p/cookies).

I’ve also removed all third party JS and other assets, with the exception of the Vimeo player, which unfortunately also injects some analytics tracking of its own. We might add back some server-side analytics in the future if it makes sense for marketing purposes, but as it stood the analytics were rarely looked at, so no need to let BigCorp track you because of it.

## Community and Open Source

It has been part of Lambda Island’s mission to support the Clojure community and ecosystem, and plenty has happened on that front this year.

The big one has been that we [relaunched ClojureVerse](https://clojureverse.org/t/announcing-clojureverse-2-0/875). This forum had been up and running for several years, but few people actually knew about it. We figured there was a need for an alternative online space, one that’s less formal than the Clojure mailing list, and less noisy than the Clojurians slack. A warm and welcoming place for thoughtful discourse, and for sharing what you’re learning and working on.

On the [2018 Clojure Community Survey](http://blog.cognitect.com/blog/2017/1/31/clojure-2018-results#block-yui_3_17_2_1_1521472913853_182124)over 17% of respondents mentioned using ClojureVerse, which considering it was only a few months since the relaunch is a really nice result.

We’ve also replaced the [Clojurians slack log](https://clojurians-log.clojureverse.org/) with a proper Clojure app ([github](https://github.com/clojureverse/clojurians-log-app) and [announcement](https://clojureverse.org/t/replacing-the-clojurians-slack-log/1614)). The old kludge of Python and Node scripts had become a nightmare to maintain. By moving it to a Clojure web app it has become a lot easier for people to submit contributions. Message parsing and rendering has much improved, and we’re properly showing threaded messages, a Slack feature that didn’t exist when the old site launched.

There is still work to be done. So far we’ve focused on getting the thing run smoothly and render things properly, and to make sure the site stays accessible when half a dozen indexing bots are crawling it at once. It’s all taken a bit of time, but this is a long term effort, and eventually we’re getting there.

The main thing that’s still missing is to automate the import of new logs into the database. This is currently still a manual process, which means that the site is often quite a bit behind. This too will get sorted out in time though.

Lambda Island picks up the tab for the hosting and the domains for both ClojureVerse and the Clojurians log, and does the bulk of the system administration and development for these projects.

### Open Source

In terms of open source it’s also been a good year for Lambda Island. We’ve released new versions of [lambdaisland/uri](https://github.com/lambdaisland/uri), [Chestnut](https://github.com/plexus/chestnut), [lambdaisland/ansi](https://github.com/lambdaisland/ansi); contributed to the Emacs world with [Chemacs](https://github.com/plexus/chemacs) and [parseclj](https://github.com/clojure-emacs/parseclj); submitted patches to [clojure.tools.cli](https://github.com/clojure/tools.cli), [integrant](https://github.com/weavejester/integrant), [rewrite-clj](https://github.com/xsc/rewrite-clj), [matcher-combinators](https://github.com/nubank/matcher-combinators), [cider-nrepl](https://github.com/plexus/cider-nrepl), [lein-figwheel](https://github.com/bhauman/lein-figwheel), [sparkledriver](https://github.com/jackrusher/sparkledriver), and [toucan](https://github.com/metabase/toucan). These are often small patches, but integrated over time they have likely made many people’s lives just a little easier.

I’m currently working on a project that could have a real impact on how people structure and run their test code, but since that’s not quite ready for prime-time yet I’ll save the details for a next installment.

## Conclusion

Looking in from the outside it may seem like not much has been happening here this past year, but nothing could be further from the truth. Several things have been brewing behind the scenes, and you’ll get to taste the fruit of that labor before long.

The biggest development is that Lambda Island is no longer a one-man venture, I’ve started collaborating more with others, both formally and informally, and you’ll be seeing much more of that in times to come, which is why I felt I could already dish out a royal “we” a few times in this post.

If you want to stay up to date about future developments then please [sign up](https://lambdaisland.com/register), and opt-in to receive our newsletter.
