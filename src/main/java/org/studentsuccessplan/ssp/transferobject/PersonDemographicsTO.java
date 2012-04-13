package org.studentsuccessplan.ssp.transferobject;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.studentsuccessplan.ssp.model.Person;
import org.studentsuccessplan.ssp.model.PersonDemographics;
import org.studentsuccessplan.ssp.model.reference.Citizenship;
import org.studentsuccessplan.ssp.model.reference.EmploymentShifts;
import org.studentsuccessplan.ssp.model.reference.Ethnicity;
import org.studentsuccessplan.ssp.model.reference.Genders;
import org.studentsuccessplan.ssp.model.reference.MaritalStatus;
import org.studentsuccessplan.ssp.model.reference.VeteranStatus;

import com.google.common.collect.Lists;

public class PersonDemographicsTO
		extends AuditableTO<PersonDemographics>
		implements TransferObject<PersonDemographics> {

	@NotNull
	private UUID personId;

	private UUID coachId, maritalStatusId, ethnicityId,
			citizenshipId, veteranStatusId;
	private boolean abilityToBenefit, local, primaryCaregiver,
			childCareNeeded, employed;
	private int numberOfChildren;
	private String anticipatedStartTerm, anticipatedStartYear,
			countryOfResidence, paymentStatus, gender, countryOfCitizenship,
			childAges, placeOfEmployment, shift, wage, totalHoursWorkedPerWeek;

	public PersonDemographicsTO() {
		super();
	}

	public PersonDemographicsTO(final PersonDemographics model) {
		super();
		fromModel(model);
	}

	@Override
	public final void fromModel(final PersonDemographics model) {
		super.fromModel(model);

		if (model.getCoach() != null) {
			coachId = model.getCoach().getId();
		}
		if (model.getMaritalStatus() != null) {
			maritalStatusId = model.getMaritalStatus().getId();
		}
		if (model.getEthnicity() != null) {
			ethnicityId = model.getEthnicity().getId();
		}
		if (model.getCitizenship() != null) {
			citizenshipId = model.getCitizenship().getId();
		}
		if (model.getVeteranStatus() != null) {
			veteranStatusId = model.getVeteranStatus().getId();
		}
		abilityToBenefit = model.isAbilityToBenefit();
		local = model.isLocal();
		primaryCaregiver = model.isPrimaryCaregiver();
		childCareNeeded = model.isChildCareNeeded();
		employed = model.isEmployed();
		numberOfChildren = model.getNumberOfChildren();
		anticipatedStartTerm = model.getAnticipatedStartTerm();
		anticipatedStartYear = model.getAnticipatedStartYear();
		countryOfResidence = model.getCountryOfResidence();
		paymentStatus = model.getPaymentStatus();
		if (model.getGender() != null) {
			gender = model.getGender().getCode();
		}
		countryOfCitizenship = model.getCountryOfCitizenship();
		childAges = model.getChildAges();
		placeOfEmployment = model.getPlaceOfEmployment();
		if (model.getShift() != null) {
			shift = model.getShift().getCode();
		}
		wage = model.getWage();
		totalHoursWorkedPerWeek = model.getTotalHoursWorkedPerWeek();
	}

	@Override
	public PersonDemographics addToModel(final PersonDemographics model) {
		super.addToModel(model);

		if (getCoachId() != null) {
			model.setCoach(new Person(getCoachId()));
		}
		if (getMaritalStatusId() != null) {
			model.setMaritalStatus(new MaritalStatus(getMaritalStatusId()));
		}
		if (getEthnicityId() != null) {
			model.setEthnicity(new Ethnicity(getEthnicityId()));
		}
		if (getCitizenshipId() != null) {
			model.setCitizenship(new Citizenship(getCitizenshipId()));
		}
		if (getVeteranStatusId() != null) {
			model.setVeteranStatus(new VeteranStatus(getVeteranStatusId()));
		}
		model.setAbilityToBenefit(isAbilityToBenefit());
		model.setLocal(isLocal());
		model.setPrimaryCaregiver(isPrimaryCaregiver());
		model.setChildCareNeeded(isChildCareNeeded());
		model.setEmployed(isEmployed());
		model.setNumberOfChildren(getNumberOfChildren());
		model.setAnticipatedStartTerm(getAnticipatedStartTerm());
		model.setAnticipatedStartYear(getAnticipatedStartYear());
		model.setCountryOfResidence(getCountryOfResidence());
		model.setPaymentStatus(getPaymentStatus());
		if (getGender() != null) {
			model.setGender(Genders.valueOf(getGender()));
		}
		model.setCountryOfCitizenship(getCountryOfCitizenship());
		model.setChildAges(getChildAges());
		model.setPlaceOfEmployment(getPlaceOfEmployment());
		if (getShift() != null) {
			model.setShift(EmploymentShifts.valueOf(getShift()));
		}
		model.setWage(getWage());
		model.setTotalHoursWorkedPerWeek(getTotalHoursWorkedPerWeek());
		return model;
	}

	@Override
	public PersonDemographics asModel() {
		return addToModel(new PersonDemographics());
	}

	public static List<PersonDemographicsTO> listToTOList(
			final List<PersonDemographics> models) {
		final List<PersonDemographicsTO> tos = Lists.newArrayList();
		for (PersonDemographics model : models) {
			tos.add(new PersonDemographicsTO(model));
		}
		return tos;
	}

	public UUID getPersonId() {
		return personId;
	}

	public void setPersonId(final UUID personId) {
		this.personId = personId;
	}

	public UUID getCoachId() {
		return coachId;
	}

	public void setCoachId(final UUID coachId) {
		this.coachId = coachId;
	}

	public UUID getMaritalStatusId() {
		return maritalStatusId;
	}

	public void setMaritalStatusId(final UUID maritalStatusId) {
		this.maritalStatusId = maritalStatusId;
	}

	public UUID getEthnicityId() {
		return ethnicityId;
	}

	public void setEthnicityId(final UUID ethnicityId) {
		this.ethnicityId = ethnicityId;
	}

	public UUID getCitizenshipId() {
		return citizenshipId;
	}

	public void setCitizenshipId(final UUID citizenshipId) {
		this.citizenshipId = citizenshipId;
	}

	public UUID getVeteranStatusId() {
		return veteranStatusId;
	}

	public void setVeteranStatusId(final UUID veteranStatusId) {
		this.veteranStatusId = veteranStatusId;
	}

	public boolean isAbilityToBenefit() {
		return abilityToBenefit;
	}

	public void setAbilityToBenefit(final boolean abilityToBenefit) {
		this.abilityToBenefit = abilityToBenefit;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(final boolean local) {
		this.local = local;
	}

	public boolean isPrimaryCaregiver() {
		return primaryCaregiver;
	}

	public void setPrimaryCaregiver(final boolean primaryCaregiver) {
		this.primaryCaregiver = primaryCaregiver;
	}

	public boolean isChildCareNeeded() {
		return childCareNeeded;
	}

	public void setChildCareNeeded(final boolean childCareNeeded) {
		this.childCareNeeded = childCareNeeded;
	}

	public boolean isEmployed() {
		return employed;
	}

	public void setEmployed(final boolean employed) {
		this.employed = employed;
	}

	public String getAnticipatedStartTerm() {
		return anticipatedStartTerm;
	}

	public void setAnticipatedStartTerm(final String anticipatedStartTerm) {
		this.anticipatedStartTerm = anticipatedStartTerm;
	}

	public String getAnticipatedStartYear() {
		return anticipatedStartYear;
	}

	public void setAnticipatedStartYear(final String anticipatedStartYear) {
		this.anticipatedStartYear = anticipatedStartYear;
	}

	public String getCountryOfResidence() {
		return countryOfResidence;
	}

	public void setCountryOfResidence(final String countryOfResidence) {
		this.countryOfResidence = countryOfResidence;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(final String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public String getCountryOfCitizenship() {
		return countryOfCitizenship;
	}

	public void setCountryOfCitizenship(final String countryOfCitizenship) {
		this.countryOfCitizenship = countryOfCitizenship;
	}

	public String getChildAges() {
		return childAges;
	}

	public void setChildAges(final String childAges) {
		this.childAges = childAges;
	}

	public String getPlaceOfEmployment() {
		return placeOfEmployment;
	}

	public void setPlaceOfEmployment(final String placeOfEmployment) {
		this.placeOfEmployment = placeOfEmployment;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(final String shift) {
		this.shift = shift;
	}

	public String getWage() {
		return wage;
	}

	public void setWage(final String wage) {
		this.wage = wage;
	}

	public String getTotalHoursWorkedPerWeek() {
		return totalHoursWorkedPerWeek;
	}

	public void setTotalHoursWorkedPerWeek(final String totalHoursWorkedPerWeek) {
		this.totalHoursWorkedPerWeek = totalHoursWorkedPerWeek;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(final int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

}
