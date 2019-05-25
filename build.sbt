name := "ihere"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "com.bot4s" %% "telegram-core" % "4.2.0-RC1"
libraryDependencies += "com.bot4s" %% "telegram-akka" % "4.2.0-RC1"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "1.2.0"

libraryDependencies += "com.softwaremill.sttp" %% "async-http-client-backend-cats" % "1.5.17"

libraryDependencies += "co.fs2" %% "fs2-core" % "1.0.4" // For cats 1.5.0 and cats-effect 1.1.0
//libraryDependencies += "co.fs2" %% "fs2-io"   % "1.0.4"
