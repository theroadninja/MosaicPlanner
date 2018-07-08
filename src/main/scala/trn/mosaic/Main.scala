package trn.mosaic

import javax.swing.BoxLayout

import scala.swing._
import scala.swing.event.ButtonClicked


// to import scala swing:  libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10+"
// actually that doesnt work; its:
// libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.0.0-M2"
// then, for intellij:  refresh project (View -> Tool Windows -> sbt, "Rrefresh all sbt projects"

// example swing code:
// https://lampsvn.epfl.ch/trac/scala/browser/scala/trunk/src/swing/scala/swing/test


//def onClick(): Unit = {
//  println("test")
//}

class UI extends MainFrame {
}

object Main {
  def main(args: Array[String]): Unit = {
    val ui = new MosaicUI
    ui.visible = true
    println("End of Main")
  }
}

