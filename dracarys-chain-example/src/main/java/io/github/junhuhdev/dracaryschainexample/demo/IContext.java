package io.github.junhuhdev.dracaryschainexample.demo;

import java.util.Optional;

public interface IContext {

	<T> Optional<T> onEvent(Class<T> event);

}
