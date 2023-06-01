package com.whiteshipjavatest;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestExecution;

public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

	public FindSlowTestExtension(long threshold) {
		this.THRESHOLD = threshold;
	}

	private final long THRESHOLD;

	@Override
	public void afterTestExecution(ExtensionContext extensionContext) throws Exception {

		String testClassName = extensionContext.getRequiredTestClass().getName();
		Method method = extensionContext.getRequiredTestMethod();
		String testMethodName = method.getName();
		SlowTest annotation = method.getAnnotation(SlowTest.class);
		ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));


		long start_time = store.remove("START_TIME", long.class);
		long duration = System.currentTimeMillis() - start_time;

		if (duration > THRESHOLD && annotation==null) {
			System.out.printf("Please consider mark method [%s] with @SlowTest. \n", testMethodName);

		}

	}

	@Override
	public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {

		String testClassName = extensionContext.getRequiredTestClass().getName();
		String testMethodName = extensionContext.getRequiredTestMethod().getName();

		ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));
		store.put("START_TIME", System.currentTimeMillis());

	}
}
