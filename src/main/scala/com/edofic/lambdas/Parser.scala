package com.edofic.lambdas

import util.parsing.combinator.JavaTokenParsers

/**
 * Created with IntelliJ IDEA.
 * User: andraz
 * Date: 2/2/13
 */
object Parser extends JavaTokenParsers{
  override def skipWhitespace: Boolean = false

  private lazy val number: Parser[Value] =
    decimalNumber ^^ (s => Value(s.toDouble))

  private lazy val string: Parser[Value] =
    stringLiteral ^^ (s => Value(s.substring(1,s.length-1)))

  private lazy val value: Parser[Value] = number | string

  private lazy val id: Parser[Identifier] =
    ident ^^ Identifier.apply

  private lazy val assignement: Parser[Assignment] =
    (ident <~ "=") ~ expr ^^ {
      case name ~ value => Assignment(name,value)
    }

  private def parend[A](p: Parser[A]): Parser[A] =
   "(" ~> p <~ ")"

  private lazy val application: Parser[Application] =
    (id <~ " ") ~ repsep(id | value | parend(application), " ") ^^ {
      case id ~ args => Application(id, args)
    }

  private lazy val lambda: Parser[Lambda] =
    (repsep("\\" ~> id, ".") <~ ".") ~ expr ^^ {
      case anons ~ body => Lambda(anons, body)
    }

  private lazy val expr: Parser[AST] =
    value | assignement | application | id | lambda

  private lazy val line: Parser[Option[AST]] =
    " *".r ~> opt(expr) <~ " *(//.*)?".r

  def apply(input: String): Seq[AST] = {
    val asts = for {
      raw <- input.split('\n')
      ln = raw.trim
      if !ln.isEmpty
    } yield parseAll(line, ln)

    asts flatMap {
      case Success(result, _) => result
      case failure: NoSuccess => scala.sys.error(failure.msg)
    }
  }
}

