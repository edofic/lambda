package com.edofic.lambdas

import Interpreter._

/**
 * User: andraz
 * Date: 2/3/13
 * Time: 10:09 AM
 */
trait Interpreter{
  this: Scope =>

  def mkLambda(anons: Seq[Identifier], body: AST): Func =
    func(Lambda(anons, body)){
      seq => {
        val values = seq map {
          case a: AST => a
          case other => Value(other)
        }
        val modifier = (anons zip values) map {
          case (id, value) => {
            case `id` => value
          }: PartialFunction[AST, AST]
        } reduce (_ orElse _)
        val newBody = AST.modify(body)(modifier)
        seq.length - anons.length match {
          case 0 =>
            run(newBody)
          case x if x<0 => //too few args -> partial application
            mkLambda(anons.drop(seq.length), newBody)
          case _ => //too much args. pass 'em on
            run(Application(newBody, seq.drop(anons.length) map Value.apply))
        }
      }
    }

  def run(expr: AST): Any = expr match {
    case Value(value) => value

    case Identifier(id) => objects(id)

    case Assignment(id, ex) => objects(id) = run(ex)

    case Lambda(anons, body) => mkLambda(anons, body)

    case Application(function, args) => run(function) match {
      case f: Func => f(args map run)
      case other => scala.sys.error(s"runtime error. expected function, got $other")
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

trait Scope{
  this: Interpreter =>

  val objects = collection.mutable.Map[String,Any]()

  private def mkFunc(name: String, body: String) =
    objects(name) = run(Parser(body))

  mkFunc("true", """\p.\q.p""")
  mkFunc("false", """\p.\q.q""")
  mkFunc("if", """\p.\q.\b.b p q""")
}

trait Natives extends Scope{
  this: Interpreter =>

  private def mkNativeFunc(name: String)(f: Seq[Any] => Any) =
    objects(name) = func(Value("native"))(f)

  private def mkFunc(name: String)(lambda: Lambda) =
    objects(name) = func(lambda)(mkLambda(lambda.anons, lambda.body))

  private def mkProxied(name: String, nArgs: Int)(f: Seq[Any] => Any) = {
    val args = (0 until nArgs) map (i => Identifier("a"+i))
    val unsafe = name + "_unsafe"
    mkNativeFunc(unsafe)(f)
    mkFunc(name)(Lambda(args, Application(Identifier(unsafe),  args)))
  }

  objects("number") = "number"
  objects("string") = "string"
  objects("run") = ""

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

  mkProxied("eq", 2)(s=>
    if(s(0)==s(1)) objects("true") else objects("false")
  )

  mkNativeFunc("seq")(s => s.last)

  mkProxied("rep", 1)(s => {
    val outer = Identifier("f")
    val inner = Identifier("g")
    def nestedApplication(n: Int, body: AST): AST =
      if(n==0)
        body
      else
        nestedApplication(n-1, Application(outer, Seq(body)))

    if(s.length!=1 && s(0).isInstanceOf[Double])
      sys.error("need exactly one argument and it should be a number")
    else
      mkLambda(Seq(outer, inner), nestedApplication(s(0).asInstanceOf[Double].toInt, inner))
  })
}
