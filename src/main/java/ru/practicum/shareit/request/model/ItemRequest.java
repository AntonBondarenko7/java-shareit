package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Entity
@Table(name = "requests", schema = "public")
@NoArgsConstructor
@Data
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column
    private Instant created = Instant.now();

}
