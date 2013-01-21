package org.corespring.models

case class DBName(
                   host: String,
                   port: String,
                   db: String,
                   username: Option[String] = None,
                   password: Option[String] = None)

object DBName {
  def apply(uri: String) = {

    def parseNoUsername = {
      val NoUser = """mongodb://(.*)/(.*)""".r
      val NoUser(hostPort, dbname) = uri
      val (host, port) = getHostPort(hostPort)
      new DBName(host, port, dbname)
    }

    def parseUsername = {
      val UserRegex = """mongodb://(.*)@(.*)/(.*)""".r
      val UserRegex(userPass, hostPort, dbname) = uri
      val (host, port) = getHostPort(hostPort)
      val (user, pass) = getUserPass(userPass)
      new DBName(host, port, dbname, Some(user), Some(pass))
    }

    def getHostPort(hostPort: String): (String, String) = splitString(hostPort, hostPort, "27017")
    def getUserPass(userPass: String): (String, String) = splitString(userPass, userPass, "password")

    def splitString(s: String, a: String, b: String): (String, String) = {
      val split = s.split(":")
      val first = if (split.length == 1) a else split(0)
      val second = if (split.length == 1) b else split(1)
      (first, second)
    }


    uri match {
      case s: String if !s.contains("@") => parseNoUsername
      case s: String if s.contains("@") => parseUsername
    }

  }

}

