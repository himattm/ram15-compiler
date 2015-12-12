package codegen;

import frontend.parser.generated.ParseException;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;
import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import visitor.BuildSymbolTableVisitor;



@RunWith(Parameterized.class)
public class CodeGenTestExtra {
    
    private File testProgram;
        
    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    // from parameters we defined in ramPrograms() method    
    public CodeGenTestExtra(File testProgram) {
        this.testProgram = testProgram;
    }
    
    @Parameterized.Parameters (name = "{index}: {0}")
    public static Collection ramPrograms() {
        
        return Arrays.asList(new Object[] {
            new File(System.getProperty("CODEGEN_DIR") + File.separator + "2-extracredit" + File.separator + "ClassLevel.ram15"),
            new File(System.getProperty("CODEGEN_DIR") + File.separator + "2-extracredit" + File.separator + "ArrayTest.ram15"),
            new File(System.getProperty("CODEGEN_DIR") + File.separator + "2-extracredit" + File.separator + "LinearSearch.ram15"),
        }); 
        
    }     
    
    @Test(timeout=10000)
    public void testRamProgram() throws FileNotFoundException, IOException, ParseException {
        
        String[] args = null;
        System.out.printf("Running javacc parser on %s ...\n", testProgram.toString());
        args = new String[] { testProgram.toString() };
                

        InputStream is = new FileInputStream(new File(args[0]));
        frontend.parser.generated.RamParser parser = new frontend.parser.generated.RamParser(is);
        syntaxtree.Program root = parser.Goal();
                    
        System.out.println("AST Created ...");
                    
        // build symbol table
        BuildSymbolTableVisitor v = new BuildSymbolTableVisitor();  
        root.accept(v); 
        System.out.println("Symbol Table built ...");
                    
        System.out.println("Generating Assembly Code ...");
        
        // prepare to capture System.output
        PrintStream originalOut = System.out;
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        try {
            root.accept(new visitor.CodeGenerator(System.out, v.getSymTab()));
        } finally {
            // restore normal System.output operation
            System.setOut(originalOut);            
        }        

        System.out.println("Saving Assembly File ...");
        PrintWriter writer = new PrintWriter(testProgram.getPath()+".s");
        writer.print(os + System.getProperty("line.separator"));
        writer.close();
                    
                    
        System.out.println("Running Assembly File in MIPS Simulator ...");
        Process p = Runtime.getRuntime().exec("java -jar " + System.getProperty("MARS_JAR") + " " + testProgram.getPath()+".s" + " " + "me");
        BufferedReader br1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                                        
        System.out.println("Saving Assembly Output ...");
        writer = new PrintWriter(testProgram.getPath()+".s"+".output");
        String s;
        while ((s = br1.readLine()) != null)    // write stdout
        {
            writer.print(s + System.getProperty("line.separator"));
            System.out.println(s);
        }
        while ((s = br2.readLine()) != null)    // write stderr
        {
            // writer.print(s + System.getProperty("line.separator"));
            System.err.println(s);
        }                    
        writer.close();
                    
        System.out.println("Comparing Against Expected Output ...");
        String s1 = org.apache.commons.io.FileUtils.readFileToString(new java.io.File(testProgram.getPath()+".correct"));
        String s2 = org.apache.commons.io.FileUtils.readFileToString(new java.io.File(testProgram.getPath()+".s"+".output"));
        s1 = s1.replaceAll("\\r\\n?", "\n");  // normalize line endings for Windows vs. Unix
        s2 = s2.replaceAll("\\r\\n?", "\n");
        assertEquals(s1, s2); 

    }
}
