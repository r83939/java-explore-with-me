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

    //Integer categoryId;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", shape = JsonFormat.Shape.STRING)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    Integer confirmedRequests;

    //Long locationId;
    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;

    //Long initiatorId;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    boolean paid;

    Integer participantLimit;

    boolean requestModeration;
}