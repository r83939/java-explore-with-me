package ru.practicum.Comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minidev.json.annotate.JsonIgnore;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @NotEmpty
    @Column(name = "text")
    String text;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    @JsonIgnore
    Event item;
    @Column(name = "created")
    private LocalDateTime created;
}
