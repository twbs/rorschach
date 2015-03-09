name := "rorschach"

version := "1.0"

scalaVersion := "2.11.5"

mainClass := Some("com.getbootstrap.rorschach.server.Boot")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers += "Eclipse Foundation Releases" at "https://repo.eclipse.org/content/repositories/releases/"

resolvers += "Eclipse Foundation Snapshots" at "https://repo.eclipse.org/content/repositories/snapshots/"

libraryDependencies += "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.5"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV   % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV    % "test",
    "org.specs2"          %%  "specs2"        % "2.3.12" % "test"
  )
}

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "–Xlint", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

// parallelExecution in Test := false

Revolver.settings
