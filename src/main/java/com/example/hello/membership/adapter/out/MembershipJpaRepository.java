package com.example.hello.membership.adapter.out;

import com.example.hello.membership.domain.code.MembershipType;
import com.example.hello.membership.domain.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MembershipJpaRepository extends JpaRepository<Membership, Long> {

  Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);

}
