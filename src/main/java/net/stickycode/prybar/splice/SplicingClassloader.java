package net.stickycode.prybar.splice;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import net.stickycode.prybar.discovery.PrybarComponentDefinition;
import net.stickycode.prybar.discovery.PrybarDefinitionRegistry;

public class SplicingClassloader
    extends ClassLoader {

  private PrybarDefinitionRegistry components;

  public SplicingClassloader(ClassLoader classLoader, PrybarDefinitionRegistry components) {
    super("splicer", classLoader);
    this.components = components;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    if (!components.hasComponent(name))
      return super.loadClass(name);

    ClassReader classReader = classReader(name);
    ClassWriter w = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
    WiringAdapter adapter = new WiringAdapter(w, new PrybarComponentDefinition(name));
    classReader.accept(adapter, 0);

    byte[] b = w.toByteArray();

    TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
    new ClassReader(b).accept(traceClassVisitor, 0);
    TraceClassVisitor trace = new TraceClassVisitor(null, new Textifier(), new PrintWriter(System.out));
    new ClassReader(b).accept(trace, 0);

    return super.defineClass(name, b, 0, b.length);
  }

  private ClassReader classReader(String name) throws ClassNotFoundException {
    InputStream rawClass = super.getResourceAsStream(name.replace('.', '/') + ".class");
    if (rawClass == null)
      throw new ClassNotFoundException(name);

    try {
      return new ClassReader(rawClass);
    }
    catch (IOException e) {
      throw new ClassNotFoundException(name, e);
    }
  }

}
