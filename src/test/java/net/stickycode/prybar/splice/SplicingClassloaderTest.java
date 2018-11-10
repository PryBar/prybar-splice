package net.stickycode.prybar.splice;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import net.stickycode.prybar.discovery.PrybarComponentDefinition;
import net.stickycode.prybar.discovery.PrybarDefinitionRegistry;
import net.stickycode.prybar.pivot.PrybarComponent;
import net.stickycode.prybar.pivot.PrybarRuntime;
import net.stickycode.prybar.splice.example.SingleFieldComponent;

public class SplicingClassloaderTest {

  @Test
  public void identity() throws ClassNotFoundException {
    ClassLoader wired = new SplicingClassloader(getClass().getClassLoader(), new PrybarDefinitionRegistry());
    wired.loadClass(Object.class.getName());
    wired.loadClass(Test.class.getName());
  }

  @Test(expected = ClassNotFoundException.class)
  public void notFound() throws ClassNotFoundException {
    ClassLoader wired = new SplicingClassloader(getClass().getClassLoader(), registry("non.exists.SomeComponent"));
    wired.loadClass("not.exists.SomeComponent");
  }

  private PrybarDefinitionRegistry registry(String... types) {
    PrybarDefinitionRegistry prybarDefinitionRegistry = new PrybarDefinitionRegistry();
    for (String type : types) {
      prybarDefinitionRegistry.register(new PrybarComponentDefinition(type));
    }
    return prybarDefinitionRegistry;
  }

  @Test
  public void classloader()
      throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    ClassLoader wired = new SplicingClassloader(getClass().getClassLoader(), registry(SingleFieldComponent.class.getName()));
    Class<?> loaded = wired.loadClass(SingleFieldComponent.class.getName());
    loaded.getDeclaredMethod("wire", new Class[] { PrybarRuntime.class });
    PrybarComponent instance = (PrybarComponent) loaded.getDeclaredConstructor(null).newInstance(null);
    instance.wire(null);
  }

}
