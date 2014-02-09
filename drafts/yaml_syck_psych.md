# Notes


* documents containing only a scalar end with ... in Psych
* space after '---' in Syck, not in Psych
* numbers in strings : double quotes in Syck, single quotes in Psych
* Handling of escaping in strings
    [33] pry(main)> Syck.dump('\ ')
    => "--- \"\\\\ \"\n"
    [34] pry(main)> Psych.dump('\ ')
    => "--- ! '\\ '\n"
* Syck and Psych use different heuristics for storing large chunks of text, but their output seems to be compatible

```yaml
---
key: "block of text
  continued
  \ line that starts with a space"
```

The way to trigger this from Psych is to make it sure it's using double quotes by putting in an escape character like \n, then making sure it breaks before multiple consecutive spaces

```yaml
---
foo:
  bar:
    baz: "foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo foo
      \  \n"
```

This output is parsed incorrectly by Syck, it puts a literal backslash in the output.

```ruby
x='---
key: "block of text
  continued
  \ line that starts with a space"'
# => "---\nkey: \"block of text\n  continued\n  \\ line that starts with a space\""
Psych.load(x)
# => {"key"=>"block of text continued  line that starts with a space"}
Syck.load(x)
# => {"key"=>"block of text continued \\ line that starts with a space"}
```

* Timestamps with timezones:
Syck converts to UTC, psych keeps timezone
created_on: 1899-11-30 00:00:00 -00:25
1899-11-30 00:25:00 UTC > 1899-11-29 23:09:39 -0025

* handling of !binary (base64 encoded)
    name: !binary |
      R3LDoWRh
Syck returns a string with the default encoding, Psych returns no/unkown (ASCII-8BIT) encoding
"Gráda", "Gr\xC3\xA1da"

* Treatment of a literal \r (^M)

:note: "To be paid for by 5th July - CL^M"
Syck : "To be paid for by 5th July - CL\r", Psych: "To be paid for by 5th July - CL "

* Hash keys >= 128 characters

Psych prefixes with a question mark and puts the value on the next line

## Results

* Before replacing '!str '

discount_awards options 1316 / 8332
discount_conditions options 218 / 4715
discounts options 13 / 4546
line_items options 3899 / 24342049
products options 10 / 8249
settings value 28 / 61644

* After replacing '!str '

Identical but

settings value 0 / 61644
So all the ones in settings are due to '!str'

* After doing manual recoding of strings to utf-8

yaml=yaml.gsub(/(\\x[0-9A-F]{2})+/) do |x|
  x.split('\x').drop(1).map(&:hex).pack('C*').force_encoding('UTF-8')
end

discount_awards_options.csv 45/1316
products_options.csv 0/10
discounts_options.csv 0/13
line_items_options.csv 262/3899
settings_value.csv 0/28
discount_conditions_options.csv 6/218

What's left is !binary, timezones and \r.