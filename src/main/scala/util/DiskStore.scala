package util

import java.io._

object DiskStore {
  def load[T](loader: => T, id: String, reload: Boolean = false): T = {
    val filename = "stored$" + md5(id) + ".dat"
    val tmpdir = System.getProperty("java.io.tmpdir")
    val file = new File(tmpdir, filename)

    if (reload) file.delete

    try {
      loadData(file)
    } catch {
      case e =>
        val data = loader
        storeData(file, data)
        data
    }
  }

  private def loadData[T](file: File): T = {
    using(new ObjectInputStream(new FileInputStream(file))) { in =>
      in.readObject.asInstanceOf[T]
    }
  }

  private def storeData[T](file: File, obj: T) {
    using(new ObjectOutputStream(new FileOutputStream(file))) { out =>
      out.writeObject(obj)
    }
  }
}
