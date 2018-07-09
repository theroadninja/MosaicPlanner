package trn.mosaic

import java.awt.Color
import java.awt.image.BufferedImage

import javax.swing.SwingUtilities

import scala.swing.event.{Key, KeyReleased, MouseClicked, MouseMoved}
import scala.swing.{Dimension, Graphics2D, Panel, Point}




object ImagePanelX {
  val MIN_ZOOM: Int = 8
  val MAX_ZOOM: Int = 16 // TODO: use a step function ...

  val EMPTY_SIZE: Dimension = new Dimension(100, 100)

  /** key of original image in the image map */
  val ORIGINAL_IMAGE = 1
}


/**
  * Listener for clicking on pixels
  */
trait PixelListener {
  /**
    * @param p: point of x,y coordinates of the pixel in the original picture
    */
  def onClick(p: Point)
}

trait MouseMovedPixelListener {
  def onMouseMoved(p: Point, color: BrickColor2)
}

trait PlateAddedListener { // TODO: get rid of this
  def onPlateAdded(p: Plate)
}

/**
  * Because scala swing doesnt have an image view >:(
  *
  * This is not a generic image view though, has zoom, grid and coordinate translation built in.
  */
class ImagePanelX() extends Panel {


  // TODO:  the value type should not be an option, what was i thinking??
  var scaledImages: Map[Int, Option[BufferedImage]] = Map[Int, Option[BufferedImage]]()

  var zoomLevel: Int = ImagePanelX.MIN_ZOOM

  var parent: Panel = null

  var onClickListener: Option[PixelListener] = None

  var mouseMovedListener: Option[MouseMovedPixelListener] = None


  var plateAddedListener: Option[PlateAddedListener] = None

  var srcPixelUnderMouse: Option[(Point, BrickColor2)] = None

  /**
    * location of the first corner a user clicks, in source coordinate space
    */
  var currentFirstClick: Option[Point] = None

  var plateModel: PlateListModel = new PlateListModel()
  plateModel.addListener(new PlateListModel.ChangedListener {
    override def onPlatesChanged(added: Option[Seq[Plate]], removed: Option[Seq[Plate]]): Unit = refresh()
  })

  def setPlates(p: Seq[Plate]): Unit = {
    plateModel.setPlates(p)
  }

  def setParent(p: Panel): Unit ={
    parent = p
  }

  def setOnClickListener(listener: Option[PixelListener]): Unit = {
    onClickListener = listener
  }
  def setOnClickListener(listener: PixelListener): Unit = setOnClickListener(Some(listener))

  def setMouseMovedListener(listener: MouseMovedPixelListener): Unit = {
    mouseMovedListener = Some(listener)
  }
  def setPlateAddedListener(listener: PlateAddedListener): Unit = {
    plateAddedListener = Some(listener)
  }


  def setImage(i: BufferedImage): Unit = setImage(Some(i))
  def setImage(img: Option[BufferedImage]): Unit ={
    //img = i

    img match {
      case Some(i) => {
        var m: Map[Int, Option[BufferedImage]] = Map[Int, Option[BufferedImage]](ImagePanelX.ORIGINAL_IMAGE -> Some(i))
        for(size: Int <- ImagePanelX.MIN_ZOOM to ImagePanelX.MAX_ZOOM){
          val scaledImg = new BufferedImage(i.getWidth * size, i.getHeight * size, BufferedImage.TYPE_INT_ARGB)
          val g = scaledImg.getGraphics
          for(x <- 0 until i.getWidth; y <- 0 until i.getHeight){
            g.setColor(new Color(i.getRGB(x,y)))
            g.fillRect(x * size, y * size, size, size)
            g.setColor(new Color(30, 30, 30))
            g.drawRect(x * size, y * size, size, size)
          }
          m = m + (size -> Some(scaledImg))
        }
        scaledImages = m
      }
      case _ => {
        scaledImages = Map[Int, Option[BufferedImage]]()
      }
    }
    this.refresh()
    // TODO: want to do one image imediately, process the rest in the background
  }

  def zoomIn(): Unit = {
    if(zoomLevel < ImagePanelX.MAX_ZOOM){
      zoomLevel += 1
      this.refresh()
    }
  }

  def zoomOut(): Unit = {
    if(zoomLevel > ImagePanelX.MIN_ZOOM){
      zoomLevel -= 1
      this.refresh()
    }
  }

