package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.event.location.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
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

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    Integer confirmedRequests;

    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    boolean paid;

    Integer participantLimit;

    boolean requestModeration;
}