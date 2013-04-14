require 'yaml'
require 'pp'

YAML.load(IO.read '../history.yaml').each do |post|
  title     = post['title']
  link      = post['link']
  pubDate   = post['pubDate']
  post_name = post['post_name']
  img_url   = post['img_url']
  img_file  = post['img_file']

  date = pubDate.strftime('%Y-%m-%d')
  md_file = "../#{date}-#{post_name}.md"
  img_target = "images/#{date}-#{post_name}.#{img_file[-3..-1]}"

  `cp "#{img_file}" "../#{img_target}"`

  File.write(md_file, <<-EOF)
---
title: #{title}
date: #{date}
layout: booklog
image: #{img_target.sub(/booklog./,'')}
---
![#{title}](#{img_target.sub(/booklog./,'')})
EOF


end

# {"title"=>"Het Verlangen - Hugo Claus",
#  "link"=>"http://bookl.posterous.com/het-verlangen-hugo-claus",
#  "pubDate"=> "DateTime: 2012-06-14T23:35:17+02:00 ((2456093j,77717s,0n),+7200s,2299161j)>",
#  "post_name"=>"het-verlangen-hugo-claus",
#  "img_url"=> "http://getfile4.posterous.com/getfile/files.posterous.com/bookl/84qkOjVQQKsnbceytnqAHPKQoGvFht2vLmfZqoHtkGcRkm6oFAhsDNMUjqny/2012-06-14_23.15.15.jpg.scaled.500.jpg",
#  "img_file"=> "/home/arne/projects/devblog/space-4477979-bookl-c7b2807fd6bb8ec5660bb9c127bb4da9/image/2012/06/42102884-2012-06-14 23.15.15.jpg"
# }
