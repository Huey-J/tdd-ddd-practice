package com.example.hello.membership.application.service;

import com.example.hello.common.MembershipErrorResult;
import com.example.hello.common.MembershipException;
import com.example.hello.membership.application.port.in.MembershipUseCase;
import com.example.hello.membership.adapter.in.response.MembershipResponseDTO;
import com.example.hello.membership.application.port.out.MembershipCommandPort;
import com.example.hello.membership.application.port.out.MembershipQueryPort;
import com.example.hello.membership.domain.Membership;
import com.example.hello.membership.domain.code.MembershipType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MembershipService implements MembershipUseCase {

  private final MembershipCommandPort membershipCommandPort;
  private final MembershipQueryPort membershipQueryPort;

  @Override
  @Transactional
  public MembershipResponseDTO addMembership(final String userId, final MembershipType membershipType, final Integer point) {
    final Membership result = membershipQueryPort.findByUserIdAndMembershipType(userId, membershipType);
    if (result != null) {
      throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    final Membership membership = Membership.builder()
        .userId(userId)
        .point(point)
        .membershipType(membershipType)
        .build();

    final Membership savedMembership = membershipCommandPort.save(membership);

    return MembershipResponseDTO.builder()
        .id(savedMembership.getId())
        .membershipType(savedMembership.getMembershipType())
        .point(savedMembership.getPoint())
        .build();
  }

}