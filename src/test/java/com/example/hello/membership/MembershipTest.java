package com.example.hello.membership;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.hello.membership.domain.Membership;
import com.example.hello.membership.domain.code.MembershipType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MembershipTest {

  @Test
  void 멤버십적립_10000원의적립금은100원() {
    // given
    final int price = 10000;
    Membership naverMembership = Membership.builder()
        .id(-1L)
        .userId("userId")
        .point(0)
        .membershipType(MembershipType.NAVER)
        .build();

    // then
    naverMembership.addPointByPrice(price);

    // when
    assertThat(naverMembership.getPoint()).isEqualTo(100);
  }

  @Test
  void 멤버십적립_30000원의적립금은300원() {
    // given
    final int price = 30000;
    Membership naverMembership = Membership.builder()
        .id(-1L)
        .userId("userId")
        .point(0)
        .membershipType(MembershipType.NAVER)
        .build();

    // then
    naverMembership.addPointByPrice(price);

    // when
    assertThat(naverMembership.getPoint()).isEqualTo(300);
  }

}
