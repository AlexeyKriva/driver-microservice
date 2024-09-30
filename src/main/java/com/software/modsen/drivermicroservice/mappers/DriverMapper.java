package com.software.modsen.drivermicroservice.mappers;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.DriverPatchDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverMapper {
    DriverMapper INSTANCE = Mappers.getMapper(DriverMapper.class);

    Driver fromDriverDtoToDriver(DriverDto driverDto);

    Driver fromDriverPatchDtoToDriver(DriverPatchDto driverPatchDto);
}
