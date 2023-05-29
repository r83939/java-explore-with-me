package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String name;

//    @Getter
//    @Setter
//    public static class UserShortDto {
//        private Long id;
//        private String name;
//
//        public UserShortDto(Long id, String name) {
//            this.id = id;
//            this.name = name;
//        }
//    }
}
