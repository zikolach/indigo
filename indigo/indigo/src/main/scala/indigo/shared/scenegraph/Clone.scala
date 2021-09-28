package indigo.shared.scenegraph

import indigo.shared.datatypes._

/** A single cloned instance of a cloneblank
  */
final case class Clone(id: CloneId, depth: Depth, transform: CloneTransformData)
    extends DependentNode
    with BasicSpatialModifiers[Clone] derives CanEqual:
  lazy val x: Int                  = transform.position.x
  lazy val y: Int                  = transform.position.y
  lazy val rotation: Radians       = transform.rotation
  lazy val scale: Vector2          = transform.scale
  lazy val flipHorizontal: Boolean = transform.flipHorizontal
  lazy val flipVertical: Boolean   = transform.flipVertical
  lazy val ref: Point              = Point.zero

  def position: Point = Point(transform.position.x, transform.position.y)
  def flip: Flip      = Flip(transform.flipHorizontal, transform.flipVertical)

  def withCloneId(newCloneId: CloneId): Clone =
    this.copy(id = newCloneId)

  def withDepth(newDepth: Depth): Clone =
    this.copy(depth = newDepth)

  def withTransforms(
      newPosition: Point,
      newRotation: Radians,
      newScale: Vector2,
      flipHorizontal: Boolean,
      flipVertical: Boolean
  ): Clone =
    this.copy(transform = CloneTransformData(newPosition, newRotation, newScale, flipHorizontal, flipVertical))

  def withPosition(newPosition: Point): Clone =
    this.copy(transform = transform.withPosition(newPosition))

  def withRotation(newRotation: Radians): Clone =
    this.copy(transform = transform.withRotation(newRotation))

  def withScale(newScale: Vector2): Clone =
    this.copy(transform = transform.withScale(newScale))

  def withHorizontalFlip(isFlipped: Boolean): Clone =
    this.copy(transform = transform.withHorizontalFlip(isFlipped))

  def withVerticalFlip(isFlipped: Boolean): Clone =
    this.copy(transform = transform.withVerticalFlip(isFlipped))

  def withFlip(newFlip: Flip): Clone =
    this.copy(
      transform = transform
        .withVerticalFlip(newFlip.vertical)
        .withHorizontalFlip(newFlip.horizontal)
    )

object Clone:
  def apply(id: CloneId): Clone =
    Clone(id, Depth.one, CloneTransformData.identity)
