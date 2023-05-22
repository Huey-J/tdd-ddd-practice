package com.example.hello.membership;

import static com.example.hello.common.MembershipConstants.USER_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hello.common.GlobalExceptionHandler;
import com.example.hello.common.MembershipErrorResult;
import com.example.hello.common.MembershipException;
import com.example.hello.membership.adapter.in.MembershipController;
import com.example.hello.membership.adapter.in.request.MembershipCreateRequestDTO;
import com.example.hello.membership.adapter.in.response.MembershipResponseDTO;
import com.example.hello.membership.application.service.MembershipService;
import com.example.hello.membership.domain.code.MembershipType;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTest {

  @InjectMocks
  private MembershipController membershipController;
  @Mock
  private MembershipService membershipService;

  private MockMvc mockMvc;
  private Gson gson;

  @BeforeEach
  public void init() {
    gson = new Gson();

    // 클래스에 @WebMvcTest 붙혀도 됨 (init 방식이 더 빠름)
    mockMvc = MockMvcBuilders.standaloneSetup(membershipController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  public void mockMvc가Null이아님() throws Exception {
    assertThat(membershipController).isNotNull();
    assertThat(mockMvc).isNotNull();
  }

  @Test
  public void 멤버십등록실패_사용자식별값이헤더에없음() throws Exception {
    // given
    final String url = "/api/v1/memberships";

    // when
    final ResultActions resultActions = mockMvc.perform(
        MockMvcRequestBuilders.post(url)
            .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  public void 멤버십등록실패_포인트가null() throws Exception {
    // given
    final String url = "/api/v1/memberships";

    // when
    final ResultActions resultActions = mockMvc.perform(
        MockMvcRequestBuilders.post(url)
            .header(USER_ID_HEADER, "12345")
            .content(gson.toJson(membershipRequest(null, MembershipType.NAVER)))
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  public void 멤버십등록실패_포인트가음수() throws Exception {
    // given
    final String url = "/api/v1/memberships";

    // when
    final ResultActions resultActions = mockMvc.perform(
        MockMvcRequestBuilders.post(url)
            .header(USER_ID_HEADER, "12345")
            .content(gson.toJson(membershipRequest(-1, MembershipType.NAVER)))
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  public void 멤버십등록실패_멤버십종류가Null() throws Exception {
    // given
    final String url = "/api/v1/memberships";

    // when
    final ResultActions resultActions = mockMvc.perform(
        MockMvcRequestBuilders.post(url)
            .header(USER_ID_HEADER, "12345")
            .content(gson.toJson(membershipRequest(10000, null)))
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  public void 멤버십등록실패_MemberService에서에러Throw() throws Exception {
    // given
    final String url = "/api/v1/memberships";
    doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
        .when(membershipService)
        .addMembership("12345", MembershipType.NAVER, 10000);

    // when
    final ResultActions resultActions = mockMvc.perform(
        MockMvcRequestBuilders.post(url)
            .header(USER_ID_HEADER, "12345")
            .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  public void 멤버십등록성공() throws Exception {
    // given
    final String url = "/api/v1/memberships";
    final MembershipResponseDTO membershipResponse = MembershipResponseDTO.builder()
        .id(-1L)
        .membershipType(MembershipType.NAVER).build();

    doReturn(membershipResponse).when(membershipService).addMembership("12345", MembershipType.NAVER, 10000);

    // when
    final ResultActions resultActions = mockMvc.perform(
        MockMvcRequestBuilders.post(url)
            .header(USER_ID_HEADER, "12345")
            .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isCreated());

    final MembershipResponseDTO response = gson.fromJson(resultActions.andReturn()
        .getResponse()
        .getContentAsString(StandardCharsets.UTF_8), MembershipResponseDTO.class);

    assertThat(response.getMembershipType()).isEqualTo(MembershipType.NAVER);
    assertThat(response.getId()).isNotNull();
  }

  private MembershipCreateRequestDTO membershipRequest(final Integer point, final MembershipType membershipType) {
    return MembershipCreateRequestDTO.builder()
        .point(point)
        .membershipType(membershipType)
        .build();
  }

  @ParameterizedTest
  @MethodSource("invalidMembershipAddParameter")
  public void 멤버십등록실패_잘못된파라미터(final Integer point, final MembershipType membershipType) throws Exception {
    // given
    final String url = "/api/v1/memberships";

    // when
    final ResultActions resultActions = mockMvc.perform(
        MockMvcRequestBuilders.post(url)
            .header(USER_ID_HEADER, "12345")
            .content(gson.toJson(membershipRequest(point, membershipType)))
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  private static Stream<Arguments> invalidMembershipAddParameter() {
    return Stream.of(
        Arguments.of(null, MembershipType.NAVER),
        Arguments.of(-1, MembershipType.NAVER),
        Arguments.of(10000, null)
    );
  }


}
