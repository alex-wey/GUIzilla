package guizilla.sol.client

import java.net.Socket
import java.io.InputStream
import java.io.OutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
  * Class that represents a client.
  * 
  * @param host - the host name
  */
class Client(host: String) {

  // assigns the port
  private val port: Integer = 8080

  /**
    * run 
    * Method that sends and receives data once connected to server
    * 
    * @param request - request to server
    * @path path - indicates to server relevant location
    * @return BufferedReader containing just the HTML returned by the request
    */
  def run(request: String, path: String): BufferedReader = {
    val socket = new Socket(host, port)
    println("Connecting to: " + host + ":8080")
    println("Connected")
    val iStream = socket.getInputStream
    val oStream = socket.getOutputStream
    val reader = new BufferedReader(new InputStreamReader(iStream))
    val writer = new BufferedWriter(new OutputStreamWriter(oStream))
    writer.write(request)
    writer.flush()
    socket.shutdownOutput()
    println("Requesting page and sending data (if need be): " + path)
    println("Request sent")
    var server_output = reader.readLine()
    while (server_output.trim() != "") {
      try {
        if (server_output.split(" ")(2).equals("OK")) {
          println("Server responded with status OK")
        }
      } catch {
        case iob: IndexOutOfBoundsException =>
      }
      server_output = reader.readLine()
    }
    reader
  }
}