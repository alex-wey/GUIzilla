package guizilla.sol.client

import javafx.scene.layout.VBox
import javafx.scene.control.TextField
import javafx.scene.control.Label
import javafx.event.EventHandler
import javafx.event.ActionEvent

/**
  * A text input element of an HTML page.
  *
  * @param name - Name of the text input for communicating with the server
  * @param value - Value of the text input as given by the user
  */
case class TextInput(val name: String, private var value: Option[String]) extends HTMLElement {

  // instantiating an text field
  val text = new TextField()

  /**
    * render
    * Method that renders element to console
    *
    * @param box - the VBox containing corresponding children
    * @param browser - the GUIBrowser
    */
  override def render(box: VBox, browser: GUIBrowser): Unit = {
    value match {
      case Some(in) =>
        text.setText(in)
      case _ =>
    }
    box.getChildren().add(text)
  }

  /**
    * addValue
    * Method that assigns value to input
    * 
    * @param input - String correspond to input value
    */
  def addValue(input: String): Unit = {
    if (text.getText != null) {
      value = Some(text.getText())
    }
  }

  /**
    * getValue
    * Method that retrieves value from private field
    * 
    * @return value - Option[String] Some(text) or None
    */
  def getValue: Option[String] = value
}