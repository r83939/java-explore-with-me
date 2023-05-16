package ru.practicum.compilation.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationNewDto {

    Long id;

    @NotEmpty
    @Size(max = 120)
    @Size(min = 3)
    String title;

    String pinned;

    List<Long> events;
}