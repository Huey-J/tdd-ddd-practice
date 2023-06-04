package com.example.hello.membership.adapter.out;

import com.example.hello.membership.application.port.out.MembershipCommandPort;
import com.example.hello.membership.domain.Membership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MembershipCommandRepository implements MembershipCommandPort {

  private final MembershipJpaRepository membershipJpaRepository;

  @Override
  public Membership save(Membership membership) {
    return membershipJpaRepository.save(membership);
  }

  @Override
  public void deleteById(Long membershipId) {
    membershipJpaRepository.deleteById(membershipId);
  }
}
