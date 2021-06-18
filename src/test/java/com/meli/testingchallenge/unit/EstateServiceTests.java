package com.meli.testingchallenge.unit;

import com.meli.testingchallenge.dtos.*;
import com.meli.testingchallenge.exceptions.DistrictNotFoundException;
import com.meli.testingchallenge.exceptions.ExistentDistrictNameException;
import com.meli.testingchallenge.models.District;
import com.meli.testingchallenge.models.Estate;
import com.meli.testingchallenge.repositories.IDistrictRepository;
import com.meli.testingchallenge.services.EstateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstateServiceTests {

    @Mock
    IDistrictRepository repository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    EstateService service;

    private ModelMapper localMapper;
    private String districtName;
    private DistrictDTO districtDto;
    private EstateAssessmentDTO expected;
    private EstateDTO estateDto;


    @BeforeEach
    public void init(){
        localMapper = new ModelMapper();
        String propName = "House1";
        districtName= "District1";
        String environmentName1 = "bigger";
        String environmentName2 = "medium";
        String environmentName3 = "smaller";
        districtDto = new DistrictDTO(districtName, 2.0);
        EnvironmentDTO environment1 = new EnvironmentDTO(environmentName1, 2.0, 2.0);
        EnvironmentDTO environment2 = new EnvironmentDTO(environmentName2, 2.0, 1.0);
        EnvironmentDTO environment3 = new EnvironmentDTO(environmentName3, 1.0, 1.0);
        List<EnvironmentDTO> environmentList = new ArrayList();
        environmentList.add(environment1);
        environmentList.add(environment2);
        environmentList.add(environment3);
        EnvironmentDTORes environmentResDto1 = new EnvironmentDTORes(environmentName1, 4.0);
        EnvironmentDTORes environmentResDto2 = new EnvironmentDTORes(environmentName2, 2.0);
        EnvironmentDTORes environmentResDto3 = new EnvironmentDTORes(environmentName3, 1.0);
        List<EnvironmentDTORes> environmentResDtosList = new ArrayList();
        environmentResDtosList.add(environmentResDto1);
        environmentResDtosList.add(environmentResDto2);
        environmentResDtosList.add(environmentResDto3);
        expected = new EstateAssessmentDTO(propName, 7.0, 14, environmentResDto1, environmentResDtosList);
        estateDto = new EstateDTO(propName, districtName, environmentList);

    }

    @Test
    public void should_calculate_correctly_all_parameters() throws DistrictNotFoundException {

        // Arrange
        District district = localMapper.map(districtDto, District.class);
        when(modelMapper.map(estateDto, Estate.class)).thenReturn(localMapper.map(estateDto, Estate.class));
        when(repository.findDistrictByName(districtName)).thenReturn(district);

        // Act
        EstateAssessmentDTO received = service.getAssessment(estateDto);

        // Assert
        Assertions.assertEquals(expected, received);
    }

    @Test
    public void should_throes_exception_when_district_name_is_not_on_repository() {

        // Arrange
        when(repository.findDistrictByName(districtName)).thenReturn(null);

        when(modelMapper.map(estateDto, Estate.class)).thenReturn(localMapper.map(estateDto, Estate.class));
        // Act
        assertThrows(DistrictNotFoundException.class, () -> service.getAssessment(estateDto));
    }

    @Test
    public void should_call_repository_passing_district_model_and_return_message() throws ExistentDistrictNameException {

        // Arrange
        District district = localMapper.map(districtDto, District.class);
        when(modelMapper.map(districtDto, District.class)).thenReturn(district);
        doNothing().when(repository).addDistrict(district);
        String expected =  "District was added Successfully";

        // Act
        String received = service.addDistrict(districtDto);

        // Assert
        verify(repository, times(1)).addDistrict(district);
        Assertions.assertEquals(expected, received);
    }

}
