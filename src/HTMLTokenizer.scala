package guizilla.src

import java.io.Reader
import java.io.StringReader
import java.io.IOException

/**
  * An exception that is thrown when there is an issue during tokenization
  */
class LexicalException(message: String) extends Exception(message)

/**
  * A tokenizer class for simple HTML files defined in the project PDF
  *
  * @param r - A reader ready to read an HTML file
  */
class HTMLTokenizer(reader: Reader) {

  val NullCharacter = '\u0000'
  var buffer = NullCharacter
  var currentToken: Option[Token] = None
  var done: Boolean = false

  /**
    * @return the current token, if it exists; None, otherwise
    */
  def current: Option[Token] = currentToken

  /**
    * Advances the tokenizer to the next token
    *
    * @throws LexicalException if a problem occurs while tokenizing
    * @throws IOException if there is a problem reading from the file
    */
  def advance() { currentToken = getNextToken() }

  /**
    * Gets the next character in the file (perhaps from a buffer)
    *
    * @throws IOException
    * @return The next character or (-1).toChar if EOF was reached
    */
  private def getChar(): Char = {
    //If there is a character in the buffer, return that.
    if (buffer != NullCharacter) {
      val character = buffer
      buffer = NullCharacter
      return character
    }

    // If the reader is finished, there is nothing else to tokenize
    if (!reader.ready) {
      done = true
      return (-1).toChar
    }

    //Read the character
    reader.read().toChar
  }

  /**
    * Reads characters from the file (and perhaps the buffer) until reaching a certain character
    *
    * @param until - The character to stop at
    * @param eatUntilChar - If true, the next call to getString or getChar will not include
    *                       the character passed in. Otherwise, the character will be trashed
    *                       after it is found.
    *
    * @throws IOException
    * @return The contents until the specified character or null if EOF was reached
    */
  private def getString(until: Char, eatUntilChar: Boolean): String = {
    val sb = new StringBuilder()

    //Empty the buffer
    if (buffer != NullCharacter) {
      sb += buffer
      buffer = NullCharacter
    }

    //If the reader is finished, there is nothing else to tokenize
    if (!reader.ready) {
      done = true
      if (sb.length != 0) {
        return sb.toString
      }
      return null
    }

    //Read characters until hitting the specified character or EOF
    var c = reader.read().toChar
    while (c != until && c != (-1).toChar) {
      sb += c
      c = reader.read().toChar
    }

    //If we stopped due to EOF, we are done after this return
    if (c == (-1).toChar) {
      done = true
      if (sb.length == 0) return null
    }

    //If we shouldn't eat the until character, we must save it in the buffer
    if (!eatUntilChar) buffer = c

    sb.toString
  }

  /**
    * @throws LexicalException
    * @throws IOException
    * @return The next token from the file
    *
    */
  private def getNextToken(): Option[Token] = {
    if (done)
      throw new LexicalException("Cannot get next token because done tokenizing")
    val c = getChar()

    //(-1).toChar implies EOF was reached
    if (c == (-1).toChar) return Some(Eof)

    c match {
      //Tag case
      case '<' =>
        //Find the content inside of "<...>", eating the '>'
        val tag: String = getString('>', true)
        //Split on whitespace (but not on whitespace within "...")
        val words: List[String] = splitOnWhitespaces(tag)

        //Match possible tags and tokenize accordingly
        Some(words(0) match {
          case "html"  => tokenizeOpenHTML(words)
          case "/html" => tokenizeCloseHTML(words)
          case "body"  => tokenizeOpenBody(words)
          case "/body" => tokenizeCloseBody(words)
          case "form"  => tokenizeOpenForm(words)
          case "/form" => tokenizeCloseForm(words)
          case "p"     => tokenizeOpenParagraph(words)
          case "/p"    => tokenizeCloseParagraph(words)
          case "a"     => tokenizeOpenLink(words)
          case "/a"    => tokenizeCloseLink(words)
          case "input" => tokenizeInput(words)
          case _ =>
            throw new LexicalException("Unrecognized " + words(0) + " tag")
        })
      //Text case
      case _ =>
        //Get all the text until the next '<', but do not eat it
        val text = getString('<', false)

        //If the text is null, EOF was reached
        if (text == null) return Some(Eof)
        //Ignore only whitespace between tokens
        else if (isJustWhitespace(text)) return getNextToken()

        //Make sure to include the first character in the text
        Some(Text(c + text))
    }
  }

