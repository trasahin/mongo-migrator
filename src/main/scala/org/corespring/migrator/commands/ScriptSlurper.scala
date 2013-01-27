package org.corespring.migrator.commands

import java.io.File
import org.corespring.migrator.models.Script

object ScriptSlurper {

  def scriptsFromPaths(paths: List[String]): List[Script] = {

    def readContents(f: File): String = {
      val source = scala.io.Source.fromFile(f)
      val lines = source.mkString
      source.close()
      lines
    }

    val allFiles: List[File] = paths.map(folder => recursiveListFiles(new File(folder))).flatten
    allFiles
      .filter(f => f.isFile && f.getName.endsWith(".js"))
      .map(f => new Script(f.getPath, readContents(f)))
  }

  private def recursiveListFiles(f: File): List[File] = f match {
    case doesntExit : File if !f.exists() => List()
    case file: File if f.isFile => List(file)
    case directory: File if f.isDirectory => {
      val files = directory.listFiles.toList
      files ++ files.filter(_.isDirectory).flatMap(recursiveListFiles)
    }
    case _ => List()
  }

}
