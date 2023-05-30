package com.example.hello.membership.application.service;

import com.example.hello.common.MembershipErrorResult;
import com.example.hello.common.MembershipException;
import com.example.hello.membership.adapter.in.response.MembershipResponseDetailDTO;
import com.example.hello.membership.application.port.in.MembershipUseCase;
import com.example.hello.membership.adapter.in.response.MembershipResponseDTO;
import com.example.hello.membership.application.port.out.MembershipCommandPort;
import com.example.hello.membership.application.port.out.MembershipQueryPort;
import com.example.hello.membership.domain.Membership;
import com.example.hello.membership.domain.code.MembershipType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

  @Override
  public List<MembershipResponseDetailDTO> getMembershipList(final String userId) {
    final List<Membership> membershipList = membershipQueryPort.findAllByUserId(userId);

    // TODO 추후 ModelMapper 적용
    return membershipList.stream()
        .map(v -> MembershipResponseDetailDTO.builder()
            .id(v.getId())
            .membershipType(v.getMembershipType())
            .point(v.getPoint())
            .createdAt(v.getCreatedAt())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  public MembershipResponseDetailDTO getMembership(final Long membershipId, final String userId) {
    final Membership membership = membershipQueryPort.findById(membershipId)
        .orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

    if (!membership.getUserId().equals(userId)) {
      throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    return MembershipResponseDetailDTO.builder()
        .id(membership.getId())
        .membershipType(membership.getMembershipType())
        .point(membership.getPoint())
        .createdAt(membership.getCreatedAt())
        .build();
  }

}