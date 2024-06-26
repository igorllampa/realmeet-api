package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.utils.MapperUtils.roomMapper;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newRoomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.exception.RoomNotFoundException;
import br.com.sw2you.realmeet.repository.RoomRepository;
import br.com.sw2you.realmeet.service.RoomService;
import br.com.sw2you.realmeet.validator.RoomValidator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RoomServiceUnitTest extends BaseUnitTest {

    private RoomService victim;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomValidator roomValidator;

    @BeforeEach
    void setupEach(){
        victim = new RoomService(roomRepository, roomValidator, roomMapper());
    }

    @Test
    void testGetRoomSuccess(){

        var room = newRoomBuilder().id(DEFAULT_ROOM_ID).build();
        when(roomRepository.findByIdAndActive(DEFAULT_ROOM_ID, true)).thenReturn(Optional.of(room));

        var dto = victim.getRoom(DEFAULT_ROOM_ID);

        assertEquals(room.getId(), dto.getId());
        assertEquals(room.getName(), dto.getName());
        assertEquals(room.getSeats(), dto.getSeats());
    }

    @Test
    void testGetRoomNotFound(){
        when(roomRepository.findByIdAndActive(DEFAULT_ROOM_ID, true)).thenReturn(Optional.empty());
        assertThrows(RoomNotFoundException.class, () ->victim.getRoom(DEFAULT_ROOM_ID));
    }

    @Test
    void testCreateRoomSuccess(){
        var createRoomDto = newCreateRoomDTO();
        var roomDto = victim.createRoom(createRoomDto);

        assertEquals(createRoomDto.getName(), roomDto.getName());
        assertEquals(createRoomDto.getSeats(), roomDto.getSeats());

        verify(roomRepository).save(any());//Verifica interacoes com o Mock: verif. se o save foi chamado com qq parametro

    }
}
