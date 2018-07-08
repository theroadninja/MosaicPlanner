package trn.mosaic

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.scalatest.FlatSpec
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

class LayoutFileSpec extends FlatSpec {
  val c1: BrickColor2 = BrickColor2.Black

  "test" should "json" in {
    val plates: Seq[Plate] = Seq(
      Plate(0, 0, 2, 2, c1),
      Plate(0, 1, 2, 2, c1),
      Plate(1, 1, 1, 1, c1))

    val layout: LayoutFile = LayoutFile(plates)
    assert(layout.plateCounts.size == 2)
    implicit val formats = Serialization.formats(NoTypeHints)
    val s: String = write(layout)
    val layout2 = read[LayoutFile](s)
    assert(layout2.plateCounts.size == 2)
    assert(layout2.plateList.size == 3)
  }

}
