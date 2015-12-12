package junit;

import frontend.parser.generated.RamParser;
import org.junit.*;
import junit.framework.TestCase;
import java.io.File;
import java.io.FileNotFoundException;
import symboltable.Table;
import syntaxtree.Program;
import visitor.BuildSymbolTableVisitor;
import visitor.TypeCheckVisitor;



/**
 * JUnit test case for the JavaCC parser.
 */

public class LinearSearchTest extends TestCase {

    Table symTab;
    
    /**
     * Construct a JavaCCParserTest object.
     */

    public LinearSearchTest() {
        super("JUnit test case for the parser");
    }

    @Before
    public void setUp() throws Exception {
        File f;
        f = new File(System.getProperty("TEST_FILE"));

        java.io.InputStream is = new java.io.FileInputStream(f);
        RamParser parser = new RamParser( is ) ;
        Program root = parser.Goal();

        System.out.println("Program lexed and parsed successfully");
        System.out.println("Abstract syntax tree built");

        // build symbol table
        BuildSymbolTableVisitor v = new BuildSymbolTableVisitor(); 
        root.accept(v); // build symbol table
        System.out.println("Symbol Table built");

        symTab = v.getSymTab(); 
        
        // perform type checking
        root.accept(new TypeCheckVisitor(v.getSymTab()));
        System.out.println("Semantic Analysis: Type Checking complete"); 
    }
    

    @Test
    public void testErrors() {
        assertFalse(symTab.anyErrors());
    }
    
    @Test
    public void testNumClasses() {
        assertTrue(symTab.numClasses() == 2);
    }
    
    @Test
    public void testContainsClass() {
        assertTrue(symTab.containsClass("LS"));
        assertTrue(symTab.containsClass("LinearSearch"));
    }
    
    @Test
    public void testClassA() {
        assertTrue(symTab.getClass("LinearSearch").numMethods() == 1);
        assertTrue(symTab.getClass("LinearSearch").numGlobals() == 0);
        assertTrue(symTab.getClass("LinearSearch").containsMethod("main"));
    }
    
    @Test
    public void testClassB() {
        assertTrue(symTab.getClass("LS").numMethods() == 4);
        assertTrue(symTab.getClass("LS").numGlobals() == 2);
        assertTrue(symTab.getClass("LS").containsVar("number"));
        assertTrue(symTab.getClass("LS").containsVar("size"));
        assertTrue(symTab.getClass("LS").containsMethod("Start"));
        assertTrue(symTab.getClass("LS").containsMethod("Print"));
        assertTrue(symTab.getClass("LS").containsMethod("Search"));
        assertTrue(symTab.getClass("LS").containsMethod("Init"));
    }
    
    @Test
    public void testClassBMethodA() {
        assertTrue(symTab.getClass("LS").getMethod("Start").numParams() == 1);
        assertTrue(symTab.getClass("LS").getMethod("Start").getParamAt(0).id().equals("sz"));
        assertTrue(symTab.getClass("LS").getMethod("Start").containsVar("aux01"));
        assertTrue(symTab.getClass("LS").getMethod("Start").containsVar("aux02"));
        assertFalse(symTab.getClass("LS").getMethod("Start").containsVar("aux03"));        
    }

    /**
     * Entry point.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(LinearSearchTest.class);
    }

}
