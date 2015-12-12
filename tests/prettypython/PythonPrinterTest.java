package prettypython;

import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;
import java.io.*;

/**
 * JUnit test case for the JavaCC parser.
 */

public class PythonPrinterTest extends TestCase {

    /**
     * Construct a JavaCCParserTest object.
     */

    public PythonPrinterTest() {
        super("JUnit test case for the python printer");
    }

    /**
     * Run the parser against each pass-test file under the folder specified by
     * PASS_TESTS_DIR property.
     */
    public void test() {
        File testDir = new File(System.getProperty("PYTHON_DIR"));
        File[] files = testDir.listFiles();
        
        // JavaCC raises ParseException if it detects a parser problem
        // JavaCC raises TokenMgrError if it detects a lexer problem
        boolean exceptionHasOccurred = false;
        
        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].toString().endsWith(".ram15")) {
                String[] args = null;
                System.out.printf("Running javacc parser on %s ...\n",
                        files[i].toString());
                args = new String[] { files[i].toString() };
                
                try {
                    // frontend.parser.generated.RamParser.main(args);
                    
                    InputStream is = new FileInputStream(new File(args[0]));
                    frontend.parser.generated.RamParser parser = new frontend.parser.generated.RamParser(is);
                    syntaxtree.Program root = parser.Goal();
                    
                    System.out.println("AST Created ...");
                    
                    
                    System.out.println("Generating Python Code ...");
                    // prepare to capture System.output
                    PrintStream originalOut = System.out;
                    OutputStream os = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(os);
                    System.setOut(ps);
                    
                    root.accept(new visitor.PythonPrintVisitor());
                    
                    // restore normal System.output operation
                    System.setOut(originalOut);
                    
                    System.out.println("Saving Python File ...");
                    PrintWriter writer = new PrintWriter(files[i].getPath()+".py");
                    writer.print(os + System.getProperty("line.separator"));
                    writer.close();
                    
                    System.out.println("Running Python File ...");
                    Process p = Runtime.getRuntime().exec("python "+files[i].getPath()+".py");
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                                        
                    System.out.println("Saving Python Output ...");
                    writer = new PrintWriter(files[i].getPath()+".py"+".output");
                    String s;
                    while ((s = br1.readLine()) != null)    // write stdout
                    {
                        writer.print(s + System.getProperty("line.separator"));
                        System.out.println(s);
                    }
                    while ((s = br2.readLine()) != null)    // write stderr
                    {
                        writer.print(s + System.getProperty("line.separator"));
                        System.err.println(s);
                    }                    
                    writer.close();
                    
                    System.out.println("Comparing Against Expected Output ...");
                    String s1 = org.apache.commons.io.FileUtils.readFileToString(new java.io.File(files[i].getPath()+".correct"));
                    String s2 = org.apache.commons.io.FileUtils.readFileToString(new java.io.File(files[i].getPath()+".py"+".output"));
                    s1 = s1.replaceAll("\\r\\n?", "\n");  // normalize line endings for Windows vs. Unix
                    s2 = s2.replaceAll("\\r\\n?", "\n");
                    assertEquals(s1, s2);   
                    
                } catch (frontend.parser.generated.ParseException e) {
                    exceptionHasOccurred = true;
                    System.out.println(e);
                } catch (frontend.parser.generated.TokenMgrError e) {
                    exceptionHasOccurred = true;
                    System.out.println(e);                    
                } catch (FileNotFoundException e) {
                    exceptionHasOccurred = true;
                    System.out.println(e);                    
                } catch (java.io.IOException e) {
                    exceptionHasOccurred = true;
                    System.out.println(e);  
                } catch (AssertionError e) {
                    exceptionHasOccurred = true;
                    System.out.println(e);
                } 
                System.out.printf("\n\n");

            }
        }

        // we shouldn't have caught any exceptions
        assertFalse(exceptionHasOccurred);
    }
    

    /**
     * Entry point.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PythonPrinterTest.class);
    }

}
