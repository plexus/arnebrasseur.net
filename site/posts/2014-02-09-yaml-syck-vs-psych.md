---
title: "Syck vs Psych: Differences and Conversion"
date: 2014-02-09
---

YAML (rhymes with camel) is a data serialization format designed to be both human and machine readable. It's distinguishing features are use of semantic whitespace, and support for a rich set of built-in and user defined types.

While not the inventor of the format, the first widespread implementation of YAML was written by Ruby-famous and now virtually deceased "Why the lucky stiff". His C implementation, titled Syck, became part of the Ruby distribution, and got bindings to several other languages as well.

Later on the PyYAML project wrote their own "libyaml", which better kept up with the evolving YAML specification, and has since become the implementation recommended as a reference by the YAML folks.

Aaron Patterson wrote bindings to libyaml, called Psych, which made it into Ruby 1.9.2. With the release of 1.9.3 Psych became the default YAMLer, although Syck was only removed in 2.0, so 1.9 users could still opt-out of using the new version.

All of this is old news, why bring it up again? Two reasons: there are probably still quite a few systems running on Syck because of legacy YAML data, and I couldn't find any resource on the web describing the differences in behavior between Syck and Psych.

At Ticketsolve we have a number of database columns that contain serialized YAML, several dozen of millions of records. This is why up to now we have kept using the old Syck. It's also the main reason we haven't migrated to Ruby 2.0 yet. So I set out to investigate how involved the change would be. I also thought that it would be a great opportunity to document the differences in behavior, based on a large enough real world data set.

The first question is : if we simply flip over and read our existing data with Psych, what would happen? It turned out that 0.016% of records would be interpreted differently. Not a lot in relative terms, but still thousands of records. These are the main differences we have found:

## Representation of non-ASCII Strings

Syck dates from a time when Ruby was still blissfully unaware of string encodings. Strings were byte arrays, rather than character arrays. Interpretation of those bytes was left to the program. To prevent emitting invalid output when fed random binary gobbledygook, Syck will emit hexadecimal escape sequences for any higher order bytes (decimal value >= 128).

```ruby
puts Syck.dump('utf8' => 'é', 'latin1' => 'é'.encode('ISO-8859-1'))
# ---
# utf8: "\xC3\xA9"
# latin1: "\xE9"
```

Psych will convert any strings it encounters to UTF-8, and then output actual UTF-8.

```ruby
puts Psych.dump('utf8' => 'é', 'latin1' => 'é'.encode('ISO-8859-1'))
# ---
# utf8: é
# latin1: é
```

Interestingly, Syck will happily and correctly parse the UTF-8 version emitted by Psych.

This difference caused the majority of incompatible interpretations. But we found more!

## Single vs Double Quotes

Syck and Psych seem to have different heuristics for when to pick single quotes, when to go for double quotes, and when to use "block text" syntax. It seems Syck pretty much always uses double quotes, whereas Psych will only switch to doubles when the string contains something like a newline which requires an escape sequence.

```ruby
puts Syck.dump('\ ')
puts Psych.dump('\ ')
# --- "\\ "
# --- ! '\ '

puts Syck.dump("\n")
puts Psych.dump("\n")
# --- "\n"
# --- ! "\n"
```

Digging more into this I found one case where Psych output confuses Syck. When Psych uses double quotes, and needs to output multiple consecutive spaces right after a line break, it will start the new line with a backslash. When reading this Syck will read an actual backslash.

```ruby
p data = {foo: {bar: {baz: 'foo '*18 + "   \n"}}}
p Syck.load(Psych.dump(data))
# {:foo=>{:bar=>{:baz=>"foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo    \n"}}}
# {:foo=>{:bar=>{:baz=>"foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo \\   \n"}}}
```

## Long Hash Keys

When the key of a Hash is longer than 128 characters long, Psych will put the key and value on separate lines, using special YAML prefixes (? and :) to indicate which is which.

```ruby
puts Psych.dump('x'*129 => 'y')
# ---
# ? xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
# : y
```

## Documents Containing a Single Scalar

Psych will put an "end of document" marker consisting of three dots after a document consisting of a single string, symbol, number, boolean or null. This is a difference in how they generate YAML, although both parsers can correctly read the other's output in this case.

```ruby
puts Psych.dump('foo')
# --- foo
# ...
p Syck.load(Psych.dump('foo'))
# "foo"
```

Another tiny change is that Syck will always put a space after the opening marker, while Psych does not.

```ruby
p Psych.dump('abc' => 123)
p Syck.dump('abc' => 123)
# "---\nabc: 123\n"
# "--- \nabc: 123\n"
```

## Red Herrings

Last year after a number of vulnerabilities concerning Rails and YAML were exposed we started using the 'safe_yaml' gem. The main feature of SafeYaml is that it allows you to whitelist the types of objects that can be created when deserializing YAML. A `HashWithIndifferentAccess` is probably fine, a Rails `RouteSet` might be more problematic.

SafeYaml will only install itself for the YAMLer that is active at the time it is loaded, so in our case it was active for Syck, not for Psych. I found differences in how timestamps with timezone information were handled, that went away when disabling SafeYaml. Similarly, data like the following, coming from YAML generated by [as3yaml](https://code.google.com/p/as3yaml/) was being incorrectly interpreted because we hadn't whitelisted the `!str` tag.

```yaml
---
:lat: !str 53.363665
:long: !str -8.02002
:zoom: !str 7
```

So the numbers were being interpreted as numbers, rather than strings.

## Converting

The only way to be certain your data will be identically interpreted after conversion as before is to read it with Syck, then dump it again with Psych. You'll have to do this on Ruby 1.9.3 so you have both implementations available. First make sure Syck is properly loaded by changing the YAML engine:

```ruby
require 'yaml'
YAML::ENGINE.yamler = 'syck'
```

Now you can use `Syck.load` and `Psych.dump`. An earlier blog post I found on the topic tells you to switch engine, load the data, then switch again and dump it. If you do this in a tight loop you will find this to be horribly slow, so just use the constants directly.

Note that you basically need to stop the world while the conversion happens, then immediately afterwards start using Psych for everything. To limit the time this migration takes, I'm first generating a list of all primary keys that are affected. Remember this was only 0.016%, so that will be a big speedup, and you can prepare this beforehand.

```ruby
records.each do |id, yaml|
  ids << id if Syck.load(yaml) != Psych.load(yaml)
end
```

After converting these you only need to run the conversion for records that have been created or changed since scanning for differences.

## The Future : JSON?

Having faced security and other issues with YAML, people are opting more and more for JSON. In fact we are also looking to eventually move this data to JSON. One thing to keep in mind is that JSON is significantly less powerful than YAML. It can only represent strings, numbers, booleans and null. No timestamps, symbols, or other richer types. Some extensions do allow storing timestamps, but these are non-standard and not guaranteed to work in an inter-operable way.

We've come to realize that YAML probably isn't be best format for serializing data, I think that's a good thing. YAML's most redeeming quality in my humble opinion, is that it's great for data that is mostly written and managed by humans, like configuration files, where the strictness of JSON (trailing commas anyone?) can be an annoyance.

## Conclusion

For such a "simple" format YAML has a surprising number of gotchas and subtleties. I've learned a lot about the format in conducting this exercise. Incompatible implementations of formal languages can be real nuisance, but also a security liability. I highly recommend looking into some of the [Langsec](http://langsec.org) material if that stuff interests you.