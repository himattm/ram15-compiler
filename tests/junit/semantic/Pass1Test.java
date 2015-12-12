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

public class Pass1Test extends TestCase {

    Table symTab;
    
    /**
     * Construct a JavaCCParserTest object.
     */

    public Pass1Test() {
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
        assertTrue(symTab.anyErrors());
    }
    
    @Test
    public void testNumClasses() {
        assertTrue(symTab.numClasses() == 2);
    }
    
    @Test
    public void testContainsClass() {
        assertTrue(symTab.containsClass("Test1"));
        assertTrue(symTab.containsClass("Test1Class"));
    }
    
    @Test
    public void testClassA() {
        assertTrue(symTab.getClass("Test1Class").numMethods() == 1);
        assertTrue(symTab.getClass("Test1Class").numGlobals() == 0);
        assertTrue(symTab.getClass("Test1Class").containsMethod("PrintNums"));
    }
    
    @Test
    public void testClassB() {
        assertTrue(symTab.getClass("Test1").numMethods() == 1);
        assertTrue(symTab.getClass("Test1").numGlobals() == 0);
        assertTrue(symTab.getClass("Test1").containsMethod("main"));
    }
    
    @Test
    public void testClassAMethodA() {
        assertTrue(symTab.getClass("Test1Class").getMethod("PrintNums").numParams() == 0);
        assertTrue(symTab.getClass("Test1Class").getMethod("PrintNums").containsVar("numbers"));       
    }

    /**
     * Entry point.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(Pass1Test.class);
    }

}
