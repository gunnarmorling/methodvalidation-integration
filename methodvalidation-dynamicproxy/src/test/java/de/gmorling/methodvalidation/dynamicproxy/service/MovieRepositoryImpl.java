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

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.gmorling.methodvalidation.dynamicproxy.domain.Movie;

public class MovieRepositoryImpl implements MovieRepository {

	private final Map<Long, Movie> sampleMovies = new TreeMap<Long, Movie>();

	public MovieRepositoryImpl() {

		Movie movie =
			new Movie(
				1,
				"The Usual Suspects",
				106,
				"Bryan Singer",
				new GregorianCalendar(1995, 7, 16).getTime());

		sampleMovies.put(movie.getId(), movie);
	}

	@Override
	public Set<Movie> findMoviesByDirector(String director) {

		Set<Movie> theValue = new HashSet<Movie>();

		for (Movie oneMovie : sampleMovies.values()) {
			if (oneMovie.getDirector().equals(director)) {
				theValue.add(oneMovie);
			}

		}

		return theValue;
	}

}