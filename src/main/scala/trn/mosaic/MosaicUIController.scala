package trn.mosaic

import java.awt.image.BufferedImage
import java.io.{File, PrintWriter}

import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read
import org.json4s.jackson.Serialization._

import scala.swing.Dialog.{Message, Options}
import scala.swing.{Action, Dialog}

class MosaicUIController(ui: MosaicUI) {

  val fileController = new FileController()

  def openPicture(ui: MosaicUI)(): Unit = {
    val selectedFile = fileController.showOpenPictureDialog()
    selectedFile.foreach{ f =>
      val img:BufferedImage = ImageIO.read(f)  // TODO: this should not be on UI thread
      ui.setImage(img)
    }
  }

  val openPieceLayout: Action = new Action("Open Piece Layout"){
    override def apply(): Unit = {
      val selectedFile = fileController.showOpenLayoutDialog()
      selectedFile.map(FileController.readfile(_)).foreach { _.foreach { json =>
        // deserialize from json
        implicit val formats = Serialization.formats(NoTypeHints)
        val f: LayoutFile = read[LayoutFile](json)
        ui.mainImage.setPlates(f.plateList)
      }}

    }
  }

  val savePieceLayout: Action = new Action("Save Piece Layout"){
    override def apply(): Unit = {
      val selectedFile = fileController.showSaveLayoutDialog()
      selectedFile.foreach { outfile =>
        implicit val formats = Serialization.formats(NoTypeHints)
        val plates = ui.mainImage.plates
        val layout = LayoutFile(plates)
        Util.using(new PrintWriter(outfile)) { pw =>
          pw.write(write(layout))
        }
      } // option foreach

    }
  }

}
