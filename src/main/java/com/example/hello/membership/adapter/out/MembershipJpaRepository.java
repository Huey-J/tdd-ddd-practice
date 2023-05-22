package com.example.hello.membership.adapter.out;

import com.example.hello.membership.domain.code.MembershipType;
import com.example.hello.membership.domain.Membership;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipJpaRepository extends JpaRepository<Membership, Long> {

  Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);

  List<Membership> findAllByUserId(final String userId);

}
