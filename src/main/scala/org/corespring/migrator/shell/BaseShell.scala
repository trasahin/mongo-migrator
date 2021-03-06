package org.corespring.migrator.shell

import org.corespring.migrator.models.{DbInfo, Script}
import exceptions.ShellException
import java.io.{File,FileWriter}
import grizzled.slf4j.Logging


case class CmdResult(name:String,out:String,err:String,exitCode:Int)

trait BaseShell extends Logging {

  /** Build the command to be executed.
   */
  def cmd( dbInfo :DbInfo, path:String) : String = dbInfo.toCmdLine + " " + path

  def prepareScript(contents:String) : String = contents

  def run(dbInfo: DbInfo, scripts: Seq[Script]): Boolean = {

    val shellFile = "baseShell_tmp.js"

    def writeToFile(s:String) : File = {
      val fw = new FileWriter(shellFile)
      fw.write(s)
      fw.close()
      new File(shellFile)
    }
    val cmdResults : Seq[CmdResult] = scripts.map {
      sc =>
        import scala.sys.process._
        val logger = new ScriptLogger(sc)
        val prepped = prepareScript(sc.contents)
        val f : File = writeToFile(prepped)
        val command = cmd(dbInfo,f.getPath)
        debug(s"running: [$command] - ${sc.name}")
        val exitCode = command ! logger
        f.delete()
        debug(sc.name)
        debug(logger.outLog)

        if(exitCode != 0){
          throw new ShellException(s"${sc.name}\n${logger.errorLog}\n${logger.outLog}")
        }

        CmdResult(sc.name, logger.outLog, logger.errorLog, exitCode)
    }

    val errorResults = cmdResults.filterNot( _.exitCode == 0)

    errorResults match {
      case Nil => true
      case _ => {

        error(errorResults)
        val msg = errorResults.map( r => r.name + "\n" + r.err + "\n" + r.out).mkString("\n")
        error( s"shell exception: $msg")
        throw new ShellException(msg)
      }
    }
  }

}

