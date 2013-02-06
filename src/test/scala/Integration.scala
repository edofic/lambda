
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

  testProgram("application through native plus",
    """
      |plus 1 2
    """.stripMargin, 3)

  testProgram("two parameter lambda proxy for plus",
    """
      |add=\x.\y.plus x y
      |add 1 2
    """.stripMargin, 3)

  testProgram("partial application",
    """
      |add=\x.\y.plus x y
      |addOne=add 1
      |addOne 2
    """.stripMargin, 3)

  testProgram("lambdas returning lambdas",
    """
      |add=\x.\y.plus x y
      |ax=\x.add x
      |ax 1 2
    """.stripMargin, 3)

  testProgram("comments",
    """
      |//no code
      |1 //a one
    """.stripMargin, 1)
}
