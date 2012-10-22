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
package de.gmorling.methodvalidation.guice;

import java.util.Set;

import javax.validation.ValidatorFactory;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.hibernate.validator.method.MethodValidator;

import com.google.inject.Inject;

public class ValidationInterceptor implements MethodInterceptor {

	@Inject
	private ValidatorFactory validatorFactory;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		MethodValidator validator = validatorFactory.getValidator().unwrap(
			MethodValidator.class);

		Set<MethodConstraintViolation<Object>> violations = validator
			.validateAllParameters(
				invocation.getThis(), invocation.getMethod(),
				invocation.getArguments());

		if (!violations.isEmpty()) {
			throw new MethodConstraintViolationException(violations);
		}

		Object result = invocation.proceed();

		violations = validator.validateReturnValue(
						invocation.getThis(),
						invocation.getMethod(),
						result);

		if (!violations.isEmpty()) {
			throw new MethodConstraintViolationException(violations);
		}

		return result;
	}

}