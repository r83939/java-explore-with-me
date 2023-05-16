package ru.practicum.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "participation_request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    LocalDateTime created;

    @Column(name = "event_id")
    Long event;

    @Column(name = "requester_id")
    Long requester;

    @Enumerated(EnumType.STRING)
    RequestState status;
}