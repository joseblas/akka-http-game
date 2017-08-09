import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

enablePlugins(JavaAppPackaging)

name := "tom-service"

version := "1.0"

scalaVersion := "2.12.2"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.4.16"
  val akkaHttpV   = "10.0.5"
  val scalaTestV  = "3.0.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "ch.megard" %% "akka-http-cors" % "0.2.1",
    "org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)
packageName in Docker := "tom-service-python"
dockerExposedPorts := Seq(9000)

dockerRepository := Some("localhost:5000/bdec")


dockerCommands := Seq(
  Cmd("FROM", "python:3.5-slim"),

  Cmd("RUN", "echo \"deb http://http.debian.net/debian jessie-backports main\" >> /etc/apt/sources.list"),
  Cmd("RUN", "apt-get update"),
  Cmd("RUN", "apt-get install -y build-essential python3-dev git"),
  Cmd("RUN", "apt install -t jessie-backports  -y openjdk-8-jre-headless ca-certificates-java"),
  Cmd("RUN", "apt-get install -y -f openjdk-8-jdk-headless"),

  Cmd("RUN", "git clone https://github.com/joseblas/tom.git"),

  Cmd("RUN", "pip3 install -U spacy"),
  Cmd("RUN", "python3 -m spacy download en"),

  Cmd("WORKDIR", "/opt/docker"),
  Cmd("ADD", "opt /opt"),

  Cmd("ADD", "opt/docker/ /app/"),
  Cmd("WORKDIR", "/app"),
  Cmd("RUN", "git clone https://github.com/joseblas/tom.git"),

  Cmd("EXPOSE", "9000"), // Backend
//  Cmd("EXPOSE", "8080"), // UI


  ExecCmd("ENTRYPOINT", "bin/tom-service", "-Dtom.tomHome=/app/tom/tom.py")
)

Revolver.settings