package com.software.modsen.drivermicroservice.mappers;

import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountBalanceDownDto;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountBalanceUpDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverAccountMapper {
    DriverAccountMapper INSTANCE = Mappers.getMapper(DriverAccountMapper.class);

    DriverAccount fromDriverAccountIncreaseDtoToDriverAccount(DriverAccountBalanceUpDto driverAccountBalanceUpDto);

    DriverAccount fromDriverAccountCancelDtoToDriverAccount(DriverAccountBalanceDownDto driverAccountBalanceDownDto);
}
