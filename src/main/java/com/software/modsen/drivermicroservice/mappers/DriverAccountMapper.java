package com.software.modsen.drivermicroservice.mappers;

import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountCancelDto;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountIncreaseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverAccountMapper {
    DriverAccountMapper INSTANCE = Mappers.getMapper(DriverAccountMapper.class);

    DriverAccount fromDriverAccountIncreaseDtoToDriverAccount(DriverAccountIncreaseDto driverAccountIncreaseDto);

    DriverAccount fromDriverAccountCancelDtoToDriverAccount(DriverAccountCancelDto driverAccountCancelDto);
}
