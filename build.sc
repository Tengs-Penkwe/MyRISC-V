import mill._, mill.scalalib._, mill.scalalib.publish._
import scalafmt._
import $ivy.`com.goyeau::mill-scalafix::0.3.1`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`com.carlosedp::mill-aliases::0.4.1`
import com.carlosedp.aliases._


object versions {
  val scala          = "2.13.12"
  val chisel         = "6.0.0-RC1"
  val chiseltest     = "5.0.2"
  val scalatest      = "3.2.16"
  val riscvassembler = "1.9.1"
  val mainargs       = "0.5.1"
}

trait BaseProject extends ScalaModule with ScalafixModule with ScalafmtModule  {
  def scalaVersion = versions.scala
  def ivyDeps = Agg(
    ivy"org.chipsalliance::chisel:${versions.chisel}",
    ivy"com.lihaoyi::mainargs:${versions.mainargs}",
  )

  def scalacPluginIvyDeps = Agg(ivy"org.chipsalliance:::chisel-plugin:${versions.chisel}")

  object test extends ScalaTests with TestModule.ScalaTest {
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:${versions.scalatest}",
      ivy"edu.berkeley.cs::chiseltest:${versions.chiseltest}",
      ivy"com.carlosedp::riscvassembler:${versions.riscvassembler}",
    )
  }

  override def scalacOptions = T {
    super.scalacOptions() ++ Seq(
      "-unchecked",
      "-deprecation",
      "-language:reflectiveCalls",
      "-encoding",
      "UTF-8",
      "-feature",
      "-Xcheckinit",
      // "-Xfatal-warnings",
      "-Ywarn-dead-code",
      "-Ywarn-unused",
      "-Ymacro-annotations",
    )
  }
}

object MyRISC_V extends BaseProject {
    def mainClass = Some("MyRISC_V.Toplevel")
}

