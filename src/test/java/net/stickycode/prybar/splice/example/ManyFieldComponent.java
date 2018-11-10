package net.stickycode.prybar.splice.example;

import javax.inject.Inject;

import net.stickycode.prybar.splice.PlaceHolder;
import net.stickycode.stereotype.StickyComponent;

@StickyComponent
public class ManyFieldComponent {

  @Inject
  private PlaceHolder component;

  @Inject
  private PlaceHolder componentTwo;

}
