package net.stickycode.prybar.splice;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class WireTest {

  @Test
  public void splice() throws IOException {
    ClassReader classReader = new ClassReader(TestComponent.class.getName());
    ClassWriter w = new ClassWriter(classReader, 0);
    WiringAdapter adapter = new WiringAdapter(w, Collections.singletonList(TestComponent.class.getName().replaceAll("\\.", "/")));
    classReader.accept(adapter, 0);
    TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
    new ClassReader(w.toByteArray()).accept(traceClassVisitor, 0);
    TraceClassVisitor trace = new TraceClassVisitor(null, new Textifier(), new PrintWriter(System.out));
    new ClassReader(w.toByteArray()).accept(trace, 0);
  }

}
