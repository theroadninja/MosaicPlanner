package trn.mosaic

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

import scala.swing.{Dimension, Point}

object InstructionExporter {

  def inRange(p: Point, x: Int, y: Int, w: Int, h: Int): Boolean = {
    val x2 = x + w
    val y2 = y + h
    return x <= p.x && p.x < x2 && y <= p.y && p.y < y2
  }
  /**
    * @return true if any part of the plate overlaps with the given rect
    */
  def inRange(p: Plate, x: Int, y: Int, w: Int, h: Int): Boolean = {
    if(p.isCorner){
      throw new RuntimeException("cant handle corner pieces yet")
    }
    p.allPoints().map(inRange(_, x, y, w, h)).reduce(_ || _)
  }
}

class InstructionExporter(size: Dimension, plateModel: PlateListModel) {

  val allPlates = plateModel.getPlates()

  val pageSrcPixelWidth = 32
  val pageSrcPixelHeight = 32
  val zoomScale = 16

  val marginLeftPx = 200
  val marginRightPx = 200
  val marginTopPx = 200
  val marginBottomPx = 200

  def export(basefile: File): Unit = {
    for(x <- 0 until size.width by pageSrcPixelWidth; y <- 0 until size.height by pageSrcPixelHeight){
      exportPage(basefile, x, y)
    }

  }

  private def exportPage(basefile: File, xIndex: Int, yIndex: Int): Unit = {
    val w: Int = pageSrcPixelWidth * zoomScale + marginLeftPx + marginRightPx
    val h: Int = pageSrcPixelHeight * zoomScale + marginTopPx + marginBottomPx
    val img: BufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)

    val newFile: File = new File(basefile.toString + s".${xIndex/pageSrcPixelWidth}.${yIndex/pageSrcPixelHeight}.png")
    //println(newFile)

    val g = img.createGraphics()

    //img.setRGB(255, 255, 255)
    g.setColor(Color.white)
    g.fillRect(0, 0, img.getWidth, img.getHeight)

    val myplates: Seq[Plate] = allPlates.filter{
      InstructionExporter.inRange(_, xIndex, yIndex, pageSrcPixelWidth, pageSrcPixelHeight)
    }


    //g.setColor(Color.black)
    //g.fillRect(0, 0, 50, 50)

    val t = new Translation(-xIndex, -yIndex)

    myplates.foreach { p =>
      println(s"drawing plate ${p}")
      val x = marginLeftPx + (p.x - xIndex) * zoomScale
      val y = marginTopPx + (p.y - yIndex) * zoomScale
      val x2 = x + p.w * zoomScale
      val y2 = y + p.h * zoomScale

      g.setColor(p.color.color())
      g.fillRect(x, y, x2-x, y2-y)

      // outline
      g.setColor(Color.black) //outline
      g.drawRect(x, y, x2-x-1, y2-y-1)
      //g.setColor(Color.white) //outline
      g.setColor(new Color(255, 255, 255, 255/2))
      g.drawRect(x+1, y+1, x2-x-2, y2-y-2)

      //draw the studs
      p.allStuds().map(t.translate(_)).foreach { stud =>
        val radius: Int = zoomScale / 2
        val studx = marginLeftPx + (stud.x * zoomScale) + (radius / 2)
        val study = marginTopPx + (stud.y * zoomScale) + (radius / 2)
        g.setColor(new Color(0, 0, 0, 255/2))
        g.drawOval(studx, study, radius, radius)
        g.setColor(new Color(255, 255, 255, 255/3))
        g.drawOval(studx, study, radius - 1, radius - 1)
      }
    }

    //draw border
    g.setColor(Color.red)
    g.drawRect(marginLeftPx, marginTopPx, w - marginRightPx - marginLeftPx, h - marginBottomPx - marginTopPx)

    ImageIO.write(img, "PNG", newFile)
  }

}
