package guizilla.sol.pages

import guizilla.src.Page
import java.util.Date

/**
  * Class that represents the Horoscope Page.
  */
class Horoscope extends Page {

  override def defaultHandler(inputs: Map[String, String], sessionID: String): String = {
    search(inputs, sessionID)
  }

  override def clone(): Page = {
    val copy = super.clone.asInstanceOf[Horoscope]
    copy
  }

  /**
    * search
    * Method that prompts the Horoscope home page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML to be rendered on home page
    */
  def search(inputs: Map[String, String], sessionID: String): String = {
    "<html><body><p>Welcome to Horoscope Central! Find your daily horoscope.</p>" +
      "<form method=\"post\" action=\"/id:" + sessionID + "/displaySign\">" +
      "<p>Please enter your date of birth (mm/dd/yyyy):</p>" +
      "<input type=\"text\" name=\"date\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"
  }

  /**
    * errorPage
    * Method that re-prompts the Horoscope page when there is an invalid given date
    * of birth
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that is prompted when the given date of birth is
    *         invalid
    */
  def errorPage(inputs: Map[String, String], sessionID: String): String = {
    "<html><body><p>Invalid date, please input in form: (mm/dd/yyyy)</p>" +
      "<form method=\"post\" action=\"/id:" + sessionID + "/displaySign\">" +
      "<p>Please re-enter your date of birth here</p>" +
      "<input type=\"text\" name=\"date\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"
  }

  /**
    * newDate
    * Method re-prompts the Horoscope home page when a user wants to search another
    * date of birth for a horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML to be rendered on the Horoscope page
    */
  def newDate(inputs: Map[String, String], sessionID: String): String = {
    "<html><body><p>Would you like to change your date? Search another horoscope:</p>" +
      "<form method=\"post\" action=\"/id:" + sessionID + "/displaySign\">" +
      "<p>Please enter a new date of birth (mm/dd/yyyy):</p>" +
      "<input type=\"text\" name=\"date\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"
  }

  /**
    * displaySign
    * Method that displays a page with the astrological sign as a link to the
    * corresponding horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents a successful searched horoscope or that
    *         prompts the error page if the given date of birth is invalid
    */
  def displaySign(inputs: Map[String, String], sessionID: String): String = {
    try {
      val date = inputs.get("date").get
      val dateSplit = date.split("/")
      val month = dateSplit(0).toInt
      val day = dateSplit(1).toInt
      val year = dateSplit(2).toInt
      val sign = getSign(year, month, day, inputs, sessionID)
      if (date.size == 10 && dateSplit.length == 3 && 
          month <= 12 && day <= 31 && day > 0 && year < 2020 && year > 0) {
        s"""<html><body><p>Congratulations, you have a horoscope!</p>
          <p>Click the link to see it: $sign</p></body></html>"""
      } else {
        errorPage(inputs, sessionID)
      }
    } catch {
      case iob: IndexOutOfBoundsException => errorPage(inputs, sessionID)
      case nfe: NumberFormatException     => errorPage(inputs, sessionID)
    }
  }

