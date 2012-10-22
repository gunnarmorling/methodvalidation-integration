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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Proxy;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.Validation;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.hibernate.validator.method.MethodValidator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.gmorling.methodvalidation.dynamicproxy.domain.Movie;
import de.gmorling.methodvalidation.dynamicproxy.service.MovieRepository;
import de.gmorling.methodvalidation.dynamicproxy.service.MovieRepositoryImpl;

public class DynamicProxyMethodValidationTest {

	private static String notNullMessage;

	private static MethodValidator validator;

	private MovieRepository movieRepository;

	@BeforeClass
	public static void setUpMethodValidator() {
		ResourceBundle bundle = ResourceBundle.getBundle("org.hibernate.validator.ValidationMessages");
		notNullMessage = bundle.getString("javax.validation.constraints.NotNull.message");

		validator = Validation.byProvider(HibernateValidator.class)
			.configure()
			.buildValidatorFactory()
			.getValidator()
			.unwrap(MethodValidator.class);
	}

	@Before
	public void setUpRepository() {

		movieRepository = getValidatingProxy(MovieRepository.class,
			new MovieRepositoryImpl());
	}

	private <T> T getValidatingProxy(Class<T> clazz, T object) {

		ValidationInvocationHandler validationHandler = new ValidationInvocationHandler(
			object, validator
			);

		@SuppressWarnings("unchecked")
		T validatingProxy = (T) Proxy.newProxyInstance(
			object.getClass().getClassLoader(),
			object.getClass().getInterfaces(),
			validationHandler);

		return validatingProxy;
	}

	@Test
	public void validMethodCall() {

		Set<Movie> moviesByBryanSinger = movieRepository
			.findMoviesByDirector("Bryan Singer");

		assertEquals(1, moviesByBryanSinger.size());
		assertEquals(
			"The Usual Suspects",
			moviesByBryanSinger.iterator().next().getTitle());
	}

	@Test
	public void methodCallFailsDueToIllegalParameter() {

		try {
			movieRepository.findMoviesByDirector(null);
			fail("Expected "
				+ MethodConstraintViolationException.class.getSimpleName()
				+ " wasn't thrown.");
		}
		catch (MethodConstraintViolationException e) {
			Set<MethodConstraintViolation<?>> violations = e
				.getConstraintViolations();
			assertEquals(1, violations.size());
			MethodConstraintViolation<?> constraintViolation = violations
				.iterator().next();
			assertEquals(notNullMessage, constraintViolation.getMessage());
			assertEquals("MovieRepository#findMoviesByDirector(arg0)",
				constraintViolation.getPropertyPath().toString());
		}

	}
}
