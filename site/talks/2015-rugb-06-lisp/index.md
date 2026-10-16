# Growing a LISP

Talk done at the Ruby User Group Berlin, 4 June 2015

Dedicated to [Guy Steele](https://www.youtube.com/watch?v=_ahvzDzKdB0)

Use the space bar or arrow keys to navigate

These slides will make more sense if you read the [speaker notes](notes.txt).

----

## Characters

```
0123456789
abcdefghijklmnopqrstuvwxyz
ABCDEFGHIJKLMNOPQRSTUVWXYZ
!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
```

---

## Digits

```
0123456789
```

---

## Letters

```
abcdefghijklmnopqrstuvwxyz
ABCDEFGHIJKLMNOPQRSTUVWXYZ
```

---

## Special Characters

```
!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
```

---

## Numbers

```
0
1
11
42
8939247
```

---

## Strings

```
"Do not go gentle!"
"Into that good night"
"R2D2"
"*@#%(^$#)(^$#"
```

---

## true & false

```
true
false
```

---

## Primitives

```
12
"hello"
true
```

---

## Reserved Special Characters

```
()"',;`
```

---

## Symbols

```
hello
*rug-b*
i-can-3/5/many-symbols
```

---

## Atoms

```
325352
hello-world
"Ruby is nice but"
"I miss my parentheses"
```

---

## Cons cell

```
(23  . hello)
("my" . "word")
```

---

## Cons cell

```
(car . cdr)
```

---

## Cons cell

```
((7 . amazing) . ("head" . "lines"))
(1 . (2 . ( 3 . things)))
```

---

## nil

```
(nil . nil)
(nil . 5)
(1 . (2 . (3 . nil)))
```

---

## Convention

---

## nil

```
nil
()
```

---

## Cons cell

```
(car . cdr)
(first . rest)
```
---

## Lists

```
()
(1 . ())
(2 . (1 . nil))
(3 . (2. (1 . ())))
```

---

## Lists

```
(1 . (2 . (3 . nil)))
(1 2 3)
```

---

## S-expressions

```
42
"smug"
(+ 2 3 5)
(filter (lambda (x) (> x 3))
        (list 1 2 3 4 5))
(("name" "Arne") ("twitter" "@plexus"))
```

---

## Binding

```
binding:
  name: "Arne"
  twitter: "@plexus"
  drinks: "tea"
  likes: ("programming" "juggling")
```

---

## Eval

```
binding, s-expression -> result
```

---

## Primitives

```
12
"hello"
true
```

---

## Eval: primitives

```
42
==> 42

"rug-b"
==> "rug-b"

true
==> true
```

---

## Symbols

```
hello
*rug-b*
i-can-3/5/many-symbols
```

---

## Eval: symbols

```
binding:
 name: "Arne"
 twitter: "@plexus"
```

&nbsp;

```
name
==> "Arne"
```

---

## List positions

```
(function-pos
  arg-pos1 arg-pos2 arg-pos3)
```

---

## Eval: Lists

---

## Eval: quote

```
(quote (hello 123 ("world")))
==> (hello 123 ("world"))
```

---

## Eval: quote

```
'(hello 123 ("world"))
==> (hello 123 ("world"))
```

---

## Function

```
fn: input1, input2, ... -> output
```

&nbsp;

```
eval: binding, s-expression -> result
```

&nbsp;

```
apply: fn, args -> result
```

---

## Built-in functions

```
+
-
=
>
zero?
car
cdr
cons
list
```

---

## Eval & Apply

```
(EVAL (> 9 (- 10 2)))
=> (APPLY > 9 (EVAL (- 10 2)))
=> (APPLY > 9 (APPLY - (10 2)))
=> (APPLY > 9 8)
==> true
```

---

## Eval & Built-in

```
(zero? 0)           ==> true
(car '(1 2 3))      ==> 1
(cdr '(1 2 3))      ==> (2 3)
(list 1 2 3)        ==> (1 2 3)
(car (list 1 2 3))  ==> 1
(car '(list 1 2 3)) ==> list
(cons 'a 'b)        ==> (a . b)
```

---

## Eval: If

```
(if (> 7 9)
  "it's bigger"
  "it's smaller")

==> "it's smaller"
```

---

## Eval: def

```
(def x 3)
```

&nbsp;

```
binding:
  x: 3
```

&nbsp;

```
(+ x 5)
==> 8
```

---

## Lambda

```
(lambda (x y)
  (+ x y))
```

&nbsp;

```
(x y)   : argument list
(+ x y) : body
```

---

## Eval: Lambda

```
(lambda (x y) (+ x y))
==> {binding: b, args: (x y), body: (+ x y)}
```

---

## Eval: Lambda

```
(def add (lambda (x y) (+ x y)))
```

&nbsp;

```
binding:
  add: {bind: b, args:.. , body:.. }
```

&nbsp;

```
(add 3 4)
=> (APPLY {bind: b, ...} 3 4)
```

---

## APPLY: (lambda (x y) (+ x y))

```
Step 1. bind arguments

(APPLY add 3 4)

binding:
  x: 3
  y: 4
```

---

## APPLY: (lambda (x y) (+ x y))

```
Step 2. evaluate body

binding:
  x: 3
  y: 4

(EVAL (+ x y))
=> (APPLY + (EVAL x) (EVAL y))
=> (APPLY + 3 4)
==> 7
```

---

## Let's use it!

```
(def map
     (lambda (fn l)
       (if (nil? l)
           nil
         (cons (fn (car l)) (map fn (cdr l))))))

(map (lambda (x) (* x x)) '(1 2 3))
==> (2 4 9)
```

---

## Let's use it!

```
(def reduce
     (lambda (fn val l)
       (if (nil? l)
           val
         (reduce fn (fn val (car l)) (cdr l)))))

(reduce + 0 '(1 2 3))
==> 6
```

---

## Macro

```
(defmacro defun (name args body)
  (list 'def name
    (list 'lambda args body)))


(defun (x y) (+ x y))
=> (eval '(def name (lambda (x y) (+ x y))))
```

---

## Questions?

```
:trollface:
```

---

## ClojureBridge

```
Saturday 11 July, 9:00 - 17:00

(InstallFest on Friday)

**Coaches Training**

Next Thursday, 19:30

6Wunderkinder (Alexanderplatz)
```
