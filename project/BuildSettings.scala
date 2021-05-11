import sbt.{plugins, AutoPlugin, Plugins}
import sbt.Keys.{dependencyOverrides, libraryDependencies}

/**
 * Dependencies that will be used by any lagompb based project
 */
object BuildSettings extends AutoPlugin {
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings =
    Seq(
      libraryDependencies ++= Dependencies.Jars ++ Dependencies.TestJars
    )
}
