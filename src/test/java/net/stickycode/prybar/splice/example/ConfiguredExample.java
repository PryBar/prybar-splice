package net.stickycode.prybar.splice.example;

import net.stickycode.prybar.pivot.PrybarComponent;
import net.stickycode.prybar.pivot.PrybarRuntime;
import net.stickycode.stereotype.StickyComponent;
import net.stickycode.stereotype.configured.Configured;

@StickyComponent
public class ConfiguredExample
    implements PrybarComponent {

  @Configured
  private String value;

  @Override
  public void configure(PrybarRuntime prybar) {
    this.value = prybar.configuration(this, String.class, "value", value);
  }
}