  override def minimumSize: Dimension = {
    val img = scaledImages.getOrElse(zoomLevel, None)
    println("GET MIN SIZE CALLED")
    img match {
      case Some(i) => {
        new Dimension(i.getWidth, i.getHeight)
      }
      case _ => { this.size }
    }
  }

  override def maximumSize = minimumSize
  override def preferredSize = minimumSize

  this.listenTo(mouse.clicks)
  this.listenTo(mouse.moves)
  this.listenTo(keys)
  this.reactions += {
    case evt @ MouseClicked(source, point, modifiers, clicks, triggersPopup) => {
      //peer.setRequestFocusEnabled(true) // trying to get key events
      this.requestFocus() // to get key events (dont seem to need 'focusable = true')
      val xy: Option[Point] = getSourcePixel(point.x, point.y)
      xy.foreach { p =>
        onClickListener.map(_.onClick(p))

        //println(s"button is ${evt.peer.getButton}")
        if(javax.swing.SwingUtilities.isLeftMouseButton(evt.peer)){
          // LEFT
          onLeftClick(p)

        } else if(javax.swing.SwingUtilities.isRightMouseButton(evt.peer)) {
          // RIGHT
          if(this.currentFirstClick.isDefined){
            this.currentFirstClick = None
            this.repaint()
          }

        }

      }
    }
    case MouseMoved(source, point, modifiers) => {
      val xy: Option[Point] = getSourcePixel(point.x, point.y)
      //xy.foreach{ p => mouseMovedListener.map( _.onMouseMoved(p) ) }

      // TODO:  add a feature that highlights the row and column the mouse is moving over!
      // TODO:  or maybe just the pixel it is moving over ... (really need partial rendering though)
      xy.foreach { newPixel =>
        val changed = srcPixelUnderMouse match {
          case Some(oldPixel) => newPixel != oldPixel._1
          case None => true
        }
        if(changed){
          srcPixelUnderMouse = Some((newPixel, this.getSourcePixelColor(newPixel).get))
          fireMouseMoved(srcPixelUnderMouse.get._1, srcPixelUnderMouse.get._2)
        }

      }
    }
    case KeyReleased(source, key, modifiers, location) => {
      println("KEY EVENT")
      if(key == Key.BackSpace || key == Key.Delete){
        plateModel.deleteSelection()
      }

    }
  }

  private def fireMouseMoved(p: Point, color: BrickColor2): Unit = {
    mouseMovedListener.foreach { listener =>
      SwingUtilities.invokeLater(() => listener.onMouseMoved(p, color))
    }
  }

  private def onLeftClick(p: Point): Unit = {
    if(plateModel.trySelectPlate(p).isDefined){
      // SELECTED PLATE
      currentFirstClick = None
      this.repaint() // TODO: need to only repaint part of it
    }else{
      if(plateModel.selectedPlate.isDefined){
        // CLEAR SELECTION
        plateModel.clearSelection()
        repaintAsync()

      }else if(! this.currentFirstClick.isDefined) {
        // FIRST CLICK
        this.currentFirstClick = Some(p)
        this.plateModel.clearSelection()
        this.repaint()  // TODO: repaint only the rect that matters
      }else{
        // SECOND CLICK
        val plate = Plate(this.currentFirstClick.get, p, getSourcePixelRgb(p).get)
        this.plateAddedListener.foreach(_.onPlateAdded(plate))
        this.plateModel.addPlate(plate, fireChanged = false)
        //this.plates = this.plates :+ plate
        this.currentFirstClick = None
        this.repaint()
      }
    }
  }

  /**
    * Translate the raw screen pixel coordinates into the (x,y) of the pixel in the original source picture
    * @param x - raw screen coordinate space
    * @param y - raw screen coordinate space
    * @return
    */
  def getSourcePixel(x: Int, y: Int): Option[Point] = {
    val img = scaledImages.getOrElse(zoomLevel, None)
    img match {
      case Some(_) => { Some(new Point(x / zoomLevel, y / zoomLevel))}
      case _ => None
    }
  }

  /**
    *
    * @param src - point in source coordinate space
    */
  private def getSourcePixelRgb(src: Point): Option[Int] ={
    val img = scaledImages.getOrElse(ImagePanelX.ORIGINAL_IMAGE, None)
    img.map( _.getRGB(src.x, src.y))
  }

