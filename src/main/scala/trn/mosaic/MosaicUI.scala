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


  val quitAction: Action = new Action("Exit"){
    override def apply(): Unit = { System.exit(0) }
  }

  // val exportInstructionsAction = new Action("Export Instructions"){
  //   override def apply(): Unit = {
  //     // save dialog, write instructions to PNG files in sections of 32
  //     // TODO
  //   }
  // }

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

  val controller = new MosaicUIController(this)
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
          controller.openPicture(MosaicUI.this)()
        }
      })
      contents += new MenuItem(controller.openPieceLayout)
      contents += new MenuItem(controller.savePieceLayout)
      contents += new MenuItem(controller.exportInstructions)
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
  northBar.contents += MosaicUI.makeButton("Delete", { case ButtonClicked(_) => controller.deleteSelected() })


  val mainImage = new ImagePanelX()
  val mainImageLayout = new FlowPanel {
    contents += mainImage
  }
  mainImage.setParent(mainImageLayout)

  mainImage.setOnClickListener(new PixelListener {
    override def onClick(p: Point): Unit = {
      //println(s"pixel clicked: ${p.x}, ${p.y}")
    }
  })
  mainImage.setPlateAddedListener(new PlateAddedListener {
    override def onPlateAdded(p: Plate): Unit = {
      println("plate added! " + p.toString)
    }
  })

  /*
  mainImage.setMouseMovedListener(new MouseMovedPixelListener {
    override def onMouseMoved(p: Point, color: BrickColor2): Unit = {
      updateSizeEstimate(p)
    }
  })*/

  mainImage.setMouseMovedListener(controller.mouseOverStatus)

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


  val messageLabel = new Label { text = "" }
  southBar.contents += messageLabel


  southBar.contents += Swing.HGlue

  val plateSizeLabel = new Label {
    text = ""
  }
  southBar.contents += plateSizeLabel
  southBar.contents += Swing.HStrut(40)
  val mouseOverColorLabel = new Label {
    text = ""
    minimumSize = new Dimension(160, 0)
    preferredSize = minimumSize
  }
  southBar.contents += mouseOverColorLabel


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

  /*
  def updateSizeEstimate(mouseSrcPixel: Point): Unit = {
    val pointingAt = s"(${mouseSrcPixel.x}, ${mouseSrcPixel.y})"
    plateSizeLabel.text = mainImage.currentFirstClick match {
      case Some(p: Point) => {
        val w = Math.abs(mouseSrcPixel.x - p.x) + 1
        val h = Math.abs(mouseSrcPixel.y - p.y) + 1
        s"${pointingAt} Plate size: ${w}, ${h}"
      }
      case _ => pointingAt
    }
  } */

}
