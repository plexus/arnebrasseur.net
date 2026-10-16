
{:.center}
Use the space bar or arrow keys to browse the slide.

Some of the images are pretty big, so if you see an empty slide then wait a bit.

---

{: style="float: right"}
![](img/big_yak.png)

# Yaks

## &nbsp;

## ROSSConf

## 25 April 2015

### Arne Brasseur

---
{: fullscreen-img="img/karaoke_terrence.jpg"}

---
{: fullscreen-img="img/karaoke_charles.jpg"}

---
{:.heading}

# What is it?

---
{:.fragments}

## What is it

A library for building APIs

It serializes objects

It's specialized in Hypermedia

---
{:.text-scale-6}

```ruby
customer = Customer.find(27)
yaks = Yaks.new

json = yaks.call(
  customer,
  mapper: CustomerMapper,
  env: rack_env
)
```

---
{:.text-scale-5}

```ruby
class CustomerMapper < Yaks::Mapper
  attributes :first_name, :last_name, :age

  def age
    Date.today - customer.date_of_birth
  end

  link :self,   '/api/customer/{id}'
  link :orders, '/api/customer/{id}/orders',
    if: -> { is_logged_in_customer? }
end
```

---
{:.fragments .text-scale-6}

Three main name spaces

```ruby
Yaks::Mapper*
Yaks::Resource*
Yaks::Format*
```

Very flexible and configurable

---
{:.text-scale-4}

Central representation `Yaks::Resource`

```ruby
Yaks::Resource.new(
  type: "customer",
  attributes: {
    first_name: "Arne",
    last_name: "Brasseur"
  },
  links: [
    Yaks::Resource::Link.new(rel: :self, uri: "/customer/5")
  ],
  forms: [
    Yaks::Resource::Form.new(...)
  ]
)
```

Turned into many output formats: HAL, JSON API, Collection+JSON, HTML, HALO, Transit


---
{:.heading}

# DEMO

---
{:.heading}

# Why?

---
{:.fragments}

## Why I built Yaks

Building hypermedia API + Ember app for Ticketsolve

No existing libraries fitted the bill

Developing in the open would improve quality

---
{:.heading}

# Community

---
{:.fragments}

## Community

Still young, small but friendly

Github issues, Gitter chat

Main contributions: improvements to specific formats

---
{:.heading style="margin-top: 62px"}

{: style="margin-top: -62px"}
# How to contribute?

---
{:.fragments}

## How to contribute?

Use it! report back!

Scratch your own itch

Coding and non-coding skills needed

---
{:.heading style="margin-top: 62px"}

{: style="margin-top: -62px"}
# What's happening?

---
{:.fragments}

## What's happening?

Recent focus: improving mutation coverage

Improve support for various formats

Better docs, examples

---
{:.heading style="margin-top: 62px"}

{: style="margin-top: -62px"}
# This afternoon

---
{:.text-scale-6 .center}

Several issues marked __`ROSSConf`__
or __`Beginner Friendly`__ in Github

---

## Improve HTML output

Ruby / HTML / CSS task

Fun task because **creative** and **immediate feedback**

---

## Check the README with Ataru

Tests code examples in documentation

8 out of 30 currently fail

Cool because you learn a useful new tool

---

## Shorthand attribute syntax

Turn this

``` ruby
class FooMapper < Yaks::Mapper
  attributes :bar

  def bar
    object.baz
  end
end
```

into

``` ruby
class FooMapper < Yaks::Mapper
  attribute :bar do
    object.baz
  end
end
```

---

## Support for Siren

JSON-based format that's becoming popular

Maps very well to Yaks's abstractions

Great for learning about how Yaks works
