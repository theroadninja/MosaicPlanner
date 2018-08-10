package trn.mosaic

import scala.swing.Point

class Translation(dx: Int, dy: Int) {

  def translate(p: Point): Point = {
    new Point(p.x + dx, p.y + dy)
  }

}
