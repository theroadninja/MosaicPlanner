package trn.mosaic

object BrickColor extends Enumeration {
  protected case class Val(r: Int, g: Int, b: Int) extends super.Val {
    def matches(color: java.awt.Color): Boolean = {
      r == color.getRed && g == color.getGreen && b == color.getBlue
    }
  }
  implicit def valueToBrickColorVal(x: Value): Val = x.asInstanceOf[Val]
  type BrickColor = Val
  val BrightBlue = Val(13, 105, 171)
  val EarthBlue = Val(32,58,86)
  val DarkStoneGrey = Val(99, 95, 97)
  val MediumStoneGrey = Val(163, 162, 164)


  // TODO: more colors


  // TODO:  implement a nearest system, for guessing the color

  //def fromRgb(rgb: Int): BrickColor = {
  //  BrickColor.values.filter( _)
  //}

  def fromColor(color: java.awt.Color): Option[BrickColor.BrickColor] = {
    BrickColor.values.find(_.matches(color)).map( _.asInstanceOf[Val])
  }
}
