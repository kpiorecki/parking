package com.kpiorecki.parking.ejb.arquillian;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.jboss.arquillian.container.spi.event.container.AfterDeploy;
import org.jboss.arquillian.container.spi.event.container.BeforeUnDeploy;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;

/**
 * Arquillian lifecycle extension executor needed to hook for container events.
 * 
 * @see <a
 *      href="https://github.com/arquillian/arquillian-examples/tree/master/arquillian-lifecycle-extension-tutorial">Arquillian
 *      lifecycle extension example</a>
 */
public class LifecycleExecutor {

	public void executeAfterDeploy(@Observes AfterDeploy event, TestClass testClass) {
		execute(testClass, com.kpiorecki.parking.ejb.arquillian.AfterDeploy.class);
	}

	public void executeBeforeUnDeploy(@Observes BeforeUnDeploy event, TestClass testClass) {
		execute(testClass, com.kpiorecki.parking.ejb.arquillian.BeforeUnDeploy.class);
	}

	private void execute(TestClass testClass, Class<? extends Annotation> annotationClass) {
		Method[] methods = testClass.getMethods(annotationClass);
		if (methods == null) {
			return;
		}
		for (Method method : methods) {
			try {
				method.invoke(null);
			} catch (Exception e) {
				throw new RuntimeException(String.format("Could not execute %s annotated method: %s", annotationClass,
						method), e);
			}
		}
	}
}
