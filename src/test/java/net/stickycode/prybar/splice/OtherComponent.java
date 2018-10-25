package net.stickycode.prybar.splice;

import javax.inject.Inject;

import net.stickycode.prybar.pivot.ComponentLifecycle;
import net.stickycode.prybar.pivot.PrybarComponent;
import net.stickycode.prybar.pivot.PrybarComponentLookup;
import net.stickycode.prybar.pivot.PrybarRuntime;
import net.stickycode.stereotype.StickyComponent;

@StickyComponent
public class OtherComponent
    implements PrybarComponent {

  @Inject
  private TestComponent point;

  public ComponentLifecycle<OtherComponent> lifecycle() {
      return new ComponentLifecycle<OtherComponent>(this) {
        public void wire(PrybarRuntime r) {
          component.point = r.require(new PrybarComponentLookup(component, TestComponent.class, "point"));
        }
        public List<PostWiring>
      };
    }

  @Override
  public void wire(PrybarRuntime prybarPivot) {
  }
}

}
