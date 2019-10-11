package guizilla.src.parser

import guizilla.src.Token

/**
  * An exception thrown when there is an unexpected token.
  * Will be an exception with the message:
  *  "Expected " + expected + ", but instead found " + found.toString
  *
  * @param expected - a string containing the expected token names
  * @param found - the token that was found
  */
class ParseException(expected: String, found: Option[Token])
  extends Exception("Expected " + expected + ", but instead found " + found)
