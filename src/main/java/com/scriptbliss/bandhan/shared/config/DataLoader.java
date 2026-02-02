package com.scriptbliss.bandhan.shared.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.scriptbliss.bandhan.auth.entity.User;
import com.scriptbliss.bandhan.auth.enums.AccountStatus;
import com.scriptbliss.bandhan.auth.enums.UserRole;
import com.scriptbliss.bandhan.auth.repository.UserRepository;
import com.scriptbliss.bandhan.event.entity.Event;
import com.scriptbliss.bandhan.event.entity.Event.EventStatus;
import com.scriptbliss.bandhan.event.entity.EventRegistration;
import com.scriptbliss.bandhan.event.entity.EventRegistration.PaymentStatus;
import com.scriptbliss.bandhan.event.repository.EventRepository;
import com.scriptbliss.bandhan.event.repository.EventRegistrationRepository;
import com.scriptbliss.bandhan.interest.entity.Interest;
import com.scriptbliss.bandhan.interest.enums.InterestType;
import com.scriptbliss.bandhan.interest.repository.InterestRepository;
import com.scriptbliss.bandhan.match.entity.Match;
import com.scriptbliss.bandhan.match.repository.MatchRepository;
import com.scriptbliss.bandhan.profile.entity.Profile;
import com.scriptbliss.bandhan.profile.enums.Gender;
import com.scriptbliss.bandhan.profile.enums.MaritalStatus;
import com.scriptbliss.bandhan.profile.repository.ProfileRepository;
import com.scriptbliss.bandhan.shared.entity.Photo;
import com.scriptbliss.bandhan.shared.repository.PhotoRepository;
import com.scriptbliss.bandhan.subscription.entity.Subscription;
import com.scriptbliss.bandhan.subscription.entity.SubscriptionPlan;
import com.scriptbliss.bandhan.subscription.enums.BillingCycle;
import com.scriptbliss.bandhan.subscription.enums.CustomerSupportLevel;
import com.scriptbliss.bandhan.subscription.enums.PlanType;
import com.scriptbliss.bandhan.subscription.repository.SubscriptionPlanRepository;
import com.scriptbliss.bandhan.subscription.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@org.springframework.context.annotation.Profile("dev")
@RequiredArgsConstructor
public class DataLoader {

	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;
	private final InterestRepository interestRepository;
	private final MatchRepository matchRepository;
	private final EventRepository eventRepository;
	private final EventRegistrationRepository eventRegistrationRepository;
	private final PhotoRepository photoRepository;
	private final SubscriptionPlanRepository subscriptionPlanRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final PasswordEncoder passwordEncoder;

