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
package de.gmorling.methodvalidation.dynamicproxy.service;

import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.gmorling.methodvalidation.dynamicproxy.domain.Movie;

/**
 * An exemplary business service for which automatic method-level validation is
 * enabled.
 * 
 * @author Gunnar Morling
 * 
 */
public interface MovieRepository {

	public abstract Set<Movie> findMoviesByDirector(
		@NotNull @Size(min = 3) String director);

}