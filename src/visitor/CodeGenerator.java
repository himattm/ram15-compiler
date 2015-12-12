package visitor;

import syntaxtree.*;
import symboltable.*;

import java.util.Enumeration;


public class CodeGenerator extends DepthFirstVisitor {

    private java.io.PrintStream out;
    private static int nextLabelNum = 0;
    private Table symTable;

    private StringBuilder dataString = new StringBuilder("");

    RamClass currentClass;
    RamVariable currentVariable;

    public CodeGenerator(java.io.PrintStream o, Table st) {
        out = o;
        symTable = st;
    }

    private void emit(String s) {
        out.println("\t" + s);
    }

    /**
     * Emit assembly with a comment
     */
    private void emit(String s, String comment) {
        out.println("\t" + s + "\t\t# " + comment);
    }

    private void emitLabel(String l) {
        out.println(l + ":");
    }

    private void emitComment(String s) {
        out.println("\t" + "#" + s);
    }

    // MainClass m;
    // ClassDeclList cl;
    public void visit(Program n) {

        emit(".text");
        emit(".globl main");

        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            n.cl.elementAt(i).accept(this);
        }

        emit("");
        emit(".data");
        out.println(dataString.toString());
    }

    // Identifier i1, i2;
    // Statement s;
    public void visit(MainClass n) {
        symTable.addClass(n.i1.toString());
        TypeCheckVisitor.currClass = symTable.getClass(n.i1.toString());
        symTable.getClass(n.i1.s).addMethod("main", new IdentifierType("void"));
        TypeCheckVisitor.currMethod = symTable.getClass(n.i1.toString()).getMethod("main");
        symTable.getMethod("main",
                TypeCheckVisitor.currClass.getId()).addParam(n.i2.toString(), new IdentifierType("String[]"));

        emitLabel("main");

        emitComment("begin prologue -- main");
        emit("subu $sp, $sp, 24", "stack frame is at least 24 bytes");
        emit("sw $fp, 4($sp)", "save caller's frame pointer");
        emit("sw $ra, 0($sp)", "save return address");

        emit("addi $fp, $sp, 20", "set up main's frame pointer");
        emitComment("end prologue -- main");

        n.s.accept(this);

        emitComment("begin epilogue -- main");
        emit("lw $ra, 0($sp)", "restore return address");
        emit("lw $fp, 4($sp)", "restore caller's frame pointer");
        emit("addi $sp, $sp, 24", "pop the stack");
        emitComment("end epilogue -- main");
        
        /*
        emit("jr $ra");   // SPIM: how to end programs
        emit("\n");       // SPIM: how to end programs 
        */

        emit("li $v0, 10");   // MARS: how to end programs
        emit("syscall");      // MARS: how to end programs

        TypeCheckVisitor.currMethod = null;

    }

    // TODO
    @Override
    public void visit(IntegerLiteral n) {
        emit("li $v0, " + n.i, "load int value into $v0");
    }

    @Override
    public void visit(Print n) {
        emitComment("begin print call");
        for (int i = 0; i < n.el.size(); i++) {
            n.el.elementAt(i).accept(this);  // resulting int saved in $v0
            emit("move $a0, $v0", "move int to $a0 to be accessed by syscall");
            emit("li $v0, 1", "set syscall to print");
            emit("syscall", "print call");

            if (i < n.el.size() - 1) {
                emit("la $a0, space", "move space to syscall arg");
                emit("li $v0, 4", "set syscall to print string");
                emit("syscall", "print space");
            }
        }
        emitComment("end print");
    }

    @Override
    public void visit(Println n) {
        emitComment("begin println");
        for (int i = 0; i < n.el.size(); i++) {
            n.el.elementAt(i).accept(this);  // resulting int saved in $v0
            emit("move $a0, $v0", "move int to syscall arg");
            emit("li $v0, 1", "set syscall to print");
            emit("syscall", "print int");

            if (i < n.el.size() - 1) {
                emit("la $a0, space", "move space to syscall arg");
                emit("li $v0, 4", "set syscall to print string");
                emit("syscall", "print space");
            }
        }

        emit("la $a0, newline", "move newline to syscall arg");
        emit("li $v0, 4", "set syscall to print string");
        emit("syscall", "print newline");

        emitComment("end println");
    }

    @Override
    public void visit(Plus n) {
        emitComment("begin plus");
        n.e1.accept(this);
        emit("subu, $sp, #sp, 4", "move stack pointer");
        emit("sw $v0, ($sp)", "save e1 to stack");

        n.e2.accept(this);
        emit("lw $t1, ($sp)", "load e1 from stack");
        emit("addu $sp, $sp, 4", "pop e2 from stack");

        emit("add $v0, $t1, $v0", "add and store in $v0");

        emitComment("end plus");
    }

    @Override
    public void visit(Minus n) {
        emitComment("begin minus");
        n.e1.accept(this);
        emit("subu, $sp, #sp, 4", "move stack pointer");
        emit("sw $v0, ($sp)", "save e1 to stack");

        n.e2.accept(this);
        emit("lw $t1, ($sp)", "load e1 from stack");
        emit("addu $sp, $sp, 4", "pop e2 from stack");

        emit("sub $v0, $t1, $v0", "subtract and store in $v0");

        emitComment("end minus");
    }

    @Override
    public void visit(Times n) {
        emitComment("begin times");
        n.e1.accept(this);
        emit("subu, $sp, #sp, 4", "move stack pointer");
        emit("sw $v0, ($sp)", "save e1 to stack");

        n.e2.accept(this);
        emit("lw $t1, ($sp)", "load e1 from stack");
        emit("addu $sp, $sp, 4", "pop e2 from stack");

        emit("mul $v0, $t1, $v0", "multiply and store in $v0");

        emitComment("end times");
    }

    @Override
    public void visit(True n) {
        
    }

    @Override
    public void visit(False n) {

    }

    @Override
    public void visit(If n) {

    }

    @Override
    public void visit(And n) {

    }

    @Override
    public void visit(Or n) {

    }

    @Override
    public void visit(LessThan n) {

    }

    @Override
    public void visit(Equals n) {

    }

    @Override
    public void visit(Not n) {

    }

    @Override
    public void visit(Call n) {

    }

    @Override
    public void visit(MethodDecl n) {

    }

    @Override
    public void visit(ClassDeclSimple n) {

    }

    @Override
    public void visit(Identifier n) {

    }

    @Override
    public void visit(IdentifierExp n) {

    }

    @Override
    public void visit(Assign n) {
        n.e.accept(this); // result saved in $v0
        emit("subu $sp, $sp, 4", "increase stack by one word");
        emit("sw $v0, ($sp)", "save exp result to stack");

        n.i.accept(this); // result saved in $v0
        emit("lw $t1, ($sp)", "load exp result from stack");
        emit("addu $sp, $sp, 4", "pop exp result from stack");

        emit("sw $t1, ($v0)", "assign value to memory address of identifier");
    }

}
