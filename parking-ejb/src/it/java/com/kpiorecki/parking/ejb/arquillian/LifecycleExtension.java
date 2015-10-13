package com.kpiorecki.parking.ejb.arquillian;

import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * Arquillian lifecycle extension needed to hook for container events.
 * 
 * @see <a
 *      href="https://github.com/arquillian/arquillian-examples/tree/master/arquillian-lifecycle-extension-tutorial">Arquillian
 *      lifecycle extension example</a>
 */
public class LifecycleExtension implements LoadableExtension {

	@Override
	public void register(ExtensionBuilder builder) {
		builder.observer(LifecycleExecutor.class);
	}
}