  /**
    * Makes an open HTML token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                Should be List("html")
    *
    * @throws LexicalException - If the list is not a single element list
    * @return An open HTML token
    */
  private def tokenizeOpenHTML(words: List[String]): Token = {
    if (words.length != 1)
      throw new LexicalException("Open HTML tags cannot have attributes")

    OpenHTML
  }

  /**
    * Makes a close HTML token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                Should be List("/html")
    *
    * @throws LexicalException - If the list is not a single element list
    * @return A close HTML token
    */
  private def tokenizeCloseHTML(words: List[String]): Token = {
    if (words.length != 1)
      throw new LexicalException("Close HTML tags cannot have attributes")

    CloseHTML
  }

  /**
    * Makes an open body token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                Should be List("body")
    *
    * @throws LexicalException - If the list is not a single element list
    * @return An open body token
    */
  private def tokenizeOpenBody(words: List[String]): Token = {
    if (words.length != 1)
      throw new LexicalException("Open body tags cannot have attributes")

    OpenBody
  }

  /**
    * Makes a close body token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                Should be List("/body")
    *
    * @throws LexicalException - If the list is not a single element list
    * @return A close body token
    */
  private def tokenizeCloseBody(words: List[String]): Token = {
    if (words.length != 1)
      throw new LexicalException("Close body tags cannot have attributes")

    CloseBody
  }

  /**
    * Makes an open form token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                List assumed to start with the element "form"
    *
    * @throws LexicalException - If the list is not a three element list or
    *                            if the second two elements aren't 'method="post"'
    *                            and 'action="..."' (either order)
    * @return An open form token
    */
  private def tokenizeOpenForm(words: List[String]): Token = {
    if (words.length != 3)
      throw new LexicalException("Open form tags require exactly 2 attributes")

    //Figure out which of the two attributes are method and action
    var methodIndex = -1
    var actionIndex = -1
    if (words(1).startsWith("method=")) {
      methodIndex = 1
      actionIndex = 2
    } else {
      methodIndex = 2
      actionIndex = 1
    }

    //Check for proper attributes
    if (!words(methodIndex).equals("method=\"post\""))
      throw new LexicalException("Forms must have a method attribute with the value \"post\"")

    val action = words(actionIndex)
    if (!action.startsWith("action=\"") || !action.endsWith("\""))
      throw new LexicalException("Forms require a well-formed action attribute, received: " + action)

    //Strip 'action="' from the beginning and '"' from the end
    OpenForm(action.substring(8, action.length - 1))
  }

  /**
    * Makes a close body token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                Should be List("/body")
    *
    * @throws LexicalException - If the list is not a single element list
    * @return A close body token
    */
  private def tokenizeCloseForm(words: List[String]): Token = {
    if (words.length != 1)
      throw new LexicalException("Close form tags cannot have attributes")

    CloseForm
  }

  /**
    * Makes an open paragraph token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                Should be List("p")
    *
    * @throws LexicalException - If the list is not a single element list
    * @return An open paragraph token
    */
  private def tokenizeOpenParagraph(words: List[String]): Token = {
    if (words.length != 1)
      throw new LexicalException("Open paragraph tags cannot have attributes")

    OpenParagraph
  }

  /**
    * Makes a close paragraph token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                Should be List("/p")
    *
    * @throws LexicalException - If the list is not a single element list
    * @return A close paragraph token
    */
  private def tokenizeCloseParagraph(words: List[String]): Token = {
    if (words.length != 1)
      throw new LexicalException("Close paragraph tags cannot have attributes")

    CloseParagraph
  }

  /**
    * Makes an open link token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                List is assumed to start with the element "a"
    *
    * @throws LexicalException - If the list is not a two element list or
    *                            if the second element isn't 'href="..."'
    * @return An open link token
    */
  private def tokenizeOpenLink(words: List[String]): Token = {
    if (words.length != 2)
      throw new LexicalException("Open link tags require exactly 1 attribute, href")

    //Check the href attribute is well-formed
    val href = words(1)
    if (!href.startsWith("href=\"") || !href.endsWith("\""))
      throw new LexicalException("Open link tags require a well-formed href attribute: " + href)

    //Strip the 'href="' from the beginning and '"' from the end
    OpenLink(href.substring(6, href.length - 1))
  }

