package junit;

import junit.framework.TestCase;
import java.io.File;

/**
 * JUnit test case for the generated scanner.
 */

public class JFlexScannerTest extends TestCase {

    /**
     * Construct a JavaCCScannerTest object.
     */

    public JFlexScannerTest() {
        super("JUnit test case for the scanner");
    }

    /**
     * Run the scanner against each pass-test file under the folder specified by
     * PASS_TESTS_DIR property.
     */
    public void testPass() {
        System.out.println("In the testPass method.");
        File passTestsDir = new File(System.getProperty("PASS_TESTS_DIR"));
        File[] files = passTestsDir.listFiles();
        boolean errorHasOccurred = false;
        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].toString().endsWith(".ram15")) {
                String[] args = null;
                System.out.printf("Running scanner on %s ...\n", files[i].toString());
//                args = new String[] { "-t", files[i].toString() };             
//                JavaCCMain.main(args);
                
                args = new String[] { files[i].toString() };
                frontend.scanner.generated.Yylex.error_flag = false;
                frontend.scanner.generated.Yylex.main(args);
                
                System.out.printf("\n");

                assertFalse(frontend.scanner.generated.Yylex.error_flag);
                
                // true if any one test fails
                errorHasOccurred |= frontend.scanner.generated.Yylex.error_flag;
            }
        }

        // We want all tests to pass
        assertFalse(errorHasOccurred);
    }

    /**
     * Run the scanner against each fail-test file under the folder specified by
     * FAIL_TESTS_DIR property.
     */
    public void testFail() {
        System.out.println("In the testFail method.");
        File failTestsDir = new File(System.getProperty("FAIL_TESTS_DIR"));
        File[] files = failTestsDir.listFiles();
        boolean errorHasntOccurred = true;
        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].toString().endsWith(".ram15")) {
                String[] args = null;
                System.out.printf("Running scanner on %s ...\n", files[i].toString());                
                args = new String[] { files[i].toString() };
                frontend.scanner.generated.Yylex.error_flag = false;
                frontend.scanner.generated.Yylex.main(args);
                System.out.printf("\n");

                assertTrue(frontend.scanner.generated.Yylex.error_flag);
                
                // false if any test doesn't fail
                errorHasntOccurred &= frontend.scanner.generated.Yylex.error_flag;
            }
        }
        
        // We want all tests to fail
        assertTrue(errorHasntOccurred);        
    }
    
    /**
     * Entry point.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(JFlexScannerTest.class);
    }

}
