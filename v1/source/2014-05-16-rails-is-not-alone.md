---
title: Rails is No Longer Alone
date: 2014-05-16
---

This is an article I've been meaning to write for a while. I was pushed over the edge by Adam Hawkin's post [Fragmentation in the Ruby Community](http://hawkins.io/2014/05/fragmentation_in_the_ruby_community/). Adam writes that fragmentation in the community is accelerating, and that Rails is a major fault line. I'd like to add some context and my personal view on the Rails vs "pure Ruby" debate.

I don't remember exactly the first time I came across Ruby. It must have been 2005 or 2006, and it most certainly was because of Rails.

Rails was revolutionary at the time. It combines a powerful and pleasant language with a pragmatic "rapid application development" approach. Getting so much from doing so little was truly a breakthrough. And Rails is still one of the most complete solutions out there. But it has its flaws.

When talking about Rails we have to remember the time it stems from. Rails chose a language that not many people were using at the time, and Ruby's ecosystem was tiny compared to what is out there now. There was no Rack, no Nokogiri, no Rspec. Chances of already being a Ruby programmer and then moving on to Rails were small. Instead people came to Rails from Java, C++, Perl, Python. They learned "Rails Ruby", the distinction didn't matter.

There also wasn't a whole lot available in the Ruby world to build upon when it came to web programming. People were using the standard lib's CGI module, that's pretty much as far as it went.

These circumstances make some choices that Rails made very reasonable. The Ruby language explicitly allows extending core classes, so why not use that capability to make it even easier, even better? There weren't that many third party libs available, so interop was only of limited concern. In fact most third party stuff that came later was built explicitly for Rails, so those libs would take care of not conflicting with Rails, with ActiveSupport.

The ability to reopen classes was in turn embraced by much that came after Rails, extending and changing Rails classes to make them "even better". It wasn't too hard to see this style of development would lead to a mess. But hey, it worked, startups shipped and made money. Life was good.

Looking at it from a 2005 perspective also explains why Rails has this "everything but the kitchen sink" approach. Rails was ambitious from the start, trying to solve as many challenges of web development it possibly could. And since there was very little to build upon, it had to do all of that itself. This explains the strong "Not Invented Here" tendencies of the Rails developers. And the fact that "adding tech available in gems to core" is an [official policy]( https://github.com/rails/rails/pull/12824#issuecomment-31039769).

In short, Rails assumes it is alone in the world, whereas in fact it no longer is. We now have a rich Ruby ecosystem. Building on top of that, using a modular approach, promoting libraries over frameworks, having lean, focused components each with their own maintainers. Libraries that stick to their own namespace (sorry, major pet peeve there). That's how I envision a healthy Ruby ecosystem. I think it's what many people hunker for. It's what I see in initiatives like [micro-rb](http://microrb.com/).

I think what turns people away from Rails is basically this, the accidental complexity that grew out of being an island. The ball of mud that is ActiveSupport. I don't see this getting better. But I do see a vibrant community of people and projects with a different vision, one that grows by the day. One that values high quality Ruby code, and high quality Ruby implementations. The future is bright. Yay Ruby!