	public void createTestData() throws Exception {
		if (userRepository.count() > 0) {
			System.out.println("Data already exists, skipping...");
			return;
		}

		System.out.println("Creating test data...");

		// ===========================================
		// 0. SUBSCRIPTION PLANS (FREE, PREMIUM only)
		// ===========================================
		System.out.println("\n=== CREATING SUBSCRIPTION PLANS ===");
		SubscriptionPlan planFree = createPlan("Free Plan", PlanType.FREE,
				BigDecimal.ZERO, BigDecimal.ZERO, 10, 5, 3, false, false, CustomerSupportLevel.NONE);
		SubscriptionPlan planPremium = createPlan("Premium Plan", PlanType.PREMIUM,
				new BigDecimal("999.00"), new BigDecimal("9999.00"), 100, 50, 10, true, true, CustomerSupportLevel.PHONE);

		// ===========================================
		// 1. ADMIN USER
		// ===========================================
		System.out.println("\n=== CREATING ADMIN ===");
		User admin = createUserEntity("rahul.sharma@example.com", "Rahul", "Sharma", "+919876543210", 
				UserRole.ADMIN, "password123");
		
		// ===========================================
		// 2. EVENT ORGANIZERS (3 total)
		// ===========================================
		System.out.println("\n=== CREATING EVENT ORGANIZERS ===");
		
		// Organizer 1: Priya Verma (Mumbai) - Female
		User organizer1 = createUserEntity("priya.verma@example.com", "Priya", "Verma", "+919876543211",
				UserRole.EVENT_ORGANIZER, "password123");
		createProfile(organizer1, LocalDate.of(1988, 3, 22), Gender.FEMALE, "Mumbai", "Maharashtra",
				"Event Organizer", "I organise matrimonial events and help singles connect.");
		createOrganizerPhoto(organizer1, Gender.FEMALE);
		
		// Organizer 2: Anil Mehta (Delhi) - Male
		User organizer2 = createUserEntity("anil.mehta@example.com", "Anil", "Mehta", "+919876543220",
				UserRole.EVENT_ORGANIZER, "password123");
		createProfile(organizer2, LocalDate.of(1985, 7, 10), Gender.MALE, "Delhi", "Delhi",
				"Event Organizer", "Passionate about bringing people together through memorable events.");
		createOrganizerPhoto(organizer2, Gender.MALE);
		
		// Organizer 3: Sunita Rao (Bangalore) - Female
		User organizer3 = createUserEntity("sunita.rao@example.com", "Sunita", "Rao", "+919876543230",
				UserRole.EVENT_ORGANIZER, "password123");
		createProfile(organizer3, LocalDate.of(1990, 11, 5), Gender.FEMALE, "Bangalore", "Karnataka",
				"Event Organizer", "Creating meaningful connections through curated matrimonial events.");
		createOrganizerPhoto(organizer3, Gender.FEMALE);

		// ===========================================
		// 3. REGULAR USERS: 5 Free F, 5 Free M, 5 Premium F, 5 Premium M
		// Spread across cities, religions, ages, marital status for filters
		// ===========================================
		System.out.println("\n=== CREATING USERS (20: 5 free F, 5 free M, 5 premium F, 5 premium M) ===");

		// ---- FREE MALE (5) ----
		User john = createUserWithProfile("john.doe@example.com", "John", "Doe", Gender.MALE,
				LocalDate.of(1990, 5, 15), "Mumbai", "Hindu", MaritalStatus.SINGLE);
		createSubscription(john, planFree, BillingCycle.MONTHLY);

		User arjun = createUserWithProfile("arjun.kapoor@example.com", "Arjun", "Kapoor", Gender.MALE,
				LocalDate.of(1991, 4, 15), "Mumbai", "Hindu", MaritalStatus.SINGLE);
		createSubscription(arjun, planFree, BillingCycle.MONTHLY);

		User amit = createUserWithProfile("amit.kumar@example.com", "Amit", "Kumar", Gender.MALE,
				LocalDate.of(1989, 9, 10), "Bangalore", "Hindu", MaritalStatus.DIVORCED);
		createSubscription(amit, planFree, BillingCycle.MONTHLY);

		User vijay = createUserWithProfile("vijay.reddy@example.com", "Vijay", "Reddy", Gender.MALE,
				LocalDate.of(1987, 11, 5), "Hyderabad", "Hindu", MaritalStatus.SINGLE);
		createSubscription(vijay, planFree, BillingCycle.MONTHLY);

		User rohan = createUserWithProfile("rohan.malhotra@example.com", "Rohan", "Malhotra", Gender.MALE,
				LocalDate.of(1992, 1, 25), "Kolkata", "Hindu", MaritalStatus.SINGLE);
		createSubscription(rohan, planFree, BillingCycle.MONTHLY);

		// ---- FREE FEMALE (5) ----
		User sneha = createUserWithProfile("sneha.patel@example.com", "Sneha", "Patel", Gender.FEMALE,
				LocalDate.of(1994, 2, 28), "Pune", "Hindu", MaritalStatus.SINGLE);
		createSubscription(sneha, planFree, BillingCycle.MONTHLY);

		User divya = createUserWithProfile("divya.nair@example.com", "Divya", "Nair", Gender.FEMALE,
				LocalDate.of(1991, 8, 12), "Kochi", "Hindu", MaritalStatus.SINGLE);
		createSubscription(divya, planFree, BillingCycle.MONTHLY);

		User alice = createUserWithProfile("alice.johnson@example.com", "Alice", "Johnson", Gender.FEMALE,
				LocalDate.of(1988, 12, 10), "Chennai", "Christian", MaritalStatus.SINGLE);
		createSubscription(alice, planFree, BillingCycle.MONTHLY);

		User pooja = createUserWithProfile("pooja.desai@example.com", "Pooja", "Desai", Gender.FEMALE,
				LocalDate.of(1993, 5, 8), "Ahmedabad", "Hindu", MaritalStatus.SINGLE);
		createSubscription(pooja, planFree, BillingCycle.MONTHLY);

		User neha = createUserWithProfile("neha.gupta@example.com", "Neha", "Gupta", Gender.FEMALE,
				LocalDate.of(1995, 11, 20), "Lucknow", "Hindu", MaritalStatus.SINGLE);
		createSubscription(neha, planFree, BillingCycle.MONTHLY);

		// ---- PREMIUM MALE (5) ----
		User rahul = createUserWithProfile("rahul.mehta@example.com", "Rahul", "Mehta", Gender.MALE,
				LocalDate.of(1988, 3, 12), "Mumbai", "Hindu", MaritalStatus.SINGLE);
		createSubscription(rahul, planPremium, BillingCycle.MONTHLY);

		User karan = createUserWithProfile("karan.singh@example.com", "Karan", "Singh", Gender.MALE,
				LocalDate.of(1990, 7, 22), "Delhi", "Sikh", MaritalStatus.SINGLE);
		createSubscription(karan, planPremium, BillingCycle.MONTHLY);

		User aditya = createUserWithProfile("aditya.sharma@example.com", "Aditya", "Sharma", Gender.MALE,
				LocalDate.of(1986, 1, 5), "Bangalore", "Hindu", MaritalStatus.DIVORCED);
		createSubscription(aditya, planPremium, BillingCycle.MONTHLY);

		User vikram = createUserWithProfile("vikram.iyer@example.com", "Vikram", "Iyer", Gender.MALE,
				LocalDate.of(1991, 9, 18), "Chennai", "Hindu", MaritalStatus.SINGLE);
		createSubscription(vikram, planPremium, BillingCycle.MONTHLY);

		User manoj = createUserWithProfile("manoj.nair@example.com", "Manoj", "Nair", Gender.MALE,
				LocalDate.of(1989, 12, 3), "Thiruvananthapuram", "Hindu", MaritalStatus.SINGLE);
		createSubscription(manoj, planPremium, BillingCycle.MONTHLY);

		// ---- PREMIUM FEMALE (5) ----
		User jane = createUserWithProfile("jane.smith@example.com", "Jane", "Smith", Gender.FEMALE,
				LocalDate.of(1992, 8, 20), "Delhi", "Hindu", MaritalStatus.SINGLE);
		createSubscription(jane, planPremium, BillingCycle.MONTHLY);

		User meera = createUserWithProfile("meera.agarwal@example.com", "Meera", "Agarwal", Gender.FEMALE,
				LocalDate.of(1993, 7, 20), "Delhi", "Hindu", MaritalStatus.SINGLE);
		createSubscription(meera, planPremium, BillingCycle.MONTHLY);

		User kavita = createUserWithProfile("kavita.singh@example.com", "Kavita", "Singh", Gender.FEMALE,
				LocalDate.of(1990, 6, 18), "Chennai", "Hindu", MaritalStatus.SINGLE);
		createSubscription(kavita, planPremium, BillingCycle.MONTHLY);

		User priya = createUserWithProfile("priya.iyer@example.com", "Priya", "Iyer", Gender.FEMALE,
				LocalDate.of(1987, 4, 14), "Bangalore", "Hindu", MaritalStatus.WIDOWED);
		createSubscription(priya, planPremium, BillingCycle.MONTHLY);

		User ananya = createUserWithProfile("ananya.bose@example.com", "Ananya", "Bose", Gender.FEMALE,
				LocalDate.of(1994, 10, 7), "Kolkata", "Hindu", MaritalStatus.SINGLE);
		createSubscription(ananya, planPremium, BillingCycle.MONTHLY);

		// ===========================================
		// 4. EVENTS (various types and statuses by different organizers)
		// ===========================================
		System.out.println("\n=== CREATING EVENTS ===");
		
		// ---- PRIYA VERMA's EVENTS (organizer1) ----
		Event event1 = createEvent(organizer1, "Speed Dating Evening - Mumbai", 
				"Join us for an exciting speed dating event in Mumbai.", 
				LocalDateTime.of(2026, 2, 15, 18, 0), "Grand Hotel, Bandra", "Mumbai", "Maharashtra",
				50, new BigDecimal("500.00"), EventStatus.UPCOMING, "SPEED_DATING");
		
		Event event2 = createEvent(organizer1, "Cultural Evening - Delhi",
				"An evening of cultural exchange and meaningful connections.",
				LocalDateTime.of(2026, 2, 22, 19, 0), "India Habitat Centre", "Delhi", "Delhi",
				100, new BigDecimal("750.00"), EventStatus.UPCOMING, "CULTURAL");
		
		Event event3 = createEvent(organizer1, "Coffee Meetup - Bangalore",
				"Casual coffee meetup for young professionals.",
				LocalDateTime.of(2026, 2, 10, 11, 0), "Third Wave Coffee, Indiranagar", "Bangalore", "Karnataka",
				30, new BigDecimal("300.00"), EventStatus.UPCOMING, "COFFEE_MEETUP");
		
		createEvent(organizer1, "Weekend Brunch - Pune",
				"Sunday brunch meetup in Pune.",
				LocalDateTime.of(2026, 2, 28, 10, 0), "The Westin, Koregaon Park", "Pune", "Maharashtra",
				40, new BigDecimal("600.00"), EventStatus.UPCOMING, "DINNER");
		
		// ---- ANIL MEHTA's EVENTS (organizer2) ----
		Event event5 = createEvent(organizer2, "Elite Singles Meet - Delhi",
				"An exclusive gathering for elite professionals seeking meaningful connections.",
				LocalDateTime.of(2026, 2, 20, 19, 0), "The Imperial Hotel", "Delhi", "Delhi",
				60, new BigDecimal("1500.00"), EventStatus.UPCOMING, "DINNER");
		
		Event event6 = createEvent(organizer2, "Weekend Retreat - Jaipur",
				"A weekend retreat for singles in the Pink City.",
				LocalDateTime.of(2026, 3, 8, 10, 0), "Rambagh Palace", "Jaipur", "Rajasthan",
				40, new BigDecimal("5000.00"), EventStatus.UPCOMING, "CULTURAL");
		
		createEvent(organizer2, "Professional Speed Dating - Gurgaon",
				"Quick connections for busy professionals.",
				LocalDateTime.of(2026, 3, 15, 18, 30), "Cyber Hub", "Gurgaon", "Haryana",
				30, new BigDecimal("800.00"), EventStatus.UPCOMING, "SPEED_DATING");
		
		createEvent(organizer2, "Diwali Special Mixer",
				"Celebrate Diwali with potential matches.",
				LocalDateTime.of(2024, 10, 25, 19, 0), "Le Meridien", "Delhi", "Delhi",
				80, new BigDecimal("1000.00"), EventStatus.COMPLETED, "CULTURAL");
		
		// ---- SUNITA RAO's EVENTS (organizer3) ----
		Event event9 = createEvent(organizer3, "Tech Professionals Meetup - Bangalore",
				"Connecting IT professionals looking for life partners.",
				LocalDateTime.of(2026, 2, 18, 18, 0), "UB City Mall", "Bangalore", "Karnataka",
				50, new BigDecimal("600.00"), EventStatus.UPCOMING, "COFFEE_MEETUP");
		
		createEvent(organizer3, "South Indian Cultural Evening",
				"Celebrate South Indian traditions while meeting potential matches.",
				LocalDateTime.of(2026, 3, 1, 17, 0), "Leela Palace", "Bangalore", "Karnataka",
				70, new BigDecimal("900.00"), EventStatus.UPCOMING, "CULTURAL");
		
		createEvent(organizer3, "Garden Brunch - Mysore",
				"A relaxed brunch in the garden city.",
				LocalDateTime.of(2026, 3, 22, 11, 0), "Radisson Blu", "Mysore", "Karnataka",
				35, new BigDecimal("450.00"), EventStatus.UPCOMING, "DINNER");
		
		createEvent(organizer3, "Startup Founders Mixer",
				"For entrepreneurs seeking life partners who understand the startup life.",
				LocalDateTime.of(2026, 4, 5, 19, 0), "WeWork Galaxy", "Bangalore", "Karnataka",
				25, new BigDecimal("700.00"), EventStatus.UPCOMING, "COFFEE_MEETUP");
		
		// ONGOING events
		Event event7 = createEvent(organizer1, "Matrimony Mixer - Kolkata",
				"Grand matrimonial mixer event.",
				LocalDateTime.of(2026, 1, 28, 17, 0), "ITC Sonar", "Kolkata", "West Bengal",
				150, new BigDecimal("1000.00"), EventStatus.ONGOING, "CULTURAL");
		
		// COMPLETED events
		Event event8 = createEvent(organizer1, "Year-End Gala - Mumbai",
				"Year-end matrimony gala.",
				LocalDateTime.of(2024, 11, 20, 18, 0), "The Leela, Mumbai", "Mumbai", "Maharashtra",
				120, new BigDecimal("1500.00"), EventStatus.COMPLETED, "DINNER");
		
		// CANCELLED event
		createEvent(organizer1, "Cancelled Test Event",
				"This event was cancelled for testing.",
				LocalDateTime.of(2025, 6, 10, 19, 0), "Test Venue", "Mumbai", "Maharashtra",
				20, new BigDecimal("0.00"), EventStatus.CANCELLED, "SPEED_DATING");

		// ===========================================
		// 5. EVENT REGISTRATIONS
		// ===========================================
		System.out.println("\n=== CREATING EVENT REGISTRATIONS ===");
		// Event 1 registrations
		createRegistration(john, event1, PaymentStatus.PAID, false);
		createRegistration(jane, event1, PaymentStatus.PAID, false);
		createRegistration(alice, event1, PaymentStatus.PENDING, false);
		createRegistration(arjun, event1, PaymentStatus.PAID, false);
		createRegistration(meera, event1, PaymentStatus.PENDING, false);
		
		// Event 2 registrations
		createRegistration(jane, event2, PaymentStatus.PAID, false);
		createRegistration(amit, event2, PaymentStatus.PAID, false);
		createRegistration(sneha, event2, PaymentStatus.PENDING, false);
		
		// Event 3 registrations
		createRegistration(john, event3, PaymentStatus.PAID, false);
		createRegistration(vijay, event3, PaymentStatus.PAID, false);
		createRegistration(kavita, event3, PaymentStatus.PENDING, false);
		
		// Event 7 (ONGOING) registrations with some attended
		createRegistration(john, event7, PaymentStatus.PAID, true);
		createRegistration(jane, event7, PaymentStatus.PAID, true);
		createRegistration(rohan, event7, PaymentStatus.PAID, false);
		createRegistration(divya, event7, PaymentStatus.PENDING, false);
		
		// Event 8 (COMPLETED) registrations - all attended
		createRegistration(alice, event8, PaymentStatus.PAID, true);
		createRegistration(arjun, event8, PaymentStatus.PAID, true);
		createRegistration(meera, event8, PaymentStatus.PAID, true);

		// More registrations with premium users
		createRegistration(rahul, event5, PaymentStatus.PAID, false);
		createRegistration(priya, event5, PaymentStatus.PENDING, false);
		createRegistration(karan, event9, PaymentStatus.PAID, false);
		createRegistration(ananya, event9, PaymentStatus.PAID, false);

		// ===========================================
		// 6. INTERESTS (spread across free/premium, M/F)
		// ===========================================
		System.out.println("\n=== CREATING INTERESTS ===");
		createInterest(john.getId(), jane.getId(), InterestType.LIKE);
		createInterest(jane.getId(), john.getId(), InterestType.LIKE);
		createInterest(arjun.getId(), meera.getId(), InterestType.LIKE);
		createInterest(meera.getId(), arjun.getId(), InterestType.LIKE);
		createInterest(amit.getId(), sneha.getId(), InterestType.LIKE);
		createInterest(sneha.getId(), amit.getId(), InterestType.LIKE);
		createInterest(vijay.getId(), kavita.getId(), InterestType.LIKE);
		createInterest(kavita.getId(), vijay.getId(), InterestType.LIKE);
		createInterest(rahul.getId(), priya.getId(), InterestType.LIKE);
		createInterest(priya.getId(), rahul.getId(), InterestType.LIKE);
		createInterest(karan.getId(), ananya.getId(), InterestType.LIKE);
		createInterest(rohan.getId(), divya.getId(), InterestType.LIKE);

		// ===========================================
		// 7. MATCHES (from mutual likes)
		// ===========================================
		System.out.println("\n=== CREATING MATCHES ===");
		createMatch(john.getId(), jane.getId(), 0.85);
		createMatch(arjun.getId(), meera.getId(), 0.82);
		createMatch(amit.getId(), sneha.getId(), 0.78);
		createMatch(vijay.getId(), kavita.getId(), 0.88);
		createMatch(rahul.getId(), priya.getId(), 0.91);

		// ===========================================
		// SUMMARY
		// ===========================================
		System.out.println("\n=== TEST DATA SUMMARY ===");
		System.out.println("Subscription: 5 Free M, 5 Free F, 5 Premium M, 5 Premium F (20 users)");
		System.out.println("Users: 1 Admin, 3 Organizers, 20 Regular (spread: city, religion, marital)");
		System.out.println("Events: 15 | Registrations: 21 | Interests: 12 | Matches: 5");
		System.out.println("\n=== LOGIN CREDENTIALS (password: password123) ===");
		System.out.println("Admin: rahul.sharma@example.com");
		System.out.println("Organizers: priya.verma@example.com, anil.mehta@example.com, sunita.rao@example.com");
		System.out.println("Free: john.doe@example.com, sneha.patel@example.com");
		System.out.println("Premium: jane.smith@example.com, rahul.mehta@example.com");
		System.out.println("\nTest data created successfully!");
	}

