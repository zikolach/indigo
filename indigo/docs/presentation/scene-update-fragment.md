---
id: scene-update-fragment
title: SceneUpdateFragment
---

> THIS PAGE IS OUT OF DATE. Apologies, please see examples while we correct the problem.

The `SceneUpdateFragment` is one of the most important types in Indigo, as it is the type that describes everything you want your player to experience, visually and audibly*.

>*An exception: You could emit a `PlaySound` event from a model or view model update via an `Outcome` if you had a good reason to do so.

Chunky usage sample taken from the lighting example code:

```scala mdoc
import indigo._

val config = GameConfig.default
val graphic =
  Graphic(50, 50, Material.Bitmap(AssetName("placeholder"))
    .withLighting(LightingModel.Lit.flat))

SceneUpdateFragment(graphic)
  .withLights(
    AmbientLight(RGBA.White.withAmount(0.1)),
    PointLight.default
      .moveTo(config.viewport.center + Point(50, 0))
      //.withAttenuation(50)
      .withColor(RGBA.Green),
    PointLight.default
      .moveTo(config.viewport.center + Point(-50, 0))
      //.withAttenuation(50)
      .withColor(RGBA.Red),
    DirectionLight(Radians.fromDegrees(30), RGBA.Green),
    SpotLight.default
      .withColor(RGBA.Magenta)
      .moveTo(config.viewport.center + Point(-150, -60))
      .rotateBy(Radians.fromDegrees(45))
      //.withHeight(25)
      //.withPower(1.5)
  )
```

Unlike `Outcome`, `SceneUpdateFragment`s are _not_ Functors, but they are Monoids, which is to say that:

1. There is a concept of an identity value, `SceneUpdateFragment.empty`, that if you add it to another `SceneUpdateFragment` it has no effect.
2. There is an append operation `|+|` for combining to of them together.

This is really important as it allows you to build parts of your scene up in lots of different ways, and then easily and reliably combine all the results together at the end. For example:

```scala
val sceneAudio: SceneUpdateFragment = ???
val background: SceneUpdateFragment = ???
val clouds: SceneUpdateFragment = ???
val player: SceneUpdateFragment = ???
val foreground: SceneUpdateFragment = ???

sceneAudio |+| background |+| clouds |+| player |+| foreground
```

Consider also the following:

```scala mdoc
val visible = true
val scene: SceneUpdateFragment = SceneUpdateFragment(graphic)
val vanishingThing =
  if(visible) SceneUpdateFragment(graphic)
  else SceneUpdateFragment.empty

scene |+| vanishingThing
```

Or this:

```scala mdoc
val l: List[SceneUpdateFragment] = List(scene, scene, scene)

l.foldLeft(SceneUpdateFragment.empty)(_ |+| _)
```

## What can you describe?

The `SceneUpdateFragment` has a fairly rich API that you can explore, but at a high level allows you to describe the following:

- The Game Layer: Nodes and effects that represent the main playable visuals
- The Lighting Layer: Nodes and effects that represent the image based lighting effects
- The Distortion Layer: Nodes that warp the visuals
- The UI Layer: Nodes and effects that represent the UI that sits above everything
- Ambient light: White light / None? Pitch black? A moonlight blue?
- Dynamic Lights: A list of dynamic lights that affect nodes with the right materials.
- Audio: Background audio tracks and volume mixing.
- Screen level effects: Some basic screen effects like saturation and tint
- Clone blanks: A list of nodes used as look up reference for Cloning.