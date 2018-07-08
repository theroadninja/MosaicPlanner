package trn.mosaic


object LayoutFile {
  def apply(plates: Seq[Plate]): LayoutFile = {
    new LayoutFile(plates, Histogram.histo(plates.map(_.asPrefab())).toSeq)
    //new LayoutFile(plates)
  }
}
//case class LayoutFile(plateList: Seq[Plate], plateCount: Map[Plate, Int], version: Int = 1) {
case class LayoutFile(plateList: Seq[Plate], plateCounts: Seq[(Plate, Int)], version: Int = 1) {
}
