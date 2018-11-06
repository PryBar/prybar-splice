package net.stickycode.prybar.splice;

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import net.stickycode.prybar.discovery.PrybarComponentDefinition;
import net.stickycode.prybar.discovery.PrybarComponentDependency;

public class WiringAdaptorTest {

  @Test
  public void splice() throws IOException {
    PrybarComponentDefinition definition = new PrybarComponentDefinition(SingleFieldComponent.class.getName());
    definition.getComponentWiring().add(new PrybarComponentDependency()
      .withFieldName("component")
      .withFieldType("net/stickycode/prybar/splice/PlaceHolder")
      .withTarget(PlaceHolder.class));
    check(definition, SingleFieldExample.class);
  }

  private void check(PrybarComponentDefinition definition, Class<SingleFieldExample> example) throws IOException {
    String targetAsm = spliceClass(definition);
    String exampleAsm = asmifierClass(example).replaceAll("Example", "Component");
    check(targetAsm, exampleAsm);
  }

  private void check(String targetAsm, String exampleAsm) throws IOException {
    Files.write(Paths.get("target.txt"), targetAsm.getBytes());
    Files.write(Paths.get("example.txt"), exampleAsm.getBytes());
    assertThat(new ByteArrayInputStream(targetAsm.getBytes())).hasSameContentAs(new ByteArrayInputStream(exampleAsm.getBytes()));
  }

  private String asmifierClass(Class<SingleFieldExample> type) throws IOException {
    StringWriter stringWriter = new StringWriter();
    TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(stringWriter));
    new ClassReader(type.getName()).accept(traceClassVisitor, 0);
    return stringWriter.toString();
  }

  private String spliceClass(PrybarComponentDefinition definition) throws IOException {
    StringWriter writer = new StringWriter();
    ClassReader classReader = new ClassReader(definition.getType());
    ClassWriter w = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
    WiringAdapter adapter = new WiringAdapter(w, definition);
    classReader.accept(adapter, 0);

    TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(writer));
    new ClassReader(w.toByteArray()).accept(traceClassVisitor, 0);
    return writer.toString();
  }

}
