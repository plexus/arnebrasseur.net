---
title: Converting FOAF to OPML
date: '2007-08-15T14:22:15+00:00'
author: Arne
permalink: /2007/08/15/converting-foaf-to-opml
language: en
tags:
- Tech
- Ruby
---

_This was published on a personal blog I ran during the nillies, it's included here as a historical archive. Links and images may be broken, it's been a while._

Soon my first professional contract comes to an end, and I will have time again to develop my newsfeed addiction.

Previously I used Bloglines, which is certainly one of the better on-line feed readers. I'm switching to a desktop feed reader however, I'm trying out Liferea.

I'd like to add all the [Planet Ugent](http://www.planet-ugent.be) blogs to my list of feeds, but the available OPML contains no URL's. Pretty useless this way. Nothing is lost however, since they still have a FOAF export. The FOAF however contains only the blog URLs, not the actual feeds.

Ruby to the rescue!
    
    
    require 'rubygems'
    require 'open-uri'
    require 'hpricot'
    
    doc = Hpricot open('http://planet-ugent.be/foafroll.xml')
    
    def extract_feed(url)
      return 'error://no url' unless url && url != ''
      begin
        page = Hpricot(open(url))
        %w(application/atom+xml application/rss+xml).each do |t|
          link = page.at("link[@type=#{t}]")
          if link
            link = link.attributes['href']
            if link =~ /^\\//
              link = url[/^[\w]+:\\/\\/[^\\/]+/] + link
            elsif link !~ /^[^\\/]+:\\/\\//
              link = url[/^.*\\//] + link
            end        
            return link
          end
        end
        'error://not found'
      rescue Timeout::Error
        'error://timeout'
      rescue SocketError
        'error://socket error'
      end
    end
    
    feeds = doc.search('foaf:member').map do |m| 
      name = m.at('foaf:name').inner_html
      url = m.at('foaf:document').attributes['rdf:about']
      [name, extract_feed(url)]
    end
    
    puts %(<opml version="1.1">
      <head>
        <title>Planet UGent
        <dateCreated>#{Time.now.rfc822}
        <dateModified>#{Time.now.rfc822}
        <ownerName>Ikke
        <ownerEmail>eikke at eikke dot commercial
      </head>
      <body>
    )
    
    feeds.each do |name, url|
      puts %(    <outline text="#{name}" xmlUrl="#{url}"/>\\n)
    end
    
    puts "  </body>\\n</opml>"
    

This script will extract the FOAF names and urls, load each page and extract the feed. Atom feeds get precedence over RSS feeds. It should be able to handle relative URLs, but this is not thoroughly tested. The OPML is written on standard out.

Find the result [here](/wp-content/uploads/ugent.opml).
