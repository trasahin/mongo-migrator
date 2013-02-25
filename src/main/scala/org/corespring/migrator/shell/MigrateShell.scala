package org.corespring.migrator.shell


object MigrateShell extends BaseShell {

  override def prepareScript(contents: String): String = {

    val template =
      """
        |if(!this["up"]){
        |  throw "You need to specify an 'up' function";
        |}
        |
        |print("running up: ");
        |up();
        |print("done.");
      """.stripMargin

    contents + "\n" + template
  }
}