	private User createUserEntity(String email, String firstName, String lastName, String phone,
			UserRole role, String password) {
		User user = User.builder()
				.email(email)
				.firstName(firstName)
				.lastName(lastName)
				.phone(phone)
				.password(passwordEncoder.encode(password))
				.role(role)
				.status(AccountStatus.ACTIVE)
				.build();
		return userRepository.save(user);
	}

	private User createUserWithProfile(String email, String firstName, String lastName, Gender gender,
			LocalDate dob, String city, String religion, MaritalStatus maritalStatus) {
		User user = User.builder()
				.email(email)
				.firstName(firstName)
				.lastName(lastName)
				.phone(generatePhoneNumber(firstName))
				.password(passwordEncoder.encode("password123"))
				.role(UserRole.USER)
				.status(AccountStatus.ACTIVE)
				.build();
		user = userRepository.save(user);

		createProfile(user, dob, gender, city, getState(city), getOccupation(firstName),
				"Looking for a life partner who shares similar values.");
		return user;
	}

	private void createProfile(User user, LocalDate dob, Gender gender, String city, String state,
			String occupation, String aboutMe) {
		Profile profile = Profile.builder()
				.user(user)
				.dateOfBirth(dob)
				.gender(gender)
				.city(city)
				.state(state)
				.country("India")
				.occupation(occupation)
				.education(getEducation(user.getFirstName()))
				.income(getIncome(user.getFirstName()))
				.heightCm(getHeight(gender))
				.weightKg(getWeight(gender))
				.aboutMe(aboutMe)
				.build();
		profileRepository.save(profile);
	}

