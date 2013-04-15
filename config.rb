###
# Compass
###

# Susy grids in Compass
# First: gem install susy
# require 'susy'

# Change Compass configuration
# compass_config do |config|
#   config.output_style = :compact
# end

###
# Page options, layouts, aliases and proxies
###

# With alternative layout
# page "/path/to/file.html", :layout => :otherlayout
#
# A path which all have the same layout
# with_layout :admin do
#   page "/admin/*"
# end

# Proxy (fake) files
# page "/this-page-has-no-template.html", :proxy => "/template-file.html" do
#   @which_fake_page = "Rendering a fake page with a variable"
# end

page "/feed.xml", :layout => false

###
# Helpers
###

# Automatic image dimensions on image_tag helper
# activate :automatic_image_sizes

helpers do
  def blog_config
    {
      :title    => '@plexus Devblog', #'',
      :subtitle => 'All Over the Map',
      :author   => 'Arne Brasseur',
      :url      => 'http://arnebrasseur.net'
    }.freeze
  end

  def feed_articles
    #blog.articles[0..5]
    blog.articles
  end

  def booklog_articles
    sitemap.resources.select do |resource|
      resource.path =~ /booklog\/\d{4}.*html/
    end.map {|r| r.metadata[:page].merge('path' => r.path) }.sort do |a1,a2|
      a2['date'] <=> a1['date'] || 0
    end
  end
end

set :css_dir, 'css'
set :js_dir, 'js'
set :images_dir, 'img'


# Build-specific configuration
configure :build do
  # For example, change the Compass output style for deployment
  activate :minify_css

  # Minify Javascript on build
  # activate :minify_javascript

  # Enable cache buster
  # activate :cache_buster

  # Use relative URLs
  # activate :relative_assets

  # Compress PNGs after build
  # First: gem install middleman-smusher
  # require "middleman-smusher"
  # activate :smusher

  # Or use a different image path
  # set :http_path, "/Content/images/"
end

activate :blog do |blog|
  blog.permalink = ":year-:month-:title"
end

activate :syntax
set :markdown_engine, :redcarpet
set :markdown, :fenced_code_blocks => true, :smartypants => true

activate :livereload


class Middleman::Application
  # Stop middleman from munging our image paths
  def image_path(src)
    src
  end
end
