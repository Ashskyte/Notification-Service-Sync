package com.notification.repository;

import com.notification.model.UserChannelPreference;
import com.notification.model.enums.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChannelPreferenceRepository extends JpaRepository<UserChannelPreference, Long> {

    List<UserChannelPreference> findByUserId(Long userId);

    Optional<UserChannelPreference> findByUserIdAndChannel(Long userId, NotificationChannel channel);
}
