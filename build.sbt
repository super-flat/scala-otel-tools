test / parallelExecution := true

lazy val root: Project = project
  .in(file("."))
  .aggregate(otel_tools)
  .enablePlugins(CommonSettings)
  .enablePlugins(NoPublish)
  .disablePlugins(Publish)
  .settings(name := "scala-otel-tools")

lazy val otel_tools: Project = project
  .in(file("code/otel-tools"))
  .enablePlugins(BuildSettings)
  .enablePlugins(Publish)
  .disablePlugins(NoPublish)
  .settings(name := "otel-tools")
