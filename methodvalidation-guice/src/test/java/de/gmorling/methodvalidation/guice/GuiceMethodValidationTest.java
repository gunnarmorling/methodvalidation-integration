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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ValidatorFactory;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

import de.gmorling.methodvalidation.guice.domain.Movie;
import de.gmorling.methodvalidation.guice.service.MovieRepository;

public class GuiceMethodValidationTest {

	private static String notNullMessage;

	@Inject
	private MovieRepository movieRepository;

	@BeforeClass
	public static void setUpClass() {
		ResourceBundle bundle = ResourceBundle.getBundle("org.hibernate.validator.ValidationMessages");
		notNullMessage = bundle.getString("javax.validation.constraints.NotNull.message");
	}

	@Before
	public void setup() {
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			public void configure() {
				bind(MovieRepository.class);
				bind(ValidatorFactory.class).toProvider(
					ValidatorFactoryProvider.class).in(Singleton.class);

				ValidationInterceptor validationInterceptor = new ValidationInterceptor();
				requestInjection(validationInterceptor);

				bindInterceptor(
					Matchers.annotatedWith(AutoValidating.class),
					Matchers.any(),
					validationInterceptor);
			}
		});

		injector.injectMembers(this);
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
	
	@Test
	public void methodCallFailsDueToIllegalReturnValue() {

		try {
			movieRepository.findMoviesByDirector( "John Hillcoat" );
			fail(
					"Expected "
							+ MethodConstraintViolationException.class.getSimpleName()
							+ " wasn't thrown."
			);
		}
		catch ( MethodConstraintViolationException e ) {
			Set<MethodConstraintViolation<?>> violations = e
					.getConstraintViolations();
			assertEquals( 1, violations.size() );
			MethodConstraintViolation<?> constraintViolation = violations
					.iterator().next();
			assertEquals( notNullMessage, constraintViolation.getMessage() );
			assertEquals(
					"MovieRepository#findMoviesByDirector()[].releaseDate",
					constraintViolation.getPropertyPath().toString()
			);
		}
	}	
}
