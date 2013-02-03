package com.edofic.lambdas

import Interpreter._

/**
 * User: andraz
 * Date: 2/3/13
 * Time: 10:09 AM
 */
class Interpreter{
  val objects = collection.mutable.Map[String,Any]()
  objects.++=(Natives.nativeObjects)

  def mkLambda(anons: Seq[Identifier], body: AST): Func =
    func(Lambda(anons, body)){
      seq => {
        val partials = (anons zip seq) map {
          case (id, value: AST) => {
            case `id` => value
          }: PartialFunction[AST, AST]
          case (id, value) => {
            case `id` => Value(value)
          }: PartialFunction[AST, AST]
        } reduce (_ orElse _)
        val newBody = AST.modify(body)(partials)
        if(seq.length==anons.length)
          run(newBody)
        else
          mkLambda(anons.drop(seq.length), newBody)
      }
    }

  def run(expr: AST): Any = expr match {
    case Value(value) => value

    case Identifier(id) => objects(id)

    case Assignment(id, ex) => objects(id) = run(ex)

    case Lambda(anons, body) => mkLambda(anons, body)

    case Application(function, args) => run(function) match {
      case f: Func => f(args map run)
      case other => scala.sys.error("runtime error. expected function, got $other")
    }
  }

  def run(exprs: Seq[AST]): Any = exprs.map(run).last
}

object Interpreter{
  trait Func extends (Seq[Any] => Any) {
    def ast: AST
    override def toString(): String = "func: " + AST.prettyPrint(ast)
  }
  def func(as: AST)(f: Seq[Any] => Any) = new Func {
    def apply(seq: Seq[Any]): Any = f(seq)

    def ast: AST = as
  }
}

object Natives{
  val nativeObjects = collection.mutable.Map[String,Any]()

  private def mkNativeFunc(name: String)(f: Seq[Any] => Any) =
    nativeObjects(name) = func(Value("native"))(f)

  nativeObjects("number") = "number"
  nativeObjects("string") = "string"
  nativeObjects("run") = ""

  mkNativeFunc("print")(s => println(s mkString " "))

  mkNativeFunc("read"){s =>
    if(s.length != 1) sys.error("wrong number of arguments")
    else {
      s(0) match {
        case "string" => readLine()
        case "number" => readLine().toDouble
        case other => sys.error(s"unknown type: $other")
      }
    }
  }

  mkNativeFunc("plus")(s =>
    if(s.forall(_.isInstanceOf[Double]))
      s.asInstanceOf[Seq[Double]] reduce {_+_}
    else
      s reduce {_.toString + _.toString}
  )

  mkNativeFunc("minus")(s =>
    if(s.forall(_.isInstanceOf[Double]))
      s.asInstanceOf[Seq[Double]] reduce {_-_}
    else
      scala.sys.error("not all arguments are numbers")
  )

  mkNativeFunc("seq")(s => s.last)
}
