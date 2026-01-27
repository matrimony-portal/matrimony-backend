package com.scriptbliss.bandhan.shared.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.enums.AccountStatus;
import com.scriptbliss.bandhan.auth.enums.UserRole;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.interest.entity.Interest;
import com.scriptbliss.bandhan.interest.enums.InterestType;
import com.scriptbliss.bandhan.interest.repository.InterestRepository;
import com.scriptbliss.bandhan.match.entity.Match;
import com.scriptbliss.bandhan.match.repository.MatchRepository;
import com.scriptbliss.bandhan.profile.entity.Profile;
import com.scriptbliss.bandhan.profile.enums.Gender;
import com.scriptbliss.bandhan.profile.enums.MaritalStatus;
import com.scriptbliss.bandhan.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Component
@org.springframework.context.annotation.Profile("dev")
@RequiredArgsConstructor
public class DataLoader {

	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;
	private final InterestRepository interestRepository;
	private final MatchRepository matchRepository;
	private final PasswordEncoder passwordEncoder;

	public void createTestData() throws Exception {
		if (userRepository.count() > 0) {
			System.out.println("Data already exists, skipping...");
			return;
		}

		System.out.println("Creating test data...");

		// Step 1: Create all users first
		System.out.println("\n=== CREATING USERS ===");
		// Male users
		Long johnId = createUser("john@example.com", "John", "Doe", Gender.MALE, LocalDate.of(1995, 5, 15), "Mumbai",
				"Hindu", MaritalStatus.SINGLE);
		Long rajId = createUser("raj@example.com", "Raj", "Sharma", Gender.MALE, LocalDate.of(1992, 8, 22), "Delhi",
				"Hindu", MaritalStatus.DIVORCED);
		Long amitId = createUser("amit@example.com", "Amit", "Patel", Gender.MALE, LocalDate.of(1990, 12, 10), "Pune",
				"Hindu", MaritalStatus.SINGLE);
		Long vikasId = createUser("vikas@example.com", "Vikas", "Kumar", Gender.MALE, LocalDate.of(1988, 3, 25),
				"Bangalore", "Hindu", MaritalStatus.SINGLE);
		Long rohitId = createUser("rohit@example.com", "Rohit", "Verma", Gender.MALE, LocalDate.of(1993, 11, 8),
				"Chennai", "Hindu", MaritalStatus.SINGLE);
		Long arjunId = createUser("arjun@example.com", "Arjun", "Singh", Gender.MALE, LocalDate.of(1991, 7, 12),
				"Hyderabad", "Sikh", MaritalStatus.DIVORCED);
		Long karthikId = createUser("karthik@example.com", "Karthik", "Nair", Gender.MALE, LocalDate.of(1989, 2, 28),
				"Kochi", "Hindu", MaritalStatus.SINGLE);
		Long deepakId = createUser("deepak@example.com", "Deepak", "Joshi", Gender.MALE, LocalDate.of(1994, 6, 18),
				"Jaipur", "Hindu", MaritalStatus.SINGLE);
		Long manishId = createUser("manish@example.com", "Manish", "Agarwal", Gender.MALE, LocalDate.of(1987, 10, 5),
				"Lucknow", "Hindu", MaritalStatus.SINGLE);
		Long sureshId = createUser("suresh@example.com", "Suresh", "Reddy", Gender.MALE, LocalDate.of(1991, 4, 12),
				"Hyderabad", "Hindu", MaritalStatus.SINGLE);

		// Female users
		Long priyaId = createUser("priya@example.com", "Priya", "Singh", Gender.FEMALE, LocalDate.of(1996, 3, 8),
				"Mumbai", "Hindu", MaritalStatus.SINGLE);
		Long saraId = createUser("sara@example.com", "Sara", "Khan", Gender.FEMALE, LocalDate.of(1993, 7, 18), "Delhi",
				"Muslim", MaritalStatus.WIDOWED);
		Long nehaId = createUser("neha@example.com", "Neha", "Gupta", Gender.FEMALE, LocalDate.of(1994, 11, 25),
				"Bangalore", "Hindu", MaritalStatus.SINGLE);
		Long kavitaId = createUser("kavita@example.com", "Kavita", "Sharma", Gender.FEMALE, LocalDate.of(1989, 9, 14),
				"Pune", "Hindu", MaritalStatus.SINGLE);
		Long ritikaId = createUser("ritika@example.com", "Ritika", "Jain", Gender.FEMALE, LocalDate.of(1992, 1, 30),
				"Chennai", "Jain", MaritalStatus.SINGLE);
		Long anjaliId = createUser("anjali@example.com", "Anjali", "Reddy", Gender.FEMALE, LocalDate.of(1990, 6, 22),
				"Hyderabad", "Hindu", MaritalStatus.DIVORCED);
		Long meenaId = createUser("meena@example.com", "Meena", "Iyer", Gender.FEMALE, LocalDate.of(1987, 4, 5),
				"Kochi", "Hindu", MaritalStatus.WIDOWED);
		Long pooja = createUser("pooja@example.com", "Pooja", "Mehta", Gender.FEMALE, LocalDate.of(1995, 8, 20),
				"Mumbai", "Hindu", MaritalStatus.SINGLE);
		Long sunita = createUser("sunita@example.com", "Sunita", "Agarwal", Gender.FEMALE, LocalDate.of(1988, 12, 15),
				"Delhi", "Hindu", MaritalStatus.SINGLE);
		Long rekha = createUser("rekha@example.com", "Rekha", "Nair", Gender.FEMALE, LocalDate.of(1993, 5, 10),
				"Bangalore", "Hindu", MaritalStatus.SINGLE);
		Long divya = createUser("divya@example.com", "Divya", "Pillai", Gender.FEMALE, LocalDate.of(1991, 9, 25),
				"Chennai", "Hindu", MaritalStatus.SINGLE);
		Long shweta = createUser("shweta@example.com", "Shweta", "Joshi", Gender.FEMALE, LocalDate.of(1990, 3, 18),
				"Pune", "Hindu", MaritalStatus.SINGLE);
		Long ritu = createUser("ritu@example.com", "Ritu", "Singh", Gender.FEMALE, LocalDate.of(1992, 7, 8), "Jaipur",
				"Hindu", MaritalStatus.SINGLE);
		Long madhuri = createUser("madhuri@example.com", "Madhuri", "Sharma", Gender.FEMALE, LocalDate.of(1989, 11, 30),
				"Lucknow", "Hindu", MaritalStatus.SINGLE);

		Long alexId = createUser("alex@example.com", "Alex", "Taylor", Gender.OTHER, LocalDate.of(1991, 9, 12),
				"Chennai", "Christian", MaritalStatus.SINGLE);

		createAdminUser("admin@matrimony.com", "Admin", "User");
		createEventOrganizerUser("organizer@matrimony.com", "Event", "Organizer", "Mumbai");

		// Step 2: Create interests
		System.out.println("\n=== CREATING INTERESTS ===");
		createInterest(johnId, priyaId, InterestType.LIKE);
		createInterest(priyaId, johnId, InterestType.LIKE);
		createInterest(vikasId, nehaId, InterestType.LIKE);
		createInterest(nehaId, vikasId, InterestType.LIKE);
		createInterest(rajId, anjaliId, InterestType.LIKE);
		createInterest(anjaliId, rajId, InterestType.LIKE);
		createInterest(amitId, kavitaId, InterestType.LIKE);
		createInterest(kavitaId, amitId, InterestType.LIKE);

		createInterest(rohitId, ritikaId, InterestType.LIKE);
		createInterest(arjunId, saraId, InterestType.LIKE);
		createInterest(johnId, nehaId, InterestType.LIKE);
		createInterest(vikasId, priyaId, InterestType.LIKE);
		createInterest(rohitId, meenaId, InterestType.LIKE);

		createInterest(johnId, saraId, InterestType.PASS);
		createInterest(amitId, meenaId, InterestType.PASS);
		createInterest(vikasId, ritikaId, InterestType.PASS);
		createInterest(rajId, priyaId, InterestType.PASS);
		createInterest(arjunId, kavitaId, InterestType.PASS);
		createInterest(rohitId, anjaliId, InterestType.PASS);

		// Step 3: Create matches
		System.out.println("\n=== CREATING MATCHES ===");
		createMatch(johnId, priyaId, 0.85);
		createMatch(vikasId, nehaId, 0.80);
		createMatch(rajId, anjaliId, 0.70);
		createMatch(amitId, kavitaId, 0.82);

		System.out.println("\n=== TEST DATA SUMMARY ===");
		System.out.println("Users created: 26 (10 Male, 14 Female, 1 Other, 1 Admin, 1 Event Organizer)");
		System.out.println("Interests created: 18 (8 LIKE pairs, 5 one-sided LIKE, 6 PASS)");
		System.out.println("Matches created: 4 (from mutual likes)");
		System.out.println("Cities: Mumbai, Delhi, Pune, Bangalore, Chennai, Hyderabad, Kochi, Jaipur, Lucknow");
		System.out.println("Test data created successfully!");
	}

