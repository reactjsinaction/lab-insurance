package org.lab.insurance.bootstrap.feeders;

import org.lab.insurance.model.common.HolidayCalendar;

import net.sf.flatpack.DataSet;

public class HolidayCalendarFeeder extends AbstractEntityFeeder<HolidayCalendar> {

	@Override
	protected String getResourceName() {
		return "./calendars.csv";
	}

	@Override
	protected HolidayCalendar buildEntity(DataSet dataSet) {
		HolidayCalendar entity = new HolidayCalendar();
		entity.setName(dataSet.getString("NAME"));
		entity.setCountry(loadCountryFromIso2(dataSet.getString("COUNTRY_ISO2")));
		return entity;
	}
}