  /**
    * Makes a close link token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                Should be List("/link")
    *
    * @throws LexicalException - If the list is not a single element list
    * @return A close link token
    */
  private def tokenizeCloseLink(words: List[String]): Token = {
    if (words.length != 1)
      throw new LexicalException("Close link tags cannot have attributes")

    CloseLink
  }

  /**
    * Makes either a submit or text input token from a list of words
    *
    * @param words - The list of words created from splitting the contents of "<...>" on spaces
    *                List is assumed to start with the element "input"
    *
    * @throws LexicalException - If the list is not a three element list or
    *                            there is no element 'type="submit"' or 'type="text"'
    *                            or the final element is not the proper attribute for the input type:
    *                              submit - 'value="submit"'
    *                              text - 'name="..."'
    * @return A close paragraph token
    */
  private def tokenizeInput(words: List[String]): Token = {
    //Input tags are self-closing and thus contain a slash we do not want when tokenizing
    if (words.length != 3 && !(words.length == 4 && words(3) == "/"))
      throw new LexicalException("Input tags require 2 attributes, found " + words.length)

    //Figure out which of the two attributes are type
    var typeIndex = -1
    var otherIndex = -1
    if (words(1).startsWith("type=\"")) {
      typeIndex = 1
      otherIndex = 2
    } else {
      typeIndex = 2
      otherIndex = 1
    }

    //Check which type of input this is
    words(typeIndex) match {
      case "type=\"submit\"" =>
        val value = words(otherIndex)

        if (!value.equals("value=\"submit\""))
          throw new LexicalException(
            "Submit inputs must contain a value attribute with the value \"submit\": " + value)

        Submit
      case "type=\"text\"" =>
        val name = words(otherIndex)

        if (!name.startsWith("name=\"") || !name.endsWith("\""))
          throw new LexicalException("Text inputs require a well-formed name attribute: " + name)

        //Strip the 'name="' from the beginning and '"' from the end
        Input(name.substring(6, name.lastIndexOf('"')))
      case _ =>
        throw new LexicalException("The only allowed input types are submit and text")
    }
  }

  /**
    * @param s - a String
    *
    * @return True if the the string consists only of whitespace, false otherwise
    */
  private def isJustWhitespace(s: String): Boolean = {
    val charArray = s.toCharArray
    for (c <- charArray)
      if (!Character.isWhitespace(c)) return false
    true
  }

  /**
    * Splits a string by white spaces. White spaces within quotations will not be split on.
    * '/' at the end of strings will be stripped. Empty strings (after '/' is stripped) will
    * not be returned.
    *
    * @param s - The string to be split
    *
    * @return A list of strings extracted from the parameter string
    * @throws IOException
    */
  private def splitOnWhitespaces(s: String): List[String] = {
    val reader = new StringReader(s)
    var list: List[String] = List()
    var c = (-1).toChar

    //Read until EOF
    do {
      c = reader.read().toChar

      //If the character is not whitespace, we begin constructing a "word"
      if (!Character.isWhitespace(c)) {
        val sb = new StringBuilder()
        //This Boolean will save the state of whether or not we are inside quotations
        var splitOnWhitespace = true

        do {
          //Add the current character to the "word"
          sb += c

          //If we encounter a quotation, we should either stop splitting on
          //whitespace characters or start again.
          if (c == '"') splitOnWhitespace = !splitOnWhitespace

          //Read the next character
          c = reader.read().toChar

          //If we encountered EOF, stop.
          //If we are splitting on whitespace (not inside quotations), we must stop
          //  if the next character is whitespace.
          //If we are not splitting on whitespace, we may continue.
        } while (c != (-1).toChar &&
          ((splitOnWhitespace && !Character.isWhitespace(c)) || !splitOnWhitespace))

        var element = sb.toString

        //Strip '/' off the end of the "word"
        if (element.endsWith("/"))
          element = element.substring(0, element.length - 1)

        //Only add the element if it is non-empty
        if (!element.isEmpty())
          list = element :: list
      }
    } while (c != (-1).toChar)

    //We always added to the front of the list, so we must reverse the order
    list.reverse
  }

}
