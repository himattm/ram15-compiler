package junit;

import junit.framework.TestCase;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * JUnit test case for the JavaCC parser.
 */

public class JavaCCParserTest extends TestCase {

    /**
     * Construct a JavaCCParserTest object.
     */

    public JavaCCParserTest() {
        super("JUnit test case for the parser");
    }

    /**
     * Run the parser against each pass-test file under the folder specified by
     * PASS_TESTS_DIR property.
     */
    public void testPass() {
        File passTestsDir = new File(System.getProperty("PASS_TESTS_DIR"));
        File[] files = passTestsDir.listFiles();
        
        // JavaCC raises ParseException if it detects a parser problem
        // JavaCC raises TokenMgrError if it detects a lexer problem
        boolean exceptionHasOccurred = false;
        
        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].toString().endsWith(".ram15")) {
                String[] args = null;
                System.out.printf("Running javacc parser on %s ...\n\n",
                        files[i].toString());
                args = new String[] { files[i].toString() };
                
                try {
                    frontend.parser.generated.RamParser.main(args);
                } catch (frontend.parser.generated.ParseException e) {
                    exceptionHasOccurred = true;
                    System.out.println(e);
                } catch (frontend.parser.generated.TokenMgrError e) {
                    exceptionHasOccurred = true;
                    System.out.println(e);                    
                } catch (FileNotFoundException e) {
                    System.out.println(e);                    
                }
                System.out.printf("\n\n");

            }
        }

        // we shouldn't have caught any exceptions
        assertFalse(exceptionHasOccurred);
    }
    
    /**
     * Run the parser against each pass-test file under the folder specified by
     * FAIL_TESTS_DIR property.
     */
    public void testFail() {
        File passTestsDir = new File(System.getProperty("FAIL_TESTS_DIR"));
        File[] files = passTestsDir.listFiles();
        
        boolean exceptionHasntOccurred = true;
        
        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].toString().endsWith(".ram15")) {
                String[] args = null;
                System.out.printf("Running javacc parser on %s ...\n\n",
                        files[i].toString());
                args = new String[] { files[i].toString() };
                
                try {
                    frontend.parser.generated.RamParser.main(args);
                    // we shouldn't get here because the parse should throw an exception
                    exceptionHasntOccurred = false;
                } catch (frontend.parser.generated.ParseException e) {
                    System.out.println(e);
                } catch (frontend.parser.generated.TokenMgrError e) {
                    System.out.println(e);                    
                } catch (FileNotFoundException e) {
                    System.out.println(e);                    
                }
                
                System.out.printf("\n\n");
            }
        }

        // all tests should throw an exception
        assertTrue(exceptionHasntOccurred);
    }    

    /**
     * Entry point.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(JavaCCParserTest.class);
    }

}
