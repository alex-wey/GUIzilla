package guizilla.sol.pages

import guizilla.src.Page
import scala.collection.mutable.HashMap

/**
  * Class that represents the Application Page (for applying to Google).
  */
class Application extends Page {

  // data map that stores inputs from user
  private val data: HashMap[String, String] = new HashMap[String, String]

  /**
    * defaultHandler
    *
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML representing a response
    */
  override def defaultHandler(inputs: Map[String, String], sessionID: String): String = {
    apply(inputs, sessionID)
  }

  //not necessary but here for clarity
  override def clone(): Page = {
    val copy = super.clone().asInstanceOf[Application]
    copy
  }

  /**
    * apply
    * Method that prompts the Application home page
    *
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML to be rendered on home page
    */
  def apply(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>Welcome to Google!</p><form method="post" 
      action="/id:$sessionID/google">
      <p>Apply here for a job.</p>
      <p>Full Name:</p>
      <input type="text" name="name" />
      <p>GPA:</p>
      <input type="text" name="gpa" />
      <input type="submit" value="submit"></form></body></html>"""
  }

  /**
    * reapply
    * Method that re-prompts the Application page if form is not completed
    * properly
    *
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML to be rendered on the Application page
    */
  def reapply(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>Form not completed properly. Please try again.</p><form method="post" 
      action="/id:$sessionID/google">
      <p>Google!</p>
      <p>Full Name:</p>
      <input type="text" name="name" />
      <p>GPA:</p>
      <input type="text" name="gpa" />
      <input type="submit" value="submit"></form></body></html>"""
  }

  /**
    * google
    * Method that prompts application
    *
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML representing a response
    */
  def google(inputs: Map[String, String], sessionID: String): String = {
    try {
      val name = inputs.get("name").get
      val gpa = inputs.get("gpa").get
      if(gpa.equals("") || name.equals("") || isInt(gpa)) {
        throw new NoSuchElementException
      }
      data += ("name" -> name)
      data += ("gpa" -> gpa)
      s"""<html><body>
        <p>Thanks for applying to Google!</p>
        <p>Positions available: Janitor, Electrician, or Mascot</p>
        <form method="post" 
        action="/id:$sessionID/applied">
        <p>Choose the position for which you want to apply for!</p>
        <p>Position:</p>
        <input type="text" name="position" />
        <input type="submit" value="submit"></form></body></html>"""
    } catch {
      case nse: NoSuchElementException => reapply(inputs, sessionID)
    }
  }

  /**
    * applied
    * Method that prompts the page after application is received (and rejects applicant
    * just as Google would)
    *
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML representing a response
    */
  def applied(inputs: Map[String, String], sessionID: String): String = {
    try {
      val gpa = data.get("gpa").get
      val position = inputs.get("position").get

      if (position.toLowerCase().equals("janitor") ||
        position.toLowerCase().equals("electrician") ||
        position.toLowerCase().equals("mascot")) {
        s"""<html><body>
          <p>Your GPA ($gpa) is not good enough for the position of $position at Google.
          <a href="/id:$sessionID/apply">Back to Google Application</a></p>
          </body></html>"""
      } else {
        "<html><body><p>Don't waste our time. That's not one of the options. " +
          "Sorry, no job for you.</p></body></html>"
      }
    } catch {
      case nse: NoSuchElementException =>
        "<html><body><p>Don't waste our time. You didn't choose a job. " +
          "Sorry, no job for you.</p></body></html>"
    }
  }
  
 /**
   * isInt
   * Method that checks if an object can be of type Int
   *
   * @param s - a string
   * @return true if the string can be of type Int, false otherwise
   */
  private def isInt(str: String): Boolean = {
    try {
      str.toInt
      false
    } catch {
      case _: Throwable => true
    }
  }
}