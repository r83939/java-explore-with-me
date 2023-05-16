package ru.practicum.event.location;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class LocationMapper {

    public static LocationDto toLocationDtoFromLocation(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}