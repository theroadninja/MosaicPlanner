package trn.mosaic

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.swing.Action

class MosaicUIController(ui: MosaicUI) {
  var lastDirectory: Option[String] = None
  var currentFilename: Option[String] = None

  def openPicture(ui: MosaicUI)(): Unit = {

    //TODO:  if a picture is already open, check for unsaved layout first ...

    println("open picture")
    val chooser = new JFileChooser()
    //chooser.setCurrentDirectory(new java.io.File("."))  // TODO: remember last folder?
    chooser.setCurrentDirectory(new java.io.File(lastDirectory.getOrElse(("."))))
    chooser.setDialogTitle("Open Picture File")
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
    chooser.setAcceptAllFileFilterUsed(true)
    chooser.addChoosableFileFilter(new FileNameExtensionFilter("Supported Image Files", "png", "jpg", "gif"))  // TODO: support more image files
    if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      lastDirectory = Some(chooser.getCurrentDirectory.toString)
      //println("directory: " + chooser.getCurrentDirectory)
      //note:  the selected file has the full path!
      val selectedFile = chooser.getSelectedFile
      this.currentFilename = Some(selectedFile.getName())
      println("file: " + selectedFile)

      //TODO: this should be in a background thread!
      //var img:BufferedImage = null
      //try {
      //  img = javax.imageio.ImageIO.read()
      //}

      //NOTE: http://otfried.org/scala/image.html
      val img:BufferedImage = ImageIO.read(selectedFile)
      ui.setImage(img)
    }
  }


  val savePieceLayout: Action = new Action("Save Piece Layout"){
    override def apply(): Unit = {
      val chooser = new JFileChooser()
      chooser.setCurrentDirectory(new java.io.File(lastDirectory.getOrElse(("."))))
      chooser.setDialogTitle("Save Plate Layout")
      if(lastDirectory.isDefined && currentFilename.isDefined){
        val f: String = lastDirectory.get.toString + "/" + currentFilename.get + ".json"
        chooser.setSelectedFile(new File(f))
      }
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
      if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        //
        println(s"plates size: ${ui.mainImage.plates.size}")
        val plates = ui.mainImage.plates
        val layout = LayoutFile(plates)
        implicit val formats = Serialization.formats(NoTypeHints)
        println(write(layout))
        println(write(plates(0)))
        println(write(plates))
        println("layout: " + write(layout))
        /*
        println("plate color: " + plates(0).color)
        val p: Plate = new Plate(0, 0, 1, 1, BrickColor2.BrightBlue, -1, -1, false)
        val j: String = write(p)
        println(j)
        val p2: Plate = read[Plate](j)
        println(p2.color)
        println(write(p2))
        */
      }
    }
  }

}
