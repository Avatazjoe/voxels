enablePlugins(ScalaJSPlugin)

name := "Voxels"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

scalaJSStage in Global := FastOptStage

resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases")

libraryDependencies += "org.denigma" %%% "threejs-facade" % "0.0.71-0.1.5"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.4" % "test"