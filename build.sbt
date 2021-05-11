test / parallelExecution := true

lazy val root: Project = project
  .in(file("."))
  .aggregate(otel_tools, proto_test)
  .enablePlugins(CommonSettings)
  .enablePlugins(NoPublish)
  .settings(name := "scala-opentelemetry-tools")

lazy val otel_tools: Project = project
  .in(file("code/opentelemetry-tools"))
  .dependsOn(proto_test % "test->compile")
  .enablePlugins(BuildSettings)
  .enablePlugins(Publish)
  .settings(name := "opentelemetry-tools")

lazy val proto_test: Project = project
  .in(file("code/.proto"))
  .enablePlugins(BuildSettings)
  .enablePlugins(NoPublish)
  .settings(
    name := "protogen",
      Compile / PB.protoSources ++= Seq(file("proto")),
      Compile / PB.includePaths ++= Seq(file("proto/test")),
      Compile / PB.targets := Seq(
        scalapb.gen(flatPackage = false, javaConversions = false, grpc = true) -> (Compile / sourceManaged).value / "scalapb"
      )
  )
