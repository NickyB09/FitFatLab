package com.fitfatlab.fitfatlab_backend.modules.coaching.repository;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLink;
import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CoachStudentLinkRepository extends JpaRepository<CoachStudentLink, UUID> {

    boolean existsByStudentIdAndStatus(UUID studentId, CoachStudentLinkStatus status);

    boolean existsByCoachIdAndStudentIdAndStatus(UUID coachId, UUID studentId, CoachStudentLinkStatus status);

    List<CoachStudentLink> findByCoachIdOrderByCreatedAtDesc(UUID coachId);

    List<CoachStudentLink> findByStudentIdOrderByCreatedAtDesc(UUID studentId);

    boolean existsByCoachIdAndStudentIdAndStatusAndAllowStudentMealEditsTrue(
            UUID coachId, UUID studentId, CoachStudentLinkStatus status);
}
