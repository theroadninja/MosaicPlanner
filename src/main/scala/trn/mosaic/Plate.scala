package trn.mosaic

import java.awt.Color

import trn.mosaic.BrickColor.BrickColor

import scala.swing.Point



object Plate {
  def apply(p1: Point, p2: Point, rgb: Int): Plate = {

    // TODO:  how do we handle corners??
    val x = Math.min(p1.x, p2.x)
    val y = Math.min(p1.y, p2.y)
    val x2 = Math.max(p1.x, p2.x)
    val y2 = Math.max(p1.y, p2.y)

    new Plate(x, y, x2 - x + 1, y2 - y + 1, BrickColor.fromColor(new Color(rgb)), isCorner = false)
  }
}
case class Plate(x: Int, y: Int, w: Int, h: Int, color: Option[BrickColor], isCorner: Boolean) {
  // should x and y be part of it?

}
