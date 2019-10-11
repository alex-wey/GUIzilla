package guizilla.sol.pages

import guizilla.src.Page

/**
  * Class that represents the FirstLast Page.
  */
class FirstLast extends Page {

  // stores input from user
  private var first: String = ""

  override def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    names(inputs, sessionId)

  override def clone(): Page = {
    val page = super.clone.asInstanceOf[FirstLast]
    page
  }

  /**
    * names
    * Method that prompts the FirstLast home page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML to be rendered on home page
    */
  def names(inputs: Map[String, String], sessionId: String): String = {
    s"""<html><body><p>Add First and Last Name</p>
      <form method=\"post\" action=\"/id:$sessionId/lastName\">
      <p> Please enter your first name: </p>
      <input type=\"text\" name=\"first\" />
      <input type=\"submit\" value=\"submit\" />
      </form></body></html>"""
  }

  /**
    * lastName
    * Method that prompts the form to fill a user's last name
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML to be rendered on page
    */
  def lastName(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("first") match {
      case Some(name) =>
        first = name
        s"""<html><body><p>Your first name entered was $name</p>
          <form method="post" action="/id:$sessionId/displayResult">
          </form></body></html>"""

        s"""<html><body><p>Your first name entered was $name</p>
          <form method=\"post\" action=\"/id:$sessionId/displayResult\">
          <p>Please enter your last name: </p>
          <input type=\"text\" name=\"last\" />
          <input type=\"submit\" value=\"submit\" />
          </form></body></html>"""
      case None =>
        """<html><body><p>I'm sorry. There was an error retrieving your input.
          "<a href=\"/Index\">Return to the Index</a></p></body></html>"""
    }
  }

  /**
    * displayResult
    * Method that displays the result
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the final result
    */
  def displayResult(inputs: Map[String, String], sessionId: String): String = {
    inputs.get("last") match {
      case Some(name) =>
        val last = name
        s"""<html><body><p>Your full name is $first $last</p></body></html>"""
      case None =>
        """<html><body><p>I'm sorry, there was an error retrieving your input.
          "<a href=\"/Index\">Return to the Index</a></p></body></html>"""
    }
  }
}