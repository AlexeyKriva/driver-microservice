package com.software.modsen.drivermicroservice.mappers;

import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverAccountMapper {
    DriverAccountMapper INSTANCE = Mappers.getMapper(DriverAccountMapper.class);

    DriverAccount fromDriverAccountDtoToDriverAccount(DriverAccountDto driverAccountDto);
}
