package guizilla.sol.client

import javafx.scene.layout.VBox
import javafx.scene.control.Label

/**
  * A text element of an HTML page.
  *
  * @param text - the text
  */
case class PageText(val text: String) extends HTMLElement {

  /**
    * render
    * Method that renders element to console
    *
    * @param box - the VBox containing corresponding children
    * @param browser - the GUIBrowser
    */
  override def render(box: VBox, browser: GUIBrowser): Unit = {
    val label = new Label()
    label.setText(text)
    label.setWrapText(true)
    box.getChildren().add(label)
  }
}