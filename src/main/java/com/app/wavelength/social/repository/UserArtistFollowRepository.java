package com.app.wavelength.social.repository;

import com.app.wavelength.social.domain.UserArtistFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserArtistFollowRepository extends JpaRepository<UserArtistFollow, UUID> {

    boolean existsByFollowerIdAndArtistId(UUID followerId, UUID artistId);

    @Modifying
    @Query("DELETE FROM UserArtistFollow f WHERE f.followerId = :followerId " +
           "AND f.artistId = :artistId")
    void deleteByFollowerIdAndArtistId(UUID followerId, UUID artistId);

    List<UserArtistFollow> findByFollowerIdOrderByFollowedAtDesc(UUID followerId);

    long countByArtistId(UUID artistId);
}