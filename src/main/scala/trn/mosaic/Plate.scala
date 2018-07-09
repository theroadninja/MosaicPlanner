package trn.mosaic

import org.json4s.{CustomSerializer, DefaultFormats, Formats, MappingException, TypeInfo}
import org.json4s.JsonAST.{JField, JInt, JObject, JValue}
import org.json4s.JsonDSL._

import scala.swing.Point

object Plate {
  def apply(p1: Point, p2: Point, rgb: Int): Plate = {

    // TODO:  how do we handle corners??
    val x = Math.min(p1.x, p2.x)
    val y = Math.min(p1.y, p2.y)
    val x2 = Math.max(p1.x, p2.x)
    val y2 = Math.max(p1.y, p2.y)

    new Plate(x, y, x2 - x + 1, y2 - y + 1, BrickColor2(rgb), -1, -1, isCorner = false)
  }

  def apply(p1: Point, w: Int, h: Int, color: BrickColor2): Plate = apply(p1.x, p1.y, w, h, color)

  def apply(x: Int, y: Int, w: Int, h: Int, color: BrickColor2): Plate =
    new Plate(x, y, w, h, color, -1, -1, false)

}


case class Plate(
  x: Int, // position
  y: Int, // position
  w: Int,
  h: Int,
  color: BrickColor2,
  cornerx: Int,  // this is the MISSING stud (i.e. rotation)
                // note: it can be -1 when isCorner is true:  means unknown rotation
  cornery: Int,  // this is the MISSING stud
  isCorner: Boolean) {

  /**
    *
    * @param p2
    * @return true if this and the other plate are made from the same lego piece
    */
  def samePart(p2: Plate): Boolean = {
    if(isCorner != p2.isCorner){
      return false
    }
    if(color != p2.color){
      return false
    }
    return (w == p2.w && h == p2.h) || (w == p2.h && h == p2.w)
  }

  def atPosition(xx: Int, yy: Int): Plate = {
    new Plate(xx, yy, w, h, color, cornerx, cornery, isCorner)
  }

  /**
    * @return plate with position and rotation info removed (and w,h sorted)
    */
  def asPrefab(): Plate = {
    new Plate(-1, -1, Math.min(w,h), Math.max(w,h), color, -1, -1, isCorner)
  }

  def contains(px: Int, py: Int): Boolean = {
    return x <= px && y <= py && px < (x + w) && py < (y + h)
  }

}

/*
class PlateSerializer extends CustomSerializer[Plate](format => ({
  //implicit val formats = DefaultFormats
  case JObject(
      JField("x", JInt(x))
        :: JField("y", JInt(y))
        :: JField("w", JInt(w))
        :: JField("h", JInt(h))
        :: JField("color", JObject())
    :: _) => Plate(0, 0, 0, 0, BrickColor2.Black)
  },
  {
    case p: Plate => JObject(
      JField("x", JInt(p.x))
    )
}))
*/