	private Event createEvent(User organizer, String title, String description, LocalDateTime eventDate,
			String venue, String city, String state, int maxParticipants, BigDecimal fee,
			EventStatus status, String eventType) {
		Event event = new Event();
		event.setOrganizer(organizer);
		event.setTitle(title);
		event.setDescription(description);
		event.setEventDate(eventDate);
		event.setVenue(venue);
		event.setCity(city);
		event.setState(state);
		event.setMaxParticipants(maxParticipants);
		event.setRegistrationFee(fee);
		event.setStatus(status);
		event.setEventType(eventType);
		return eventRepository.save(event);
	}

	private void createRegistration(User user, Event event, PaymentStatus paymentStatus, boolean attended) {
		EventRegistration reg = new EventRegistration();
		reg.setUser(user);
		reg.setEvent(event);
		reg.setPaymentStatus(paymentStatus);
		reg.setAttended(attended);
		reg.setRegistrationDate(LocalDateTime.now().minusDays((long) (Math.random() * 30)));
		eventRegistrationRepository.save(reg);
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

	private SubscriptionPlan createPlan(String planName, PlanType planType, BigDecimal priceMonthly, BigDecimal priceYearly,
			int maxProfiles, int maxMessages, int maxPhotos, boolean priorityMatching, boolean advancedFilters, CustomerSupportLevel support) {
		SubscriptionPlan p = SubscriptionPlan.builder()
				.planName(planName)
				.planType(planType)
				.priceMonthly(priceMonthly)
				.priceYearly(priceYearly)
				.maxProfilesView(maxProfiles)
				.maxMessages(maxMessages)
				.maxPhotos(maxPhotos)
				.priorityMatching(priorityMatching)
				.advancedFilters(advancedFilters)
				.customerSupport(support)
				.isActive(true)
				.build();
		return subscriptionPlanRepository.save(p);
	}

	private void createSubscription(User user, SubscriptionPlan plan, BillingCycle cycle) {
		Subscription s = Subscription.builder()
				.userId(user.getId())
				.plan(plan)
				.billingCycle(cycle)
				.startDate(LocalDate.now())
				.isActive(true)
				.autoRenew(plan.getPlanType() == PlanType.PREMIUM)
				.paymentAmount(plan.getPriceMonthly())
				.build();
		subscriptionRepository.save(s);
	}

	private String getState(String city) {
		switch (city) {
		case "Mumbai": return "Maharashtra";
		case "Delhi": return "Delhi";
		case "Pune": return "Maharashtra";
		case "Bangalore": return "Karnataka";
		case "Chennai": return "Tamil Nadu";
		case "Hyderabad": return "Telangana";
		case "Kochi": return "Kerala";
		case "Kolkata": return "West Bengal";
		case "Ahmedabad": return "Gujarat";
		case "Lucknow": return "Uttar Pradesh";
		case "Thiruvananthapuram": return "Kerala";
		default: return "Maharashtra";
		}
	}

	private String getOccupation(String firstName) {
		String[] occupations = { "Software Engineer", "Doctor", "Teacher", "Business Analyst", 
				"Marketing Manager", "Consultant" };
		return occupations[Math.abs(firstName.hashCode()) % occupations.length];
	}

	private String getEducation(String firstName) {
		String[] educations = { "Bachelor's Degree", "Master's Degree", "PhD", "MBA", 
				"Engineering", "Medical Degree" };
		return educations[Math.abs(firstName.hashCode()) % educations.length];
	}

	private BigDecimal getIncome(String firstName) {
		BigDecimal[] incomes = { new BigDecimal("400000"), new BigDecimal("600000"), 
				new BigDecimal("800000"), new BigDecimal("1200000"), new BigDecimal("500000") };
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

	private void createOrganizerPhoto(User organizer, Gender gender) {
		String fileName;
		String filePath;
		String mimeType;
		
		if (gender == Gender.MALE) {
			fileName = "male-organizer-profile-pic.jpg";
			filePath = "/assets/images/event-organizer/male-organizer-profile-pic.jpg";
			mimeType = "image/jpeg";
		} else {
			fileName = "female-organizer-profile-pic.png";
			filePath = "/assets/images/event-organizer/female-organizer-profile-pic.png";
			mimeType = "image/png";
		}
		
		Photo photo = new Photo();
		photo.setUserId(organizer.getId());
		photo.setFileName(fileName);
		photo.setFilePath(filePath);
		photo.setFileSize(50000L); // Approximate size
		photo.setMimeType(mimeType);
		photo.setIsPrimary(true);
		photo.setSortOrder(0);
		photo.setAltText(organizer.getFirstName() + " " + organizer.getLastName() + " profile photo");
		photoRepository.save(photo);
	}
}
