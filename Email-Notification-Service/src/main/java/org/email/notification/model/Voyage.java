package org.email.notification.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Voyage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime scheduledTime;

    private LocalDateTime actualTime;

    private Boolean isNotified = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "voyage")
    private Set<Passenger> passengers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voyage voyage = (Voyage) o;
        return Objects.equals(id, voyage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
