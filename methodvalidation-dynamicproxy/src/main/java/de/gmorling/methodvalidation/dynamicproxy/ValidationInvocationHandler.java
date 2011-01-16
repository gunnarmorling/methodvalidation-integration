/**
 *  Copyright 2011 Gunnar Morling (http://www.gunnarmorling.de/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.gmorling.methodvalidation.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;

import org.hibernate.validator.MethodConstraintViolation;
import org.hibernate.validator.MethodConstraintViolationException;
import org.hibernate.validator.MethodValidator;

/**
 * An invocation handler used to test method-level validation.
 * 
 * @author Gunnar Morling
 */
public class ValidationInvocationHandler implements InvocationHandler {

	private final Object wrapped;

	private final MethodValidator validator;

	public ValidationInvocationHandler(Object wrapped, MethodValidator validator) {

		this.wrapped = wrapped;
		this.validator = validator;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		Set<MethodConstraintViolation<Object>> constraintViolations = validator
			.validateParameters(wrapped, method, args);

		if (!constraintViolations.isEmpty()) {
			throw new MethodConstraintViolationException(constraintViolations);
		}

		Object result = method.invoke(wrapped, args);

		constraintViolations = validator.validateReturnValue(wrapped, method,
			result);

		if (!constraintViolations.isEmpty()) {
			throw new MethodConstraintViolationException(constraintViolations);
		}

		return result;
	}
}
