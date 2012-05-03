package org.jasig.ssp.service.reference.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jasig.ssp.dao.reference.JournalSourceDao;
import org.jasig.ssp.model.reference.JournalSource;
import org.jasig.ssp.service.reference.JournalSourceService;

@Service
@Transactional
public class JournalSourceServiceImpl extends
		AbstractReferenceService<JournalSource>
		implements JournalSourceService {

	@Autowired
	transient private JournalSourceDao dao;

	public JournalSourceServiceImpl() {
		super(JournalSource.class);
	}

	protected void setDao(final JournalSourceDao dao) {
		this.dao = dao;
	}

	@Override
	protected JournalSourceDao getDao() {
		return dao;
	}
}
