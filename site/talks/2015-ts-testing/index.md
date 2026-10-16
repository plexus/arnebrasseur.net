## Topics

* New and improved "Context"
* Test isolation
* RSpec deprecations

---

## New Context

Before

``` ruby
Context.account = ...
Context.subdomain = ...
Context.user = ...
Context.locale = ...
# ...
```

Set and reset in several places

---

## New Context

After

``` ruby
  def call(env)
    Context.context = Context::LazyContext.new(env)
    @app.call(env)
  ensure
    Context.context = Context::NullContext
  end
```

---

## New Context

Benefits

* Always consistent
* Always correctly reset


---


## Keeping Tests Isolated

In principle tests should be isolated

- Each individual tests starts from the same clean slate
- Setup => Test => Teardown
- At the end we're back where we started

---

In practice this it's not always so simple

- Database content
- Globals (`Context`, `ActionController::Base.perform_caching`, ...)
- `Rails.cache`
- Redis (feature flags, ...)
- Test group instance variables
- Object caches (FooBar.null_object_cache, ...)

---

### Database Content

Most often done this way

- start from an empty DB
- test runs in a transaction, rolls back at the end
- DB is empty again for next

---

### Database Content

``` ruby
config.use_transactional_fixtures = true
```

`before :each` / `after :each` are part of the transaction

`before :all` / `after :all` are not!

---

### Database Content

Setup in `before :each` will be rolled back

Setup in `before :all` will not

Few if any tests clean up in `after :all`

What state is the DB in?

---

### What state is the DB in?

Assume nothing

Likely: vanilla

To be sure: `Rspec.describe Foo, :create_vanilla_account do`

More about that later

---

## Example Group Instance Variables

Proper usage

``` ruby
RSpec.describe Foo do
  before :all do
    @member = Member.create!(...)
  end

  before :each do
    @member.reload
  end

  after :all do
    @member.destroy!
  end
end
```

Can be done sparingly to speed things up

---

## Example Group Instance Variables

Caveats

``` ruby
RSpec.describe Foo do
  before :all do
    @member = Member.create!(...)
  end

  before :each do
    @member.reload
    @member.errors = nil
    @member.email_validation = nil
    @member.password_validation = nil
    # what else?
  end

  after :all do
    @member.destroy!
  end
end
```

---

## Example Group Instance Variables

Possible but slower: "hard reload"

``` ruby
RSpec.describe Foo do
  before :all do
    @member = Member.create!(...)
  end

  before :each do
    @member = @member.class.find(@member.id)
  end

  after :all do
    @member.destroy!
  end
end
```

---

## Context

* gets set to `MutableContext` before each test
* gets set back to `NullContext` after each test
* if you must change it in a test, consider using `with_context` / `merge_context`
* to isolate from Context changes elsewhere, use `preverving_context`

---

`with_context` temporarily swaps out the context, swaps the old one
back in the end.

Shields in two directions

``` ruby
with_context account: ..., subdomain: ..., locale: ... do
  # code that relies on new Context
end
```

---

`merge_context` is like `with_context`, but copies over context fields
that aren't mentioned explicitly

``` ruby
  def merge_context(values, &block)
    with_context Context.context.to_h.merge(values), &block
  end
```

---

These are usable in migrations!

``` ruby
require Rails.root.join('spec/global_context_helper')

class FooBar < ActiveRecord::Migration

  def up
    Account.all.each do | account |
      merge_context account: account do

        # do stuff here

      end
    end
  end

end
```

---

## :create_vanilla_account

``` ruby
RSpec.describe FooClass, :create_vanilla_account do
  # ...
end
```

---

## :create_vanilla_account

* creates (new or from dump) in `before :all`
* sets `Context` (and resets for each test)
  * account
  * subdomain
  * user
  * locale

---

## :create_vanilla_account

* Does "smart reloading" of AR instances in `after :each`
  * `reload` if changed (`lock_version`, `cache_version`, `updated_on`)
  * `clear_associations_cache`
  * "hard reload" if destroyed
  * clear `errors`

---

## :create_vanilla_account

No need to

``` ruby
before do
  Context.account = ...
  [@foo, @bar, @baz].map(&:reload)
  # etc.
end
```

---

## :create_vanilla_account

Occasionally the automatic reloading really doesn't fix it, and you have to do a "hard reload" yourself

``` ruby
before do
  @foo = @foo.class.find(@foo.id)
end
```

That's a last resort

---

## :create_vanilla_account

If you find more cases where state leaks between tests, especially
through ActiveRecord instances, ping me, I want to know!

`:force_clean` should never be needed

---

## :create_vanilla_account

Try to keep it "vanilla"

```
RSpec.describe :create_vanilla_account, ticket_zone_unallocated_size: 100 do
```

Provided for existing tests that used it. Prevents us from using the
CSV dump (reloads in 0.1s), forced to do a full "create_vanilla"
(~17s)

Same for

```
create_vanilla_account do
  # mess up your vanilla account here
end
```

Better: change things after loading vanilla, so your changes are rolled back.

---

### :isolated_vanilla_account

This will reload the database before *each* test.

Used for capybara tests. In the case of capybara tests (when ticketbooth/integration_helper.rb) is loaded, it will also add an extra before hook to tests that use this to set the subdomain to `foo`.

Also for tests with `uses_transaction`. This tells Rails/RSpec that, because some code your calling *does* use transactions, the test *should not* be wrapped in a transaction. In other words all bets are off, database state is kept from test to test.

---

## Selenium / Capybara

---

## Inconsistencies that are left

- Vanilla sets `Context.user`, probably should be opt-in
- Move `spec/*_helper.rb` to `lib/test_helper`?
- remove `force_clean`
- `before(:each)` vs `before( :each )` vs `before :each` vs `before`

---

## RSpec deprecations

`stub` vs `double`

*Stub* methods on existing objects

``` ruby
@foo.stub(:method_name => value)
@foo.stub(:method_name) { value }
@foo.stub(:method_name).and_return(value)
```

Create test *doubles*

``` ruby
double('user', logged_in: true)
```

Use double/stub instead of `any_number_of_times`

---

## RSpec deprecations

`not_to raise_error`

Check if no errors are raised. Checking only for a certain type is deprecated, so don't do this

```` ruby
expect {
  ...
}.not_to raise_error(FooException, /message something something/)
```
