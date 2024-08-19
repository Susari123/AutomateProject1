package com.Edvak_EHR_Automation_V1.utilities;

import java.util.Date;


import lombok.Getter;
import lombok.ToString;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TestData {

	
	public static class PolicyDates{
		private String from;
		private String to;
		
		public PolicyDates(String a, String b) {
			this.from = a;
			this.to = b;
		}
		
		public String getFrom() {
			return from;
		}
		
		public String getTo() {
			return to;
		}
	}
	
	public static PolicyDates randomizePolicyDates() {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        Faker faker = new Faker();
        Date fromDate = Date.from(
                java.time.LocalDate.of(2024, 1, 1).atStartOfDay(java.time.ZoneId.systemDefault())
                        .toInstant());

        Date fakeDate = faker.date().between(fromDate, new Date());

        LocalDate from = fakeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String to = from.plusYears(faker.random().nextInt(1, 9)).plusDays(faker.random().nextInt(1, 31))
                .plusMonths(faker.random().nextInt(1, 12)).format(pattern);

        return new PolicyDates(from.format(pattern), to);
    }

}
