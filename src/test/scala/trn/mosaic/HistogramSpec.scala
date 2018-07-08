package trn.mosaic

import org.scalatest.FlatSpec

class HistogramSpec extends FlatSpec {

  "test" should "test" in {

    val list = Seq(1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3)

    val map = Histogram.histo(list)

    assert(map.size == 3)
    assert(map(1) == 2)
    assert(map(2) == 4)
    assert(map(3) == 6)
  }

}
