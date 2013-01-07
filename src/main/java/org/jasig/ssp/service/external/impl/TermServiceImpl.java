/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.service.external.impl;

import java.util.Collection;

import org.jasig.ssp.dao.external.TermDao;
import org.jasig.ssp.model.external.Term;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.external.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Term service implementation
 * 
 * @author jon.adams
 */
@Service
@Transactional
public class TermServiceImpl extends AbstractExternalDataService<Term>
		implements TermService {

	@Autowired
	transient private TermDao dao;

	@Override
	protected TermDao getDao() {
		return dao;
	}

	protected void setDao(final TermDao dao) {
		this.dao = dao;
	}

	@Override
	public Term getByCode(final String code) throws ObjectNotFoundException {
		return getDao().getByCode(code);
	}

	@Override
	public Term getCurrentTerm() throws ObjectNotFoundException {
		return getDao().getCurrentTerm();
	}
}