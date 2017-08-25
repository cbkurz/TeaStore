/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.petsupplystore.persistence.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.persistence.domain.CategoryRepository;
import tools.descartes.petsupplystore.rest.AbstractCRUDEndpoint;

/**
 * Persistence endpoint for CRUD operations on Categories.
 * @author Joakim von Kistowski
 *
 */
@Path("categories")
public class CategoryEndpoint extends AbstractCRUDEndpoint<Category> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long createEntity(final Category category) {
		return CategoryRepository.REPOSITORY.createEntity(category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Category findEntityById(final long id) {
		Category category = CategoryRepository.REPOSITORY.getEntity(id);
		if (category == null) {
			return null;
		}
		return new Category(category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Category> listAllEntities(final int startIndex, final int maxResultCount) {
		List<Category> categories = new ArrayList<Category>();
		for (Category c : CategoryRepository.REPOSITORY.getAllEntities(startIndex, maxResultCount)) {
			categories.add(new Category(c));
		}
		return categories;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean updateEntity(long id, Category category) {
		return CategoryRepository.REPOSITORY.updateEntity(id, category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean deleteEntity(long id) {
		return CategoryRepository.REPOSITORY.removeEntity(id);
	}
	
}