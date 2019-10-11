package guizilla.sol.client

import javafx.scene.layout.VBox

/**
  * A paragraph element of an HTML page.
  *
  * @param elements - the HTML elements of the paragraph
  */
case class Paragraph(elements: List[HTMLElement]) extends HTMLElement {

  /**
    * render
    * Method that renders element to console
    *
    * @param box - the VBox containing corresponding children
    * @param browser - the GUIBrowser
    */
  override def render(box: VBox, browser: GUIBrowser): Unit = {
    for (el <- elements) {
      el.render(box, browser)
    }
  }
}