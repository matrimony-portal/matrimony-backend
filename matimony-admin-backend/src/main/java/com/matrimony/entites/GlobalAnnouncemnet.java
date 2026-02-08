package com.matrimony.entites;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name ="Global_ announcement")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class GlobalAnnouncemnet {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    // Admin name who sent the message
	    @Column(nullable = false)
	    private String adminName;

	    // Message content coming from React textarea
	    @Column(nullable = false, columnDefinition = "TEXT")
	    private String message;

	    // Date when message was sent
	    @Column(nullable = false)
	    private LocalDate date;

	    // Time when message was sent
	    @Column(nullable = false)
	    private LocalTime time;

}
