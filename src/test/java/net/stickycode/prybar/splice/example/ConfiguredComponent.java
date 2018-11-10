package net.stickycode.prybar.splice.example;

import net.stickycode.stereotype.StickyComponent;
import net.stickycode.stereotype.configured.Configured;

@StickyComponent
public class ConfiguredComponent {

  @Configured
  private String value;

}
