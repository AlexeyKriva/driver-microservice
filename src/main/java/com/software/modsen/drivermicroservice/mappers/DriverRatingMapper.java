package com.software.modsen.drivermicroservice.mappers;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPutDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverRatingMapper {
    DriverRatingMapper INSTANCE = Mappers.getMapper(DriverRatingMapper.class);

    DriverRating fromPassengerRatingDtoToPassengerRating(DriverRatingDto driverRatingDto);

    DriverRating fromPassengerRatingPutDtoToPassengerRating(DriverRatingPutDto driverRatingPutDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePassengerRatingFromPassengerRatingPatchDto(DriverRatingPatchDto driverRatingPatchDto,
                                                          @MappingTarget DriverRating driverRating);

    default void updatePassengerRatingFromPassengerRatingDto(DriverRatingDto driverRatingDto,
                                                             @MappingTarget DriverRating driverRating) {
        Float newPassengerRating = (driverRating.getRatingValue()
                * Float.valueOf(driverRating.getNumberOfRatings())
                + Float.valueOf(driverRatingDto.getRatingValue()))
                / (float) (driverRating.getNumberOfRatings() + 1);
        driverRating.setRatingValue(newPassengerRating);
        driverRating.setNumberOfRatings(driverRating.getNumberOfRatings() + 1);
    }
}