  private def getSourcePixelColor(src: Point): Option[BrickColor2] = {
    getSourcePixelRgb(src).map(BrickColor2(_))
  }

  def refresh(): Unit = {
    //
    val img = scaledImages.getOrElse(zoomLevel, None)
    img match {
      case Some(i) => {
        // not sure which parts are necessary to make this work
        println("REFRESHING")
        val d = new Dimension(i.getWidth, i.getHeight)
        this.minimumSize = d
        this.preferredSize = d
        this.maximumSize = d
        println("set size to: " + d.toString)
        this.peer.setMinimumSize(d)
        this.peer.setMaximumSize(d)

        this.parent.preferredSize = d
        this.parent.minimumSize = d
        this.parent.maximumSize = d

        this.revalidate()
        this.peer.invalidate()
        this.parent.revalidate()
        this.parent.repaint()
        this.repaint()

        println("current size is: " + this.size.toString)
      }
      case _ => {}
    }
  }


  /**
    * schedule a repaint on the UI thread without blocking the current execution
    */
  private def repaintAsync(): Unit = {
    SwingUtilities.invokeLater(() => repaint())
  }

  override def paint(g: Graphics2D): Unit = {
    //scala graphics2d is just a typedef to java.awt.graphics2d
    super.paint(g)

    val img = scaledImages.getOrElse(zoomLevel, None)

    img match {
      case Some(i) => { //image loaded
        // IMAGE
        g.drawImage(i, 0, 0, null)

        // PLATES
        this.drawPlates(g)

        // SELECTED PLATE
        plateModel.selectedPlate.foreach { selected =>
          drawPlate(g, selected, true)
        }

        // TARGET RECT
        this.currentFirstClick.foreach{ p =>
          drawTarget(g, p)
        }
      }
      case _ => {  // no image loaded
        g.setColor(new Color(50, 50, 50))
        g.fillRect(0, 0, this.bounds.width, this.bounds.height)
      }

    }
    //
  }


  private def drawPlate(g: Graphics2D, p: Plate, selected: Boolean = false): Unit = {
    val s = zoomLevel
    if(p.isCorner){
      // TODO
    } else {
      val x1 = p.x * s
      val y1 = p.y * s
      val w = p.w * s
      val h = p.h * s

      val (outerColor, innerColor) = selected match {
        case true => (Color.YELLOW, Color.ORANGE)
        case false => (Color.white, new Color(100, 0, 50))
      }

      //g.setColor(Color.white)
      g.setColor(outerColor)
      g.drawRect(x1, y1, w, h)

      //val innerColor: Color = new Color(100, 0, 50)
      g.setColor(innerColor)
      g.drawRect(x1 + 1, y1 + 1, w - 2, h - 2)

      g.setColor(new Color(127, 127, 127, 50))
      g.fillRect(x1 + 2, y1 + 2, w - 4, h - 4)

      //diagonal line
      g.setColor(new Color(innerColor.getRed, innerColor.getGreen, innerColor.getBlue, 100))
      g.drawLine(x1 + 2, y1 + 2, x1 + 2 + w - 4, y1 + 2 + h - 4)
    }
  }

  private def drawPlates(g: Graphics2D): Unit = {
    this.plateModel.getPlates().foreach(drawPlate(g, _))
  }

  /**
    * draw rect in source coordinates
    * @param x
    * @param y
    * @param w
    * @param h
    */
  private def drawRectSrc(x: Int, y: Int, w: Int, h: Int): Unit = {

  }

  /**
    * @param g
    * @param p - which source pixel (rectangle in source coordinate space)
    */
  private def drawTarget(g: Graphics2D, p: Point): Unit = {
    val s:Int = zoomLevel
    g.setColor(Color.pink)
    g.drawRect(p.x * s, p.y * s, s, s)

    g.setColor(Color.red)
    val x1 = p.x * s + 1
    val y1 = p.y * s + 1
    g.drawRect(x1, y1, s - 2, s - 2)

    val x2 = x1 + s - 2
    val y2 = y1 + s - 2

    // an X
    //g.drawLine(x1, y1, x2, y2)
    //g.drawLine(x1, y2, x2, y1)

    // a +
    val xm = (x1 + x2)/2
    val ym = (y1 + y2)/2
    g.drawLine(xm, y1, xm, y2)
    g.drawLine(x1, ym, x2, ym)

  }

}
