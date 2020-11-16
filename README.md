# sf-study-parser
Test repository. Contains implementation of test project form 14, 15, 16 modules in scope of SkillFactory 
Java Developer training course.

The programs parses a web page, which is search results page and saves search results into file.
 
The program takes one argument - URL string which is posted by the web page to issue the search.
To get this string, open the search parameters page, enter a keyword and period, then launch the search on the web page.
When the search results page opens, note the URL of the search results page - it is the program parameter.
For example the search for vacations dated within last 365 days by "Java" keyword will look like
https://www.sql.ru/forum/actualsearch.aspx?search=Java&sin=1&bid=66&dt=365

The program logs its progress to file named "parser.log.0.0.txt" (%h/parser.log.%u.%g.txt) in user home directory 
as well as to the console.

The program saves search results to the file named results.txt in the same directory where jar file locates or 
project home folder if you launch it from IDEA.  

  
 
