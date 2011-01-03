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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * An application-scoped bean providing access to a {@link ValidatorFactory}.
 * 
 * @author Gunnar Morling
 * 
 */
@ApplicationScoped
public class ValidatorFactoryBean {

	@SuppressWarnings("unused")
	@Produces
	private ValidatorFactory factory;
	
	@PostConstruct
	protected void setupFactory() {
		factory = Validation.buildDefaultValidatorFactory();
	}

}
