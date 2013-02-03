package com.edofic.lambdas

import util.{Try,Success,Failure}

/**
 * User: andraz
 * Date: 2/3/13
 * Time: 12:18 PM
 */
object Repl {
  val helpText = """
  |  Repl is VERY whitespace sensitive. 
  |  
  |  You get the value from the last expressions printed back to screen.
  |  For debugging reasons you get the AST of the function when defining
  |  a lambda.  
  |
  |  Supported syntax: 
  |    * strings and numbers(ints and floats)
  |    * space delimited function aplication: f a b
  |    * assignments: a=1
  |    * lambda expressions: \x.\y.sum x y
  |""".stripMargin

  def main(args: Array[String]) = {
    val interpreter = new Interpreter with Scope with Natives
    println("\nLambda REPL 0.1")
    println("""  "quit" to quit and "help" to get help""" + "\n")
    def step{
      readLine("lambda> ") match {
        case "quit" => return
        case "help" => println(helpText)    
        case "" => println()
        case line =>
          Try(interpreter run Parser(line)) match {
            case Success(result) => println(result)
            case Failure(err) => println(s"ERROR\n$err\n")
          }
      }
      step
    }
    step
  }
}
