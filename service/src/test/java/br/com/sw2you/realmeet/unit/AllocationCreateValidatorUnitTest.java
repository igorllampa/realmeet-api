package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.utils.TestDataCreator.*;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.repository.AllocationRepository;
import br.com.sw2you.realmeet.util.DateUtils;
import br.com.sw2you.realmeet.validator.AllocationValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

public class AllocationCreateValidatorUnitTest extends BaseUnitTest {
    private AllocationValidator victim;

    @Mock
    AllocationRepository allocationRepository;

    @BeforeEach
    void setupEach() {
        victim = new AllocationValidator(allocationRepository);
    }

    @Test
    void testValidateWhenAllocationIsValid(){
        victim.validate(newCreateAllocationDTO());
    }


    @Test
    void testValidateWhenSubjectIsMissing(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate(newCreateAllocationDTO().subject(null))
        );

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ALLOCATION_SUBJECT, ALLOCATION_SUBJECT + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenSubjectExceedsLength(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () ->
                    victim.validate(
                            newCreateAllocationDTO().subject(StringUtils.rightPad("X", ALLOCATION_SUBJECT_MAX_LENGTH+1, "X"))
                    )
                );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
                new ValidationError(ALLOCATION_SUBJECT, ALLOCATION_SUBJECT + EXCEEDS_MAX_LENGTH), exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeNameIsMissing(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate(newCreateAllocationDTO().employeeName(null))
        );

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ALLOCATION_EMPLOYEE_NAME, ALLOCATION_EMPLOYEE_NAME + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenEmployeeNameExceedsLength(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () ->
                        victim.validate(
                                newCreateAllocationDTO().employeeName(StringUtils.rightPad("X", ALLOCATION_EMPLOYEE_NAME_MAX_LENGTH+1, "X"))
                        )
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
                new ValidationError(ALLOCATION_EMPLOYEE_NAME, ALLOCATION_EMPLOYEE_NAME + EXCEEDS_MAX_LENGTH), exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeEmailIsMissing(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate(newCreateAllocationDTO().employeeEmail(null))
        );

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ALLOCATION_EMPLOYEE_EMAIL, ALLOCATION_EMPLOYEE_EMAIL + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenEmployeeEmailExceedsLength(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () ->
                        victim.validate(
                                newCreateAllocationDTO().employeeEmail(StringUtils.rightPad("X", ALLOCATION_EMPLOYEE_EMAIL_MAX_LENGTH+1, "X"))
                        )
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
                new ValidationError(ALLOCATION_EMPLOYEE_EMAIL, ALLOCATION_EMPLOYEE_EMAIL + EXCEEDS_MAX_LENGTH), exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenStartAtIsMissing(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate(newCreateAllocationDTO().startAt(null))
        );

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenEndAtIsMissing(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate(newCreateAllocationDTO().endAt(null))
        );

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ALLOCATION_END_AT, ALLOCATION_END_AT + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenDateOrderIsInvalid(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate(newCreateAllocationDTO().startAt(DateUtils.now().plusDays(1)).endAt(DateUtils.now().minusMinutes(30)))
        );

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + INCONSISTENT), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenDateIsInThePast(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate(newCreateAllocationDTO().startAt(DateUtils.now().minusMinutes(30)).endAt(DateUtils.now().plusMinutes(30)))
        );

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + IN_THE_PAST), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenDateIntervalExceedsMaxDuration(){
        var exception = assertThrows(
                InvalidRequestException.class,
                () -> victim.validate(newCreateAllocationDTO().startAt(DateUtils.now().plusDays(1)).endAt(DateUtils.now().plusDays(1).plusSeconds(ALLOCATION_MAX_DURATION_SECONDS+1)))
        );

        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ALLOCATION_END_AT, ALLOCATION_END_AT + EXCEEDS_DURATION), exception.getValidationErrors().getError(0));
    }


    @Test
    void testValidateDateIntervals(){
        assertTrue(isScheduleAllowed(tomorrowAt(4), tomorrowAt(5), tomorrowAt(1), tomorrowAt(2) ));
        assertTrue(isScheduleAllowed(tomorrowAt(4), tomorrowAt(5), tomorrowAt(6), tomorrowAt(7) ));
        assertTrue(isScheduleAllowed(tomorrowAt(4), tomorrowAt(5), tomorrowAt(3), tomorrowAt(4) ));
        assertTrue(isScheduleAllowed(tomorrowAt(4), tomorrowAt(5), tomorrowAt(5), tomorrowAt(6) ));

        assertFalse(isScheduleAllowed(tomorrowAt(4), tomorrowAt(7), tomorrowAt(4), tomorrowAt(7) ));
        assertFalse(isScheduleAllowed(tomorrowAt(4), tomorrowAt(7), tomorrowAt(4), tomorrowAt(5) ));
        assertFalse(isScheduleAllowed(tomorrowAt(4), tomorrowAt(7), tomorrowAt(6), tomorrowAt(7) ));
        assertFalse(isScheduleAllowed(tomorrowAt(4), tomorrowAt(7), tomorrowAt(3), tomorrowAt(5) ));
        assertFalse(isScheduleAllowed(tomorrowAt(4), tomorrowAt(7), tomorrowAt(6), tomorrowAt(8) ));
    }

    private OffsetDateTime tomorrowAt(int hour){
        return OffsetDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(hour, 0), DateUtils.DEFAULT_TIMEZONE);
    }

    private boolean isScheduleAllowed(
        OffsetDateTime scheduledAllocationStart,
        OffsetDateTime scheduledAllocationEndAt,
        OffsetDateTime newAllocationStart,
        OffsetDateTime newAllocationEnd
    ){
        given(allocationRepository.findAllWithFilters(any(), any(), any(), any()))
            .willReturn(
                List.of(
                    newAllocationBuilder(
                        newRoomBuilder()
                            .build()
                    )
                    .startAt(scheduledAllocationStart)
                    .endAt(scheduledAllocationEndAt)
                    .build()
                )
            );

        try {
            victim.validate(newCreateAllocationDTO().startAt(newAllocationStart).endAt(newAllocationEnd));
            return true;
        }catch (InvalidRequestException e){
            return false;
        }
    }
}
