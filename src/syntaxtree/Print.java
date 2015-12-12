package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Print extends Statement {
  public ExpList el;

  public Print(ExpList ael) {
    el=ael; 
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
