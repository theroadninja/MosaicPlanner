package trn.mosaic

object Histogram {

  def histo[T](things: Seq[T]): Map[T, Int] = {
    // can't think of a simpler way to do this in scala
    val histo = scala.collection.mutable.Map[T, Int]().withDefaultValue(0)
    things.foreach { k =>
      histo.put(k, histo(k) + 1)
    }
    histo.toMap
  }

}
