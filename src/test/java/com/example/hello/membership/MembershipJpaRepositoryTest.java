package com.example.hello.membership;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.hello.membership.domain.Membership;
import com.example.hello.membership.adapter.out.MembershipJpaRepository;
import com.example.hello.membership.domain.code.MembershipType;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MembershipJpaRepositoryTest {

  @Autowired
  private MembershipJpaRepository membershipRepository;

  @Test
  public void MembershipRepository가Null이아님() {
    assertThat(membershipRepository).isNotNull();
  }

  @Nested
  class 멤버십등록 {

    @Test
    public void 정상등록() {
      // given
      final Membership membership = Membership.builder()
          .userId("userId")
          .membershipType(MembershipType.NAVER)
          .point(10000)
          .build();

      // when
      final Membership result = membershipRepository.save(membership);

      // then
      assertThat(result.getId()).isNotNull();
      assertThat(result.getUserId()).isEqualTo("userId");
      assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
      assertThat(result.getPoint()).isEqualTo(10000);
    }
  }

  @Nested
  class 멤버십조회 {

    @Test
    public void 정상조회() {
      // given
      final Membership membership = Membership.builder()
          .userId("userId")
          .membershipType(MembershipType.NAVER)
          .point(10000)
          .build();

      // when
      membershipRepository.save(membership);
      final Membership findResult = membershipRepository.findByUserIdAndMembershipType("userId",
          MembershipType.NAVER);

      // then
      assertThat(findResult).isNotNull();
      assertThat(findResult.getId()).isNotNull();
      assertThat(findResult.getUserId()).isEqualTo("userId");
      assertThat(findResult.getMembershipType()).isEqualTo(MembershipType.NAVER);
      assertThat(findResult.getPoint()).isEqualTo(10000);
    }

    @Test
    public void 찾지못함() {
      // given
      final Membership membership = Membership.builder()
          .userId("userId")
          .membershipType(MembershipType.NAVER)
          .point(10000)
          .build();

      // when
      membershipRepository.save(membership);
      final Membership findResult = membershipRepository.findByUserIdAndMembershipType("userId",
          MembershipType.KAKAO);

      // then
      assertThat(findResult).isNull();
    }
  }

  @Nested
  class 멤버십목록조회 {

    // common given
    private final String requestUserId = "userId";

    @Test
    public void 정상조회_리스트2개() {
      // given
      final Membership naverMembership = Membership.builder()
          .userId(requestUserId)
          .membershipType(MembershipType.NAVER)
          .point(10000)
          .build();

      final Membership kakaoMembership = Membership.builder()
          .userId(requestUserId)
          .membershipType(MembershipType.KAKAO)
          .point(10000)
          .build();

      membershipRepository.save(naverMembership);
      membershipRepository.save(kakaoMembership);

      // when
      List<Membership> result = membershipRepository.findAllByUserId(requestUserId);

      // then
      assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void 아무것도없음() {
      // given

      // when
      List<Membership> result = membershipRepository.findAllByUserId(requestUserId);

      // then
      assertThat(result.size()).isEqualTo(0);
    }
  }

  @Nested
  class 멤버십삭제 {

    @Test
    void 멤버십추가후삭제() {
      // given
      final Membership naverMembership = Membership.builder()
          .userId("userId")
          .membershipType(MembershipType.NAVER)
          .point(1000)
          .build();

      final Membership savedMembership = membershipRepository.save(naverMembership);

      // when
      membershipRepository.deleteById(savedMembership.getId());

      // then
    }
  }

}
