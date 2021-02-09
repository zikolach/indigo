package indigo.shared.materials

import indigo.shared.assets.AssetName
import indigo.shared.shader.StandardShaders

// import indigo.shared.shader.Uniform
// import indigo.shared.shader.ShaderPrimitive.float

sealed trait StandardMaterial extends Material

object StandardMaterial {

  final case class Blit(diffuse: AssetName) extends StandardMaterial {
    val hash: String =
      diffuse.value

    def toGLSLShader: GLSLShader =
      GLSLShader(
        StandardShaders.Blit,
        Map(),
        // Map(Uniform("ALPHA") -> float(alpha)),
        Some(diffuse),
        None,
        None,
        None
      )
  }

  // final case class Textured(diffuse: AssetName, isLit: Boolean) extends StandardMaterial {

  //   def withDiffuse(newDiffuse: AssetName): Textured =
  //     this.copy(diffuse = newDiffuse)

  //   def lit: Textured =
  //     this.copy(isLit = true)

  //   def unlit: Textured =
  //     this.copy(isLit = false)

  //   def toGLSLShader: GLSLShader =
  //     GLSLShader(
  //       StandardShaders.Basic,
  //       Map(),
  //       Some(diffuse),
  //       None,
  //       None,
  //       None
  //     )

  //   lazy val hash: String =
  //     diffuse.value + (if (isLit) "1" else "0")
  // }
  // object Textured {
  //   def apply(diffuse: AssetName): Textured =
  //     new Textured(diffuse, false)

  //   def unapply(t: Textured): Option[(AssetName, Boolean)] =
  //     Some((t.diffuse, t.isLit))
  // }

  // final case class Lit(
  //     albedo: AssetName,
  //     emissive: Option[Texture],
  //     normal: Option[Texture],
  //     specular: Option[Texture],
  //     isLit: Boolean
  // ) extends StandardMaterial {

  //   def withAlbedo(newAlbedo: AssetName): Lit =
  //     this.copy(albedo = newAlbedo)

  //   def withEmission(emissiveAssetName: AssetName, amount: Double): Lit =
  //     this.copy(emissive = Some(Texture(emissiveAssetName, amount)))

  //   def withNormal(normalAssetName: AssetName, amount: Double): Lit =
  //     this.copy(normal = Some(Texture(normalAssetName, amount)))

  //   def withSpecular(specularAssetName: AssetName, amount: Double): Lit =
  //     this.copy(specular = Some(Texture(specularAssetName, amount)))

  //   def lit: Lit =
  //     this.copy(isLit = true)

  //   def unlit: Lit =
  //     this.copy(isLit = false)

  //   lazy val hash: String =
  //     albedo.value +
  //       emissive.map(_.hash).getOrElse("_") +
  //       normal.map(_.hash).getOrElse("_") +
  //       specular.map(_.hash).getOrElse("_") +
  //       (if (isLit) "1" else "0")

  //   def toGLSLShader: GLSLShader =
  //     GLSLShader(
  //       StandardShaders.Basic,
  //       Map(),
  //       Some(albedo),
  //       emissive.map(_.assetName),
  //       normal.map(_.assetName),
  //       specular.map(_.assetName)
  //     )
  // }
  // object Lit {
  //   def apply(
  //       albedo: AssetName,
  //       emissive: Option[Texture],
  //       normal: Option[Texture],
  //       specular: Option[Texture]
  //   ): Lit =
  //     new Lit(albedo, emissive, normal, specular, true)

  //   def apply(
  //       albedo: AssetName
  //   ): Lit =
  //     new Lit(albedo, None, None, None, true)

  //   def apply(
  //       albedo: AssetName,
  //       emissive: AssetName
  //   ): Lit =
  //     new Lit(
  //       albedo,
  //       Some(Texture(emissive, 1.0d)),
  //       None,
  //       None,
  //       true
  //     )

  //   def apply(
  //       albedo: AssetName,
  //       emissive: AssetName,
  //       normal: AssetName
  //   ): Lit =
  //     new Lit(
  //       albedo,
  //       Some(Texture(emissive, 1.0d)),
  //       Some(Texture(normal, 1.0d)),
  //       None,
  //       true
  //     )

  //   def apply(
  //       albedo: AssetName,
  //       emissive: AssetName,
  //       normal: AssetName,
  //       specular: AssetName
  //   ): Lit =
  //     new Lit(
  //       albedo,
  //       Some(Texture(emissive, 1.0d)),
  //       Some(Texture(normal, 1.0d)),
  //       Some(Texture(specular, 1.0d)),
  //       true
  //     )

  //   def fromAlbedo(albedo: AssetName): Lit =
  //     new Lit(albedo, None, None, None, true)
  // }

}

// final case class Texture(assetName: AssetName, amount: Double) {
//   def hash: String =
//     assetName.value + amount.toString()
// }
