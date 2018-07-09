package trn.mosaic

import org.scalatest._
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.swing.{Color, Point}

class PlateSpec extends FlatSpec {

  val c1 = BrickColor2.Black
  val c2 = BrickColor2.MediumStoneGrey
  val c3 = BrickColor2(new Color(1, 200, 3))
  val c4 = BrickColor2(new Color(1, 200, 3))

  val ps1: Point = new Point(0, 0)

  "test" should "samePart" in {


    val p1 = Plate(0, 0, 2, 2, c1)
    val p2 = Plate(1, 1, 2, 2, c1)
    val corner = Plate(0, 0, 2, 2, c1, 1, 1, true)

    assert(p1.samePart(p2))
    assert(! p1.samePart(corner))
    assert(! p1.samePart(Plate(0, 0, 2, 2, c2)))
    assert(! p1.samePart(Plate(0, 0, 2, 2, c3)))
    assert(! p1.samePart(Plate(0, 0, 2, 3, c1)))

    assert(Plate(0, 0, 5, 5, c3).samePart(Plate(1, 1, 5, 5, c4)))
    assert(Plate(0, 0, 6, 5, c3).samePart(Plate(1, 1, 5, 6, c4)))
  }

  "test" should "samePart rotation" in {

    assert(Plate(ps1, 4, 5, c1).samePart(Plate(ps1, 4, 5, c1)))
    assert(Plate(ps1, 4, 5, c1).samePart(Plate(ps1, 5, 4, c1)))
    assert(Plate(ps1, 5, 4, c1).samePart(Plate(ps1, 4, 5, c1)))
    assert(! Plate(ps1, 5, 4, c1).samePart(Plate(ps1, 5, 5, c1)))
    assert(! Plate(ps1, 5, 4, c1).samePart(Plate(ps1, 4, 4, c1)))
  }

  "test" should "atPosition" in {

    val p1 = Plate(0, 0, 2, 2, c1)
    assert(p1.atPosition(42, 69) == Plate(42, 69, 2, 2, c1))
    assert(p1.atPosition(42, 69).atPosition(5,6) == Plate(5, 6, 2, 2, c1))

  }

  "test" should "asPrefab" in {
    val pc = Plate(5, 5, 2, 2, c1, 1, 1, true)
    val pc2 = Plate(1, 2, 2, 2, c1, 0, 1, true)
    assert(pc != pc2)
    assert(pc.asPrefab() == pc2.asPrefab())

    val p1 = Plate(128, 64, 5, 1, BrickColor2.DarkStoneGrey)
    assert(p1.asPrefab() == Plate(-1, -1, 1, 5, BrickColor2.DarkStoneGrey))
    val p2 = Plate(128, 64, 1, 5, BrickColor2.DarkStoneGrey)
    assert(p2.asPrefab() == Plate(-1, -1, 1, 5, BrickColor2.DarkStoneGrey))

    assert(p1 != p2)
    assert(p1.asPrefab() == p2.asPrefab())
  }

  "test" should "histogram" in {
    val p1a = Plate(0, 0, 2, 2, c1)
    val p1b = Plate(1, 5, 2, 2, c1)

    val p2a = Plate( 0, 0, 2, 2, c2)
    val p2b = Plate( 5, 6, 2, 2, c2)
    val p2c = Plate( 1, 1, 2, 2, c2)

    val histo = Histogram.histo(Seq(p1a, p1b, p2a, p2b, p2c).map(_.asPrefab()))
    assert(histo.size == 2)
    assert(histo(p1a.asPrefab()) == 2)
    assert(histo(p2a.asPrefab()) == 3)
  }

  "test" should "json" in {
    implicit val formats = Serialization.formats(NoTypeHints)
    val p1 = Plate(0, 1, 2, 3, c1)
    val s: String = write(p1)
    val p: Plate = read[Plate](s)
  }

  "test" should "contains" in {

    def assertContains(p: Plate, points: Seq[(Int, Int)], contains:Boolean = true): Unit = {
      for((x,y) <- points) {
        assert(contains == p.contains(x,y))
      }
    }
    def assertNotContains(p: Plate, points: Seq[(Int, Int)]): Unit = assertContains(p, points, false)

    val p1 = Plate(0, 0, 2, 2, c1)
    assertContains(p1, Seq((0,0), (0,1), (1,0), (1,1)))
    assertNotContains(p1, Seq((-1,-1), (2,2), (2,1), (2,0), (0,2), (1,2)))

    val p2 = Plate(6, 1, 2, 3, c1)
    assertContains(p2, Seq((6,1), (6,2), (6,3), (7,1), (7,2), (7,3)))
    assertNotContains(p2, Seq(
      (5, 0), (6, 0), (7, 0), (8, 0),
      (5, 1),                 (8, 1),
      (5, 2),                 (8, 2),
      (5, 3),                 (8, 3),
      (5, 4), (6, 4), (7, 4), (8, 4)
    ))
  }

}
