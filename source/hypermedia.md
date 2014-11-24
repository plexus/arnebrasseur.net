Hypermedia types

Good initial list at http://amundsen.com/hypermedia/

Ways of rating/classifying REST/Hypermedia approaches
The H-Factor
The H-Factor http://amundsen.com/hypermedia/hfactor/  is a number of criteria that a given type does or does not support. E.g. embedded links, navigational links, link relation types, control data for specifying resource types to be read/written, etc.

The Richardson Maturity Model
http://martinfowler.com/articles/richardsonMaturityModel.html

Hypermedia Core
An attempt to create a unified model across formats http://hyperschema.org/core/

Low level serialization formats
 * JSON
 * XML
 * EDN https://github.com/edn-format/edn
 * HTML

Hypermedia message models
 * Atom http://atomenabled.org/developers/syndication/
 * Siren : https://github.com/kevinswiber/siren
 * JSON-API : http://jsonapi.org/format/#url-based-json-api
 * HAL http://stateless.co/hal_specification.html / http://tools.ietf.org/html/draft-kelly-json-hal-06
 * JSON-LD (JSON for Linked Data, RDF based) http://json-ld.org/
 * Hydra builds on top of JSON-LD to provide a shared vocabulary http://www.markus-lanthaler.com/hydra/
 * Collection+JSON http://amundsen.com/media-types/collection/
 * JSON Hyper-Schema, extensions to JSON-Schema for hyperlinks http://json-schema.org/latest/json-schema-hypermedia.html
 * Verbose, direct implementation of all Hypermedia Core features http://verbose.readthedocs.org/en/latest/overview.html
 * UBER http://hyperschema.org/mediatypes/uber
 * Mason https://github.com/JornWildt/Mason
 * OData http://www.odata.org/   http://en.wikipedia.org/wiki/Open_Data_Protocol
 * Transit - From the Clojure world, JSON based, has links https://github.com/cognitect/transit-format

Meta-formats
 * JSON-Schema http://json-schema.org/

Clients - Ruby
 * HyperResource (HAL) https://github.com/gamache/hyperresource

Clients - Javascript
 * Traverson (HAL, ad hoc) https://github.com/basti1302/traverson
 * Hyperagent.js (HAL) http://weluse.github.io/hyperagent/

Clients - C#
 * Ramone (???) https://github.com/JornWildt/Ramone

RFCs
RFC2616 HTTP 1.1 http://www.ietf.org/rfc/rfc2616.txt

RFC4287 The Atom Syndication Format http://tools.ietf.org/html/rfc4287

RFC4288 Media types http://tools.ietf.org/html/rfc4288

Builds on top of MIME types (http://tools.ietf.org/html/rfc2045)

RFC5988 Web Linking http://tools.ietf.org/html/rfc5988

Formalizes and sets up a registry for link relations as used by Atom.

RFC6570 URI Templates http://tools.ietf.org/html/rfc6570

RFC6902 JSON PATCH http://tools.ietf.org/html/rfc6902

A scheme for specifying changes to be made to a JSON document. Used by JSON-API for sending updates back to the server.

RFC6906 The "profile" link relation http://tools.ietf.org/search/rfc6906

Defines a "profile" link that can be used to specify a specific specialization of a resource type.

W3C docs
CURIE Syntax 1.0 http://www.w3.org/TR/curie/

 A syntax for expressing Compact URIs, used by HAL.

Hymermedia Web Mailing list
https://groups.google.com/forum/?nomobile=true#!forum/hypermedia-web

Interesting thread about a unifying model for clients
https://groups.google.com/forum/?nomobile=true#!topic/hypermedia-web/d6DnumFEcyY

API-Craft Mailing list
General mailing list about APIs, though Hypermedia often comes up.

Custom media types, generic media types and profile links
https://groups.google.com/forum/#!msg/api-craft/5N5SS0JMAJw/b0diFRzopY0J

Long thread on api versioning
https://groups.google.com/forum/#!topic/api-craft/iInGY4vmgro

Books
Building Hypermedia APIs with HTML5 and Node
http://my.safaribooksonline.com/book/programming/javascript/9781449309497

RESTful Web APIs
http://my.safaribooksonline.com/book/web-development/9781449359713

Other interesting links
http://www.foxycart.com/blog/the-hypermedia-debate#.UrMfnkNeCY4

Roy Fielding's dissertation defining rest
http://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm

Heroku on JSON-Schema
https://blog.heroku.com/archives/2014/1/8/json_schema_for_heroku_platform_api?utm_source=newsletter&utm_medium=email&utm_campaign=januarynewsletter&mkt_tok=3RkMMJWWfF9wsRonuKzNZKXonjHpfsX54%2BstWqWwlMI%2F0ER3fOvrPUfGjI4AScJrI%2BSLDwEYGJlv6SgFQrjAMapmyLgLUhE%3D

Blog post comparing various formats with examples
http://sookocheff.com/posts/2014-03-11-on-choosing-a-hypermedia-format/
