Lambda REPL
======

This is a simple lambda calculus REPL I created as an excercise. Theoreticaly it's Touring complete but there's not much you can to with it. And it's very sensitive to whitespace. Basially you can't add any(except newlines). 

You get the classic Read-Eval-Print-Loop. For the sake of easier debugging the "print" part for lambdas pretty prints out the AST. For conveniance you can fetch the assembled jar [lambda-0.1-SNAPSHOT.jar](http://edofic.github.com/lambda/lambda-0.1-SNAPSHOT.jar) (run with scala)

Supported syntax
====
* values as in strings `hello world` and numbers `1` or `2.3`
* java style identifiers `FooBar`
* assignments `meaning=42`
* function application, arguments separated by single space `f a b` is function f applied on arguments a and b. Also suports parenthesis for nested applications `f (a b)` this invokes a with b as an argument and passes the result to f
* lambda expressions `\p.\q.p p q` this creates a function that takes p and q and then invokes p with p and q as arguments(boolean and). Pretty standard notation.
