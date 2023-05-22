package com.example.hello.membership;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.hello.common.MembershipErrorResult;
import com.example.hello.common.MembershipException;
import com.example.hello.membership.adapter.in.response.MembershipResponseDTO;
import com.example.hello.membership.application.port.out.MembershipCommandPort;
import com.example.hello.membership.application.port.out.MembershipQueryPort;
import com.example.hello.membership.application.service.MembershipService;
import com.example.hello.membership.domain.Membership;
import com.example.hello.membership.domain.code.MembershipType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

  @InjectMocks
  private MembershipService membershipService;
  @Mock
  private MembershipCommandPort membershipCommandPort;
  @Mock
  private MembershipQueryPort membershipQueryPort;

  private final String userId = "userId";
  private final MembershipType membershipType = MembershipType.NAVER;
  private final Integer point = 10000;

  @Nested
  class 멤버십등록 {

    @Test
    public void 멤버십등록실패_이미존재함() {
      // given
      doReturn(Membership.builder().build()).when(
          membershipQueryPort).findByUserIdAndMembershipType(userId, membershipType);

      // when
      final MembershipException result = assertThrows(MembershipException.class,
          () -> membershipService.addMembership(userId, membershipType, point));

      // then
      assertThat(result.getErrorResult()).isEqualTo(
          MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    @Test
    public void 정상등록() {
      // given
      doReturn(null).when(membershipQueryPort).findByUserIdAndMembershipType(userId, membershipType);
      doReturn(membership()).when(membershipCommandPort).save(any(Membership.class));

      // when
      final MembershipResponseDTO result = membershipService.addMembership(userId, membershipType, point);

      // then
      assertThat(result.getId()).isNotNull();
      assertThat(result.getMembershipType()).isEqualTo(membershipType);
      assertThat(result.getPoint()).isEqualTo(point);

      // verify
      verify(membershipQueryPort, times(1)).findByUserIdAndMembershipType(userId, membershipType);
      verify(membershipCommandPort, times(1)).save(any(Membership.class));
    }

    private Membership membership() {
      return Membership.builder()
          .id(-1L)
          .userId(userId)
          .point(point)
          .membershipType(MembershipType.NAVER)
          .build();
    }

  }


}
