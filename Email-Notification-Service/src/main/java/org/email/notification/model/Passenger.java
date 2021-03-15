package org.email.notification.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String number;

    private String fullName;

    @ManyToOne
    private Voyage voyage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(id, passenger.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
