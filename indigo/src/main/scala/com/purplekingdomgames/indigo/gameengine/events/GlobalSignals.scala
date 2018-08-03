package com.purplekingdomgames.indigo.gameengine.events

import com.purplekingdomgames.indigo.gameengine.constants.KeyCode
import com.purplekingdomgames.indigo.gameengine.scenegraph.datatypes.Point

object GlobalSignals {

  def MousePosition: Point = GlobalSignalsManager.MousePosition

  def LeftMouseHeldDown: Boolean = GlobalSignalsManager.LeftMouseHeldDown

  def KeysDown: Set[KeyCode] = GlobalSignalsManager.KeysDown

}

private[indigo] object GlobalSignalsManager {

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private var signals: Signals = Signals.default

  def update(events: List[GameEvent]): Signals = {
    signals = events.foldLeft(signals) { (signals, e) =>
      e match {
        case mp: MouseEvent.Move =>
          signals.copy(mousePosition = mp.position)

        case _: MouseEvent.MouseDown =>
          signals.copy(leftMouseHeldDown = true)

        case _: MouseEvent.MouseUp =>
          signals.copy(leftMouseHeldDown = false)

        case e: KeyboardEvent.KeyDown =>
          signals.copy(keysDown = signals.keysDown + e.keyCode)

        case e: KeyboardEvent.KeyUp =>
          signals.copy(keysDown = signals.keysDown.filterNot(_ === e.keyCode))

        case _ =>
          signals
      }
    }

    signals
  }

  def MousePosition: Point = signals.mousePosition

  def LeftMouseHeldDown: Boolean = signals.leftMouseHeldDown

  def KeysDown: Set[KeyCode] = signals.keysDown

}

private[indigo] case class Signals(mousePosition: Point, keysDown: Set[KeyCode], leftMouseHeldDown: Boolean)
private[indigo] object Signals {
  val default: Signals = Signals(
    mousePosition = Point.zero,
    keysDown = Set(),
    leftMouseHeldDown = false
  )
}
