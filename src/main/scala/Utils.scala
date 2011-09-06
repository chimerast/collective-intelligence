import java.security.MessageDigest

object Utils {
  def md5(str: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(str.getBytes("UTF-8"))

    md5.digest().map(0xFF & _).map("%02x".format(_)).mkString
  }

  def using[S <% { def close(): Unit }, T](what: S)(block: S => T): T = {
    try {
      block(what)
    } finally {
      what.close
    }
  }
}