import sbt._
import scala.sys.process._

object ShaderLibraryGen {

  val extensions: List[String] =
    List(".vert", ".frag")

  val fileFilter: String => Boolean =
    name => extensions.exists(e => name.endsWith(e))

  def extractDetails(remaining: Seq[String], name: String, file: File): Option[ShaderDetails] =
    remaining match {
      case Nil =>
        None

      case ext :: exts if name.endsWith(ext) =>
        Some(ShaderDetails(name.substring(0, name.indexOf(ext)).capitalize, name, ext, IO.read(file)))

      case _ :: exts =>
        extractDetails(exts, name, file)
    }

  val tripleQuotes: String = "\"\"\""

  def template(moduleName: String, fullyQualifiedPath: String, contents: String): String =
    s"""package $fullyQualifiedPath
    |
    |object $moduleName {
    |
    |$contents
    |
    |}
    """.stripMargin

  def extractShaderCode(text: String, tag: String, assetName: String, newName: String): Seq[ShaderSnippet] =
    s"""//<indigo-$tag>((.|\r\n|\n|\r)*)//</indigo-$tag>""".r
      .findAllIn(text)
      .toSeq
      .map(_.toString)
      .map(_.split('\n').drop(1).dropRight(1).mkString("\n"))
      .map(program => ShaderSnippet(newName + tag.split("-").map(_.capitalize).mkString, program))

  def makeShaderLibrary(moduleName: String, fullyQualifiedPath: String, files: Set[File], sourceManagedDir: File): Seq[File] = {
    println("Generating Indigo RawShaderCode Library...")

    val shaderFiles: Seq[File] =
      files.filter(f => fileFilter(f.name)).toSeq

    val glslValidatorExitCode = {
      val command = Seq("glslangValidator", "-v")
      val run = sys.props("os.name").toLowerCase match {
        case x if x contains "windows" => Seq("cmd", "/C") ++ command
        case _ => command
      }
      run.!
    }

    println("***************")
    println("GLSL Validation")
    println("***************")

    if (glslValidatorExitCode == 0)
      shaderFiles.foreach { f =>
        val exit = {
          val command = Seq("glslangValidator", f.getCanonicalPath)
          val run = sys.props("os.name").toLowerCase match {
            case x if x contains "windows" => Seq("cmd", "/C") ++ command
            case _ => command
          }
          run.!
        }

        if (exit != 0)
          throw new Exception("GLSL Validation Error in: " + f.getName)
        else
          println(f.getName + " [valid]")
      }
    else
      println("**WARNING**: GLSL Validator not installed, shader code not checked.")

    val shaderDetails: Seq[ShaderDetails] =
      shaderFiles
        .map(f => extractDetails(extensions, f.name, f))
        .collect { case Some(s) => s }

    val contents: String =
      shaderDetails
        .flatMap { d =>
          extractShaderCode(d.shaderCode, "vertex", d.originalName + d.ext, d.newName) ++
            extractShaderCode(d.shaderCode, "fragment", d.originalName + d.ext, d.newName) ++
            extractShaderCode(d.shaderCode, "prepare", d.originalName + d.ext, d.newName) ++
            extractShaderCode(d.shaderCode, "light", d.originalName + d.ext, d.newName) ++
            extractShaderCode(d.shaderCode, "composite", d.originalName + d.ext, d.newName)
        }
        .map { snippet =>
          s"""  val ${snippet.variableName}: String =
             |    ${tripleQuotes}${snippet.snippet}${tripleQuotes}
             |
          """.stripMargin
        }
        .mkString("\n")

    val file: File =
      sourceManagedDir / (moduleName + ".scala")

    val newContents: String =
      template(moduleName, fullyQualifiedPath, contents)

    IO.write(file, newContents)

    println("Written: " + file.getCanonicalPath)

    Seq(file)
  }

  case class ShaderDetails(newName: String, originalName: String, ext: String, shaderCode: String)
  case class ShaderSnippet(variableName: String, snippet: String)
}
