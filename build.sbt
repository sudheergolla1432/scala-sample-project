ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "UniversityCourse"
  )

javacOptions ++= Seq("-source", "11", "-target", "11")

val DoobieVersion = "1.0.0-RC1"
val NewTypeVersion = "0.4.4"
val Http4sVersion = "1.0.0-M21"
val CirceVersion = "0.14.0-M5"

libraryDependencies ++= Seq(
  "org.scala-lang.modules"            %% "scala-java8-compat"           % "0.9.1",
  "com.aerospike"                      % "aerospike-client"             % "4.4.18",
  "javax.json"                         % "javax.json-api"               % "1.0",
  "javax.servlet"                      % "javax.servlet-api"            % "3.1.0",
  "jakarta.ws.rs"                      % "jakarta.ws.rs-api"            % "2.1.5",
  "org.tpolecat"                       %% "doobie-core"                 % DoobieVersion,
  "org.tpolecat"                       %% "doobie-postgres"             % DoobieVersion,
  "org.tpolecat"                       %% "doobie-hikari"               % DoobieVersion,
  "io.estatico"                        %% "newtype"                     % NewTypeVersion,
  "org.http4s"                         %% "http4s-blaze-server"         % Http4sVersion, // server on which we are running http
  "org.http4s"                         %% "http4s-circe"                % Http4sVersion, //json serialization
  "org.http4s"                         %% "http4s-dsl"                  % Http4sVersion, //library for extension methods
  "io.circe"                           %% "circe-generic"               % CirceVersion,
)