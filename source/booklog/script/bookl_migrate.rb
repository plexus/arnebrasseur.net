# -*- coding: utf-8 -*-
require 'nokogiri'
require 'date'
require 'yaml'
$bookl_dir = '/home/arne/projects/devblog/space-4477979-bookl-c7b2807fd6bb8ec5660bb9c127bb4da9'

File.write( 'booklog/history.yaml', YAML.dump(
    Dir[$bookl_dir+'/posts/*.xml'].map do |f|
      doc = Nokogiri( IO.read(f).gsub(/<(wp|content):/,'<') )

      result = {}

      %w(title link pubDate post_name).map do |tag|
        result[tag] = (doc/tag).map(&:text).first
      end

      result['pubDate'] = DateTime.parse(result['pubDate'])

      result['img_url'] = (Nokogiri((doc/'encoded').text)/'img').first.attributes['src'].value

      img_base = result['img_url'].sub(/\A.*\//,'').gsub(/\.scaled.*/,'').gsub(/.{4}\Z/,'').gsub('_', '?')

      result['img_file'] = Dir[$bookl_dir + '/image/**/*'+ img_base + '*'].first

      if result['img_file'].nil?
        p img_base
      end

      if result['post_name'] =~ /^\d+$/
        result['post_name'] = result['title'].gsub(' - ', ' ').gsub(' ', '-').downcase.gsub("'",'')
      end

      result
    end.to_a
))