	private Long createUser(String email, String firstName, String lastName, Gender gender, LocalDate dob, String city,
			String religion, MaritalStatus maritalStatus) {
		User user = User.builder().email(email).firstName(firstName).lastName(lastName)
				.phone(generatePhoneNumber(firstName)).password(passwordEncoder.encode("password123"))
				.status(AccountStatus.ACTIVE).build();
		user = userRepository.save(user);

		Profile profile = Profile.builder().user(user).dateOfBirth(dob).gender(gender).city(city).state(getState(city))
				.religion(religion).maritalStatus(maritalStatus).occupation(getOccupation(firstName))
				.education(getEducation(firstName)).income(getIncome(firstName)).heightCm(getHeight(gender))
				.weightKg(getWeight(gender)).country("India")
				.aboutMe("Looking for a life partner who shares similar values and interests.").build();
		profileRepository.save(profile);
		return user.getId();
	}

	private void createAdminUser(String email, String firstName, String lastName) {
		User admin = User.builder().email(email).firstName(firstName).lastName(lastName).phone("+91-9999999999")
				.password(passwordEncoder.encode("admin123")).status(AccountStatus.ACTIVE).role(UserRole.ADMIN).build();
		userRepository.save(admin);
	}

	private void createEventOrganizerUser(String email, String firstName, String lastName, String city) {
		User organizer = User.builder().email(email).firstName(firstName).lastName(lastName).phone("+91-8888888888")
				.password(passwordEncoder.encode("organizer123")).status(AccountStatus.ACTIVE)
				.role(UserRole.EVENT_ORGANIZER).build();
		organizer = userRepository.save(organizer);

		Profile profile = Profile.builder().user(organizer).dateOfBirth(LocalDate.of(1985, 1, 1)).gender(Gender.OTHER)
				.city(city).state(getState(city)).country("India").occupation("Event Organizer").education("MBA")
				.aboutMe("Professional event organizer specializing in matrimony events.").build();
		profileRepository.save(profile);
	}

