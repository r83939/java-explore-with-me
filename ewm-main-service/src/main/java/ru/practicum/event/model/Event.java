package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotEmpty
    @Size(max = 120)
    @Size(min = 3)
    String title;

    @NotEmpty
    @Size(max = 7000)
    @Size(min = 20)
    String description;

    @NotEmpty
    @Size(max = 2000)
    @Size(min = 20)
    String annotation;

    @Enumerated(EnumType.STRING)
    EventState state;

    Integer categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", shape = JsonFormat.Shape.STRING)
    LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", shape = JsonFormat.Shape.STRING)
    LocalDateTime publishedOn;

    Integer confirmedRequests;

    Long locationId;

    Long initiatorId;

    boolean paid;

    Integer participantLimit;

    boolean requestModeration;
}