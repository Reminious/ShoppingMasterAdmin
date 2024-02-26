package com.example.demo.Entity;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "feedback")
@Setter
@Getter
public class Feedback {
    
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   
	private Long id;
	@Column(name = "content", nullable = false)
    private String content;
	@Column(name = "time", nullable = false)
    private LocalDateTime time;

 
}
