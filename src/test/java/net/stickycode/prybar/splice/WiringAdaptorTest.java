package net.stickycode.prybar.splice;

import static org.assertj.core.api.Assertions.assertThat;

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
import net.stickycode.prybar.splice.example.ConfiguredComponent;
import net.stickycode.prybar.splice.example.ConfiguredExample;
import net.stickycode.prybar.splice.example.ManyFieldComponent;
import net.stickycode.prybar.splice.example.ManyFieldExample;
import net.stickycode.prybar.splice.example.PlaceHolder;
import net.stickycode.prybar.splice.example.SingleFieldComponent;
import net.stickycode.prybar.splice.example.SingleFieldExample;

public class WiringAdaptorTest {

  @Test
  public void singleField() throws IOException {
    PrybarComponentDefinition definition = new PrybarComponentDefinition(SingleFieldComponent.class.getName());
    definition.getComponentWiring().add(new PrybarComponentDependency()
      .withFieldName("component")
      .withFieldType("net/stickycode/prybar/splice/example/PlaceHolder")
      .withTarget(PlaceHolder.class));
    check(definition, SingleFieldExample.class);
  }

  @Test
  public void manyField() throws IOException {
    PrybarComponentDefinition definition = new PrybarComponentDefinition(ManyFieldComponent.class.getName());
    definition.getComponentWiring()
      .add(new PrybarComponentDependency()
        .withFieldName("component")
        .withFieldType("net/stickycode/prybar/splice/example/PlaceHolder")
        .withTarget(PlaceHolder.class))
      .add(new PrybarComponentDependency()
        .withFieldName("componentTwo")
        .withFieldType("net/stickycode/prybar/splice/example/PlaceHolder")
        .withTarget(PlaceHolder.class));
    check(definition, ManyFieldExample.class);
  }
  
  private void check(PrybarComponentDefinition definition, Class<?> example) throws IOException {
    String targetAsm = spliceClass(definition).replaceAll(".*LineNumber.*\n",        "");
    String exampleAsm = asmifierClass(example).replaceAll("Example", "Component").replaceAll(".*LineNumber.*\n",        "");
    check(targetAsm, exampleAsm, example.getSimpleName());
  }

  private void check(String targetAsm, String exampleAsm, String type) throws IOException {
    Files.write(Paths.get(type + "-target.txt"), targetAsm.getBytes());
    Files.write(Paths.get(type + "-example.txt"), exampleAsm.getBytes());
    assertThat(new ByteArrayInputStream(targetAsm.getBytes())).hasSameContentAs(new ByteArrayInputStream(exampleAsm.getBytes()));
  }

  private String asmifierClass(Class<?> type) throws IOException {
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