	private String getState(String city) {
		switch (city) {
		case "Mumbai":
			return "Maharashtra";
		case "Delhi":
			return "Delhi";
		case "Pune":
			return "Maharashtra";
		case "Bangalore":
			return "Karnataka";
		case "Chennai":
			return "Tamil Nadu";
		case "Hyderabad":
			return "Telangana";
		case "Kochi":
			return "Kerala";
		case "Jaipur":
			return "Rajasthan";
		case "Lucknow":
			return "Uttar Pradesh";
		default:
			return "Maharashtra";
		}
	}

	private String getOccupation(String firstName) {
		String[] occupations = { "Software Engineer", "Doctor", "Teacher", "Business Analyst", "Marketing Manager",
				"Consultant" };
		return occupations[Math.abs(firstName.hashCode()) % occupations.length];
	}

	private String getEducation(String firstName) {
		String[] educations = { "Bachelor's Degree", "Master's Degree", "PhD", "MBA", "Engineering", "Medical Degree" };
		return educations[Math.abs(firstName.hashCode()) % educations.length];
	}

	private BigDecimal getIncome(String firstName) {
		BigDecimal[] incomes = { new BigDecimal("400000"), new BigDecimal("600000"), new BigDecimal("800000"),
				new BigDecimal("1200000"), new BigDecimal("500000") };
		return incomes[Math.abs(firstName.hashCode()) % incomes.length];
	}

	private Integer getHeight(Gender gender) {
		return gender == Gender.MALE ? 175 : gender == Gender.FEMALE ? 160 : 170;
	}

	private Integer getWeight(Gender gender) {
		return gender == Gender.MALE ? 70 : gender == Gender.FEMALE ? 55 : 65;
	}

	private String generatePhoneNumber(String firstName) {
		int hash = Math.abs(firstName.hashCode());
		return String.format("+91-9%09d", hash % 1000000000);
	}

	private void createInterest(Long fromUserId, Long toUserId, InterestType type) {
		try {
			Interest interest = new Interest();
			interest.setFromUserId(fromUserId);
			interest.setToUserId(toUserId);
			interest.setType(type);
			interestRepository.save(interest);
		} catch (Exception e) {
			System.err.println("Failed to create interest: " + e.getMessage());
		}
	}

	private void createMatch(Long user1Id, Long user2Id, Double score) {
		try {
			Match match = new Match();
			match.setUser1Id(user1Id);
			match.setUser2Id(user2Id);
			match.setCompatibilityScore(score);
			matchRepository.save(match);
		} catch (Exception e) {
			System.err.println("Failed to create match: " + e.getMessage());
		}
	}
}