package com.example.hello.membership;


import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

  Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);

}
