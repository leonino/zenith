package br.com.solutil.zenith.controller;

import br.com.solutil.zenith.dto.MemberDTO;
import br.com.solutil.zenith.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"deprecation", "null"})
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MemberDTO testMemberDTO;
    private List<MemberDTO> testMemberList;

    @BeforeEach
    void setUp() {
        // Setup test member
        testMemberDTO = new MemberDTO();
        testMemberDTO.setId(1L);
        testMemberDTO.setName("Test Member");
        testMemberDTO.setCim("CIM001");
        testMemberDTO.setGrau("Mestre");
        testMemberDTO.setDataNascimento(LocalDate.of(1980, 1, 1));
        testMemberDTO.setCpf("123.456.789-00");
        testMemberDTO.setRg("12.345.678-9");
        testMemberDTO.setProfissao("Developer");
        testMemberDTO.setEndereco("Test Street, 123");
        testMemberDTO.setCidade("Test City");
        testMemberDTO.setEstado("SP");
        testMemberDTO.setCep("12345-678");
        testMemberDTO.setTelefone("(11) 98765-4321");
        testMemberDTO.setEmail("test@test.com");
        testMemberDTO.setDataIniciacao(LocalDate.of(2020, 1, 1));
        testMemberDTO.setObservacoes("Test member");
        testMemberDTO.setAtivo(true);

        // Setup test member list
        testMemberList = Arrays.asList(testMemberDTO);
    }

    @Test
    void createMember_Success() {
        // Arrange
        when(memberService.create(any(MemberDTO.class))).thenReturn(testMemberDTO);

        // Act
        ResponseEntity<MemberDTO> response = memberController.createMember(testMemberDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testMemberDTO, response.getBody());
        verify(memberService).create(any(MemberDTO.class));
    }

    @Test
    void getMember_Success() {
        // Arrange
        when(memberService.findById(anyLong())).thenReturn(testMemberDTO);

        // Act
        ResponseEntity<MemberDTO> response = memberController.getMember(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testMemberDTO, response.getBody());
        verify(memberService).findById(1L);
    }

    @Test
    void getAllMembers_Success() {
        // Arrange
        when(memberService.findAll()).thenReturn(testMemberList);

        // Act
        ResponseEntity<List<MemberDTO>> response = memberController.getAllMembers();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testMemberList, response.getBody());
        assertEquals(1, response.getBody().size());
        verify(memberService).findAll();
    }

    @Test
    void updateMember_Success() {
        // Arrange
        when(memberService.update(anyLong(), any(MemberDTO.class))).thenReturn(testMemberDTO);

        // Act
        ResponseEntity<MemberDTO> response = memberController.updateMember(1L, testMemberDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testMemberDTO, response.getBody());
        verify(memberService).update(1L, testMemberDTO);
    }

    @Test
    void deleteMember_Success() {
        // Arrange
        doNothing().when(memberService).delete(anyLong());

        // Act
        ResponseEntity<Void> response = memberController.deleteMember(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(memberService).delete(1L);
    }

    @Test
    void getMembersByStatus_Success() {
        // Arrange
        when(memberService.findByAtivo(anyBoolean())).thenReturn(testMemberList);

        // Act
        ResponseEntity<List<MemberDTO>> response = memberController.getMembersByStatus(true);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testMemberList, response.getBody());
        assertEquals(1, response.getBody().size());
        verify(memberService).findByAtivo(true);
    }
}
