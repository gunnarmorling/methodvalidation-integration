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
package de.gmorling.methodvalidation.spring;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ValidatorFactory;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.validator.MethodConstraintViolation;
import org.hibernate.validator.MethodConstraintViolationException;
import org.hibernate.validator.MethodValidator;

/**
 * @author Kevin Pollet
 */
@Aspect
public class ValidationInterceptor {

	@Inject
	private ValidatorFactory validatorFactory;

	//Match any public methods in a class annotated with @AutoValidating
	@Around("execution(public * *(..)) && @within(de.gmorling.methodvalidation.spring.AutoValidating)")
	public Object validateMethodParameters(ProceedingJoinPoint pjp) throws Throwable {
		Object result;
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		MethodValidator validator = validatorFactory.getValidator().unwrap( MethodValidator.class );

		Set<MethodConstraintViolation<Object>> parametersViolations = validator.validateParameters(
				pjp.getTarget(), signature.getMethod(), pjp.getArgs()
		);
		if ( !parametersViolations.isEmpty() ) {
			throw new MethodConstraintViolationException( parametersViolations );
		}

		result =  pjp.proceed(); //Execute the method

		Set<MethodConstraintViolation<Object>> returnValueViolations = validator.validateReturnValue(
				pjp.getTarget(), signature.getMethod(), result
		);
		if ( !returnValueViolations.isEmpty() ) {
			throw new MethodConstraintViolationException( returnValueViolations );
		}

		return result;
	}

}
