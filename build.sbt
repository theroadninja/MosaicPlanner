name := "LegoMosaicScala"

version := "0.1"

scalaVersion := "2.12.6"

//libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10+"
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.0.0-M2"

libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"

//I'd rather use the play framework, but dont want to deal with setting it up
//libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.0-M4"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.6.0-M4"


// then, for intellij:  refresh project (View -> Tool Windows -> sbt, "Refresh all sbt projects"
