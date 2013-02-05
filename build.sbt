import AssemblyKeys._

scalaVersion := "2.10.0"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

initialCommands in console := "import com.edofic.lambdas._"

assemblySettings

assembleArtifact in packageScala := false

jarName in assembly := "lambda-0.1-SNAPSHOT.jar"

