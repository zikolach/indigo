package indigo.platform.events

import indigo.shared.constants.Key
import indigo.shared.datatypes.Point
import indigo.shared.events.KeyboardEvent
import indigo.shared.events.MouseButton
import indigo.shared.events.MouseEvent
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.html
import org.scalajs.dom.window

final class WorldEvents:

  def absoluteCoordsX(relativeX: Double): Int = {
    val offset: Double =
      if (window.pageXOffset > 0) window.pageXOffset
      else if (document.documentElement.scrollLeft > 0) document.documentElement.scrollLeft
      else if (document.body.scrollLeft > 0) document.body.scrollLeft
      else 0

    (relativeX - offset).toInt
  }

  def absoluteCoordsY(relativeY: Double): Int = {
    val offset: Double =
      if (window.pageYOffset > 0) window.pageYOffset
      else if (document.documentElement.scrollTop > 0) document.documentElement.scrollTop
      else if (document.body.scrollTop > 0) document.body.scrollTop
      else 0

    (relativeY - offset).toInt
  }

  def init(canvas: html.Canvas, magnification: Int, globalEventStream: GlobalEventStream): Unit = {
    // Onclick only supports the left mouse button
    canvas.onclick = { (e: dom.MouseEvent) =>
      val rect = canvas.getBoundingClientRect()

      globalEventStream.pushGlobalEvent(
        MouseEvent.Click(
          absoluteCoordsX(e.pageX.toInt - rect.left.toInt) / magnification,
          absoluteCoordsY(e.pageY.toInt - rect.top.toInt) / magnification
        )
      )
    }

    /*
      Follows the most conventional, basic definition of wheel.
      To be fair, the wheel event doesn't necessarily means that the device is a mouse, or even that the
      deltaY represents the direction of the vertical scrolling (usually negative is upwards and positive downwards).
      For the sake of simplicity, we're assuming a common mouse with a simple wheel.

      More info: https://developer.mozilla.org/en-US/docs/Web/API/WheelEvent
     */
    canvas.onwheel = { (e: dom.WheelEvent) =>
      val rect = canvas.getBoundingClientRect()
      val wheel = MouseEvent.Wheel(
        Point(
          absoluteCoordsX(e.pageX.toInt - rect.left.toInt) / magnification,
          absoluteCoordsY(e.pageY.toInt - rect.top.toInt) / magnification
        ),
        e.deltaY
      )

      globalEventStream.pushGlobalEvent(wheel)
    }

    canvas.onmousemove = { (e: dom.MouseEvent) =>
      val rect = canvas.getBoundingClientRect()

      globalEventStream.pushGlobalEvent(
        MouseEvent.Move(
          absoluteCoordsX(e.pageX.toInt - rect.left.toInt) / magnification,
          absoluteCoordsY(e.pageY.toInt - rect.top.toInt) / magnification
        )
      )
    }

    canvas.onmousedown = { (e: dom.MouseEvent) =>
      val rect = canvas.getBoundingClientRect()

      MouseButton.fromOrdinalOpt(e.button).foreach { mouseButton =>
        globalEventStream.pushGlobalEvent(
          MouseEvent.MouseDown(
            absoluteCoordsX(e.pageX.toInt - rect.left.toInt) / magnification,
            absoluteCoordsY(e.pageY.toInt - rect.top.toInt) / magnification,
            mouseButton
          )
        )
      }
    }

    canvas.onmouseup = { (e: dom.MouseEvent) =>
      val rect = canvas.getBoundingClientRect()

      MouseButton.fromOrdinalOpt(e.button).foreach { mouseButton =>
        globalEventStream.pushGlobalEvent(
          MouseEvent.MouseUp(
            absoluteCoordsX(e.pageX.toInt - rect.left.toInt) / magnification,
            absoluteCoordsY(e.pageY.toInt - rect.top.toInt) / magnification,
            mouseButton
          )
        )
      }
    }

    // Prevent right mouse button from popping up the context menu
    canvas.oncontextmenu = _.preventDefault()

    document.onkeydown = { (e: dom.KeyboardEvent) =>
      globalEventStream.pushGlobalEvent(KeyboardEvent.KeyDown(Key(e.keyCode, e.key)))
    }

    document.onkeyup = { (e: dom.KeyboardEvent) =>
      globalEventStream.pushGlobalEvent(KeyboardEvent.KeyUp(Key(e.keyCode, e.key)))
    }

  }

  def kill(canvas: html.Canvas): Unit = {
    canvas.removeEventListener("click", (e: dom.MouseEvent) => ())
    canvas.removeEventListener("wheel", (e: dom.WheelEvent) => ())
    canvas.removeEventListener("mousemove", (e: dom.MouseEvent) => ())
    canvas.removeEventListener("mousedown", (e: dom.MouseEvent) => ())
    canvas.removeEventListener("mouseup", (e: dom.MouseEvent) => ())
    document.removeEventListener("keydown", (e: dom.KeyboardEvent) => ())
    document.removeEventListener("keyup", (e: dom.KeyboardEvent) => ())
  }

end WorldEvents
