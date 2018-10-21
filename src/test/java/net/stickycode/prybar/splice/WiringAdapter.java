package net.stickycode.prybar.splice;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.Collection;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.stickycode.prybar.pivot.PrybarComponent;

public class WiringAdapter
    extends ClassVisitor {

  private Collection<String> components;

  private boolean component = false;

  public WiringAdapter(ClassWriter writer, Collection<String> components) {
    super(Opcodes.ASM6, writer);
    this.components = components;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    if (component = components.contains(name))
      super.visit(version, access, name, signature, superName, concatPrybarComponent(interfaces));
    else
      super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    return super.visitMethod(access, name, descriptor, signature, exceptions);
  }

  private String[] concatPrybarComponent(String[] interfaces) {
    if (interfaces != null)
      return new String[] { PrybarComponent.class.getName().replaceAll("\\.", "/") };

    return null;
  }

  @Override
  public void visitEnd() {
    if (component)
      visitWireMethod();
    super.visitEnd();

  }

  private void visitWireMethod() {
    MethodVisitor mv = visitMethod(ACC_PUBLIC, "wire", "(Lnet/stickycode/prybar/pivot/PrybarPivot;)V", null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(14, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitLdcInsn(Type.getType("Lnet/stickycode/prybar/pivot/examples/RootComponent1;"));
    mv.visitMethodInsn(INVOKEVIRTUAL, "net/stickycode/prybar/pivot/PrybarPivot", "find", "(Ljava/lang/Class;)Ljava/lang/Object;",
      false);
    mv.visitTypeInsn(CHECKCAST, "net/stickycode/prybar/pivot/examples/RootComponent1");
    mv.visitFieldInsn(PUTFIELD, "net/stickycode/prybar/pivot/examples/LeafComponent1", "root",
      "Lnet/stickycode/prybar/pivot/examples/RootComponent1;");
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitLineNumber(15, l1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "net/stickycode/prybar/pivot/examples/LeafComponent1", "root",
      "Lnet/stickycode/prybar/pivot/examples/RootComponent1;");
    Label l2 = new Label();
    mv.visitJumpInsn(IFNONNULL, l2);
    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLineNumber(16, l3);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitTypeInsn(NEW, "net/stickycode/prybar/pivot/PrybarComponentLookup");
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitLdcInsn(Type.getType("Lnet/stickycode/prybar/pivot/examples/RootComponent1;"));
    mv.visitLdcInsn("root");
    mv.visitMethodInsn(INVOKESPECIAL, "net/stickycode/prybar/pivot/PrybarComponentLookup", "<init>",
      "(Lnet/stickycode/prybar/pivot/PrybarComponent;Ljava/lang/Class;Ljava/lang/String;)V", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "net/stickycode/prybar/pivot/PrybarPivot", "failure",
      "(Lnet/stickycode/prybar/pivot/PrybarMessage;)V", false);
    mv.visitLabel(l2);
    mv.visitLineNumber(17, l2);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitInsn(RETURN);
    Label l4 = new Label();
    mv.visitLabel(l4);
    mv.visitLocalVariable("this", "Lnet/stickycode/prybar/pivot/examples/LeafComponent1;", null, l0, l4, 0);
    mv.visitLocalVariable("r", "Lnet/stickycode/prybar/pivot/PrybarPivot;", null, l0, l4, 1);
    mv.visitMaxs(6, 2);
    mv.visitEnd();
  }
}
