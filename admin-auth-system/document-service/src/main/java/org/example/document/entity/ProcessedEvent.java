package org.example.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "processed_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProcessedEvent {

    @Id
    @NonNull
    @EqualsAndHashCode.Include
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

}
