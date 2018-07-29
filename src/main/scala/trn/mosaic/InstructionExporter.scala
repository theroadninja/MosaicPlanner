package trn.mosaic

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

import scala.swing.Dimension

class InstructionExporter(size: Dimension, plateModel: PlateListModel) {

  val pageSrcPixelWidth = 32
  val pageSrcPixelHeight = 32
  val zoomScale = 8

  val marginLeftPx = 100
  val marginRightPx = 100
  val marginTopPx = 100
  val marginBottomPx = 100

  def export(basefile: File): Unit = {

  }

  private def exportPage(basefile: File, xIndex: Int, yIndex: Int): Unit = {
    val w: Int = pageSrcPixelWidth * zoomScale + marginLeftPx + marginRightPx
    val h: Int = pageSrcPixelHeight * zoomScale + marginTopPx + marginBottomPx
    val img: BufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)

    // img.getGraphics
    // ImageIO.write(img, "PNG", new File("test.png"))
  }

}
