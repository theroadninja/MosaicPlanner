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
import scala.swing.{Action, Dialog, Point}

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
        //val plates = ui.mainImage.plates
        //val layout = LayoutFile(plates)
        val layout = ui.mainImage.plateModel.getLayoutFile()
        Util.using(new PrintWriter(outfile)) { pw =>
          pw.write(write(layout))
        }
      } // option foreach

    }
  }

  val exportInstructions: Action = new Action("Export Instructions"){
    override def apply(): Unit = {
      ui.mainImage.getSourceImageSize() match {
        case None => Unit
        case Some(size) => {
          //
          fileController.showExportInstructionsDialog() match {
          //fileController.showOpenLayoutDialog() match {
            case None => Unit
            case Some(selectedFile) => {
              val exporter = new InstructionExporter(size, ui.mainImage.plateModel)
              exporter.export(selectedFile)
            }
          }


        }
      }
      // TODO

      //selectedFile.map(FileController.readfile(_)).foreach { _.foreach { json =>
      //  // deserialize from json
      //  //implicit val formats = Serialization.formats(NoTypeHints)
      //  //val f: LayoutFile = read[LayoutFile](json)
      //  //ui.mainImage.setPlates(f.plateList)
      //}}
    }
  }

  def deleteSelected(): Unit = {
    // note: this is only for the delete button.  The image panel handles delete key presses on its own
    if(! ui.mainImage.plateModel.deleteSelection()){
      ui.messageLabel.text = "No Plate Selected"
    }
  }

  /**
    * update the status label with helpful info about whatever the mouse is moving over in the image panel
    * @param mouseSrcPixel source point
    * @param color color of source pixel under mouse
    */
  def mouseOverStatus(mouseSrcPixel: Point, color: BrickColor2): Unit = {
    val pointingAt = s"(${mouseSrcPixel.x}, ${mouseSrcPixel.y})"
    val pointMessage = ui.mainImage.currentFirstClick match {
      case Some(p: Point) => {
        val w = Math.abs(mouseSrcPixel.x - p.x) + 1
        val h = Math.abs(mouseSrcPixel.y - p.y) + 1
        s"${pointingAt} Plate size: ${w}, ${h}"
      }
      case _ => pointingAt
    }
    val colorMessage = color.name match {
      case "" => color.color().toString
      case s => s
    }

    //ui.plateSizeLabel.text = s"${pointMessage} - ${colorMessage}"
    ui.plateSizeLabel.text = pointMessage
    ui.mouseOverColorLabel.text = colorMessage
  }

}
