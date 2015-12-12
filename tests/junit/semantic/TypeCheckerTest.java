package junit; 

import frontend.parser.generated.ParseException;
import frontend.parser.generated.RamParser;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
 
import org.junit.Test;
import org.junit.Before;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import org.junit.runner.notification.Failure;
import symboltable.Table;
import syntaxtree.Program;
import visitor.BuildSymbolTableVisitor;
import visitor.TypeCheckVisitor;


@RunWith(Parameterized.class)
public class TypeCheckerTest {
    
    private File testProgram;
    private Boolean shouldPass;
        
    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    // from parameters we defined in ramPrograms() method    
    public TypeCheckerTest(File testProgram, Boolean shouldPass) {
        this.testProgram = testProgram;
        this.shouldPass = shouldPass;
    }
    
    @Parameterized.Parameters (name = "{1} {index}: {0}")
    public static Collection ramPrograms() {
        
        return Arrays.asList(new Object[][] {
            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "1" + File.separator + "Bool.ram15"), true},
            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "1" + File.separator + "HelloWorld.ram15"), true},
            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "1" + File.separator + "InfiniteLoop.ram15"), true},
            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "1" + File.separator + "LinearSearch.ram15"), true},
            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "1" + File.separator + "Logical.ram15"), true},
            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "1" + File.separator + "Program2.ram15"), true},
            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "1" + File.separator + "Array.ram15"), false},            
            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "1" + File.separator + "ParsePass2.ram15"), false},
            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "1" + File.separator + "pass1.ram15"), false},
            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "1" + File.separator + "Sigma.ram15"), false}            
//            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "2" + File.separator + "AssignIntegerWorks.ram15"), true},
//            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "2" + File.separator + "InstanceTest.ram15"), true},
//            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "2" + File.separator + "Pass1.ram15"), true},
//            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "2" + File.separator + "print5.ram15"), true},
//            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "2" + File.separator + "Program1.ram15"), true},
//            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "2" + File.separator + "Program2.ram15"), true},
//            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "2" + File.separator + "Semantic1.ram15"), true},
//            { new File(System.getProperty("PASS_TESTS_DIR") + File.separator + "2" + File.separator + "Valid2.ram15"), true},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "bad_names.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "Fail3.ram15"), false},            
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "file4.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "FunWithArrays.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "Invalid-And.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "Invalid-Undefined-Var.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "Semantic3.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "SemanticErr1.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "SemanticErr3.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "SemanticError1.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "SemanticError2.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "Sigma.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "TokenThirdTest.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "UnknownTypes.ram15"), false},
//            { new File(System.getProperty("FAIL_TESTS_DIR") + File.separator + "2" + File.separator + "wrong_number_params.ram15"), false}
        }); 
        
    }    

   
    @Test(timeout=1000)
    public void testRamProgram() throws FileNotFoundException, IOException, ParseException {
        
        String[] args = null;
        System.out.println("*******************************************\n\n\n\n");
        System.out.printf("Running javacc parser on %s ...\n", testProgram.toString());
        args = new String[] { testProgram.toString() };
                
        InputStream is = new FileInputStream(new File(args[0]));
        frontend.parser.generated.RamParser parser = new frontend.parser.generated.RamParser(is);
        syntaxtree.Program root = parser.Goal();
                    
        System.out.println("Program lexed and parsed successfully");
        System.out.println("Abstract syntax tree built");

        // build symbol table
        BuildSymbolTableVisitor v = new BuildSymbolTableVisitor(); 
        root.accept(v); // build symbol table
        System.out.println("Symbol Table built");

        Table symTab = v.getSymTab(); 
        
        // perform type checking
        root.accept(new TypeCheckVisitor(v.getSymTab()));
        System.out.println("Semantic Analysis: Type Checking complete");         
        
        System.out.println(!v.getSymTab().anyErrors() + " " + this.shouldPass);
        assertEquals(!v.getSymTab().anyErrors(), this.shouldPass); 
    }
}