package com.example.hello.membership.adapter.out;

import com.example.hello.membership.application.port.out.MembershipQueryPort;
import com.example.hello.membership.domain.Membership;
import com.example.hello.membership.domain.code.MembershipType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MembershipQueryRepository implements MembershipQueryPort {

  private final MembershipJpaRepository membershipJpaRepository;

  @Override
  public Membership findByUserIdAndMembershipType(String userId, MembershipType membershipType) {
    return membershipJpaRepository.findByUserIdAndMembershipType(userId, membershipType);
  }
}
