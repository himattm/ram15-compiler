
/* 
 *    User Code Section
 *
 *    This section has code that will be copied to the top of the generated parser,
 *    used for package declarations, imports, etc.
 */
package frontend.scanner.generated;
 
%%

/* 
 * Options and Declarations Section
 * 
 * This section is for any jflex declarations and options settings.
 *
 */
 
%public       /* Makes the generated class public */
%standalone   /* Creates a main method in the generated class that expects an input file. 
                 Also instructs Scanner to send any unmatched input to java.lang.System.out and resume scanning,
                 instead of aborting. */


/* Any following code in between the symbols %{  %} 
 * is copied verbatim into the generated class. It can 
 * report when something interesting is found.
 */
%{
    public static boolean error_flag = false;

    /*  Call me to say what you found */
    public void found(String tokenClass) { 
       // System.out.println("\nFound |" + str + "| from text -->" + yytext() + "<--");

       System.out.println("< " + tokenClass + " , \"" + yytext() + "\" >");
       System.out.flush();
    }

    /* When the catchall rule matches at the end, we have a problem */
    public void error() {
        error_flag = true;    // we have an error, set a flag to remember this
        System.out.println("ERROR! " + yytext());
        System.out.flush();
    }
%}


/* Any abbreviations that we are defining. */
DIGIT = [0-9]

%%

/* 
 * Lexical Rules Section
 * 
 * Finally, patterns of interest and what to do upon finding them.
 */


"hello"			{ found("GREETING"); }
("0"|"1")+		{ found("BINARY_INT"); }
"("             { found("LPAREN"); }
")"             { found("RPAREN"); }
"["             { found("LSQBRACE"); }
"]"             { found("RSQBRACE"); }
// "{"             { found("LCURLY"); }
"}"             { found("RCURLY"); }
":"             { found("COLON"); }
";"             { found("SEMICOLON"); }
"."             { found("DOT"); }
"public"        { found("PUBLIC"); }
.               { error(); }