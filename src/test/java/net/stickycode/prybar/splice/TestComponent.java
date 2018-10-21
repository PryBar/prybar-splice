package net.stickycode.prybar.splice;

import javax.inject.Inject;

import net.stickycode.stereotype.StickyComponent;

@StickyComponent
public class TestComponent {

  @Inject
  private OtherComponent component;

  public OtherComponent getComponent() {
    return component;
  }

}
