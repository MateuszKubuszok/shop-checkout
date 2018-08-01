import sbt._
import Settings._

lazy val root = project.root
  .setName("shop-checkout")
  .setDescription("Shop checkout application")
  .configureRoot
  .aggregate(checkout)

lazy val checkout = project.from("checkout")
  .setName("checkout")
  .setDescription("Checkout library utilities")
  .setInitialImport("_")
  .configureModule
  .configureTests()
  .settings(Compile / resourceGenerators += task[Seq[File]] {
    val file = (Compile / resourceManaged).value / "shop-checkout-version.conf"
    IO.write(file, s"version=${version.value}")
    Seq(file)
  })

addCommandAlias("fullTest", ";test;scalastyle")

addCommandAlias("fullCoverageTest", ";coverage;test;coverageReport;coverageAggregate;scalastyle")

addCommandAlias("relock", ";unlock;reload;update;lock")