  /**
    * getSign
    * Method that retrieves the astrological sign from a given date of birth
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents a link to the horoscope or that
    *         prompts the error page if the given date of birth is invalid
    */
  def getSign(year: Integer, month: Integer, day: Integer,
                      inputs: Map[String, String], sessionID: String): String = {
    val date = new Date(year, month, day)
    if (date.compareTo(new Date(year, 1, 20)) >= 0 &&
      date.compareTo(new Date(year, 2, 18)) <= 0) {
      s"""<a href="/id:$sessionID/Aquarius">Aquarius</a>"""
    } else if (date.compareTo(new Date(year, 2, 19)) >= 0 &&
      date.compareTo(new Date(year, 3, 20)) <= 0) {
      s"""<a href="/id:$sessionID/Pisces">Pisces</a>"""
    } else if (date.compareTo(new Date(year, 3, 21)) >= 0 &&
      date.compareTo(new Date(year, 4, 19)) <= 0) {
      s"""<a href="/id:$sessionID/Aries">Aries</a>"""
    } else if (date.compareTo(new Date(year, 4, 20)) >= 0 &&
      date.compareTo(new Date(year, 5, 20)) <= 0) {
      s"""<a href="/id:$sessionID/Taurus">Taurus</a>"""
    } else if (date.compareTo(new Date(year, 5, 21)) >= 0 &&
      date.compareTo(new Date(year, 6, 20)) <= 0) {
      s"""<a href="/id:$sessionID/Gemini">Gemini</a>"""
    } else if (date.compareTo(new Date(year, 6, 21)) >= 0 &&
      date.compareTo(new Date(year, 7, 22)) <= 0) {
      s"""<a href="/id:$sessionID/Cancer">Cancer</a>"""
    } else if (date.compareTo(new Date(year, 7, 23)) >= 0 &&
      date.compareTo(new Date(year, 8, 22)) <= 0) {
      s"""<a href="/id:$sessionID/Leo">Leo</a>"""
    } else if (date.compareTo(new Date(year, 8, 23)) >= 0 &&
      date.compareTo(new Date(year, 9, 22)) <= 0) {
      s"""<a href="/id:$sessionID/Virgo">Virgo</a>"""
    } else if (date.compareTo(new Date(year, 9, 23)) >= 0 &&
      date.compareTo(new Date(year, 10, 22)) <= 0) {
      s"""<a href="/id:$sessionID/Libra">Libra</a>"""
    } else if (date.compareTo(new Date(year, 10, 23)) >= 0 &&
      date.compareTo(new Date(year, 11, 21)) <= 0) {
      s"""<a href="/id:$sessionID/Scorpio">Scorpio</a>"""
    } else if (date.compareTo(new Date(year, 11, 22)) >= 0 &&
      date.compareTo(new Date(year, 12, 22)) <= 0) {
      s"""<a href="/id:$sessionID/Sagittarius">Sagittarius</a>"""
    } else if (date.compareTo(new Date(year, 12, 22)) >= 0 &&
      date.compareTo(new Date(year, 12, 31)) <= 0 ||
      date.compareTo(new Date(year, 1, 1)) >= 0 &&
      date.compareTo(new Date(year, 1, 19)) <= 0) {
      s"""<a href="/id:$sessionID/Capricorn">Capricorn</a>"""
    } else {
      errorPage(inputs, sessionID)
    }
  }

  /** Aquarius
    *  Method that retrieves the Aquarius horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Aquarius horoscope with a
    *         link to another search entry or the home page
    */
  def Aquarius(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Aquarius. Aquarius' don't deserve horoscopes.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }
  
  /** Pisces
    *  Method that retrieves the Pisces horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Pisces horoscope with a link
    *         to another search entry or the home page
    */
  def Pisces(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Pisces. You are way cooler than everybody else.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Aries
    *  Method that retrieves the Aries horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Aries horoscope with a link to
    *         another search entry or the home page
    */
  def Aries(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are an Aries. Horoscopes are useless, as they trick you</p>
      <p>into thinking you are something other than you really are.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Taurus
    *  Method that retrieves the Taurus horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Taurus horoscope with a link
    *         to another search entry or the home page
    */
  def Taurus(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Taurus. Welcome to the club (wooh!).
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Gemini
    *  Method that retrieves the Gemini horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Gemini horoscope with a link
    *         to another search entry or the home page
    */
  def Gemini(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You = Gemini. This means you have two faces. 'Nuf said.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search>Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Cancer
    *  Method that retrieves the Cancer horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Cancer horoscope with a link
    *         to another search entry or the home page
    */
  def Cancer(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are Cancer. Bleh.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Leo
    *  Method that retrieves the Leo horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Leo horoscope with a link to
    *         another search entry or the home page
    */
  def Leo(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Leo. Roar like the lion you are (RAWR XD)!
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Virgo
    *  Method that retrieves the Virgo horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Virgo horoscope with a link to
    *         another search entry or the home page
    */
  def Virgo(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Virgo. Don't know much about this horoscope. My bad.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Libra
    *  Method that retrieves the Libra horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Libra horoscope with a link to
    *         another search entry or the home page
    */
  def Libra(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Libra. You seem to have a pretty balanced lifestyle. Well done.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Scorpio
    *  Method that retrieves the Scorpio horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Scorpio horoscope with a link
    *         to another search entry or the home page
    */
  def Scorpio(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Scorpio. I will debug you.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Sagittarius
    *  Method that retrieves the Sagittarius horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Sagittarius horoscope with a
    *         link to another search entry or the home page
    */
  def Sagittarius(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Sagittarius. Neigh. Yay. Hooray.
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }

  /** Capricorn
    *  Method that retrieves the Capricorn horoscope
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the Capricorn horoscope with a link
    *         to another search entry or the home page
    */
  def Capricorn(inputs: Map[String, String], sessionID: String): String = {
    s"""<html><body><p>You are a Capricorn. You would make a great stew. Yummy!
      <a href="/id:$sessionID/newDate">Want to try a different date of birth?</a>
        <a href="http://localhost/Horoscope/search">Return to Horoscope Central</a>
        </p></body></html>"""
  }
}