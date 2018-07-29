package trn.mosaic

import java.awt.image.BufferedImage
import java.io.{File, PrintWriter}

import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

import scala.swing.Dialog


object FileController {

  def readfile(file: File): Option[String] = {
    try {
        Some(Util.using(scala.io.Source.fromFile(file)){ f => f.mkString })
    } finally {
      //TODO: actually we should show a message to the user...
      None
    }
  }
}

class FileController {
  var lastDirectory: Option[String] = None
  var currentPicFilename: Option[String] = None

  def getRecentDirectory(): File = {
    val file = new File("recentpath.txt")
    try {
      new File(lastDirectory.getOrElse {
        Util.using(scala.io.Source.fromFile(file)){ f => f.mkString }
      })
    } finally {
      new File(".")
    }
  }

  def setRecentDirectory(path: File): Unit = {
    val file = new File("recentpath.txt")
    lastDirectory = Some(path.toString)

    //scala doesnt have a 'with'/'using' ?
    Util.using(new PrintWriter(file)) { pw =>
      pw.write(path.toString)
    }
  }


  def getFileChooser(title: String): JFileChooser = {
    val chooser = new JFileChooser()
    chooser.setCurrentDirectory(getRecentDirectory())
    chooser.setDialogTitle(title)
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
    return chooser
  }

  def showOpenPictureDialog(): Option[File] = {
    //TODO:  if a picture is already open, check for unsaved layout first ...

    val chooser = getFileChooser("Open Picture File")
    chooser.setAcceptAllFileFilterUsed(true)
    chooser.addChoosableFileFilter(new FileNameExtensionFilter("Supported Image Files", "png", "jpg", "gif"))  // TODO: support more image files
    if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      setRecentDirectory(chooser.getCurrentDirectory)
      val selectedFile = chooser.getSelectedFile
      this.currentPicFilename = Some(selectedFile.getName())
      return Some(selectedFile)

    }else{
      return None
    }
  }

  def showOpenLayoutDialog(): Option[File] = {
    val chooser = getFileChooser("Open Plate Layout")
    chooser.setAcceptAllFileFilterUsed(true)
    if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      setRecentDirectory(chooser.getCurrentDirectory)
      return Some(chooser.getSelectedFile)
    }else{
      return None
    }
  }

  def showSaveLayoutDialog(): Option[File] = {

    val chooser = getFileChooser("Save Plate Layout")
    //chooser.setCurrentDirectory(new java.io.File(lastDirectory.getOrElse(("."))))
    //chooser.setDialogTitle("Save Plate Layout")
    if(lastDirectory.isDefined && currentPicFilename.isDefined){
      val f: String = lastDirectory.get.toString + "/" + currentPicFilename.get + ".json"
      chooser.setSelectedFile(new File(f))
    }
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)

    if(chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
      return None
    }

    // see if the file exists
    val outfile = chooser.getSelectedFile
    return confirmOverwrite(outfile)
    /*
    if(outfile.exists()){
      val choice = Dialog.showConfirmation(null, s"${outfile.getName} exists.  Overwrite?", "Overwrite?")
      if(choice != Dialog.Result.Yes){
        return None
      }
    }

    return Some(outfile)
    */
  }

  def confirmOverwrite(outfile: File): Option[File] = {
    if(outfile.exists()){
      val choice = Dialog.showConfirmation(null, s"${outfile.getName} exists.  Overwrite?", "Overwrite?")
      if(choice != Dialog.Result.Yes){
        return None
      }
    }
    return Some(outfile)
  }

  def showExportInstructionsDialog(): Option[File] = {
    val chooser = getFileChooser("Export Instructions (multiple files)")
    lastDirectory.foreach { f => chooser.setCurrentDirectory(new File(f)) }
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
    if(chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION){
      return None
    }

    val outfile = chooser.getSelectedFile
    return confirmOverwrite(outfile)
  }
}
