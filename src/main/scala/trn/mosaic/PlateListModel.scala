package trn.mosaic

import javax.swing.SwingUtilities
import trn.mosaic.PlateListModel.ChangedListener

import scala.collection.mutable
import scala.swing.Point


object PlateListModel {

  trait ChangedListener {
    // TODO:  implement sending added, removed
    def onPlatesChanged(added: Option[Seq[Plate]], removed: Option[Seq[Plate]])
  }
}

/**
  * Maintains a list of plates for the ImagePanelX
  */
class PlateListModel {

  // TODO:  track if modified since save

  /** tracks if modified since save */
  var modified = false

  var selectedPlate: Option[Plate] = None

  private val listeners: mutable.ArrayBuffer[ChangedListener] = new mutable.ArrayBuffer()
  //
  private var plates: Seq[Plate] = Seq()  // TODO: do all this more intelligently?

  def clearModified(): Unit = { modified = false }

  def fireChanged(): Unit = {
    modified = true
    listeners.foreach { listener =>
      SwingUtilities.invokeLater(() => {
        listener.onPlatesChanged(None, None)
      })
    }
  }

  def clearSelection(): Unit = {
    selectedPlate = None
  }

  def deleteSelection(): Boolean = {
    selectedPlate match {
      case Some(selected) => {
        plates = plates.filter(_ != selected)
        selectedPlate = None
        fireChanged()
        true
      }
      case _ => false
    }
  }

  def addListener(n: ChangedListener): Unit ={
    listeners.append(n)
  }

  def setPlates(p: Seq[Plate]): Unit = {
    plates = p
    fireChanged()
  }

  def getPlates(): Seq[Plate] = {
    return plates
  }

  def addPlate(plate: Plate, fireChanged: Boolean = true): Plate = {
    this.plates = this.plates :+ plate
    return plate
  }

  def getLayoutFile(): LayoutFile = {
    return LayoutFile(plates)
  }

  /**
    * Select and return the first plate found that contains the given coordinate
    * @param srcX - x pixel in source coordinates
    * @param srcY
    * @return
    */
  def trySelectPlate(srcX: Int, srcY: Int): Option[Plate] = {
    // NOTE: plates are one-based
    //plates.find(_.pl)I
    //k
    return plates.find(_.contains(srcX, srcY)).map { found =>
      selectedPlate = Some(found)
      found
    }
  }
  def trySelectPlate(p: Point): Option[Plate] = trySelectPlate(p.x, p.y)

}
