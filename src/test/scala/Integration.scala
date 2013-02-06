
import org.scalatest.FunSuite

import com.edofic.lambdas._

/**
 * User: andraz
 * Date: 2/5/13
 * Time: 9:28 PM
 */
class Integration extends FunSuite {
  def runtime = new Interpreter with Scope with Natives

  def testProgram(name: String, source: String, result: Any) =
    test(name){
      assert(runtime.run(Parser(source)) === result)
    }

  testProgram("simple number", "1", 1)

  testProgram("simple string", "\"hai\"", "hai")

  testProgram("assigments and id's",
    """
      |meaning=42
      |meaning
    """.stripMargin, 42)

  testProgram("application", "true 1 2", 1)

  testProgram("partial application",
    """
      |one=true 1
      |one 2
    """.stripMargin, 1)

  testProgram("lambdas returning lambdas",
    """
      |t=\x.true x
      |t 1 2
    """.stripMargin, 1)

  testProgram("comments",
    """
      |//no code
      |1 //a one
    """.stripMargin, 1)

  testProgram("true false if",
    """
      |if 1 2 (true false true)
    """.stripMargin, 2)

  testProgram("native plus", "plus 1 2 3", 6)

  testProgram("two parameter lambda proxy for plus",
    """
      |add=\x.\y.plus x y
      |add 1 2
    """.stripMargin, 3)

  testProgram("native minus", "minus 6 2 3", 1)

  testProgram("native eq", "plus (eq 1 1 1 0) (eq 1 2 0 1)", 2)

  testProgram("native ne", "plus (ne 1 1 1 0) (ne 1 2 0 1)", 0)

  testProgram("native lt", "plus (lt 1 2 1 0) (lt 2 1 0 1)", 2)

  testProgram("native gt", "plus (gt 1 2 1 0) (gt 2 1 0 1)", 0)

  testProgram("native rep",
    """
      |addOne=\x.plus 1 x
      |rep 20 addOne 0
    """.stripMargin, 20)

  testProgram("recursion",
    """
      |count=\n.eq n 0 0 (count (minus n 1))
      |count 20
    """.stripMargin, 0)

  testProgram("fibonacci numbers",
    """
      |fibonacci=\n.lt n 2 1 (plus (fibonacci (minus n 1)) (fibonacci (minus n 2)))
      |fibonacci 10
    """.stripMargin, 89)
}
