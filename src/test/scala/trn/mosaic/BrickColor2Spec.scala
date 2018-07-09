package trn.mosaic

import org.scalatest._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.swing.Color

class BrickColor2Spec extends FlatSpec {

  "Whatever" should "serialize test" in {

    val c = new Color(0, 0, 0)
    val t: (Int, Int, Int) = (c.getRed, c.getGreen, c.getBlue)
    assert((0, 0, 0) == t)

    assert(BrickColor2.Black == BrickColor2.KNOWN_COLORS(t))

    val bc = BrickColor2(new Color(0,0,0))

    assert(bc.name == "Black")

  }

  "test" should "json" in {

    implicit val formats = Serialization.formats(NoTypeHints)
    val s = write(BrickColor2.Black)
    val c: BrickColor2 = read[BrickColor2](s)
    assert(c == BrickColor2.Black)
  }

  "test" should "fuzzymatching" in {

    assert(!BrickColor2.White.closeEnough(251, 255, 255))
    assert(BrickColor2.White.closeEnough(251, 255, 254))

    assert(BrickColor2.White == BrickColor2(new Color(255, 255, 254)))
    assert(BrickColor2.White == BrickColor2(new Color(255, 255, 254)))
    assert(BrickColor2.White == BrickColor2(new Color(251, 255, 254)))
    assert(BrickColor2.White == BrickColor2(new Color(255, 251, 254)))
    assert(BrickColor2.White == BrickColor2(new Color(255, 255, 251)))
    assert(BrickColor2.White != BrickColor2(new Color(251, 251, 251)))
  }

}
