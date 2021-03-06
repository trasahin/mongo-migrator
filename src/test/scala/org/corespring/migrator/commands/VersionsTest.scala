package org.corespring.migrator.commands

import org.specs2.mutable.{After, Specification}
import org.joda.time.DateTime
import org.corespring.migrator.helpers.DbSingleton
import org.corespring.migrator.models.Version

class VersionsTest extends Specification {

  sequential
  Version.init(DbSingleton.db)

  "Versions" should {
    "list all the versions" in new dbTest {
      Version.create(new Version(new DateTime(), List(), "versionOne"))
      Version.create(new Version(new DateTime(), List(), "versionTwo"))
      Versions(DbSingleton.mongoUri).begin
    }
  }
}

trait dbTest extends After {
  def after = Version.dropCollection
}
