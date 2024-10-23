package com.software.modsen.drivermicroservice.mappers;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingMessage;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPutDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverRatingMapper {
    DriverRatingMapper INSTANCE = Mappers.getMapper(DriverRatingMapper.class);

    DriverRating fromDriverRatingPutDtoToDriverRating(DriverRatingPutDto driverRatingPutDto);

    DriverRating fromDriverRatingPatchDtoToDriverRating(DriverRatingPatchDto driverRatingPatchDto);

    default void updateDriverRatingFromDriverRatingDto(DriverRatingMessage driverRatingMessage,
                                                       @MappingTarget DriverRating driverRating) {
        Float newDriverRating = (driverRating.getRatingValue()
                * Float.valueOf(driverRating.getNumberOfRatings())
                + Float.valueOf(driverRatingMessage.ratingValue()))
                / (float) (driverRating.getNumberOfRatings() + 1);
        driverRating.setRatingValue(newDriverRating);
        driverRating.setNumberOfRatings(driverRating.getNumberOfRatings() + 1);
    }
}