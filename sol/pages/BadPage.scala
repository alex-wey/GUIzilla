package guizilla.sol.pages

import guizilla.src.Page

/**
  * Class that represents a Bad Page.
  */
class BadPage extends Page {
  
  override def defaultHandler(inputs: Map[String, String], sessionID: String): String = {
    malformed(inputs, sessionID)
  }

  /**
    * malformed
    * Method that prompts the Malformed HTML exception
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents response
    */
  def malformed(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>bad input</p>"""
  }
}