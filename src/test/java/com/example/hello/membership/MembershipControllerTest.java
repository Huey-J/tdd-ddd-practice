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
import com.example.hello.membership.adapter.in.response.MembershipResponseDetailDTO;
import com.example.hello.membership.application.service.MembershipService;
import com.example.hello.membership.domain.code.MembershipType;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

  @Nested
  class 멤버십등록 {

    // common given
    private final String url = "/api/v1/memberships";
    private final String userIdInHeader = "12345";
    private final MembershipType requestedMembershipType = MembershipType.NAVER;
    private final Integer requestedPoint = 10000;

    @Test
    public void 멤버십등록실패_사용자식별값이헤더에없음() throws Exception {
      // given

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.post(url)
              .content(gson.toJson(membershipRequest(requestedPoint, requestedMembershipType)))
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_포인트가null() throws Exception {
      // given
      final Integer requestedPointNull = null;

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.post(url)
              .header(USER_ID_HEADER, userIdInHeader)
              .content(gson.toJson(membershipRequest(requestedPointNull, requestedMembershipType)))
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_포인트가음수() throws Exception {
      // given
      final Integer requestedPointMinus = null;

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.post(url)
              .header(USER_ID_HEADER, userIdInHeader)
              .content(gson.toJson(membershipRequest(requestedPointMinus, requestedMembershipType)))
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_멤버십종류가Null() throws Exception {
      // given
      final MembershipType requestedMembershipTypeNull = null;

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.post(url)
              .header(USER_ID_HEADER, userIdInHeader)
              .content(gson.toJson(membershipRequest(requestedPoint, requestedMembershipTypeNull)))
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_MemberService에서에러Throw() throws Exception {
      // given
      doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
          .when(membershipService)
          .addMembership(userIdInHeader, requestedMembershipType, requestedPoint);

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.post(url)
              .header(USER_ID_HEADER, userIdInHeader)
              .content(gson.toJson(membershipRequest(requestedPoint, requestedMembershipType)))
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록성공() throws Exception {
      // given
      final MembershipResponseDTO membershipResponse = MembershipResponseDTO.builder()
          .id(-1L)
          .membershipType(requestedMembershipType)
          .point(requestedPoint)
          .build();

      doReturn(membershipResponse).when(membershipService).addMembership(userIdInHeader, requestedMembershipType, requestedPoint);

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.post(url)
              .header(USER_ID_HEADER, userIdInHeader)
              .content(gson.toJson(membershipRequest(requestedPoint, requestedMembershipType)))
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions.andExpect(status().isCreated());

      final MembershipResponseDTO response = gson.fromJson(resultActions.andReturn()
          .getResponse()
          .getContentAsString(StandardCharsets.UTF_8), MembershipResponseDTO.class);

      assertThat(response.getPoint()).isEqualTo(requestedPoint);
      assertThat(response.getMembershipType()).isEqualTo(requestedMembershipType);
      assertThat(response.getId()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalidMembershipAddParameter")
    public void 멤버십등록실패_잘못된파라미터(final Integer point, final MembershipType membershipType) throws Exception {
      // given

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.post(url)
              .header(USER_ID_HEADER, userIdInHeader)
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

    private MembershipCreateRequestDTO membershipRequest(final Integer point, final MembershipType membershipType) {
      return MembershipCreateRequestDTO.builder()
          .point(point)
          .membershipType(membershipType)
          .build();
    }
  }

  @Nested
  class 멤버십목록조회 {

    // common given
    private final String url = "/api/v1/memberships";
    private final String userIdInHeader = "12345";

    @Test
    public void 멤버십목록조회실패_사용자식별값이헤더에없음() throws Exception {
      // given

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.get(url)
      );

      // then
      resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십목록조회성공() throws Exception {
      // given
      doReturn(Arrays.asList(
          MembershipResponseDetailDTO.builder().build(),
          MembershipResponseDetailDTO.builder().build(),
          MembershipResponseDetailDTO.builder().build()
      )).when(membershipService).getMembershipList(userIdInHeader);

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.get(url)
              .header(USER_ID_HEADER, userIdInHeader)
      );

      // then
      resultActions.andExpect(status().isOk());
    }
  }

  @Nested
  class 멤버십상세조회 {

    // common given
    private final String url = "/api/v1/memberships";
    private final String userIdInHeader = "12345";

    @Test
    public void 존재하지않음_실패() throws Exception {
      // given
      doThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND))
          .when(membershipService)
          .getMembership(-1L, userIdInHeader);

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.get(url + "/-1")
              .header(USER_ID_HEADER, userIdInHeader)
      );

      // then
      resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void 성공() throws Exception {
      // given
      doReturn(MembershipResponseDetailDTO.builder().build())
          .when(membershipService)
          .getMembership(-1L, userIdInHeader);

      // when
      final ResultActions resultActions = mockMvc.perform(
          MockMvcRequestBuilders.get(url + "/-1")
              .header(USER_ID_HEADER, userIdInHeader)
              .param("membershipType", MembershipType.NAVER.name())
      );

      // then
      resultActions.andExpect(status().isOk());
    }
  }

}
