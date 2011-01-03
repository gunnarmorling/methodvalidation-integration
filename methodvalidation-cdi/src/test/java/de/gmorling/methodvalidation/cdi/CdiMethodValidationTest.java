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
package de.gmorling.methodvalidation.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Set;

import javax.inject.Inject;

import org.hibernate.validator.MethodConstraintViolation;
import org.hibernate.validator.MethodConstraintViolationException;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.gmorling.methodvalidation.cdi.domain.Movie;
import de.gmorling.methodvalidation.cdi.service.MovieRepository;

@RunWith(Arquillian.class)
public class CdiMethodValidationTest {

	@Inject
	private MovieRepository movieRepository;

	@Deployment
	public static JavaArchive createTestArchive() throws Exception {
		return ShrinkWrap
			.create(JavaArchive.class, "test.jar")
			.addManifestResource("beans.xml", ArchivePaths.create("beans.xml"))
			.addPackage(MovieRepository.class.getPackage())
			.addPackage(ValidationInterceptor.class.getPackage())
			.addPackage(Movie.class.getPackage());
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
			assertEquals("may not be null", constraintViolation.getMessage());
		}

	}
}
