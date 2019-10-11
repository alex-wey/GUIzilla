Instructions for use
-
Once the program is initiated, the GUI home page will appear, prompting the user with the optionality of going to the TA servers or the pages I've created. All links are denoted by blue, and all page text is denoted by black. By clicking the Back button, a user is able to return to the previous page (unless a user is on the home page of Guizilla). A user can enter a URL in the address bar and press the submit button (to the right of the address bar) to navigate to a desired page. By clicking the Quit button, a user terminates the program.

By clicking a link, the program will navigate the user to a new page. A page can contain page text, links, and forms. By navigating to a form, a user can enter a text input and/or submit that form presented on the page by clicking the Submit
button located below the text input. By clicking the Back button, a user can return to that form with their previous inputs still there. These action events can be used for navigation and interactivity.

The correct syntax for a URL is either "http://hostname/path" or just "/path" if a user has already connected to a server.

Overview of design
-
My program is centered around my GUIBrowser class and is designed using client-server architecture, in which the client sends GET or POST requests to which the server responds with HTML that is then parsed into a list of HTML elements to be rendered by the GUIBrowser.

The GUIBrowser handles all of the button actions in the program. The Back and Submit button functionality relies on the getPage method, which is essential in the program since it facilitates the interaction between the client and other servers in order to access the necessary pages.

Regarding the client-server model, the Client class is responsible for returning the HTML provided by the server, which is done through the run method, which in short instantiates a socket to be accepted and reads in the server's output through the socket connection. The Server class is broken up into two primary methods, listener and dispatcher. The listener method listens for incoming connections and provides relevant elements from a request to the dispatcher. This method takes in these elements, invokes the proper method, and returns the HTML to be sent back to the client. All the while, I are keep track of multiple states in my program by assigning session IDs to each page.

The HTMLElement class and all of its subclasses represent the objects formed from parsing a page. Each of these classes contain a render method, which provides the template for the manner in which that element ought to be displayed on the GUI browser. In addition, the  render methods handle events, meaning that they also orient the response to a user input on that specific element. For example, clicking a link will go to the page it links to.

The RunApplication class is responsible for handling the JavaFX application by loading the fxml_main.fxml, loading the root, creating the controller object, setting the stage, showing that stage, and finally rendering the home page.

Regarding the pages, I have five: Search, Horoscope, Application, FirstLast, and BadPage. Except for BadPage (which is a page used to test malformed HTML), each of these pages demonstrate the understanding of stateful dynamically-generated web pages.

The Search page does a beautiful job displaying up to the top ten results for a given free-text query. By clicking one of the titles, represented as links, a user is able to view the text of the article. A user can go back to the results by clicking the Back button, and clicking once more will return the user to the home page.

Horoscope is a creative page that prompts a user to input their date of birth to discover what their astrological sign is. Once given a valid date of birth, the program will prompt the user's sign as a link, which once clicked can navigate to a daily horoscope. A user is able to return to the home page or try another date of birth. The Horoscope class utilizes a combination of forms and links.

Application is an exact replica of how to apply to Google. The user is prompted with multiple forms at once (asking for the full name and GPA), which is then processed and navigates the user to a new page prompting which job they are applying for. After submitting that form, a final response page will appear, containing a link to go back to the Application home page. The Application class utilizes links and a series of sequential forms.
