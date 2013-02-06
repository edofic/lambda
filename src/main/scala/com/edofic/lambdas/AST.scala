package com.edofic.lambdas

sealed trait AST

class Value(raw: => Any) extends AST {
  lazy val value = raw
}

object Value{
  def apply(raw: => Any) = new Value(raw)
  def unapply(v: Value): Option[Any] = Some(v.value)
}

case class Identifier(id: String) extends AST

case class Assignment(id: String, expression: AST) extends AST

case class Lambda(anons: Seq[Identifier], body: AST) extends AST

case class Application(function: AST, args: Seq[AST]) extends AST


object AST{
  private val astID: PartialFunction[AST,AST] = {
    case a => a
  }

  def modify(ast: AST)(partial: PartialFunction[AST,AST]): AST = {
    val par = partial orElse astID
    def mod(ast: AST): AST = {
      par(ast match {
        case Assignment(id, exp) => Assignment(id, mod(exp))
        case Lambda(anons, body) => Lambda(anons,mod(body))
        case Application(func, args) => Application(mod(func), args map mod)
        case other => other
      })
    }
    mod(ast)
  }

  def prettyPrint(ast: AST): String = ast match {
    case Value(value: String) => "\""+value+"\""
    case Value(value) => value.toString
    case Identifier(id) => id
    case Assignment(id, exp) => s"$id=${prettyPrint(exp)}"
    case Application(f, args) => prettyPrint(f) + " " + args.map{
      case a: Application => "("+prettyPrint(a)+")"
      case other => prettyPrint(other)
    }.mkString(" ")
    case Lambda(anons, body) => anons.map(a=> "\\"+prettyPrint(a)).mkString(".") + "." + prettyPrint(body)
  }
}

