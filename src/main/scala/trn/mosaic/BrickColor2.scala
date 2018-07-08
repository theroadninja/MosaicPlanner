package trn.mosaic

import scala.swing.Color


object BrickColor2 {

  val Black = new BrickColor2("Black", 0, 0, 0)
  val BrightBlue = new BrickColor2("BrightBlue", 13, 105, 171)
  val EarthBlue = new BrickColor2("EarthBlue", 32,58,86)
  val DarkStoneGrey = new BrickColor2("DarkStoneGrey", 99, 95, 97)
  val MediumStoneGrey = new BrickColor2("MediumStoneGrey", 163, 162, 164)

  val VALUES = Seq(Black, BrightBlue, EarthBlue, DarkStoneGrey, MediumStoneGrey)

  val KNOWN_COLORS: Map[(Int, Int, Int), BrickColor2] = VALUES.map { c => c.key() -> c }.toMap
  //val KNOWN_COLORS = Map[Color, BrickColor2](
  //  BrightBlue.color() -> BrightBlue,
  //  EarthBlue.color() ->
  //)

  def apply(color: Color): BrickColor2 = {
    KNOWN_COLORS.getOrElse(
      (color.getRed, color.getGreen, color.getBlue), //Color object doesnt has correctly?
      new BrickColor2("", color.getRed, color.getGreen, color.getBlue))
  }

  def apply(rgb: Int): BrickColor2 = apply(new Color(rgb))

}

/**
  * apparenty scala enums dont serializes well
  */
case class BrickColor2(name: String, x: Int, y: Int, z: Int) {

  def color(): Color = {
    new Color(x, y, z)
  }

  def key(): (Int, Int, Int) = {
    (x, y, z)
  }
}
