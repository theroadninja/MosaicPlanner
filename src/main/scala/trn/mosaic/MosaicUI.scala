package trn.mosaic

import java.awt.FlowLayout
import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import javax.swing.filechooser.FileNameExtensionFilter

import scala.swing.MainFrame
import javax.swing.{BoxLayout, JFileChooser, JFrame}

import scala.swing._
import scala.swing.event.{ButtonClicked, Event}


object MosaicUI {

  var lastDirectory: Option[String] = None

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

  val openPieceLayout: Action = new Action("Open Piece Layout"){
    override def apply(): Unit = {
      println("open piece layout")
    }
  }

  val savePieceLayout: Action = new Action("Save Piece Layout"){
    override def apply(): Unit = {
      println("save piece layout")
    }
  }

  val quitAction: Action = new Action("Exit"){
    override def apply(): Unit = { System.exit(0) }
  }

  def makeButton(label: String, listener: PartialFunction[Event, Unit]): Button = {
    val button = new Button {
      text = label
    }
    button.reactions += listener
    button
  }
}

class MosaicUI extends MainFrame {
  title = "Mosaic App"
  preferredSize = new Dimension(800,600)
  // contents = new Label("Legos!")

  menuBar = new MenuBar {
    contents += new Menu("File")
    {
      //this is how the example code did it:
      //contents += new MenuItem(new Action("Open Picture"){
      //  //override def apply(): Unit = { println("open") }
      //  override def apply = MosaicUI.openPicture
      //})
      contents += new MenuItem(new Action("Open Picture"){
        override def apply(): Unit = {
          MosaicUI.openPicture(MosaicUI.this)()
        }
      })
      contents += new MenuItem(MosaicUI.openPieceLayout)
      contents += new MenuItem(MosaicUI.savePieceLayout)
      contents += new MenuItem(MosaicUI.quitAction)


    }
  }



  val northBar = new BoxPanel(Orientation.Horizontal)
  val zoomInBtn = new Button {
    text = "+"
  }
  zoomInBtn.reactions += {
    case ButtonClicked(_) => zoomIn()
  } // or:  val buttonlistener: PartialFunction[Event, Unit] = { case ButtonClicked(_) ...

  northBar.contents += zoomInBtn
  northBar.contents += MosaicUI.makeButton("-", { case ButtonClicked(_) => zoomOut() })


  val mainImage = new ImagePanelX()
  val mainImageLayout = new FlowPanel {
    contents += mainImage
  }
  mainImage.setParent(mainImageLayout)

  mainImage.setOnClickListener(new PixelListener {
    override def onClick(p: Point): Unit = {
      println(s"pixel clicked: ${p.x}, ${p.y}")
    }
  })
  mainImage.setPlateAddedListener(new PlateAddedListener {
    override def onPlateAdded(p: Plate): Unit = {
      println("plate added! " + p.toString)
    }
  })
  mainImage.setMouseMovedListener(new MouseMovedPixelListener {
    override def onMouseMoved(p: Point): Unit = {
      updateSizeEstimate(p)
    }
  })

  val scrollpane = new ScrollPane(mainImageLayout)
  scrollpane.horizontalScrollBarPolicy = ScrollPane.BarPolicy.Always
  scrollpane.verticalScrollBarPolicy = ScrollPane.BarPolicy.Always

  scrollpane.preferredSize = new Dimension(100, 100)


  // SOUTH BAR
  val southBar = new BoxPanel(Orientation.Horizontal)

  val button = new Button {
    text = "Insert Corner"
  }
  button.reactions += {
    case ButtonClicked(b1) => {
      println("MONKEY")
    }
  }
  southBar.contents += button
  southBar.contents += Swing.HStrut(20)

  val plateSizeLabel = new Label {
    text = ""
  }
  southBar.contents += plateSizeLabel

  val border = new BorderPanel {
    add(northBar, BorderPanel.Position.North)
    add(scrollpane, BorderPanel.Position.Center)
    add(southBar, BorderPanel.Position.South)
  }

  //contents = box
  contents = border


  def onClick(): Unit = {
    println("button clicked")
  }

  def setImage(img: BufferedImage): Unit = {
    mainImage.setImage(img)
  }

  def zoomIn(): Unit = {
    mainImage.zoomIn()
  }
  def zoomOut(): Unit = {
    mainImage.zoomOut()
  }

  def updateSizeEstimate(mouseSrcPixel: Point): Unit = {
    val pointingAt = s"(${mouseSrcPixel.x}, ${mouseSrcPixel.y})"
    plateSizeLabel.text = mainImage.currentFirstClick match {
      case Some(p: Point) => {
        val w = Math.abs(mouseSrcPixel.x - p.x)
        val h = Math.abs(mouseSrcPixel.y - p.y)
        s"${pointingAt} Plate size: ${w}, ${h}"
      }
      case _ => pointingAt
    }
  }

}
