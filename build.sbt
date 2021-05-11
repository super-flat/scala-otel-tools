import scalapb.compiler.Version.scalapbVersion

test / parallelExecution := true

lazy val root: Project = project
  .in(file("."))
  .aggregate(otel_tools, proto_test)
  .enablePlugins(CommonSettings)
  .enablePlugins(NoPublish)
  .disablePlugins(Publish)
  .settings(name := "scala-otel-tools")

lazy val otel_tools: Project = project
  .in(file("code/otel-tools"))
  .dependsOn(proto_test % "test->compile")
  .enablePlugins(BuildSettings)
  .enablePlugins(Publish)
  .disablePlugins(NoPublish)
  .settings(name := "otel-tools")

lazy val proto_test: Project = project
  .in(file("code/.proto"))
  .enablePlugins(BuildSettings)
  .enablePlugins(NoPublish)
  .disablePlugins(Publish)
  .settings(
    libraryDependencies ++= Seq(
      // scalapb
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapbVersion % "protobuf"
    ),
    name := "protogen",
      Compile / PB.protoSources ++= Seq(file("proto")),
      Compile / PB.includePaths ++= Seq(file("proto/test")),
      Compile / PB.targets := Seq(
        scalapb.gen(flatPackage = false, javaConversions = false, grpc = true) -> (Compile / sourceManaged).value / "scalapb"
      )
  )
