package net.stickycode.prybar.splice;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.stickycode.prybar.discovery.PrybarComponentDefinition;
import net.stickycode.prybar.discovery.PrybarComponentDependency;
import net.stickycode.prybar.pivot.PrybarComponent;
import net.stickycode.prybar.pivot.PrybarRuntime;

public class WiringAdapter
    extends ClassVisitor {

  private PrybarComponentDefinition component;

  private boolean defaultConstructorFound = false;

  public WiringAdapter(ClassWriter writer, PrybarComponentDefinition component) {
    super(Opcodes.ASM6, writer);
    this.component = component;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    super.visit(version, access, name, signature, superName, concatPrybarComponent(interfaces));
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions);
    if ("<init>".equals(name))
      if (descriptor == null)
        defaultConstructorFound = true;

    return visitMethod;
  }

  private String[] concatPrybarComponent(String[] interfaces) {
    if (interfaces != null)
      return new String[] { fullPathOfType(PrybarComponent.class) };

    return null;
  }

  private String fullPathOfType(Class<?> type) {
    return type.getName().replaceAll("\\.", "/");
  }

  @Override
  public void visitEnd() {
    if (!defaultConstructorFound)
      visitDefaultConstructor();
    visitWireMethod();
    super.visitEnd();
  }

  private void visitDefaultConstructor() {

  }

  private void visitWireMethod() {
    MethodVisitor mv = visitMethod(ACC_PUBLIC, "wire", wireSignature(), null, null);
    mv.visitCode();
    Label l0 = new Label();
    mv.visitLabel(l0);
    mv.visitLineNumber(12, l0);
    for (PrybarComponentDependency d : component.getComponentWiring())
      injectDependency(mv, d);
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLineNumber(14, l2);
    mv.visitInsn(RETURN);
    Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLocalVariable("this", component.getTypeReference(), null, l0, l3, 0);// "Lnet/stickycode/prybar/pivot/LeafComponent2;"
    mv.visitLocalVariable("prybar", "Lnet/stickycode/prybar/pivot/PrybarRuntime;", null, l0, l3, 1);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  private void injectDependency(MethodVisitor mv, PrybarComponentDependency d) {
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitTypeInsn(NEW, "net/stickycode/prybar/pivot/PrybarComponentLookup");
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitLdcInsn(Type.getType(d.getFieldTypeReference()));// "Lnet/stickycode/prybar/pivot/RootComponent;"
    mv.visitLdcInsn("root");
    mv.visitMethodInsn(INVOKESPECIAL, "net/stickycode/prybar/pivot/PrybarComponentLookup", "<init>",
      "(Lnet/stickycode/prybar/pivot/PrybarComponent;Ljava/lang/Class;Ljava/lang/String;)V", false);
    mv.visitMethodInsn(INVOKEINTERFACE, "net/stickycode/prybar/pivot/PrybarRuntime", "require",
      "(Lnet/stickycode/prybar/pivot/PrybarComponentLookup;)Ljava/lang/Object;", true);
    mv.visitTypeInsn(CHECKCAST, d.getFieldTypePath());// "net/stickycode/prybar/pivot/RootComponent"
    mv.visitFieldInsn(PUTFIELD, component.getTypePath(), // "net/stickycode/prybar/pivot/LeafComponent2"
      d.getFieldName(), // "root",
      d.getFieldTypeReference());// "Lnet/stickycode/prybar/pivot/RootComponent;");

  }

  private String wireSignature() {
    return "(L" + fullPathOfType(PrybarRuntime.class) + ";)V";
  }
}
