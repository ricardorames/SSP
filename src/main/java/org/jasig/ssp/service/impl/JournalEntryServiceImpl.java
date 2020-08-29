/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.service.impl;

import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.jasig.ssp.dao.JournalEntryDao;
import org.jasig.ssp.dao.PersonDao;
import org.jasig.ssp.model.JournalEntry;
import org.jasig.ssp.model.JournalEntryDetail;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.service.AbstractRestrictedPersonAssocAuditableService;
import org.jasig.ssp.service.JournalEntryService;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.PersonProgramStatusService;
import org.jasig.ssp.transferobject.reports.BaseStudentReportTO;
import org.jasig.ssp.transferobject.reports.EntityCountByCoachSearchForm;
import org.jasig.ssp.transferobject.reports.EntityStudentCountByCoachTO;
import org.jasig.ssp.transferobject.reports.JournalCaseNotesStudentReportTO;
import org.jasig.ssp.transferobject.reports.JournalStepSearchFormTO;
import org.jasig.ssp.transferobject.reports.JournalStepStudentReportTO;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.jasig.ssp.web.api.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class JournalEntryServiceImpl
		extends AbstractRestrictedPersonAssocAuditableService<JournalEntry>
		implements JournalEntryService {

	private transient JournalEntryDao journalEntryDao;
	private transient PersonDao personDao;
	private transient PersonProgramStatusService personProgramStatusService;

	@Autowired
	public JournalEntryServiceImpl(JournalEntryDao journalEntryDao,
								   PersonProgramStatusService personProgramStatusService,
								   PersonDao personDao) {
		this.journalEntryDao = journalEntryDao;
		this.personDao = personDao;
		this.personProgramStatusService = personProgramStatusService;
	}

	@Override
	protected JournalEntryDao getDao() {
		return journalEntryDao;
	}

	@Override
	public JournalEntry create(final JournalEntry obj)
			throws ObjectNotFoundException, ValidationException {
		return save(obj);
	}

	@Override
	public JournalEntry save(final JournalEntry obj)
			throws ObjectNotFoundException, ValidationException {
		final JournalEntry journalEntry = getDao().save(obj);
		checkForTransition(journalEntry);
		return journalEntry;
	}

	private void checkForTransition(final JournalEntry journalEntry)
			throws ObjectNotFoundException, ValidationException {

		// search for a JournalStep that indicates a transition
		Optional<JournalEntryDetail> detailOptional = journalEntry.getJournalEntryDetails().stream()
				.filter(JournalEntryDetail::isUsedForTransition)
				.findFirst();

        // is used for transition, so attempt to set program status
        if(detailOptional.isPresent()) {
            personProgramStatusService.setTransitionForStudent(journalEntry.getPerson());
        }

	}
	
	@Override
	public Long getCountForCoach(Person coach, Date createDateFrom, Date createDateTo, List<UUID> studentTypeIds){
		return journalEntryDao.getJournalCountForCoach(coach, createDateFrom, createDateTo, studentTypeIds);
	}

	@Override
	public Long getStudentCountForCoach(Person coach, Date createDateFrom, Date createDateTo, List<UUID> studentTypeIds) {
		return journalEntryDao.getStudentJournalCountForCoach(coach, createDateFrom, createDateTo, studentTypeIds);
	}
	
	@Override
	public PagingWrapper<EntityStudentCountByCoachTO> getStudentJournalCountForCoaches(EntityCountByCoachSearchForm form){
		return journalEntryDao.getStudentJournalCountForCoaches(form);
	}
	
	@Override
	public PagingWrapper<JournalStepStudentReportTO> getJournalStepStudentReportTOsFromCriteria(JournalStepSearchFormTO personSearchForm,  
			SortingAndPaging sAndP){
		return journalEntryDao.getJournalStepStudentReportTOsFromCriteria(personSearchForm,
				sAndP);
	}
	
 	@Override
 	public List<JournalCaseNotesStudentReportTO> getJournalCaseNoteStudentReportTOsFromCriteria(JournalStepSearchFormTO personSearchForm, SortingAndPaging sAndP) throws ObjectNotFoundException {
		final List<JournalCaseNotesStudentReportTO> personsWithJournalEntries = journalEntryDao
				.getJournalCaseNoteStudentReportTOsFromCriteria(personSearchForm, sAndP);

		final Map<String, JournalCaseNotesStudentReportTO> map =
				personsWithJournalEntries.stream()
						.collect(Collectors.toMap(JournalCaseNotesStudentReportTO::getSchoolId, (entry) -> entry));

		final SortingAndPaging personSAndP =
				SortingAndPaging.createForSingleSortAll(ObjectStatus.ACTIVE, "lastName", "DESC");
		final PagingWrapper<BaseStudentReportTO> persons = personDao.getStudentReportTOs(personSearchForm, personSAndP);

		if (persons != null) {
                persons.getRows().stream()
				  .filter(person -> !map.containsKey(person.getSchoolId()) && StringUtils.isNotBlank(person.getCoachSchoolId()))
				  .filter(person -> needsToAddStudent(personSearchForm, person))
				  .forEach(person -> {
					final JournalCaseNotesStudentReportTO entry = new JournalCaseNotesStudentReportTO(person);
					personsWithJournalEntries.add(entry);
					map.put(entry.getSchoolId(), entry);
				});
                sortByStudentName(personsWithJournalEntries);
	     }

 		 return personsWithJournalEntries;
 	}

	private boolean needsToAddStudent(JournalStepSearchFormTO personSearchForm, BaseStudentReportTO person) {
		return !(personSearchForm.getJournalSourceIds() != null &&
				getDao().getJournalCountForPersonForJournalSourceIds(person.getId(), personSearchForm.getJournalSourceIds()) == 0);
	}

	private static void sortByStudentName(List<JournalCaseNotesStudentReportTO> toSort) {
		Collections.sort(toSort);
	}

}