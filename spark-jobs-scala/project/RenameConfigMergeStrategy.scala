import sbtassembly.MergeStrategy

import java.io.File

class RenameConfigMergeStrategy extends MergeStrategy {
  override def name: String = "Rename to application.properties"

  override def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] = {
    Right(files.map(_ -> "application.properties"))
  }
}