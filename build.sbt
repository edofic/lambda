import AssemblyKeys._

scalaVersion := "2.10.0"

initialCommands in console := "import com.edofic.lambdas._"

assemblySettings

assembleArtifact in packageScala := false

jarName in assembly := "lambda-0.1-SNAPSHOT.jar"

