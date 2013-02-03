Lambda REPL
======

This is a simple lambda calculus REPL I created as an excercise. Theoreticaly it's Touring complete but not very practical. And it's very sensitive to whitespace. Basially you can't add any(except newlines). 

You get the classic Read-Eval-Print-Loop. For the sake of easier debugging the "print" part for lambdas pretty prints out the AST. For conveniance you can fetch the assembled jar [lambda-0.1-SNAPSHOT.jar](http://edofic.github.com/lambda/lambda-0.1-SNAPSHOT.jar) (run with scala). Or `git clone` and `sbt run`

Supported syntax
====
* values as in strings `hello world` and numbers `1` or `2.3`
* java style identifiers `FooBar`
* assignments `meaning=42`
* function application, arguments separated by single space `f a b` is function f applied on arguments a and b. Also suports parenthesis for nested applications `f (a b)` this invokes a with b as an argument and passes the result to f
* lambda expressions `\p.\q.p p q` this creates a function that takes p and q and then invokes p with p and q as arguments(boolean and). Pretty standard notation.

Native extensions
====
There are some native functions hardcoded into the interpreter. Think of it as standard library.
* `plus` sums up numbers or concatenates strings `plus 1 2 3` or `plus "hello " "world"`
* `minus` subtracts numbers `minus 10 2 3`
* `print` prints all arguments to standard output
* `read` reads from standard input. `read string` to read a string and `read number` for numbers
* `seq` a sequential combinator. Varargs function that always returns the last argument. Also provided dummy constant `run`. See below code sample.

    greet=\x.seq (print "who are you?") (print "hello" (read string))
    greet run

And a transcript from running this
    
    lambda> greet run
    who are you?
    andraz
    hello andraz


