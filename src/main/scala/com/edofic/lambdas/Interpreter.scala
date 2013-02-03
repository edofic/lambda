package com.edofic.lambdas

/**
 * User: andraz
 * Date: 2/3/13
 * Time: 10:09 AM
 */
class Interpreter {
  val objects = collection.mutable.Map[String,Any]()

  trait Func extends (Seq[Any] => Any) {
    def ast: AST
    override def toString(): String = "func: " + ast.toString
  }
  def func(as: AST)(f: Seq[Any] => Any) = new Func {
    def apply(seq: Seq[Any]): Any = f(seq)

    def ast: AST = as
  }

  objects("print") = func(Value("native"))(s => println(s mkString " "))

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
