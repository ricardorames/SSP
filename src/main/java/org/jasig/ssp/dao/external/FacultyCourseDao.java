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
package org.jasig.ssp.dao.external;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jasig.ssp.model.external.FacultyCourse;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.transferobject.external.SearchFacultyCourseTO;
import org.springframework.stereotype.Repository;
import java.util.List;


/**
 * Data access class for the FacultyCourse reference entity.
 */
@Repository
public class FacultyCourseDao extends AbstractExternalDataDao<FacultyCourse> {

    public FacultyCourseDao() {
        super(FacultyCourse.class);
    }

    /**
     * Gets all courses for the specified faculty.
     *
     * @param facultySchoolId The faculty school id to use to lookup the associated data.
     * @return The specified courses if any were found.
     * @throws ObjectNotFoundException If specified object could not be found.
     */
    @SuppressWarnings("unchecked")
    public List<FacultyCourse> getAllCoursesForFaculty(final String facultySchoolId) throws ObjectNotFoundException {

        if (!StringUtils.isNotBlank(facultySchoolId)) {
            throw new ObjectNotFoundException(facultySchoolId,
                    FacultyCourse.class.getName());
        }

        return createCriteria().add(Restrictions.eq("facultySchoolId", facultySchoolId)).list();
	}

	@SuppressWarnings("unchecked")
	public FacultyCourse getCourseByFacultySchoolIdAndFormattedCourse(
	        final String facultySchoolId, final String formattedCourse) throws ObjectNotFoundException {

			    if (!StringUtils.isNotBlank(formattedCourse)) {
			throw new ObjectNotFoundException(formattedCourse,
					FacultyCourse.class.getName());
		}

		//KLUDGE, section should really be included but it is not required in the current data model.  If the criteria is not unique
		//we nievely pick the first one and cross our fingers.  This should be changed after section_code because part of the natural key
        final List<FacultyCourse> result = createCriteria().add(
				Restrictions.eq("facultySchoolId", facultySchoolId)).add(
				Restrictions.eq("formattedCourse", formattedCourse)).list();
		return result.size() > 0 ? result.get(0) : null;
		//END KLUDGE
	}

    @SuppressWarnings("unchecked")
    public FacultyCourse  getCourseByFacultySchoolIdAndFormattedCourseAndTermCode(
            final String facultySchoolId, final String formattedCourse, final String termCode)
            throws ObjectNotFoundException {

        if (StringUtils.isBlank(facultySchoolId)) {
            throw new ObjectNotFoundException("Must specify a faculty school ID",
                    FacultyCourse.class.getName());
        }

        if (StringUtils.isBlank(formattedCourse)) {
            throw new ObjectNotFoundException("Must specify a formatted course ID",
                    FacultyCourse.class.getName());
        }

        if (StringUtils.isBlank(termCode)) {
            throw new ObjectNotFoundException("Must specify a term code",
                    FacultyCourse.class.getName());
        }

        //KLUDGE, section should really be included but it is not required in the current data model.  If the criteria is not unique
        //we nievely pick the first one and cross our fingers.  This should be changed after section_code because part of the natural key
        final List<FacultyCourse> result = createCriteria()
                .add(Restrictions.eq("facultySchoolId", facultySchoolId))
                .add(Restrictions.eq("formattedCourse", formattedCourse))
                .add(Restrictions.eq("termCode", termCode)).list();
        if(result.size() > 0) {
            return result.get(0);
        }
        throw new ObjectNotFoundException(String.format("FacultyCourse with facultySchoolId %s, formattedCourse %s and termCode %s not found.", facultySchoolId, formattedCourse, termCode), persistentClass.getName());
        //END KLUDGE
    }

    @SuppressWarnings("unchecked")
    public FacultyCourse getCourseBySearchFacultyCourseTO(SearchFacultyCourseTO requestTO)
            throws ObjectNotFoundException {
        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("facultySchoolId", requestTO.getFacultySchoolId()));

        if (requestTO.hasFormattedCourse()) {
            criteria.add(Restrictions.eq("formattedCourse", requestTO.getFormattedCourse()));
        }

        if (requestTO.hasSectionCode()) {
            criteria.add(Restrictions.eq("sectionCode", requestTO.getSectionCode()));
        }

        if(requestTO.hasTermCode()) {
                criteria.add(Restrictions.eq("termCode",requestTO.getTermCode()));
        }
		
		final List<FacultyCourse> result = criteria.list();
		return result.size() > 0 ? result.get(0) : null;
	}
}