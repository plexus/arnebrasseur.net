###
# Blog settings
###

# Time.zone = "UTC"
require "sanitize"

helpers do
  def logo        ; 'avatar.jpg' end
  def title       ; 'Arne Brasseur' end
  def home_url    ; '/' end
  def description ; 'Emergence for Developers' ; end
  def cover       ; 'background_iguazu_smaller.jpg' end
  def url_prefix(str='')  ; 'http://devblog.arnebrasseur.net' + str ; end

  # Strip all HTML tags from string
  def strip_tags(html)
    Sanitize.clean(html.strip).strip
  end

  APOS   = ?'.freeze
  QUOT   = ?".freeze
  LT     = '<'.freeze
  GT     = '>'.freeze
  SPACE  = ' '.freeze
  EQ     = '='.freeze
  AMP    = '&'.freeze
  FSLASH = '/'.freeze

  E_AMP  = '&amp;'.freeze
  E_APOS = '&#x27;'.freeze
  E_QUOT = '&quot;'.freeze
  E_LT   = '&lt;'.freeze
  E_GT   = '&gt;'.freeze

  ESCAPE_ATTR_APOS = {AMP => E_AMP, APOS => E_APOS}
  ESCAPE_ATTR_QUOT = {AMP => E_AMP, QUOT => E_QUOT}
  ESCAPE_ATTR      = {AMP => E_AMP, QUOT => E_QUOT, APOS => E_APOS}
  ESCAPE_TEXT      = {AMP => E_AMP, APOS => E_APOS, QUOT => E_QUOT, LT => E_LT, GT => E_GT}

  ESCAPE_ATTR_APOS_REGEX = Regexp.new("[#{ESCAPE_ATTR_APOS.keys.join}]")
  ESCAPE_ATTR_QUOT_REGEX = Regexp.new("[#{ESCAPE_ATTR_QUOT.keys.join}]")
  ESCAPE_ATTR_REGEX      = Regexp.new("[#{ESCAPE_ATTR.keys.join}]")
  ESCAPE_TEXT_REGEX      = Regexp.new("[#{ESCAPE_TEXT.keys.join}]")

  def escape_attr(string)
    string.gsub(ESCAPE_ATTR_REGEX, ESCAPE_ATTR)
  rescue => e
    e.to_s
  end

  def escape_text(string)
    string.gsub(ESCAPE_TEXT_REGEX, ESCAPE_TEXT)
  rescue => e
    e.to_s
  end
end

activate :blog do |blog|
  # This will add a prefix to all links, template references and source paths
  # blog.prefix = "blog"

  blog.layout = "post"

  blog.permalink = ":year-:month-:title"

  # blog.permalink = "{year}/{month}/{day}/{title}.html"
  # Matcher for blog source files
  # blog.sources = "{year}-{month}-{day}-{title}.html"
  # blog.taglink = "tags/{tag}.html"
  # blog.layout = "layout"
  # blog.summary_separator = /(READMORE)/
  # blog.summary_length = 250
  # blog.year_link = "{year}.html"
  # blog.month_link = "{year}/{month}.html"
  # blog.day_link = "{year}/{month}/{day}.html"
  # blog.default_extension = ".markdown"

  blog.tag_template = "tag.html"
  blog.calendar_template = "calendar.html"

  # Enable pagination
  blog.paginate = true
  blog.per_page = 10
  # blog.page_link = "page/{num}"
end

page "/feed.xml", layout: false

###
# Compass
###

# Change Compass configuration
# compass_config do |config|
#   config.output_style = :compact
# end

###
# Page options, layouts, aliases and proxies
###

# Per-page layout changes:
#
# With no layout
# page "/path/to/file.html", layout: false
#
# With alternative layout
# page "/path/to/file.html", layout: :otherlayout
#
# A path which all have the same layout
# with_layout :admin do
#   page "/admin/*"
# end

# Proxy pages (http://middlemanapp.com/basics/dynamic-pages/)
# proxy "/this-page-has-no-template.html", "/template-file.html", locals: {
#  which_fake_page: "Rendering a fake page with a local variable" }

###
# Helpers
###

# Automatic image dimensions on image_tag helper
# activate :automatic_image_sizes

# Reload the browser automatically whenever files change
# activate :livereload

# Methods defined in the helpers block are available in templates
# helpers do
#   def some_helper
#     "Helping"
#   end
# end

set :css_dir, 'stylesheets'
set :js_dir, 'javascripts'
set :images_dir, 'images'
set :partials_dir, 'partials'
set :fonts_dir, 'fonts'

activate :syntax
set :markdown_engine, :kramdown
set :markdown, input: 'GFM'

activate :livereload

# Build-specific configuration
configure :build do
  # For example, change the Compass output style for deployment
  # activate :minify_css

  # Minify Javascript on build
  # activate :minify_javascript

  # Enable cache buster
  # activate :asset_hash

  # Use relative URLs
  # activate :relative_assets

  # Or use a different image path
  # set :http_prefix, "/Content/images/"
end

module NotSoObsolete
  def warn *_
    # ignore these kind of warnings
  end
end

URI.singleton_class.prepend NotSoObsolete
