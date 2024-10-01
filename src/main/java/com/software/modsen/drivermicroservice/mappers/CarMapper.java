package com.software.modsen.drivermicroservice.mappers;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarDto;
import com.software.modsen.drivermicroservice.entities.car.CarPatchDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarMapper {
    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    Car fromCarDtoToCar(CarDto carDto);

    Car fromCarPatchDtoToCar(CarPatchDto carPatchDto);
}
