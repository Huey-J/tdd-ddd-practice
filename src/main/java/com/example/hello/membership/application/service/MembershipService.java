package com.example.hello.membership.application.service;

import com.example.hello.common.MembershipErrorResult;
import com.example.hello.common.MembershipException;
import com.example.hello.membership.application.port.in.response.MembershipResponse;
import com.example.hello.membership.adapter.out.MembershipRepository;
import com.example.hello.membership.domain.Membership;
import com.example.hello.membership.domain.code.MembershipType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MembershipService {

  private final MembershipRepository membershipRepository;

  @Transactional
  public MembershipResponse addMembership(final String userId, final MembershipType membershipType, final Integer point) {
    final Membership result = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
    if (result != null) {
      throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    final Membership membership = Membership.builder()
        .userId(userId)
        .point(point)
        .membershipType(membershipType)
        .build();

    final Membership savedMembership = membershipRepository.save(membership);

    return MembershipResponse.builder()
        .id(savedMembership.getId())
        .membershipType(savedMembership.getMembershipType())
        .build();
  }

}